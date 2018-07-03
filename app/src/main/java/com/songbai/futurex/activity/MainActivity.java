package com.songbai.futurex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.fragment.HomeFragment;
import com.songbai.futurex.fragment.LegalCurrencyFragment;
import com.songbai.futurex.fragment.MarketFragment;
import com.songbai.futurex.fragment.MineFragment;
import com.songbai.futurex.fragment.TradeFragment;
import com.songbai.futurex.fragment.UpdateVersionDialogFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.AppVersion;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.utils.OnNavigationListener;
import com.songbai.futurex.view.BottomTabs;
import com.songbai.futurex.view.ScrollableViewPager;
import com.songbai.futurex.websocket.model.TradeDir;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements OnNavigationListener, TradeFragment.OnOptionalClickListener {

    public static final int PAGE_HOME = 0;
    public static final int PAGE_MARKET = 1;
    public static final int PAGE_LEGAL_CURRENCY = 2;
    public static final int PAGE_TRADE = 3;
    public static final int PAGE_MINE = 4;

    @BindView(R.id.viewPager)
    ScrollableViewPager mViewPager;
    @BindView(R.id.bottomTabs)
    BottomTabs mBottomTabs;
    private MainFragmentsAdapter mMainFragmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        translucentStatusBar();
        checkVersion();
        mMainFragmentsAdapter = new MainFragmentsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMainFragmentsAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setScrollable(false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mBottomTabs.selectTab(position);
                setStatusBarDarkModeForM(position != 0 && position != 4);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setCurrentItem(0);

        mBottomTabs.setOnTabClickListener(new BottomTabs.OnTabClickListener() {
            @Override
            public void onTabClick(int position) {
                mBottomTabs.selectTab(position);
                mViewPager.setCurrentItem(position, false);
//                umengClickStatistics(position);
            }
        });
    }

    @Override
    public void onNavigation(int mainPageIndex, Intent exUserDefineData) {
        Fragment fragment = mMainFragmentsAdapter.getFragment(mainPageIndex);
        if (fragment instanceof TradeFragment) {
            int tradeDir = exUserDefineData.getIntExtra(ExtraKeys.TRADE_DIRECTION, TradeDir.DIR_BUY_IN);
            CurrencyPair pair = exUserDefineData.getParcelableExtra(ExtraKeys.CURRENCY_PAIR);
            ((TradeFragment) fragment).setTradeDir(tradeDir);
            ((TradeFragment) fragment).setCurrencyPair(pair);
            mBottomTabs.performTabClick(mainPageIndex);
        }
    }

    @Override
    public void onOptionalClick() {
        Fragment fragment = mMainFragmentsAdapter.getFragment(PAGE_MARKET);
        if (fragment instanceof MarketFragment) {
            ((MarketFragment) fragment).updateOptionalList();
        }
    }

    private void checkVersion() {
        Apic.queryForceVersion()
                .callback(new Callback<Resp<AppVersion>>() {
                    @Override
                    protected void onRespSuccess(Resp<AppVersion> resp) {

                        if (resp.getData() != null && (resp.getData().isForceUpdate() || resp.getData().isNeedUpdate())) {
                            UpdateVersionDialogFragment.newInstance(resp.getData(), resp.getData().isForceUpdate())
                                    .show(getSupportFragmentManager());
                        }
                    }
                })
                .fire();
    }

    private static class MainFragmentsAdapter extends FragmentPagerAdapter {

        FragmentManager mFragmentManager;

        public MainFragmentsAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case PAGE_HOME:
                    return new HomeFragment();
                case PAGE_MARKET:
                    return new MarketFragment();
                case PAGE_LEGAL_CURRENCY:
                    return new LegalCurrencyFragment();
                case PAGE_TRADE:
                    return new TradeFragment();
                case PAGE_MINE:
                    return new MineFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
