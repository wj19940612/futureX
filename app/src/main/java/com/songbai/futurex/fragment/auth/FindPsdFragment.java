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
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.FindPsdData;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.RegularExpUtils;
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
 * Description:
 * <p>
 * APIs:
 */
public class FindPsdFragment extends UniqueActivity.UniFragment {

    private static final int REQ_CODE_FIND_PSD = 93;

    @BindView(R.id.closePage)
    ImageView mClosePage;
    @BindView(R.id.phoneOrEmail)
    EditText mPhoneOrEmail;
    @BindView(R.id.next)
    TextView mNext;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;
    Unbinder unbinder;

    private AuthCodeViewController mAuthCodeViewController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_psd, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mPhoneOrEmail.addTextChangedListener(mValidationWatcher);
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
        String phoneOrEmail = mPhoneOrEmail.getText().toString().trim();

        if (TextUtils.isEmpty(phoneOrEmail)) {
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.closePage, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closePage:
                getActivity().finish();
                break;
            case R.id.next:
                requestAuthCode(null);
                break;
        }
    }

    private void requestAuthCode(String imageAuthCode) {
        final String phoneOrEmail = mPhoneOrEmail.getText().toString().trim();
        AuthCodeGet.Builder builder = AuthCodeGet.Builder.anAuthCodeGet();
        if (RegularExpUtils.isValidEmail(phoneOrEmail)) {
            builder.email(phoneOrEmail);
        } else {
            builder.phone(phoneOrEmail);
        }
        AuthCodeGet authCodeGet = builder
                .type(AuthCodeGet.TYPE_FORGET_PSD)
                .imgCode(imageAuthCode).build();

        Apic.getAuthCode(authCodeGet).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        openAuthCodePage(phoneOrEmail);
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

    private void openAuthCodePage(String phoneOrEmail) {
        FindPsdData findPsdData = new FindPsdData();
        if (RegularExpUtils.isValidEmail(phoneOrEmail)) {
            findPsdData.setEmail(phoneOrEmail);
        } else {
            findPsdData.setPhone(phoneOrEmail);
        }
        UniqueActivity.launcher(getActivity(), AuthCodeFragment.class)
                .putExtra(ExtraKeys.FIND_PSD_DATA, findPsdData)
                .execute(this, REQ_CODE_FIND_PSD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_FIND_PSD && resultCode == Activity.RESULT_OK) {
            getActivity().finish();
        }
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
