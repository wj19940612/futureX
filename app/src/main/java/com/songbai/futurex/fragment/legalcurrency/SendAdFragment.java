package com.songbai.futurex.fragment.legalcurrency;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.OtcWaresAdd;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/6/22
 */
public class SendAdFragment extends UniqueActivity.UniFragment {

    @BindView(R.id.buyIn)
    TextView mBuyIn;
    @BindView(R.id.sellOut)
    TextView mSellOut;
    @BindView(R.id.priceType)
    TextView mPriceType;
    @BindView(R.id.premiumRate)
    TextView mPremiumRate;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.tradeLimit)
    TextView mTradeLimit;
    @BindView(R.id.payCurrencySymbol)
    TextView mPayCurrencySymbol;
    @BindView(R.id.tradeAmount)
    TextView mTradeAmount;
    @BindView(R.id.coinSymbol)
    TextView mCoinSymbol;
    @BindView(R.id.payType)
    TextView mPayType;
    @BindView(R.id.remark)
    TextView mRemark;
    @BindView(R.id.buyer_limit)
    TextView mBuyerLimit;
    @BindView(R.id.authenticationText)
    TextView mAuthenticationText;
    @BindView(R.id.primaryCertification)
    TextView mPrimaryCertification;
    @BindView(R.id.seniorCertification)
    TextView mSeniorCertification;
    @BindView(R.id.hintText)
    TextView mHintText;
    @BindView(R.id.tradeCountLimit)
    TextView mTradeCountLimit;
    @BindView(R.id.buyerLimitGroup)
    ConstraintLayout mBuyerLimitGroup;
    @BindView(R.id.areaCode)
    TextView mAreaCode;
    @BindView(R.id.phone)
    TextView mPhone;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_ad, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
    }

    private void otcWaresAdd(OtcWaresAdd otcWaresAdd) {
        Apic.otcWaresAdd(otcWaresAdd)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {

                    }
                }).fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }
}
