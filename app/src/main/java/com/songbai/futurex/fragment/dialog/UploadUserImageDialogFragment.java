package com.songbai.futurex.fragment.dialog;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.ImageSelectActivity;
import com.songbai.futurex.activity.LookBigPictureActivity;
import com.songbai.futurex.activity.mine.ClipHeadImageActivity;
import com.songbai.futurex.utils.FileUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.PermissionUtil;
import com.songbai.futurex.utils.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/12/19.
 * 上传用户头像
 */

public class UploadUserImageDialogFragment extends BottomDialogFragment {

    private static final String KEY_IMAGE_URL = "KEY_IMAGE_URL";
    private static final String KEY_TYPE = "type";
    private static final String KEY_IMAGE_URL_INDEX = "key_image_url_index";
    private static final String KEY_SELECT_IMAGE_HINT_TEXT = "key_select_image_hint_text";
    private static final String KEY_SELECT_IMAGE_HINT = "key_select_image_hint";
    private static final String KEY_SELECT_IMAGE_MAX_AMOUNT = "key_select_image_max_amount";

    //不做任何处理，只返回图片地址
    public static final int IMAGE_TYPE_NOT_DEAL = 1750;
    //上传头像类似裁剪 图片可移动，缩放
    public static final int IMAGE_TYPE_CLIPPING_IMAGE_SCALE_OR_MOVE = 3750;
    // 实名认证 型裁剪  拍照后，获取固定区域的图片
    public static final int IMAGE_TYPE_CLIPPING_IMMOBILIZATION_AREA = 2750;
    //打开自定义画廊
    public static final int IMAGE_TYPE_OPEN_CUSTOM_GALLERY = 4750;

    /**
     * 打开相机的请求码
     */
    private static final int REQ_CODE_TAKE_PHONE_FROM_CAMERA = 379;
    /**
     * 打开图册的请求码
     */
    public static final int REQ_CODE_TAKE_PHONE_FROM_PHONES = 600;
    /**
     * 打开自定义裁剪页面的请求码
     */
    public static final int REQ_CLIP_HEAD_IMAGE_PAGE = 144;
    /**
     * 打开区域拍照页面的请求吗
     */
    private static final int REQ_CODE_AREA_TAKE_PHONE = 46605;
    /**
     * 打开自定义画廊页面请求码
     */
    private static final int REQ_CODE_TAKE_PHONE_FROM_GALLERY = 46606;

    @BindView(R.id.selectImageHint)
    AppCompatTextView mSelectImageHint;
    @BindView(R.id.takePhoneFromCamera)
    AppCompatTextView mTakePhoneFromCamera;
    @BindView(R.id.takePhoneFromGallery)
    AppCompatTextView mTakePhoneFromGallery;
    @BindView(R.id.takePhoneCancel)
    AppCompatTextView mTakePhoneCancel;
    @BindView(R.id.lookHDPicture)
    AppCompatTextView mLookHDPicture;
    @BindView(R.id.textExample)
    TextView mTextExample;
    @BindView(R.id.hintImg)
    ImageView mHintImg;
    @BindView(R.id.imageHintGroup)
    LinearLayout mImageHintGroup;

    private Unbinder mBind;
    private File mFile;

    private OnImagePathListener mOnImagePathListener;

    private String HDPictureUrl;
    private int mImageDealType;
    private int mImageUrlIndex;
    private String mSelectImageHintText;
    private int mImageMaxSelectAmount;
    private int mHintImageID;

    public interface OnImagePathListener {
        void onImagePath(int index, String imagePath);
    }

    public UploadUserImageDialogFragment() {

    }

    public static UploadUserImageDialogFragment newInstance(int type) {
        return newInstance(type, "");
    }

    public static UploadUserImageDialogFragment newInstance(int type, String url) {
        return newInstance(type, url, -1);
    }

    public static UploadUserImageDialogFragment newInstance(int type, int imageIndex) {
        return newInstance(type, "", imageIndex);
    }

    public static UploadUserImageDialogFragment newInstance(int type, String url, int imageIndex) {
        return newInstance(type, url, imageIndex, "");
    }

    public static UploadUserImageDialogFragment newInstance(int type, String url, int imageIndex, String selectImageHint) {
        return newInstance(type, url, imageIndex, selectImageHint, 0);
    }

