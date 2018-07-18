package com.songbai.futurex.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.LegalCurrencyOrderActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.fragment.legalcurrency.MyPosterFragment;
import com.songbai.futurex.fragment.legalcurrency.PublishPosterFragment;
import com.songbai.futurex.fragment.legalcurrency.WantBuyOrSellFragment;
import com.songbai.futurex.fragment.mine.SelectPayTypeFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CountryCurrency;
import com.songbai.futurex.model.LegalCoin;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.status.OTCOrderStatus;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.Network;
import com.songbai.futurex.view.BadgeTextView;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.MsgHintController;
import com.songbai.futurex.view.popup.WaresPairFilter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/5/30
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class LegalCurrencyFragment extends BaseFragment {
    private static int REQUEST_PUBLISH_POSTER = 14321;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.radioHeader)
    RadioHeader mRadioHeader;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.order)
    BadgeTextView mOrder;
    private Unbinder mUnbinder;
    private ArrayList<LegalCoin> mLegalCoins;
    private ArrayList<CountryCurrency> mCountryCurrencies;
    private String mSelectedCurrencySymbol;
    private String mSelectedLegalSymbol;
    private ArrayList<BaseFragment> mFragments;
    private boolean isPrepared;
    private boolean mInited;
    private Network.NetworkChangeReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (TextUtils.isEmpty(mSelectedCurrencySymbol) || TextUtils.isEmpty(mSelectedLegalSymbol)) {
                getOtcPair();
            }
        }
    };
    private int mSelectedIndex = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_legal_currency, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        isPrepared = true;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BaseActivity) getActivity()).addStatusBarHeightPaddingTop(mTitleBar);
        Network.registerNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
        getOtcPair();
        initView();
    }

    private void getOtcPair() {
        getLegalCoin();
        getCountryCurrency();
    }

    private void initView() {
        if (TextUtils.isEmpty(mSelectedCurrencySymbol) || TextUtils.isEmpty(mSelectedLegalSymbol) || mInited) {
            return;
        }
        mInited = true;
        mTitle.setText(getString(R.string.x_faction_str_x,
                mSelectedLegalSymbol.toUpperCase(),
                mSelectedCurrencySymbol.toUpperCase()));
        mFragments = new ArrayList<>();
        mFragments.add(WantBuyOrSellFragment.newInstance(OTCOrderStatus.ORDER_DIRECT_SELL, mSelectedLegalSymbol, mSelectedCurrencySymbol));
        mFragments.add(WantBuyOrSellFragment.newInstance(OTCOrderStatus.ORDER_DIRECT_BUY, mSelectedLegalSymbol, mSelectedCurrencySymbol));
        mFragments.add(MyPosterFragment.newInstance(mSelectedLegalSymbol, mSelectedCurrencySymbol));
        LegalCurrencyPager adapter = new LegalCurrencyPager(getChildFragmentManager(), mFragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);
        mRadioHeader.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                mViewPager.setCurrentItem(position, false);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mRadioHeader.selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) {
            if (TextUtils.isEmpty(mSelectedCurrencySymbol) || TextUtils.isEmpty(mSelectedLegalSymbol)) {
                getOtcPair();
            }
            if (mSelectedIndex != -1) {
                mViewPager.setCurrentItem(mSelectedIndex);
                mSelectedIndex = -1;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(mSelectedCurrencySymbol) || TextUtils.isEmpty(mSelectedLegalSymbol)) {
            getOtcPair();
        }
    }

    private void getLegalCoin() {
        Apic.getLegalCoin().tag(TAG)
                .callback(new Callback<Resp<ArrayList<LegalCoin>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<LegalCoin>> resp) {
                        mLegalCoins = resp.getData();
                        mSelectedLegalSymbol = mLegalCoins.get(0).getSymbol().toLowerCase();
                        initView();
                    }
                }).fireFreely();
    }

    private void getCountryCurrency() {
        Apic.getCountryCurrency().tag(TAG)
                .callback(new Callback<Resp<ArrayList<CountryCurrency>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<CountryCurrency>> resp) {
                        mCountryCurrencies = resp.getData();
                        mSelectedCurrencySymbol = mCountryCurrencies.get(0).getEnglishName().toLowerCase();
                        initView();
                    }
                }).fireFreely();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PUBLISH_POSTER) {
            if (data != null) {
                boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, false);
                if (shouldRefresh) {
                    MyPosterFragment myPosterFragment = (MyPosterFragment) mFragments.get(2);
                    myPosterFragment.refresh(true);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mInited = false;
        Network.unregisterNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
    }

    @OnClick({R.id.title, R.id.order, R.id.publishPoster})
    public void onViewClicked(View view) {
        LocalUser user = LocalUser.getUser();
        switch (view.getId()) {
            case R.id.title:
                if (mLegalCoins != null && mCountryCurrencies != null) {
                    showWaresPairFilter();
                } else {
                    getOtcPair();
                }
                break;
            case R.id.order:
                if (user.isLogin()) {
                    Launcher.with(this, LegalCurrencyOrderActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.publishPoster:
                if (user.isLogin()) {
                    if (user.getUserInfo().getPayment() > 0) {
                        UniqueActivity.launcher(this, PublishPosterFragment.class)
                                .putExtra(ExtraKeys.SELECTED_LEGAL_COIN_SYMBOL, mSelectedLegalSymbol)
                                .putExtra(ExtraKeys.SELECTED_CURRENCY_SYMBOL, mSelectedCurrencySymbol)
                                .execute(this, REQUEST_PUBLISH_POSTER);
                    } else {
                        MsgHintController msgHintController = new MsgHintController(getContext(), new MsgHintController.OnClickListener() {
                            @Override
                            public void onConfirmClick() {
                                UniqueActivity.launcher(LegalCurrencyFragment.this, SelectPayTypeFragment.class)
                                        .execute();
                            }
                        });
                        SmartDialog smartDialog = SmartDialog.solo(getActivity());
                        smartDialog.setCustomViewController(msgHintController)
                                .show();
                        msgHintController.setMsg(R.string.publish_poster_have_not_bind_pay);
                        msgHintController.setConfirmText(R.string.go_to_bind);
                        msgHintController.setImageRes(R.drawable.ic_popup_attention);
                    }
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            default:
        }
    }

    private void showWaresPairFilter() {
        WaresPairFilter waresPairFilter = new WaresPairFilter(getContext(), mLegalCoins, mCountryCurrencies);
        waresPairFilter.setSelectedSymbol(mSelectedLegalSymbol, mSelectedCurrencySymbol);
        waresPairFilter.setOnSelectCallBack(new WaresPairFilter.OnSelectCallBack() {
            @Override
            public void onSelected(String tempLegalSymbol, String tempCurrencySymbol) {
                mSelectedCurrencySymbol = tempCurrencySymbol;
                mSelectedLegalSymbol = tempLegalSymbol;
                mTitle.setText(getString(R.string.x_faction_str_x,
                        mSelectedLegalSymbol.toUpperCase(),
                        mSelectedCurrencySymbol.toUpperCase()));
                for (BaseFragment fragment : mFragments) {
                    if (fragment instanceof WantBuyOrSellFragment) {
                        ((WantBuyOrSellFragment) fragment).
                                setRequestParamAndRefresh(mSelectedLegalSymbol, mSelectedCurrencySymbol);
                    }
                    if (fragment instanceof MyPosterFragment) {
                        ((MyPosterFragment) fragment).
                                setRequestParamAndRefresh(mSelectedLegalSymbol, mSelectedCurrencySymbol);
                    }
                }
            }
        });
        waresPairFilter.showOrDismiss(mRadioHeader);
    }

    public void setSelectedIndex(int selectedIndex) {
        mSelectedIndex = selectedIndex;
    }

    private class LegalCurrencyPager extends FragmentPagerAdapter {
        private ArrayList<BaseFragment> mList;

        LegalCurrencyPager(FragmentManager fm, ArrayList<BaseFragment> fragments) {
            super(fm);
            mList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }
}
