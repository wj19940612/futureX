package com.songbai.futurex.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.utils.Launcher;
import com.umeng.message.PushAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Modified by john on 2018/7/4
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.logo)
    ImageView mLogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        translucentStatusBar();
        Apic.addBindToken(PushAgent.getInstance(getApplicationContext()).getRegistrationId()).fire();
        mLogo.postDelayed(new Runnable() {
            @Override
            public void run() {
                Launcher.with(getActivity(), MainActivity.class).execute();
                finish();
            }
        }, 1500);
    }
}
