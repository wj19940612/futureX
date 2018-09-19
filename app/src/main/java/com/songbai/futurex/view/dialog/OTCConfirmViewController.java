package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.mine.CashPwdFragment;
import com.songbai.futurex.model.PreTradeBean;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Modified by john on 2018/6/1
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class OTCConfirmViewController extends SmartDialog.CustomViewController {

    @BindView(R.id.topTitle)
    TextView mTitle;
    @BindView(R.id.close)
    ImageView mClose;
    @BindView(R.id.trader)
    TextView mTrader;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.amount)
    TextView mAmount;
    @BindView(R.id.dealAmount)
    TextView mDealAmount;
    @BindView(R.id.fee)
    TextView mFee;
    @BindView(R.id.totalAmount)
    TextView mTotalAmount;
    @BindView(R.id.cashPwd)
    EditText mCashPwd;
    @BindView(R.id.forgetCashPwd)
    TextView mForgetCashPwd;
    @BindView(R.id.confirm)
    TextView mConfirm;
    private OnClickListener mOnClickListener;
    private Context mContext;
    private boolean showCashPwd = true;
    private PreTradeBean mData;
    private String mSelectedCoinSymbol;
    private String mSelectedLegalSymbol;
    private boolean mBuy;

    public void setData(PreTradeBean data, String selectedLegalSymbol, String selectedCoinSymbol, boolean buy) {
        mData = data;
        mSelectedLegalSymbol = selectedLegalSymbol;
        mSelectedCoinSymbol = selectedCoinSymbol;
        mBuy = buy;
        setShowCashPwd(!buy);
        setCashPwdState();
        setView(data);
    }

    private void setView(PreTradeBean data) {
        if (isViewInitialized()) {
            mTrader.setText(mContext.getString(R.string.trader_x, data.getSellerName()));
            mPrice.setText(mContext.getString(mBuy ? R.string.buy_price_x : R.string.sell_price_x, data.getFixedPrice() + mSelectedLegalSymbol.toUpperCase() + "/" + mSelectedCoinSymbol.toUpperCase()));
            mAmount.setText(mContext.getString(mBuy ? R.string.buy_amount_x : R.string.sell_amount_x, data.getCoinCount() + mSelectedCoinSymbol.toUpperCase()));
            mDealAmount.setText(mContext.getString(R.string.deal_amount_x, data.getOrderPrice() + mSelectedLegalSymbol.toUpperCase()));
            mFee.setText(mContext.getString(R.string.otc_deal_fee_x, data.getRatio() + "%"));
            mTotalAmount.setText(mContext.getString(R.string.total_deal_amount_x, data.getOrderAmount() + mSelectedLegalSymbol.toUpperCase()));
            mConfirm.setText(mBuy ? R.string.confirm_buy : R.string.confirm_sell);
            mConfirm.setEnabled(checkConfirmButtonEnable());
        }
    }

    public interface OnClickListener {
        void onForgetClick();

        void onConfirmClick(String cashPwd);
    }

    public OTCConfirmViewController(Context context, OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_otc_confirm, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onInitView(View view, final SmartDialog dialog) {
        setCashPwdState();
        mCashPwd.addTextChangedListener(mWatcher);
        mForgetCashPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onForgetClick();
                }
                UniqueActivity.launcher(mContext, CashPwdFragment.class)
                        .putExtra(ExtraKeys.HAS_WITH_DRAW_PASS, true).execute();
            }
        });
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String authCode = mCashPwd.getText().toString();
                mOnClickListener.onConfirmClick(authCode);
            }
        });
    }

    private void setCashPwdState() {
        mCashPwd.setVisibility(showCashPwd ? View.VISIBLE : View.GONE);
        mForgetCashPwd.setVisibility(showCashPwd ? View.VISIBLE : View.GONE);
    }

    private ValidationWatcher mWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean enable = checkConfirmButtonEnable();
            if (enable != mConfirm.isEnabled()) {
                mConfirm.setEnabled(enable);
            }
        }
    };

    private boolean checkConfirmButtonEnable() {
        if (showCashPwd && TextUtils.isEmpty(mCashPwd.getText().toString().trim())) {
            return false;
        }
        return true;
    }

    public void setShowCashPwd(boolean showCashPwd) {
        this.showCashPwd = showCashPwd;
    }

    public void setTitle(int titleRes) {
        if (isViewInitialized()) {
            mTitle.setText(titleRes);
        }
    }

    public void mConfirm(int titleRes) {
        if (isViewInitialized()) {
            mConfirm.setText(titleRes);
        }
    }
}
