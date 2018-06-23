package com.songbai.futurex.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.songbai.futurex.R;


/**
 * Created by ${wangJie} on 2017/4/27.
 */

public class RedPointTipTextView extends AppCompatTextView {

    //顶部的小红点显示
    private int mPointVisibility;
    private Paint mPointPaint;
    private int mRedPointRadius;
    private int mPointHorizontalPadding;
    private int mPointVerticalPadding;

    public RedPointTipTextView(Context context) {
        this(context, null);
    }

    public RedPointTipTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842884);
    }

    public RedPointTipTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RedPointTipTextView);
        mPointVisibility = typedArray.getInt(R.styleable.RedPointTipTextView_redPointTipVisibility, 8);
        mRedPointRadius = typedArray.getDimensionPixelSize(R.styleable.RedPointTipTextView_redPointRadius, 10);
        mPointHorizontalPadding = typedArray.getDimensionPixelSize(R.styleable.RedPointTipTextView_pointHorizontalPadding, 0);
        mPointVerticalPadding = typedArray.getDimensionPixelSize(R.styleable.RedPointTipTextView_pointVerticalPadding, 0);
        mPointPaint = new Paint();
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPointVisibility == VISIBLE) {
            mPointPaint.setColor(ContextCompat.getColor(getContext(), R.color.red));
            mPointPaint.setAntiAlias(true);
            mPointPaint.setDither(true);
            mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            float cx = getWidth() - mRedPointRadius * 2 - mPointHorizontalPadding;
            float cy = getPaddingTop() + mRedPointRadius * 2 + mPointVerticalPadding;
            canvas.drawCircle(cx, cy, mRedPointRadius, mPointPaint);
        } else {
            canvas.drawCircle(0, 0, 0, mPointPaint);
        }
    }

    public void setRedPointVisibility(int visibility) {
        mPointVisibility = visibility;
        invalidate();
    }

    public void setPointHorizontalPadding(int pointHorizontalPadding) {
        mPointHorizontalPadding = pointHorizontalPadding;
    }
}
