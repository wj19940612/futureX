package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.GoogleAuthVerify;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.GoogleAuthCodeViewController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/6/15
 */
public class GoogleAuthenticatorSettingsFragment extends UniqueActivity.UniFragment {
    private static final int OPEN = 1;
    private static final int CLOSED = 0;
    @BindView(R.id.drawCoinSwitch)
    ImageView mDrawCoinSwitch;
    @BindView(R.id.resetCashPwdSwitch)
    ImageView mResetCashPwdSwitch;
    @BindView(R.id.confirmOtcTradSSwitch)
    ImageView mConfirmOtcTradSSwitch;
    Unbinder unbinder;
    private boolean mAuthenticated;
    private int mDraw;
    private int mSetDrawPass;
    private int mCnyTrade;
    private GoogleAuthVerify mGoogleAuthVerify;
    private GoogleAuthCodeViewController mGoogleAuthCodeViewController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_authenticator_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        setAuthVerify();
    }

    private void setAuthVerify() {
        LocalUser user = LocalUser.getUser();
        if (user.isLogin()) {
            UserInfo userInfo = user.getUserInfo();
            mAuthenticated = userInfo.getGoogleAuth() == 1;
            if (mAuthenticated) {
                String googleAuthString = userInfo.getGoogleAuthString();
                mGoogleAuthVerify = new Gson().fromJson(googleAuthString, GoogleAuthVerify.class);
                if (mGoogleAuthVerify == null) {
                    mGoogleAuthVerify = new GoogleAuthVerify();
                }
                mDraw = mGoogleAuthVerify.getDRAW();
                mSetDrawPass = mGoogleAuthVerify.getSET_DRAW_PASS();
                mCnyTrade = mGoogleAuthVerify.getCNY_TRADE();
            }
        }
        mDrawCoinSwitch.setEnabled(mAuthenticated);
        mResetCashPwdSwitch.setEnabled(mAuthenticated);
        mConfirmOtcTradSSwitch.setEnabled(mAuthenticated);
        mDrawCoinSwitch.setSelected(mDraw == OPEN);
        mResetCashPwdSwitch.setSelected(mSetDrawPass == OPEN);
        mConfirmOtcTradSSwitch.setSelected(mCnyTrade == OPEN);
    }

    public void setAuthVerify(String authCode, String googleCode) {
        Apic.setAuthVerify(authCode, googleCode).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        getUserInfo();
                    }
                })
                .fire();
    }

    private void getUserInfo() {
        Apic.getUserInfo().tag(TAG).indeterminate(this).tag(TAG)
                .callback(new Callback<Resp<UserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<UserInfo> resp) {
                        LocalUser.getUser().setUserInfo(resp.getData());
                        setAuthVerify();
                    }
                }).fireFreely();
    }

    @OnClick({R.id.drawCoinSwitch, R.id.resetCashPwdSwitch, R.id.confirmOtcTradSSwitch})
    public void onViewClicked(View view) {
        boolean isSelected = view.isSelected();
        if (isSelected) {
            showGoogleAuthDialog(view,isSelected);
        } else {
            setState(view, isSelected);
            setAuthVerify(new Gson().toJson(mGoogleAuthVerify), "");
        }
    }

    private void setState(View view, boolean isSelected) {
        switch (view.getId()) {
            case R.id.drawCoinSwitch:
                mGoogleAuthVerify.setDRAW(isSelected ? CLOSED : OPEN);
                break;
            case R.id.resetCashPwdSwitch:
                mGoogleAuthVerify.setSET_DRAW_PASS(isSelected ? CLOSED : OPEN);
                break;
            case R.id.confirmOtcTradSSwitch:
                mGoogleAuthVerify.setCNY_TRADE(isSelected ? CLOSED : OPEN);
                break;
            default:
        }
    }

    private void showGoogleAuthDialog(final View view, final boolean isSelected) {
        mGoogleAuthCodeViewController = new GoogleAuthCodeViewController(getActivity(), new GoogleAuthCodeViewController.OnClickListener() {
            @Override
            public void onConfirmClick(String authCode) {
                setState(view, isSelected);
                setAuthVerify(new Gson().toJson(mGoogleAuthVerify), authCode);
            }
        });
        SmartDialog.solo(getActivity())
                .setCustomViewController(mGoogleAuthCodeViewController)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
