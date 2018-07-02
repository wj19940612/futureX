package com.songbai.futurex.fragment.mine.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.model.mine.CoinPropertyFlow;
import com.songbai.futurex.model.status.FlowStatus;
import com.songbai.futurex.model.status.FlowType;
import com.songbai.futurex.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public class PropertyFlowAdapter extends RecyclerView.Adapter {
    private List<CoinPropertyFlow> mList = new ArrayList<>();
    private OnClickListener mOnClickListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_coin_property, parent, false);
        return new PropertyFlowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PropertyFlowHolder) {
            ((PropertyFlowHolder) holder).bindData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(PagingWrap<CoinPropertyFlow> pagingResp) {
        if (pagingResp.getStart() == 0) {
            mList.clear();
        }
        mList.addAll(pagingResp.getData());
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onItemClick();
    }

    class PropertyFlowHolder extends RecyclerView.ViewHolder {
        private final View mRootView;
        @BindView(R.id.type)
        TextView mType;
        @BindView(R.id.amount)
        TextView mAmount;
        @BindView(R.id.status)
        TextView mStatus;
        @BindView(R.id.timestamp)
        TextView mTimestamp;

        PropertyFlowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mRootView = view;
        }

        void bindData(CoinPropertyFlow coinPropertyFlow) {

            int flowType = coinPropertyFlow.getFlowType();
            switch (flowType) {
                case FlowType.DRAW:
                    mType.setText(R.string.withdraw_cash);
                    break;
                case FlowType.DEPOSITE:
                    mType.setText(R.string.recharge_coin);
                    break;
                case FlowType.ENTRUST_BUY:
                    mType.setText(R.string.buy_order);
                    break;
                case FlowType.ENTRUST_SELL:
                    mType.setText(R.string.sell_order);
                    break;
                case FlowType.OTC_TRADE_OUT:
                    mType.setText(R.string.otc_transfer_out);
                    break;
                case FlowType.DRAW_FEE:
                    mType.setText(R.string.withdraw_fee);
                    break;
                case FlowType.TRADE_FEE:
                    mType.setText(R.string.deal_fee);
                    break;
                case FlowType.PROMOTER_TO:
                    mType.setText(R.string.promoter_account_transfer_into);
                    break;
                case FlowType.OTC_TRADE_IN:
                    mType.setText(R.string.otc_trade_in);
                    break;
                case FlowType.AGENCY_TO:
                    mType.setText(R.string.agency_to);
                    break;
                case FlowType.LEGAL_ACCOUNT_IN:
                    mType.setText(R.string.legal_account_in);
                    break;
                case FlowType.COIN_ACCOUNT_OUT:
                    mType.setText(R.string.coin_account_out);
                    break;
                default:
            }
            mAmount.setText(String.valueOf(coinPropertyFlow.getValue()));
            int status = coinPropertyFlow.getStatus();
            switch (status) {
                case FlowStatus.SUCCESS:
                    mStatus.setText(R.string.completed);
                    break;
                case FlowStatus.FREEZE:
                    mStatus.setText(R.string.freeze);
                    break;
                case FlowStatus.DRAW_REJECT:
                    mStatus.setText(R.string.withdraw_coin_rejected);
                    break;
                case FlowStatus.ENTRUS_RETURN:
                    mStatus.setText(R.string.entrust_return);
                    break;
                case FlowStatus.FREEZE_DEDUCT:
                    mStatus.setText(R.string.freeze_deduct);
                    break;
                case FlowStatus.ENTRUSE_RETURN_SYS:
                    mStatus.setText(R.string.sys_withdraw);
                    break;
                case FlowStatus.FREEZE_RETURN:
                    mStatus.setText(R.string.freeze_return);
                    break;
                default:
            }
            mTimestamp.setText(DateUtil.format(coinPropertyFlow.getCreateTime(), "HH:mm MM/dd"));

            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onItemClick();
                    }
                }
            });
        }
    }
}

