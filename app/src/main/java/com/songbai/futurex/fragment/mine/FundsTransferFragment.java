package com.songbai.futurex.fragment.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.AccountList;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class FundsTransferFragment extends UniqueActivity.UniFragment {
    public static final int FUNDS_TRANSFER_RESULT = 112;
    @BindView(R.id.fromAccount)
    TextView mFromAccount;
    @BindView(R.id.ivArrow)
    ImageView mIvArrow;
    @BindView(R.id.toAccount)
    TextView mToAccount;
    @BindView(R.id.coinType)
    TextView mCoinType;
    @BindView(R.id.transferAmount)
    EditText mTransferAmount;
    @BindView(R.id.transferAll)
    TextView mTransferAll;
    @BindView(R.id.confirmTransfer)
    TextView mConfirmTransfer;
    private Unbinder mBind;
    private List<AccountList.AccountBean> mAccountBeans;
    private OptionsPickerView mPvOptions;
    private int mTransferType;
    private AccountList.AccountBean mSelectedAccountBean;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_funds_transfer, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mTransferType = extras.getInt(ExtraKeys.TRANSFER_TYPE);
        mAccountBeans = extras.getParcelableArrayList(ExtraKeys.ACCOUNT_BEANS);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        setView();
    }

    private void setView() {
        mFromAccount.setText(mTransferType == 0 ? R.string.coin_coin_account : R.string.legal_currency_account);
        mToAccount.setText(mTransferType == 1 ? R.string.coin_coin_account : R.string.legal_currency_account);
        setSelectedItem(0);
    }

    private void setSelectedItem(int options1) {
        mSelectedAccountBean = mAccountBeans.get(options1);
        double ableCoin = mSelectedAccountBean.getAbleCoin();
        mConfirmTransfer.setEnabled(ableCoin > 0);
        mCoinType.setText(mSelectedAccountBean.getCoinType().toUpperCase());
        mTransferAmount.setHint(getString(R.string.most_transfer_amount_type_x,
                FinanceUtil.formatWithScale(ableCoin),
                mSelectedAccountBean.getCoinType().toUpperCase()));
        fixAmount();
    }

    private void fixAmount() {
        String amount = mTransferAmount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            return;
        }
        if (Double.valueOf(amount) > Double.valueOf(mSelectedAccountBean.getAbleCoin())) {
            mTransferAmount.setText("");
        }
    }

    private void accountTransfer(String coinType, int type, String count) {
        Apic.accountTransfer(coinType, type, count)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        ToastUtil.show(R.string.transfer_success);
                        setResult(FundsTransferFragment.FUNDS_TRANSFER_RESULT,
                                new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
                        finish();
                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.ivArrow, R.id.coinType, R.id.transferAll, R.id.confirmTransfer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivArrow:
                if (mTransferType == 0) {
                    mTransferType = 1;
                } else {
                    mTransferType = 0;
                }
                setView();
                break;
            case R.id.coinType:
                if (mAccountBeans.size() > 1) {
                    showSelector();
                }
                break;
            case R.id.transferAll:
                mTransferAmount.setText(FinanceUtil.formatWithScale(mSelectedAccountBean.getAbleCoin(), 8));
                mTransferAmount.setSelection(mTransferAmount.getText().length());
                break;
            case R.id.confirmTransfer:
                accountTransfer(mSelectedAccountBean.getCoinType(), mTransferType == 0 ? 3 : 4, mTransferAmount.getText().toString());
                break;
            default:
        }
    }

    private void showSelector() {
        ArrayList<String> coinTypes = new ArrayList<>();
        for (AccountList.AccountBean accountBean : mAccountBeans) {
            coinTypes.add(accountBean.getCoinType().toUpperCase());
        }
        mPvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                setSelectedItem(options1);
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
        mPvOptions.setPicker(coinTypes, null, null);
        mPvOptions.show();
    }
}
