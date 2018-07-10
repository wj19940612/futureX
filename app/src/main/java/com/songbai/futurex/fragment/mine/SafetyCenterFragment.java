package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.mine.SetGesturePwdActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class SafetyCenterFragment extends UniqueActivity.UniFragment {
    private static final int AUTH = 1;
    @BindView(R.id.setCashPwd)
    IconTextRow mSetCashPwd;
    @BindView(R.id.changeLoginPwd)
    IconTextRow mChangeLoginPwd;
    @BindView(R.id.googleAuthenticator)
    IconTextRow mGoogleAuthenticator;
    @BindView(R.id.gesturePwd)
    IconTextRow mGesturePwd;
    private Unbinder mBind;
    private boolean hasWithDrawPass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safety_center, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        isDrawPass();
        LocalUser user = LocalUser.getUser();
        if (user.isLogin()) {
            UserInfo userInfo = user.getUserInfo();
            if (userInfo.getGoogleAuth() == AUTH) {
                mGoogleAuthenticator.setSubText(userInfo.getGoogleAuth() == AUTH ? R.string.certificated : R.string.uncertificated);
            }
        }
    }

    private void isDrawPass() {
        Apic.isDrawPass()
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        hasWithDrawPass = true;
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.Code.CASH_PWD_NONE) {
                            hasWithDrawPass = false;
                            mSetCashPwd.setSubText(R.string.not_set);
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

    @OnClick({R.id.setCashPwd, R.id.changeLoginPwd, R.id.googleAuthenticator, R.id.googleAuthenticatorSettings, R.id.gesturePwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setCashPwd:
                UniqueActivity.launcher(this, CashPwdFragment.class).putExtra(ExtraKeys.HAS_WITH_DRAW_PASS, hasWithDrawPass).execute();
                break;
            case R.id.changeLoginPwd:
                UniqueActivity.launcher(this, ChangeLoginPwdFragment.class).execute();
                break;
            case R.id.googleAuthenticator:
                UniqueActivity.launcher(this, GoogleAuthenticatorFragment.class).execute();
                break;
            case R.id.googleAuthenticatorSettings:
                if (LocalUser.getUser().getUserInfo().getGoogleAuth() == AUTH) {
                    UniqueActivity.launcher(this, GoogleAuthenticatorSettingsFragment.class).execute();
                }
                break;
            case R.id.gesturePwd:
                Launcher.with(this, SetGesturePwdActivity.class).execute();
                break;
            default:
        }
    }
}
