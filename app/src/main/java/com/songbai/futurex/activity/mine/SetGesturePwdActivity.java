package com.songbai.futurex.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yangguangda
 * @date 2018/6/5
 */
public class SetGesturePwdActivity extends BaseActivity {
    @BindView(R.id.confirm)
    TextView mConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_gesture_pwd);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.confirm)
    public void onViewClicked() {
        Launcher.with(getActivity(), SafeActivity.class).execute();
    }
}
