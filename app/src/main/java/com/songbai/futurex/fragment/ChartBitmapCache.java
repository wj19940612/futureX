package com.songbai.futurex.fragment;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class ChartBitmapCache {
    private static ChartBitmapCache sCache;

    private Map<String, Bitmap> mBitmaps;

    public ChartBitmapCache() {
        mBitmaps = new HashMap<>();
    }

    public static ChartBitmapCache getInstance() {
        if (sCache == null) {
            sCache = new ChartBitmapCache();
        }
        return sCache;
    }

    public void putBitmap(String pairs, Bitmap bitmap) {
        mBitmaps.put(pairs, bitmap);
    }

    public Bitmap getBimtap(String pairs) {
        return mBitmaps.get(pairs);
    }


}
