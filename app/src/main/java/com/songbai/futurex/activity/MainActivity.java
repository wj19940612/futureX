package com.songbai.futurex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.Preference;
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
import com.songbai.futurex.newotc.SimpleOTCFragment;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.OnNavigationListener;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.UmengCountEventId;
import com.songbai.futurex.view.BottomTabs;
import com.songbai.futurex.view.ScrollableViewPager;
import com.songbai.futurex.websocket.model.TradeDir;
import com.umeng.message.PushAgent;

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
    private long mFirstBackTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        translucentStatusBar();

        Apic.addBindToken(PushAgent.getInstance(getApplicationContext()).getRegistrationId()).fireFreely();

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
                switch (position) {
                    case PAGE_HOME:
                        umengEventCount(UmengCountEventId.HOME0005);
                        break;
                    case PAGE_MARKET:
                        umengEventCount(UmengCountEventId.HOME0006);
                        break;
                    case PAGE_LEGAL_CURRENCY:
                        umengEventCount(UmengCountEventId.HOME0007);
                        break;
                    case PAGE_TRADE:
                        umengEventCount(UmengCountEventId.HOME0008);
                        break;
                    case PAGE_MINE:
                        umengEventCount(UmengCountEventId.HOME0009);
                        break;
                    default:
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int mainPageIndex = intent.getIntExtra(ExtraKeys.PAGE_INDEX, -1);
        if (mainPageIndex > -1) {
            onNavigation(mainPageIndex, intent);
        }
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
        if (fragment instanceof LegalCurrencyFragment) {
            int index = exUserDefineData.getIntExtra(ExtraKeys.LEGAL_CURRENCY_PAGE_INDEX, 2);
            ((LegalCurrencyFragment) fragment).setSelectedIndex(index);
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
        if (Preference.get().getRefreshLanguage()) {
            Preference.get().setRefreshLanguage(false);
            return;
        }
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

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - mFirstBackTime > 1500) {
            // TODO: 2018/8/22 文案
            ToastUtil.show(R.string.back_press_hint);
            mFirstBackTime = secondTime;
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (KeyBoardUtils.isShouldHideKeyboard(v, ev)) {
                KeyBoardUtils.closeKeyboard(v);
                v.clearFocus();
            }
            return super.dispatchTouchEvent(ev);
        }
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
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
//                    return new LegalCurrencyFragment();
                    return new SimpleOTCFragment();
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
