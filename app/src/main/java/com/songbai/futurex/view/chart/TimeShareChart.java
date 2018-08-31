package com.songbai.futurex.view.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.songbai.futurex.R;
import com.songbai.futurex.model.KTrend;
import com.songbai.futurex.utils.Display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeShareChart extends View {

    private Paint mPaint;
    private Paint mShaderPaint;
    private LinearGradient mShader;
    private List<KTrend> mKTrends;
    private boolean mIsInited;
    private double mMaxClose;
    private Path mPath;
    private float baseArea;


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
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.green));

        mShaderPaint = new Paint();
        mShaderPaint.setAntiAlias(true);
        mShaderPaint.setStyle(Paint.Style.FILL);
//        mShaderPaint.setColor(ContextCompat.getColor(getContext(), R.color.green));
//        mShader = new LinearGradient(0, 0, 0, 100, new int[]{ContextCompat.getColor(getContext(), R.color.white), ContextCompat.getColor(getContext(), R.color.alphaGreen)}, null, Shader.TileMode.CLAMP);
//        mShaderPaint.setShader(mShader);
        mShaderPaint.setShader(new LinearGradient(0, 0, 0, Display.dp2Px(52, getResources()), new int[]{ContextCompat.getColor(getContext(), R.color.white_20), ContextCompat.getColor(getContext(), R.color.alphaGreen)}, null, Shader.TileMode.CLAMP));

        baseArea = Display.dp2Px(12, getResources());
        mPath = new Path();
    }

    public void updateData(List<KTrend> data) {
        if (data != null && data.size() > 0 && !mIsInited) {
            getDrawData(data);
            mIsInited = true;
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


    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("zzz", "onDraw");
        super.onDraw(canvas);
        if (mKTrends == null || mKTrends.size() == 0) return;


        canvas.rotate(180);
        canvas.translate(-getWidth(), -getHeight());
        double dx = (double) getWidth() / mKTrends.size();
        double dy = (double) (getHeight() - baseArea) / mMaxClose;
        int startX;
        int startY;
        int endX;
        int endY;
//        mPath.moveTo(0,0);
//        mPath.lineTo(200,0);
//        mPath.lineTo(200,getHeight());
//        mPath.lineTo(0,getHeight()-60);
//        mPath.lineTo(0,0);
//        canvas.drawPath(mPath,mShaderPaint);
        for (int i = 1; i < mKTrends.size(); i++) {
            startX = (int) (getWidth() - (dx * (i - 1)));
            startY = (int) (dy * mKTrends.get(i - 1).getClosePrice() + baseArea);
            endX = (int) (getWidth() - (dx * (i)));
            endY = (int) (dy * mKTrends.get(i).getClosePrice() + baseArea);
            Log.e("zzz", "mKTrends:" + i + "  startY:" + startY + " endY:" + endY);
            mPath.moveTo(startX, 0);
            mPath.lineTo(endX, 0);
            mPath.lineTo(endX, endY);
            mPath.lineTo(startX, startY);
            mPath.lineTo(startX, 0);
            canvas.drawPath(mPath, mShaderPaint);
        }

    }
}
