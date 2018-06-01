package com.songbai.futurex.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/29
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.login)
    TextView mLogin;

    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick(R.id.login)
    public void onViewClicked() {
//        Launcher.with(getActivity(), LoginActivity.class)
//                .execute();

//        new AlertDialog.Builder(getActivity())
//                .setPositiveButton("E", null)
//                .create().show();

        SmartDialog.solo(getActivity())
                .setMessage(R.string.message_auth_code)
                .setPositive(R.string.ok)
                .show();

        mLogin.postDelayed(new Runnable() {
            @Override
            public void run() {
                SmartDialog.solo(getActivity())
                        .setMessage(R.string.message_center)
                        .setPositive(R.string.ok)
                        .show();
            }
        }, 3000);
    }
}
