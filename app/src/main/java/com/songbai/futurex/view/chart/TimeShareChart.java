package com.songbai.futurex.view.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.ChartCache;
import com.songbai.futurex.model.KTrend;
import com.songbai.futurex.utils.Display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeShareChart extends View {

    public static final int CANVAS_HEIGHT = 52;
    public static final int BASE_AREA = 12;
    public static final float STROKE_WIDTH = 0.5f;

    private Paint mPaint;
    private Paint mShaderPaint;
    private List<KTrend> mKTrends;
    private double mMaxClose;
    private double mMinClose;
    private float baseArea;

    private String mPair;
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
        mPaint.setStrokeWidth(Display.dp2Px(STROKE_WIDTH, getResources()));

        mShaderPaint = new Paint();
        mShaderPaint.setAntiAlias(true);
        mShaderPaint.setStyle(Paint.Style.FILL);
        mShaderPaint.setShader(new LinearGradient(0, 0, 0, Display.dp2Px(CANVAS_HEIGHT, getResources()), new int[]{ContextCompat.getColor(getContext(), R.color.alphaGreen), ContextCompat.getColor(getContext(), R.color.alphaGreen)}, null, Shader.TileMode.CLAMP));

        baseArea = Display.dp2Px(BASE_AREA, getResources());
    }

    public void updateData(String pair, List<KTrend> data, double upDropSeed) {
//        Log.e("zzz", "updateData:" + pair);
        if (!TextUtils.isEmpty(pair)) {
            mPair = pair;
        }
        if (data != null && data.size() > 0) {
            getDrawData(data);
            initPaintColor(upDropSeed);
            mForceRefresh = true;
            invalidate(0, 0, getWidth(), getHeight());
        } else {
            if (mKTrends != null) {
                mKTrends.clear();
            }
            invalidate(0, 0, getWidth(), getHeight());
        }
    }

    private void initPaintColor(double upDropSeed) {
        if (upDropSeed < 0) {
            mPaint.setColor(ContextCompat.getColor(getContext(), R.color.red));
            mShaderPaint.setShader(new LinearGradient(0, 0, 0, Display.dp2Px(CANVAS_HEIGHT, getResources()), new int[]{ContextCompat.getColor(getContext(), R.color.alphaRed), ContextCompat.getColor(getContext(), R.color.alphaRed)}, null, Shader.TileMode.CLAMP));
        } else {
            mPaint.setColor(ContextCompat.getColor(getContext(), R.color.green));
            mShaderPaint.setShader(new LinearGradient(0, 0, 0, Display.dp2Px(CANVAS_HEIGHT, getResources()), new int[]{ContextCompat.getColor(getContext(), R.color.alphaGreen), ContextCompat.getColor(getContext(), R.color.alphaGreen)}, null, Shader.TileMode.CLAMP));
        }
    }

    private void getDrawData(List<KTrend> data) {
        mKTrends = new ArrayList<>();
        for (KTrend kTrend : data) {
            if (kTrend.getClosePrice() >= 0.0) {
                mKTrends.add(kTrend);
            }
        }
        mMaxClose = Collections.max(mKTrends).getClosePrice();
        mMinClose = Collections.min(mKTrends).getClosePrice();
    }

    public void justDraw(String pair, double upDropSeed) {
//        Log.e("zzz", "justDraw:" + pair);
        if (!TextUtils.isEmpty(pair)) {
            mPair = pair;
        }

        initPaintColor(upDropSeed);
        invalidate(0, 0, getWidth(), getHeight());
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        Log.e("zzz", "onDraw");
        super.onDraw(canvas);

        if (TextUtils.isEmpty(mPair)) return;

        drawPath(canvas);
    }

    private void drawPath(Canvas canvas) {
        Path path = ChartCache.getInstance().getPath(mPair);
        Path strokePath = ChartCache.getInstance().getStrokePath(mPair);

        canvas.rotate(180);
        canvas.translate(-getWidth(), -getHeight());
        if (path == null || strokePath == null || mForceRefresh) {

            if (mKTrends == null || mKTrends.size() == 0) return;

            if (mMaxClose == mMinClose) {
                drawHorizontalLine(canvas);
            } else {
                drawNormalLine(canvas);
            }
        } else {
            canvas.drawPath(path, mShaderPaint);
            canvas.drawPath(strokePath, mPaint);
        }
    }

    private void drawHorizontalLine(Canvas canvas) {
        Path path = new Path();
        path.reset();
        Path strokePath = new Path();
        strokePath.reset();
        path.moveTo(getWidth(), 0);
        path.lineTo(getWidth(), (getHeight() - baseArea) / 2 + baseArea);
        path.lineTo(0, (getHeight() - baseArea) / 2 + baseArea);
        path.lineTo(00, 0);

        strokePath.moveTo(getWidth(), (getHeight() - baseArea) / 2 + baseArea);
        strokePath.lineTo(0, (getHeight() - baseArea) / 2 + baseArea);
        canvas.drawPath(path, mShaderPaint);
        canvas.drawPath(strokePath, mPaint);

        ChartCache.getInstance().putPath(mPair, path);
        ChartCache.getInstance().putStrokePath(mPair, strokePath);
        mForceRefresh = false;
    }

    private void drawNormalLine(Canvas canvas) {
        double dx = (double) getWidth() / mKTrends.size();
        double dy = (double) (getHeight() - baseArea) / (mMaxClose - mMinClose);

        int startX;
        int startY;
        int endX;
        int endY;
        Path path = new Path();
        path.reset();
        Path strokePath = new Path();
        strokePath.reset();

        path.moveTo(getWidth(), 0);
        path.lineTo(getWidth(), (float) (dy * mKTrends.get(0).getClosePrice() + baseArea));
        strokePath.moveTo(getWidth(), (float) (dy * mKTrends.get(0).getClosePrice() + baseArea));
        for (int i = 1; i < mKTrends.size(); i++) {
            startX = (int) (getWidth() - (dx * (i - 1)));
            startY = (int) (dy * mKTrends.get(i - 1).getClosePrice() + baseArea);
            endX = (int) (getWidth() - (dx * (i)));
            endY = (int) (dy * mKTrends.get(i).getClosePrice() + baseArea);
//                Log.e("zzz", mPair + i + "  startY:" + startY + " endY:" + endY);
            strokePath.lineTo(startX, startY);
            path.lineTo(startX, startY);

            strokePath.lineTo(endX, endY);
            path.lineTo(endX, endY);
        }
        path.lineTo(0, 0);
        canvas.drawPath(path, mShaderPaint);
        canvas.drawPath(strokePath, mPaint);

        ChartCache.getInstance().putPath(mPair, path);
        ChartCache.getInstance().putPath(mPair, strokePath);
        mForceRefresh = false;
    }

    /**
     * 用bitmap的方式保存视图，在重复draw时直接绘制保存下来的bitmap(暂时不用这种方法)
     *
     * @param canvas
     * @param mPath
     * @param mStrokePath
     */
    private void drawBitmap(Canvas canvas, Path mPath, Path mStrokePath) {
        Bitmap pairBitmap = ChartCache.getInstance().getBitmap(mPair);

        if (pairBitmap == null || mForceRefresh) {

            if (mKTrends == null || mKTrends.size() == 0) return;

//            Log.e("zzz", "draw bitmap:" + mPair);
            Bitmap mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(mBitmap);
            //转换坐标系
            bitmapCanvas.rotate(180);
            bitmapCanvas.translate(-getWidth(), -getHeight());

            if (mMaxClose == mMinClose) {
                mPath.reset();
                mPath.moveTo(getWidth(), 0);
                mPath.lineTo(getWidth(), (getHeight() - baseArea) / 2 + baseArea);
                mPath.lineTo(0, (getHeight() - baseArea) / 2 + baseArea);
                mPath.lineTo(0, 0);
                mStrokePath.reset();

                mStrokePath.moveTo(getWidth(), (getHeight() - baseArea) / 2 + baseArea);
                mStrokePath.lineTo(0, (getHeight() - baseArea) / 2 + baseArea);
                bitmapCanvas.drawPath(mPath, mShaderPaint);
                bitmapCanvas.drawPath(mStrokePath, mPaint);

                canvas.drawBitmap(mBitmap, 0, 0, null);
                ChartCache.getInstance().putBitmap(mPair, mBitmap);
                mForceRefresh = false;
            } else {
                double dx = (double) getWidth() / mKTrends.size();
                double dy = (double) (getHeight() - baseArea) / (mMaxClose - mMinClose);

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
                ChartCache.getInstance().putBitmap(mPair, mBitmap);
                mForceRefresh = false;
            }
        } else {
//            Log.e("zzz", "set bitmap:" + mPair);
            canvas.drawBitmap(pairBitmap, 0, 0, null);
        }
    }
}
