package com.songbai.futurex.fragment.mine;

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

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.PromoterInfo;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.autofit.AutofitTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/7/31
 */
public class MyInviteFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.inviteNum)
    AutofitTextView mInviteNum;
    @BindView(R.id.awardAmount)
    AutofitTextView mAwardAmount;
    @BindView(R.id.radioHeader)
    RadioHeader mRadioHeader;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_invite, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(InviteHistoryFragment.newInstance());
        list.add(InviteAwardHistoryFragment.newInstance());
        mViewPager.setAdapter(new HistoryAdapter(getChildFragmentManager(), list));
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
        getCurrentPromoterMsg();
    }

    private void getCurrentPromoterMsg() {
        Apic.getCurrentPromoterMsg().tag(TAG)
                .callback(new Callback<Resp<PromoterInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<PromoterInfo> resp) {
                        PromoterInfo promoterInfo = resp.getData();
                        if (promoterInfo != null) {
                            mInviteNum.setText(String.valueOf(promoterInfo.getMyUsersCount()));
                            mAwardAmount.setText(getString(R.string.x_bfb, String.valueOf(promoterInfo.getTotalCom())));
                        }
                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    private class HistoryAdapter extends FragmentPagerAdapter {

        private final ArrayList<Fragment> mList;

        public HistoryAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            mList = list;
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
