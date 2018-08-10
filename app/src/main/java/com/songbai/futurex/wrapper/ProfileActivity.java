package com.songbai.futurex.wrapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.wrapper.fragment.ChooseSexDialogFragment;
import com.songbai.futurex.wrapper.fragment.UploadUserImageDialogFragment;
import com.songbai.wrapres.IconTextRow;
import com.songbai.wrapres.autofit.AutofitTextView;
import com.songbai.wrapres.model.UserDetailInfo;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;
import sbai.com.glide.GlideApp;

import static com.songbai.futurex.wrapper.fragment.UploadUserImageDialogFragment.REQ_CLIP_HEAD_IMAGE_PAGE;


public class ProfileActivity extends WrapBaseActivity implements ChooseSexDialogFragment.OnUserSexListener {

    private static final int REQ_CODE_USER_NAME = 165;

    private static final int REQ_CODE_LOCATION = 805;

    @BindView(R.id.userHeadImage)
    ImageView mUserHeadImage;
    @BindView(R.id.helpArrow)
    ImageView mHelpArrow;
    @BindView(R.id.headImageLayout)
    RelativeLayout mHeadImageLayout;
    @BindView(R.id.nickName)
    IconTextRow mNickName;
    @BindView(R.id.sex)
    IconTextRow mSex;
    @BindView(R.id.age)
    IconTextRow mAge;
    @BindView(R.id.location)
    AutofitTextView mLocation;

    private String[] mAgeList;
    private int mSelectAgeListIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mAgeList = new String[101];
        for (int i = 0; i < 101; i++) {
            mAgeList[i] = ((i + Calendar.getInstance().get(Calendar.YEAR) - 100) + "年");
        }

        updateUserInfo();
        updateUserImage();

        requestDetailUserInfo();
    }

    private void requestDetailUserInfo() {
        Apic.requestUserInfo()
                .tag(TAG)
                .indeterminate(this)
                .callback(new Callback4Resp<Resp<UserDetailInfo>, UserDetailInfo>() {
                    @Override
                    protected void onRespData(UserDetailInfo data) {
                        if (!TextUtils.isEmpty(data.getUserPortrait()) &&
                                !data.getUserPortrait().equalsIgnoreCase(LocalWrapUser.getUser().getUserInfo().getUserPortrait())) {
                            setResult(RESULT_OK);
                        }
                        LocalWrapUser.getUser().getUserInfo().updateLocalUserInfo(data);
                        updateUserInfo();
                        updateUserImage();
                    }
                }).fireFreely();
    }

    private void updateUserInfo() {
        WrapUserInfo userInfo = LocalWrapUser.getUser().getUserInfo();
        mNickName.setSubText(userInfo.getUserName());
        if (userInfo.getUserSex() == WrapUserInfo.SEX_GIRL) {
            mSex.setSubText(getString(R.string.girl));
        } else if (userInfo.getUserSex() == WrapUserInfo.SEX_BOY) {
            mSex.setSubText(getString(R.string.boy));
        }
        if (userInfo.getAge() != null) {
            mAge.setSubText(userInfo.getAge().toString());
        }
        mLocation.setText(userInfo.getLand());
    }

    private void updateUserImage() {
        if (LocalWrapUser.getUser().isLogin()) {
            GlideApp.with(this).load(LocalWrapUser.getUser().getUserInfo().getUserPortrait())
                    .circleCrop()
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(mUserHeadImage);
        } else {
            GlideApp.with(this).load(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(mUserHeadImage);
        }
    }

    @OnClick({R.id.headImageLayout, R.id.nickName, R.id.sex, R.id.age, R.id.location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.headImageLayout:
                UploadUserImageDialogFragment uploadUserImageDialogFragment = UploadUserImageDialogFragment.newInstance(
                        UploadUserImageDialogFragment.IMAGE_TYPE_CLIPPING_IMAGE_SCALE_OR_MOVE,
                        LocalWrapUser.getUser().getUserInfo().getUserPortrait());
                uploadUserImageDialogFragment.show(getSupportFragmentManager());
                break;
            case R.id.nickName:
                Launcher.with(getActivity(), ModifyUserNameActivity.class).execute(REQ_CODE_USER_NAME);
                break;
            case R.id.sex:
                new ChooseSexDialogFragment().show(getSupportFragmentManager());
                break;
            case R.id.age:
                showAgePicker();
                break;
            case R.id.location:
                Launcher.with(getActivity(), Location2Activity.class).execute(REQ_CODE_LOCATION);
                break;
        }
    }

    @Override
    public void onUserSex(int userSex) {
        if (userSex != 0) {
            if (userSex == WrapUserInfo.SEX_GIRL) {
                mSex.setSubText(getString(R.string.girl));
            } else if (userSex == WrapUserInfo.SEX_BOY) {
                mSex.setSubText(getString(R.string.boy));
            }
        }
    }

    private void submitUserInfo() {
        Integer age = null;
        if (!TextUtils.isEmpty(mAge.getSubText().trim())) {
            age = Integer.parseInt(mAge.getSubText().trim());
        }

        String land = null;
        if (!TextUtils.isEmpty(mLocation.getText())) {
            land = mLocation.getText().toString().trim();
        }

        Integer sex = null;
        if (LocalWrapUser.getUser().getUserInfo().getUserSex() != 0) {
            sex = LocalWrapUser.getUser().getUserInfo().getUserSex() == WrapUserInfo.SEX_BOY ? 2 : 1;
        }
        if (age != null || !TextUtils.isEmpty(land) || sex != null) {
            Apic.updateUserInfo(age, land, sex)
                    .tag(TAG)
                    .indeterminate(this)
                    .callback(new Callback<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                            finish();
                        }
                    }).fireFreely();
        }
    }

    @Override
    public void onBackPressed() {
        if (LocalWrapUser.getUser().isLogin()) {
            submitUserInfo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_CODE_LOCATION) {
            mLocation.setText(LocalWrapUser.getUser().getUserInfo().getLand());
        }

        if (resultCode == RESULT_OK && requestCode == REQ_CLIP_HEAD_IMAGE_PAGE) {
            updateUserImage();
        }

        if (resultCode == RESULT_OK && requestCode == REQ_CODE_USER_NAME) {
            updateUserInfo();
        }
    }

    private void showAgePicker() {
        OptionPicker picker = new OptionPicker(this, mAgeList);
        picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
        picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));
        picker.setPressedTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        picker.setTopHeight(50);
        picker.setAnimationStyle(R.style.BottomDialogAnimation);
        picker.setOffset(2);
        if (LocalWrapUser.getUser().getUserInfo().getAge() == null) {
            mSelectAgeListIndex = 73;
        } else {
            if (LocalWrapUser.getUser().getUserInfo().getAge() <= 100) {
                mSelectAgeListIndex = 100 - LocalWrapUser.getUser().getUserInfo().getAge();
            } else {
                mSelectAgeListIndex = 100;
            }
        }
        picker.setSelectedItem(mAgeList[mSelectAgeListIndex]);
        picker.setTextColor(ContextCompat.getColor(getActivity(), R.color.text));
        WheelView.DividerConfig lineConfig = new WheelView.DividerConfig(0);
        lineConfig.setColor(ContextCompat.getColor(getActivity(), R.color.split));
        picker.setDividerConfig(lineConfig);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if (!TextUtils.isEmpty(item)) {
                    mAge.setSubText(String.valueOf(100 - index));
                    LocalWrapUser.getUser().getUserInfo().setAge(100 - index);
                    mSelectAgeListIndex = index;
                }
            }
        });
        picker.show();
    }


}
