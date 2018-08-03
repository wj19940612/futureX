package com.songbai.futurex.wrapper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.songbai.futurex.R;
import com.songbai.futurex.wrapper.fragment.WrapGameFragment;
import com.songbai.futurex.wrapper.fragment.WrapHomeFragment;
import com.songbai.futurex.wrapper.fragment.WrapMineFragment;
import com.songbai.futurex.wrapper.fragment.WrapNewsFragment;
import com.songbai.wrapres.BottomTabs;
import com.songbai.wrapres.ScrollableViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Modified by john on 2018/7/11
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class WrapMainActivity extends WrapBaseActivity {

    @BindView(R.id.viewPager)
    ScrollableViewPager mViewPager;
    @BindView(R.id.bottomTabs)
    BottomTabs mBottomTabs;

    private MainFragmentsAdapter mMainFragmentsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_main);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        mMainFragmentsAdapter = new MainFragmentsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMainFragmentsAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setScrollable(false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mBottomTabs.selectTab(position);
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
            }
        });
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
                case 0:
                    return new WrapNewsFragment();
                case 1:
                    return new WrapHomeFragment();
                case 2:
                    return new WrapMineFragment();
                case 3:
                    return new WrapGameFragment();
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

}
