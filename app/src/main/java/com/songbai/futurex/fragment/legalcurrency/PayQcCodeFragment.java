package com.songbai.futurex.fragment.legalcurrency;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.image.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/8/14
 */
public class PayQcCodeFragment extends UniqueActivity.UniFragment {

    @BindView(R.id.qcCode)
    ImageView mQRCode;
    @BindView(R.id.saveQRCode)
    TextView mSaveQcCode;
    Unbinder unbinder;
    private String mImagePath;

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mImagePath = extras.getString(ExtraKeys.IMAGE_PATH);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_qc_code, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        GlideApp
                .with(this)
                .load(mImagePath)
                .into(mQRCode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.saveQRCode)
    public void onViewClicked() {
        mQRCode.setDrawingCacheEnabled(true);
        mQRCode.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //设置绘制缓存背景颜色
        mQRCode.setDrawingCacheBackgroundColor(Color.WHITE);

        ImageUtils.saveImageToGallery(getContext(), loadBitmapFromView(mQRCode));
        ToastUtil.show(R.string.save_success);
    }

    private static Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        //如果不设置canvas画布为白色，则生成透明
//        c.drawColor(Color.WHITE);
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }
}
