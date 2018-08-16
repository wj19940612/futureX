package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/7/30
 */
public class GoogleAuthFragment extends UniqueActivity.UniFragment {
    private static final int AUTH = 1;

    @BindView(R.id.googleAuthenticator)
    IconTextRow mGoogleAuthenticator;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmnet_google_auth, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        LocalUser user = LocalUser.getUser();
        if (user.isLogin()) {
            UserInfo userInfo = user.getUserInfo();
            mGoogleAuthenticator.setSubText(userInfo.getGoogleAuth() == AUTH ? R.string.certificated : R.string.uncertificated);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.googleAuthenticator, R.id.resetGoogleAuthenticator, R.id.googleAuthenticatorSettings})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.googleAuthenticator:
                if (LocalUser.getUser().getUserInfo().getGoogleAuth() == AUTH) {
                    return;
                }
                UniqueActivity.launcher(this, GoogleAuthenticatorFragment.class).execute();
                break;
            case R.id.resetGoogleAuthenticator:
                if (LocalUser.getUser().getUserInfo().getGoogleAuth() == AUTH) {
                    UniqueActivity.launcher(this, GoogleAuthenticatorFragment.class).execute();
                    return;
                }
                ToastUtil.show(getString(R.string.please_set_google_authenticator_first));
                break;
            case R.id.googleAuthenticatorSettings:
                if (LocalUser.getUser().getUserInfo().getGoogleAuth() == AUTH) {
                    UniqueActivity.launcher(this, GoogleAuthenticatorSettingsFragment.class).execute();
                    return;
                }
                ToastUtil.show(getString(R.string.please_set_google_authenticator_first));
                break;
            default:
        }
    }
}
