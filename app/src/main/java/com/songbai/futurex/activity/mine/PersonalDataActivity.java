package com.songbai.futurex.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.StatusBarActivity;
import com.songbai.futurex.fragment.dialog.UploadUserImageDialogFragment;
import com.songbai.futurex.utils.Launcher;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/29
 */
public class PersonalDataActivity extends StatusBarActivity {

    private Unbinder mBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        mBind = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }

    @OnClick({R.id.headImageLayout, R.id.nickName, R.id.realName, R.id.phoneAuthentication,
            R.id.mailAuthentication, R.id.primaryAuthentication, R.id.advancedAuthentication,
            R.id.fiatPayManagement, R.id.addressManagement})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.headImageLayout:
                UploadUserImageDialogFragment uploadUserImageDialogFragment = UploadUserImageDialogFragment.newInstance(
                        UploadUserImageDialogFragment.IMAGE_TYPE_CLIPPING_IMAGE_SCALE_OR_MOVE, "", -1, getString(R.string.please_select_portrait));
                uploadUserImageDialogFragment.show(getSupportFragmentManager());
                break;
            case R.id.nickName:
                Launcher.with(this, ModifyNickNameActivity.class).execute();
                break;
            case R.id.realName:
                break;
            case R.id.phoneAuthentication:
                Launcher.with(this, BindPhoneActivity.class);
                break;
            case R.id.mailAuthentication:
                break;
            case R.id.primaryAuthentication:
                break;
            case R.id.advancedAuthentication:
                break;
            case R.id.fiatPayManagement:
                break;
            case R.id.addressManagement:
                break;
            default:
        }
    }
}
