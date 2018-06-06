package com.sbai.httplib;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Modified by john on 2018/6/6
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class BitmapCfg {

    private int maxWidth;
    private int maxHeight;
    private Bitmap.Config mConfig;
    private ImageView.ScaleType mScaleType;

    public BitmapCfg() {
        mConfig = Bitmap.Config.ARGB_8888;
    }

    public BitmapCfg(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.mConfig = Bitmap.Config.ARGB_8888;
        this.mScaleType = ImageView.ScaleType.FIT_XY;
    }

    public BitmapCfg(int maxWidth, int maxHeight, Bitmap.Config config, ImageView.ScaleType scaleType) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        mConfig = config;
        mScaleType = scaleType;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public Bitmap.Config getConfig() {
        return mConfig;
    }

    public ImageView.ScaleType getScaleType() {
        return mScaleType;
    }
}
