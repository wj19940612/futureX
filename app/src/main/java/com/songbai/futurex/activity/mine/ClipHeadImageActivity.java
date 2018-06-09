package com.songbai.futurex.activity.mine;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.clipimage.ClipImageLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClipHeadImageActivity extends BaseActivity {

    @BindView(R.id.clipImageLayout)
    ClipImageLayout mClipImageLayout;
    @BindView(R.id.cancel)
    AppCompatTextView mCancel;
    @BindView(R.id.complete)
    AppCompatTextView mComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_head_image);
        ButterKnife.bind(this);
        String imagePath = getIntent().getStringExtra(ExtraKeys.IMAGE_PATH);
        String forMi5 = imagePath.replace("/raw//", "");
        mClipImageLayout.setZoomImageViewImage(forMi5);
    }

    @OnClick({R.id.cancel, R.id.complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.complete:
                submitFile();
                break;
        }
    }

    private void submitFile() {
        Bitmap clipBitmap = mClipImageLayout.clip();
//        String picture = ImageUtils.bitmapToBase64(clipBitmap);
        String base64 = ImageUtils.compressImageToBase64(clipBitmap, getActivity());
        submitPortraitPath(base64);
    }

    private void submitPortraitPath(final String data) {
        // TODO: 2018/5/29
        Apic.submitPortraitPath(data)
                .timeout(10_000)
                .indeterminate(this)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        requestUserInfo();
                    }
                })
                .fire();
    }

    private void requestUserInfo() {
        // TODO: 2018/5/29
        Apic.findUserInfo()
                .tag(TAG)
                .callback(new Callback<Resp<UserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<UserInfo> resp) {
                        LocalUser.getUser().setUserInfo(resp.getData());
//                        ToastUtil.show(R.string.modify_success);
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .fire();
    }

}
