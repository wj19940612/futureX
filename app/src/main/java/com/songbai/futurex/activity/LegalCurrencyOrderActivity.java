package com.songbai.futurex.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.songbai.futurex.R;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.fragment.legalcurrency.LegalCurrencyOrderListFragment;
import com.songbai.futurex.model.status.OtcOrderStatus;
import com.songbai.futurex.view.RadioHeader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/6/28
 */
public class LegalCurrencyOrderActivity extends BaseActivity {
    @BindView(R.id.radioHeader)
    RadioHeader mRadioHeader;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private Unbinder mBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_currency);
        mBind = ButterKnife.bind(this);
        ArrayList<BaseFragment> fragments = new ArrayList<>();
        fragments.add(LegalCurrencyOrderListFragment.newInstance(""));
        fragments.add(LegalCurrencyOrderListFragment.newInstance(String.valueOf(OtcOrderStatus.ORDER_UNPAIED)));
        fragments.add(LegalCurrencyOrderListFragment.newInstance(String.valueOf(OtcOrderStatus.ORDER_PAIED)));
        fragments.add(LegalCurrencyOrderListFragment.newInstance(String.valueOf(OtcOrderStatus.ORDER_CANCLED)));
        fragments.add(LegalCurrencyOrderListFragment.newInstance(String.valueOf(OtcOrderStatus.ORDER_COMPLATED)));
        mViewPager.setAdapter(new LegalCurrencyOrderPager(getSupportFragmentManager(), fragments));
        mViewPager.setOffscreenPageLimit(fragments.size() - 1);
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
        mRadioHeader.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                mViewPager.setCurrentItem(position);
            }
        });
    }

    private class LegalCurrencyOrderPager extends FragmentPagerAdapter {
        private ArrayList<BaseFragment> mList;

        LegalCurrencyOrderPager(FragmentManager fm, ArrayList<BaseFragment> fragments) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }
}
