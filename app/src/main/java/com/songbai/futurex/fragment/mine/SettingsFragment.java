package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class SettingsFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.language)
    IconTextRow mLanguage;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
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

    @OnClick({R.id.language, R.id.aboutUs, R.id.feedback, R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.language:
                break;
            case R.id.aboutUs:
                UniqueActivity.launcher(getActivity(), AboutUsFragment.class).execute();
                break;
            case R.id.feedback:
                UniqueActivity.launcher(getActivity(), FeedbackFragment.class).execute();
                break;
            case R.id.logout:
                break;
        }
    }
}