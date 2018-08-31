package com.songbai.futurex.newotc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.LegalCurrencyOrderActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.WebActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.fragment.legalcurrency.LegalCurrencyOrderDetailFragment;
import com.songbai.futurex.fragment.mine.BindPhoneFragment;
import com.songbai.futurex.fragment.mine.CashPwdFragment;
import com.songbai.futurex.fragment.mine.PrimaryCertificationFragment;
import com.songbai.futurex.fragment.mine.SelectPayTypeFragment;
import com.songbai.futurex.fragment.mine.SeniorCertificationFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.NewOTCPrice;
import com.songbai.futurex.model.NewOTCYetOrder;
import com.songbai.futurex.model.NewOrderData;
import com.songbai.futurex.model.ParamBean;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.status.OTCOrderStatus;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.Network;
import com.songbai.futurex.utils.UmengCountEventId;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.utils.inputfilter.MoneyValueFilter;
import com.songbai.futurex.view.BadgeTextView;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.MsgHintController;
import com.songbai.futurex.view.dialog.WithDrawPsdViewController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/8/27
 */
public class SimpleOTCFragment extends BaseFragment {
    private static final int SHOULD_BIND_PAY = -1;

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.order)
    BadgeTextView mOrder;
    @BindView(R.id.radioHeader)
    RadioHeader mRadioHeader;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.tradeAmount)
    EditText mTradeAmount;
    @BindView(R.id.coinSymbol)
    TextView mCoinSymbol;
    @BindView(R.id.turnover)
    EditText mTurnover;
    @BindView(R.id.turnoverSymbol)
    TextView mTurnoverSymbol;
    @BindView(R.id.confirm)
    TextView mConfirm;
    Unbinder unbinder;
    @BindView(R.id.tradePriceText)
    TextView mTradePriceText;
    @BindView(R.id.tradeAmountText)
    TextView mTradeAmountText;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.recentOrderHint)
    LinearLayout mRecentOrderHint;
    private int mTradeType = OTCOrderStatus.ORDER_DIRECT_BUY;
    private String mSelectedCoinSymbol = "usdt";
    private String mSelectedLegalSymbol = "cny";
    private NewOTCPrice mNewOTCPrice;
    private boolean mPrepared;

    private Network.NetworkChangeReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            getPrice();
            getYetOrder();
        }
    };
    private ValidationWatcher mWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            setAmountAndTurnover();
        }
    };

    private ValidationWatcher mConfirmEnableWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            checkConfirmEnable();
        }
    };

    private View.OnTouchListener mListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            resetValueCal();
            if (v instanceof EditText) {
                EditText editText = (EditText) v;
                editText.addTextChangedListener(mWatcher);
                editText.setSelection(editText.getText().toString().trim().length());
            }
            return false;
        }
    };
    private NewOTCYetOrder mNewOTCYetOrder;

    private void setAmountAndTurnover() {
        if (mNewOTCPrice != null) {
            String turnover = mTurnover.getText().toString().trim();
            String amount = mTradeAmount.getText().toString().trim();
            if (mTurnover.isFocused()) {
                double value = 0;
                if (!TextUtils.isEmpty(turnover)) {
                    value = Double.valueOf(turnover);
                }
                double calAmount = value / (mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY ? mNewOTCPrice.getBuyPrice() : mNewOTCPrice.getSellPrice());
                mTradeAmount.setText(calAmount == 0 ? "" : FinanceUtil.subZeroAndDot(calAmount, 2));
            } else if (mTradeAmount.isFocused()) {
                double value = 0;
                if (!TextUtils.isEmpty(amount)) {
                    value = Double.valueOf(amount);
                }
                double calTurnover = value * (mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY ? mNewOTCPrice.getBuyPrice() : mNewOTCPrice.getSellPrice());
                mTurnover.setText(calTurnover == 0 ? "" : FinanceUtil.subZeroAndDot(calTurnover, 2));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_otc, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPrepared = true;
        ((BaseActivity) getActivity()).addStatusBarHeightPaddingTop(mTitleBar);
        Network.registerNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
        mTradeAmount.addTextChangedListener(mConfirmEnableWatcher);
        mTurnover.addTextChangedListener(mConfirmEnableWatcher);
        mTradeAmount.setOnTouchListener(mListener);
        mTurnover.setOnTouchListener(mListener);
        mRadioHeader.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                clearData();
                if (position == 0) {
                    mTradeType = OTCOrderStatus.ORDER_DIRECT_BUY;
                } else {
                    mTradeType = OTCOrderStatus.ORDER_DIRECT_SELL;
                }
                setView();
                setPrice();
                setLimit();
            }
        });
        setView();
        getPrice();
    }

    private void resetValueCal() {
        mTradeAmount.removeTextChangedListener(mWatcher);
        mTurnover.removeTextChangedListener(mWatcher);
    }

    private void checkConfirmEnable() {
        String turnover = mTurnover.getText().toString().trim();
        String tradeAmount = mTradeAmount.getText().toString().trim();
        boolean enabled = !TextUtils.isEmpty(turnover) && !TextUtils.isEmpty(tradeAmount);
        if (enabled) {
            enabled = false;
            if (mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY && Double.valueOf(turnover) >= mNewOTCPrice.getBuyMinPrice() && Double.valueOf(turnover) <= mNewOTCPrice.getBuyMaxPrice()) {
                enabled = true;
            } else if (mTradeType == OTCOrderStatus.ORDER_DIRECT_SELL && Double.valueOf(turnover) >= mNewOTCPrice.getSellMinPrice() && Double.valueOf(turnover) <= mNewOTCPrice.getSellMaxPrice()) {
                enabled = true;
            }
        }
        mConfirm.setEnabled(enabled);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            getPrice();
            getYetOrder();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mPrepared) {
            getPrice();
            getYetOrder();
        }
    }

    private void getPrice() {
        Apic.newOtcGetPrice().tag(TAG)
                .callback(new Callback<Resp<NewOTCPrice>>() {
                    @Override
                    protected void onRespSuccess(Resp<NewOTCPrice> resp) {
                        mNewOTCPrice = resp.getData();
                        setPrice();
                        setLimit();
                    }
                }).fireFreely();
    }

    private void setLimit() {
        if (mNewOTCPrice != null) {
            if (mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY) {
                double buyPrice = mNewOTCPrice.getBuyPrice();
                double buyMinCount = mNewOTCPrice.getBuyMinCount();
                double buyMinPrice = mNewOTCPrice.getBuyMinPrice();
                double buyMaxCount = mNewOTCPrice.getBuyMaxCount();
                double buyMaxPrice = mNewOTCPrice.getBuyMaxPrice();
                double minCount = buyMinPrice / buyPrice;
                double maxCount = buyMaxPrice / buyPrice;
                if (minCount < buyMinCount) {
                    mNewOTCPrice.setBuyMinPrice(Double.parseDouble(FinanceUtil.formatWithScale(buyMinCount * buyPrice)));
                } else {
                    mNewOTCPrice.setBuyMinCount(Double.parseDouble(FinanceUtil.formatWithScale(buyMinPrice / buyPrice)));
                }
                if (maxCount > buyMaxCount) {
                    mNewOTCPrice.setBuyMaxPrice(Double.parseDouble(FinanceUtil.formatWithScale(buyMaxCount * buyPrice)));
                } else {
                    mNewOTCPrice.setBuyMaxCount(Double.parseDouble(FinanceUtil.formatWithScale(buyMaxPrice / buyPrice)));
                }
                mTradeAmount.setFilters(new InputFilter[]{
                        new MoneyValueFilter(getContext())
                                .setDigits(2)
                                .filterMin(mNewOTCPrice.getBuyMinCount())
                                .filterMax(mNewOTCPrice.getBuyMaxCount())});
                mTurnover.setFilters(new InputFilter[]{
                        new MoneyValueFilter(getContext())
                                .setDigits(2)
                                .filterMin(mNewOTCPrice.getBuyMinPrice())
                                .filterMax(mNewOTCPrice.getBuyMaxPrice())});
            } else {
                double sellPrice = mNewOTCPrice.getSellPrice();
                double sellMinCount = mNewOTCPrice.getSellMinCount();
                double sellMinPrice = mNewOTCPrice.getSellMinPrice();
                double sellMaxCount = mNewOTCPrice.getSellMaxCount();
                double sellMaxPrice = mNewOTCPrice.getSellMaxPrice();
                double minCount = sellMinPrice / sellPrice;
                double maxCount = sellMaxPrice / sellPrice;
                if (minCount < sellMinCount) {
                    mNewOTCPrice.setSellMinPrice(Double.parseDouble(FinanceUtil.formatWithScale(sellMinCount * sellPrice)));
                } else {
                    mNewOTCPrice.setSellMinCount(Double.parseDouble(FinanceUtil.formatWithScale(sellMinPrice / sellPrice)));
                }
                if (maxCount > sellMaxCount) {
                    mNewOTCPrice.setSellMaxPrice(Double.parseDouble(FinanceUtil.formatWithScale(sellMaxCount * sellPrice)));
                } else {
                    mNewOTCPrice.setSellMaxCount(Double.parseDouble(FinanceUtil.formatWithScale(sellMaxPrice / sellPrice)));
                }
                mTradeAmount.setFilters(new InputFilter[]{
                        new MoneyValueFilter(getContext())
                                .setDigits(2)
                                .filterMin(mNewOTCPrice.getSellMinCount())
                                .filterMax(mNewOTCPrice.getSellMaxCount())});
                mTurnover.setFilters(new InputFilter[]{
                        new MoneyValueFilter(getContext())
                                .setDigits(2)
                                .filterMin(mNewOTCPrice.getSellMinPrice())
                                .filterMax(mNewOTCPrice.getSellMaxPrice())});
            }
        }
    }

    private void getYetOrder() {
        if (!LocalUser.getUser().isLogin()) {
            return;
        }
        Apic.newOtcGetYetOrder().tag(TAG)
                .callback(new Callback<Resp<NewOTCYetOrder>>() {
                    @Override
                    protected void onRespSuccess(Resp<NewOTCYetOrder> resp) {
                        mNewOTCYetOrder = resp.getData();
                        mRecentOrderHint.setVisibility(mNewOTCYetOrder.getCount() > 0 ? View.VISIBLE : View.GONE);
                        String id = mNewOTCYetOrder.getId();
                        mRecentOrderHint.setVisibility(!TextUtils.isEmpty(id) ? View.VISIBLE : View.GONE);
                    }
                }).fireFreely();
    }

    private void setPrice() {
        if (mNewOTCPrice != null) {
            mPrice.setText(getString(R.string.x_space_x,
                    mTradeType == 1 ? String.valueOf(mNewOTCPrice.getBuyPrice()) :
                            String.valueOf(mNewOTCPrice.getSellPrice()),
                    mSelectedLegalSymbol.toUpperCase()));
        }
    }

    private void setView() {
        mTitle.setText(getString(R.string.x_faction_str_x,
                mSelectedCoinSymbol.toUpperCase(),
                mSelectedLegalSymbol.toUpperCase()));
        mTradePriceText.setText(mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY ? R.string.buy_in_price : R.string.sell_out_price);
        mTradeAmountText.setText(mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY ? R.string.buy_in_amount : R.string.sell_out_amount);
        mCoinSymbol.setText(mSelectedCoinSymbol.toUpperCase());
        mTurnoverSymbol.setText(mSelectedLegalSymbol.toUpperCase());
        mConfirm.setText(mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY ? R.string.buy_in : R.string.sell_out);
        mConfirm.setBackgroundResource(mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY ? R.drawable.btn_primary : R.drawable.btn_red);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Network.unregisterNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
        unbinder.unbind();
    }

    @OnClick({R.id.recentOrderHint, R.id.order, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recentOrderHint:
                if (mNewOTCYetOrder != null) {
                    if (mNewOTCYetOrder.getCount() > 1) {
                        lunchOrder();
                    } else {
                        String id = mNewOTCYetOrder.getId();
                        UniqueActivity.launcher(this, LegalCurrencyOrderDetailFragment.class)
                                .putExtra(ExtraKeys.ORDER_ID, id)
                                .putExtra(ExtraKeys.TRADE_DIRECTION, mNewOTCYetOrder.getDirect())
                                .execute();
                    }
                }
                break;
            case R.id.order:
                lunchOrder();
                break;
            case R.id.confirm:
                if (LocalUser.getUser().isLogin()) {
                    trade();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            default:
        }
    }

    private void lunchOrder() {
        if (LocalUser.getUser().isLogin()) {
            Launcher.with(this, LegalCurrencyOrderActivity.class).execute();
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    private void trade() {
        if (LocalUser.getUser().getUserInfo().getAuthenticationStatus() < 1) {
            showAlertMsgHint(Resp.Code.NEEDS_PRIMARY_CERTIFICATION);
            return;
        }
        if (LocalUser.getUser().getUserInfo().getSafeSetting() != 1) {
            showAlertMsgHint(Resp.Code.CASH_PWD_NONE);
            return;
        }
        if (LocalUser.getUser().getUserInfo().getPayment() < 1) {
            showAlertMsgHint(SHOULD_BIND_PAY);
            return;
        }
        if (mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY) {
            buy("", mTradeAmount.getText().toString(), mSelectedCoinSymbol);
        } else {
            WithDrawPsdViewController withDrawPsdViewController = new WithDrawPsdViewController(getActivity(),
                    new WithDrawPsdViewController.OnClickListener() {
                        @Override
                        public void onForgetClick() {
                            umengEventCount(UmengCountEventId.TRADE0004);
                        }

                        @Override
                        public void onConfirmClick(String cashPwd, String googleAuth) {
                            sell(mTradeAmount.getText().toString(), mSelectedCoinSymbol, cashPwd);
                        }
                    });
            withDrawPsdViewController.setShowGoogleAuth(false);
            SmartDialog smartDialog = SmartDialog.solo(getActivity());
            smartDialog.setCustomViewController(withDrawPsdViewController)
                    .show();
            withDrawPsdViewController.setTitle(R.string.fund_password_verification);
        }
    }

    private void showAlertMsgHint(final int code) {
        int msg = 0;
        int confirmText = R.string.ok;
        switch (code) {
            case SHOULD_BIND_PAY:
                msg = R.string.have_not_bind_pay;
                confirmText = R.string.go_to_bind;
                break;
            case Resp.Code.PHONE_NONE:
                msg = R.string.should_bind_phone;
                confirmText = R.string.go_to_set;
                break;
            case Resp.Code.CASH_PWD_NONE:
                msg = R.string.set_draw_cash_pwd_hint;
                confirmText = R.string.go_to_set;
                break;
            case Resp.Code.NEEDS_PRIMARY_CERTIFICATION:
                msg = R.string.poster_owner_set_needs_primary_certification;
                confirmText = R.string.go_to;
                break;
            case Resp.Code.NEEDS_SENIOR_CERTIFICATION:
                msg = R.string.poster_owner_set_needs_advanced_certification;
                confirmText = R.string.go_to;
                break;
            case Resp.Code.NEEDS_MORE_DEAL_COUNT:
                msg = R.string.your_deal_count_is_less_than_limit;
                confirmText = R.string.got_it;
                break;
            default:
        }
        MsgHintController withDrawPsdViewController = new MsgHintController(getActivity(), new MsgHintController.OnClickListener() {
            @Override
            public void onConfirmClick() {
                switch (code) {
                    case Resp.Code.PHONE_NONE:
                        UniqueActivity.launcher(SimpleOTCFragment.this, BindPhoneFragment.class)
                                .execute();
                        break;
                    case SHOULD_BIND_PAY:
                        UniqueActivity.launcher(SimpleOTCFragment.this, SelectPayTypeFragment.class)
                                .execute();
                        break;
                    case Resp.Code.CASH_PWD_NONE:
                        UniqueActivity.launcher(SimpleOTCFragment.this, CashPwdFragment.class)
                                .putExtra(ExtraKeys.HAS_WITH_DRAW_PASS, false)
                                .execute();
                        break;
                    case Resp.Code.NEEDS_PRIMARY_CERTIFICATION:
                        UniqueActivity.launcher(SimpleOTCFragment.this, PrimaryCertificationFragment.class)
                                .execute();
                        break;
                    case Resp.Code.NEEDS_SENIOR_CERTIFICATION:
                        UniqueActivity.launcher(SimpleOTCFragment.this, SeniorCertificationFragment.class)
                                .putExtra(ExtraKeys.AUTHENTICATION_STATUS, LocalUser.getUser().getUserInfo().getAuthenticationStatus())
                                .execute();
                        break;
                    default:
                }
            }
        });
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog.setCustomViewController(withDrawPsdViewController)
                .show();
        withDrawPsdViewController.setConfirmText(confirmText);
        withDrawPsdViewController.setMsg(msg);
        withDrawPsdViewController.setImageRes(R.drawable.ic_popup_attention);
    }

    private void sell(String coinCount, String coinSymbol, String drawPwd) {
        Apic.newOtcSell(coinCount, coinSymbol, md5Encrypt(drawPwd)).tag(TAG)
                .callback(new Callback<Resp<NewOrderData>>() {
                    @Override
                    protected void onRespSuccess(Resp<NewOrderData> resp) {
                        clearData();
                        NewOrderData data = resp.getData();
                        if (data.getOrderType() == 1) {
                            OpenOtc365(data);
                        } else {
                            String id = String.valueOf(data.getId());
                            UniqueActivity.launcher(SimpleOTCFragment.this, LegalCurrencyOrderDetailFragment.class)
                                    .putExtra(ExtraKeys.ORDER_ID, id)
                                    .putExtra(ExtraKeys.TRADE_DIRECTION, mNewOTCYetOrder.getDirect())
                                    .execute();
                        }
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        int code = failedResp.getCode();
                        if (code == Resp.Code.PHONE_NONE || code == Resp.Code.CASH_PWD_NONE || code == Resp.Code.NEEDS_PRIMARY_CERTIFICATION
                                || code == Resp.Code.NEEDS_SENIOR_CERTIFICATION || code == Resp.Code.NEEDS_MORE_DEAL_COUNT) {
                            showAlertMsgHint(code);
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
    }

    private void OpenOtc365(NewOrderData data) {
        ParamBean param = data.getParam();
        StringBuilder builder = new StringBuilder();
        //拼接post提交参数
        builder.append("coin_amount=").append(param.getCoin_amount()).append("&")
                .append("sync_url=").append(param.getSync_url()).append("&")
                .append("coin_sign=").append(param.getCoin_sign()).append("&")
                .append("sign=").append(param.getSign()).append("&")
                .append("order_time=").append(param.getOrder_time()).append("&")
                .append("pay_card_num=").append(param.getPay_card_num()).append("&")
                .append("async_url=").append(param.getAsync_url()).append("&")
                .append("id_card_num=").append(param.getId_card_num()).append("&")
                .append("kyc=").append(param.getKyc()).append("&")
                .append("phone=").append(param.getPhone()).append("&")
                .append("company_order_num=").append(param.getCompany_order_num()).append("&")
                .append("appkey=").append(param.getAppkey()).append("&")
                .append("id_card_type=").append(param.getId_card_type()).append("&")
                .append("username=").append(param.getUsername()).append("&");
        Launcher.with(getActivity(), Otc365FilterWebActivity.class)
                .putExtra(WebActivity.EX_URL, data.getTargetUrl())
                .putExtra(WebActivity.EX_POST_DATA, builder.toString())
                .execute();
    }

    private void buy(String cost, String coinCount, String coinSymbol) {
        Apic.newOtcDestineOrder(cost, coinCount, coinSymbol).tag(TAG)
                .callback(new Callback<Resp<NewOrderData>>() {
                    @Override
                    protected void onRespSuccess(Resp<NewOrderData> resp) {
                        clearData();
                        NewOrderData data = resp.getData();
                        if (data.getOrderType() == 1) {
                            OpenOtc365(data);
                        } else {
                            String id = String.valueOf(data.getId());
                            UniqueActivity.launcher(SimpleOTCFragment.this, LegalCurrencyOrderDetailFragment.class)
                                    .putExtra(ExtraKeys.ORDER_ID, id)
                                    .putExtra(ExtraKeys.TRADE_DIRECTION, mNewOTCYetOrder.getDirect())
                                    .execute();
                        }
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        int code = failedResp.getCode();
                        if (code == Resp.Code.PHONE_NONE || code == Resp.Code.CASH_PWD_NONE || code == Resp.Code.NEEDS_PRIMARY_CERTIFICATION
                                || code == Resp.Code.NEEDS_SENIOR_CERTIFICATION || code == Resp.Code.NEEDS_MORE_DEAL_COUNT) {
                            showAlertMsgHint(code);
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
    }

    private void clearData() {
        mTradeAmount.setText("");
        mTurnover.setText("");
    }
}
