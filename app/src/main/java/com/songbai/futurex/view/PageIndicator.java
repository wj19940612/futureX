package com.songbai.futurex.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.songbai.futurex.R;


public class PageIndicator extends View {

    private int mCount;
    private int mInterval;
    private ColorStateList mIndicatorColor;
    private ColorStateList mSelectedColor;
    private int mPointRadius;

    // 运行矩形的指示器
    private boolean mRectIndicators;
    private int mRectWidth;
    private int mRectHeight;

    private int mCurrentIndex;

    private static Paint sPaint;
    private static RectF sRect;

    private boolean mInfinite;

    public PageIndicator(Context context) {
        super(context);

        init();
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        processAttrs(attrs);

        init();
    }

    private void init() {
        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sRect = new RectF();
        mCurrentIndex = 0;
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PageIndicator);

        int defaultInterval = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                getResources().getDisplayMetrics());
        int defaultRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                getResources().getDisplayMetrics());

        int defaultRectWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics());
        int defaultRectHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                getResources().getDisplayMetrics());

        mCount = typedArray.getInt(R.styleable.PageIndicator_indicators, 1);
        mInterval = typedArray.getDimensionPixelSize(R.styleable.PageIndicator_indicatorsInterval, defaultInterval);
        mPointRadius = typedArray.getDimensionPixelSize(R.styleable.PageIndicator_pointRadius, defaultRadius);
        mRectWidth = typedArray.getDimensionPixelSize(R.styleable.PageIndicator_rectWidth, defaultRectWidth);
        mRectHeight = typedArray.getDimensionPixelSize(R.styleable.PageIndicator_rectHeight, defaultRectHeight);
        mRectIndicators = typedArray.getBoolean(R.styleable.PageIndicator_rectIndicators, false);

        mIndicatorColor = typedArray.getColorStateList(R.styleable.PageIndicator_indicatorColor);
        mSelectedColor = typedArray.getColorStateList(R.styleable.PageIndicator_selectedColor);

        if (mIndicatorColor == null) {
            mIndicatorColor = ColorStateList.valueOf(Color.BLACK);
        }
        if (mSelectedColor == null) {
            mSelectedColor = ColorStateList.valueOf(Color.WHITE);
        }
        mInfinite = typedArray.getBoolean(R.styleable.PageIndicator_infinite, false);

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        if (mRectIndicators) {
            width = measureDimension(widthMeasureSpec, mRectWidth * mCount + (mCount - 1) * mInterval);
            height = measureDimension(heightMeasureSpec, mRectHeight);
        } else {
            width = measureDimension(widthMeasureSpec, mPointRadius * 2 * mCount + (mCount - 1) * mInterval);
            height = measureDimension(heightMeasureSpec, mPointRadius * 2);
        }

        setMeasuredDimension(width, height);
    }

    private int measureDimension(int measureSpec, int defaultDimension) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = specSize;

        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = Math.min(defaultDimension, specSize);
                break;
        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = 0;
        int top = 0;

        for (int i = 0; i < mCount; i++) {
            int right;
            int bottom;
            if (mRectIndicators) {
                right = left + mRectWidth;
                bottom = top + mRectHeight;
            } else {
                right = left + mPointRadius * 2;
                bottom = top + mPointRadius * 2;
            }

            if (i == mCurrentIndex) {
                sPaint.setColor(mSelectedColor.getColorForState(getDrawableState(), 0));
            } else {
                sPaint.setColor(mIndicatorColor.getColorForState(getDrawableState(), 0));
            }

            sRect.set(left, top, right, bottom);
            if (mRectIndicators) {
                canvas.drawRect(sRect, sPaint);
            } else {
                canvas.drawOval(sRect, sPaint);
            }
            left = right + mInterval;
        }
    }

    public void setCount(int count) {
        if (mCount != count) {
            mCount = count;
            invalidate();
            requestLayout();
        }
    }

    public void move(int index) {
        if (mInfinite) {
            index = (index + mCount) % mCount;
        }

        if (index < 0 || index >= mCount) return;

        if (mCurrentIndex != index) {
            mCurrentIndex = index;
            invalidate();
        }
    }

    public void next() {
        move(mCurrentIndex + 1);
    }

    public void back() {
        move(mCurrentIndex - 1);
    }
}
