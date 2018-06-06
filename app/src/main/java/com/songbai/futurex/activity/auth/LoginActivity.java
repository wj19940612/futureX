package com.songbai.futurex.activity.auth;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.model.local.LoginData;
import com.songbai.futurex.model.local.RegisterData;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.RegularExpUtils;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.PasswordEditText;

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
            boolean enable = checkSignInButtonEnable();
            if (enable != mLogin.isEnabled()) {
                mLogin.setEnabled(enable);
            }
        }
    };

    private boolean checkClearBtnVisible() {
        String phone = mPhoneOrEmail.getText().toString();
        return !TextUtils.isEmpty(phone);
    }

    private boolean checkSignInButtonEnable() {
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
                break;
            case R.id.login:
                showImageAuthCodeDialog();
                login();
                break;
            case R.id.forgetPassword:
                // TODO: 2018/5/30
                break;
            case R.id.goRegister:
                openRegisterPage();
                break;
            case R.id.phoneNumberClear:
                mPhoneOrEmail.setText("");
                break;
        }
    }

    private void showImageAuthCodeDialog() {
        // todo get image auth code and login, meanwhile anim login button progress
    }

    private void login() {
        LoginData loginData = new LoginData(RegisterData.PLATFORM_ANDROID);
        String phoneOrEmail = mPhoneOrEmail.getText().toString();
        if (RegularExpUtils.isValidEmail(phoneOrEmail)) {
            loginData.setEmail(phoneOrEmail);
        } else {
            loginData.setPhone(phoneOrEmail);
        }
        String password = md5Encrypt(mPassword.getPassword());
        loginData.setUserPass(password);
        //Apic.login(loginData);
    }

    private void openRegisterPage() {
        ComponentName callingActivity = getActivity().getCallingActivity();
        if (callingActivity != null && callingActivity.getClassName().equals(RegisterActivity.class.getCanonicalName())) {
            getActivity().finish();
        } else {
            Launcher.with(getActivity(), RegisterActivity.class).execute(REQ_CODE_REGISTER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_REGISTER && resultCode == RESULT_OK) {
            finish();
        }
    }
}
