package com.songbai.futurex.activity.mine;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.DealDetailFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.SysTime;
import com.songbai.futurex.model.order.Order;
import com.songbai.futurex.model.order.OrderStatus;
import com.songbai.futurex.swipeload.RVSwipeLoadActivity;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.KeyBoardHelper;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.HistoryFilter;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.PushDestUtils;
import com.songbai.futurex.websocket.market.MarketSubscriber;
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
    @BindView(R.id.filterView)
    ConstraintLayout mFilterView;
    @BindView(R.id.dimView)
    View mDimView;

    private int mPage;
    private OrderAdapter mOrderAdapter;
    private RadioHeader mRadioHeader;

    private TextView mFilter;
    private HistoryFilter mHistoryFilter;
    private MarketSubscriber mMarketSubscriber;
    private KeyBoardHelper mKeyBoardHelper;
    private boolean mKeyboardVisible;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_orders);
        ButterKnife.bind(this);

        mKeyBoardHelper = new KeyBoardHelper(getActivity());
        mKeyBoardHelper.onCreate();
        mKeyBoardHelper.setOnKeyBoardStatusChangeListener(new KeyBoardHelper.OnKeyBoardStatusChangeListener() {
            @Override
            public void OnKeyBoardPop(int keyboardHeight) {
                mKeyboardVisible = true;
            }

            @Override
            public void OnKeyBoardClose(int oldKeyboardHeight) {
                mKeyboardVisible = false;
            }
        });

        initTitleBar();
        initFilterView();

        mSwipeTarget.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOrderAdapter = new OrderAdapter(new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                if (obj instanceof Order && ((Order) obj).hasDealDetail()) {
                    UniqueActivity.launcher(getActivity(), DealDetailFragment.class)
                            .putExtra(ExtraKeys.ORDER, (Parcelable) obj)
                            .execute();
                }
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

        mMarketSubscriber = new MarketSubscriber(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code, String dest) {
                if (PushDestUtils.isEntrustOrder(dest)) {
                    requestOrderList();
                }
            }
        });

        requestOrderList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mKeyBoardHelper.onDestroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mMarketSubscriber.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMarketSubscriber.pause();
    }

    private void initTitleBar() {
        View customView = mTitleBar.getCustomView();
        mRadioHeader = customView.findViewById(R.id.radioHeader);
        mFilter = customView.findViewById(R.id.filter);
        mRadioHeader.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                if (position == 0) {
                    mEmptyView.setVisibility(View.GONE);
                    mOrderAdapter.setOrderType(Order.TYPE_CUR_ENTRUST);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    mOrderAdapter.setOrderType(Order.TYPE_HIS_ENTRUST);
                }
                mPage = 0;
                requestOrderList();
            }
        });
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHistoryFilter.isShowing()) {
                    mHistoryFilter.dismiss();
                } else {
                    mHistoryFilter.show();
                }
            }
        });
    }

    private void initFilterView() {
        mHistoryFilter = new HistoryFilter(mFilterView, mDimView);
        mHistoryFilter.setOnFilterListener(new HistoryFilter.OnFilterListener() {
            @Override
            public void onFilter(HistoryFilter.FilterResult filterResult) {
                mPage = 0;
                requestOrderList();
                mHistoryFilter.dismiss();
            }
        });
        mHistoryFilter.setOnCurrencySelectorShowListener(new HistoryFilter.OnCurrencySelectorShowListener() {
            @Override
            public void onCurrencySelectorShow() {
                if (mKeyboardVisible) {
                    KeyBoardUtils.closeKeyboard(mFilterView);
                }
            }
        });
    }

    private void requestRevokeOrder(Order order) {
        Apic.revokeOrder(order.getId()).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        //requestOrderList();
                    }
                }).fire();
    }

    private void requestOrderList() {
        if (mHistoryFilter.getFilterResult() != null) {
            HistoryFilter.FilterResult filterResult = mHistoryFilter.getFilterResult();
            requestOrderList(filterResult.getBaseCurrency(), filterResult.getCounterCurrency(), filterResult.getTradeDirection());
        }
    }

    private void requestOrderList(String prefixSymbol, String suffixSymbol, Integer direction) {
        int type = mRadioHeader.getSelectedPosition() == 0 ? Order.TYPE_CUR_ENTRUST : Order.TYPE_HIS_ENTRUST;
        String endTime = Uri.encode(DateUtil.format(SysTime.getSysTime().getSystemTimestamp()));
        Apic.getEntrustOrderList(mPage, type, endTime, prefixSymbol, suffixSymbol, direction).tag(TAG)
                .id(mRadioHeader.getSelectTab())
                .callback(new Callback4Resp<Resp<PagingWrap<Order>>, PagingWrap<Order>>() {
                    @Override
                    protected void onRespData(PagingWrap<Order> data) {
                        if (getId().equals(mRadioHeader.getSelectTab())) {
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

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopFreshOrLoadAnimation();
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
                if (order.getStatus() == OrderStatus.REVOKED
                        || order.getStatus() == OrderStatus.REVOKING
                        || order.getStatus() == OrderStatus.SYSTEM_REVOKED) {
                    color = ContextCompat.getColor(context, R.color.text49);
                }
                mTradePair.setText(tradeDir + " " + CurrencyUtils.formatPairName(order.getPairs()));
                mTradePair.setTextColor(color);
                mTime.setText(DateUtil.format(order.getOrderTime(), "HH:mm MM/dd"));
                if (order.getEntrustType() == Order.LIMIT_TRADE) {
                    mEntrustPrice.setText(order.getEntrustPrice());
                } else {
                    mEntrustPrice.setText(R.string.market_price);
                }
                mEntrustVolume.setText(order.getEntrustCount());
                mOrderStatus.setText(getStatusTextRes(order.getStatus()));
                mEntrustPriceTitle.setText(context.getString(R.string.entrust_price_x, order.getSuffix().toUpperCase()));
                mEntrustVolumeTitle.setText(context.getString(R.string.entrust_volume_x, order.getPrefix().toUpperCase()));
                mDealTotalAmt.setText(CurrencyUtils.getAmt(order.getDealCountDouble() * order.getDealPriceDouble()));
                mDealAveragePrice.setText(order.getDealPrice());
                mDealVolume.setText(order.getDealCount());
                mDealTotalAmtTitle.setText(context.getString(R.string.deal_total_amt_x, order.getSuffix().toUpperCase()));
                mDealAveragePriceTitle.setText(context.getString(R.string.deal_average_price_x, order.getSuffix().toUpperCase()));
                mDealVolumeTitle.setText(context.getString(R.string.deal_volume_x, order.getPrefix().toUpperCase()));
            }

            private int getStatusTextRes(int status) {
                switch (status) {
                    case OrderStatus.PENDING_DEAL:
                        return R.string.not_deal;
                    case OrderStatus.REVOKING:
                        return R.string.revoking;
                    case OrderStatus.REVOKED:
                        return R.string.revoked;
                    case OrderStatus.ALL_DEAL:
                        return R.string.deal_with_arrow;
                    case OrderStatus.PART_DEAL:
                        return R.string.part_deal_with_arrow;
                    case OrderStatus.SYSTEM_REVOKED:
                        return R.string.system_revoked;
                    case OrderStatus.PART_DEAL_PART_REVOKED:
                        return R.string.part_deal_part_revoked_with_arrow;
                    default:
                        return R.string.unknown_status;
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
                mTradePair.setText(tradeDir + " " + CurrencyUtils.formatPairName(order.getPairs()));
                mTradePair.setTextColor(color);
                mTime.setText(DateUtil.format(order.getOrderTime(), "HH:mm MM/dd"));
                if (order.getEntrustType() == Order.LIMIT_TRADE) {
                    mEntrustPrice.setText(order.getEntrustPrice());
                } else {
                    mEntrustPrice.setText(R.string.market_price);
                }
                mEntrustVolume.setText(order.getEntrustCount());
                mActualDealVolume.setText(order.getDealCount());
                mRevoke.setText(getStatusTextRes(order.getStatus()));
                if (order.getStatus() == OrderStatus.PENDING_DEAL) {
                    mRevoke.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onOrderRevokeClickListener != null) {
                                onOrderRevokeClickListener.onOrderRevoke(order);
                            }
                        }
                    });
                } else {
                    mRevoke.setOnClickListener(null);
                }

                mEntrustPriceTitle.setText(context.getString(R.string.entrust_price_x, order.getSuffix().toUpperCase()));
                mEntrustVolumeTitle.setText(context.getString(R.string.entrust_volume_x, order.getPrefix().toUpperCase()));
                mActualDealVolumeTitle.setText(context.getString(R.string.actual_deal_x, order.getPrefix().toUpperCase()));
            }

            private int getStatusTextRes(int status) {
                switch (status) {
                    case OrderStatus.PENDING_DEAL:
                        return R.string.revoke_order;
                    case OrderStatus.REVOKING:
                        return R.string.revoking;
                    default:
                        return R.string.unknown_status;
                }
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
