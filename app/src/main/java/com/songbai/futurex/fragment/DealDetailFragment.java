package com.songbai.futurex.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.order.DealDetail;
import com.songbai.futurex.model.order.Order;
import com.songbai.futurex.model.order.OrderStatus;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/7/9
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class DealDetailFragment extends UniqueActivity.UniFragment {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private Order mOrder;
    private DealDetailAdapter mDealDetailAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deal_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mOrder = extras.getParcelable(ExtraKeys.ORDER);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDealDetailAdapter = new DealDetailAdapter();
        mRecyclerView.setAdapter(mDealDetailAdapter);

        Apic.getOrderDealDetail(mOrder.getWid()).tag(TAG)
                .callback(new Callback<Resp<List<DealDetail>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<DealDetail>> resp) {
                        mDealDetailAdapter.setDealDetailList(resp.getData());
                    }
                }).fireFreely();
    }

    static class DealDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int HEADER = 0;

        private List<DealDetail> mDealDetailList;
        private Order mOrder;
        private Context mContext;

        public DealDetailAdapter() {
            mDealDetailList = new ArrayList<>();
        }

        public void setDealDetailList(List<DealDetail> dealDetailList) {
            mDealDetailList = dealDetailList;
            createOrder(dealDetailList);
            notifyDataSetChanged();
        }

        private void createOrder(List<DealDetail> dealDetailList) {
            if (dealDetailList == null || dealDetailList.size() == 0) {
                return;
            }

            DealDetail dealDetail = dealDetailList.get(0);
            mOrder = new Order();
            mOrder.setPairs(dealDetail.getPairs());
            mOrder.setDirection(dealDetail.getDirection());
            mOrder.setStatus(dealDetail.getStatus());
            mOrder.setDealPrice(dealDetail.getDealPrice());
            mOrder.setDealCount(dealDetail.getDealCount());
            mOrder.setPoundage(dealDetail.getPoundage());
        }

        static class HeaderViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tradePair)
            TextView mTradePair;
            @BindView(R.id.dealTotalAmtTitle)
            TextView mDealTotalAmtTitle;
            @BindView(R.id.dealAveragePriceTitle)
            TextView mDealAveragePriceTitle;
            @BindView(R.id.dealVolumeTitle)
            TextView mDealVolumeTitle;
            @BindView(R.id.dealTotalAmt)
            TextView mDealTotalAmt;
            @BindView(R.id.dealAveragePrice)
            TextView mDealAveragePrice;
            @BindView(R.id.dealVolume)
            TextView mDealVolume;
            @BindView(R.id.dealFeeTitle)
            TextView mDealFeeTitle;
            @BindView(R.id.dealFee)
            TextView mDealFee;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(Order order, Context context) {
                int color = ContextCompat.getColor(context, R.color.green);
                String tradeDir = context.getString(R.string.buy_in);
                String feeSign = order.getPrefix();
                if (order.getDirection() == Order.DIR_SELL) {
                    color = ContextCompat.getColor(context, R.color.red);
                    tradeDir = context.getString(R.string.sell_out);
                    feeSign = order.getSuffix();
                }
                if (order.getStatus() == OrderStatus.REVOKED) {
                    color = ContextCompat.getColor(context, R.color.text49);
                }
                mTradePair.setText(tradeDir + " " + CurrencyUtils.formatPairName(order.getPairs()));
                mTradePair.setTextColor(color);
                mDealTotalAmt.setText(CurrencyUtils.getAmt(order.getDealCountDouble() * order.getDealPriceDouble()));
                mDealAveragePrice.setText(order.getDealPrice());
                mDealVolume.setText(order.getDealCount());
                mDealTotalAmtTitle.setText(context.getString(R.string.deal_total_amt_x, order.getSuffix().toUpperCase()));
                mDealAveragePriceTitle.setText(context.getString(R.string.deal_average_price_x, order.getSuffix().toUpperCase()));
                mDealVolumeTitle.setText(context.getString(R.string.deal_volume_x, order.getPrefix().toUpperCase()));
                mDealFee.setText(order.getPoundage());
                mDealFeeTitle.setText(context.getString(R.string.deal_fee_x, feeSign.toUpperCase()));
            }
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.dealPrice)
            TextView mDealPrice;
            @BindView(R.id.dealVolume)
            TextView mDealVolume;
            @BindView(R.id.dealFee)
            TextView mDealFee;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(Order order, DealDetail dealDetail, Context context) {
                mTime.setText(context.getString(R.string.time_x, DateUtil.format(dealDetail.getDealTime(), "HH:mm MM/dd")));
                mDealPrice.setText(dealDetail.getDealPrice() + " " + order.getSuffix().toUpperCase());
                mDealVolume.setText(dealDetail.getDealCount() + " " + order.getPrefix().toUpperCase());
                if (order.getDirection() == Order.DIR_SELL) {
                    mDealFee.setText(dealDetail.getPoundage() + " " + order.getSuffix().toUpperCase());
                } else {
                    mDealFee.setText(dealDetail.getPoundage() + " " + order.getPrefix().toUpperCase());
                }
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            if (viewType == HEADER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_deal_detail_header, parent, false);
                return new HeaderViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_deal_detail, parent, false);
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderViewHolder) {
                ((HeaderViewHolder) holder).bind(mOrder, mContext);
            } else if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).bind(mOrder, mDealDetailList.get(position - 1), mContext);
            }
        }

        @Override
        public int getItemCount() {
            if (mOrder == null) {
                return 0;
            }
            return mDealDetailList.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEADER;
            }
            return position;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
