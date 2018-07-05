package com.songbai.futurex.fragment.mine;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.httplib.BitmapCfg;
import com.sbai.httplib.ReqCallback;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.PasswordEditText;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.AuthCodeViewController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class ChangeLoginPwdFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.password)
    PasswordEditText mPassword;
    @BindView(R.id.confirmPassword)
    PasswordEditText mConfirmPassword;
    @BindView(R.id.smsAuthCode)
    EditText mSmsAuthCode;
    @BindView(R.id.confirm)
    TextView mConfirm;
    @BindView(R.id.authCodeText)
    TextView mAuthCodeText;
    @BindView(R.id.sendAuthHint)
    TextView mSendAuthHint;
    @BindView(R.id.sendAuthCode)
    TextView mSendAuthCode;
    private Unbinder mBind;
    private UserInfo mUserInfo;
    private String mUserPhone;
    private String mUserEmail;
    private boolean mMailMode;
    private AuthCodeViewController mAuthCodeViewController;
    private boolean mFreezeGetPhoneAuthCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_login_pwd, container, false);
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
        initView();
    }

    private void initView() {
        mUserInfo = LocalUser.getUser().getUserInfo();
        mUserPhone = mUserInfo.getUserPhone();
        mUserEmail = mUserInfo.getUserEmail();
        mMailMode = TextUtils.isEmpty(mUserPhone);
        mAuthCodeText.setText(mMailMode ? R.string.email_auth_code : R.string.sms_auth_code);
        mSmsAuthCode.setHint(mMailMode ? R.string.please_input_mail_auth_code : R.string.please_input_sms_auth_code);
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
        AuthCodeGet authCodeGet = AuthCodeGet.Builder.anAuthCodeGet()
                .type(AuthCodeGet.TYPE_SAFE_PSD)
                .build();

        Apic.getAuthCode(authCodeGet).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        freezeGetPhoneAuthCodeButton();
                        mSendAuthHint.setVisibility(View.VISIBLE);
                        mSendAuthHint.setText(mMailMode ? getString(R.string.send_mail_auth_code_hint_x, mUserEmail)
                                : getString(R.string.send_sms_auth_code_phone_hint_x, mUserPhone));
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_REQUIRED
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_TIMEOUT
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_FAILED) {
                            showImageAuthCodeDialog();
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
    }

    private void freezeGetPhoneAuthCodeButton() {
        mFreezeGetPhoneAuthCode = true;
        startScheduleJobRightNow(1000);
        mSendAuthCode.setTag(60);
        mSendAuthCode.setEnabled(false);
        mSendAuthCode.setText(getString(R.string.x_seconds, 60));
    }

    @Override
    public void onTimeUp(int count) {
        Integer tag = (Integer) mSendAuthCode.getTag();
        int authCodeCounter = tag != null ? tag.intValue() : 0;
        authCodeCounter--;
        if (authCodeCounter <= 0) {
            mSendAuthCode.setEnabled(true);
            mSendAuthCode.setText(R.string.regain);
            mSendAuthCode.setTag(null);
            stopScheduleJob();
        } else {
            mSendAuthCode.setTag(authCodeCounter);
            mSendAuthCode.setText(getString(R.string.x_seconds, authCodeCounter));
        }
    }

    private void showImageAuthCodeDialog() {
        mAuthCodeViewController = new AuthCodeViewController(getContext(), new AuthCodeViewController.OnClickListener() {
            @Override
            public void onConfirmClick(String authCode) {
                requestPhoneAuthCode(authCode);
            }

            @Override
            public void onImageCodeClick(ImageView imageView) {
                requestAuthCodeImage(imageView.getWidth(), imageView.getHeight());
            }
        });

        SmartDialog.solo(getActivity())
                .setCustomViewController(mAuthCodeViewController)
                .show();

        mAuthCodeViewController.setTitle(R.string.please_input_auth_code);
        ImageView imageView = mAuthCodeViewController.getAuthCodeImage();
        requestAuthCodeImage(imageView.getLayoutParams().width, imageView.getLayoutParams().height);
    }

    private void requestPhoneAuthCode(String imageAuthCode) {
        AuthCodeGet authCodeGet = AuthCodeGet.Builder.anAuthCodeGet()
                .imgCode(imageAuthCode)
                .type(AuthCodeGet.TYPE_SAFE_PSD)
                .build();

        Apic.getAuthCode(authCodeGet).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        freezeGetPhoneAuthCodeButton();
                        mSendAuthHint.setVisibility(View.VISIBLE);
                        mSendAuthHint.setText(mMailMode ? getString(R.string.send_mail_auth_code_hint_x, mUserEmail)
                                : getString(R.string.send_sms_auth_code_phone_hint_x, mUserPhone));
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_REQUIRED
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_TIMEOUT
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_FAILED) {
                            showImageAuthCodeDialog();
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
    }

    private void requestAuthCodeImage(int width, int height) {
        Apic.getAuthCodeImage(AuthCodeGet.TYPE_SAFE_PSD).tag(TAG)
                .bitmapCfg(new BitmapCfg(width, height))
                .callback(new ReqCallback<Bitmap>() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        mAuthCodeViewController.setAuthCodeBitmap(bitmap);
                    }

                    @Override
                    public void onFailure(ReqError reqError) {
                        mAuthCodeViewController.loadImageFailure();
                    }
                }).fireFreely();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.sendAuthCode, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sendAuthCode:
                getAuthCode();
                break;
            case R.id.confirm:
                // TODO: 2018/7/5 接口不对 或者设计图不对
                String password = mPassword.getPassword();
                String confirmPassword = mConfirmPassword.getPassword();
                if (password.equals(confirmPassword)) {
                    Apic.updateLoginPass(password, password)
                            .callback(new Callback<Resp<Object>>() {
                                @Override
                                protected void onRespSuccess(Resp<Object> resp) {
                                    finish();
                                }
                            }).fire();
                }
                break;
            default:
        }
    }
}
