package com.songbai.futurex.fragment;

import android.graphics.Bitmap;
import android.graphics.Path;

import java.util.HashMap;
import java.util.Map;

public class ChartBitmapCache {
    private static ChartBitmapCache sCache;

    private Map<String, Bitmap> mBitmaps;
    private Map<String,Path> mPaths;
    private Map<String,Path> mStrokePaths;

    public ChartBitmapCache() {
        mBitmaps = new HashMap<>();
        mPaths = new HashMap<>();
        mStrokePaths = new HashMap<>();
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

    public Bitmap getBitmap(String pairs) {
        return mBitmaps.get(pairs);
    }

    public void putPath(String pairs,Path path){
        mPaths.put(pairs,path);
    }

    public Path getPath(String pairs){
        return mPaths.get(pairs);
    }

    public void putStrokePath(String pairs,Path path){
        mStrokePaths.put(pairs,path);
    }

    public Path getStrokePath(String pairs){
        return mStrokePaths.get(pairs);
    }


}
