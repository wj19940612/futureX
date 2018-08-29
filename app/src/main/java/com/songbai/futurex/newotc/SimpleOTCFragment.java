package com.songbai.futurex.newotc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.LegalCurrencyOrderActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.NewOTCPrice;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.BadgeTextView;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/8/27
 */
public class SimpleOTCFragment extends BaseFragment {
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
    private int mTradeType = 1;
    private String mSelectedCoinSymbol = "usdt";
    private String mSelectedLegalSymbol = "cny";
    private NewOTCPrice mNewOTCPrice;
    private boolean mPrepared;

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

    private void setAmountAndTurnover() {
        String turnover = mTurnover.getText().toString().trim();
        String amount = mTradeAmount.getText().toString().trim();
        if (mTurnover.isFocused()) {
            double value = 0;
            if (!TextUtils.isEmpty(turnover)) {
                value = Double.valueOf(turnover);
            }
            double calAmount = value / (mTradeType == 1 ? mNewOTCPrice.getBuyPrice() : mNewOTCPrice.getSellPrice());
            mTradeAmount.setText(calAmount == 0 ? "" : String.valueOf(calAmount));
        } else if (mTradeAmount.isFocused()) {
            double value = 0;
            if (!TextUtils.isEmpty(amount)) {
                value = Double.valueOf(amount);
            }
            double calTurnover = value * (mTradeType == 1 ? mNewOTCPrice.getBuyPrice() : mNewOTCPrice.getSellPrice());
            mTurnover.setText(calTurnover == 0 ? "" : String.valueOf(calTurnover));
        }
        mTradeAmount.setFocusable(true);
        mTurnover.setFocusable(true);
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
        mTradeAmount.addTextChangedListener(mConfirmEnableWatcher);
        mTurnover.addTextChangedListener(mConfirmEnableWatcher);
        mTradeAmount.setOnTouchListener(mListener);
        mTurnover.setOnTouchListener(mListener);
        mRadioHeader.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                clearData();
                if (position == 0) {
                    mTradeType = 1;
                } else {
                    mTradeType = 2;
                }
                setView();
                setPrice();
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
        mConfirm.setEnabled(!TextUtils.isEmpty(turnover) && !TextUtils.isEmpty(tradeAmount));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            getPrice();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mPrepared) {
            getPrice();
        }
    }

    private void getPrice() {
        Apic.newOtcGetPrice().tag(TAG)
                .callback(new Callback<Resp<NewOTCPrice>>() {
                    @Override
                    protected void onRespSuccess(Resp<NewOTCPrice> resp) {
                        mNewOTCPrice = resp.getData();
                        setPrice();
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
        mTradePriceText.setText(mTradeType == 1 ? R.string.buy_in_price : R.string.sell_out_price);
        mTradeAmountText.setText(mTradeType == 1 ? R.string.buy_in_amount : R.string.sell_out_amount);
        mCoinSymbol.setText(mSelectedCoinSymbol.toUpperCase());
        mTurnoverSymbol.setText(mSelectedLegalSymbol.toUpperCase());
        mConfirm.setText(mTradeType == 1 ? R.string.buy_in : R.string.sell_out);
        mConfirm.setBackgroundResource(mTradeType == 1 ? R.drawable.btn_primary : R.drawable.btn_red);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.recentOrderHint, R.id.order, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recentOrderHint:

                break;
            case R.id.order:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(this, LegalCurrencyOrderActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
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

    private void trade() {
        if (mTradeType == 1) {
            buy(mTradeAmount.getText().toString(), mTurnover.getText().toString(), mSelectedLegalSymbol);
        } else {
            sell(mTurnover.getText().toString(), mSelectedLegalSymbol);
        }
    }

    private void sell(String coinCount, String coinSymbol) {
        Apic.newOtcSell(coinCount, coinSymbol,"").tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        clearData();
                    }
                }).fire();
    }

    private void buy(String cost, String coinCount, String coinSymbol) {
        Apic.newOtcDestineOrder(cost, coinCount, coinSymbol).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        clearData();
                    }
                }).fire();
    }

    private void clearData() {
        mTradeAmount.setText("");
        mTurnover.setText("");
    }
}
