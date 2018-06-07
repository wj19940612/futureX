package com.songbai.futurex.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.httplib.ReqError;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.fragment.mine.PropertyListFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.utils.Display;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class MyPropertyActivity extends BaseActivity {
    @BindView(R.id.propertyCardPager)
    ViewPager mPropertyCardPager;
    @BindView(R.id.propertyListPager)
    ViewPager mPropertyListPager;
    @BindView(R.id.indicator)
    View mIndicator;
    private Unbinder mBind;
    float mPagerTranslationX;
    private int mCardPagePadding;
    private PropertyCardAdapter mPropertyCardAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_property);
        mBind = ButterKnife.bind(this);
        mPagerTranslationX = Display.dp2Px(20, getResources());
        mPropertyCardAdapter = new PropertyCardAdapter();
        mPropertyCardPager.setAdapter(mPropertyCardAdapter);
        mPropertyCardPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("wtf", "position=" + position + ",positionOffset=" + positionOffset);
                mPropertyCardPager.setTranslationX(-mPagerTranslationX * (1 - 2 * (position + positionOffset)));
                mPropertyListPager.scrollTo((int) ((position + positionOffset) * mPropertyListPager.getMeasuredWidth()), 0);
                mIndicator.setTranslationX(mIndicator.getMeasuredWidth() * (position + positionOffset));
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mPropertyCardPager.setTranslationX(-mPagerTranslationX);
        mPropertyCardPager.setCurrentItem(0);
        mCardPagePadding = (int) Display.dp2Px(12, getResources());
        mPropertyCardPager.setPageMargin(mCardPagePadding);
        mPropertyListPager.setAdapter(new PropertyListAdapter(getSupportFragmentManager()));
        mPropertyListPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPropertyCardPager.scrollTo((int) ((position + positionOffset) * (mPropertyCardPager.getMeasuredWidth() + mCardPagePadding)), 0);
                mPropertyCardPager.setTranslationX(-mPagerTranslationX * (1 - 2 * (position + positionOffset)));
                mIndicator.setTranslationX(mIndicator.getMeasuredWidth() * (position + positionOffset));
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        findCommissionOfSubordinate();
    }

    private void findCommissionOfSubordinate() {
        Apic.findCommissionOfSubordinate()
                .callback(new Callback<Object>() {

                    @Override
                    public void onFailure(ReqError reqError) {

                    }

                    @Override
                    protected void onRespSuccess(Object resp) {

                    }
                })
                .fire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }

    public void setAccountAmount(String balance) {
        mPropertyCardAdapter.setAccountList(balance);
        mPropertyCardAdapter.notifyDataSetChanged();
    }

    static class PropertyCardAdapter extends PagerAdapter {
        @BindView(R.id.accountType)
        TextView mAccountType;
        @BindView(R.id.totalPropertyType)
        TextView mTotalPropertyType;
        @BindView(R.id.propertyAmount)
        TextView mPropertyAmount;
        @BindView(R.id.inviteNum)
        TextView mInviteNum;
        @BindView(R.id.transfer)
        TextView mTransfer;
        private String mBalance;

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Context context = container.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.row_account_property, container, false);
            ButterKnife.bind(this, view);
            switch (position) {
                case 0:
                    view.setBackgroundResource(R.drawable.property_bg_blue);
                    mAccountType.setText(R.string.personal_account);
                    mTotalPropertyType.setText(R.string.total_property_equivalent);
                    if (!TextUtils.isEmpty(mBalance)) {
                        mPropertyAmount.setText(mBalance);
                    }
                    mInviteNum.setVisibility(View.GONE);
                    mTransfer.setVisibility(View.GONE);
                    break;
                case 1:
                    view.setBackgroundResource(R.drawable.property_bg_green);
                    mAccountType.setText(R.string.promoted_account);
                    mTotalPropertyType.setText(R.string.promoted_property_equivalent);
                    mPropertyAmount.setText("0.13429464");
                    int num = 189;
                    String string = context.getString(R.string.invite_num_x, num);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.sixtyPercentWhite)),
                            0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.setSpan(new AbsoluteSizeSpan(11, true),
                            0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mInviteNum.setText(spannableStringBuilder);
                    mInviteNum.setVisibility(View.VISIBLE);
                    mTransfer.setVisibility(View.VISIBLE);
                    break;
                default:
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        void setAccountList(String balance) {
            mBalance = balance;
        }
    }

    static class PropertyListAdapter extends FragmentPagerAdapter {
        PropertyListAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PropertyListFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
