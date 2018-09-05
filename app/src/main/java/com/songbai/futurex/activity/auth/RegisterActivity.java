package com.songbai.futurex.activity.auth;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.httplib.BitmapCfg;
import com.sbai.httplib.ReqCallback;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.auth.SetPsdFragment;
import com.songbai.futurex.fragment.mine.PlatformIntroFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.AreaCode;
import com.songbai.futurex.model.local.AuthCodeCheck;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.RegisterData;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.StrFormatter;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.AuthCodeViewController;
import com.songbai.futurex.view.dialog.MsgHintController;
import com.songbai.futurex.view.dialog.SelectAreaCodesViewController;
import com.songbai.futurex.websocket.MessageProcessor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    private static final int REQ_CODE_SET_PASS = 92;

    @BindView(R.id.closePage)
    ImageView mClosePage;
    @BindView(R.id.pageTitle)
    TextView mPageTitle;
    @BindView(R.id.registerTypeSwitch)
    TextView mRegisterTypeSwitch;
    @BindView(R.id.areaCode)
    TextView mAreaCode;
    @BindView(R.id.phoneNumber)
    EditText mPhoneNumber;
    @BindView(R.id.phoneAuthCode)
    EditText mPhoneAuthCode;
    @BindView(R.id.emailAuthCode)
    EditText mEmailAuthCode;
    @BindView(R.id.next)
    TextView mNext;
    @BindView(R.id.switchToLogin)
    TextView mSwitchToLogin;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;
    @BindView(R.id.phoneLine)
    LinearLayout mPhoneLine;
    @BindView(R.id.email)
    EditText mEmail;
    @BindView(R.id.emailLine)
    LinearLayout mEmailLine;
    @BindView(R.id.getPhoneAuthCode)
    TextView mGetPhoneAuthCode;
    @BindView(R.id.phoneAuthCodeLine)
    LinearLayout mPhoneAuthCodeLine;
    @BindView(R.id.getEmailAuthCode)
    TextView mGetEmailAuthCode;
    @BindView(R.id.emailAuthCodeLine)
    LinearLayout mEmailAuthCodeLine;
    @BindView(R.id.checkUserAgreement)
    CheckBox mCheckUserAgreement;

    private boolean mFreezeGetPhoneAuthCode;
    private boolean mFreezeGetEmailAuthCode;

    private SelectAreaCodesViewController mSelectAreaCodesViewController;
    private AuthCodeViewController mAuthCodeViewController;
    private SmartDialog mSmartDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mPhoneNumber.addTextChangedListener(mValidationWatcher);
        mPhoneAuthCode.addTextChangedListener(mValidationWatcher);
        mEmail.addTextChangedListener(mValidationWatcher);
        mEmailAuthCode.addTextChangedListener(mValidationWatcher);
        mCheckUserAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean enable = checkNextButtonEnable();
                if (enable != mNext.isEnabled()) {
                    mNext.setEnabled(enable);
                }
            }
        });

        mSelectAreaCodesViewController = new SelectAreaCodesViewController(this,
                new SelectAreaCodesViewController.OnSelectListener() {
                    @Override
                    public void onSelect(AreaCode areaCode) {
                        mAreaCode.setText(StrFormatter.getFormatAreaCode(areaCode.getTeleCode()));
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

        Apic.getAreaCodes().tag(TAG)
                .callback(new Callback4Resp<Resp<List<AreaCode>>, List<AreaCode>>() {
                    @Override
                    protected void onRespData(List<AreaCode> data) {
                        if (!data.isEmpty()) {
                            String areaCode = data.get(0).getTeleCode();
                            mAreaCode.setText(StrFormatter.getFormatAreaCode(areaCode));
                        }
                        mSelectAreaCodesViewController.setAreaCodeList(data);
                    }
                }).fireFreely();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhoneNumber.removeTextChangedListener(mValidationWatcher);
        mPhoneAuthCode.removeTextChangedListener(mValidationWatcher);
        mEmail.removeTextChangedListener(mValidationWatcher);
        mEmailAuthCode.removeTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkNextButtonEnable();
            if (enable != mNext.isEnabled()) {
                mNext.setEnabled(enable);
            }

            if (isPhoneRegister()) {
                enable = checkGetPhoneAuthCodeEnable();
                if (enable != mGetPhoneAuthCode.isEnabled()) {
                    mGetPhoneAuthCode.setEnabled(enable);
                }
            } else {
                enable = checkGetEmailAuthCodeButtonEnable();
                if (enable != mGetEmailAuthCode.isEnabled()) {
                    mGetEmailAuthCode.setEnabled(enable);
                }
            }
        }
    };

    private boolean checkGetPhoneAuthCodeEnable() {
        String phone = mPhoneNumber.getText().toString().trim();
        return !TextUtils.isEmpty(phone) && !mFreezeGetPhoneAuthCode;
    }

    private boolean checkGetEmailAuthCodeButtonEnable() {
        String email = mEmail.getText().toString().trim();
        return !TextUtils.isEmpty(email) && !mFreezeGetEmailAuthCode;
    }

    private boolean checkNextButtonEnable() {
        if (isPhoneRegister()) {
            String phone = mPhoneNumber.getText().toString().trim();
            String phoneAuthCode = mPhoneAuthCode.getText().toString().trim();
            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(phoneAuthCode) || !mCheckUserAgreement.isChecked()) {
                return false;
            }
            if (phoneAuthCode.length() < 4) {
                return false;
            }
        } else {
            String email = mEmail.getText().toString().trim();
            String emailAuthCode = mEmailAuthCode.getText().toString().trim();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(emailAuthCode) || !mCheckUserAgreement.isChecked()) {
                return false;
            }
            if (emailAuthCode.length() < 4) {
                return false;
            }
        }
        return true;
    }

    @OnClick({R.id.closePage, R.id.registerTypeSwitch, R.id.areaCode, R.id.next, R.id.switchToLogin,
            R.id.getPhoneAuthCode, R.id.getEmailAuthCode, R.id.userAgreement})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closePage:
                getActivity().finish();
                break;
            case R.id.registerTypeSwitch:
                switchRegisterType();
                break;
            case R.id.areaCode:
                showAreaCodeSelector();
                break;
            case R.id.next:
                requestCheckAuthCode();
                break;
            case R.id.switchToLogin:
                openLoginPage();
                break;
            case R.id.getPhoneAuthCode:
                requestPhoneAuthCode(null);
                break;
            case R.id.getEmailAuthCode:
                requestEmailAuthCode(null);
                break;
            case R.id.userAgreement:
                UniqueActivity.launcher(this, PlatformIntroFragment.class)
                        .putExtra(ExtraKeys.INTRODUCE_STYLE, PlatformIntroFragment.STYLE_SERVICE_AGREEMENT).execute();
                break;
        }
    }

    private void requestCheckAuthCode() {
        AuthCodeCheck authCodeCheck = new AuthCodeCheck();
        authCodeCheck.setType(AuthCodeGet.TYPE_REGISTER);
        if (isPhoneRegister()) {
            String areaCode = mAreaCode.getText().toString().trim();
            String phone = mPhoneNumber.getText().toString().trim();
            String phoneAuthCode = mPhoneAuthCode.getText().toString().trim();
            authCodeCheck.setData(phone);
            authCodeCheck.setTeleCode(areaCode);
            authCodeCheck.setMsgCode(phoneAuthCode);
        } else {
            String email = mEmail.getText().toString().trim();
            String emailAuthCode = mEmailAuthCode.getText().toString().trim();
            authCodeCheck.setData(email);
            authCodeCheck.setMsgCode(emailAuthCode);
        }
        Apic.checkAuthCode(authCodeCheck).tag(TAG)
                .indeterminate(this)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        openSetPassPage();
                    }
                }).fire();
    }

    private void openSetPassPage() {
        RegisterData registerData = new RegisterData(RegisterData.PLATFORM_ANDROID);
        if (isPhoneRegister()) {
            String areaCode = mAreaCode.getText().toString().trim();
            String phone = mPhoneNumber.getText().toString().trim();
            String phoneAuthCode = mPhoneAuthCode.getText().toString().trim();
            registerData.setTelteCode(areaCode);
            registerData.setPhone(phone);
            registerData.setMsgCode(phoneAuthCode);
        } else {
            String email = mEmail.getText().toString().trim();
            String emailAuthCode = mEmailAuthCode.getText().toString().trim();
            registerData.setEmail(email);
            registerData.setMsgCode(emailAuthCode);
        }

        UniqueActivity.launcher(this, SetPsdFragment.class)
                .putExtra(ExtraKeys.REGISTER_DATA, registerData)
                .execute(REQ_CODE_SET_PASS);
    }

    private void openLoginPage() {
        ComponentName callingActivity = getActivity().getCallingActivity();
        if (callingActivity != null && callingActivity.getClassName().equals(LoginActivity.class.getCanonicalName())) {
            finish();
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute(REQ_CODE_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {

        }
        if (requestCode == REQ_CODE_SET_PASS && resultCode == RESULT_OK) {
            MessageProcessor.get().register();
            setResult(RESULT_OK);
            finish();
        }
    }

    private void requestEmailAuthCode(String imageAuthCode) {
        String email = mEmail.getText().toString().trim();
        AuthCodeGet authCodeGet = AuthCodeGet.Builder.anAuthCodeGet()
                .email(email)
                .type(AuthCodeGet.TYPE_REGISTER)
                .imgCode(imageAuthCode)
                .build();

        Apic.getAuthCode(authCodeGet).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        freezeGetEmailAuthCodeButton();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_REQUIRED
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_TIMEOUT
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_FAILED) {
                            showImageAuthCodeDialog();
                        } else if (failedResp.getCode() == Resp.Code.MAIL_EXIST) {
                            showAccountExistsDialog(failedResp.getCode());
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
    }

    private void requestPhoneAuthCode(String imageAuthCode) {
        String phone = mPhoneNumber.getText().toString().trim();
        String areaCode = mAreaCode.getText().toString();
        AuthCodeGet authCodeGet = AuthCodeGet.Builder.anAuthCodeGet()
                .phone(phone)
                .teleCode(areaCode)
                .imgCode(imageAuthCode)
                .type(AuthCodeGet.TYPE_REGISTER)
                .build();

        Apic.getAuthCode(authCodeGet).tag(TAG)
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
                        } else if (failedResp.getCode() == Resp.Code.PHONE_EXIST) {
                            showAccountExistsDialog(failedResp.getCode());
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
    }

    private void showAccountExistsDialog(int code) {
        final MsgHintController msgHintController = new MsgHintController(RegisterActivity.this, new MsgHintController.OnClickListener() {
            @Override
            public void onConfirmClick() {
                finish();
            }
        });
        mSmartDialog = SmartDialog.solo(RegisterActivity.this);
        mSmartDialog.setCustomViewController(msgHintController)
                .show();
        msgHintController.setConfirmText(R.string.go_login);
        msgHintController.setCloseText(R.string.re_enter);
        msgHintController.setOnColseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSmartDialog != null) {
                    mSmartDialog.dismiss();
                }
                mPhoneNumber.setText("");
                mEmail.setText("");
            }
        });
        msgHintController.setMsg(code == Resp.Code.PHONE_EXIST ? R.string.phone_exists : R.string.mail_exists);
        msgHintController.setImageRes(R.drawable.ic_popup_attention);
    }

    private void showImageAuthCodeDialog() {
        mAuthCodeViewController = new AuthCodeViewController(this, new AuthCodeViewController.OnClickListener() {
            @Override
            public void onConfirmClick(String authCode) {
                if (isPhoneRegister()) {
                    requestPhoneAuthCode(authCode);
                } else {
                    requestEmailAuthCode(authCode);
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
        Apic.getAuthCodeImage(AuthCodeGet.TYPE_REGISTER).tag(TAG)
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

    private void freezeGetEmailAuthCodeButton() {
        mFreezeGetEmailAuthCode = true;
        startScheduleJobRightNow(1000);
        mGetEmailAuthCode.setTag(60);
        mGetEmailAuthCode.setEnabled(false);
        mGetEmailAuthCode.setText(getString(R.string.x_seconds, 60));
    }

    private void freezeGetPhoneAuthCodeButton() {
        mFreezeGetPhoneAuthCode = true;
        startScheduleJobRightNow(1000);
        mGetPhoneAuthCode.setTag(60);
        mGetPhoneAuthCode.setEnabled(false);
        mGetPhoneAuthCode.setText(getString(R.string.x_seconds, 60));
    }

    @Override
    public void onTimeUp(int count) {
        Integer tag = (Integer) mGetPhoneAuthCode.getTag();
        int phoneAuthCodeCounter = tag != null ? tag.intValue() : 0;
        tag = (Integer) mGetEmailAuthCode.getTag();
        int emailAuthCodeCounter = tag != null ? tag.intValue() : 0;

        phoneAuthCodeCounter--;
        if (phoneAuthCodeCounter <= 0) {
            if (phoneAuthCodeCounter == 0) {
                mFreezeGetPhoneAuthCode = false;
                mGetPhoneAuthCode.setEnabled(true);
                mGetPhoneAuthCode.setText(R.string.regain);
                mGetPhoneAuthCode.setTag(null);
            }
        } else {
            mGetPhoneAuthCode.setTag(phoneAuthCodeCounter);
            mGetPhoneAuthCode.setText(getString(R.string.x_seconds, phoneAuthCodeCounter));
        }

        emailAuthCodeCounter--;
        if (emailAuthCodeCounter <= 0) {
            if (emailAuthCodeCounter == 0) {
                mFreezeGetEmailAuthCode = false;
                mGetEmailAuthCode.setEnabled(true);
                mGetEmailAuthCode.setText(R.string.regain);
                mGetEmailAuthCode.setTag(null);
            }
        } else {
            mGetEmailAuthCode.setTag(emailAuthCodeCounter);
            mGetEmailAuthCode.setText(getString(R.string.x_seconds, emailAuthCodeCounter));
        }

        if (phoneAuthCodeCounter <= 0 && emailAuthCodeCounter <= 0) {
            stopScheduleJob();
        }
    }

    private void showAreaCodeSelector() {
        SmartDialog.with(getActivity()).setCustomViewController(mSelectAreaCodesViewController)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setWidthScale(1)
                .show();
    }

    private void switchRegisterType() {
        if (isPhoneRegister()) {
            switchToEmailRegister();
        } else {
            switchToPhoneRegister();
        }
    }

    private void switchToPhoneRegister() {
        mPageTitle.setText(R.string.phone_register);
        mRegisterTypeSwitch.setText(R.string.email_register);

        mPhoneLine.setVisibility(View.VISIBLE);
        mPhoneAuthCodeLine.setVisibility(View.VISIBLE);

        mEmailLine.setVisibility(View.GONE);
        mEmailAuthCodeLine.setVisibility(View.GONE);
    }

    private void switchToEmailRegister() {
        mPageTitle.setText(R.string.email_register);
        mRegisterTypeSwitch.setText(R.string.phone_register);

        mPhoneLine.setVisibility(View.GONE);
        mPhoneAuthCodeLine.setVisibility(View.GONE);

        mEmailLine.setVisibility(View.VISIBLE);
        mEmailAuthCodeLine.setVisibility(View.VISIBLE);
    }

    private boolean isPhoneRegister() {
        return mPhoneLine.getVisibility() == View.VISIBLE;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (KeyBoardUtils.isShouldHideKeyboard(v, ev)) {
                KeyBoardUtils.closeKeyboard(v);
                v.clearFocus();
            }
            return super.dispatchTouchEvent(ev);
        }
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }
}
