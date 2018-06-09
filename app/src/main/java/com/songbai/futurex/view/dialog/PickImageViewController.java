package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.SmartDialog;

/**
 * Modified by john on 2018/6/1
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class PickImageViewController extends SmartDialog.CustomViewController implements View.OnClickListener {
    /**
     * 打开相机的请求码
     */
    public static final int REQ_CODE_TAKE_PHONE_FROM_CAMERA = 379;
    /**
     * 打开图册的请求码
     */
    public static final int REQ_CODE_TAKE_PHONE_FROM_PHONES = 600;

    private OnClickListener mOnClickListener;
    private Context mContext;
    private TextView mSelectImageHint;
    private SmartDialog mDialog;
    private ImageView mHintImg;
    private int mHintImage;
    private TextView mTextExample;
    private String mHintText;

    public interface OnClickListener {
        void onCameraClick();

        void onGalleryClick();
    }

    public PickImageViewController(Context context, OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_pick_image, null);
    }

    @Override
    public void onInitView(View view, final SmartDialog dialog) {
        mDialog = dialog;
        mSelectImageHint = (TextView) view.findViewById(R.id.selectImageHint);
        LinearLayout imageHintGroup = (LinearLayout) view.findViewById(R.id.imageHintGroup);
        imageHintGroup.setOnClickListener(this);
        mTextExample = (TextView) view.findViewById(R.id.textExample);
        mHintImg = (ImageView) view.findViewById(R.id.hintImg);
        TextView takePhoneFromCamera = (TextView) view.findViewById(R.id.takePhoneFromCamera);
        takePhoneFromCamera.setOnClickListener(this);
        TextView takePhoneFromGallery = (TextView) view.findViewById(R.id.takePhoneFromGallery);
        takePhoneFromGallery.setOnClickListener(this);
        TextView takePhoneCancel = (TextView) view.findViewById(R.id.takePhoneCancel);
        takePhoneCancel.setOnClickListener(this);
        setHintImage(mHintImage);
        setHintText(mHintText);
    }

    public void setHintText(String hintText) {
        mHintText = hintText;
        if (mSelectImageHint != null) {
            mSelectImageHint.setText(hintText);
        }
    }

    public void setHintImage(@DrawableRes int hintImage) {
        mHintImage = hintImage;
        if (mTextExample != null) {
            mTextExample.setVisibility(hintImage <= 0 ? View.INVISIBLE : View.VISIBLE);
        }
        if (mHintImg != null) {
            mHintImg.setVisibility(hintImage <= 0 ? View.INVISIBLE : View.VISIBLE);
            mHintImg.setImageResource(hintImage);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageHintGroup:
                mDialog.dismiss();
                break;
            case R.id.takePhoneFromCamera:
                if (mOnClickListener != null) {
                    mOnClickListener.onCameraClick();
                }
                break;
            case R.id.takePhoneFromGallery:
                if (mOnClickListener != null) {
                    mOnClickListener.onGalleryClick();
                }
                break;
            case R.id.takePhoneCancel:
                mDialog.dismiss();
                break;
            default:
        }
    }
}
