package com.songbai.futurex.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.mine.BindMailFragment;
import com.songbai.futurex.fragment.mine.BindPhoneFragment;
import com.songbai.futurex.fragment.mine.DrawCoinAddressFragment;
import com.songbai.futurex.fragment.mine.LegalCurrencyPayFragment;
import com.songbai.futurex.fragment.mine.PrimaryCertificationFragment;
import com.songbai.futurex.fragment.mine.SeniorCertificationFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.Display;
import com.songbai.futurex.utils.ImagePicker;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/5/29
 */
public class PersonalDataActivity extends BaseActivity {

    private static final int MODIFY_PERSONAL_DATA = 12313;

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
    private boolean mHasPhone;
    private boolean mHasEmail;
    private int mAuthenticationStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        mBind = ButterKnife.bind(this);
        setIconTextRowSubDrawablePadding(mMailCertification);
        setIconTextRowSubDrawablePadding(mPrimaryCertification);
        setIconTextRowSubDrawablePadding(mSeniorCertification);
        setUserInfo();
    }

    private void setUserInfo() {
        LocalUser localUser = LocalUser.getUser();
        if (localUser.isLogin()) {
            UserInfo userInfo = localUser.getUserInfo();
            GlideApp
                    .with(this)
                    .load(userInfo.getUserPortrait())
                    .circleCrop()
                    .into(mUserHeadImage);
            mNickName.setSubText(userInfo.getUserName());
            mRealName.setSubText(userInfo.getRealName());
            String userPhone = userInfo.getUserPhone();
            mHasPhone = !TextUtils.isEmpty(userPhone);
            if (mHasPhone) {
                mPhoneCertification.setSubText(userPhone);
            } else {
                setIconTextRowSubDrawable(mPhoneCertification, R.drawable.ic_common_unautherized);
            }
            String userEmail = userInfo.getUserEmail();
            mHasEmail = !TextUtils.isEmpty(userEmail);
            if (mHasEmail) {
                mMailCertification.setSubText(userEmail);
            } else {
                setIconTextRowSubDrawable(mMailCertification, R.drawable.ic_common_unautherized);
            }
            // 认证状态:0未完成任何认证,1初级认证,2高级认证成功,3高级认证审核中,4高级认证失败
            mAuthenticationStatus = userInfo.getAuthenticationStatus();
            switch (mAuthenticationStatus) {
                case 0:
                    setIconTextRowSubDrawable(mPrimaryCertification, R.drawable.ic_common_unautherized);
                    setIconTextRowSubDrawable(mSeniorCertification, R.drawable.ic_common_unautherized);
                    break;
                case 1:
                    setIconTextRowSubDrawable(mPrimaryCertification, R.drawable.ic_common_autherized);
                    setIconTextRowSubDrawable(mSeniorCertification, R.drawable.ic_common_unautherized);
                    break;
                case 2:
                    setIconTextRowSubDrawable(mPrimaryCertification, R.drawable.ic_common_autherized);
                    setIconTextRowSubDrawable(mSeniorCertification, R.drawable.ic_common_autherized);
                    break;
                case 3:
                    setIconTextRowSubDrawable(mPrimaryCertification, R.drawable.ic_common_autherized);
                    setIconTextRowSubDrawable(mSeniorCertification, R.drawable.ic_common_inreview);
                    mSeniorCertification.setSubText(R.string.certificating);
                    break;
                case 4:
                    setIconTextRowSubDrawable(mPrimaryCertification, R.drawable.ic_common_autherized);
                    setIconTextRowSubDrawable(mSeniorCertification, R.drawable.ic_common_unautherized);
                    break;
                default:
            }
        }
    }

    private void setIconTextRowSubDrawablePadding(IconTextRow iconTextRow) {
        TextView subTextView = iconTextRow.getSubTextView();
        subTextView.setCompoundDrawablePadding((int) Display.dp2Px(9, getResources()));
    }

    private void setIconTextRowSubDrawable(IconTextRow iconTextRow, @DrawableRes int id) {
        TextView subTextView = iconTextRow.getSubTextView();
        subTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, id, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }

    @OnClick({R.id.headImageLayout, R.id.nickName, R.id.realName, R.id.phoneCertification,
            R.id.mailCertification, R.id.primaryCertification, R.id.seniorCertification,
            R.id.legalCurrencyPayManagement, R.id.addressManagement})
    public void onViewClicked(View view) {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        switch (view.getId()) {
            case R.id.headImageLayout:
//                UploadUserImageDialogFragment uploadUserImageDialogFragment = UploadUserImageDialogFragment.newInstance(
//                        UploadUserImageDialogFragment.IMAGE_TYPE_CLIPPING_IMAGE_SCALE_OR_MOVE, "", -1, getString(R.string.please_select_portrait));
//                uploadUserImageDialogFragment.show(getSupportFragmentManager());
                ImagePicker
                        .create(this)
                        .openGallery()
                        .maxSelectNum(1)
                        .forResult();
//                ImagePicker
//                        .create(this)
//                        .openCamera()
//                        .forResult();
                break;
            case R.id.nickName:
                Launcher.with(this, ModifyNickNameActivity.class)
                        .putExtra(ExtraKeys.NICK_NAME, userInfo.getUserName())
                        .execute(MODIFY_PERSONAL_DATA);
                break;
            case R.id.realName:
                break;
            case R.id.phoneCertification:
                UniqueActivity.launcher(this, BindPhoneFragment.class)
                        .putExtra(ExtraKeys.HAS_BIND_PHONE, TextUtils.isEmpty(userInfo.getUserPhone()))
                        .execute(MODIFY_PERSONAL_DATA);
                break;
            case R.id.mailCertification:
                UniqueActivity.launcher(this, BindMailFragment.class)
                        .putExtra(ExtraKeys.HAS_BIND_EMAIL, mHasEmail)
                        .execute(MODIFY_PERSONAL_DATA);
                break;
            case R.id.primaryCertification:
                if (mAuthenticationStatus > 0) {
                    return;
                }
                UniqueActivity.launcher(this, PrimaryCertificationFragment.class)
                        .execute(MODIFY_PERSONAL_DATA);
                break;
            case R.id.seniorCertification:
                UniqueActivity.launcher(this, SeniorCertificationFragment.class)
                        .putExtra(ExtraKeys.AUTHENTICATION_STATUS, mAuthenticationStatus)
                        .execute(MODIFY_PERSONAL_DATA);
                break;
            case R.id.legalCurrencyPayManagement:
                UniqueActivity.launcher(getActivity(), LegalCurrencyPayFragment.class)
                        .execute(MODIFY_PERSONAL_DATA);
                break;
            case R.id.addressManagement:
                UniqueActivity.launcher(getActivity(), DrawCoinAddressFragment.class)
                        .execute(MODIFY_PERSONAL_DATA);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_TYPE_OPEN_CUSTOM_GALLERY) {
            if (data != null) {
                String stringExtra = data.getStringExtra(ExtraKeys.IMAGE_PATH);
                String image = ImageUtils.compressImageToBase64(stringExtra, this);
                uploadImage(image);
            }
        } else if (requestCode == ImagePicker.REQ_CODE_TAKE_PHONE_FROM_PHONES) {
            String galleryBitmapPath = ImagePicker.getGalleryBitmapPath(this, data);
            if (!TextUtils.isEmpty(galleryBitmapPath)) {
                String image = ImageUtils.compressImageToBase64(galleryBitmapPath, this);
                uploadImage(image);
            }
        } else if (requestCode == MODIFY_PERSONAL_DATA) {
            requestUserInfo();
        }
    }

    private void uploadImage(String image) {
        Apic.uploadImage(image)
                .callback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        submitPortraitPath(resp.getData());
                    }
                })
                .fire();
    }

    private void submitPortraitPath(String image) {
        Apic.submitPortraitPath(image)
                .callback(new Callback<Object>() {
                    @Override
                    protected void onRespSuccess(Object resp) {
                        //                        ToastUtil.show(R.string.modify_success);
                        requestUserInfo();
                    }
                })
                .fire();
    }

    private void requestUserInfo() {
        Apic.findUserInfo()
                .tag(TAG)
                .callback(new Callback<Resp<UserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<UserInfo> resp) {
                        LocalUser.getUser().setUserInfo(resp.getData());
                        setUserInfo();
                    }
                })
                .fire();
    }
}
