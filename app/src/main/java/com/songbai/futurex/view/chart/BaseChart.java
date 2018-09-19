package com.songbai.futurex.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * base chart for kline and trend chart
 */
public abstract class BaseChart extends View {

    private enum Action {
        NONE,
        TOUCH, // touch line
        DRAG,
    }

    private static final int FONT_SIZE_DP = 9;
    private static final int FONT_BIG_SIZE_DP = 10;
    private static final int TEXT_MARGIN_WITH_LINE_DP = 4;
    private static final int RECT_PADDING_DP = 6;
    private static final int HEIGHT_TIME_LINE_DP = 30;
    private static final int HEIGHT_MAIN_CHART = 240;
    private static final int HEIGHT_VOL_CHART = 70;
    private static final int HEIGHT_SUB_CHART = 90;

    private static final int WHAT_LONG_PRESS = 1;
    private static final int WHAT_ONE_CLICK = 2;
    private static final int DELAY_ONE_CLICK = 100;
    private static final float CLICK_PIXELS = 1;

    public static Paint sPaint;

    private Path mPath;
    private RectF mRectF;
    private StringBuilder mStringBuilder;
    private Handler mHandler;
    private Paint.FontMetrics mFontMetrics;
    protected ChartCfg mChartCfg;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private OnViewScaleChangedListener mOnViewScaleChangedListener;

    public interface OnViewScaleChangedListener {
        void onViewScaleChanged(float scale);
    }

    protected float mFontSize;
    protected int mFontHeight;
    protected float mOffset4CenterText; // center y of text + this will draw the text in center you want
    protected float mBigFontSize;
    protected int mBigFontHeight;
    protected float mOffset4CenterBigText;
    protected float mOneXAxisWidth;
    protected int mTextMargin; // The margin between text and baseline
    protected int mTimeLineHeight;
    protected int mMainChartHeight;
    protected int mVolChartHeight;
    protected int mSubChartHeight;

    private int mXRectPadding;
    private int mYRectPadding;

    private int mTouchIndex; // The position of cross when touch view
    private float mDownX;
    private float mDownY;
    private Action mAction;
    //private long mElapsedTime;

    private float mTransactionX;
    private float mMaxTransactionX;
    private float mPreviousTransactionX;
    private float mStartX;
    private int mStartPointOffset;

    public BaseChart(Context context) {
        super(context);
        init();
    }

