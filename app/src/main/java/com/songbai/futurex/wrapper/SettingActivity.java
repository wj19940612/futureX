package com.songbai.futurex.wrapper;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.PermissionUtil;
import com.songbai.wrapres.IconTextRow;
import com.songbai.wrapres.SmartDialog;
import com.songbai.wrapres.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.notificationSwitch)
    ImageView mNotificationSwitch;
    @BindView(R.id.receiveNotification)
    LinearLayout mReceiveNotification;
    @BindView(R.id.personalData)
    IconTextRow mPersonalData;
    @BindView(R.id.logout)
    TextView mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mNotificationSwitch.setSelected(PermissionUtil.isNotificationEnabled(this));
        if (LocalWrapUser.getUser().isLogin()) {
            mLogout.setVisibility(View.VISIBLE);
        } else {
            mLogout.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.notificationSwitch, R.id.personalData, R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.notificationSwitch:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                break;
            case R.id.personalData:
                if (LocalWrapUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), ProfileActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), WrapLoginActivity.class).execute();
                }
                break;
            case R.id.logout:
                logout();
                break;
        }
    }

    private void logout() {
        SmartDialog.with(getActivity(), R.string.affirm_logout)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        Apic.logout()
                                .tag(TAG)
                                .callback(new Callback<Resp<Object>>() {

                                    @Override
                                    protected void onRespSuccess(Resp<Object> resp) {
                                        LocalWrapUser.getUser().logout();
                                        finish();
                                    }
                                })
                                .fire();
                    }
                })
                .show();

    }
}
