package com.songbai.futurex.view.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.songbai.futurex.R;
import com.songbai.futurex.fragment.ChartBitmapCache;
import com.songbai.futurex.model.KTrend;
import com.songbai.futurex.utils.Display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeShareChart extends View {

    private Paint mPaint;
    private Paint mShaderPaint;
    private List<KTrend> mKTrends;
    private double mMaxClose;
    private Path mPath;
    private Path mStrokePath;
    private float baseArea;

    private String mPair;
//    private Map<String, Bitmap> mBitmaps;
    private boolean mForceRefresh;


    public TimeShareChart(Context context) {
        this(context, null);
    }

    public TimeShareChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeShareChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//消除锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.green));

        mShaderPaint = new Paint();
        mShaderPaint.setAntiAlias(true);
        mShaderPaint.setStyle(Paint.Style.FILL);
        mShaderPaint.setShader(new LinearGradient(0, 0, 0, Display.dp2Px(52, getResources()), new int[]{ContextCompat.getColor(getContext(), R.color.white_20), ContextCompat.getColor(getContext(), R.color.alphaGreen)}, null, Shader.TileMode.CLAMP));

        baseArea = Display.dp2Px(12, getResources());
        mPath = new Path();
        mStrokePath = new Path();
    }

    public void updateData(String pair, List<KTrend> data) {
        Log.e("zzz", "updateData:" + pair);
        if (!TextUtils.isEmpty(pair)) {
            mPair = pair;
        }
        if (data != null && data.size() > 0) {
            getDrawData(data);
            mForceRefresh = true;
            invalidate(0, 0, getWidth(), getHeight());
        } else {
            if (mKTrends != null) {
                mKTrends.clear();
            }
            invalidate(0, 0, getWidth(), getHeight());
        }
    }

    private void getDrawData(List<KTrend> data) {
        mKTrends = new ArrayList<>();
        for (KTrend kTrend : data) {
            if (kTrend.getClosePrice() >= 0.0) {
                mKTrends.add(kTrend);
            }
        }
//        if (data.size() < 20) {
//            mKTrends = data;
//        } else {
//            mKTrends = data.subList(data.size() - 21, data.size() - 1);
//            mKTrends = new ArrayList<>();
//            int di = data.size() / 20;
//            int mantissa = data.size() % 20;
//            for (int i = mantissa; i < data.size(); i += di) {
//                mKTrends.add(data.get(i));
//            }
//        }
        mMaxClose = Collections.max(mKTrends).getClosePrice();
    }

    public void justDraw(String pair, List<KTrend> data){
        Log.e("zzz", "justDraw:" + pair);
        if (!TextUtils.isEmpty(pair)) {
            mPair = pair;
        }

        if (data != null && data.size() > 0) {
            getDrawData(data);
            invalidate(0, 0, getWidth(), getHeight());
        } else {
            if (mKTrends != null) {
                mKTrends.clear();
            }
            invalidate(0, 0, getWidth(), getHeight());
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("zzz", "onDraw");
        super.onDraw(canvas);

        if (mKTrends == null || mKTrends.size() == 0) return;

        Bitmap pairBitmap = ChartBitmapCache.getInstance().getBimtap(mPair);

//        if((pairBitmap == null || mForceRefresh) && mPair.equals("btc_usdt")){
//            Log.e("zzz","test draw btc_usdt");
//            Bitmap mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas bitmapCanvas = new Canvas(mBitmap);
//            //转换坐标系
//            bitmapCanvas.rotate(180);
//            bitmapCanvas.translate(-getWidth(), -getHeight());
//
//            mStrokePath.moveTo(getWidth(), getHeight());
//            mStrokePath.lineTo(0,getHeight());
//
//            bitmapCanvas.drawPath(mStrokePath, mPaint);
//            canvas.drawBitmap(mBitmap, 0, 0, null);
//            ChartBitmapCache.getInstance().putBitmap(mPair, mBitmap);
//            mForceRefresh = false;
//            return;
//        }
        if (pairBitmap == null || mForceRefresh) {
            Log.e("zzz","draw bitmap:"+mPair);
            Bitmap mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(mBitmap);
            //转换坐标系
            bitmapCanvas.rotate(180);
            bitmapCanvas.translate(-getWidth(), -getHeight());

            double dx = (double) getWidth() / mKTrends.size();
            double dy = (double) (getHeight() - baseArea) / mMaxClose;

            int startX;
            int startY;
            int endX;
            int endY;
            mPath.reset();
            mPath.moveTo(getWidth(), 0);
            mPath.lineTo(getWidth(), (float) (dy * mKTrends.get(0).getClosePrice() + baseArea));
            mStrokePath.reset();
            mStrokePath.moveTo(getWidth(), (float) (dy * mKTrends.get(0).getClosePrice() + baseArea));
            for (int i = 1; i < mKTrends.size(); i++) {
                startX = (int) (getWidth() - (dx * (i - 1)));
                startY = (int) (dy * mKTrends.get(i - 1).getClosePrice() + baseArea);
                endX = (int) (getWidth() - (dx * (i)));
                endY = (int) (dy * mKTrends.get(i).getClosePrice() + baseArea);
//                Log.e("zzz", mPair + i + "  startY:" + startY + " endY:" + endY);
                mStrokePath.lineTo(startX, startY);
                mPath.lineTo(startX, startY);

                mStrokePath.lineTo(endX, endY);
                mPath.lineTo(endX, endY);
            }
            mPath.lineTo(0, 0);
            bitmapCanvas.drawPath(mPath, mShaderPaint);
            bitmapCanvas.drawPath(mStrokePath, mPaint);

            canvas.drawBitmap(mBitmap, 0, 0, null);
            ChartBitmapCache.getInstance().putBitmap(mPair, mBitmap);
            mForceRefresh = false;
        } else{
            Log.e("zzz","set bitmap:"+mPair);
            canvas.drawBitmap(pairBitmap, 0, 0, null);
        }

    }
}
