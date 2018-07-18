package com.songbai.futurex.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.httplib.ReqError;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.mine.FundsTransferFragment;
import com.songbai.futurex.fragment.mine.PropertyListFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.AccountBean;
import com.songbai.futurex.model.mine.AccountList;
import com.songbai.futurex.model.mine.InviteSubordinate;
import com.songbai.futurex.utils.Display;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class MyPropertyActivity extends BaseActivity {
    private static int REQUEST_TRANSFER = 1223;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.propertyCardPager)
    ViewPager mPropertyCardPager;
    @BindView(R.id.propertyListPager)
    ViewPager mPropertyListPager;
    @BindView(R.id.indicatorContainer)
    LinearLayout mIndicatorContainer;
    @BindView(R.id.indicator)
    View mIndicator;
    private Unbinder mBind;
    float mPagerTranslationX;
    private int mCardPagePadding;
    private PropertyCardAdapter mPropertyCardAdapter;
    int mCardSize = 2;
    private int mScrollWidth;
    private SparseArray<AccountList> mAccountLists = new SparseArray<>(3);
    private ArrayList<PropertyListFragment> mFragments;
    private SmartDialog mSmartDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_property);
        mBind = ButterKnife.bind(this);
        initView();
        findCommissionOfSubordinate();
    }

    private void initView() {
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(MyPropertyActivity.this, PropertyFlowActivity.class)
                        .putExtra(ExtraKeys.PROPERTY_FLOW_FILTER_TYPE_ALL, true)
                        .putExtra(ExtraKeys.PROPERTY_FLOW_ACCOUNT_TYPE, mPropertyListPager.getCurrentItem())
                        .execute();
            }
        });
        mPagerTranslationX = Display.dp2Px(20, getResources());
        mIndicatorContainer.post(new Runnable() {
            @Override
            public void run() {
                int measuredWidth = mIndicatorContainer.getMeasuredWidth();
                mScrollWidth = (int) (measuredWidth / mCardSize + 0.5);
                ViewGroup.LayoutParams layoutParams = mIndicator.getLayoutParams();
                layoutParams.width = mScrollWidth;
                mIndicator.setLayoutParams(layoutParams);
            }
        });
        mPropertyCardAdapter = new PropertyCardAdapter(this);
        mPropertyCardAdapter.setData(mAccountLists);
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
                setCardPagerTranslationX(position, 0);
                mPropertyListPager.setCurrentItem(position, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mPropertyCardPager.setTranslationX(-mPagerTranslationX);
        mPropertyCardPager.setCurrentItem(0);
        mCardPagePadding = (int) Display.dp2Px(12, getResources());
        mPropertyCardPager.setPageMargin(mCardPagePadding);
        mFragments = new ArrayList<>();
        for (int i = 0; i < mCardSize; i++) {
            mFragments.add(PropertyListFragment.newInstance(i));
        }
        PropertyListAdapter adapter = new PropertyListAdapter(getSupportFragmentManager());
        adapter.setList(mFragments);
        mPropertyListPager.setAdapter(adapter);
        mPropertyListPager.setOffscreenPageLimit(2);
        mPropertyListPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setCardPagerTranslationX(position, positionOffset);
                mPropertyCardPager.scrollTo((int) ((position + positionOffset) * (mPropertyCardPager.getMeasuredWidth() + mCardPagePadding)), 0);
                mIndicator.setTranslationX(mIndicator.getMeasuredWidth() * (position + positionOffset));
            }

            @Override
            public void onPageSelected(int position) {
                setCardPagerTranslationX(position, 0);
                mPropertyCardPager.setCurrentItem(position, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setCardPagerTranslationX(int position, float positionOffset) {
        float translateX = position + positionOffset;
        float translateXRightBorder = mCardSize - 2;
        float translateXLeftBorder = 1;
        float scale = 1;
        if (mCardSize < 3) {
            translateXRightBorder = mCardSize - 1.5f;
            translateXLeftBorder = 1 - 0.5f;
            scale = 2;
        }
        if (translateX < translateXLeftBorder) {
            mPropertyCardPager.setTranslationX(-mPagerTranslationX * scale * (translateXLeftBorder - (translateX)));
        } else {
            if (translateX > translateXRightBorder) {
                mPropertyCardPager.setTranslationX(mPagerTranslationX * scale * (translateX - translateXRightBorder));
            } else {
                mPropertyCardPager.setTranslationX(0);
            }
        }
    }

    private void findCommissionOfSubordinate() {
        Apic.findCommissionOfSubordinate().tag(TAG)
                .callback(new Callback<Resp<InviteSubordinate>>() {

                    @Override
                    protected void onRespSuccess(Resp<InviteSubordinate> resp) {
                        InviteSubordinate inviteSubordinate = resp.getData();
                        int resultCount = inviteSubordinate.getResultCount();
                        mPropertyCardAdapter.setInviteCount(resultCount);
                    }

                    @Override
                    public void onFailure(ReqError reqError) {

                    }
                })
                .fire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }

    public void setAccountAmount(int position, AccountList accountList) {
        mAccountLists.put(position, accountList);
        mPropertyCardAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TRANSFER) {
            if (data != null) {
                boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, false);
                if (shouldRefresh) {
                    for (PropertyListFragment fragment : mFragments) {
                        fragment.requestData();
                    }
                }
            }
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

    class PropertyCardAdapter extends PagerAdapter {
        private final Context mContext;
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
        private SparseArray<AccountList> mData;
        private int mInviteCount;

        PropertyCardAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mCardSize;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            int position = (int) ((View) object).getTag();
            setData(position, mContext, (View) object);
            return super.getItemPosition(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            final Context context = container.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.row_account_property, container, false);
            ButterKnife.bind(this, view);
            view.setTag(position);
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
                    mInviteNum.setVisibility(View.VISIBLE);
                    mTransfer.setText(R.string.fast_transfer);
                    break;
                default:
            }
            setData(position, context, view);
            container.addView(view);
            return view;
        }

        private void setData(final int position, final Context context, View view) {
            TextView mPropertyAmount = view.findViewById(R.id.propertyAmount);
            TextView mInviteNum = view.findViewById(R.id.inviteNum);
            TextView mTransfer = view.findViewById(R.id.transfer);
            final AccountList accountList = mData.get(position);
            if (accountList != null) {
                double balance = accountList.getBalance();
                mPropertyAmount.setText(FinanceUtil.formatWithScale(balance, 8));
            }
            if (position == 2) {
                String string = context.getString(R.string.invite_num_x, mInviteCount);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.sixtyPercentWhite)),
                        0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(new AbsoluteSizeSpan(11, true),
                        0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mInviteNum.setText(spannableStringBuilder);
            }
            mTransfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<AccountBean> accountBeans;
                    if (accountList != null) {
                        accountBeans = accountList.getAccount();
                    } else {
                        accountBeans = new ArrayList<>();
                    }
                    switch (position) {
                        case 0:
                            ArrayList<AccountBean> list = new ArrayList<>();
                            for (AccountBean accountBean : accountBeans) {
                                if (accountBean.getLegal() == AccountBean.IS_LEGAL) {
                                    list.add(accountBean);
                                }
                            }
                            if (list.size() > 0) {
                                openTransfer(list, context, position);
                            }
                            break;
                        case 1:
                            openTransfer((ArrayList<? extends Parcelable>) accountBeans, context, position);
                            break;
                        case 2:
                            showTransferPop();
                            break;
                        default:
                    }
                }
            });
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        public void setData(SparseArray<AccountList> data) {
            mData = data;
        }

        public void setInviteCount(int inviteCount) {
            mInviteCount = inviteCount;
        }
    }

    private void showTransferPop() {
        TransferController transferController = new TransferController(this, new TransferController.OnClickListener() {
            @Override
            public void onConfirmClick() {
                userTransfer();
            }
        });
        mSmartDialog = SmartDialog.solo(this);
        mSmartDialog
                .setWidthScale(0.8f)
                .setWindowGravity(Gravity.CENTER)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(transferController)
                .show();
    }

    private void userTransfer() {
        Apic.userTransfer("").tag(TAG).callback(new Callback<Resp>() {
            @Override
            protected void onRespSuccess(Resp resp) {
                ToastUtil.show(R.string.transfer_success);
                if (mSmartDialog != null) {
                    mSmartDialog.dismiss();
                }
            }
        }).fire();
    }

    static class TransferController extends SmartDialog.CustomViewController {
        @BindView(R.id.close)
        ImageView mClose;
        @BindView(R.id.confirm)
        TextView mConfirm;
        private Context mContext;
        private OnClickListener mOnClickListener;

        public interface OnClickListener {
            void onConfirmClick();
        }

        public TransferController(Context context, OnClickListener onClickListener) {
            mContext = context;
            mOnClickListener = onClickListener;
        }

        @Override
        protected View onCreateView() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_transfer, null);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        protected void onInitView(View view, final SmartDialog dialog) {
            mClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onConfirmClick();
                    }
                }
            });
        }
    }

    private static void openTransfer(ArrayList<? extends Parcelable> accountBeans, Context context, int position) {
        UniqueActivity.launcher(context, FundsTransferFragment.class)
                .putExtra(ExtraKeys.TRANSFER_TYPE, position)
                .putExtra(ExtraKeys.ACCOUNT_BEANS, accountBeans)
                .execute(REQUEST_TRANSFER);
    }

    static class PropertyListAdapter extends FragmentPagerAdapter {
        private ArrayList<PropertyListFragment> mList;

        PropertyListAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        public void setList(ArrayList<PropertyListFragment> list) {
            mList = list;
        }
    }
}
