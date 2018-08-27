package com.songbai.futurex.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.websocket.model.DeepData;

import java.math.RoundingMode;
import java.util.List;

/**
 * Modified by john on 2018/6/20
 * <p>
 * Description: 深度图内的梯度线图
 * <p>
 * APIs:
 */
public class DeepV extends View {

    private List<DeepData> mBuyDeepList;
    private List<DeepData> mSellDeepList;

    private Path mPath;
    private Paint mPaint;

    private float mLineWidth;
    private float mTextSize;
    private float mTextHeight;
    private float mOffset4CenterText;
    private float mTextMarginLeft;
    private float mTextMarginBottom;
    private float mVolumeTextSize;
    private float mVolumeTextHeight;
    private float mOffset4CenterVolumeText;

    private Paint.FontMetrics mFontMetrics;

    private double[] mBaselines;

    public DeepV(Context context) {
        super(context);
        init();
    }

    public DeepV(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLineWidth = dp2Px(2);
        mTextSize = sp2Px(12);
        mPaint.setTextSize(mTextSize);
        mFontMetrics = mPaint.getFontMetrics();
        mPaint.getFontMetrics(mFontMetrics);
        mTextHeight = mFontMetrics.bottom - mFontMetrics.top;
        mOffset4CenterText = calOffsetY4TextCenter();

        mTextMarginBottom = dp2Px(7);
        mTextMarginLeft = dp2Px(12);

        mVolumeTextSize = sp2Px(10);
        mPaint.setTextSize(mVolumeTextSize);
        mFontMetrics = mPaint.getFontMetrics();
        mPaint.getFontMetrics(mFontMetrics);
        mVolumeTextHeight = mFontMetrics.bottom - mFontMetrics.top;
        mOffset4CenterVolumeText = calOffsetY4TextCenter();

        mBaselines = new double[8];
    }

    protected float calOffsetY4TextCenter() {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        return fontHeight / 2 - fontMetrics.bottom;
    }

    protected Path getPath() {
        if (mPath == null) {
            mPath = new Path();
        }
        mPath.reset();
        return mPath;
    }

    public void setDeepList(List<DeepData> buyDeepList, List<DeepData> sellDeepList) {
        mBuyDeepList = buyDeepList;
        mSellDeepList = sellDeepList;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBuyDeepList == null || mBuyDeepList.isEmpty()) return;

        if (mSellDeepList == null || mSellDeepList.isEmpty()) return;

        calculateBaselines();

        drawBaselineText(canvas);

        Path path = getPath();
        float x = 0;
        float y = 0;
        float firstX = 0;
        for (int i = mBuyDeepList.size() - 1; i >= 0; i--) {
            DeepData deepData = mBuyDeepList.get(i);
            x = getBuyX(deepData.getPrice());
            y = getY(deepData.getTotalCount());
            if (path.isEmpty()) {
                firstX = x;
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }

            if (i - 1 >= 0) {
                x = getBuyX(mBuyDeepList.get(i - 1).getPrice());
                path.lineTo(x, y);
            }
        }
        setBuyDeepLinePaint(mPaint);
        canvas.drawPath(path, mPaint);
        // fill path
        path.lineTo(x, getHeight());
        path.lineTo(firstX, getHeight());
        path.close();
        setBuyDeepLineFillPaint(mPaint);
        canvas.drawPath(path, mPaint);

        path = getPath();
        for (int i = 0; i < mSellDeepList.size(); i++) {
            DeepData deepData = mSellDeepList.get(i);
            x = getSellX(deepData.getPrice());
            y = getY(deepData.getTotalCount());
            if (path.isEmpty()) {
                firstX = x;
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            if (i + 1 < mSellDeepList.size()) {
                x = getSellX(mSellDeepList.get(i + 1).getPrice());
                path.lineTo(x, y);
            }
        }
        setSellDeepLinePaint(mPaint);
        canvas.drawPath(path, mPaint);
        // fill path
        path.lineTo(x, getPaddingTop() + getHeight());
        path.lineTo(firstX, getPaddingTop() + getHeight());
        path.close();
        setSellDeepLineFillPaint(mPaint);
        canvas.drawPath(path, mPaint);

        drawBuySellTexT(canvas);
    }

    private void drawBaselineText(Canvas canvas) {
        setVolumeTextPaint(mPaint);
        for (int i = 0; i < mBaselines.length; i++) {
            if (i == 0 || i >= mBaselines.length - 2) continue;

            String volume = formatVolume(mBaselines[i]);
            float textX = getWidth() - getPaddingRight() - mTextMarginLeft - mPaint.measureText(volume);
            float textY = getY(mBaselines[i]) + mOffset4CenterVolumeText;
            canvas.drawText(volume, textX, textY, mPaint);
        }
    }

