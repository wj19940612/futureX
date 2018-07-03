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
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.AuthSendOld;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
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
public class BindMailFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.mailAuthCode)
    EditText mMailAuthCode;
    @BindView(R.id.getMailAuthCode)
    TextView mGetMailAuthCode;
    @BindView(R.id.smsAuthCode)
    EditText mSmsAuthCode;
    @BindView(R.id.getSmsAuthCode)
    TextView mGetSmsAuthCode;
    @BindView(R.id.confirmBind)
    TextView mConfirmBind;
    @BindView(R.id.email)
    EditText mEmail;
    private Unbinder mBind;
    private AuthCodeViewController mAuthCodeViewController;
    private boolean mHasBindEmail;
    private boolean mFreezeGetEmailAuthCode;
    private boolean mFreezeGetPhoneAuthCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bind_mail, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mHasBindEmail = extras.getBoolean(ExtraKeys.HAS_BIND_EMAIL, false);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        if (mHasBindEmail) {
            mTitleBar.setTitle(R.string.modify_mail);
            mConfirmBind.setText(R.string.confirm_modify);
            mSmsAuthCode.setHint(R.string.please_input_used_mail_auth_code);
        } else {
            mTitleBar.setTitle(R.string.bind_mail);
            mConfirmBind.setText(R.string.confirm_bind);
        }
        mEmail.addTextChangedListener(mWatcher);
        mSmsAuthCode.addTextChangedListener(mWatcher);
        mMailAuthCode.addTextChangedListener(mWatcher);
    }

    private ValidationWatcher mWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean enable = checkConfirmButtonEnable();
            mConfirmBind.setEnabled(enable);
        }
    };

    private boolean checkConfirmButtonEnable() {
        String email = this.mEmail.getText().toString();
        String mailAuthCode = mMailAuthCode.getText().toString();
        String smsAuthCode = mSmsAuthCode.getText().toString();
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(mailAuthCode) && !TextUtils.isEmpty(smsAuthCode);
    }

    @OnClick({R.id.getMailAuthCode, R.id.getSmsAuthCode, R.id.confirmBind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getMailAuthCode:
                String email = mEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    return;
                }
                getEmailAuthCode(email);
                break;
            case R.id.getSmsAuthCode:
                sendOld("");
                break;
            case R.id.confirmBind:
                bindMail(mEmail.getText().toString(), mMailAuthCode.getText().toString(), mSmsAuthCode.getText().toString());
                break;
            default:
        }
    }

    private void getEmailAuthCode(String email) {
        AuthCodeGet authCodeGet = AuthCodeGet.Builder.anAuthCodeGet()
                .email(email)
                .type(AuthCodeGet.BINDING_EMAIL)
                .build();
        Apic.getAuthCode(authCodeGet)
                .callback(new Callback<Object>() {
                    @Override
                    protected void onRespSuccess(Object resp) {
                        freezeGetEmailAuthCodeButton();
                    }
                })
                .fire();
    }

    private void freezeGetEmailAuthCodeButton() {
        mFreezeGetEmailAuthCode = true;
        startScheduleJobRightNow(1000);
        mGetMailAuthCode.setTag(60);
        mGetMailAuthCode.setEnabled(false);
        mGetMailAuthCode.setText(getString(R.string.x_seconds, 60));
    }

    private void freezeGetPhoneAuthCodeButton() {
        mFreezeGetPhoneAuthCode = true;
        startScheduleJobRightNow(1000);
        mGetSmsAuthCode.setTag(60);
        mGetSmsAuthCode.setEnabled(false);
        mGetSmsAuthCode.setText(getString(R.string.x_seconds, 60));
    }

    @Override
    public void onTimeUp(int count) {
        boolean timeUp = false;
        if (mFreezeGetPhoneAuthCode) {
            Integer tag = (Integer) mGetSmsAuthCode.getTag();
            int authCodeCounter = tag != null ? tag.intValue() : 0;
            authCodeCounter--;
            if (authCodeCounter <= 0) {
                mGetSmsAuthCode.setEnabled(true);
                mGetSmsAuthCode.setText(R.string.regain);
                mGetSmsAuthCode.setTag(null);
                timeUp = true;
            } else {
                mGetSmsAuthCode.setTag(authCodeCounter);
                mGetSmsAuthCode.setText(getString(R.string.x_seconds, authCodeCounter));
            }
        }
        if (mFreezeGetEmailAuthCode) {
            Integer tag = (Integer) mGetMailAuthCode.getTag();
            int authCodeCounter = tag != null ? tag.intValue() : 0;
            authCodeCounter--;
            if (authCodeCounter <= 0) {
                mGetMailAuthCode.setEnabled(true);
                mGetMailAuthCode.setText(R.string.regain);
                mGetMailAuthCode.setTag(null);
                timeUp = true;
            } else {
                mGetMailAuthCode.setTag(authCodeCounter);
                mGetMailAuthCode.setText(getString(R.string.x_seconds, authCodeCounter));
            }
        }
        if (timeUp) {
            stopScheduleJob();
        }
    }

    private void showImageAuthCodeDialog() {
        mAuthCodeViewController = new AuthCodeViewController(getContext(), new AuthCodeViewController.OnClickListener() {
            @Override
            public void onConfirmClick(String authCode) {
                sendOld(authCode);
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

    private void sendOld(String imageAuthCode) {
        AuthSendOld authSendOld = new AuthSendOld();
        authSendOld.setImgCode(imageAuthCode);
        authSendOld.setSmsType(AuthCodeGet.BINDING_EMAIL);
        authSendOld.setSendType(mHasBindEmail ? AuthSendOld.TYPE_MAIL : AuthSendOld.TYPE_SMS);

        Apic.sendOld(authSendOld).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        freezeGetPhoneAuthCodeButton();
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
        Apic.getAuthCodeImage(AuthCodeGet.BINDING_EMAIL).tag(TAG)
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

    private void bindMail(String email, String emailMsgCode, String phoneMsgCode) {
        Apic.bindEmail(email, emailMsgCode, phoneMsgCode)
                .callback(new Callback<Object>() {
                    @Override
                    protected void onRespSuccess(Object resp) {
                        ToastUtil.show(R.string.bind_success);
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
}
