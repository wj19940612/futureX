package com.songbai.futurex.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import sbai.com.glide.GlideApp;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class LookBigPictureActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.imageView)
    PhotoView mImageView;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    private String mPortrait;
    private int mDelete;
    private int mMissAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_big_picture);
        ButterKnife.bind(this);
        initData(getIntent());
        initTitleBar();
        if (mMissAvatar == 0) {
            GlideApp.with(this).load(mPortrait)
                    .thumbnail(0.1f)
                    .error(R.drawable.ic_default_avatar_big)
                    .dontAnimate()
                    .into(mImageView);
        } else {
            GlideApp.with(this).load(mPortrait)
                    .thumbnail(0.1f)
                    .error(R.drawable.ic_default_image)
                    .dontAnimate()
                    .into(mImageView);
        }

        mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    showSaveImageDialog();
                } else {
                    ToastUtil.show(R.string.sd_is_not_useful);
                }
                return false;
            }
        });
    }

    private void showSaveImageDialog() {
        SmartDialog.solo(getActivity(), getString(R.string.save_to_the_gallery))
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog) {
                        dialog.dismiss();
                        if (!TextUtils.isEmpty(mPortrait)) {
                            downLoadImage(mPortrait);
                        } else {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_avatar_big);
                            saveBitmapToGallery(bitmap);
                        }
                    }
                }).show();
    }

    private void downLoadImage(final String portrait) {
        GlideApp.with(getActivity())
                .asBitmap()
                .load(portrait)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        saveBitmapToGallery(resource);
                    }
                });
    }

    private void saveBitmapToGallery(Bitmap resource) {
        if (resource != null) {
            File file = ImageUtils.getUtil().saveGalleryBitmap(getActivity(), resource, getFileName());
            if (file != null && file.exists()) {
                ToastUtil.show(getString(R.string.image_save_to_, getFileName()));
            } else {
                ToastUtil.show(R.string.image_save_fail);
            }
        }
    }

    private String getFileName() {
        return System.currentTimeMillis() + ".jpg";
    }


    private void initData(Intent intent) {
        mPortrait = intent.getStringExtra(ExtraKeys.PORTRAIT);
        if (mPortrait == null) {
            // TODO: 2018/5/29  
//            mPortrait = Preference.get().getBigImage();
        }
        mDelete = intent.getIntExtra(ExtraKeys.PIC_DELETE, -1);
        mMissAvatar = intent.getIntExtra(ExtraKeys.MISS_AVATAR, 0);
    }

    private void initTitleBar() {
        if (mMissAvatar == 0) {
            mTitleBar.setVisibility(View.GONE);
        } else {
            mTitleBar.setVisibility(View.VISIBLE);
            if (mDelete != -1) {
                mTitleBar.setRightVisible(true);
                mTitleBar.setRightTextLeftImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_photo));
                mTitleBar.setOnRightViewClickListener(this);
            } else {
                mTitleBar.setRightVisible(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        showDeleteDialog();
    }

    private void showDeleteDialog() {
        SmartDialog.solo(getActivity())
                .setTitle(getString(R.string.is_sure_delete_photo))
                .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                })
                .setPositive(R.string.confirm, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog) {
                        setResult(RESULT_OK);
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

}