    private String formatVolume(double baseline) {
        if (baseline < 1000) {
            return FinanceUtil.formatWithScale(baseline, 1, RoundingMode.DOWN);
        } else if (baseline < 1000000) {
            return FinanceUtil.formatWithScale(baseline / 1000, 2, RoundingMode.DOWN) + "k";
        } else {
            return FinanceUtil.formatWithScale(baseline / 1000000, 2, RoundingMode.DOWN) + "m";
        }
    }

    private void calculateBaselines() {
        double maxBuyVolume = mBuyDeepList.get(mBuyDeepList.size() - 1).getTotalCount();
        double minBuyVolume = mBuyDeepList.get(0).getTotalCount();
        double maxSellVolume = mSellDeepList.get(mSellDeepList.size() - 1).getTotalCount();
        double minSellVolume = mSellDeepList.get(0).getTotalCount();

        double maxV = Math.max(maxBuyVolume, maxSellVolume);
        double minV = Math.min(minBuyVolume, minSellVolume);

        double interval = (maxV - minV) / (mBaselines.length - 1);
        maxV = maxV + interval; // 扩大上限
        interval = (maxV - minV) / (mBaselines.length - 1);
        mBaselines[0] = maxV;
        mBaselines[mBaselines.length - 1] = minV;
        for (int i = 1; i < mBaselines.length - 1; i++) {
            mBaselines[i] = mBaselines[i - 1] - interval;
        }
    }

    private void drawBuySellTexT(Canvas canvas) {
        String bid = getContext().getString(R.string.bid);
        setBidTextPaint(mPaint);
        float textX = getPaddingLeft() + mTextMarginLeft;
        float textY = getPaddingTop() + getHeight() - mTextMarginBottom - mTextHeight / 2 + mOffset4CenterText;
        canvas.drawText(bid, textX, textY, mPaint);

        String ask = getContext().getString(R.string.ask);
        setAskTextPaint(mPaint);
        float textWidth = mPaint.measureText(ask);
        textX = getWidth() - getPaddingRight() - mTextMarginLeft - textWidth;
        textY = getPaddingTop() + getHeight() - mTextMarginBottom - mTextHeight / 2 + mOffset4CenterText;
        canvas.drawText(ask, textX, textY, mPaint);
    }

    private void setAskTextPaint(Paint paint) {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.red));
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(null);
        paint.setTextSize(mTextSize);
    }

    private void setVolumeTextPaint(Paint paint) {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text99));
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(null);
        paint.setTextSize(mVolumeTextSize);
    }


    private void setBidTextPaint(Paint paint) {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.green));
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(null);
        paint.setTextSize(mTextSize);
    }

    private void setSellDeepLineFillPaint(Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, getHeight(),
                Color.parseColor("#3FFD5656"),
                Color.parseColor("#3FFD5656"),
                Shader.TileMode.CLAMP));
    }

    private void setBuyDeepLineFillPaint(Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, getHeight(),
                Color.parseColor("#3F3FC87D"),
                Color.parseColor("#3F3FC87D"),
                Shader.TileMode.CLAMP));
    }

    private void setSellDeepLinePaint(Paint paint) {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.red));
        paint.setStrokeWidth(mLineWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setShader(null);
    }

    private void setBuyDeepLinePaint(Paint paint) {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.green));
        paint.setStrokeWidth(mLineWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setShader(null);
    }

    public float dp2Px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public float sp2Px(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }

    public float getBuyX(double price) {
        double max = mBuyDeepList.get(0).getPrice();
        double min = mBuyDeepList.get(mBuyDeepList.size() - 1).getPrice();
        return (float) ((price - min) / (max - min) * getHalfWidth());
    }

    private int getHalfWidth() {
        return getWidth() / 2;
    }

    public float getY(double volume) {
        double height = getHeight() - getPaddingTop() - getPaddingBottom();
        return (float) (getPaddingTop() +
                (mBaselines[0] - volume) / (mBaselines[0] - mBaselines[mBaselines.length - 1]) * height);
    }

    public float getSellX(double price) {
        double max = mSellDeepList.get(mSellDeepList.size() - 1).getPrice();
        double min = mSellDeepList.get(0).getPrice();
        return (float) (getHalfWidth() + (price - min) / (max - min) * getHalfWidth());
    }
}
