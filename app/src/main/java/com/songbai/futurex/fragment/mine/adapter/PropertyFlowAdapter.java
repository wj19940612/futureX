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
import com.songbai.futurex.model.status.CurrencyFlowStatus;
import com.songbai.futurex.model.status.CurrencyFlowType;
import com.songbai.futurex.model.status.OTCFlowStatus;
import com.songbai.futurex.model.status.OTCFlowType;
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
    private int mAccount;

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

    public void setAccount(int account) {
        mAccount = account;
    }

    public interface OnClickListener {
        void onItemClick(int id);
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

        void bindData(final CoinPropertyFlow coinPropertyFlow) {
            switch (mAccount) {
                case 0:
                    bindCurrencyFlowData(coinPropertyFlow);
                    break;
                case 1:
                    bindOTCFlowData(coinPropertyFlow);
                    break;
                case 2:
                    break;
                default:
            }
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onItemClick(coinPropertyFlow.getId());
                    }
                }
            });
        }

        private void bindCurrencyFlowData(CoinPropertyFlow coinPropertyFlow) {
            int flowType = coinPropertyFlow.getFlowType();
            switch (flowType) {
                case CurrencyFlowType.DRAW:
                    mType.setText(R.string.withdraw_cash);
                    break;
                case CurrencyFlowType.DEPOSITE:
                    mType.setText(R.string.recharge_coin);
                    break;
                case CurrencyFlowType.ENTRUST_BUY:
                    mType.setText(R.string.buy_order);
                    break;
                case CurrencyFlowType.ENTRUST_SELL:
                    mType.setText(R.string.sell_order);
                    break;
                case CurrencyFlowType.OTC_TRADE_OUT:
                    mType.setText(R.string.otc_transfer_out);
                    break;
                case CurrencyFlowType.DRAW_FEE:
                    mType.setText(R.string.withdraw_fee);
                    break;
                case CurrencyFlowType.TRADE_FEE:
                    mType.setText(R.string.deal_fee);
                    break;
                case CurrencyFlowType.PROMOTER_TO:
                    mType.setText(R.string.promoter_account_transfer_into);
                    break;
                case CurrencyFlowType.OTC_TRADE_IN:
                    mType.setText(R.string.otc_trade_in);
                    break;
                case CurrencyFlowType.AGENCY_TO:
                    mType.setText(R.string.agency_to);
                    break;
                case CurrencyFlowType.LEGAL_ACCOUNT_IN:
                    mType.setText(R.string.legal_account_in);
                    break;
                case CurrencyFlowType.COIN_ACCOUNT_OUT:
                    mType.setText(R.string.coin_account_in);
                    break;
                default:
            }
            mAmount.setText(String.valueOf(coinPropertyFlow.getValue()));
            int status = coinPropertyFlow.getStatus();
            switch (status) {
                case CurrencyFlowStatus.SUCCESS:
                    mStatus.setText(R.string.completed);
                    break;
                case CurrencyFlowStatus.FREEZE:
                    mStatus.setText(R.string.freeze);
                    break;
                case CurrencyFlowStatus.DRAW_REJECT:
                    mStatus.setText(R.string.withdraw_coin_rejected);
                    break;
                case CurrencyFlowStatus.ENTRUS_RETURN:
                    mStatus.setText(R.string.entrust_return);
                    break;
                case CurrencyFlowStatus.FREEZE_DEDUCT:
                    mStatus.setText(R.string.freeze_deduct);
                    break;
                case CurrencyFlowStatus.ENTRUSE_RETURN_SYS:
                    mStatus.setText(R.string.sys_withdraw);
                    break;
                case CurrencyFlowStatus.FREEZE_RETURN:
                    mStatus.setText(R.string.freeze_return);
                    break;
                default:
            }
            mTimestamp.setText(DateUtil.format(coinPropertyFlow.getCreateTime(), "HH:mm MM/dd"));
        }

        private void bindOTCFlowData(CoinPropertyFlow coinPropertyFlow) {
            int flowType = coinPropertyFlow.getFlowType();
            switch (flowType) {
                case OTCFlowType.COIN_ACCOUNT_IN:
                    mType.setText(R.string.coin_account_in);
                    break;
                case OTCFlowType.LEGAL_CURRENCY_ACCOUNT_OUT:
                    mType.setText(R.string.legal_account_out);
                    break;
                case OTCFlowType.OTC_TRADE_IN:
                    mType.setText(R.string.otc_trade_in);
                    break;
                case OTCFlowType.OTC_TRADE_OUT:
                    mType.setText(R.string.otc_trade_out);
                    break;
                default:
            }
            mAmount.setText(String.valueOf(coinPropertyFlow.getValue()));
            int status = coinPropertyFlow.getStatus();
            switch (status) {
                case OTCFlowStatus.SUCCESS:
                    mStatus.setText(R.string.completed);
                    break;
                case OTCFlowStatus.FREEZE:
                    mStatus.setText(R.string.freeze);
                    break;
                case OTCFlowStatus.FREEZE_DEDUCT:
                    mStatus.setText(R.string.freeze_deduct);
                    break;
                case OTCFlowStatus.CANCEL_TRADE_FREEZE:
                    mStatus.setText(R.string.cancel_trade_freeze);
                    break;
                case OTCFlowStatus.SYS_CANCEL_TRADE_FREEZE:
                    mStatus.setText(R.string.sys_cancel_trade_freeze);
                    break;
                case OTCFlowStatus.POSTER_OFF_SHELF_RETURN:
                    mStatus.setText(R.string.poster_off_shelf_return);
                    break;
                default:
            }
            mTimestamp.setText(DateUtil.format(coinPropertyFlow.getCreateTime(), "HH:mm MM/dd"));
        }
    }
}

