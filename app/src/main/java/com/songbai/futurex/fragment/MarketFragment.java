package com.songbai.futurex.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.MainActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.OnNavigationListener;
import com.songbai.futurex.utils.UmengCountEventId;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.PushDestUtils;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.market.MarketSubscriber;
import com.songbai.futurex.websocket.model.MarketData;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/5/30
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class MarketFragment extends BaseFragment {

    private static final int REQ_CODE_MARKET_DETAIL = 95;

    Unbinder unbinder;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.radioHeader)
    RadioHeader mRadioHeader;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private MarketListAdapter mMarketListAdapter;

    private MarketSubscriber mMarketSubscriber;

    private OnNavigationListener mOnNavigationListener;

    private View mEditToggle;
    private ImageView mEditIcon;
    private TextView mCompleteEdit;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationListener) {
            mOnNavigationListener = (OnNavigationListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void updateOptionalList() {
        Fragment fragment = mMarketListAdapter.getFragment(3);
        if (fragment instanceof OptionalListFragment) {
            ((OptionalListFragment) fragment).requestOptionalList();
        }
    }

    private static class MarketListAdapter extends FragmentPagerAdapter {

        FragmentManager mFragmentManager;

        public MarketListAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MarketListFragment.newInstance("USDT");
                case 1:
                    return MarketListFragment.newInstance("BTC");
                case 2:
                    return MarketListFragment.newInstance("ETH");
                case 3:
                    return new OptionalListFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseActivity) getActivity()).addStatusBarHeightPaddingTop(mTitleBar);

        initTitleBar();

        mRadioHeader.setOnTabClickListener(new RadioHeader.OnTabClickListener() {
            @Override
            public void onTabClick(int position, String content) {
                mViewPager.setCurrentItem(position, true);
                switch (position) {
                    case 0:
                        umengEventCount(UmengCountEventId.MARKET0002);
                        break;
                    case 1:
                        umengEventCount(UmengCountEventId.MARKET0003);
                        break;
                    case 2:
                        umengEventCount(UmengCountEventId.MARKET0004);
                        break;
                    case 3:
                        umengEventCount(UmengCountEventId.MARKET0005);
                        break;
                }
            }
        });

        mMarketListAdapter = new MarketListAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mMarketListAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mRadioHeader.selectTab(position);
                if(position ==3){
                    mEditToggle.setVisibility(View.VISIBLE);
                }else {
                    mEditToggle.setVisibility(View.GONE);
                }
            }
        });

        mMarketSubscriber = new MarketSubscriber(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code, String dest) {
                if (PushDestUtils.isAllMarket(dest)) {
                    new DataParser<Response<Map<String, MarketData>>>(data) {
                        @Override
                        public void onSuccess(Response<Map<String, MarketData>> mapResponse) {
                            setMarketDataList(mapResponse.getContent());
                        }
                    }.parse();
                }
            }
        });
    }

    private void setMarketDataList(Map<String, MarketData> content) {
        for (int i = 0; i < mMarketListAdapter.getCount(); i++) {
            Fragment fragment = mMarketListAdapter.getFragment(i);
            if (fragment instanceof MarketListFragment) {
                ((MarketListFragment) fragment).setMarketDataList(content);
                continue;
            }
            if (fragment instanceof OptionalListFragment) {
                ((OptionalListFragment) fragment).setMarketDataList(content);
                continue;
            }
        }
    }

    private void initTitleBar() {
        View customView = mTitleBar.getCustomView();
        mEditToggle = customView.findViewById(R.id.editToggle);
        mEditToggle.setVisibility(View.GONE);
        mEditIcon = mEditToggle.findViewById(R.id.editIcon);
        mCompleteEdit = mEditToggle.findViewById(R.id.completeEdit);
        mEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                    return;
                }
                mEditIcon.setVisibility(View.GONE);
                mCompleteEdit.setVisibility(View.VISIBLE);
                setEditMode(true);
            }
        });
        mCompleteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditIcon.setVisibility(View.VISIBLE);
                mCompleteEdit.setVisibility(View.GONE);
                setEditMode(false);
                requestUpdateOptionalSort();
            }
        });
        customView.findViewById(R.id.search)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        umengEventCount(UmengCountEventId.MARKET0001);
                        openSearchPage();
                    }
                });
    }

    private void setEditMode(boolean b) {
        Fragment fragment = mMarketListAdapter.getFragment(3);
        if (fragment instanceof OptionalListFragment) {
            ((OptionalListFragment) fragment).setEditMode(b);
        }
    }

    private void requestUpdateOptionalSort() {
        Fragment fragment = mMarketListAdapter.getFragment(3);
        if (fragment instanceof OptionalListFragment) {
            ((OptionalListFragment) fragment).requestUpdateOptionalSort();
        }
    }

    public void openSearchPage() {
        UniqueActivity.launcher(getActivity(), SearchCurrencyFragment.class).execute();
    }

    public void openMarketDetailPage(CurrencyPair currencyPair) {
        UniqueActivity.launcher(this, MarketDetailFragment.class)
                .putExtra(ExtraKeys.CURRENCY_PAIR, currencyPair)
                .execute(this, REQ_CODE_MARKET_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_MARKET_DETAIL && resultCode == Activity.RESULT_FIRST_USER) { // 行情详情选择交易
            if (mOnNavigationListener != null) {
                mOnNavigationListener.onNavigation(MainActivity.PAGE_TRADE, data);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMarketSubscriber.resume();
        mMarketSubscriber.subscribeAll();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMarketSubscriber.pause();
        mMarketSubscriber.unSubscribeAll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
