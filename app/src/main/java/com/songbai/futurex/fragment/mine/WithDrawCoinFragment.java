package com.songbai.futurex.fragment.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.AccountBean;
import com.songbai.futurex.model.mine.CoinAddress;
import com.songbai.futurex.model.mine.DrawLimit;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.ItemSelectController;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class WithDrawCoinFragment extends UniqueActivity.UniFragment {
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
    @BindView(R.id.googleAuthCodeGroup)
    LinearLayout mGoogleAuthCodeGroup;
    @BindView(R.id.googleAuthCode)
    EditText mGoogleAuthCode;
    private Unbinder mBind;
    private AccountBean mAccountBean;
    private double mWithdrawRate;
    private ArrayList<CoinAddress> mCoinAddresses;
    private boolean mNeedGoogle;
    private DrawLimit mDrawLimit;
    private SmartDialog mSmartDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw_coin, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mAccountBean = extras.getParcelable(ExtraKeys.ACCOUNT_BEAN);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        needGoogle();
        getCoinTypeDrawLimit();
        mUsableAmount.setText(getString(R.string.amount_space_coin_x,
                FinanceUtil.formatWithScale(mAccountBean.getAbleCoin(), 8), mAccountBean.getCoinType().toUpperCase()));
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
            amount = "0";
        }
        double value = Double.valueOf(amount);
        if (mDrawLimit != null) {
            double maxWithdrawAmount = mDrawLimit.getMaxWithdrawAmount();
            if (maxWithdrawAmount < value) {
                mWithDrawAmount.setText(String.valueOf(maxWithdrawAmount));
                mWithDrawAmount.setSelection(mWithDrawAmount.getText().length());
                return;
            }
        }
        double fee = mWithdrawRate;
        mFee.setText(getString(R.string.amount_space_coin_x, FinanceUtil.formatWithScale(fee, 8), mAccountBean.getCoinType().toUpperCase()));
        mResultAmount.setText(getString(R.string.amount_space_coin_x,
                FinanceUtil.formatWithScale(FinanceUtil.subtraction(value, fee).doubleValue(), 8), mAccountBean.getCoinType().toUpperCase()));
    }

    private void needGoogle() {
        Apic.needGoogle("DRAW").tag(TAG)
                .callback(new Callback<Resp<Boolean>>() {
                    @Override
                    protected void onRespSuccess(Resp<Boolean> resp) {
                        mNeedGoogle = resp.getData();
                        mGoogleAuthCodeGroup.setVisibility(mNeedGoogle ? View.VISIBLE : View.GONE);
                    }
                })
                .fire();
    }

    private void getCoinTypeDrawLimit() {
        Apic.getCoinTypeDrawLimit(mAccountBean.getCoinType()).tag(TAG)
                .callback(new Callback<Resp<DrawLimit>>() {
                    @Override
                    protected void onRespSuccess(Resp<DrawLimit> resp) {
                        mDrawLimit = resp.getData();
                        mWithdrawRate = mDrawLimit.getWithdrawRate();
                        setLimit(mDrawLimit);
                    }
                })
                .fire();
    }

    private void setLimit(DrawLimit drawLimit) {
        String minWithDrawAmountStr = FinanceUtil.subZeroAndDot(drawLimit.getMinWithdrawAmount(), 8);
        mFee.setText(getString(R.string.amount_space_coin_x, FinanceUtil.formatWithScale(drawLimit.getWithdrawRate(), 8), mAccountBean.getCoinType().toUpperCase()));
        mWithDrawAmount.setHint(getString(R.string.min_draw_amount_coin_x, minWithDrawAmountStr, mAccountBean.getCoinType().toUpperCase()));
        mWithDrawRules.setText(getString(R.string.with_draw_rules_x, minWithDrawAmountStr, drawLimit.getConfirm()));
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
                getDrawWalletAddrByCoinType(mAccountBean.getCoinType());
                break;
            case R.id.drawAll:
                mWithDrawAmount.setText(FinanceUtil.formatWithScale(mAccountBean.getAbleCoin(), 8));
                break;
            case R.id.confirmDraw:
                drawCoin(mAccountBean.getCoinType(),
                        mEtWithDrawAddress.getText().toString(),
                        Double.valueOf(mWithDrawAmount.getText().toString()),
                        mNeedGoogle ? mGoogleAuthCode.getText().toString() : "",
                        md5Encrypt(mCashPwd.getText().toString()));
                break;
            default:
        }
    }

    private void drawCoin(String coinType, String toAddr, double withdrawAmount, String googleCode, String drawPassword) {
        Apic.drawCoin(coinType, toAddr, withdrawAmount, googleCode, drawPassword).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        setResult(FundsTransferFragment.FUNDS_TRANSFER_RESULT,
                                new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
                        ToastUtil.show(R.string.draw_coin_success);
                        finish();
                    }
                })
                .fire();
    }

    private void getDrawWalletAddrByCoinType(String coinType) {
        Apic.getDrawWalletAddrByCoinType(coinType).tag(TAG)
                .callback(new Callback<Resp<ArrayList<CoinAddress>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<CoinAddress>> resp) {
                        mCoinAddresses = resp.getData();
                        if (mCoinAddresses.size() > 1) {
                            showAddressPicker();
                        } else {
                            ToastUtil.show(R.string.no_draw_coin_address);
                        }
                    }
                })
                .fire();
    }

    private void showAddressPicker() {
        if (mCoinAddresses != null && mCoinAddresses.size() > 0) {
            ItemSelectController addressSelector = new ItemSelectController(getContext());
            DrawCoinAddressListAdapter adapter = new DrawCoinAddressListAdapter();
            adapter.setList(mCoinAddresses);
            adapter.setOnItemClickListener(new DrawCoinAddressListAdapter.OnItemClickListener() {
                @Override
                public void onItemLongClick(CoinAddress coinAddress) {
                    setSelectedAddress(coinAddress);
                }
            });
            addressSelector.setAdapter(adapter);
            mSmartDialog = SmartDialog.solo(getActivity());
            mSmartDialog
                    .setWidthScale(1)
                    .setWindowGravity(Gravity.BOTTOM)
                    .setWindowAnim(R.style.BottomDialogAnimation)
                    .setCustomViewController(addressSelector)
                    .show();
            addressSelector.setHintText(getString(R.string.select_draw_coin_address));
        }
    }

    private void setSelectedAddress(CoinAddress coinAddress) {
        mEtWithDrawAddress.setText(coinAddress.getToAddr());
        mEtWithDrawAddress.setSelection(mEtWithDrawAddress.getText().length());
        if (mSmartDialog != null) {
            mSmartDialog.dismiss();
        }
    }

    static class DrawCoinAddressListAdapter extends RecyclerView.Adapter {

        private ArrayList<CoinAddress> mList = new ArrayList<>();
        private static OnItemClickListener sOnItemClickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_draw_coin_address, parent, false);
            return new DrawCoinAddressHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof DrawCoinAddressHolder) {
                ((DrawCoinAddressHolder) holder).bindHolder(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(ArrayList<CoinAddress> list) {
            mList.clear();
            mList.addAll(list);
        }

        public void removeItem(CoinAddress coinAddress) {
            mList.remove(coinAddress);
        }

        interface OnItemClickListener {
            void onItemLongClick(CoinAddress coinAddress);
        }

        void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            sOnItemClickListener = onItemClickListener;
        }

        static class DrawCoinAddressHolder extends RecyclerView.ViewHolder {
            private View mRootView;
            @BindView(R.id.remark)
            TextView mRemark;
            @BindView(R.id.toAddress)
            TextView mToAddress;

            DrawCoinAddressHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                mRootView = view;
            }

            void bindHolder(final CoinAddress coinAddress) {
                mRemark.setText(coinAddress.getRemark());
                mToAddress.setText(coinAddress.getToAddr());
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sOnItemClickListener != null) {
                            sOnItemClickListener.onItemLongClick(coinAddress);
                        }
                    }
                });
            }
        }
    }
}
