package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.dialog.UploadUserImageDialogFragment;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_senior_certification, container, false);
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

    @OnClick({R.id.frontImg, R.id.backImg, R.id.handIdCardImg, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.frontImg:
                UploadUserImageDialogFragment uploadUserImageDialogFragment = UploadUserImageDialogFragment.newInstance(
                        UploadUserImageDialogFragment.IMAGE_TYPE_CLIPPING_IMAGE_SCALE_OR_MOVE, "",
                        -1, getString(R.string.please_add_certification_front_pic),
                        1, R.drawable.ic_authentication_idcard_front);
                uploadUserImageDialogFragment.show(getChildFragmentManager());
                break;
            case R.id.backImg:
                uploadUserImageDialogFragment = UploadUserImageDialogFragment.newInstance(
                        UploadUserImageDialogFragment.IMAGE_TYPE_CLIPPING_IMAGE_SCALE_OR_MOVE, "",
                        -1, getString(R.string.please_add_certification_back_pic),
                        1, R.drawable.ic_authentication_idcard_back);
                uploadUserImageDialogFragment.show(getChildFragmentManager());
                break;
            case R.id.handIdCardImg:
                uploadUserImageDialogFragment = UploadUserImageDialogFragment.newInstance(
                        UploadUserImageDialogFragment.IMAGE_TYPE_CLIPPING_IMAGE_SCALE_OR_MOVE, "",
                        -1, getString(R.string.please_add_handle_certification_pic),
                        1, R.drawable.ic_authentication_idcard_handheld);
                uploadUserImageDialogFragment.show(getChildFragmentManager());
                break;
            case R.id.submit:
                break;
            default:
        }
    }
}
