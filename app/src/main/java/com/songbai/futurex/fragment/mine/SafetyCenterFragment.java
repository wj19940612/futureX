package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.mine.SetGesturePwdActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
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
    @BindView(R.id.setCashPwd)
    IconTextRow mSetCashPwd;
    @BindView(R.id.changeLoginPwd)
    IconTextRow mChangeLoginPwd;
    @BindView(R.id.googleAuthenticator)
    IconTextRow mGoogleAuthenticator;
    @BindView(R.id.gesturePwd)
    IconTextRow mGesturePwd;
    private Unbinder mBind;

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
    }

    private void isDrawPass() {
        Apic.isDrawPass()
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {

                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.setCashPwd, R.id.changeLoginPwd, R.id.googleAuthenticator, R.id.gesturePwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setCashPwd:
                UniqueActivity.launcher(this, CashPwdFragment.class).execute();
                break;
            case R.id.changeLoginPwd:
                UniqueActivity.launcher(this, ChangeLoginPwdFragment.class).execute();
                break;
            case R.id.googleAuthenticator:
                UniqueActivity.launcher(this, GoogleAuthenticatorFragment.class).execute();
                break;
            case R.id.gesturePwd:
                Launcher.with(this, SetGesturePwdActivity.class).execute();
                break;
            default:
        }
    }
}
