package com.songbai.futurex.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.legalcurrency.MyAdFragment;
import com.songbai.futurex.fragment.legalcurrency.SendAdFragment;
import com.songbai.futurex.fragment.legalcurrency.WantBuyOrSellFragment;
import com.songbai.futurex.view.BadgeTextView;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.TitleBar;

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
    private LegalCurrencyPager mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_legal_currency, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BaseActivity) getActivity()).addStatusBarHeightPaddingTop(mTitleBar);
        ArrayList<BaseFragment> fragments = new ArrayList<>();
        fragments.add(WantBuyOrSellFragment.newInstance(1, "btc", "cny"));
        fragments.add(WantBuyOrSellFragment.newInstance(2, "eos", "cny"));
        fragments.add(MyAdFragment.newInstance());
        mAdapter = new LegalCurrencyPager(getChildFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        for (BaseFragment fragment : fragments) {
            Log.e("wtf", "onViewCreated: " + fragment.toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick({R.id.title, R.id.order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title:
                break;
            case R.id.order:
                UniqueActivity.launcher(this, SendAdFragment.class).execute();
                break;
            default:
        }
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
