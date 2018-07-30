package com.songbai.futurex.fragment.mine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import com.songbai.futurex.model.local.AuthSendOld;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.CreateGoogleKey;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.utils.ZXingUtils;
import com.songbai.futurex.utils.image.ImageUtils;
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
public class GoogleAuthenticatorFragment extends UniqueActivity.UniFragment {
    private static final int AUTH = 1;

    @BindView(R.id.qcCode)
    ImageView mQcCode;
    @BindView(R.id.secretKey)
    TextView mSecretKey;
    @BindView(R.id.googleAuthCode)
    EditText mGoogleAuthCode;
    @BindView(R.id.confirm)
    TextView mConfirm;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.authCodeText)
    TextView mAuthCodeText;
    @BindView(R.id.authCode)
    EditText mAuthCode;
    @BindView(R.id.getAuthCode)
    TextView mGetAuthCode;
    @BindView(R.id.googleAuthCodeText)
    TextView mGoogleAuthCodeText;
    private Unbinder mBind;
    private AuthCodeViewController mAuthCodeViewController;
    private boolean mSendSms;
    private boolean mFreezeGetAuthCode;
    private boolean mReset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_authenticator, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mReset = LocalUser.getUser().getUserInfo().getGoogleAuth() == AUTH;
        mTitleBar.setTitle(mReset ? R.string.reset_google_auth : R.string.google_authenticator);
        mAuthCodeText.setVisibility(mReset ? View.VISIBLE : View.GONE);
        mAuthCode.setVisibility(mReset ? View.VISIBLE : View.GONE);
        mGetAuthCode.setVisibility(mReset ? View.VISIBLE : View.GONE);
        if (!mReset) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mGoogleAuthCode.getLayoutParams();
            layoutParams.leftToRight = mGoogleAuthCodeText.getId();
            mGoogleAuthCode.setLayoutParams(layoutParams);
        }
        mGoogleAuthCode.addTextChangedListener(mWatcher);
        mAuthCode.addTextChangedListener(mWatcher);
        String userPhone = LocalUser.getUser().getUserInfo().getUserPhone();
        mSendSms = !TextUtils.isEmpty(userPhone);
        mAuthCodeText.setText(mSendSms ? R.string.sms_auth_code : R.string.mail_auth_code);
        mAuthCode.setHint(mSendSms ? R.string.please_input_sms_auth_code : R.string.please_input_mail_auth_code);
        createGoogleKey();
    }

    private ValidationWatcher mWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (mReset) {
                mConfirm.setEnabled(mGoogleAuthCode.getText().toString().trim().length() > 0 && mAuthCode.getText().toString().trim().length() > 0);
            } else {
                mConfirm.setEnabled(mGoogleAuthCode.getText().toString().trim().length() > 0);
            }
        }
    };

    private void createGoogleKey() {
        Apic.createGoogleKey().tag(TAG)
                .callback(new Callback<Resp<CreateGoogleKey>>() {
                    @Override
                    protected void onRespSuccess(Resp<CreateGoogleKey> resp) {
                        setGoogleKey(resp.getData());
                    }
                })
                .fire();
    }

    private void bindGoogleKey(String googleCode, String drawPass, String googleKey) {
        Apic.bindGoogleKey(googleCode, drawPass, googleKey).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        ToastUtil.show(R.string.bind_google_authenticator_success);
                        LocalUser user = LocalUser.getUser();
                        if (user.isLogin()) {
                            UserInfo userInfo = user.getUserInfo();
                            if (userInfo.getGoogleAuth() == 0) {
                                userInfo.setGoogleAuth(1);
                                LocalUser.getUser().setUserInfo(userInfo);
                            }
                        }
                        finish();
                        UniqueActivity.launcher(getContext(), GoogleAuthenticatorSettingsFragment.class).execute();
                    }
                })
                .fire();
    }

    private void setGoogleKey(CreateGoogleKey data) {
        final Bitmap bitmap = ZXingUtils.createQRImage(data.getQrCode(), mQcCode.getMeasuredWidth(), mQcCode.getMeasuredHeight());
        mQcCode.setImageBitmap(bitmap);
        mQcCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImageUtils.saveImageToGallery(getContext(), bitmap);
                ToastUtil.show(R.string.save_success);
                return true;
            }
        });
        mSecretKey.setText(data.getGoogleKey());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.copy, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.copy:
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setPrimaryClip(ClipData.newPlainText(null, mSecretKey.getText()));
                ToastUtil.show(R.string.copy_success);
                break;
            case R.id.confirm:
                bindGoogleKey(mGoogleAuthCode.getText().toString(), "", mSecretKey.getText().toString());
                break;
            default:
        }
    }

    @OnClick(R.id.getAuthCode)
    public void onViewClicked() {
        if (mSendSms) {
            sendOld(AuthSendOld.TYPE_SMS, "");
        } else {
            sendOld(AuthSendOld.TYPE_MAIL, "");
        }
    }

    private void sendOld(int type, String authCode) {
        AuthSendOld authSendOld = new AuthSendOld();
        authSendOld.setSendType(type);
        authSendOld.setSmsType(AuthCodeGet.TYPE_SAFE_PSD);
        authSendOld.setImgCode(authCode);
        Apic.sendOld(authSendOld).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        freezeGetAuthCodeButton();
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
                })
                .fire();
    }

    private void showImageAuthCodeDialog() {
        mAuthCodeViewController = new AuthCodeViewController(getContext(), new AuthCodeViewController.OnClickListener() {
            @Override
            public void onConfirmClick(String authCode) {
                if (mSendSms) {
                    sendOld(AuthSendOld.TYPE_SMS, authCode);
                } else {
                    sendOld(AuthSendOld.TYPE_MAIL, authCode);
                }
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

    private void freezeGetAuthCodeButton() {
        mFreezeGetAuthCode = true;
        startScheduleJobRightNow(1000);
        mGetAuthCode.setTag(60);
        mGetAuthCode.setEnabled(false);
        mGetAuthCode.setText(getString(R.string.x_seconds, 60));
    }

    @Override
    public void onTimeUp(int count) {
        if (mFreezeGetAuthCode) {
            Integer tag = (Integer) mGetAuthCode.getTag();
            int authCodeCounter = tag != null ? tag.intValue() : 0;
            authCodeCounter--;
            if (authCodeCounter <= 0) {
                mGetAuthCode.setEnabled(true);
                mGetAuthCode.setText(R.string.regain);
                mGetAuthCode.setTag(null);
                mFreezeGetAuthCode = false;
                stopScheduleJob();
            } else {
                mGetAuthCode.setTag(authCodeCounter);
                mGetAuthCode.setText(getString(R.string.x_seconds, authCodeCounter));
            }
        }
    }
}
