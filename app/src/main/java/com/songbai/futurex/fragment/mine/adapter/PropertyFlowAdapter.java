package com.songbai.futurex.fragment.mine.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.songbai.futurex.model.status.PromoterFlowType;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.FinanceUtil;

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
    private boolean mSingleType;

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

    public void setSingleType(boolean singleType) {
        mSingleType = singleType;
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
        @BindView(R.id.amountText)
        TextView mAmountText;
        @BindView(R.id.statusText)
        TextView mStatusText;
        @BindView(R.id.status)
        TextView mStatus;
        @BindView(R.id.timestamp)
        TextView mTimestamp;
        @BindView(R.id.coinTypeText)
        TextView mCoinTypeText;
        @BindView(R.id.coinType)
        TextView mCoinType;

        PropertyFlowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mRootView = view;
        }

        void bindData(final CoinPropertyFlow coinPropertyFlow) {
            mCoinType.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            mAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            mStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            mTimestamp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            mCoinType.setVisibility(mSingleType ? View.GONE : View.VISIBLE);
            mCoinTypeText.setVisibility(mSingleType ? View.GONE : View.VISIBLE);
            mAmount.setGravity(mSingleType ? Gravity.START : Gravity.END);
            mAmountText.setGravity(mSingleType ? Gravity.START : Gravity.END);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mAmountText.getLayoutParams();
            layoutParams.horizontalWeight = mSingleType ? 1 : 0.8f;
            mAmountText.setLayoutParams(layoutParams);
            mStatus.setGravity(mSingleType ? Gravity.CENTER : Gravity.END);
            mStatusText.setGravity(mSingleType ? Gravity.CENTER : Gravity.END);
            if (mSingleType) {
                ConstraintLayout.LayoutParams statusTextLayoutParams = (ConstraintLayout.LayoutParams) mStatusText.getLayoutParams();
                statusTextLayoutParams.leftMargin = 0;
                mStatusText.setLayoutParams(statusTextLayoutParams);
            }
            switch (mAccount) {
                case 0:
                    bindCurrencyFlowData(coinPropertyFlow);
                    break;
                case 1:
                    bindOTCFlowData(coinPropertyFlow);
                    break;
                case 2:
                    bindPromoterFlowData(coinPropertyFlow);
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
            mAmount.setText(FinanceUtil.subZeroAndDot(coinPropertyFlow.getValue(), 8));
            mTimestamp.setText(DateUtil.format(coinPropertyFlow.getCreateTime(), "HH:mm MM/dd"));
        }

        private void bindCurrencyFlowData(CoinPropertyFlow coinPropertyFlow) {
            mCoinType.setText(coinPropertyFlow.getCoinType().toUpperCase());
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
                    mType.setText(R.string.coin_account_out);
                    break;
                case CurrencyFlowType.PERIODIC_RELEASE:
                    mType.setText(R.string.periodic_release);
                    break;
                case CurrencyFlowType.SPECIAL_TRADE:
                    mType.setText(R.string.special_trade);
                    break;
                case CurrencyFlowType.CASHBACK:
                    mType.setText(R.string.cashback);
                    break;
                case CurrencyFlowType.INVT_REWARD:
                    mType.setText(R.string.invt_reward);
                    break;
                case CurrencyFlowType.DISTRIBUTED_REV:
                    mType.setText(R.string.distributed_rev);
                    break;
                case CurrencyFlowType.RELEASED_BFB:
                    mType.setText(R.string.release_bfb);
                    break;
                case CurrencyFlowType.MINERS_REWAR:
                    mType.setText(R.string.miners_rewar);
                    break;
                case CurrencyFlowType.SHARED_FEE:
                    mType.setText(R.string.shared_fee);
                    break;
                case CurrencyFlowType.SUBSCRIPTION:
                    mType.setText(R.string.subscription);
                    break;
                case CurrencyFlowType.EVENT_GRANT:
                    mType.setText(R.string.event_grant);
                    break;
                case CurrencyFlowType.EVENT_AWARD:
                    mType.setText(R.string.event_award);
                    break;
                default:
                    mType.setText(R.string.others);
                    break;
            }
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
                    mStatus.setText(R.string.others);
                    break;
            }
        }

        private void bindOTCFlowData(CoinPropertyFlow coinPropertyFlow) {
            mCoinType.setText(coinPropertyFlow.getCoinType().toUpperCase());
            int flowType = coinPropertyFlow.getFlowType();
            switch (flowType) {
                case OTCFlowType.TRADE_COMMISSION:
                    mType.setText(R.string.trade_commission);
                    break;
                case OTCFlowType.TRANSFER_TO_USER_FREEZE:
                    mType.setText(R.string.transfer_to_user_freeze);
                    break;
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
                    mType.setText(R.string.others);
            }
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
                    mStatus.setText(R.string.others);
            }
        }

        private void bindPromoterFlowData(CoinPropertyFlow coinPropertyFlow) {
            mCoinType.setText(coinPropertyFlow.getCoinType().toUpperCase());
            mStatusText.setText(R.string.type);
            int flowType = coinPropertyFlow.getFlowType();
            switch (flowType) {
                case PromoterFlowType.TRADE_REBATE:
                    mType.setText(R.string.trade_rebate);
                    mStatus.setText(R.string.trade_rebate);
                    break;
                case PromoterFlowType.TRANSFER_TO_PERSONAL_ACCOUNT:
                    mType.setText(R.string.transfer_to_personal_account);
                    mStatus.setText(R.string.transfer_to_personal_account);
                    break;
                default:
                    mType.setText(R.string.others);
                    mStatus.setText(R.string.others);
            }
        }
    }
}

