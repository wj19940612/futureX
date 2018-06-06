package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.view.PasswordEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class ChangeLoginPwdFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.password)
    PasswordEditText mPassword;
    @BindView(R.id.confirmPassword)
    PasswordEditText mConfirmPassword;
    @BindView(R.id.smsAuthCode)
    EditText mSmsAuthCode;
    @BindView(R.id.confirm)
    TextView mConfirm;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_login_pwd, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.smsAuthCode, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.smsAuthCode:
                break;
            case R.id.confirm:
                break;
            default:
        }
    }
}
