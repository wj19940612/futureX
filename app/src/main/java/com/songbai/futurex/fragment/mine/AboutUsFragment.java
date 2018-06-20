package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.AppInfo;
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
        Apic.queryForceVersion(AppInfo.getVersionName(getContext()), "")
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

    @OnClick({R.id.platformIntroduction, R.id.companyIntroduction, R.id.connectCustomerService, R.id.versionUpdating})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.platformIntroduction:
                break;
            case R.id.companyIntroduction:
                break;
            case R.id.connectCustomerService:
                break;
            case R.id.versionUpdating:
                queryForceVersion();
                break;
            default:
        }
    }
}
