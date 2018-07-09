package com.songbai.futurex.fragment.legalcurrency;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.AreaCode;
import com.songbai.futurex.model.CountryCurrency;
import com.songbai.futurex.model.LegalCoin;
import com.songbai.futurex.model.OtcWarePoster;
import com.songbai.futurex.model.QuotaPrice;
import com.songbai.futurex.model.local.WaresModel;
import com.songbai.futurex.model.mine.BankCardBean;
import com.songbai.futurex.model.mine.BindBankList;
import com.songbai.futurex.model.status.PayType;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.StrFormatter;
import com.songbai.futurex.utils.StrUtil;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.utils.inputfilter.MoneyValueFilter;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.BuyLimitController;
import com.songbai.futurex.view.dialog.PayTypeController;
import com.songbai.futurex.view.dialog.PosterPreviewController;
import com.songbai.futurex.view.dialog.PriceTypeController;
import com.songbai.futurex.view.dialog.RemarkInputController;
import com.songbai.futurex.view.dialog.TradeLimitController;
import com.songbai.futurex.view.popup.WaresPairFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/6/22
 */
public class PublishPosterFragment extends UniqueActivity.UniFragment {
    public static final int PUBLISH_POSTER_RESULT = 12323;
    @BindView(R.id.buyIn)
    TextView mBuyIn;
    @BindView(R.id.sellOut)
    TextView mSellOut;
    @BindView(R.id.priceType)
    TextView mPriceType;
    @BindView(R.id.waresPairGroup)
    RelativeLayout mWaresPairGroup;
    @BindView(R.id.waresPair)
    TextView mWaresPair;
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
    EditText mTradeCountLimit;
    @BindView(R.id.buyerAuthLimitGroup)
    LinearLayout mBuyerAuthLimitGroup;
    @BindView(R.id.buyerCountLimitGroup)
    LinearLayout mBuyerCountLimitGroup;
    @BindView(R.id.areaCode)
    TextView mAreaCode;
    @BindView(R.id.phone)
    EditText mPhone;
    @BindView(R.id.priceText)
    TextView mPriceText;
    @BindView(R.id.priceSymbol)
    TextView mPriceSymbol;
    @BindView(R.id.confirmRulesGroup)
    LinearLayout mConfirmRulesGroup;
    @BindView(R.id.preview)
    TextView mPreview;
    @BindView(R.id.check)
    ImageView mCheck;
    @BindView(R.id.tvBalance)
    TextView mTvBalance;
    private Unbinder mBind;
    private WaresModel mWaresModel;
    private String mLegalPaySymbol = "";
    private String mLegalCoinSymbol = "";
    private HashMap<String, String> mConditionKeyValue = new HashMap<>();
    private int mPosterId;
    private OtcWarePoster mOtcWarePoster;
    private ArrayList<LegalCoin> mLegalCoins;
    private ArrayList<CountryCurrency> mCountryCurrencies;
    private double mQuotaPrice;
    private List<AreaCode> mAreaCodes;
    private OptionsPickerView mPvOptions;
    private String mBalance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish_poster, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mPosterId = extras.getInt(ExtraKeys.OTC_WARE_POSTER_ID);
        mLegalPaySymbol = extras.getString(ExtraKeys.SELECTED_CURRENCY_SYMBOL, "");
        mLegalCoinSymbol = extras.getString(ExtraKeys.SELECTED_LEGAL_COIN_SYMBOL, "");
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mWaresModel = new WaresModel();
        mConfirmRulesGroup.setSelected(false);
        mTradeCountLimit.setFilters(new InputFilter[]{new MoneyValueFilter().filterMax(1000)});
        restoreData();
        accountBalance();
        getLegalCoin();
        getCountryCurrency();
        getAreaCode();
        otcWaresGet(false);
        quotaPrice();
    }

    private void restoreData() {
        if (mOtcWarePoster != null) {
            mLegalPaySymbol = mOtcWarePoster.getPayCurrency();
            mLegalCoinSymbol = mOtcWarePoster.getCoinSymbol();
            mWaresModel.setDealType(mOtcWarePoster.getDealType());
            mWaresModel.setPriceType(mOtcWarePoster.getPriceType());
            mWaresModel.setCoinSymbol(mLegalCoinSymbol);
            mWaresModel.setPayCurrency(mLegalPaySymbol);
            switch (mOtcWarePoster.getPriceType()) {
                case OtcWarePoster.FIXED_PRICE:
                    mWaresModel.setFixedPrice(FinanceUtil.trimTrailingZero(mOtcWarePoster.getFixedPrice()));
                    break;
                case OtcWarePoster.FLOATING_PRICE:
                    mWaresModel.setPercent(String.valueOf(mOtcWarePoster.getPercent()));
                    break;
                default:
            }
            mWaresModel.setId(String.valueOf(mPosterId));
            mWaresModel.setPayInfo(mOtcWarePoster.getPayInfo());
            mWaresModel.setRemark(mOtcWarePoster.getRemark());
            mWaresModel.setTradeCount(mOtcWarePoster.getTradeCount());
            mWaresModel.setPayIds(mOtcWarePoster.getPayIds());
            mWaresModel.setMinTurnover(mOtcWarePoster.getMinTurnover());
            mWaresModel.setMaxTurnover(mOtcWarePoster.getMaxTurnover());
            mWaresModel.setTradeCount(mOtcWarePoster.getTradeCount());
            mWaresModel.setConditionType(mOtcWarePoster.getConditionType());
            mWaresModel.setConditionValue(mOtcWarePoster.getConditionValue());
            List<BankCardBean> bankList = mOtcWarePoster.getBankList();
            if (bankList != null && !bankList.isEmpty()) {
                StringBuilder payInfo = new StringBuilder();
                for (BankCardBean bankCardBean : bankList) {
                    switch (bankCardBean.getPayType()) {
                        case BankCardBean.PAYTYPE_ALIPAY:
                            payInfo.append(PayType.ALIPAY);
                            payInfo.append(",");
                            break;
                        case BankCardBean.PAYTYPE_WX:
                            payInfo.append(PayType.WXPAY);
                            payInfo.append(",");
                            break;
                        case BankCardBean.PAYTYPE_BANK:
                            payInfo.append(PayType.BANK_PAY);
                            payInfo.append(",");
                            break;
                        default:
                    }
                }
                mWaresModel.setPayInfo(payInfo.substring(0, payInfo.length() - 1));
                mWaresModel.setPayIds(mOtcWarePoster.getPayIds());
            }
        } else {
            mPayCurrencySymbol.setText(mLegalPaySymbol.toUpperCase());
            mCoinSymbol.setText(mLegalCoinSymbol.toUpperCase());
            setDealType(true);
            mWaresModel.setPriceType(OtcWarePoster.FLOATING_PRICE);
            mWaresModel.setCoinSymbol(mLegalCoinSymbol);
            mWaresModel.setPayCurrency(mLegalPaySymbol);
            mWaresModel.setConditionType("");
            mWaresModel.setPercent("");
            mWaresModel.setId("");
            mWaresModel.setConditionValue("");
        }
        restoreDataView(mWaresModel);
    }

    private void restoreDataView(WaresModel waresModel) {
        mWaresPair.setText(getString(R.string.x_faction_str_x,
                mLegalCoinSymbol.toUpperCase(),
                mLegalPaySymbol.toUpperCase()));
        mWaresModel.setCoinSymbol(mLegalCoinSymbol);
        mWaresModel.setPayCurrency(mLegalPaySymbol);
        Log.e(TAG, "restoreDataView: " + new Gson().toJson(waresModel));
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
        mBuyIn.setText(getString(R.string.buy_symbol_x, mLegalCoinSymbol.toUpperCase()));
        mSellOut.setText(getString(R.string.sell_symbol_x, mLegalCoinSymbol.toUpperCase()));
        setPriceType(waresModel.getPriceType());
        switch (waresModel.getPriceType()) {
            case OtcWarePoster.FIXED_PRICE:
                mPremiumRate.setFilters(new InputFilter[]{new MoneyValueFilter()});
                String fixedPrice = waresModel.getFixedPrice();
                mPremiumRate.setText(fixedPrice);
                break;
            case OtcWarePoster.FLOATING_PRICE:
                mPremiumRate.setFilters(new InputFilter[]{new MoneyValueFilter(true, true)
                        .filterMin(-1000).filterMax(1000)});
                mPremiumRate.setText(waresModel.getPercent());
                break;
            default:
        }
        mPremiumRate.setSelection(mPremiumRate.getText().length());
        setTradeLimit(waresModel.getMinTurnover(), waresModel.getMaxTurnover());
        double tradeCount = waresModel.getTradeCount();
        if (tradeCount > 0) {
            mTradeAmount.setText(FinanceUtil.trimTrailingZero(tradeCount));
            mTradeAmount.setSelection(mTradeAmount.getText().length());
        }
        mRemark.setText(waresModel.getRemark());
        setPayInfo(waresModel.getPayInfo());
        String conditionType = mWaresModel.getConditionType();
        String conditionValue = mWaresModel.getConditionValue();
        if (!TextUtils.isEmpty(conditionType)) {
            String[] conditionTypes = conditionType.split(",");
            String[] conditionValues = conditionValue.split(",");
            for (int i = 0; i < conditionTypes.length; i++) {
                mConditionKeyValue.put(conditionTypes[i], conditionValues[i]);
            }
        }
        setConditionType(conditionType);
        setConditionValue(mConditionKeyValue);
        mBuyIn.setText(getString(R.string.buy_symbol_x, mLegalCoinSymbol.toUpperCase()));
        mSellOut.setText(getString(R.string.sell_symbol_x, mLegalCoinSymbol.toUpperCase()));
        mPremiumRate.addTextChangedListener(new ValidationWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (mWaresModel.getPriceType() == OtcWarePoster.FLOATING_PRICE) {
                    String string = mPremiumRate.getText().toString();
                    if (string.equals("-")) {
                        return;
                    }
                    if (string.length() > 0) {
                        double rate = Double.valueOf(string);
//                        if (rate > 999) {
//                            rate = 999;
//                            mPremiumRate.setText(String.valueOf(rate));
//                            mPremiumRate.setSelection(mPremiumRate.getText().length());
//                        }
                        setFloatingPrice(rate);
                    } else {
                        setFloatingPrice(0);
                    }
                }
                checkCanPreview();
            }
        });
        mTradeAmount.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(6)});
        mTradeAmount.addTextChangedListener(new ValidationWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkCanPreview();
            }
        });
    }

    private void setFloatingPrice(double rate) {
        String price = getString(R.string.x_space_x,
                FinanceUtil.formatWithScale((1 + rate / 100) * mQuotaPrice),
                mLegalPaySymbol.toUpperCase());
        SpannableString priceStr = StrUtil.mergeTextWithColor(getString(R.string.quota_price),
                price,
                ContextCompat.getColor(getContext(), R.color.red));
        mPrice.setText(priceStr);
    }

    private void setBalance(String balance) {
        mTvBalance.setVisibility(View.VISIBLE);
        SpannableString balanceStr = StrUtil.mergeTextWithColor(getString(R.string.account_balance),
                balance,
                ContextCompat.getColor(getContext(), R.color.red));
        mTvBalance.setText(balanceStr);
    }

    private void setConditionValue(HashMap<String, String> conditionKeyValue) {
        String authValue = conditionKeyValue.get(OtcWarePoster.CONDITION_AUTH);
        if (!TextUtils.isEmpty(authValue)) {
            if (authValue.equals("1")) {
                setCertificationLimit(true);
            } else if (authValue.equals("2")) {
                setCertificationLimit(false);
            }
        } else {
            mSeniorCertification.setSelected(false);
            mPrimaryCertification.setSelected(false);
        }
        String tradeValue = conditionKeyValue.get(OtcWarePoster.CONDITION_TRADE);
        if (!TextUtils.isEmpty(tradeValue)) {
            mTradeCountLimit.setText(tradeValue);
        } else {
            mTradeCountLimit.setText("");
        }
    }

    private void setDealType(boolean buy) {
        mBuyIn.setSelected(buy);
        mSellOut.setSelected(!buy);
        mWaresModel.setDealType(buy ? OtcWarePoster.DEAL_TYPE_BUY : OtcWarePoster.DEAL_TYPE_SELL);
    }

    private void otcWaresGet(final boolean preview) {
        if (mPosterId == 0) {
            return;
        }
        Apic.otcWaresGet(mPosterId)
                .callback(new Callback<Resp<OtcWarePoster>>() {
                    @Override
                    protected void onRespSuccess(Resp<OtcWarePoster> resp) {
                        mOtcWarePoster = resp.getData();
                        restoreData();
                        if (preview) {
                            showPreview(mOtcWarePoster);
                        }
                    }
                })
                .fire();
    }

    private void accountBalance() {
        Apic.accountBalance(mLegalCoinSymbol)
                .callback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        mBalance = resp.getData();
                        setBalance(mBalance);
                    }
                }).fire();
    }

    private void getLegalCoin() {
        Apic.getLegalCoin()
                .callback(new Callback<Resp<ArrayList<LegalCoin>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<LegalCoin>> resp) {
                        mLegalCoins = resp.getData();
                    }
                }).fire();
    }

    private void getCountryCurrency() {
        Apic.getCountryCurrency()
                .callback(new Callback<Resp<ArrayList<CountryCurrency>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<CountryCurrency>> resp) {
                        mCountryCurrencies = resp.getData();
                    }
                }).fire();
    }

    private void getBindListData() {
        Apic.bindList(0)
                .callback(new Callback<Resp<BindBankList>>() {
                    @Override
                    protected void onRespSuccess(Resp<BindBankList> resp) {
                        showPayTypeSelector(resp.getData());
                    }
                })
                .fire();
    }

    private void quotaPrice() {
        Apic.quotaPrice(mWaresModel.getCoinSymbol(), mWaresModel.getPayCurrency(), mWaresModel.getDealType())
                .callback(new Callback<Resp<QuotaPrice>>() {
                    @Override
                    protected void onRespSuccess(Resp<QuotaPrice> resp) {
                        mQuotaPrice = resp.getData().getPrice();
                        setFloatingPrice(0);
                    }
                }).fire();
    }

    private void otcWaresAdd(WaresModel otcWaresAdd) {
        Apic.otcWaresAdd(otcWaresAdd)
                .callback(new Callback<Resp<Integer>>() {
                    @Override
                    protected void onRespSuccess(Resp<Integer> resp) {
                        Intent data = new Intent();
                        data.putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true);
                        PublishPosterFragment.this.setResult(PUBLISH_POSTER_RESULT, data);
                        mPosterId = resp.getData();
                        otcWaresGet(true);
                    }
                }).fire();
    }

    private void otcWaresUpdate(final WaresModel otcWaresAdd) {
        Apic.otcWaresUpdate(otcWaresAdd)
                .callback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        Intent data = new Intent();
                        data.putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true);
                        PublishPosterFragment.this.setResult(PUBLISH_POSTER_RESULT, data);
                        otcWaresGet(true);
                    }
                }).fire();
    }

    private void getAreaCode() {
        Apic.getAreaCodes().tag(TAG)
                .callback(new Callback4Resp<Resp<List<AreaCode>>, List<AreaCode>>() {
                    @Override
                    protected void onRespData(List<AreaCode> data) {
                        if (!data.isEmpty()) {
                            mAreaCodes = data;
                            String areaCode = data.get(0).getTeleCode();
                            mAreaCode.setText(StrFormatter.getFormatAreaCode(areaCode));
                        }
                    }
                }).fireFreely();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.waresPair, R.id.preview, R.id.buyIn, R.id.sellOut, R.id.priceType, R.id.tradeLimit, R.id.payType,
            R.id.remark, R.id.buyerLimit, R.id.primaryCertification, R.id.seniorCertification, R.id.areaCode, R.id.confirmRulesGroup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.waresPair:
                if (mLegalCoins != null && mCountryCurrencies != null) {
                    showWaresPairFilter();
                } else {
                    getLegalCoin();
                    getCountryCurrency();
                }
                break;
            case R.id.preview:
                saveDataAndUpdate();
                break;
            case R.id.buyIn:
                setDealType(true);
                quotaPrice();
                accountBalance();
                break;
            case R.id.sellOut:
                setDealType(false);
                quotaPrice();
                accountBalance();
                break;
            case R.id.priceType:
                showPriceTypeSelector();
                break;
            case R.id.tradeLimit:
                showTradeLimitSelector();
                break;
            case R.id.payType:
                getBindListData();
                break;
            case R.id.remark:
                showRemarkInput();
                break;
            case R.id.buyerLimit:
                showBuyLimitSelector();
                break;
            case R.id.primaryCertification:
                mConditionKeyValue.put(OtcWarePoster.CONDITION_AUTH, "1");
                setConditionValue(mConditionKeyValue);
                break;
            case R.id.seniorCertification:
                mConditionKeyValue.put(OtcWarePoster.CONDITION_AUTH, "2");
                setConditionValue(mConditionKeyValue);
                break;
            case R.id.areaCode:
                showAreaCodeSelector();
                break;
            case R.id.confirmRulesGroup:
                mConfirmRulesGroup.setSelected(!mConfirmRulesGroup.isSelected());
                checkCanPreview();
                mCheck.setImageResource(mConfirmRulesGroup.isSelected() ? R.drawable.ic_common_checkmark : 0);
                break;
            default:
        }
    }

    private void checkCanPreview() {
        boolean canPreview = mConfirmRulesGroup.isSelected();
        String price = mPremiumRate.getText().toString();
        if (TextUtils.isEmpty(price)) {
            canPreview = false;
        }
        if (mWaresModel.getMaxTurnover() == 0) {
            canPreview = false;
        }
        String tradeAmount = mTradeAmount.getText().toString();
        if (TextUtils.isEmpty(tradeAmount)) {
            canPreview = false;
        }
        if (TextUtils.isEmpty(mWaresModel.getPayInfo())) {
            canPreview = false;
        }
        mPreview.setEnabled(canPreview);
    }

    private void showWaresPairFilter() {
        WaresPairFilter waresPairFilter = new WaresPairFilter(getContext(), mLegalCoins, mCountryCurrencies);
        waresPairFilter.setSelectedSymbol(mLegalCoinSymbol, mLegalPaySymbol);
        waresPairFilter.setOnSelectCallBack(new WaresPairFilter.OnSelectCallBack() {
            @Override
            public void onSelected(String tempLegalSymbol, String tempCurrencySymbol) {
                mLegalPaySymbol = tempCurrencySymbol;
                mLegalCoinSymbol = tempLegalSymbol;
                mWaresPair.setText(getString(R.string.x_faction_str_x,
                        mLegalCoinSymbol.toUpperCase(),
                        mLegalPaySymbol.toUpperCase()));
                mWaresModel.setCoinSymbol(mLegalCoinSymbol);
                mWaresModel.setPayCurrency(mLegalPaySymbol);
                restoreData();
                quotaPrice();
                accountBalance();
            }
        });
        waresPairFilter.showOrDismiss(mWaresPairGroup);
    }

    private void saveDataAndUpdate() {
        String tradeAmount = mTradeAmount.getText().toString();
        String rateOrPrice = mPremiumRate.getText().toString();
//        if (TextUtils.isEmpty(tradeAmount) || TextUtils.isEmpty(rateOrPrice)) {
//            // TODO: 2018/6/27  提示文字
//            ToastUtil.show(R.string.deal_count_limit_unset);
//            return;
//        }
//        if (TextUtils.isEmpty(mWaresModel.getPayInfo())) {
//            // TODO: 2018/6/27  提示文字
//            ToastUtil.show(R.string.deal_count_limit_unset);
//            return;
//        }
//        if (mWaresModel.getMaxTurnover() == 0) {
//            // TODO: 2018/6/27  提示文字
//            ToastUtil.show(R.string.deal_count_limit_unset);
//            return;
//        }
        mWaresModel.setTradeCount(Double.valueOf(tradeAmount));
        switch (mWaresModel.getPriceType()) {
            case OtcWarePoster.FIXED_PRICE:
                mWaresModel.setFixedPrice(FinanceUtil.trimTrailingZero(Double.valueOf(rateOrPrice)));
                break;
            case OtcWarePoster.FLOATING_PRICE:
                mWaresModel.setPercent(rateOrPrice);
                break;
            default:
        }
        String phone = mPhone.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            mWaresModel.setTelephone(phone);
        }
        if (mConditionKeyValue.containsKey(OtcWarePoster.CONDITION_TRADE)) {
            String value = mTradeCountLimit.getText().toString();
            if (TextUtils.isEmpty(value)) {
                ToastUtil.show(R.string.deal_count_limit_unset);
                return;
            }
            mConditionKeyValue.put(OtcWarePoster.CONDITION_TRADE, value);
        }
        StringBuilder conditionType = new StringBuilder();
        StringBuilder conditionValue = new StringBuilder();
        for (Map.Entry<String, String> entry : mConditionKeyValue.entrySet()) {
            conditionType.append(entry.getKey());
            conditionType.append(",");
            conditionValue.append(entry.getValue());
            conditionValue.append(",");
        }
        if (conditionType.length() > 0) {
            mWaresModel.setConditionType(conditionType.subSequence(0, conditionType.length() - 1).toString());
        }
        if (conditionValue.length() > 0) {
            mWaresModel.setConditionValue(conditionValue.subSequence(0, conditionValue.length() - 1).toString());
        }
        if (mPosterId == 0) {
            otcWaresAdd(mWaresModel);
        } else {
            mWaresModel.setId(String.valueOf(mPosterId));
            otcWaresUpdate(mWaresModel);
        }
    }

    private void setCertificationLimit(boolean b) {
        mSeniorCertification.setSelected(!b);
        mPrimaryCertification.setSelected(b);
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
        if (!TextUtils.isEmpty(conditionType)) {
            StringBuilder sb = new StringBuilder();
            if (conditionType.contains(OtcWarePoster.CONDITION_AUTH)) {
                if (!mConditionKeyValue.containsKey(OtcWarePoster.CONDITION_AUTH)) {
                    mConditionKeyValue.put(OtcWarePoster.CONDITION_AUTH, "1");
                }
                sb.append(getString(R.string.authentication));
                sb.append("、");
                mBuyerAuthLimitGroup.setVisibility(View.VISIBLE);
            } else {
                mConditionKeyValue.remove(OtcWarePoster.CONDITION_AUTH);
                mBuyerAuthLimitGroup.setVisibility(View.GONE);
            }
            if (conditionType.contains(OtcWarePoster.CONDITION_TRADE)) {
                if (!mConditionKeyValue.containsKey(OtcWarePoster.CONDITION_TRADE)) {
                    mConditionKeyValue.put(OtcWarePoster.CONDITION_TRADE, "");
                }
                sb.append(getString(R.string.deal_success_count));
                sb.append("、");
                mBuyerCountLimitGroup.setVisibility(View.VISIBLE);
            } else {
                mConditionKeyValue.remove(OtcWarePoster.CONDITION_TRADE);
                mBuyerCountLimitGroup.setVisibility(View.GONE);
            }
            if (sb.length() > 0) {
                mBuyerLimit.setText(sb.toString().substring(0, sb.length() - 1));
            } else {
                mBuyerLimit.setText("");
            }
        }
        mWaresModel.setConditionType(conditionType);
        setConditionValue(mConditionKeyValue);
    }

    private void showAreaCodeSelector() {
        if (mAreaCodes == null || mAreaCodes.isEmpty()) {
            return;
        }
        final ArrayList<String> codes = new ArrayList<>();
        for (AreaCode code : mAreaCodes) {
            codes.add(code.getTeleCode());
        }
        mPvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                String areaCode = codes.get(options1);
                mAreaCode.setText(StrFormatter.getFormatAreaCode(areaCode));
                mWaresModel.setAreaCode(areaCode);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_view, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        TextView cancel = v.findViewById(R.id.cancel);
                        TextView confirm = v.findViewById(R.id.confirm);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPvOptions.dismiss();
                            }
                        });
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPvOptions.returnData();
                                mPvOptions.dismiss();
                            }
                        });
                    }
                })
                .setCyclic(false, false, false)
                .setTextColorCenter(ContextCompat.getColor(getContext(), R.color.text22))
                .setTextColorOut(ContextCompat.getColor(getContext(), R.color.text99))
                .setDividerColor(ContextCompat.getColor(getContext(), R.color.bgDD))
                .build();
        mPvOptions.setPicker(codes, null, null);
        mPvOptions.show();
    }

    private void showPreview(OtcWarePoster otcWarePoster) {
        PosterPreviewController posterPreviewController = new PosterPreviewController(getContext());
        posterPreviewController.setOnItemClickListener(new PosterPreviewController.OnItemClickListener() {
            @Override
            public void onConfirmClick() {
                updateStatus(mPosterId);
            }
        });
        posterPreviewController.setOtcWarePoster(otcWarePoster);
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(posterPreviewController)
                .show();
    }

    private void updateStatus(int posterId) {
        Apic.otcWaresUpdateStatus(posterId, OtcWarePoster.ON_SHELF)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        Intent data = new Intent();
                        data.putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true);
                        setResult(PUBLISH_POSTER_RESULT, data);
                        finish();
                    }
                })
                .fire();
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

    private void showPayTypeSelector(BindBankList data) {
        PayTypeController payTypeController = new PayTypeController(getContext());
        payTypeController.setOnItemClickListener(new PayTypeController.OnItemClickListener() {
            @Override
            public void onConfirmClick(String payInfo, String id) {
                setPayInfo(payInfo);
                mWaresModel.setPayIds(id);
            }
        });
        String payInfo = mWaresModel.getPayInfo();
        payTypeController.setPayInfo(payInfo);
        payTypeController.setSelectedBankId(mWaresModel.getPayIds());
        payTypeController.setBankList(data);
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(payTypeController)
                .show();
    }

    private void setPayInfo(String payInfo) {
        if (TextUtils.isEmpty(payInfo)) {
            mWaresModel.setPayInfo("");
            mPayType.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (payInfo.contains(PayType.ALIPAY)) {
            sb.append(getString(R.string.alipay));
            sb.append("、");
        }
        if (payInfo.contains(PayType.WXPAY)) {
            sb.append(getString(R.string.weichat));
            sb.append("、");
        }
        if (payInfo.contains(PayType.BANK_PAY)) {
            sb.append(getString(R.string.bank_card));
            sb.append("、");
        }
        if (sb.length() > 0) {
            mPayType.setText(sb.toString().substring(0, sb.length() - 1));
            mWaresModel.setPayInfo(payInfo);
        }
        checkCanPreview();
    }

    private void showTradeLimitSelector() {
        TradeLimitController tradeLimitController = new TradeLimitController(getContext());
        tradeLimitController.setOnItemClickListener(new TradeLimitController.OnItemClickListener() {
            @Override
            public void onConfirmClick(double minTurnover, double maxTurnover) {
                setTradeLimit(minTurnover, maxTurnover);
            }
        });
        tradeLimitController.restoreLimit(mWaresModel.getMinTurnover(), mWaresModel.getMaxTurnover());
        tradeLimitController.setPayCurrencySymbol(mWaresModel.getPayCurrency());
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(tradeLimitController)
                .show();
    }

    private void setTradeLimit(double minTurnover, double maxTurnover) {
        if (maxTurnover != 0) {
            mTradeLimit.setText(getString(R.string.limit_range_x,
                    FinanceUtil.trimTrailingZero(minTurnover), FinanceUtil.trimTrailingZero(maxTurnover)));
            mWaresModel.setMinTurnover(minTurnover);
            mWaresModel.setMaxTurnover(maxTurnover);
        }
        checkCanPreview();
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
                mPremiumRate.setFilters(new InputFilter[]{new MoneyValueFilter()});
                String fixedPrice = mWaresModel.getFixedPrice();
                mPremiumRate.setText(fixedPrice);
                mPriceType.setText(R.string.fixed_price);
                mPriceText.setText(R.string.price);
                mPriceSymbol.setText(mLegalPaySymbol.toUpperCase());
                mWaresModel.setPriceType(OtcWarePoster.FIXED_PRICE);
                mPrice.setVisibility(View.GONE);
                break;
            case OtcWarePoster.FLOATING_PRICE:
                mPremiumRate.setFilters(new InputFilter[]{new MoneyValueFilter(true, true)
                        .filterMin(-1000).filterMax(1000)});
                mPremiumRate.setText(mWaresModel.getPercent());
                mPriceType.setText(R.string.floating_price);
                mPriceText.setText(R.string.floating_price_rate);
                mPriceSymbol.setText(R.string.percent_symbol);
                mWaresModel.setPriceType(OtcWarePoster.FLOATING_PRICE);
                mPrice.setVisibility(View.VISIBLE);
                setFloatingPrice(0);
                break;
            default:
        }
    }
}
