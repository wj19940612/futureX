package com.songbai.futurex.fragment.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import com.songbai.futurex.model.local.BankBindData;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.BankCardBean;
import com.songbai.futurex.model.mine.BindBankList;
import com.songbai.futurex.view.PasswordEditText;
import com.songbai.futurex.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/6/4
 */
public class AddPayFragment extends UniqueActivity.UniFragment {
    public static final int ADD_PAY_RESULT = 12343;

    @BindView(R.id.accountName)
    TextView mAccountName;
    @BindView(R.id.accountNum)
    EditText mAccountNum;
    @BindView(R.id.realName)
    EditText mRealName;
    @BindView(R.id.confirm_add)
    TextView mConfirmAdd;
    @BindView(R.id.withDrawPass)
    PasswordEditText mWithDrawPass;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    Unbinder unbinder;
    private boolean mIsAlipay;
    private String mName;
    private BindBankList mBindBankList;
    private boolean mHasBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_pay, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mIsAlipay = extras.getBoolean(ExtraKeys.IS_ALIPAY);
        mBindBankList = extras.getParcelable(ExtraKeys.BIND_BANK_LIST);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        LocalUser user = LocalUser.getUser();
        if (user.isLogin()) {
            String realName = user.getUserInfo().getRealName();
            if (!TextUtils.isEmpty(realName)) {
                mRealName.setText(realName);
                setUnEditable(mRealName);
            }
        }
        if (mIsAlipay) {
            mTitleBar.setTitle(R.string.add_ali_pay);
            mAccountName.setText(R.string.ali_pay_account);
            mAccountNum.setHint(R.string.please_input_ali_pay_account);
            if (mBindBankList != null) {
                String cardNumber = mBindBankList.getAliPay().getCardNumber();
                if (!TextUtils.isEmpty(cardNumber)) {
                    mTitleBar.setTitle(R.string.edit);
                    mHasBind = true;
                }
                mAccountNum.setText(mBindBankList.getAliPay().getCardNumber());
                otcBankAccount(mBindBankList.getAliPay().getId());
            }
        } else {
            mTitleBar.setTitle(R.string.add_wei_chat_pay);
            mAccountName.setText(R.string.wei_chat_account);
            mAccountNum.setHint(R.string.please_input_wei_chat_account);
            if (mBindBankList != null) {
                String cardNumber = mBindBankList.getWechat().getCardNumber();
                if (!TextUtils.isEmpty(cardNumber)) {
                    mTitleBar.setTitle(R.string.edit);
                    mHasBind = true;
                }
                mAccountNum.setText(cardNumber);
                otcBankAccount(mBindBankList.getWechat().getId());
            }
        }
    }

    private void setUnEditable(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
    }

    private void otcBankAccount(int id) {
        Apic.otcBankAccount(id).tag(TAG)
                .callback(new Callback<Resp<BankCardBean>>() {
                    @Override
                    protected void onRespSuccess(Resp<BankCardBean> resp) {
                        BankCardBean bankCardBean = resp.getData();
                        if (bankCardBean != null) {
                            mName = bankCardBean.getRealName();
                            mRealName.setText(mName);
                            mAccountNum.setText(bankCardBean.getCardNumber());
                        }
                    }
                })
                .fire();
    }

    private void bankBand(BankBindData bankBindData) {
        Apic.bankBind(bankBindData).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        FragmentActivity activity = AddPayFragment.this.getActivity();
                        activity.setResult(ADD_PAY_RESULT, new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
                        activity.finish();
                    }
                })
                .fireFreely();
    }

    private void updateBankAccount(String type, String account, String name, String withDrawPass) {
        Apic.updateBankAccount(type, account, name, withDrawPass).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        setResult(ADD_PAY_RESULT, new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
                        finish();
                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.confirm_add)
    public void onViewClicked() {
        String account = mAccountNum.getText().toString();
        String realName = mRealName.getText().toString();
        if (mHasBind) {
            updateBankAccount(mIsAlipay ? BankBindData.PAY_TYPE_ALIPAY : BankBindData.PAY_TYPE_WECHATPAY,
                    account,
                    mName,
                    md5Encrypt(mWithDrawPass.getPassword()));
        } else {
            BankBindData bankBindData = BankBindData.Builder
                    .aBankBindData()
                    .payType(mIsAlipay ? BankBindData.PAY_TYPE_ALIPAY : BankBindData.PAY_TYPE_WECHATPAY)
                    .cardNumber(account)
                    .realName(mName)
                    .withDrawPass(md5Encrypt(mWithDrawPass.getPassword()))
                    .build();
            bankBand(bankBindData);
        }
    }
}
