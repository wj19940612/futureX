package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.songbai.futurex.model.mine.CoinPropertyFlow;
import com.songbai.futurex.model.status.CurrencyFlowType;
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
    private int mId;

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
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        getPropertyFlowDetail(mId);
    }

    private void getPropertyFlowDetail(int id) {
        Apic.getUserFinanceFlowDetail(id).tag(TAG)
                .callback(new Callback<Resp<CoinPropertyFlow>>() {
                    @Override
                    protected void onRespSuccess(Resp<CoinPropertyFlow> resp) {
                        setView(resp.getData());
                    }
                }).fire();
    }

    private void setView(CoinPropertyFlow data) {
        if (data == null) {
            return;
        }
        int flowType = data.getFlowType();
        switch (flowType) {
            case CurrencyFlowType.DRAW:
                mFlowType.setText(R.string.withdraw_cash);
                break;
            case CurrencyFlowType.DEPOSITE:
                mFlowType.setText(R.string.recharge_coin);
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
                mFlowType.setText(R.string.coin_account_in);
                break;
            default:
        }
        mAddress.setText("");
        mTimestamp.setText(DateUtil.format(data.getCreateTime(), DateUtil.FORMAT_HOUR_MINUTE_SECOND_DATE_YEAR));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
