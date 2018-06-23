package com.songbai.futurex.fragment.legalcurrency;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.OtcWarePoster;
import com.songbai.futurex.model.local.OtcWaresAdd;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.PayTypeController;
import com.songbai.futurex.view.dialog.PriceTypeController;
import com.songbai.futurex.view.dialog.TradeLimitController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    EditText mPremiumRate;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.tradeLimit)
    TextView mTradeLimit;
    @BindView(R.id.payCurrencySymbol)
    TextView mPayCurrencySymbol;
    @BindView(R.id.tradeAmount)
    EditText mTradeAmount;
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
    @BindView(R.id.priceText)
    TextView mPriceText;
    @BindView(R.id.priceSymbol)
    TextView mPriceSymbol;
    private Unbinder mBind;
    private OtcWaresAdd mOtcWaresAdd;
    private String mLegalPaySymbol;
    private String mCionSymbol;

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
        mOtcWaresAdd = new OtcWaresAdd();
        mLegalPaySymbol = "CNY";
        mCionSymbol = "USDT";
        mPayCurrencySymbol.setText(mLegalPaySymbol);
        mCoinSymbol.setText(mCionSymbol);
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

    @OnClick({R.id.buyIn, R.id.sellOut, R.id.priceType, R.id.tradeLimit, R.id.payType, R.id.remark, R.id.buyer_limit, R.id.areaCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buyIn:
                break;
            case R.id.sellOut:
                break;
            case R.id.priceType:
                showPriceTypeSelector();
                break;
            case R.id.tradeLimit:
                showTradeLimitSelector();
                break;
            case R.id.payType:
                showPayTypeSelector();
                break;
            case R.id.remark:
                break;
            case R.id.buyer_limit:
                break;
            case R.id.areaCode:
                break;
            default:
        }
    }

    private void showPayTypeSelector() {
        PayTypeController payTypeController = new PayTypeController(getContext());
        payTypeController.setOnItemClickListener(new PayTypeController.OnItemClickListener() {
            @Override
            public void onConfirmClick() {

            }
        });
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(payTypeController)
                .show();
    }

    private void showTradeLimitSelector() {
        TradeLimitController tradeLimitController = new TradeLimitController(getContext());
        tradeLimitController.setOnItemClickListener(new TradeLimitController.OnItemClickListener() {
            @Override
            public void onConfirmClick(int minTurnover, int maxTurnover) {
                setTradeLimit(minTurnover, maxTurnover);
            }
        });
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(tradeLimitController)
                .show();
    }

    private void setTradeLimit(int minTurnover, int maxTurnover) {
        mTradeLimit.setText(getString(R.string.limit_range_x, String.valueOf(minTurnover), String.valueOf(maxTurnover)));
//        mOtcWaresAdd.setMinTurnover(minTurnover);
//        mOtcWaresAdd.setMaxTurnover(maxTurnover);
    }

    private void showPriceTypeSelector() {
        PriceTypeController priceTypeController = new PriceTypeController(getContext());
        priceTypeController.setOnItemClickListener(new PriceTypeController.OnItemClickListener() {
            @Override
            public void onItemClick(int type) {
                setPriceType(type);
            }
        });
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(priceTypeController)
                .show();
    }

    private void setPriceType(int type) {
        switch (type) {
            case OtcWarePoster.FIXED_PRICE:
                mPriceType.setText(R.string.fixed_price);
                mPriceText.setText(R.string.price);
                mPriceSymbol.setText("USDT");
                mOtcWaresAdd.setPriceType(OtcWarePoster.FIXED_PRICE);
                mPrice.setVisibility(View.GONE);
                break;
            case OtcWarePoster.FLOATING_PRICE:
                mPriceType.setText(R.string.floating_price);
                mPriceText.setText(R.string.floating_price_rate);
                mPriceSymbol.setText(R.string.percent_symbol);
                mOtcWaresAdd.setPriceType(OtcWarePoster.FLOATING_PRICE);
                mPrice.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }
}
