package com.songbai.futurex.newotc;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.songbai.futurex.fragment.mine.AddBankingCardFragment;
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
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.SysMessage;
import com.songbai.futurex.model.status.OTCOrderStatus;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.Network;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.UmengCountEventId;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.utils.inputfilter.MoneyValueFilter;
import com.songbai.futurex.view.BadgeTextView;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.MsgHintController;
import com.songbai.futurex.view.dialog.SimpleOTCLimitController;
import com.songbai.futurex.view.dialog.WithDrawPsdViewController;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.PushDestUtils;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.msg.MsgProcessor;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;

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
    boolean setAuth, setCashPwd, setPhone, bindMainland, bindPay = false;

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
                mEditTurnover = v.getId() == R.id.turnover;
            }
            return false;
        }
    };
    private NewOTCYetOrder mNewOTCYetOrder;
    private MsgProcessor mMsgProcessor;
    private boolean mEditTurnover;
    private SimpleOTCLimitController mSimpleOTCLimitController;
    private static final int REQUEST_SET = 12343;
    private SmartDialog mSmartDialog;

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
                checkConfirmEnable();
            }
        });
        setView();
        getPrice();
        initSocketListener();
        initMsgPush();
    }

    private void initSocketListener() {
        mMsgProcessor = new MsgProcessor(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code, String dest) {
                if (PushDestUtils.isMsg(dest)) {
                    new DataParser<Response<SysMessage>>(data) {

                        @Override
                        public void onSuccess(Response<SysMessage> resp) {
                            getYetOrder();
                        }
                    }.parse();
                }
            }
        });
    }

    private void initMsgPush() {
        mMsgProcessor.registerMsg();
    }

    private void resetValueCal() {
        mTradeAmount.removeTextChangedListener(mWatcher);
        mTurnover.removeTextChangedListener(mWatcher);
    }

    private void checkConfirmEnable() {
        String turnover = mTurnover.getText().toString().trim();
        String tradeAmount = mTradeAmount.getText().toString().trim();
        boolean enabled = !TextUtils.isEmpty(turnover) && !TextUtils.isEmpty(tradeAmount);
        if (mNewOTCPrice != null) {
            if (mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY && mNewOTCPrice.getBuyWaresCount() < 1) {
                enabled = false;
                mConfirm.setText(R.string.trading_closed);
            } else if (mTradeType == OTCOrderStatus.ORDER_DIRECT_SELL && mNewOTCPrice.getSellWaresCount() < 1) {
                enabled = false;
                mConfirm.setText(R.string.trading_closed);
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
        mMsgProcessor.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMsgProcessor.pause();
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
                        checkConfirmEnable();
                    }
                }).fireFreely();
    }

    private void setLimit() {
        mTradeAmount.setFilters(new InputFilter[]{
                new MoneyValueFilter(getContext())
                        .setDigits(2)});
        mTurnover.setFilters(new InputFilter[]{
                new MoneyValueFilter(getContext())
                        .setDigits(2)});
        if (mNewOTCPrice != null) {
            if (mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY) {
                mTradeAmount.setHint(getString(R.string.limit_range_x,
                        String.valueOf(mNewOTCPrice.getBuyMinCount()),
                        String.valueOf(mNewOTCPrice.getBuyMaxCount())));
                mTurnover.setHint(getString(R.string.limit_range_x,
                        String.valueOf(mNewOTCPrice.getBuyMinPrice()),
                        String.valueOf(mNewOTCPrice.getBuyMaxPrice())));
            } else {
                mTradeAmount.setHint(getString(R.string.limit_range_x,
                        String.valueOf(mNewOTCPrice.getSellMinCount()),
                        String.valueOf(mNewOTCPrice.getSellMaxCount())));
                mTurnover.setHint(getString(R.string.limit_range_x,
                        String.valueOf(mNewOTCPrice.getSellMinPrice()),
                        String.valueOf(mNewOTCPrice.getSellMaxPrice())));
            }
        }
    }

    private void getYetOrder() {
        if (!LocalUser.getUser().isLogin()) {
            mRecentOrderHint.setVisibility(View.GONE);
            return;
        }
        Apic.newOtcGetYetOrder().tag(TAG)
                .callback(new Callback<Resp<NewOTCYetOrder>>() {
                    @Override
                    protected void onRespSuccess(Resp<NewOTCYetOrder> resp) {
                        mNewOTCYetOrder = resp.getData();
                        mRecentOrderHint.setVisibility(mNewOTCYetOrder.getCount() > 0 ? View.VISIBLE : View.GONE);
                        if (mNewOTCYetOrder.getCount() == 0) {
                            String id = mNewOTCYetOrder.getId();
                            mRecentOrderHint.setVisibility(!TextUtils.isEmpty(id) ? View.VISIBLE : View.GONE);
                        }
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
        mCoinSymbol.post(new Runnable() {
            @Override
            public void run() {
                int coinSymbolMeasuredWidth = mCoinSymbol.getMeasuredWidth();
                int turnoverSymbolMeasuredWidth = mTurnoverSymbol.getMeasuredWidth();
                int minWidth = Math.max(coinSymbolMeasuredWidth, turnoverSymbolMeasuredWidth);
                mCoinSymbol.setMinWidth(minWidth);
                mTurnoverSymbol.setMinWidth(minWidth);
            }
        });
        mConfirm.setText(mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY ? R.string.buy_in : R.string.sell_out);
        mConfirm.setBackgroundResource(mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY ? R.drawable.btn_primary : R.drawable.btn_red);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Network.unregisterNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
        mMsgProcessor.unregisterMsg();
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
                        UniqueActivity.launcher(this, LegalCurrencyOrderDetailFragment.class)
                                .putExtra(ExtraKeys.ORDER_ID, mNewOTCYetOrder.getId())
                                .putExtra(ExtraKeys.TRADE_DIRECTION, mNewOTCYetOrder.getDirect())
                                .execute();
                    }
                }
                break;
            case R.id.order:
                lunchOrder();
                break;
            case R.id.confirm:
                if (mNewOTCPrice != null) {
                    if (mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY && mNewOTCPrice.getBuyWaresCount() < 1) {
                        return;
                    }
                    if (mTradeType == OTCOrderStatus.ORDER_DIRECT_SELL && mNewOTCPrice.getSellWaresCount() < 1) {
                        return;
                    }
                }
                if (LocalUser.getUser().isLogin()) {
                    checkTrade();
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

    private void checkTrade() {
        boolean shouldShowAlert = shouldAlert();
        if (shouldShowAlert) {
            showAlert();
        } else {
            trade();
        }
    }

    private boolean shouldAlert() {
        boolean ret = false;
        setAuth = setCashPwd = setPhone = bindMainland = bindPay = false;
        boolean otc365 = isOtc365();
        if ((mTradeType == OTCOrderStatus.ORDER_DIRECT_SELL || otc365) && LocalUser.getUser().getUserInfo().getAuthenticationStatus() < 1) {
            setAuth = true;
            ret = true;
        }
        if ((mTradeType == OTCOrderStatus.ORDER_DIRECT_SELL || otc365) && LocalUser.getUser().getUserInfo().getSafeSetting() < 1) {
            ret = true;
            setCashPwd = true;
        }
        if (otc365 && TextUtils.isEmpty(LocalUser.getUser().getUserInfo().getUserPhone())) {
            ret = true;
            setPhone = true;
        }
        if (otc365 && LocalUser.getUser().getUserInfo().getOtcBankCount() < 1) {
            bindMainland = true;
            ret = true;
        }
        if (!otc365 && mTradeType == OTCOrderStatus.ORDER_DIRECT_SELL && LocalUser.getUser().getUserInfo().getPayment() < 1) {
            ret = true;
            bindPay = true;
        }
        return ret;
    }

    private boolean isOtc365() {
        return mNewOTCPrice != null &&
                ((mNewOTCPrice.getBuyOtc365Status() == 1 && mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY)
                        || (mNewOTCPrice.getSellOtc365Status() == 1 && mTradeType == OTCOrderStatus.ORDER_DIRECT_SELL));
    }

    private void showAlert() {
        if (mSimpleOTCLimitController == null) {
            mSimpleOTCLimitController = new SimpleOTCLimitController(getContext(), new SimpleOTCLimitController.OnItemClickListener() {
                @Override
                public void onItemClick(View view) {
                    switch (view.getId()) {
                        case R.id.setAuth:
                            UniqueActivity.launcher(SimpleOTCFragment.this, PrimaryCertificationFragment.class)
                                    .execute(SimpleOTCFragment.this, REQUEST_SET);
                            break;
                        case R.id.setCashPwd:
                            UniqueActivity.launcher(SimpleOTCFragment.this, CashPwdFragment.class)
                                    .putExtra(ExtraKeys.HAS_WITH_DRAW_PASS, false)
                                    .execute(SimpleOTCFragment.this, REQUEST_SET);
                            break;
                        case R.id.bindPhone:
                            UniqueActivity.launcher(SimpleOTCFragment.this, BindPhoneFragment.class)
                                    .execute(SimpleOTCFragment.this, REQUEST_SET);
                            break;
                        case R.id.bindBankCard:
                            if (LocalUser.getUser().getUserInfo().getAuthenticationStatus() < 1) {
                                ToastUtil.show(R.string.require_primary_auth);
                                return;
                            }
                            if (LocalUser.getUser().getUserInfo().getSafeSetting() < 1) {
                                ToastUtil.show(R.string.require_cash_pwd);
                                return;
                            }
                            UniqueActivity.launcher(SimpleOTCFragment.this, AddBankingCardFragment.class)
                                    .execute(SimpleOTCFragment.this, REQUEST_SET);
                            break;
                        case R.id.bindPay:
                            if (LocalUser.getUser().getUserInfo().getAuthenticationStatus() < 1) {
                                ToastUtil.show(R.string.require_primary_auth);
                                return;
                            }
                            if (LocalUser.getUser().getUserInfo().getSafeSetting() < 1) {
                                ToastUtil.show(R.string.require_cash_pwd);
                                return;
                            }
                            UniqueActivity.launcher(SimpleOTCFragment.this, SelectPayTypeFragment.class)
                                    .execute(SimpleOTCFragment.this, REQUEST_SET);
                            break;
                        default:
                    }
                }
            });
        }
        mSmartDialog = SmartDialog.solo(getActivity());
        mSmartDialog.setCustomViewController(mSimpleOTCLimitController)
                .show();
        setAlertView();
    }

    private void setAlertView() {
        if (mSimpleOTCLimitController != null) {
            shouldAlert();
            mSimpleOTCLimitController.setState(isOtc365(), mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY);
            mSimpleOTCLimitController.setAuth(setAuth);
            mSimpleOTCLimitController.setCashPwd(setCashPwd);
            mSimpleOTCLimitController.setPhone(setPhone);
            mSimpleOTCLimitController.bindMainland(bindMainland);
            mSimpleOTCLimitController.bindPay(bindPay);
        }
    }

    private void trade() {
        final String coinCount = mTradeAmount.getText().toString();
        if (mNewOTCPrice != null) {
            if (mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY) {
                if (Double.valueOf(coinCount) < mNewOTCPrice.getBuyMinCount()) {
                    ToastUtil.show(getString(mEditTurnover ? R.string.min_buy_amount_x : R.string.min_buy_num_x, mEditTurnover ? mNewOTCPrice.getBuyMinPrice() : mNewOTCPrice.getBuyMinCount()));
                    return;
                } else if (Double.valueOf(coinCount) > mNewOTCPrice.getBuyMaxCount()) {
                    ToastUtil.show(getString(mEditTurnover ? R.string.max_buy_amount_x : R.string.max_buy_num_x, mEditTurnover ? mNewOTCPrice.getBuyMaxPrice() : mNewOTCPrice.getBuyMaxCount()));
                    return;
                }
            } else if (mTradeType == OTCOrderStatus.ORDER_DIRECT_SELL) {
                if (Double.valueOf(coinCount) < mNewOTCPrice.getSellMinCount()) {
                    ToastUtil.show(getString(mEditTurnover ? R.string.min_sell_amount_x : R.string.min_sell_num_x, mEditTurnover ? mNewOTCPrice.getSellMinPrice() : mNewOTCPrice.getSellMinCount()));
                    return;
                } else if (Double.valueOf(coinCount) > mNewOTCPrice.getSellMaxCount()) {
                    ToastUtil.show(getString(mEditTurnover ? R.string.min_sell_amount_x : R.string.min_sell_num_x, mEditTurnover ? mNewOTCPrice.getSellMaxPrice() : mNewOTCPrice.getSellMaxCount()));
                    return;
                }
            }
        }
        if (mTradeType == OTCOrderStatus.ORDER_DIRECT_BUY) {
            buy("", coinCount, mSelectedCoinSymbol);
        } else {
            WithDrawPsdViewController withDrawPsdViewController = new WithDrawPsdViewController(getActivity(),
                    new WithDrawPsdViewController.OnClickListener() {
                        @Override
                        public void onForgetClick() {
                            umengEventCount(UmengCountEventId.TRADE0004);
                        }

                        @Override
                        public void onConfirmClick(String cashPwd, String googleAuth) {
                            sell(coinCount, mSelectedCoinSymbol, cashPwd);
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
            case Resp.Code.BANK_CADR_NONE:
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
                    case Resp.Code.BANK_CADR_NONE:
                        UniqueActivity.launcher(SimpleOTCFragment.this, AddBankingCardFragment.class)
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
                        if (data.getOrderType() == 1 && data.getParam() != null) {
                            openOtc365(data);
                        } else {
                            String id = String.valueOf(data.getId());
                            UniqueActivity.launcher(SimpleOTCFragment.this, LegalCurrencyOrderDetailFragment.class)
                                    .putExtra(ExtraKeys.ORDER_ID, id)
                                    .putExtra(ExtraKeys.TRADE_DIRECTION, OTCOrderStatus.ORDER_DIRECT_SELL)
                                    .execute();
                        }
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        int code = failedResp.getCode();
                        if (code == Resp.Code.PHONE_NONE || code == Resp.Code.CASH_PWD_NONE || code == Resp.Code.NEEDS_PRIMARY_CERTIFICATION
                                || code == Resp.Code.NEEDS_SENIOR_CERTIFICATION || code == Resp.Code.NEEDS_MORE_DEAL_COUNT || code == Resp.Code.BANK_CADR_NONE) {
                            showAlertMsgHint(code);
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
    }

    private void openOtc365(NewOrderData data) {
        ParamBean param = data.getParam();
        if (param != null) {
            Launcher.with(getActivity(), Otc365FilterWebActivity.class)
                    .putExtra(WebActivity.EX_URL, data.getTargetUrl())
                    .putExtra(WebActivity.EX_POST_DATA, createPostData(param))
                    .execute();
        }
    }

    public String createPostData(Object obj) {
        if (obj == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        Field[] fields = obj.getClass().getDeclaredFields();
        String[] types1 = {"int", "java.lang.String", "boolean", "char", "float", "double", "long", "short", "byte"};
        String[] types2 = {"Integer", "java.lang.String", "java.lang.Boolean", "java.lang.Character", "java.lang.Float", "java.lang.Double", "java.lang.Long", "java.lang.Short", "java.lang.Byte"};
        for (Field field : fields) {
            field.setAccessible(true);
            // 字段名
            String key = field.getName();
            if ("$change".equals(key) || "serialVersionUID".equals(key)) {
                continue;
            }
            builder.append(key).append("=");
            // 字段值
            for (int i = 0; i < types1.length; i++) {
                if (field.getType().getName()
                        .equalsIgnoreCase(types1[i]) || field.getType().getName().equalsIgnoreCase(types2[i])) {
                    try {
                        Object value = field.get(obj);
                        builder.append(getEncode(String.valueOf(value))).append("&");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return builder.toString();
    }

    private String getEncode(String value) {
        try {
            return URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void buy(String cost, String coinCount, String coinSymbol) {
        Apic.newOtcDestineOrder(cost, coinCount, coinSymbol).tag(TAG)
                .callback(new Callback<Resp<NewOrderData>>() {
                    @Override
                    protected void onRespSuccess(Resp<NewOrderData> resp) {
                        clearData();
                        NewOrderData data = resp.getData();
                        if (data.getOrderType() == 1) {
                            openOtc365(data);
                        } else {
                            String id = String.valueOf(data.getId());
                            UniqueActivity.launcher(SimpleOTCFragment.this, LegalCurrencyOrderDetailFragment.class)
                                    .putExtra(ExtraKeys.ORDER_ID, id)
                                    .putExtra(ExtraKeys.TRADE_DIRECTION, OTCOrderStatus.ORDER_DIRECT_BUY)
                                    .execute();
                        }
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        int code = failedResp.getCode();
                        if (code == Resp.Code.PHONE_NONE || code == Resp.Code.CASH_PWD_NONE || code == Resp.Code.NEEDS_PRIMARY_CERTIFICATION
                                || code == Resp.Code.NEEDS_SENIOR_CERTIFICATION || code == Resp.Code.NEEDS_MORE_DEAL_COUNT || code == Resp.Code.BANK_CADR_NONE) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSmartDialog != null) {
            getUserInfo();
        }
    }

    private void getUserInfo() {
        Apic.findUserInfo().tag(TAG)
                .callback(new Callback<Resp<UserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<UserInfo> resp) {
                        LocalUser.getUser().setUserInfo(resp.getData());
                        setAlertView();
                    }
                })
                .fire();
    }
}
