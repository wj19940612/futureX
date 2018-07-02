package com.songbai.futurex.activity.mine;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.Order;
import com.songbai.futurex.model.local.SysTime;
import com.songbai.futurex.model.status.OrderStatus;
import com.songbai.futurex.swipeload.RVSwipeLoadActivity;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.NumUtils;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.TitleBar;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Modified by john on 2018/7/2
 * <p>
 * Description: 币币交易订单页面
 * <p>
 * APIs:
 */
public class TradeOrdersActivity extends RVSwipeLoadActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_target)
    EmptyRecyclerView mSwipeTarget;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.rootView)
    ConstraintLayout mRootView;

    private int mPage;
    private OrderAdapter mOrderAdapter;
    private RadioHeader mRadioHeader;
    private TextView mFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_orders);
        ButterKnife.bind(this);

        initTitleBar();

        mSwipeTarget.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOrderAdapter = new OrderAdapter(new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {

            }
        });
        mOrderAdapter.setOnOrderRevokeClickListener(new OrderAdapter.OnOrderRevokeClickListener() {
            @Override
            public void onOrderRevoke(Order order) {
                requestRevokeOrder(order);
            }
        });
        mSwipeTarget.setAdapter(mOrderAdapter);
        mSwipeTarget.setEmptyView(mEmptyView);

        requestOrderList();
    }

    private void initTitleBar() {
        View customView = mTitleBar.getCustomView();
        mRadioHeader = customView.findViewById(R.id.radioHeader);
        mFilter = customView.findViewById(R.id.filter);
        mRadioHeader.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                if (position == 0) {
                    mOrderAdapter.setOrderType(Order.TYPE_CUR_ENTRUST);
                } else {
                    mOrderAdapter.setOrderType(Order.TYPE_HIS_ENTRUST);
                }
                mPage = 0;
                requestOrderList();
            }
        });
    }

    private void requestRevokeOrder(Order order) {
        Apic.revokeOrder(order.getId()).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        requestOrderList();
                    }
                }).fire();
    }

    private void requestOrderList() {
        int type = mRadioHeader.getSelectedPosition() == 0 ? Order.TYPE_CUR_ENTRUST : Order.TYPE_HIS_ENTRUST;
        String endTime = Uri.encode(DateUtil.format(SysTime.getSysTime().getSystemTimestamp()));
        Apic.getEntrustOrderList(mPage, type, endTime, null, null).tag(TAG)
                .id(mRadioHeader.getSelectTab())
                .callback(new Callback4Resp<Resp<PagingWrap<Order>>, PagingWrap<Order>>() {
                    @Override
                    protected void onRespData(PagingWrap<Order> data) {
                        if (getId().equalsIgnoreCase(mRadioHeader.getSelectTab())) {
                            if (mPage == 0) {
                                mOrderAdapter.setOrderList(data.getData());
                            } else {
                                mOrderAdapter.appendOrderList(data.getData());
                            }
                            stopLoadMoreAnimation();

                            if (data.getData().size() < Apic.DEFAULT_PAGE_SIZE) {
                                mSwipeToLoadLayout.setLoadMoreEnabled(false);
                            } else {
                                mSwipeToLoadLayout.setLoadMoreEnabled(true);
                            }
                        }
                    }
                }).fireFreely();
    }

    @Override
    public View getContentView() {
        return mRootView;
    }

    @Override
    public void onLoadMore() {
        mPage++;
        requestOrderList();
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        requestOrderList();
    }

    static class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        static class HistoryViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tradePair)
            TextView mTradePair;
            @BindView(R.id.orderStatus)
            TextView mOrderStatus;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.entrustPrice)
            TextView mEntrustPrice;
            @BindView(R.id.entrustVolume)
            TextView mEntrustVolume;
            @BindView(R.id.dealTotalAmt)
            TextView mDealTotalAmt;
            @BindView(R.id.dealAveragePrice)
            TextView mDealAveragePrice;
            @BindView(R.id.dealVolume)
            TextView mDealVolume;
            @BindView(R.id.entrustPriceTitle)
            TextView mEntrustPriceTitle;
            @BindView(R.id.entrustVolumeTitle)
            TextView mEntrustVolumeTitle;
            @BindView(R.id.dealTotalAmtTitle)
            TextView mDealTotalAmtTitle;
            @BindView(R.id.dealAveragePriceTitle)
            TextView mDealAveragePriceTitle;
            @BindView(R.id.dealVolumeTitle)
            TextView mDealVolumeTitle;

            public HistoryViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(Order order, Context context) {
                int color = ContextCompat.getColor(context, R.color.green);
                String tradeDir = context.getString(R.string.buy_in);
                if (order.getDirection() == Order.DIR_SELL) {
                    color = ContextCompat.getColor(context, R.color.red);
                    tradeDir = context.getString(R.string.sell_out);
                }
                if (order.getStatus() == OrderStatus.REVOKED) {
                    color = ContextCompat.getColor(context, R.color.text49);
                }
                mTradePair.setText(tradeDir + " " + order.getPairs().toUpperCase());
                mTradePair.setTextColor(color);
                mTime.setText(DateUtil.format(order.getOrderTime(), "mm:ss MM/dd"));
                mEntrustPrice.setText(NumUtils.getPrice(order.getEntrustPrice()));
                mEntrustVolume.setText(NumUtils.getVolume(order.getEntrustCount()));
                mOrderStatus.setText(getStatusTextRes(order.getStatus()));
                mEntrustPriceTitle.setText(context.getString(R.string.entrust_price_x, order.getSuffix()));
                mEntrustVolumeTitle.setText(context.getString(R.string.entrust_volume_x, order.getPrefix()));
                mDealTotalAmt.setText(NumUtils.getAmt(order.getDealCount() * order.getDealPrice()));
                mDealAveragePrice.setText(NumUtils.getPrice(order.getDealPrice()));
                mDealVolume.setText(NumUtils.getVolume(order.getDealCount()));
                mDealTotalAmtTitle.setText(context.getString(R.string.deal_total_amt_x, order.getSuffix()));
                mDealAveragePriceTitle.setText(context.getString(R.string.deal_average_price_x, order.getSuffix()));
                mDealVolumeTitle.setText(context.getString(R.string.deal_volume_x, order.getPrefix()));
            }

            private int getStatusTextRes(int status) {
                switch (status) {
                    case OrderStatus.REVOKED:
                        return R.string.revoked;
                    case OrderStatus.ALL_DEAL:
                        return R.string.deal;
                    case OrderStatus.PART_DEAL:
                        return R.string.part_deal;
                    case OrderStatus.SYSTEM_REVOKED:
                        return R.string.system_revoked;
                    case OrderStatus.PART_DEAL_PART_REVOKED:
                        return R.string.part_deal_part_revoked;
                    default:
                        return R.string.unknown_operation;
                }
            }
        }

        static class EntrustViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tradePair)
            TextView mTradePair;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.revoke)
            TextView mRevoke;
            @BindView(R.id.entrustPrice)
            TextView mEntrustPrice;
            @BindView(R.id.entrustVolume)
            TextView mEntrustVolume;
            @BindView(R.id.actualDealVolume)
            TextView mActualDealVolume;
            @BindView(R.id.entrustPriceTitle)
            TextView mEntrustPriceTitle;
            @BindView(R.id.entrustVolumeTitle)
            TextView mEntrustVolumeTitle;
            @BindView(R.id.actualDealVolumeTitle)
            TextView mActualDealVolumeTitle;

            public EntrustViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(final Order order, Context context, final OnOrderRevokeClickListener onOrderRevokeClickListener) {
                int color = ContextCompat.getColor(context, R.color.green);
                String tradeDir = context.getString(R.string.buy_in);
                if (order.getDirection() == Order.DIR_SELL) {
                    color = ContextCompat.getColor(context, R.color.red);
                    tradeDir = context.getString(R.string.sell_out);
                }
                mTradePair.setText(tradeDir + " " + order.getPairs().toUpperCase());
                mTradePair.setTextColor(color);
                mTime.setText(DateUtil.format(order.getOrderTime(), "mm:ss MM/dd"));
                mEntrustPrice.setText(NumUtils.getPrice(order.getEntrustPrice()));
                mEntrustVolume.setText(NumUtils.getVolume(order.getEntrustCount()));
                mActualDealVolume.setText(NumUtils.getVolume(order.getDealCount()));
                mRevoke.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onOrderRevokeClickListener != null) {
                            onOrderRevokeClickListener.onOrderRevoke(order);
                        }
                    }
                });
                mEntrustPriceTitle.setText(context.getString(R.string.entrust_price_x, order.getSuffix()));
                mEntrustVolumeTitle.setText(context.getString(R.string.entrust_volume_x, order.getPrefix()));
                mActualDealVolumeTitle.setText(context.getString(R.string.actual_deal_x, order.getPrefix()));
            }
        }

        interface OnOrderRevokeClickListener {
            void onOrderRevoke(Order order);
        }

        private List<Order> mOrderList;
        private OnRVItemClickListener mOnRVItemClickListener;
        private Context mContext;
        private int mOrderType;
        private OnOrderRevokeClickListener mOnOrderRevokeClickListener;
        private Set<String> mOrderIdSet;

        public OrderAdapter(OnRVItemClickListener onRVItemClickListener) {
            mOrderList = new ArrayList<>();
            mOnRVItemClickListener = onRVItemClickListener;
            mOrderIdSet = new HashSet<>();
            mOrderType = Order.TYPE_CUR_ENTRUST;
        }

        public void setOrderType(int orderType) {
            mOrderType = orderType;
        }

        public void setOrderList(List<Order> orderList) {
            mOrderList = orderList;
            mOrderIdSet.clear();
            for (Order order : mOrderList) {
                mOrderIdSet.add(order.getId());
            }
            notifyDataSetChanged();
        }

        public void appendOrderList(List<Order> orderList) {
            for (Order order : orderList) {
                if (mOrderIdSet.add(order.getId())) {
                    mOrderList.add(order);
                }
            }
            notifyDataSetChanged();
        }

        public void setOnOrderRevokeClickListener(OnOrderRevokeClickListener onOrderRevokeClickListener) {
            mOnOrderRevokeClickListener = onOrderRevokeClickListener;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            if (viewType == Order.TYPE_CUR_ENTRUST) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_entrust_order, parent, false);
                return new EntrustViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history_order, parent, false);
                return new HistoryViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof EntrustViewHolder) {
                ((EntrustViewHolder) holder).bind(mOrderList.get(position), mContext, mOnOrderRevokeClickListener);
            } else if (holder instanceof HistoryViewHolder) {
                ((HistoryViewHolder) holder).bind(mOrderList.get(position), mContext);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRVItemClickListener.onItemClick(holder.itemView, position, mOrderList.get(position));
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return mOrderType;
        }

        @Override
        public int getItemCount() {
            return mOrderList.size();
        }
    }
}
