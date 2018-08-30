package com.songbai.futurex.activity.auth;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.httplib.BitmapCfg;
import com.sbai.httplib.ReqCallback;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.auth.FindPsdFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.local.LoginData;
import com.songbai.futurex.model.local.RegisterData;
import com.songbai.futurex.service.SocketPushService;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.RegularExpUtils;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.UmengCountEventId;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.PasswordEditText;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.AuthCodeViewController;
import com.songbai.futurex.websocket.MessageProcessor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    private static final int REQ_CODE_REGISTER = 90;

    @BindView(R.id.closePage)
    ImageView mClosePage;
    @BindView(R.id.pageTitle)
    TextView mPageTitle;
    @BindView(R.id.phoneOrEmail)
    EditText mPhoneOrEmail;
    @BindView(R.id.phoneNumberClear)
    ImageView mPhoneNumberClear;
    @BindView(R.id.password)
    PasswordEditText mPassword;
    @BindView(R.id.login)
    TextView mLogin;
    @BindView(R.id.loading)
    ImageView mLoading;
    @BindView(R.id.forgetPassword)
    TextView mForgetPassword;
    @BindView(R.id.goRegister)
    TextView mGoRegister;
    @BindView(R.id.passwordLoginOperations)
    LinearLayout mPasswordLoginOperations;
    @BindView(R.id.contentArea)
    LinearLayout mContentArea;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;

    private AuthCodeViewController mAuthCodeViewController;
    private LoginData mLoginData;
    private SmartDialog mSmartDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.BottomEnterExitActivity);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mPageTitle.setText(getString(R.string.login_x, getString(R.string.app_name)));
        mPhoneOrEmail.addTextChangedListener(mPhoneOrEmailValidationWatcher);
        mPhoneOrEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(mPhoneOrEmail.getText().toString()) && hasFocus) {
                    mPhoneNumberClear.setVisibility(View.VISIBLE);
                } else {
                    mPhoneNumberClear.setVisibility(View.INVISIBLE);
                }
            }
        });
        mPhoneOrEmail.setText(LocalUser.getUser().getLastAct());
        mPassword.addTextChangedListener(mValidationWatcher);
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPhoneNumberClear.setVisibility(View.INVISIBLE);
                }
            }
        });
        mPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNumberClear.setVisibility(View.INVISIBLE);
                mPassword.requestFocus();
            }
        });
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    KeyBoardUtils.closeKeyboard(mRootView);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhoneOrEmail.removeTextChangedListener(mPhoneOrEmailValidationWatcher);
        mPassword.removeTextChangedListener(mValidationWatcher);
        mLoading.clearAnimation();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_to_bottom);
    }

    private ValidationWatcher mPhoneOrEmailValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mValidationWatcher.afterTextChanged(s);

            mPhoneNumberClear.setVisibility(checkClearBtnVisible() ? View.VISIBLE : View.INVISIBLE);
        }
    };

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkLoginButtonEnable();
            if (enable != mLogin.isEnabled()) {
                mLogin.setEnabled(enable);
            }
        }
    };

    private boolean checkClearBtnVisible() {
        String phone = mPhoneOrEmail.getText().toString();
        return !TextUtils.isEmpty(phone);
    }

    private boolean checkLoginButtonEnable() {
        String phoneOrEmail = mPhoneOrEmail.getText().toString().trim();
        String password = mPassword.getPassword();

        if (TextUtils.isEmpty(phoneOrEmail)) {
            return false;
        }

        if (TextUtils.isEmpty(password) || password.length() < 8) {
            return false;
        }

        return true;
    }

    @OnClick({R.id.closePage, R.id.login, R.id.forgetPassword, R.id.goRegister, R.id.phoneNumberClear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closePage:
                finish();
                umengEventCount(UmengCountEventId.LOGIN0004);
                break;
            case R.id.login:
                if (mLoading.getVisibility() != View.VISIBLE) {
                    umengEventCount(UmengCountEventId.LOGIN0001);
                    login("");
                }
                break;
            case R.id.forgetPassword:
                umengEventCount(UmengCountEventId.LOGIN0002);
                UniqueActivity.launcher(getActivity(), FindPsdFragment.class).execute();
                break;
            case R.id.goRegister:
                umengEventCount(UmengCountEventId.LOGIN0003);
                openRegisterPage();
                break;
            case R.id.phoneNumberClear:
                mPhoneOrEmail.setText("");
                break;
        }
    }

    private void showImageAuthCodeDialog() {
        mAuthCodeViewController = new AuthCodeViewController(this, new AuthCodeViewController.OnClickListener() {
            @Override
            public void onConfirmClick(String authCode) {
                login(authCode);
            }

            @Override
            public void onImageCodeClick(ImageView imageView) {
                requestAuthCodeImage(imageView.getWidth(), imageView.getHeight());
            }
        });
        mAuthCodeViewController.setControlByOutSide(true);

        mSmartDialog = SmartDialog.solo(getActivity());
        mSmartDialog
                .setCustomViewController(mAuthCodeViewController)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                .show();

        final ImageView imageView = mAuthCodeViewController.getAuthCodeImage();
        requestAuthCodeImage(imageView.getLayoutParams().width, imageView.getLayoutParams().height);
    }

    private void login(String authCode) {
        mLoginData = new LoginData(RegisterData.PLATFORM_ANDROID);
        mLoginData.setImgCode(authCode);
        String phoneOrEmail = mPhoneOrEmail.getText().toString().trim();
        if (RegularExpUtils.isValidEmail(phoneOrEmail)) {
            mLoginData.setEmail(phoneOrEmail);
        } else {
            mLoginData.setPhone(phoneOrEmail);
        }
        String password = md5Encrypt(mPassword.getPassword());
        mLoginData.setUserPass(password);

        Apic.login(mLoginData).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mLogin.setText(R.string.logining);
                        mLoading.setVisibility(View.VISIBLE);
                        mLoading.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.loading));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mLogin.setText(R.string.login);
                        mLoading.setVisibility(View.GONE);
                        mLoading.clearAnimation();
                    }

                    @Override
                    protected void onRespSuccess(Resp resp) {
                        if (mSmartDialog != null) {
                            mSmartDialog.dismiss();
                        }
                        ToastUtil.show(R.string.login_success);
                        LocalUser.getUser().login();
                        requestUserInfo();
                    }

                    @Override
                    public void onFailure(ReqError reqError) {
                        super.onFailure(reqError);
                        mLoading.setVisibility(View.GONE);
                        mLoading.clearAnimation();
                    }

                    @Override
                    public void onResponse(Resp resp) {
                        if (resp.getCode() == Resp.Code.REQUARE_IMAGE_AUTH) {
                            mLoading.setVisibility(View.GONE);
                            mLoading.clearAnimation();
                            showImageAuthCodeDialog();
                        } else if (resp.getCode() == Resp.Code.PWD_ERROR) {
                            mLoading.setVisibility(View.GONE);
                            mLoading.clearAnimation();
                            if (mSmartDialog != null) {
                                mSmartDialog.dismiss();
                            }
                            super.onResponse(resp);
                        } else if (resp.getCode() == Resp.Code.IMAGE_AUTH_CODE_FAILED) {
                            mLoading.setVisibility(View.GONE);
                            mLoading.clearAnimation();
                            if (mSmartDialog != null) {
                                ImageView imageView = mAuthCodeViewController.getAuthCodeImage();
                                requestAuthCodeImage(imageView.getWidth(), imageView.getHeight());
                                mAuthCodeViewController.clearAuthCode();
                            }
                            super.onResponse(resp);
                        } else {
                            super.onResponse(resp);
                        }
                    }
                }).fireFreely();
    }

    private void requestUserInfo() {
        Apic.getUserInfo().tag(TAG).indeterminate(this)
                .callback(new Callback4Resp<Resp<UserInfo>, UserInfo>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mLogin.setText(R.string.logining);
                        mLoading.setVisibility(View.VISIBLE);
                        mLoading.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.loading));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mLogin.setText(R.string.login);
                        mLoading.setVisibility(View.GONE);
                        mLoading.clearAnimation();
                    }

                    @Override
                    protected void onRespData(UserInfo data) {
                        LocalUser.getUser().setUserInfo(data, mLoginData.getPhone(), mLoginData.getEmail());
                        MessageProcessor.get().register();
                        putNewUserToService();
                        Preference.get().setOptionalListRefresh(true);
                        Preference.get().setPosterListRefresh(true);
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
                }).fireFreely();
    }

    private void requestAuthCodeImage(int width, int height) {
        Apic.getAuthCodeImage(AuthCodeGet.TYPE_QUICK_LOGIN).tag(TAG)
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

    private void openRegisterPage() {
        ComponentName callingActivity = getActivity().getCallingActivity();
        if (callingActivity != null && callingActivity.getClassName().equals(RegisterActivity.class.getCanonicalName())) {
            getActivity().finish();
        } else {
            Launcher.with(getActivity(), RegisterActivity.class).execute(REQ_CODE_REGISTER);
        }
    }

    private void putNewUserToService() {
        Intent intent = new Intent(LoginActivity.this, SocketPushService.class);
        intent.putExtra(SocketPushService.NEWUSER, true);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_REGISTER && resultCode == RESULT_OK) {
            finish();
        }
    }
}
