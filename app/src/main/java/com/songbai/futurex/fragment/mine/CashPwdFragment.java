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

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.PasswordEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class CashPwdFragment extends UniqueActivity.UniFragment {
    private static final String SET_CACH_AUTH_TYPE_PHONE = "0";
    private static final String SET_CACH_AUTH_TYPE_MAIL = "1";
    @BindView(R.id.password)
    PasswordEditText mPassword;
    @BindView(R.id.confirmPassword)
    PasswordEditText mConfirmPassword;
    @BindView(R.id.smsAuthCode)
    EditText mSmsAuthCode;
    @BindView(R.id.confirm)
    TextView mConfirm;
    @BindView(R.id.sendAuthHint)
    TextView mSendAuthHint;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_pwd, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mPassword.addTextChangedListener(mTextWatcher);
        mConfirmPassword.addTextChangedListener(mTextWatcher);
        mSmsAuthCode.addTextChangedListener(mTextWatcher);
    }

    private ValidationWatcher mTextWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String password = mPassword.getPassword();
            String confirmPassword = mConfirmPassword.getPassword();
            String authCode = mSmsAuthCode.getText().toString();
            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(authCode)) {
                mConfirm.setEnabled(false);
            } else {
                mConfirm.setEnabled(true);
            }
        }
    };

    private void getAuthCode() {
        // TODO: 2018/6/6 手机/邮箱 用户是否有手机 有则手机 无则邮箱
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        AuthCodeGet authCodeGet = AuthCodeGet.Builder.anAuthCodeGet()
                .phone(userInfo.getUserPhone())
                .teleCode(userInfo.getTeleCode())
                .type(AuthCodeGet.TYPE_REGISTER)
                .build();

        Apic.getAuthCode(authCodeGet).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
//                        freezeGetPhoneAuthCodeButton();
                        mSendAuthHint.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_REQUIRED
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_TIMEOUT
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_FAILED) {
                            // TODO: 2018/6/6
//                            showImageAuthCodeDialog();
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
    }

    private void setDrawPass(String drawPass, String affirmPass, String msgCode, String type) {
        Apic.setDrawPass(drawPass, affirmPass, msgCode, type)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {

                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.sendSmsAuthCode, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sendSmsAuthCode:
                getAuthCode();
                break;
            case R.id.confirm:
                String password = mPassword.getPassword();
                String confirmPassword = mConfirmPassword.getPassword();
                String msgCode = mSmsAuthCode.getText().toString();
                if (!password.equals(confirmPassword)) {
                    ToastUtil.show(R.string.the_two_passwords_differ);
                    return;
                }
                setDrawPass(password, confirmPassword, msgCode, SET_CACH_AUTH_TYPE_PHONE);
            default:
        }
    }
}
