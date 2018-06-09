package com.songbai.futurex.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.ImageSelectActivity;

import java.io.File;

/**
 * @author yangguangda
 * @date 2018/6/8
 */
public class ImagePicker {
    /**
     * 打开相机的请求码
     */
    public static final int REQ_CODE_TAKE_PHONE_FROM_CAMERA = 379;
    /**
     * 打开图册的请求码
     */
    public static final int REQ_CODE_TAKE_PHONE_FROM_PHONES = 600;

    //打开自定义画廊
    public static final int IMAGE_TYPE_OPEN_CUSTOM_GALLERY = 4750;

    private Activity mActivity;
    private Fragment mFragment;
    private int mMaxNum = 1;
    private boolean mGallery;
    private File mFile;
    private boolean customGallery = true;

    private ImagePicker(Activity activity) {
        mActivity = activity;
    }

    private ImagePicker(Fragment fragment) {
        mFragment = fragment;
    }

    public static ImagePicker create(Activity activity) {
        return new ImagePicker(activity);
    }

    public static ImagePicker create(Fragment fragment) {
        return new ImagePicker(fragment);
    }

    public ImagePicker openCamera() {
        mGallery = false;
        return this;
    }

    public ImagePicker openGallery() {
        mGallery = true;
        return this;
    }

    public ImagePicker maxSelectNum(int maxNum) {
        mMaxNum = maxNum;
        return this;
    }

    public ImagePicker crop(int maxNum) {
        mMaxNum = maxNum;
        return this;
    }

    public ImagePicker imageCustomGallery() {
        customGallery = true;
        return this;
    }

    public ImagePicker imageSystemGallery() {
        customGallery = false;
        return this;
    }

    public void forResult() {
        Intent intent;
        if (mGallery) {
            if (customGallery) {
                intent = createIntent(ImageSelectActivity.class);
                intent.putExtra(ExtraKeys.IMAGE, mMaxNum);
                open(intent, IMAGE_TYPE_OPEN_CUSTOM_GALLERY);
            } else {
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    open(intent, REQ_CODE_TAKE_PHONE_FROM_PHONES);
                }
            }
        } else {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && PermissionUtil.cameraIsCanUse()) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mFile = FileUtils.createFile(getContext().getString(R.string.app_name) + System.currentTimeMillis() + "image.jpg");
                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，防止拿到
                Uri mMBitmapUri = Uri.fromFile(mFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mMBitmapUri);
                open(intent, REQ_CODE_TAKE_PHONE_FROM_CAMERA);
            } else {
                ToastUtil.show(getContext().getString(R.string.please_open_camera_permission));
            }
        }
    }

    public static String getGalleryBitmapPath(Context context, Intent data) {
        if (data != null && data.getData() != null) {
            Uri photosUri = data.getData();
            if (photosUri != null) {
                ContentResolver contentResolver = context.getContentResolver();
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

    private Intent createIntent(Class<?> target) {
        if (mActivity != null) {
            return new Intent(mActivity, target);
        }
        if (mFragment != null) {
            return new Intent(mFragment.getContext(), target);
        }
        throw new ExceptionInInitializerError();
    }

    private Context getContext() {
        if (mActivity != null) {
            return mActivity;
        }
        if (mFragment != null) {
            mFragment.getContext();
        }
        throw new ExceptionInInitializerError();
    }

    private void open(Intent intent, int requestCode) {
        if (mActivity != null) {
            mActivity.startActivityForResult(intent, requestCode);
        }
        if (mFragment != null) {
            mFragment.startActivityForResult(intent, requestCode);
        }
    }
}