    public BaseChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPath = new Path();
        mRectF = new RectF();
        mStringBuilder = new StringBuilder();
        mHandler = new ChartHandler();
        mChartCfg = new ChartCfg();
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), mOnScaleGestureListener);
        mGestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);

        // text font
        mFontSize = sp2Px(FONT_SIZE_DP);
        sPaint.setTextSize(mFontSize);
        mFontMetrics = sPaint.getFontMetrics();
        sPaint.getFontMetrics(mFontMetrics);
        mFontHeight = (int) (mFontMetrics.bottom - mFontMetrics.top);
        mOffset4CenterText = calOffsetY4TextCenter();

        // big text font
        mBigFontSize = sp2Px(FONT_BIG_SIZE_DP);
        sPaint.setTextSize(mBigFontSize);
        sPaint.getFontMetrics(mFontMetrics);
        mBigFontHeight = (int) (mFontMetrics.bottom - mFontMetrics.top);
        mOffset4CenterBigText = calOffsetY4TextCenter();

        // constant
        mTextMargin = (int) dp2Px(TEXT_MARGIN_WITH_LINE_DP);
        mXRectPadding = (int) dp2Px(RECT_PADDING_DP);
        mYRectPadding = mXRectPadding / 5;
        mTimeLineHeight = (int) dp2Px(HEIGHT_TIME_LINE_DP);
        mMainChartHeight = (int) dp2Px(HEIGHT_MAIN_CHART);
        mVolChartHeight = (int) dp2Px(HEIGHT_VOL_CHART);
        mSubChartHeight = (int) dp2Px(HEIGHT_SUB_CHART);

        // gesture
        mTouchIndex = -1;
        mAction = Action.NONE;
    }

    private class ChartHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_LONG_PRESS || msg.what == WHAT_ONE_CLICK) {
                if (mChartCfg.isEnableCrossLine()) {
                    mAction = Action.TOUCH;
                    MotionEvent e = (MotionEvent) msg.obj;
                    triggerCrossLinesRedraw(e);
                }
            }
        }
    }

    public ChartCfg getChartCfg() {
        return mChartCfg;
    }

    public void notifyCfgChanged() {
        requestLayout();
        redraw();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = getChartHeight() + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(getDimension(desiredWidth, widthMeasureSpec), getDimension(desiredHeight, heightMeasureSpec));
    }

    private int getChartHeight() {
        int height = mMainChartHeight + mTimeLineHeight;

        if (mChartCfg.isVolEnable()) {
            height += mVolChartHeight;
        }

        if (mChartCfg.isSubIndexEnable()) {
            height += mSubChartHeight;
        }
        return height;
    }

    private int getDimension(int desiredSize, int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = desiredSize;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int width = getWidth() - getPaddingLeft() - getPaddingRight();

        if (mChartCfg.isEnableDrag()) {
            mOneXAxisWidth = calculateOneXAxisWidth();
            mMaxTransactionX = calculateMaxTransactionX();
        }

        calculateBaseLines(mChartCfg.getBaseLineArray());
        drawBaseLines(mChartCfg.getBaseLineArray(), left, top, width, mMainChartHeight, canvas);

        int volTop = -1;
        if (mChartCfg.isVolEnable()) {
            volTop = top + mMainChartHeight + mTimeLineHeight;

            calculateVolBaseLines(mChartCfg.getVolBaseLineArray());
            drawVolBaseLines(mChartCfg.getVolBaseLineArray(), left, volTop, width, mVolChartHeight, canvas);
        }

        drawData(left, top, width, mMainChartHeight, mChartCfg.isVolEnable(), volTop, mVolChartHeight, canvas);

        drawTimeLine(left, top + mMainChartHeight, width, mTimeLineHeight, canvas);

        drawIndex(left, top, width, mMainChartHeight, mTimeLineHeight, mVolChartHeight, mSubChartHeight, canvas);

        if (mTouchIndex >= 0) {
            if (mChartCfg.isEnableCrossLine()) {
                drawCrossLines(mChartCfg.isVolEnable(), mTouchIndex,
                        left, top, width, mMainChartHeight,
                        left, volTop, width, mVolChartHeight,
                        canvas);

                onCrossLinesAppear(mTouchIndex);
            }
        } else {
            onCrossLinesDisappear(mTouchIndex);
        }
    }

    protected float calculateMaxTransactionX() {
        return 0;
    }

    protected float calculateOneXAxisWidth() {
        return 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mTouchIndex >= 0) { // touchLines appear
            getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(event);
    }

    private ScaleGestureDetector.SimpleOnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private float mPreScale = 1;

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scale = scaleGestureDetector.getScaleFactor() * mPreScale;

            if (scale > 2.04) {
                scale = 2.04f;
            }

            if (scale < 0.5) {
                scale = 0.5f;
            }

            if (mPreScale != scale) {
                mChartCfg.setViewScale(scale);
                mPreScale = scale;
                if (mOnViewScaleChangedListener != null) {
                    mOnViewScaleChangedListener.onViewScaleChanged(scale);
                }

                redraw();
                return true;
            }

            return false;
        }
    };

    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Message message = mHandler.obtainMessage(WHAT_LONG_PRESS, e);
            mHandler.sendMessage(message);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleGestureDetector.onTouchEvent(event);

        mGestureDetector.onTouchEvent(event);

        switch (event.getActionMasked() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mAction == Action.TOUCH) {
                    Message message = mHandler.obtainMessage(WHAT_ONE_CLICK, event);
                    mHandler.sendMessageDelayed(message, DELAY_ONE_CLICK);
                }

                mDownX = event.getX();
                mDownY = event.getY();

                mStartX = event.getX() - mPreviousTransactionX;

                return true;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(mDownX - event.getX()) < CLICK_PIXELS
                        || Math.abs(mDownY - event.getY()) < CLICK_PIXELS) {
                    return false;
                }

                mHandler.removeMessages(WHAT_ONE_CLICK);

                if (mAction == Action.TOUCH) {
                    return triggerCrossLinesRedraw(event);
                }

                if (mChartCfg.isEnableDrag() && (mAction == Action.NONE || mAction == Action.DRAG)) {
                    double distance = Math.abs(event.getX() - (mStartX + mPreviousTransactionX));
                    if (distance > mOneXAxisWidth) {
                        mAction = Action.DRAG;
                        mTransactionX = event.getX() - mStartX;
                        if (mTransactionX > mMaxTransactionX) {
                            mTransactionX = mMaxTransactionX;
                        }
                        if (mTransactionX < 0) {
                            mTransactionX = 0;
                        }
                        int newStartPointOffset = calculatePointOffset();
                        if (mStartPointOffset != newStartPointOffset) {
                            mStartPointOffset = newStartPointOffset;
                            redraw();
                        }
                        return true;
                    }
                }

                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mAction == Action.TOUCH && mHandler.hasMessages(WHAT_ONE_CLICK)) {
                    mHandler.removeMessages(WHAT_ONE_CLICK);
                    mAction = Action.NONE;
                    if (mTouchIndex != -1) {
                        mTouchIndex = -1;
                        redraw();
                    }
                } else if (mAction == Action.DRAG) {
                    mAction = Action.NONE;
                    mPreviousTransactionX = mTransactionX;
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean triggerCrossLinesRedraw(MotionEvent event) {
        int newTouchIndex = calculateTouchIndex(event);
        if (newTouchIndex != mTouchIndex && checkTouchIndexValid(newTouchIndex)) {
            mTouchIndex = newTouchIndex;
            redraw();
            return true;
        }
        return false;
    }

    protected boolean checkTouchIndexValid(int newTouchIndex) {
        return false;
    }

    protected boolean isDragging() {
        return mAction == Action.DRAG;
    }

    public float getDragTransX() {
        return mTransactionX;
    }

    protected int calculateTouchIndex(MotionEvent e) {
        return -1;
    }

    protected int calculatePointOffset() {
        return (int) (mTransactionX / mOneXAxisWidth);
    }

    /**
     * draw touch lines, total area of top without middle
     *
     * @param indexesEnable
     * @param touchIndex
     * @param left
     * @param top
     * @param width
     * @param height
     * @param left2
     * @param volTop
     * @param width2
     * @param volHeight
     * @param canvas
     */
    protected void drawCrossLines(boolean indexesEnable, int touchIndex,
                                  int left, int top, int width, int height,
                                  int left2, int volTop, int width2, int volHeight,
                                  Canvas canvas) {
    }

    protected void onCrossLinesAppear(int touchIndex) {
    }

    protected void onCrossLinesDisappear(int touchIndex) {
    }

    protected abstract void calculateBaseLines(float[] baselines);

    protected void calculateVolBaseLines(double[] indexesBaseLines) {
    }

    /**
     * draw main chart baselines
     *
     * @param baselines
     * @param left
     * @param top
     * @param width
     * @param height
     * @param canvas
     */
    protected abstract void drawBaseLines(float[] baselines, int left, int top, int width, int height, Canvas canvas);

    /**
     * draw vol chart baselines
     *
     * @param volBaseLines
     * @param left
     * @param volTop
     * @param width
     * @param volHeight
     * @param canvas
     */
    protected void drawVolBaseLines(double[] volBaseLines, int left, int volTop, int width, int volHeight, Canvas canvas) {
    }

    /**
     * draw real time data
     *
     * @param left
     * @param top
     * @param width
     * @param height
     * @param canvas
     */
    protected abstract void drawData(int left, int top, int width, int height, boolean volEnable,
                                     int volTop, int volHeight, Canvas canvas);

    /**
     * draw real unstable data
     *
     * @param indexesEnable
     * @param left
     * @param top
     * @param width
     * @param topPartHeight
     * @param left2
     * @param top2
     * @param width1
     * @param bottomPartHeight
     * @param canvas
     */
    protected void drawUnstableData(boolean indexesEnable,
                                    int left, int top, int width, int topPartHeight,
                                    int left2, int top2, int width1, int bottomPartHeight,
                                    Canvas canvas) {

    }


    /**
     * draw time line
     *
     * @param left   the left(x) coordinate of time line text
     * @param top    the top(y coordinate）of time line text
     * @param width
     * @param timeLineHeight
     * @param canvas
     */
    protected abstract void drawTimeLine(int left, int top, int width, int timeLineHeight, Canvas canvas);

    /**
     * draw hints for chart
     *
     * @param left
     * @param top
     * @param width
     * @param mainChartHeight
     * @param timeLineHeight
     * @param volChartHeight
     * @param subChartHeight
     * @param canvas
     */
    protected abstract void drawIndex(int left, int top, int width,
                                      int mainChartHeight, int timeLineHeight, int volChartHeight, int subChartHeight,
                                      Canvas canvas);

    /**
     * 返回主图部分
     *
     * @return
     */
    protected int getMainChartHeight() {
        return mMainChartHeight;
    }

    /**
     * 返回 vol 指标区域
     *
     * @return
     */
    protected int getVolChartHeight() {
        return mVolChartHeight;
    }

    /**
     * 返回唯一 path
     *
     * @return
     */
    protected Path getPath() {
        if (mPath == null) {
            mPath = new Path();
        }
        mPath.reset();
        return mPath;
    }

    public int getStartPointOffset() {
        return mStartPointOffset;
    }

    /**
     * 返回唯一 RectF
     *
     * @return
     */
    protected RectF getRectF() {
        if (mRectF == null) {
            mRectF = new RectF();
        }
        mRectF.setEmpty();
        return mRectF;
    }

    /**
     * 返回唯一 StringBuilder
     *
     * @return
     */
    protected StringBuilder getStringBuilder() {
        if (mStringBuilder == null) {
            mStringBuilder = new StringBuilder();
        }
        mStringBuilder.setLength(0);
        return mStringBuilder;
    }

    /**
     * 计算图表数据区的 y 坐标
     *
     * @param y 介于 最大和最小 baseline（baseline[0] ~ baseline[length - 1]） 之间的合法数据
     * @return
     */
    protected float getChartY(float y) {
        float[] baseLines = mChartCfg.getBaseLineArray();

        // When values beyond baselines, eg. ma. return -1
        if (y > baseLines[0] || y < baseLines[baseLines.length - 1]) {
            return -1;
        }

        int height = getMainChartHeight();
        y = (baseLines[0] - y) / (baseLines[0] - baseLines[baseLines.length - 1]) * height;
        return y + getPaddingTop();
    }

    protected float getVolChartY(double y) {
        double[] indexesBaseLines = mChartCfg.getVolBaseLineArray();

        // When values beyond indexes baselines, eg. mv. return -1
        if (y > indexesBaseLines[0] || y < indexesBaseLines[indexesBaseLines.length - 1]) {
            return -1;
        }

        int height = getVolChartHeight();

        float chartY = (float) ((indexesBaseLines[0] - y) * 1.0f /
                (indexesBaseLines[0] - indexesBaseLines[indexesBaseLines.length - 1]) * height);

        return chartY + getPaddingTop() + getMainChartHeight() + mTimeLineHeight;
    }

    /**
     * 计算图表数据区的 x 坐标
     *
     * @param index 0 ~ xAxis 之间的合法值
     * @return
     */
    protected float getChartX(int index) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        return getPaddingLeft() + index * width * 1.0f / mChartCfg.getXAxis();
    }

    /**
     * getCharX(index) 的逆运算
     *
     * @param chartX
     * @return
     */
    protected int getIndexOfXAxis(float chartX) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        chartX = chartX - getPaddingLeft();
        return (int) (chartX * mChartCfg.getXAxis() / width);
    }

    /**
     * the startXY of drawText is at baseline, this is used to add some offsetY for text center
     * centerY = y + offsetY
     *
     * @return offsetY
     */
    protected float calOffsetY4TextCenter() {
        Paint.FontMetrics fontMetrics = sPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        return fontHeight / 2 - fontMetrics.bottom;
    }

    /**
     * this method is used to calculate a rectF for the text that will be drew
     * we add some left-right padding for the text, just for nice
     *
     * @param textX     left of text
     * @param textY     y of text baseline
     * @param textWidth
     * @return
     */
    protected RectF getTextBgRectF(float textX, float textY, float textWidth) {
        mRectF.left = textX - mXRectPadding;
        mRectF.top = textY + mFontMetrics.top - mYRectPadding;
        mRectF.right = textX + textWidth + mXRectPadding;
        mRectF.bottom = textY + mFontMetrics.bottom + mYRectPadding;
        return mRectF;
    }

    public float dp2Px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public float sp2Px(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }

    public String formatNumber(float value) {
        return formatNumber(value, mChartCfg.getNumberScale());
    }

    protected String formatNumber(double value, int numberScale) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();

        decimalFormat.setMaximumFractionDigits(numberScale);
        decimalFormat.setMinimumFractionDigits(numberScale);
        decimalFormat.setMinimumIntegerDigits(1);
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        String v = decimalFormat.format(value);
        return v;
    }

    protected void redraw() {
        invalidate(0, 0, getWidth(), getHeight());
    }

    protected void reset() {
        mTouchIndex = -1; // The position of cross when touch view
        mDownX = 0;
        mDownY = 0;
        mAction = Action.NONE;

        mTransactionX = 0;
        mMaxTransactionX = 0;
        mPreviousTransactionX = 0;
        mStartX = 0;
        mStartPointOffset = 0;
    }

    public void setOnViewScaleChangedListener(OnViewScaleChangedListener onViewScaleChangedListener) {
        mOnViewScaleChangedListener = onViewScaleChangedListener;
    }
}
