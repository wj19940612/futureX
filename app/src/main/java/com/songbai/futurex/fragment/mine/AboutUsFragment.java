package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class AboutUsFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.versionUpdating)
    IconTextRow mVersionUpdating;
    private Unbinder mBind;

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
        mVersionUpdating.setSubText(getString(R.string.version_x, AppInfo.getVersionName(getContext())));
    }

    private void queryForceVersion() {
        Apic.queryForceVersion()
                .callback(new Callback<Resp<AppVersion>>() {
                    @Override
                    protected void onRespSuccess(Resp<AppVersion> resp) {
                        if (resp.getData() != null && resp.getData().isForceUpdate() || resp.getData().isNeedUpdate()) {
                            UpdateVersionDialogFragment.newInstance(resp.getData(), resp.getData().isForceUpdate())
                                    .show(getChildFragmentManager());
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

    @OnClick({R.id.platformIntroduction, R.id.companyIntroduction, R.id.connectCustomerService, R.id.versionUpdating})
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
            case R.id.versionUpdating:
                queryForceVersion();
                break;
            default:
        }
    }
}
