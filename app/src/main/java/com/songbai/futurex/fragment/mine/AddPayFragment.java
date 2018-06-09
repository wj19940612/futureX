package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.songbai.futurex.model.local.BankBindData;
import com.songbai.futurex.model.local.LocalUser;
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
    @BindView(R.id.accountName)
    TextView mAccountName;
    @BindView(R.id.accountNum)
    EditText mAccountNum;
    @BindView(R.id.realName)
    EditText mRealName;
    @BindView(R.id.confirm_add)
    TextView mConfirmAdd;
    Unbinder unbinder;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    private boolean mIsAlipay;

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
        } else {
            mTitleBar.setTitle(R.string.add_wei_chat_pay);
            mAccountName.setText(R.string.wei_chat_account);
            mAccountNum.setHint(R.string.please_input_wei_chat_account);
        }
    }

    private void setUnEditable(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
    }

    private void bankBand(BankBindData bankBindData) {
        Apic.bankBind(bankBindData)
                .callback(new Callback<Object>() {
                    @Override
                    protected void onRespSuccess(Object resp) {

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
        BankBindData bankBindData = BankBindData.Builder
                .aBankBindData()
                .payType(mIsAlipay ? BankBindData.PAY_TYPE_ALIPAY : BankBindData.PAY_TYPE_WECHATPAY)
                .cardNumber(account)
                .realName(realName)
                .build();
        bankBand(bankBindData);
    }
}
