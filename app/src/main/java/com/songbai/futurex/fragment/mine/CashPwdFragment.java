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
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.AuthSendOld;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.PasswordEditText;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.AuthCodeViewController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class CashPwdFragment extends UniqueActivity.UniFragment {
    private static final String TAG = CashPwdFragment.class.getSimpleName();
    private static final String SET_CASH_AUTH_TYPE_PHONE = "0";
    private static final String SET_CASH_AUTH_TYPE_MAIL = "1";
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
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
    @BindView(R.id.smsAuthCodeText)
    TextView mSmsAuthCodeText;
    @BindView(R.id.googleAuthCodeText)
    TextView mGoogleAuthCodeText;
    @BindView(R.id.googleAuthCode)
    EditText mGoogleAuthCode;
    @BindView(R.id.sendAuthCode)
    TextView mSendAuthCode;
    private Unbinder mBind;
    private String mUserPhone;
    private String mUserEmail;
    private UserInfo mUserInfo;
    private boolean mMailMode;
    private AuthCodeViewController mAuthCodeViewController;
    private boolean mNeedGoogle;
    private boolean mFreezeGetPhoneAuthCode;
    private boolean mHsaWithDrawPass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_pwd, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mHsaWithDrawPass = extras.getBoolean(ExtraKeys.HAS_WITH_DRAW_PASS);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mPassword.addTextChangedListener(mTextWatcher);
        mConfirmPassword.addTextChangedListener(mTextWatcher);
        mSmsAuthCode.addTextChangedListener(mTextWatcher);
        initView();
        needGoogle();
    }

    private void initView() {
        mTitleBar.setTitle(mHsaWithDrawPass ? R.string.change_cash_pwd : R.string.set_cash_pwd);
        mUserInfo = LocalUser.getUser().getUserInfo();
        if (mUserInfo != null) {
            mUserPhone = mUserInfo.getUserPhone();
            mUserEmail = mUserInfo.getUserEmail();
        }
        mMailMode = TextUtils.isEmpty(mUserPhone);
        mSmsAuthCodeText.setText(mMailMode ? R.string.email_auth_code : R.string.sms_auth_code);
        mSmsAuthCode.setHint(mMailMode ? R.string.please_input_mail_auth_code : R.string.please_input_sms_auth_code);
    }

    private ValidationWatcher mTextWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String password = mPassword.getPassword();
            String confirmPassword = mConfirmPassword.getPassword();
            String authCode = mSmsAuthCode.getText().toString().trim();
            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(authCode)) {
                mConfirm.setEnabled(false);
            } else {
                mConfirm.setEnabled(true);
            }
        }
    };

    private void needGoogle() {
        Apic.needGoogle("SET_DRAW_PASS").tag(TAG)
                .callback(new Callback<Resp<Boolean>>() {

                    @Override
                    protected void onRespSuccess(Resp<Boolean> resp) {
                        mNeedGoogle = resp.getData();
                        mGoogleAuthCodeText.setVisibility(mNeedGoogle ? View.VISIBLE : View.GONE);
                        mGoogleAuthCode.setVisibility(mNeedGoogle ? View.VISIBLE : View.GONE);
                    }
                })
                .fireFreely();
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
        AuthSendOld authSendOld = new AuthSendOld();
        authSendOld.setImgCode(imageAuthCode);
        authSendOld.setSmsType(AuthCodeGet.TYPE_SAFE_PSD);
        authSendOld.setSendType(AuthSendOld.TYPE_DEFAULT);
        Apic.sendOld(authSendOld).tag(TAG)
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

    private void setDrawPass(String drawPass, String affirmPass, String msgCode, String type, String googleCode) {
        Apic.setDrawPass(drawPass, affirmPass, msgCode, type, googleCode).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        ToastUtil.show(R.string.set_cash_pwd_success);
                        LocalUser user = LocalUser.getUser();
                        if (user.isLogin()) {
                            UserInfo userInfo = user.getUserInfo();
                            if (userInfo.getSafeSetting() == 0) {
                                userInfo.setSafeSetting(1);
                                LocalUser.getUser().setUserInfo(userInfo);
                            }
                        }
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

    @OnClick({R.id.sendAuthCode, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sendAuthCode:
                requestPhoneAuthCode("");
                break;
            case R.id.confirm:
                String password = mPassword.getPassword();
                String confirmPassword = mConfirmPassword.getPassword();
                String msgCode = mSmsAuthCode.getText().toString();
                if (!password.equals(confirmPassword)) {
                    ToastUtil.show(R.string.the_two_passwords_differ);
                    return;
                }
                if (password.length()<8) {
                    ToastUtil.show(R.string.draw_cash_pwd_can_not_short_than_8);
                    return;
                }
                if (mNeedGoogle && TextUtils.isEmpty(mGoogleAuthCode.getText().toString())) {
                    return;
                }
                setDrawPass(md5Encrypt(password), md5Encrypt(confirmPassword), msgCode,
                        mMailMode ? SET_CASH_AUTH_TYPE_MAIL : SET_CASH_AUTH_TYPE_PHONE,
                        mNeedGoogle ? mGoogleAuthCode.getText().toString() : "");
            default:
        }
    }
}
