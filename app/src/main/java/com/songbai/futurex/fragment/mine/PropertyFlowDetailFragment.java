package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.PropertyFlowDetail;
import com.songbai.futurex.model.status.CurrencyFlowStatus;
import com.songbai.futurex.model.status.CurrencyFlowType;
import com.songbai.futurex.model.status.OTCFlowStatus;
import com.songbai.futurex.model.status.OTCFlowType;
import com.songbai.futurex.utils.DateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public class PropertyFlowDetailFragment extends UniqueActivity.UniFragment {

    @BindView(R.id.flowType)
    TextView mFlowType;
    @BindView(R.id.address)
    TextView mAddress;
    @BindView(R.id.fee)
    TextView mFee;
    @BindView(R.id.timestamp)
    TextView mTimestamp;
    Unbinder unbinder;
    @BindView(R.id.addressGroup)
    LinearLayout mAddressGroup;
    @BindView(R.id.confirmAmount)
    TextView mConfirmAmount;
    @BindView(R.id.volumeGroup)
    LinearLayout mVolumeGroup;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.feeGroup)
    LinearLayout mFeeGroup;
    @BindView(R.id.amount)
    TextView mAmount;
    private int mId;
    private int mAccountType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_property_flow_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mId = extras.getInt(ExtraKeys.PROPERTY_FLOW_ID);
        mAccountType = extras.getInt(ExtraKeys.PROPERTY_FLOW_ACCOUNT_TYPE, 0);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        getPropertyFlowDetail(mId);
    }

    private void getPropertyFlowDetail(int id) {
        if (mAccountType == 0) {
            Apic.getUserFinanceFlowDetail(id).tag(TAG)
                    .callback(new Callback<Resp<PropertyFlowDetail>>() {
                        @Override
                        protected void onRespSuccess(Resp<PropertyFlowDetail> resp) {
                            setView(resp.getData());
                        }
                    }).fire();
        } else {
            Apic.getUserOtcFinanceFlow(id).tag(TAG)
                    .callback(new Callback<Resp<PropertyFlowDetail>>() {
                        @Override
                        protected void onRespSuccess(Resp<PropertyFlowDetail> resp) {
                            setView(resp.getData());
                        }
                    }).fire();
        }
    }

    private void setView(PropertyFlowDetail data) {
        if (data == null) {
            return;
        }
        mAmount.setText(getString(R.string.x_space_x, data.getValueStr(), data.getCoinType().toUpperCase()));
        mAddress.setText("");
        mAddressGroup.setVisibility(View.GONE);
        mFeeGroup.setVisibility(View.GONE);
        mVolumeGroup.setVisibility(View.GONE);
        switch (mAccountType) {
            case 0:
                setFlowTypeAndStatus(data);
                break;
            case 1:
                setOtcTypeAndStatus(data);
                break;
            default:
        }
        mTimestamp.setText(DateUtil.format(data.getCreateTime(), DateUtil.FORMAT_HOUR_MINUTE_SECOND_DATE_YEAR));
    }

    private void setFlowTypeAndStatus(PropertyFlowDetail data) {
        int flowType = data.getFlowType();
        String fee = data.getFee();
        switch (flowType) {
            case CurrencyFlowType.DRAW:
                mFlowType.setText(R.string.withdraw_cash);
                mAddress.setText(data.getToAddr());
                mFee.setText(getString(R.string.x_space_x, fee, data.getCoinType().toUpperCase()));
                mConfirmAmount.setText(data.getConfirm_num());
                mAddressGroup.setVisibility(View.VISIBLE);
                mFeeGroup.setVisibility(TextUtils.isEmpty(fee) ? View.GONE : View.VISIBLE);
                mVolumeGroup.setVisibility(View.VISIBLE);
                break;
            case CurrencyFlowType.DEPOSITE:
                mAddress.setText(data.getToAddr());
                mFlowType.setText(R.string.recharge_coin);
                mAddress.setText(data.getToAddr());
                mFee.setText(getString(R.string.x_space_x, fee, data.getCoinType().toUpperCase()));
                mConfirmAmount.setText(data.getConfirm_num());
                mAddressGroup.setVisibility(View.VISIBLE);
                mFeeGroup.setVisibility(TextUtils.isEmpty(fee) ? View.GONE : View.VISIBLE);
                mVolumeGroup.setVisibility(View.VISIBLE);
                break;
            case CurrencyFlowType.ENTRUST_BUY:
                mFlowType.setText(R.string.buy_order);
                break;
            case CurrencyFlowType.ENTRUST_SELL:
                mFlowType.setText(R.string.sell_order);
                break;
            case CurrencyFlowType.OTC_TRADE_OUT:
                mFlowType.setText(R.string.otc_transfer_out);
                break;
            case CurrencyFlowType.DRAW_FEE:
                mFlowType.setText(R.string.withdraw_fee);
                break;
            case CurrencyFlowType.TRADE_FEE:
                mFlowType.setText(R.string.deal_fee);
                break;
            case CurrencyFlowType.PROMOTER_TO:
                mFlowType.setText(R.string.promoter_account_transfer_into);
                break;
            case CurrencyFlowType.OTC_TRADE_IN:
                mFlowType.setText(R.string.otc_trade_in);
                break;
            case CurrencyFlowType.AGENCY_TO:
                mFlowType.setText(R.string.agency_to);
                break;
            case CurrencyFlowType.LEGAL_ACCOUNT_IN:
                mFlowType.setText(R.string.legal_account_in);
                break;
            case CurrencyFlowType.COIN_ACCOUNT_OUT:
                mFlowType.setText(R.string.coin_account_out);
                break;
            case CurrencyFlowType.PERIODIC_RELEASE:
                mFlowType.setText(R.string.periodic_release);
                break;
            case CurrencyFlowType.SPECIAL_TRADE:
                mFlowType.setText(R.string.special_trade);
                break;
            case CurrencyFlowType.CASHBACK:
                mFlowType.setText(R.string.cashback);
                break;
            case CurrencyFlowType.INVT_REWARD:
                mFlowType.setText(R.string.invt_reward);
                break;
            case CurrencyFlowType.DISTRIBUTED_REV:
                mFlowType.setText(R.string.distributed_rev);
                break;
            case CurrencyFlowType.RELEASED_BFB:
                mFlowType.setText(R.string.release_bfb);
                break;
            case CurrencyFlowType.MINERS_REWAR:
                mFlowType.setText(R.string.miners_rewar);
                break;
            case CurrencyFlowType.SHARED_FEE:
                mFlowType.setText(R.string.shared_fee);
                break;
            case CurrencyFlowType.SUBSCRIPTION:
                mFlowType.setText(R.string.subscription);
                break;
            default:
                mFlowType.setText(R.string.others);
                break;
        }
        int status = data.getStatus();
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

    private void setOtcTypeAndStatus(PropertyFlowDetail data) {
        int flowType = data.getFlowType();
        switch (flowType) {
            case OTCFlowType.COIN_ACCOUNT_IN:
                mFlowType.setText(R.string.coin_account_in);
                break;
            case OTCFlowType.LEGAL_CURRENCY_ACCOUNT_OUT:
                mFlowType.setText(R.string.legal_account_out);
                break;
            case OTCFlowType.OTC_TRADE_IN:
                mFlowType.setText(R.string.otc_trade_in);
                break;
            case OTCFlowType.OTC_TRADE_OUT:
                mFlowType.setText(R.string.otc_trade_out);
                break;
            default:
                mFlowType.setText(R.string.others);
        }
        int status = data.getStatus();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
