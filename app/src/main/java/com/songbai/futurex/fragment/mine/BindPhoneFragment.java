package com.songbai.futurex.fragment.mine;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.sbai.httplib.BitmapCfg;
import com.sbai.httplib.ReqCallback;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.AreaCode;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.AuthSendOld;
import com.songbai.futurex.utils.StrFormatter;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.AuthCodeViewController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class BindPhoneFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.areaCode)
    TextView mAreaCode;
    @BindView(R.id.phone)
    EditText mPhone;
    @BindView(R.id.authCode)
    EditText mAuthCode;
    @BindView(R.id.getMessageAuthCode)
    TextView mGetMessageAuthCode;
    @BindView(R.id.mailAuthCode)
    EditText mMailAuthCode;
    @BindView(R.id.getMailAuthCode)
    TextView mGetMailAuthCode;
    @BindView(R.id.confirmBind)
    TextView mConfirmBind;
    private Unbinder mBind;
    private OptionsPickerView mPvOptions;
    private List<AreaCode> mAreaCodes;
    private String mPhoneNum;
    private String mMailAuth;
    private String mSmsAuth;
    private AuthCodeViewController mAuthCodeViewController;
    private boolean mFreezeGetEmailAuthCode;
    private boolean mFreezeGetPhoneAuthCode;
    private boolean mHasBindPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bind_phone, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mHasBindPhone = extras.getBoolean(ExtraKeys.HAS_BIND_PHONE);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mTitleBar.setTitle(mHasBindPhone ? R.string.edit_phone : R.string.bind_phone);
        if (mHasBindPhone) {
            mMailAuthCode.setHint(R.string.used_phone_auth_code);
        }
        mPhone.addTextChangedListener(mWatcher);
        mAuthCode.addTextChangedListener(mWatcher);
        mMailAuthCode.addTextChangedListener(mWatcher);
        getAreaCode();
    }

    private void getAreaCode() {
        Apic.getAreaCodes().tag(TAG)
                .callback(new Callback4Resp<Resp<List<AreaCode>>, List<AreaCode>>() {
                    @Override
                    protected void onRespData(List<AreaCode> data) {
                        if (!data.isEmpty()) {
                            mAreaCodes = data;
                            String areaCode = data.get(0).getTeleCode();
                            mAreaCode.setText(StrFormatter.getFormatAreaCode(areaCode));
                        }
                    }
                }).fireFreely();
    }

    private void updatePhone(String phoneNum, String phoneMsgCode, String msgCode, String type) {
        Apic.updatePhone(mAreaCode.getText().toString(), phoneNum, phoneMsgCode, msgCode, type).tag(TAG)
                .callback(new Callback4Resp<Resp<List<AreaCode>>, List<AreaCode>>() {
                    @Override
                    protected void onRespData(List<AreaCode> data) {
                        ToastUtil.show(R.string.bind_success);
                        finish();
                    }
                }).fireFreely();
    }

    private ValidationWatcher mWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mPhoneNum = mPhone.getText().toString().trim();
            mMailAuth = mMailAuthCode.getText().toString().trim();
            mSmsAuth = mAuthCode.getText().toString().trim();
            boolean enable = !TextUtils.isEmpty(mPhoneNum) && !TextUtils.isEmpty(mMailAuth) && !TextUtils.isEmpty(mSmsAuth);
            mConfirmBind.setEnabled(enable);
        }
    };

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
        mGetMessageAuthCode.setTag(60);
        mGetMessageAuthCode.setEnabled(false);
        mGetMessageAuthCode.setText(getString(R.string.x_seconds, 60));
    }

    @Override
    public void onTimeUp(int count) {
        boolean timeUp = false;
        if (mFreezeGetPhoneAuthCode) {
            Integer tag = (Integer) mGetMessageAuthCode.getTag();
            int authCodeCounter = tag != null ? tag.intValue() : 0;
            authCodeCounter--;
            if (authCodeCounter <= 0) {
                mGetMessageAuthCode.setEnabled(true);
                mGetMessageAuthCode.setText(R.string.regain);
                mGetMessageAuthCode.setTag(null);
                mFreezeGetEmailAuthCode = false;
                timeUp = true;
            } else {
                timeUp = false;
                mGetMessageAuthCode.setTag(authCodeCounter);
                mGetMessageAuthCode.setText(getString(R.string.x_seconds, authCodeCounter));
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
                mFreezeGetPhoneAuthCode = false;
                timeUp = true;
            } else {
                timeUp = false;
                mGetMailAuthCode.setTag(authCodeCounter);
                mGetMailAuthCode.setText(getString(R.string.x_seconds, authCodeCounter));
            }
        }
        if (timeUp) {
            stopScheduleJob();
        }
    }

    private void requestAuthCode(String phoneNum, String authCode) {
        AuthCodeGet authCodeGet = AuthCodeGet.Builder.anAuthCodeGet()
                .type(AuthCodeGet.TYPE_MODIFY_PHONE)
                .imgCode(authCode)
                .phone(phoneNum)
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
                            showImageAuthCodeDialog(false);
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
    }

    private void showImageAuthCodeDialog(final boolean sentOld) {
        mAuthCodeViewController = new AuthCodeViewController(getContext(), new AuthCodeViewController.OnClickListener() {
            @Override
            public void onConfirmClick(String authCode) {
                if (sentOld) {
                    if (mHasBindPhone) {
                        sendOld(AuthSendOld.TYPE_SMS, authCode);
                    } else {
                        sendOld(AuthSendOld.TYPE_MAIL, authCode);
                    }
                } else {
                    requestAuthCode(mPhoneNum, authCode);
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
        Apic.getAuthCodeImage(AuthCodeGet.TYPE_MODIFY_PHONE).tag(TAG)
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

    private void sendOld(int type, String authCode) {
        AuthSendOld authSendOld = new AuthSendOld();
        authSendOld.setSendType(type);
        authSendOld.setSmsType(AuthCodeGet.TYPE_MODIFY_PHONE);
        authSendOld.setImgCode(authCode);
        Apic.sendOld(authSendOld)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        freezeGetEmailAuthCodeButton();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_REQUIRED
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_TIMEOUT
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_FAILED) {
                            showImageAuthCodeDialog(true);
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.areaCode, R.id.getMessageAuthCode, R.id.getMailAuthCode, R.id.confirmBind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.areaCode:
                showAreaCodeSelector(mAreaCodes);
                break;
            case R.id.getMessageAuthCode:
                if (!TextUtils.isEmpty(mPhoneNum)) {
                    requestAuthCode(mPhoneNum, "");
                }
                break;
            case R.id.getMailAuthCode:
                if (mHasBindPhone) {
                    sendOld(AuthSendOld.TYPE_SMS, "");
                } else {
                    sendOld(AuthSendOld.TYPE_MAIL, "");
                }
                break;
            case R.id.confirmBind:
                updatePhone(mPhoneNum, mSmsAuth, mMailAuth, "");
                break;
            default:
        }
    }

    private void showAreaCodeSelector(List<AreaCode> areaCode) {
        if (areaCode == null || areaCode.isEmpty()) {
            return;
        }
        final ArrayList<String> codes = new ArrayList<>();
        for (AreaCode code : areaCode) {
            codes.add(code.getTeleCode());
        }
        mPvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                mAreaCode.setText(StrFormatter.getFormatAreaCode(codes.get(options1)));
            }
        }).setLayoutRes(R.layout.pickerview_custom_view, new CustomListener() {
            @Override
            public void customLayout(View v) {
                TextView cancel = v.findViewById(R.id.cancel);
                TextView confirm = v.findViewById(R.id.confirm);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvOptions.dismiss();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvOptions.returnData();
                        mPvOptions.dismiss();
                    }
                });
            }
        })
                .setCyclic(false, false, false)
                .setTextColorCenter(ContextCompat.getColor(getContext(), R.color.text22))
                .setTextColorOut(ContextCompat.getColor(getContext(), R.color.text99))
                .setDividerColor(ContextCompat.getColor(getContext(), R.color.bgDD))
                .build();
        mPvOptions.setPicker(codes);
        mPvOptions.show();
    }
}
