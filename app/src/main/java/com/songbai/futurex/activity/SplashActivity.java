package com.songbai.futurex.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.sbai.httplib.ReqError;
import com.songbai.futurex.BuildConfig;
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.Host;
import com.songbai.futurex.model.OtcConfig;
import com.songbai.futurex.service.SocketPushService;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.PermissionViewController;
import com.songbai.futurex.wrapper.Apic;
import com.songbai.futurex.wrapper.WrapMainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Modified by john on 2018/7/4
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class SplashActivity extends StatusBarActivity {

    private static final int REQ_CODE_PERMISSION = 10000;

    protected String TAG;

    @BindView(R.id.logo)
    ImageView mLogo;

    private List<Host> mHostList;
    private Host mFinalHost;
    private PermissionViewController mPermissionViewController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        startSocketPushService();
        translucentStatusBar();
        com.songbai.futurex.http.Apic.getOtcTransConfig().tag(TAG)
                .callback(new Callback<Resp<OtcConfig>>() {
                    @Override
                    protected void onRespSuccess(Resp<OtcConfig> resp) {
                        if (resp != null) {
                            Preference.get().setCloseOTC(resp.getData().getOtcConfig() == 1);
                        }
                    }
                }).fireFreely();
        if (hasSelfPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE})) {
            startRunningApp();
        } else {
            mPermissionViewController = new PermissionViewController(this, new PermissionViewController.OnClickListener() {
                @Override
                public void onOpenClick(SmartDialog dialog) {
                    dialog.dismiss();
                    requestNecessaryPermissions();
                }

                @Override
                public void onCloseClick(SmartDialog dialog) {
                    dialog.dismiss();
                    finish();
                }
            });
            SmartDialog.solo(this)
                    .setCustomViewController(mPermissionViewController)
                    .setCancelableOnTouchOutside(false)
                    .setWidthScale(0.7f)
                    .show();
        }
    }

    private void requestNecessaryPermissions() {
        requestPermission(this, REQ_CODE_PERMISSION,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(int requestCode) {
                        if (requestCode == REQ_CODE_PERMISSION) {
                            startRunningApp();
                        }
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
                        finish();
                    }
                });
    }

    private void startRunningApp() {
        mLogo.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.IS_PROD) {
                    requestHost();
                } else {
                    openApp(new Host());
                }
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Api.cancel(TAG);
    }

    private void requestHost() {
        Apic.requestHost().tag(TAG)
                .callback(new Callback<ArrayList<Host>>() {
                    @Override
                    protected void onRespSuccess(ArrayList<Host> resp) {
                        if (resp != null && !resp.isEmpty()) {
                            mHostList = resp;
                            determineHost();
                        }
                    }
                }).fireFreely();
    }

    private void determineHost() {
        for (Host host : mHostList) {
            requestAppType(host);
        }
    }

    private void requestAppType(final Host host) {
        Apic.requestAppType().tag(TAG)
                .host(host.getHost())
                .callback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);

                    }

                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        if (mFinalHost != null) {
                            return;
                        }

                        mFinalHost = host;
                        if (!TextUtils.isEmpty(resp.getData()) && resp.getData().equals("1")) {
                            openVest();
                        } else {
                            openApp(mFinalHost);
                        }
                    }

                    @Override
                    public void onFailure(ReqError reqError) {

                    }
                }).fireFreely();
    }

    private void openApp(Host finalHost) {
        Api.setFixedHost(finalHost.getHost());
        Launcher.with(SplashActivity.this, MainActivity.class).execute();
        supportFinishAfterTransition();
        finish();
    }

    private void openVest() {
        Launcher.with(SplashActivity.this, WrapMainActivity.class).execute();
        supportFinishAfterTransition();
        finish();
    }

    private void startSocketPushService() {
        Intent intent = new Intent(this, SocketPushService.class);
        startService(intent);
    }
}
