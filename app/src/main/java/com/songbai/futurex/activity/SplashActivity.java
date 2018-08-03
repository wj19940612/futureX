package com.songbai.futurex.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.sbai.httplib.ReqError;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.Host;
import com.songbai.futurex.utils.Launcher;
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

    protected String TAG;

    @BindView(R.id.logo)
    ImageView mLogo;

    private List<Host> mHostList;
    private Host mFinalHost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        translucentStatusBar();

        mLogo.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Launcher.with(getActivity(), MainActivity.class).execute();
                //Launcher.with(getActivity(), WrapMainActivity.class).execute();
                //finish();
                requestHost();
            }
        }, 1500);
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
                        Log.d("Temp", "onRespSuccess: mFinalHost: " + mFinalHost + ", isVest: " + resp.getData()); // todo remove later
                        if (mFinalHost != null) {
                            return;
                        }
                        Log.d("Temp", "onRespSuccess: opening app"); // todo remove later

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
}
