package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.dialog.UploadUserImageDialogFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.RealNameAuthData;
import com.songbai.futurex.model.mine.UserAuth;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class SeniorCertificationFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.certificationType)
    IconTextRow mCertificationType;
    @BindView(R.id.realName)
    IconTextRow mRealName;
    @BindView(R.id.certificationNumber)
    IconTextRow mCertificationNumber;
    @BindView(R.id.frontImg)
    ImageView mFrontImg;
    @BindView(R.id.backImg)
    ImageView mBackImg;
    @BindView(R.id.handIdCardImg)
    ImageView mHandIdCardImg;
    @BindView(R.id.submit)
    TextView mSubmit;
    private Unbinder mBind;
    private UserAuth mUserAuth;
    private int mAuthenticationStatus;
    private boolean mCanEdit;
    private RealNameAuthData mRealNameAuthData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_senior_certification, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mAuthenticationStatus = extras.getInt(ExtraKeys.AUTHENTICATION_STATUS);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mCanEdit = mAuthenticationStatus == 4 || mAuthenticationStatus == 1;
        mSubmit.setVisibility(mCanEdit ? View.VISIBLE : View.INVISIBLE);
        getUserAuth();
    }

    private void getUserAuth() {
        Apic.getUserAuth()
                .callback(new Callback<Resp<UserAuth>>() {
                    @Override
                    protected void onRespSuccess(Resp<UserAuth> resp) {
                        setAuthInfo(resp.getData());
                    }
                })
                .fire();
    }

    private void setAuthInfo(UserAuth data) {
        if (data == null) {
            return;
        }
        mUserAuth = data;
        mRealNameAuthData = RealNameAuthData.Builder.create()
                .idType(mUserAuth.getIdType())
                .build();
        int idType = data.getIdType();
        int idCardTypeText;
        switch (idType) {
            case 0:
                idCardTypeText = R.string.mainland_id_card;
                break;
            case 1:
                idCardTypeText = R.string.tw_id_card;
                break;
            case 2:
                idCardTypeText = R.string.passport;
                break;
            default:
                idCardTypeText = R.string.mainland_id_card;
        }
        mCertificationType.setSubText(idCardTypeText);
        mRealName.setSubText(data.getName());
        mCertificationNumber.setSubText(data.getIdcardNum());
        if (!mCanEdit) {
            GlideApp
                    .with(this)
                    .load(data.getIdcardFrontImg())
                    .centerInside()
                    .into(mFrontImg);
            GlideApp
                    .with(this)
                    .load(data.getIdcardBackImg())
                    .centerInside()
                    .into(mBackImg);
            GlideApp
                    .with(this)
                    .load(data.getHandIdcardImg())
                    .centerInside()
                    .into(mHandIdCardImg);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.frontImg, R.id.backImg, R.id.handIdCardImg, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.frontImg:
            case R.id.backImg:
            case R.id.handIdCardImg:
                selectImage(view);
                break;
            case R.id.submit:
                confirmAuth();
                break;
            default:
        }
    }

    private void selectImage(final View view) {
        if (mCanEdit) {
            UploadUserImageDialogFragment uploadUserImageDialogFragment = UploadUserImageDialogFragment.newInstance(
                    UploadUserImageDialogFragment.IMAGE_TYPE_NOT_DEAL, "",
                    -1, getString(R.string.please_add_certification_front_pic),
                    1, R.drawable.ic_authentication_idcard_front);
            uploadUserImageDialogFragment.setOnImagePathListener(new UploadUserImageDialogFragment.OnImagePathListener() {
                @Override
                public void onImagePath(int index, String imagePath) {
                    String image = ImageUtils.compressImageToBase64(imagePath, getContext());
                    uploadImage(image, view);
                }
            });
            uploadUserImageDialogFragment.show(getChildFragmentManager());
        }
    }

    private void confirmAuth() {
        Apic.realNameAuth(mRealNameAuthData)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        ToastUtil.show(R.string.submit_success);
                        finish();
                    }
                })
                .fire();
    }

    private void uploadImage(String image, final View view) {
        Apic.uploadImage(image)
                .callback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        setImage(resp.getData(), view);
                    }
                })
                .fire();
    }

    private void setImage(String url, View view) {
        GlideApp
                .with(this)
                .load(url)
                .into((ImageView) view);
        switch (view.getId()) {
            case R.id.frontImg:
                mRealNameAuthData.setIdcardFrontImg(url);
                break;
            case R.id.backImg:
                mRealNameAuthData.setIdcardBackImg(url);
                break;
            case R.id.handIdCardImg:
                mRealNameAuthData.setHandIdcardImg(url);
                break;
            default:
        }
        checkIsEnable();
    }

    private void checkIsEnable() {
        if (!TextUtils.isEmpty(mRealNameAuthData.getIdcardFrontImg())
                && !TextUtils.isEmpty(mRealNameAuthData.getIdcardBackImg())
                && !TextUtils.isEmpty(mRealNameAuthData.getHandIdcardImg())) {
            mSubmit.setEnabled(true);
        } else {
            mSubmit.setEnabled(false);
        }
    }
}