    public static UploadUserImageDialogFragment newInstance(int type, String url, int imageIndex, String selectImageHint, int maxImageAmount) {
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        args.putString(KEY_IMAGE_URL, url);
        args.putInt(KEY_IMAGE_URL_INDEX, imageIndex);
        args.putString(KEY_SELECT_IMAGE_HINT_TEXT, selectImageHint);
        args.putInt(KEY_SELECT_IMAGE_MAX_AMOUNT, maxImageAmount);
        UploadUserImageDialogFragment fragment = new UploadUserImageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static UploadUserImageDialogFragment newInstance(int type, String url, int imageIndex, String selectImageHint, int maxImageAmount, int hintImageID) {
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        args.putString(KEY_IMAGE_URL, url);
        args.putInt(KEY_IMAGE_URL_INDEX, imageIndex);
        args.putString(KEY_SELECT_IMAGE_HINT_TEXT, selectImageHint);
        args.putInt(KEY_SELECT_IMAGE_MAX_AMOUNT, maxImageAmount);
        args.putInt(KEY_SELECT_IMAGE_HINT, hintImageID);
        UploadUserImageDialogFragment fragment = new UploadUserImageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public UploadUserImageDialogFragment setOnImagePathListener(OnImagePathListener onImagePathListener) {
        mOnImagePathListener = onImagePathListener;
        return this;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnImagePathListener) {
            mOnImagePathListener = (OnImagePathListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageDealType = getArguments().getInt(KEY_TYPE, IMAGE_TYPE_NOT_DEAL);
            HDPictureUrl = getArguments().getString(KEY_IMAGE_URL);
            mImageUrlIndex = getArguments().getInt(KEY_IMAGE_URL_INDEX, -1);
            mSelectImageHintText = getArguments().getString(KEY_SELECT_IMAGE_HINT_TEXT, "");
            mImageMaxSelectAmount = getArguments().getInt(KEY_SELECT_IMAGE_MAX_AMOUNT, 0);
            mHintImageID = getArguments().getInt(KEY_SELECT_IMAGE_HINT, -1);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        || mImageDealType == IMAGE_TYPE_CLIPPING_IMAGE_SCALE_OR_MOVE
        if (!TextUtils.isEmpty(HDPictureUrl)) {
            mLookHDPicture.setVisibility(View.VISIBLE);
        } else {
            mLookHDPicture.setVisibility(View.GONE);
        }
        mSelectImageHint.setVisibility(TextUtils.isEmpty(mSelectImageHintText) ? View.GONE : View.VISIBLE);
        mSelectImageHint.setText(mSelectImageHintText);
        if (mHintImageID != -1) {
            mHintImg.setImageResource(mHintImageID);
            mTextExample.setVisibility(View.VISIBLE);
            mHintImg.setVisibility(View.VISIBLE);
        } else {
            mTextExample.setVisibility(View.INVISIBLE);
            mHintImg.setVisibility(View.INVISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_upload_user_image, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
        int screenHeight = dm.heightPixels;
        int statusBarHeight = getStatusBarHeight(getContext());
        int dialogHeight = screenHeight - statusBarHeight;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = dialogHeight;
        win.setAttributes(params);
    }

    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.takePhoneFromCamera, R.id.takePhoneFromGallery, R.id.takePhoneCancel, R.id.lookHDPicture, R.id.hintImg, R.id.imageHintGroup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takePhoneFromCamera:
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && PermissionUtil.cameraIsCanUse()) {
                    if (mImageDealType == IMAGE_TYPE_CLIPPING_IMMOBILIZATION_AREA) {
                        openAreaTakePage();
                    } else {
                        openSystemCameraPage();
                    }
                } else {
                    ToastUtil.show(getString(R.string.please_open_camera_permission));
                }

                break;
            case R.id.takePhoneFromGallery:
                if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    openGalleryPage();
                } else {
                    ToastUtil.show(R.string.sd_is_not_useful);
                }
                break;
            case R.id.takePhoneCancel:
                this.dismissAllowingStateLoss();
                break;

            case R.id.lookHDPicture:
                Launcher.with(getActivity(), LookBigPictureActivity.class)
                        .putExtra(ExtraKeys.IMAGE_PATH, HDPictureUrl)
                        .execute();
                this.dismissAllowingStateLoss();
                break;
            case R.id.hintImg:
                break;
            case R.id.imageHintGroup:
                this.dismissAllowingStateLoss();
                break;
            default:
        }
    }

    private void openGalleryPage() {
        if (mImageDealType == IMAGE_TYPE_OPEN_CUSTOM_GALLERY) {
            Intent openGalleryIntent = new Intent(getContext(), ImageSelectActivity.class);
            openGalleryIntent.putExtra(ExtraKeys.IMAGE, mImageMaxSelectAmount);
            startActivityForResult(openGalleryIntent, REQ_CODE_TAKE_PHONE_FROM_GALLERY);
        } else {
            Intent openAlbumIntent = new Intent(
                    Intent.ACTION_PICK);
            openAlbumIntent.setType("image/*");
            if (openAlbumIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(openAlbumIntent, REQ_CODE_TAKE_PHONE_FROM_PHONES);
            }
        }
    }

    private void openSystemCameraPage() {
        Intent openCameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        mFile = FileUtils.createFile(getString(R.string.app_name) + System.currentTimeMillis() + "image.jpg");
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，防止拿到
        Uri mMBitmapUri = Uri.fromFile(mFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMBitmapUri);
        startActivityForResult(openCameraIntent, REQ_CODE_TAKE_PHONE_FROM_CAMERA);
    }

    private void openAreaTakePage() {
        if (PermissionUtil.cameraIsCanUse()) {
//            startActivityForResult(new Intent(getActivity(), AreaTakePhoneActivity.class), REQ_CODE_AREA_TAKE_PHONE);
        } else {
            ToastUtil.show(getString(R.string.please_open_camera_permission));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == FragmentActivity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_TAKE_PHONE_FROM_CAMERA:
                    if (mFile != null) {
                        Uri mMBitmapUri = Uri.fromFile(mFile);
                        if (mMBitmapUri != null) {
                            if (!TextUtils.isEmpty(mMBitmapUri.getPath())) {
                                dealImagePath(mMBitmapUri.getPath());
                            }
                        }
                    }
                    break;
                case REQ_CODE_TAKE_PHONE_FROM_PHONES:
                    String galleryBitmapPath = getGalleryBitmapPath(data);
                    if (!TextUtils.isEmpty(galleryBitmapPath)) {
                        dealImagePath(galleryBitmapPath);
                    }
                    break;
                case REQ_CODE_AREA_TAKE_PHONE:
//                    String imageUrl = data.getStringExtra(Launcher.EX_PAYLOAD);
//                    if (!TextUtils.isEmpty(imageUrl)) {
//                        dealImagePath(imageUrl);
//                    }
                    break;
                case REQ_CODE_TAKE_PHONE_FROM_GALLERY:
                    String imagePath = data.getStringExtra(ExtraKeys.IMAGE_PATH);
                    if (!TextUtils.isEmpty(imagePath)) {
                        dealImagePath(imagePath);
                    }
                    break;
                default:
            }
        } else {
            dismissAllowingStateLoss();
        }

    }

