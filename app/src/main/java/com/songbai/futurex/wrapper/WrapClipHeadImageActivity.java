package com.songbai.futurex.wrapper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.songbai.futurex.R;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.clipimage.ClipImageLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WrapClipHeadImageActivity extends WrapBaseActivity {

    public static final String KEY_CLIP_USER_IMAGE = "CLIP_USER_IMAGE";
    @BindView(R.id.clipImageLayout)
    ClipImageLayout mClipImageLayout;
    @BindView(R.id.cancel)
    AppCompatTextView mCancel;
    @BindView(R.id.complete)
    AppCompatTextView mComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_clip_head_image);
        ButterKnife.bind(this);
        translucentStatusBar();

        Intent intent = getIntent();
        String bitmapPath = intent.getStringExtra(KEY_CLIP_USER_IMAGE);
        //修复米5的图片显示问题
        String forMi5 = bitmapPath.replace("/raw//", "");
        Log.d(TAG, "传入的地址" + bitmapPath);
        mClipImageLayout.setZoomImageViewImage(forMi5);
    }


    @OnClick({R.id.cancel, R.id.complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.complete:
                Bitmap clipBitmap = mClipImageLayout.clip(Bitmap.Config.RGB_565);
                if (clipBitmap != null) {
//                    String bitmapToBase64 = ImageUtils.compressImageToBase64(clipBitmap);
                    String bitmapToBase64 = ImageUtils.bitmapToBase64(clipBitmap);
                    clipBitmap.recycle();
                    confirmUserNewHeadImage(bitmapToBase64);
                }
                break;
        }
    }

    private void confirmUserNewHeadImage(String bitmapToBase64) {
        Apic.uploadImage(bitmapToBase64)
                .indeterminate(this)
                .tag(TAG)
//                .setRetryPolicy(new DefaultRetryPolicy(100000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                .callback(new Callback4Resp<Resp<String>, String>() {
                    @Override
                    protected void onRespData(String data) {
                        uploadUserHeadImageUrl(data);
                    }
                })
                .fire();
    }

    private void uploadUserHeadImageUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            Apic.updateUserHeadImagePath(url)
                    .indeterminate(this)
                    .tag(TAG)
                    .callback(new Callback4Resp<Resp<String>, String>() {
                        @Override
                        protected void onRespData(String data) {
                            if (!TextUtils.isEmpty(data)) {
                                WrapUserInfo userInfo = LocalWrapUser.getUser().getUserInfo();
                                userInfo.setUserPortrait(data);
                                LocalWrapUser.getUser().setUserInfo(userInfo);
                            }
                            setResult(RESULT_OK);
                            finish();
                        }
                    })
                    .fire();
        }
    }
}
