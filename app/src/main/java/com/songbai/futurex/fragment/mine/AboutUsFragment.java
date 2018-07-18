package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.CustomServiceActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.UpdateVersionDialogFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.AppVersion;
import com.songbai.futurex.utils.AppInfo;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class AboutUsFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.versionUpdate)
    TextView mVersionUpdate;
    @BindView(R.id.versionIcon)
    View mVersionIcon;
    private Unbinder mBind;

    private AppVersion mAppVersion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mVersionUpdate.setText(getString(R.string.version_x, AppInfo.getVersionName(getContext())));
        queryVersion();
    }

    private void queryVersion() {
        Apic.queryForceVersion().tag(TAG)
                .callback(new Callback<Resp<AppVersion>>() {
                    @Override
                    protected void onRespSuccess(Resp<AppVersion> resp) {
                        if (resp.getData() != null && (resp.getData().isForceUpdate() || resp.getData().isNeedUpdate())) {
                            mVersionIcon.setVisibility(View.VISIBLE);
                            mAppVersion = resp.getData();
                        }
                    }
                })
                .fireFreely();
    }

    private void queryForceVersion() {
        if (mAppVersion == null) {
            Apic.queryForceVersion().tag(TAG)
                    .callback(new Callback<Resp<AppVersion>>() {
                        @Override
                        protected void onRespSuccess(Resp<AppVersion> resp) {
                            if (resp.getData() != null && (resp.getData().isForceUpdate() || resp.getData().isNeedUpdate())) {
                                UpdateVersionDialogFragment.newInstance(resp.getData(), resp.getData().isForceUpdate())
                                        .show(getChildFragmentManager());
                            }else{
                                ToastUtil.show(R.string.is_latest_version_now);
                            }
                        }
                    })
                    .fireFreely();
        } else if (mAppVersion.isForceUpdate() || mAppVersion.isForceUpdate()) {
            UpdateVersionDialogFragment.newInstance(mAppVersion, mAppVersion.isForceUpdate())
                    .show(getChildFragmentManager());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.platformIntroduction, R.id.companyIntroduction, R.id.connectCustomerService, R.id.versionLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.platformIntroduction:
                UniqueActivity.launcher(this, PlatformIntroFragment.class).putExtra(ExtraKeys.INTRODUCE_STYLE, PlatformIntroFragment.STYLE_PLATFORM).execute();
                break;
            case R.id.companyIntroduction:
                UniqueActivity.launcher(this, PlatformIntroFragment.class).putExtra(ExtraKeys.INTRODUCE_STYLE, PlatformIntroFragment.STYLE_COMPANY).execute();
                break;
            case R.id.connectCustomerService:
                Launcher.with(this, CustomServiceActivity.class).execute();
                break;
            case R.id.versionLayout:
                queryForceVersion();
                break;
            default:
        }
    }
}
