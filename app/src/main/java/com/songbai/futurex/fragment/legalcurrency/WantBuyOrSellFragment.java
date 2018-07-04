package com.songbai.futurex.fragment.legalcurrency;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.fragment.mine.CashPwdFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.LegalCurrencyTrade;
import com.songbai.futurex.model.local.GetOtcWaresHome;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.status.OtcOrderStatus;
import com.songbai.futurex.swipeload.BaseSwipeLoadFragment;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.utils.inputfilter.MoneyValueFilter;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.MsgHintController;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/6/21
 */
public class WantBuyOrSellFragment extends BaseSwipeLoadFragment implements OnRVItemClickListener {
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    @BindView(R.id.swipe_target)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    private Unbinder mBind;
    private WantByAdapter mAdapter;
    private String mCoinType;
    private String mPayCurrency;
    private int mType = OtcOrderStatus.ORDER_DIRECT_SELL;
    private GetOtcWaresHome mGetOtcWaresHome;
    private int mPage;
    private boolean isPrepared;
    private boolean isFirstLoad;
    private boolean mPairChanged;

    public static WantBuyOrSellFragment newInstance(int type, String coinType, String payCurrency) {
        WantBuyOrSellFragment wantBuyOrSellFragment = new WantBuyOrSellFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("coinType", coinType);
        bundle.putString("payCurrency", payCurrency);
        wantBuyOrSellFragment.setArguments(bundle);
        return wantBuyOrSellFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        View view = inflater.inflate(R.layout.fragment_want_buy, container, false);
        mBind = ButterKnife.bind(this, view);
        isPrepared = true;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getInt("type", 2);
            mCoinType = arguments.getString("coinType");
            mPayCurrency = arguments.getString("payCurrency");
        }
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new WantByAdapter();
        mAdapter.setOnRVItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mGetOtcWaresHome = new GetOtcWaresHome();
        mGetOtcWaresHome.setPage(mPage);
        mGetOtcWaresHome.setPageSize(20);
        mGetOtcWaresHome.setType(mType);
        mGetOtcWaresHome.setSortName("synthesis");
        mGetOtcWaresHome.setSort("asc");
        mGetOtcWaresHome.setCoinType(mCoinType);
        mGetOtcWaresHome.setPayCurrency(mPayCurrency);
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) {
            lazyLoad();
        }
        if (isVisibleToUser && isPrepared && mPairChanged) {
            otcWaresCommend(mGetOtcWaresHome);
        }
    }

    private void lazyLoad() {
        if (isPrepared && getUserVisibleHint()) {
            if (isFirstLoad) {
                isFirstLoad = false;
                otcWaresCommend(mGetOtcWaresHome);
            }
        }
    }

    public void setRequestParamAndRefresh(String coinType, String payCurrency) {
        mPage = 0;
        mCoinType = coinType;
        mPayCurrency = payCurrency;
        if (mGetOtcWaresHome == null) {
            mGetOtcWaresHome = new GetOtcWaresHome();
            mGetOtcWaresHome.setPageSize(20);
            mGetOtcWaresHome.setType(mType);
            mGetOtcWaresHome.setSortName("synthesis");
            mGetOtcWaresHome.setSort("asc");
        }
        mGetOtcWaresHome.setPage(mPage);
        mGetOtcWaresHome.setCoinType(mCoinType);
        mGetOtcWaresHome.setPayCurrency(mPayCurrency);
        mPairChanged = true;
        if (getUserVisibleHint() && isPrepared && mPairChanged) {
            otcWaresCommend(mGetOtcWaresHome);
        }
    }

    private void otcWaresCommend(GetOtcWaresHome getOtcWaresHome) {
        Apic.otcWaresHome(getOtcWaresHome)
                .callback(new Callback<Resp<PagingWrap<LegalCurrencyTrade>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingWrap<LegalCurrencyTrade>> resp) {
                        if (mPairChanged) {
                            mPairChanged = false;
                        }
                        mSwipeToLoadLayout.setLoadMoreEnabled(true);
                        mAdapter.setList(resp.getData().getData());
                        mAdapter.notifyDataSetChanged();
                        stopFreshOrLoadAnimation();
                        if (mPage == 0) {
                            mRecyclerView.hideAll(false);
                        }
                        mPage++;
                        mGetOtcWaresHome.setPage(mPage);
                        if (mPage >= resp.getData().getTotal()) {
                            mSwipeToLoadLayout.setLoadMoreEnabled(false);
                        }
                    }

                    @Override
                    public void onFailure(ReqError reqError) {
                        super.onFailure(reqError);
                        stopFreshOrLoadAnimation();
                        if (mPage == 0) {
                            mRecyclerView.hideAll(false);
                        }
                    }
                }).fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
        isPrepared = false;
    }

    @Override
    public void onLoadMore() {
        otcWaresCommend(mGetOtcWaresHome);
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        mGetOtcWaresHome.setPage(mPage);
        otcWaresCommend(mGetOtcWaresHome);
    }

    @NonNull
    @Override
    public Object getSwipeTargetView() {
        return mRecyclerView;
    }

    @NonNull
    @Override
    public SwipeToLoadLayout getSwipeToLoadLayout() {
        return mSwipeToLoadLayout;
    }

    @NonNull
    @Override
    public RefreshHeaderView getRefreshHeaderView() {
        return mSwipeRefreshHeader;
    }

    @NonNull
    @Override
    public LoadMoreFooterView getLoadMoreFooterView() {
        return mSwipeLoadMoreFooter;
    }

    @Override
    public void onItemClick(View view, int position, Object obj) {
        if (LocalUser.getUser().isLogin()) {
            if (LocalUser.getUser().getUserInfo().getSafeSetting() != 1) {
                showSetDrawCashPwdHint();
            }
            showBuyOrSellView(mType, obj);
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    private void showSetDrawCashPwdHint() {
        MsgHintController withDrawPsdViewController = new MsgHintController(getActivity(), new MsgHintController.OnClickListener() {
            @Override
            public void onConfirmClick() {
                UniqueActivity.launcher(WantBuyOrSellFragment.this, CashPwdFragment.class)
                        .putExtra(ExtraKeys.HAS_WITH_DRAW_PASS, false)
                        .execute();
            }
        });
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog.setCustomViewController(withDrawPsdViewController)
                .show();
        withDrawPsdViewController.setConfirmText(R.string.go_to_set);
        withDrawPsdViewController.setMsg(R.string.set_draw_cash_pwd_hint);
        withDrawPsdViewController.setImageRes(R.drawable.ic_popup_attention);
    }

    private void showBuyOrSellView(final int type, Object obj) {
        final LegalCurrencyTrade legalCurrencyTrade = (LegalCurrencyTrade) obj;
        if (legalCurrencyTrade.getOperate() == 0) {
            ToastUtil.show(R.string.can_not_trade_with_self);
            return;
        }
        BuyOrSellController buyOrSellController = new BuyOrSellController(getContext());
        buyOrSellController.setData(legalCurrencyTrade);
        buyOrSellController.setType(type);
        buyOrSellController.setOnConfirmClickListener(new BuyOrSellController.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String coinAmount, String currencyAmout, String cashPwd) {
                if (type == OtcOrderStatus.ORDER_DIRECT_SELL) {
                    otcOrderBuy(legalCurrencyTrade.getId(), currencyAmout, coinAmount);
                } else if (type == OtcOrderStatus.ORDER_DIRECT_BUY) {
                    otcOrderSell(legalCurrencyTrade.getId(), coinAmount, cashPwd);
                }
            }
        });
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(0.8f)
                .setWindowGravity(Gravity.CENTER)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(buyOrSellController)
                .show();
    }

    private void otcOrderBuy(int id, String cost, String coinCount) {
        Apic.otcOrderBuy(id, cost, coinCount)
                .callback(new Callback<Resp<Integer>>() {
                    @Override
                    protected void onRespSuccess(Resp<Integer> resp) {
                        UniqueActivity.launcher(WantBuyOrSellFragment.this, LegalCurrencyOrderDetailFragment.class)
                                .putExtra(ExtraKeys.ORDER_ID, resp.getData())
                                .putExtra(ExtraKeys.TRADE_DIRECTION, 1)
                                .execute();
                    }
                }).fire();
    }

    private void otcOrderSell(int id, String coinCount, String drawPass) {
        Apic.otcOrderSell(id, coinCount, md5Encrypt(drawPass))
                .callback(new Callback<Resp<Integer>>() {
                    @Override
                    protected void onRespSuccess(Resp<Integer> resp) {
                        UniqueActivity.launcher(WantBuyOrSellFragment.this, LegalCurrencyOrderDetailFragment.class)
                                .putExtra(ExtraKeys.ORDER_ID, resp.getData())
                                .putExtra(ExtraKeys.TRADE_DIRECTION, 2)
                                .execute();
                    }
                }).fire();
    }

    static class BuyOrSellController extends SmartDialog.CustomViewController {

        private final Context mContext;
        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.close)
        ImageView mClose;
        @BindView(R.id.price)
        TextView mPrice;
        @BindView(R.id.tradeLimit)
        TextView mTradeLimit;
        @BindView(R.id.coinAmount)
        EditText mCoinAmount;
        @BindView(R.id.coinSymbol)
        TextView mCoinSymbol;
        @BindView(R.id.coinGroup)
        LinearLayout mCoinGroup;
        @BindView(R.id.currencyGroup)
        LinearLayout mCurrencyGroup;
        @BindView(R.id.currencyAmount)
        EditText mCurrencyAmount;
        @BindView(R.id.currencySymbol)
        TextView mCurrencySymbol;
        @BindView(R.id.drawCashPwd)
        EditText mDrawCashPwd;
        @BindView(R.id.confirm)
        TextView mConfirm;
        private LegalCurrencyTrade mData;
        private int mType;

        private OnConfirmClickListener mOnConfirmClickListener;

        public BuyOrSellController(Context context) {
            mContext = context;
        }

        @Override
        protected View onCreateView() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_buy_or_sell, null);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        protected void onInitView(View view, final SmartDialog dialog) {
            String coinSymbol = mData.getCoinSymbol();
            mTitle.setText(mContext.getString(mType == OtcOrderStatus.ORDER_DIRECT_SELL ? R.string.buy_x : R.string.sell_x, coinSymbol.toUpperCase()));
            mDrawCashPwd.setVisibility(mType == OtcOrderStatus.ORDER_DIRECT_SELL ? View.GONE : View.VISIBLE);
            mCurrencyGroup.setVisibility(mType == OtcOrderStatus.ORDER_DIRECT_SELL ? View.VISIBLE : View.GONE);
            String payCurrency = mData.getPayCurrency();
            mPrice.setText(mData.getFixedPrice() + payCurrency.toUpperCase());
            final double maxTurnover = mData.getMaxTurnover();
            mTradeLimit.setText(mContext.getString(R.string.amount_limit, FinanceUtil.trimTrailingZero(mData.getMinTurnover())
                    , FinanceUtil.trimTrailingZero(maxTurnover)));
            mCoinSymbol.setText(coinSymbol.toUpperCase());
            mCurrencySymbol.setText(payCurrency.toUpperCase());
            double maxNum = Double.valueOf(mData.getChangeCount());
            maxNum = Math.min(maxNum, maxTurnover / mData.getFixedPrice());
            mCoinAmount.setHint(mContext.getString(mType == OtcOrderStatus.ORDER_DIRECT_SELL ? R.string.max_buy_amount : R.string.max_sell_amount, FinanceUtil.formatWithScale(maxNum, 6)));
            mCoinAmount.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(6)});
            final double finalMaxNum = maxNum;
            mCoinAmount.addTextChangedListener(new ValidationWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    String string = mCoinAmount.getText().toString();
                    if (!TextUtils.isEmpty(string)) {
                        mCurrencyAmount.setText("");
                        double value = Double.valueOf(string);
                        if (value > finalMaxNum) {
                            mCoinAmount.setText(FinanceUtil.formatWithScale(finalMaxNum, 6));
                            mCoinAmount.setSelection(mCoinAmount.getText().length());
                        }
                    }
                }
            });
            mCurrencyAmount.addTextChangedListener(new ValidationWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    String string = mCurrencyAmount.getText().toString();
                    if (!TextUtils.isEmpty(string)) {
                        mCoinAmount.setText("");
                        double value = Double.valueOf(string);
                        if (value > maxTurnover) {
                            mCurrencyAmount.setText(String.valueOf(maxTurnover));
                            mCurrencyAmount.setSelection(mCurrencyAmount.getText().length());
                        }
                    }
                }
            });
            mCurrencyAmount.setHint(mContext.getString(R.string.max_buy_amount, FinanceUtil.formatWithScale(maxTurnover)));
            mClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currencyAmount = mCurrencyAmount.getText().toString();
                    String coinAmount = mCoinAmount.getText().toString();
                    String cashPwd = mDrawCashPwd.getText().toString();
                    if (TextUtils.isEmpty(currencyAmount) && TextUtils.isEmpty(coinAmount)) {
                        ToastUtil.show(R.string.trade_amount_empty_hint);
                        return;
                    }
                    if (mType == OtcOrderStatus.ORDER_DIRECT_BUY && TextUtils.isEmpty(cashPwd)) {
                        ToastUtil.show(R.string.cash_pwd_empty_hint);
                        return;
                    }
                    if (mOnConfirmClickListener != null) {
                        mOnConfirmClickListener.onConfirmClick(mCoinAmount.getText().toString(), mCurrencyAmount.getText().toString(), mDrawCashPwd.getText().toString());
                    }
                }
            });
        }

        interface OnConfirmClickListener {
            void onConfirmClick(String coinAmount, String currencyAmout, String cashPwd);
        }

        public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
            mOnConfirmClickListener = onConfirmClickListener;
        }

        public void setData(LegalCurrencyTrade data) {
            mData = data;
        }

        public void setType(int type) {
            mType = type;
        }
    }

    class WantByAdapter extends RecyclerView.Adapter {
        private OnRVItemClickListener mOnRVItemClickListener;
        private List<LegalCurrencyTrade> mList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_want_buy, parent, false);
            return new WantBuyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof WantBuyHolder) {
                ((WantBuyHolder) holder).bindData(mList.get(position));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnRVItemClickListener != null) {
                        mOnRVItemClickListener.onItemClick(holder.itemView, position, mList.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(List<LegalCurrencyTrade> list) {
            if (mPage == 0) {
                mList.clear();
            }
            mList.addAll(list);
        }

        public void setOnRVItemClickListener(OnRVItemClickListener onRVItemClickListener) {
            mOnRVItemClickListener = onRVItemClickListener;
        }

        class WantBuyHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.headPortrait)
            ImageView mHeadPortrait;
            @BindView(R.id.certification)
            ImageView mCertification;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.countDealRate)
            TextView mCountDealRate;
            @BindView(R.id.price)
            TextView mPrice;
            @BindView(R.id.sumSection)
            TextView mSumSection;
            @BindView(R.id.ownCount)
            TextView mOwnCount;
            @BindView(R.id.wechatPayIcon)
            ImageView mWechatPayIcon;
            @BindView(R.id.unionPayIcon)
            ImageView mUnionPayIcon;
            @BindView(R.id.aliPayIcon)
            ImageView mAliPayIcon;

            WantBuyHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindData(LegalCurrencyTrade legalCurrencyTrade) {
                GlideApp
                        .with(getContext())
                        .load(legalCurrencyTrade.getUserPortrait())
                        .circleCrop()
                        .into(mHeadPortrait);
                int authStatus = legalCurrencyTrade.getAuthStatus();
                mCertification.setVisibility(authStatus == 1 || authStatus == 2 ? View.VISIBLE : View.GONE);
                switch (authStatus) {
                    case 1:
                        mCertification.setImageResource(R.drawable.ic_primary_star);
                        break;
                    case 2:
                        mCertification.setImageResource(R.drawable.ic_senior_star);
                        break;
                    default:
                }
                mUserName.setText(legalCurrencyTrade.getUsername());
                mPrice.setText(getString(R.string.x_space_x,
                        FinanceUtil.formatWithScale(legalCurrencyTrade.getFixedPrice()),
                        legalCurrencyTrade.getPayCurrency().toUpperCase()));
                mSumSection.setText(getString(R.string.amount_limit, String.valueOf(legalCurrencyTrade.getMinTurnover()), String.valueOf(legalCurrencyTrade.getMaxTurnover())));
                mOwnCount.setText(getString(R.string.own_amount, legalCurrencyTrade.getChangeCount()));
                mCountDealRate.setText(getString(R.string.x_done_count_done_rate_x,
                        legalCurrencyTrade.getCountDeal(),
                        FinanceUtil.formatToPercentage(legalCurrencyTrade.getDoneRate())));
                String payInfo = legalCurrencyTrade.getPayInfo();
                mWechatPayIcon.setVisibility(payInfo.contains("wxPay") ? View.VISIBLE : View.GONE);
                mAliPayIcon.setVisibility(payInfo.contains("aliPay") ? View.VISIBLE : View.GONE);
                mUnionPayIcon.setVisibility(payInfo.contains("bankPay") ? View.VISIBLE : View.GONE);
            }
        }
    }
}