    private String getGalleryBitmapPath(Intent data) {
        if (data != null && data.getData() != null) {
            Uri photosUri = data.getData();
            if (photosUri != null) {
                ContentResolver contentResolver = getActivity().getContentResolver();
                Cursor cursor = contentResolver.query(photosUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    if (!TextUtils.isEmpty(path)) {
                        return path;
                    }
                    cursor.close();
                } else {
                    if (!TextUtils.isEmpty(photosUri.getPath())) {
                        return photosUri.getPath();
                    } else {
                        return photosUri.toString();
                    }
                }
            }
        }
        return null;
    }

    private void dealImagePath(String imaUri) {
        if (mImageDealType == IMAGE_TYPE_CLIPPING_IMAGE_SCALE_OR_MOVE) {
            Intent intent = new Intent(getActivity(), ClipHeadImageActivity.class);
            intent.putExtra(ExtraKeys.IMAGE_PATH, imaUri);
            getActivity().startActivityForResult(intent, REQ_CLIP_HEAD_IMAGE_PAGE);
        } else {
            if (mOnImagePathListener != null) {
                mOnImagePathListener.onImagePath(mImageUrlIndex, imaUri.replace("/raw//", ""));
            }
        }
        dismissAllowingStateLoss();
    }

//    //调用系统裁剪，有问题，有些手机不支持裁剪后获取图片
//    private void cropImage(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
//        intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
//        intent.putExtra("aspectY", 1);// x:y=1:1
//        intent.putExtra("outputX", 600);//图片输出大小
//        intent.putExtra("outputY", 600);
//        intent.putExtra("output", uri);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 返回格式
//        startActivityForResult(intent, REQ_CODE_CROP_IMAGE);
//    }
}
