package com.songbai.futurex.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.fragment.dialog.UploadUserImageDialogFragment;
import com.songbai.futurex.utils.Display;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/29
 */
public class PersonalDataActivity extends BaseActivity {

    @BindView(R.id.userHeadImage)
    ImageView mUserHeadImage;
    @BindView(R.id.nickName)
    IconTextRow mNickName;
    @BindView(R.id.realName)
    IconTextRow mRealName;
    @BindView(R.id.phoneCertification)
    IconTextRow mPhoneCertification;
    @BindView(R.id.mailCertification)
    IconTextRow mMailCertification;
    @BindView(R.id.primaryCertification)
    IconTextRow mPrimaryCertification;
    @BindView(R.id.seniorCertification)
    IconTextRow mSeniorCertification;
    private Unbinder mBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        mBind = ButterKnife.bind(this);
        setIconTextRowSubDrawablePadding(mMailCertification);
        setIconTextRowSubDrawablePadding(mPrimaryCertification);
        setIconTextRowSubDrawablePadding(mSeniorCertification);
    }

    private void setIconTextRowSubDrawablePadding(IconTextRow iconTextRow) {
        TextView subTextView = iconTextRow.getSubTextView();
        subTextView.setCompoundDrawablePadding((int) Display.dp2Px(9, getResources()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }

    @OnClick({R.id.headImageLayout, R.id.nickName, R.id.realName, R.id.phoneCertification,
            R.id.mailCertification, R.id.primaryCertification, R.id.seniorCertification,
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
            case R.id.phoneCertification:
                Launcher.with(this, BindPhoneActivity.class);
                break;
            case R.id.mailCertification:
                break;
            case R.id.primaryCertification:
                break;
            case R.id.seniorCertification:
                break;
            case R.id.fiatPayManagement:
                break;
            case R.id.addressManagement:
                break;
            default:
        }
    }
}
