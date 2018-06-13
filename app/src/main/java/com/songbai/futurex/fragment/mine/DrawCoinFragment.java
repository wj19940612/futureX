package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.AccountList;
import com.songbai.futurex.model.mine.DrawLimit;
import com.songbai.futurex.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class DrawCoinFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.etWithDrawAddress)
    EditText mEtWithDrawAddress;
    @BindView(R.id.withDrawAmount)
    EditText mWithDrawAmount;
    @BindView(R.id.cashPwd)
    EditText mCashPwd;
    @BindView(R.id.confirmDraw)
    TextView mConfirmDraw;
    @BindView(R.id.usableAmount)
    TextView mUsableAmount;
    @BindView(R.id.fee)
    TextView mFee;
    @BindView(R.id.resultAmount)
    TextView mResultAmount;
    @BindView(R.id.withDrawRules)
    TextView mWithDrawRules;
    private Unbinder mBind;
    private AccountList.AccountBean mAccountBean;
    private double mWithdrawRate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_draw_coin, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mAccountBean = extras.getParcelable(ExtraKeys.ACCOUNT_BEAN);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        getCoinTypeDrawLimit();
        mUsableAmount.setText(getString(R.string.amount_space_coin_x, mAccountBean.getAbleCoin(), mAccountBean.getCoinType().toUpperCase()));
        mWithDrawAmount.addTextChangedListener(mWatcher);
    }

    private ValidationWatcher mWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            setFeeAndResult();
        }
    };

    private void setFeeAndResult() {
        String amount = mWithDrawAmount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            amount="0";
        }
        Double value = Double.valueOf(amount);
        double fee = value * mWithdrawRate;
        mFee.setText(getString(R.string.amount_space_coin_x, String.valueOf(fee), mAccountBean.getCoinType().toUpperCase()));
        mResultAmount.setText(getString(R.string.amount_space_coin_x, String.valueOf(value - fee), mAccountBean.getCoinType().toUpperCase()));
    }

    private void getCoinTypeDrawLimit() {
        Apic.getCoinTypeDrawLimit(mAccountBean.getCoinType())
                .callback(new Callback<Resp<DrawLimit>>() {
                    @Override
                    protected void onRespSuccess(Resp<DrawLimit> resp) {
                        DrawLimit drawLimit = resp.getData();
                        mWithdrawRate = drawLimit.getWithdrawRate();
                        setLimit(drawLimit);
                    }
                })
                .fire();
    }

    private void setLimit(DrawLimit drawLimit) {
        mWithDrawAmount.setHint(getString(R.string.min_draw_amount_coin_x, String.valueOf(drawLimit.getMinWithdrawAmount()), mAccountBean.getCoinType().toUpperCase()));
        mWithDrawRules.setText(getString(R.string.with_draw_rules_x, String.valueOf(drawLimit.getMinWithdrawAmount())));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.withDrawAddress, R.id.drawAll, R.id.confirmDraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.withDrawAddress:
                break;
            case R.id.drawAll:
                break;
            case R.id.confirmDraw:
                break;
            default:
        }
    }
}
