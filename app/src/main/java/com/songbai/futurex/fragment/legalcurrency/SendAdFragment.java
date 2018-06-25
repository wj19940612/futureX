package com.songbai.futurex.fragment.legalcurrency;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.OtcWarePoster;
import com.songbai.futurex.model.local.WaresModel;
import com.songbai.futurex.model.status.PayType;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.BuyLimitController;
import com.songbai.futurex.view.dialog.PayTypeController;
import com.songbai.futurex.view.dialog.PosterPreviewController;
import com.songbai.futurex.view.dialog.PriceTypeController;
import com.songbai.futurex.view.dialog.RemarkInputController;
import com.songbai.futurex.view.dialog.TradeLimitController;

import java.util.HashMap;

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
    @BindView(R.id.buyerLimit)
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
    @BindView(R.id.buyerAuthLimitGroup)
    LinearLayout mBuyerAuthLimitGroup;
    @BindView(R.id.buyerCountLimitGroup)
    LinearLayout mBuyerCountLimitGroup;
    @BindView(R.id.areaCode)
    TextView mAreaCode;
    @BindView(R.id.phone)
    TextView mPhone;
    @BindView(R.id.priceText)
    TextView mPriceText;
    @BindView(R.id.priceSymbol)
    TextView mPriceSymbol;
    private Unbinder mBind;
    private WaresModel mWaresModel;
    private String mLegalPaySymbol;
    private String mCionSymbol;
    private HashMap<String, String> buyCondition = new HashMap<>();
    private String mPosterId;
    private OtcWarePoster mOtcWarePoster;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_ad, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mOtcWarePoster = (OtcWarePoster) extras.get(ExtraKeys.OTC_WARE_POSTER);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mWaresModel = new WaresModel();
        if (mOtcWarePoster != null) {
            Log.e(TAG, "restoreData1: " + new Gson().toJson(mOtcWarePoster));

            mLegalPaySymbol = mOtcWarePoster.getPayCurrency();
            mCionSymbol = mOtcWarePoster.getCoinSymbol();
            mWaresModel.setDealType(mOtcWarePoster.getDealType());
            mWaresModel.setPriceType(mOtcWarePoster.getPriceType());
            mWaresModel.setCoinSymbol(mCionSymbol);
            mWaresModel.setPayCurrency(mLegalPaySymbol);
            switch (mOtcWarePoster.getPriceType()) {
                case OtcWarePoster.FIXED_PRICE:
                    mWaresModel.setFixedPrice(mOtcWarePoster.getFixedPrice());
                    break;
                case OtcWarePoster.FLOATING_PRICE:
                    mWaresModel.setPercent(String.valueOf(mOtcWarePoster.getPercent()));
                    break;
                default:
            }
            mWaresModel.setPayInfo(mOtcWarePoster.getPayInfo());
            mWaresModel.setFixedPrice(mOtcWarePoster.getFixedPrice());
            mWaresModel.setRemark(mOtcWarePoster.getRemark());
            mWaresModel.setTradeCount(mOtcWarePoster.getTradeCount());
            mWaresModel.setPayIds(mOtcWarePoster.getPayIds());
            mWaresModel.setMinTurnover(mOtcWarePoster.getMinTurnover());
            mWaresModel.setMaxTurnover(mOtcWarePoster.getMaxTurnover());
            mWaresModel.setTradeCount(mOtcWarePoster.getTradeCount());
            restoreData(mWaresModel);
        } else {
            mLegalPaySymbol = "cny";
            mCionSymbol = "usdt";
            mPayCurrencySymbol.setText(mLegalPaySymbol.toUpperCase());
            mCoinSymbol.setText(mCionSymbol.toUpperCase());
            setDealType(true);
            mWaresModel.setCoinSymbol(mCionSymbol);
            mWaresModel.setPayCurrency(mLegalPaySymbol);
            mWaresModel.setConditionType("");
            mWaresModel.setConditionValue("");
        }
        mBuyIn.setText(getString(R.string.buy_symbol_x, mCionSymbol.toUpperCase()));
        mSellOut.setText(getString(R.string.sell_symbol_x, mCionSymbol.toUpperCase()));
    }

    private void restoreData(WaresModel waresModel) {
        Log.e(TAG, "restoreData: " + new Gson().toJson(waresModel));
        mPayCurrencySymbol.setText(waresModel.getPayCurrency().toUpperCase());
        mCoinSymbol.setText(waresModel.getCoinSymbol().toUpperCase());
        switch (waresModel.getDealType()) {
            case OtcWarePoster.DEAL_TYPE_BUY:
                setDealType(true);
                break;
            case OtcWarePoster.DEAL_TYPE_SELL:
                setDealType(false);
                break;
            default:
        }
        mBuyIn.setText(getString(R.string.buy_symbol_x, mCionSymbol.toUpperCase()));
        mSellOut.setText(getString(R.string.sell_symbol_x, mCionSymbol.toUpperCase()));
        setPriceType(waresModel.getPriceType());
        switch (waresModel.getPriceType()) {
            case OtcWarePoster.FIXED_PRICE:
                mPremiumRate.setText(String.valueOf(waresModel.getFixedPrice()));
                break;
            case OtcWarePoster.FLOATING_PRICE:
                mPremiumRate.setText(String.valueOf(waresModel.getPercent()));
                break;
            default:
        }
        mPremiumRate.setSelection(mPremiumRate.getText().length());
        setTradeLimit(waresModel.getMinTurnover(), waresModel.getMaxTurnover());
        mTradeAmount.setText(String.valueOf(waresModel.getTradeCount()));
        mTradeAmount.setSelection(mTradeAmount.getText().length());
        mRemark.setText(waresModel.getRemark());
        setPayInfo(waresModel.getPayInfo());
        String conditionType = mWaresModel.getConditionType();
        setConditionType(conditionType);
    }

    private void setDealType(boolean buy) {
        mBuyIn.setSelected(buy);
        mSellOut.setSelected(!buy);
        mWaresModel.setDealType(buy ? OtcWarePoster.DEAL_TYPE_BUY : OtcWarePoster.DEAL_TYPE_SELL);
    }

    private void otcWaresAdd(WaresModel otcWaresAdd) {
        Apic.otcWaresAdd(otcWaresAdd)
                .callback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        mPosterId = resp.getData();
                        showPreview(mPosterId);
                    }
                }).fire();
    }

    private void otcWaresUpadate(WaresModel otcWaresAdd) {
        Apic.otcWaresAdd(otcWaresAdd)
                .callback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        mPosterId = resp.getData();
                        showPreview(mPosterId);
                    }
                }).fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.preview, R.id.buyIn, R.id.sellOut, R.id.priceType, R.id.tradeLimit, R.id.payType,
            R.id.remark, R.id.buyerLimit, R.id.primaryCertification, R.id.seniorCertification, R.id.areaCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.preview:
                mWaresModel.setTradeCount(Double.valueOf(mTradeAmount.getText().toString()));
                switch (mWaresModel.getPriceType()) {
                    case OtcWarePoster.FIXED_PRICE:
                        mWaresModel.setFixedPrice(Double.valueOf(mPremiumRate.getText().toString()));
                        break;
                    case OtcWarePoster.FLOATING_PRICE:
                        mWaresModel.setPercent(mPremiumRate.getText().toString());
                        break;
                    default:
                }
                if (TextUtils.isEmpty(mPosterId)) {
                    otcWaresAdd(mWaresModel);
                } else {
                    mWaresModel.setId(mPosterId);
                    otcWaresUpadate(mWaresModel);
                }
                break;
            case R.id.buyIn:
                setDealType(true);
                break;
            case R.id.sellOut:
                setDealType(false);
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
                showRemarkInput();
                break;
            case R.id.buyerLimit:
                showBuyLimitSelector();
                break;
            case R.id.primaryCertification:
                mPrimaryCertification.setSelected(mPrimaryCertification.isSelected());
                break;
            case R.id.seniorCertification:
                mSeniorCertification.setSelected(mSeniorCertification.isSelected());
                break;
            case R.id.areaCode:
                showAreaCodeSelector();
                break;
            default:
        }
    }

    private void showBuyLimitSelector() {
        BuyLimitController payTypeController = new BuyLimitController(getContext());
        payTypeController.setOnItemClickListener(new BuyLimitController.OnItemClickListener() {
            @Override
            public void onConfirmClick(String conditionType) {
                setConditionType(conditionType);
            }
        });
        payTypeController.setConditionType(mWaresModel.getConditionType());
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(payTypeController)
                .show();
    }

    private void setConditionType(String conditionType) {
        if (TextUtils.isEmpty(conditionType)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (conditionType.contains(OtcWarePoster.CONDITION_AUTH)) {
            buyCondition.put(OtcWarePoster.CONDITION_AUTH, "");
            sb.append(getString(R.string.authentication));
            sb.append("、");
            mBuyerAuthLimitGroup.setVisibility(View.VISIBLE);
        } else {
            mBuyerAuthLimitGroup.setVisibility(View.GONE);
        }
        if (conditionType.contains(OtcWarePoster.CONDITION_TRADE)) {
            buyCondition.put(OtcWarePoster.CONDITION_TRADE, "");
            sb.append(getString(R.string.deal_success_count));
            sb.append("、");
            mBuyerCountLimitGroup.setVisibility(View.VISIBLE);
        } else {
            mBuyerCountLimitGroup.setVisibility(View.GONE);
        }
        if (sb.length() > 0) {
            mBuyerLimit.setText(sb.toString().substring(0, sb.length() - 1));
            mWaresModel.setConditionType(conditionType);
        }
    }

    private void showAreaCodeSelector() {

    }

    private void showPreview(String id) {
        PosterPreviewController posterPreviewController = new PosterPreviewController(getContext());
        posterPreviewController.setOnItemClickListener(new PosterPreviewController.OnItemClickListener() {
            @Override
            public void onConfirmClick() {
            }
        });
        posterPreviewController.setPosterId(id);
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(posterPreviewController)
                .show();
    }

    private void showRemarkInput() {
        RemarkInputController remarkInputController = new RemarkInputController(getContext());
        remarkInputController.setOnItemClickListener(new RemarkInputController.OnItemClickListener() {
            @Override
            public void onConfirmClick(String remark) {
                setRemark(remark);
            }
        });
        remarkInputController.restoreRemark(mWaresModel.getRemark());
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(remarkInputController)
                .show();
    }

    private void setRemark(String remark) {
        mRemark.setText(remark);
        mWaresModel.setRemark(remark);
    }

    private void showPayTypeSelector() {
        PayTypeController payTypeController = new PayTypeController(getContext());
        payTypeController.setOnItemClickListener(new PayTypeController.OnItemClickListener() {
            @Override
            public void onConfirmClick(String payInfo) {
                setPayInfo(payInfo);
            }
        });
        payTypeController.setPayInfo(mWaresModel.getPayInfo());
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(payTypeController)
                .show();
    }

    private void setPayInfo(String payType) {
        StringBuilder sb = new StringBuilder();
        if (payType.contains(PayType.ALIPAY)) {
            sb.append(getString(R.string.alipay));
            sb.append("、");
        }
        if (payType.contains(PayType.WXPAY)) {
            sb.append(getString(R.string.weichat));
            sb.append("、");
        }
        if (payType.contains(PayType.BANK_PAY)) {
            sb.append(getString(R.string.bank_card));
            sb.append("、");
        }
        if (sb.length() > 0) {
            mPayType.setText(sb.toString().substring(0, sb.length() - 1));
            mWaresModel.setPayInfo(payType);
        }
    }

    private void showTradeLimitSelector() {
        TradeLimitController tradeLimitController = new TradeLimitController(getContext());
        tradeLimitController.setOnItemClickListener(new TradeLimitController.OnItemClickListener() {
            @Override
            public void onConfirmClick(int minTurnover, int maxTurnover) {
                setTradeLimit(minTurnover, maxTurnover);
            }
        });
        tradeLimitController.restoreLimit(mWaresModel.getMinTurnover(), mWaresModel.getMaxTurnover());
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(tradeLimitController)
                .show();
    }

    private void setTradeLimit(double minTurnover, double maxTurnover) {
        mTradeLimit.setText(getString(R.string.limit_range_x,
                String.valueOf(minTurnover), String.valueOf(maxTurnover)));
        mWaresModel.setMinTurnover(minTurnover);
        mWaresModel.setMaxTurnover(maxTurnover);
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
                mPriceSymbol.setText(mCionSymbol);
                mWaresModel.setPriceType(OtcWarePoster.FIXED_PRICE);
                mPrice.setVisibility(View.GONE);
                break;
            case OtcWarePoster.FLOATING_PRICE:
                mPriceType.setText(R.string.floating_price);
                mPriceText.setText(R.string.floating_price_rate);
                mPriceSymbol.setText(R.string.percent_symbol);
                mWaresModel.setPriceType(OtcWarePoster.FLOATING_PRICE);
                mPrice.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }
}
