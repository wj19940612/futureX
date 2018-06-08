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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.httplib.ReqError;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.mine.FundsTransferFragment;
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
    @BindView(R.id.indicatorContiner)
    LinearLayout mIndicatorContiner;
    @BindView(R.id.indicator)
    View mIndicator;
    private Unbinder mBind;
    float mPagerTranslationX;
    private int mCardPagePadding;
    private PropertyCardAdapter mPropertyCardAdapter;
    int size = 3;
    private int mScrollWidth;
    private String[] mAccountAmount = new String[3];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_property);
        mBind = ButterKnife.bind(this);
        mPagerTranslationX = Display.dp2Px(20, getResources());
        mIndicatorContiner.post(new Runnable() {
            @Override
            public void run() {
                int measuredWidth = mIndicatorContiner.getMeasuredWidth();
                mScrollWidth = (int) (measuredWidth / size + 0.5);
                ViewGroup.LayoutParams layoutParams = mIndicator.getLayoutParams();
                layoutParams.width = mScrollWidth;
            }
        });
        mPropertyCardAdapter = new PropertyCardAdapter();
        mPropertyCardAdapter.setData(mAccountAmount);
        mPropertyCardPager.setOffscreenPageLimit(2);
        mPropertyCardPager.setAdapter(mPropertyCardAdapter);
        mPropertyCardPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setCardPagerTranslationX(position, positionOffset);
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
        mPropertyListPager.setOffscreenPageLimit(2);
        mPropertyListPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPropertyCardPager.scrollTo((int) ((position + positionOffset) * (mPropertyCardPager.getMeasuredWidth() + mCardPagePadding)), 0);
                setCardPagerTranslationX(position, positionOffset);
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

    private void setCardPagerTranslationX(int position, float positionOffset) {
        if (position <= 1) {
            mPropertyCardPager.setTranslationX(-mPagerTranslationX * (1 - (position + positionOffset)));
        } else if (position >= size - 2) {
            mPropertyCardPager.setTranslationX(mPagerTranslationX * (position + positionOffset - size + 2));
        } else {
            mPropertyCardPager.setTranslationX(0);
        }
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

    public void setAccountAmount(int position, String balance) {
        mAccountAmount[position] = balance;
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
        private String[] mData;

        @Override
        public int getCount() {
            return 3;
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
            final Context context = container.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.row_account_property, container, false);
            ButterKnife.bind(this, view);
            if (!TextUtils.isEmpty(mData[position])) {
                mPropertyAmount.setText(mData[position]);
            }
            switch (position) {
                case 0:
                    view.setBackgroundResource(R.drawable.property_bg_purple);
                    mAccountType.setText(R.string.coin_coin_account);
                    mTotalPropertyType.setText(R.string.total_property_equivalent);
                    mTransfer.setText(R.string.transfer);
                    mInviteNum.setVisibility(View.GONE);
                    break;
                case 1:
                    view.setBackgroundResource(R.drawable.property_bg_blue);
                    mAccountType.setText(R.string.legal_currency_account);
                    mTotalPropertyType.setText(R.string.total_property_equivalent);
                    mTransfer.setText(R.string.transfer);
                    mInviteNum.setVisibility(View.GONE);
                    break;
                case 2:
                    view.setBackgroundResource(R.drawable.property_bg_green);
                    mAccountType.setText(R.string.promoted_account);
                    mTotalPropertyType.setText(R.string.promoted_property_equivalent);
                    int num = 189;
                    String string = context.getString(R.string.invite_num_x, num);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.sixtyPercentWhite)),
                            0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.setSpan(new AbsoluteSizeSpan(11, true),
                            0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mInviteNum.setText(spannableStringBuilder);
                    mTransfer.setText(R.string.fast_transfer);
                    mInviteNum.setVisibility(View.VISIBLE);
                    break;
                default:
            }
            mTransfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UniqueActivity.launcher(context, FundsTransferFragment.class).execute();
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        public void setData(String[] data) {
            mData = data;
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
            return 3;
        }
    }
}
