package com.songbai.futurex.fragment.mine;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
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
import com.songbai.futurex.model.local.LocalUser;
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
            mTitleBar.setTitle(R.string.unbind_mail);
            mConfirmBind.setText(R.string.confirm_unbind);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
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
                getSmsAuthCode();
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

                    }
                })
                .fire();
    }

    private void getSmsAuthCode() {
        AuthCodeGet authCodeGet = AuthCodeGet.Builder.anAuthCodeGet()
                .phone(LocalUser.getUser().getLastAct())
                .type(AuthCodeGet.TYPE_SAFE_PSD)
                .build();
        Apic.getAuthCode(authCodeGet)
                .callback(new Callback<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {

                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_REQUIRED
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_TIMEOUT
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_FAILED) {
                            Log.e("wtf", "adsfdsf");
                            showImageAuthCodeDialog();
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                })
                .fire();
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

    // TODO: 2018/6/9 有疑问
    private void requestPhoneAuthCode(String imageAuthCode) {
        AuthCodeGet authCodeGet = AuthCodeGet.Builder.anAuthCodeGet()
                .imgCode(imageAuthCode)
                .type(AuthCodeGet.TYPE_SAFE_PSD)
                .build();

        Apic.getAuthCode(authCodeGet).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
//                        freezeGetPhoneAuthCodeButton();
//                        mSendAuthHint.setVisibility(View.VISIBLE);
//                        mSendAuthHint.setText(mMailMode ? getString(R.string.send_mail_auth_code_hint_x, mUserEmail)
//                                : getString(R.string.send_sms_auth_code_phone_hint_x, mUserPhone));
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

    // TODO: 2018/6/9 接口有问题
    private void bindMail(String email, String emailMsgCode, String phoneMsgCode) {
        Apic.bindEmail(email, emailMsgCode, phoneMsgCode)
                .callback(new Callback<Object>() {
                    @Override
                    protected void onRespSuccess(Object resp) {

                    }
                })
                .fire();
    }
}
