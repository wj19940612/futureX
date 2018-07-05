package com.songbai.futurex.fragment.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.songbai.futurex.model.local.AuthCodeCheck;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.FindPsdData;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.AuthCodeViewController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/6/6
 * <p>
 * Description: 邮箱或者手机号输入验证码页面
 */
public class AuthCodeFragment extends UniqueActivity.UniFragment {

    private static final int REQ_CODE_SET_PASS = 92;

    @BindView(R.id.closePage)
    ImageView mClosePage;
    @BindView(R.id.phoneOrEmail)
    TextView mPhoneOrEmail;
    @BindView(R.id.getAuthCode)
    TextView mGetAuthCode;
    @BindView(R.id.next)
    TextView mNext;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;
    Unbinder unbinder;
    @BindView(R.id.authCode)
    EditText mAuthCode;

    private FindPsdData mFindPsdData;

    private AuthCodeViewController mAuthCodeViewController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_code, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mFindPsdData = extras.getParcelable(ExtraKeys.FIND_PSD_DATA);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
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
        if (mFindPsdData.isFindPhonePassword()) {
            mPhoneOrEmail.setText(mFindPsdData.getPhone());
            mAuthCode.setHint(R.string.sms_auth_code);
        } else {
            mPhoneOrEmail.setText(mFindPsdData.getEmail());
            mAuthCode.setHint(R.string.email_auth_code);
        }
        mAuthCode.addTextChangedListener(mValidationWatcher);

        mGetAuthCode.performClick();
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkNextButtonEnable();
            if (enable != mNext.isEnabled()) {
                mNext.setEnabled(enable);
            }
        }
    };

    private boolean checkNextButtonEnable() {
        String authCode = mAuthCode.getText().toString().trim();

        if (TextUtils.isEmpty(authCode)) {
            return false;
        }

        return true;
    }

    private void freezeGetAuthCodeButton() {
        startScheduleJobRightNow(1000);
        mGetAuthCode.setTag(60);
        mGetAuthCode.setEnabled(false);
        mGetAuthCode.setText(getString(R.string.x_seconds, 60));
    }

    @Override
    public void onTimeUp(int count) {
        Integer tag = (Integer) mGetAuthCode.getTag();
        int authCodeCounter = tag != null ? tag.intValue() : 0;

        authCodeCounter--;
        if (authCodeCounter <= 0) {
            mGetAuthCode.setEnabled(true);
            mGetAuthCode.setText(R.string.regain);
            mGetAuthCode.setTag(null);
            stopScheduleJob();
        } else {
            mGetAuthCode.setTag(authCodeCounter);
            mGetAuthCode.setText(getString(R.string.x_seconds, authCodeCounter));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.closePage, R.id.getAuthCode, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closePage:
                getActivity().finish();
                break;
            case R.id.getAuthCode:
                requestAuthCode(null);
                break;
            case R.id.next:
                requestCheckAuthCode();
                break;
        }
    }

    private void requestCheckAuthCode() {
        AuthCodeCheck authCodeCheck = new AuthCodeCheck();
        authCodeCheck.setType(AuthCodeGet.TYPE_FORGET_PSD);
        if (mFindPsdData.isFindPhonePassword()) {
            authCodeCheck.setData(mFindPsdData.getPhone());
        } else {
            authCodeCheck.setData(mFindPsdData.getEmail());
        }
        String authCode = mAuthCode.getText().toString().trim();
        authCodeCheck.setMsgCode(authCode);
        Apic.checkAuthCode(authCodeCheck).tag(TAG)
                .indeterminate(this)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        openSetPadPage();
                    }
                }).fire();
    }

    private void openSetPadPage() {
        String authCode = mAuthCode.getText().toString().trim();
        mFindPsdData.setMsgCode(authCode);

        UniqueActivity.launcher(this, SetPsdFragment.class)
                .putExtra(ExtraKeys.FIND_PSD_DATA, mFindPsdData)
                .execute(this, REQ_CODE_SET_PASS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SET_PASS && resultCode == Activity.RESULT_OK) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }

    private void requestAuthCode(String imageAuthCode) {
        AuthCodeGet.Builder builder = AuthCodeGet.Builder.anAuthCodeGet();
        if (mFindPsdData.isFindPhonePassword()) {
            builder.phone(mFindPsdData.getPhone());
        } else {
            builder.email(mFindPsdData.getEmail());
        }
        AuthCodeGet authCodeGet = builder
                .type(AuthCodeGet.TYPE_FORGET_PSD)
                .imgCode(imageAuthCode).build();

        Apic.getAuthCode(authCodeGet).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
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
                }).fire();
    }

    private void showImageAuthCodeDialog() {
        mAuthCodeViewController = new AuthCodeViewController(getActivity(), new AuthCodeViewController.OnClickListener() {
            @Override
            public void onConfirmClick(String authCode) {
                requestAuthCode(authCode);
            }

            @Override
            public void onImageCodeClick(ImageView imageView) {
                requestAuthCodeImage(imageView.getWidth(), imageView.getHeight());
            }
        });

        SmartDialog.solo(getActivity())
                .setCustomViewController(mAuthCodeViewController)
                .show();

        ImageView imageView = mAuthCodeViewController.getAuthCodeImage();
        requestAuthCodeImage(imageView.getLayoutParams().width, imageView.getLayoutParams().height);
    }

    private void requestAuthCodeImage(int width, int height) {
        Apic.getAuthCodeImage(AuthCodeGet.TYPE_FORGET_PSD).tag(TAG)
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
}
