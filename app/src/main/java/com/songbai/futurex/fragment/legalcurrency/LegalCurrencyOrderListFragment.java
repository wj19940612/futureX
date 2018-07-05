package com.songbai.futurex.fragment.legalcurrency;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.OtcOrderCompletedActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.LegalCurrencyOrder;
import com.songbai.futurex.model.status.OtcOrderStatus;
import com.songbai.futurex.swipeload.BaseSwipeLoadFragment;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.view.EmptyRecyclerView;
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
 * @date 2018/6/28
 */
public class LegalCurrencyOrderListFragment extends BaseSwipeLoadFragment implements OnRVItemClickListener {
    public static final int REQUEST_ORDER_DETAIL = 12541;

    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_target)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    private Unbinder mBind;
    private int mPage;
    private int mPageSize = 20;
    private String mStatus;
    private OrderListAdapter mAdapter;

    public static LegalCurrencyOrderListFragment newInstance(String status) {
        LegalCurrencyOrderListFragment legalCurrencyOrderListFragment = new LegalCurrencyOrderListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("status", status);
        legalCurrencyOrderListFragment.setArguments(bundle);
        return legalCurrencyOrderListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_legal_currency_order_list, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mStatus = arguments.getString("status");
        }
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new OrderListAdapter();
        mAdapter.setOnRVItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        legalCurrencyOrderList(mPage, mPageSize, mStatus);
    }

    private void legalCurrencyOrderList(final int page, int pageSize, String status) {
        Apic.legalCurrencyOrderList(page, pageSize, status).tag(TAG)
                .callback(new Callback<Resp<PagingWrap<LegalCurrencyOrder>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingWrap<LegalCurrencyOrder>> resp) {
                        mAdapter.setList(resp.getData());
                        mAdapter.notifyDataSetChanged();
                        stopFreshOrLoadAnimation();
                        mPage++;
                        if (resp.getData().getTotal() <= mPage) {
                            mSwipeToLoadLayout.setLoadMoreEnabled(false);
                        } else {
                            mSwipeToLoadLayout.setLoadMoreEnabled(true);
                        }
                        if (page == 0) {
                            mRecyclerView.hideAll(false);
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
    public void onLoadMore() {
        legalCurrencyOrderList(mPage, mPageSize, mStatus);
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        legalCurrencyOrderList(mPage, mPageSize, mStatus);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ORDER_DETAIL) {
            if (data != null) {
                boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, false);
                if (shouldRefresh) {
                    mPage = 0;
                    legalCurrencyOrderList(mPage, mPageSize, mStatus);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onItemClick(View view, int position, Object obj) {
        if (obj instanceof LegalCurrencyOrder) {
            LegalCurrencyOrder legalCurrencyOrder = (LegalCurrencyOrder) obj;
            switch (legalCurrencyOrder.getStatus()) {
                case OtcOrderStatus.ORDER_CANCLED:
                case OtcOrderStatus.ORDER_COMPLATED:
                    Launcher.with(this, OtcOrderCompletedActivity.class)
                            .putExtra(ExtraKeys.ORDER_ID, legalCurrencyOrder.getId())
                            .putExtra(ExtraKeys.TRADE_DIRECTION, legalCurrencyOrder.getDirect())
                            .execute();
                    break;
                case OtcOrderStatus.ORDER_PAIED:
                case OtcOrderStatus.ORDER_UNPAIED:
                    UniqueActivity.launcher(this, LegalCurrencyOrderDetailFragment.class)
                            .putExtra(ExtraKeys.ORDER_ID, legalCurrencyOrder.getId())
                            .putExtra(ExtraKeys.TRADE_DIRECTION, legalCurrencyOrder.getDirect())
                            .execute(this, REQUEST_ORDER_DETAIL);
                    break;
                default:
            }
        }
    }

    class OrderListAdapter extends RecyclerView.Adapter {

        private List<LegalCurrencyOrder> mList = new ArrayList<>();
        private OnRVItemClickListener mOnRVItemClickListener;
        private Context mContext;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_legal_currency_order, parent, false);
            return new LegalCurrencyOrderHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof LegalCurrencyOrderHolder) {
                ((LegalCurrencyOrderHolder) holder).bindData(mList.get(position));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnRVItemClickListener != null) {
                        mOnRVItemClickListener.onItemClick(v, position, mList.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(PagingWrap<LegalCurrencyOrder> pagingWrap) {
            if (pagingWrap.getStart() == 0) {
                mList.clear();
            }
            mList.addAll(pagingWrap.getData());
        }

        public void setOnRVItemClickListener(OnRVItemClickListener onRVItemClickListener) {
            mOnRVItemClickListener = onRVItemClickListener;
        }

        class LegalCurrencyOrderHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.headPortrait)
            ImageView mHeadPortrait;
            @BindView(R.id.certification)
            ImageView mCertification;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.status)
            TextView mStatus;
            @BindView(R.id.statusContainer)
            FrameLayout mStatusContainer;
            @BindView(R.id.dealType)
            TextView mDealType;
            @BindView(R.id.tradeCount)
            TextView mTradeCount;
            @BindView(R.id.timestamp)
            TextView mTimestamp;
            @BindView(R.id.desc)
            TextView mDesc;

            public LegalCurrencyOrderHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindData(LegalCurrencyOrder legalCurrencyOrder) {
                GlideApp
                        .with(mContext)
                        .load(legalCurrencyOrder.getChangePortrait())
                        .circleCrop()
                        .into(mHeadPortrait);
                mUserName.setText(legalCurrencyOrder.getChangeName());
                switch (legalCurrencyOrder.getDirect()) {
                    case OtcOrderStatus.ORDER_DIRECT_BUY:
                        mDealType.setText(mContext.getString(R.string.buy_x,
                                legalCurrencyOrder.getCoinSymbol().toUpperCase()));
                        break;
                    case OtcOrderStatus.ORDER_DIRECT_SELL:
                        mDealType.setText(mContext.getString(R.string.sell_x,
                                legalCurrencyOrder.getCoinSymbol().toUpperCase()));
                        break;
                    default:
                }
                mTradeCount.setText(getString(R.string.trade_count_symbol_x,
                        FinanceUtil.formatWithScale(legalCurrencyOrder.getOrderAmount())
                        , legalCurrencyOrder.getPayCurrency().toUpperCase()));
                // TODO: 2018/6/29 缺少用户认证状态 字段
//                mCertification.setVisibility(authStatus == 1 || authStatus == 2 ? View.VISIBLE : View.GONE);
//                switch (authStatus) {
//                    case 1:
//                        mCertification.setImageResource(R.drawable.ic_primary_star);
//                        break;
//                    case 2:
//                        mCertification.setImageResource(R.drawable.ic_senior_star);
//                        break;
//                    default:
//                }
                mTimestamp.setText(DateUtil.format(legalCurrencyOrder.getOrderTime(),
                        DateUtil.FORMAT_SPECIAL_SLASH_NO_HOUR));
                switch (legalCurrencyOrder.getStatus()) {
                    case OtcOrderStatus.ORDER_CANCLED:
                        mStatus.setText(R.string.canceled);
                        mDesc.setText(R.string.you_have_canceled_trade);
                        break;
                    case OtcOrderStatus.ORDER_UNPAIED:
                        mStatus.setText(R.string.wait_to_pay_and_confirm);
                        mDesc.setText(R.string.otc_order_success_wait_pay);
                        break;
                    case OtcOrderStatus.ORDER_PAIED:
                        mStatus.setText(R.string.wait_sell_transfer_coin);
                        mDesc.setText(R.string.wait_sell_transfer_coin);
                        break;
                    case OtcOrderStatus.ORDER_COMPLATED:
                        mStatus.setText(R.string.completed);
                        mDesc.setText(R.string.otc_trade_complete_desc);
                        break;
                    default:
                }
            }
        }
    }
}
