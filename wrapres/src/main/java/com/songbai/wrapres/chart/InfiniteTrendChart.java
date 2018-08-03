package com.songbai.wrapres.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Modified by john on 24/02/2018
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class InfiniteTrendChart extends ChartView {

    private static final float VOLUME_WIDTH = 6f; //dp
    private static final float DATA_LINE_WIDTH = 2f;
    private static final float TOUCH_LINE_WIDTH = 1;
    private static final float OUTER_CIRCLE_RADIUS = 3.5f;
    private static final float INNER_CIRCLE_RADIUS = 2.5f;

    public interface OnDragListener {

        void onArriveLeft(TrendData theLeft);

        void onArriveRight(TrendData theRight);
    }

    public interface OnTouchLinesAppearListener {
        /**
         * @param data         current kline data
         * @param previousData previous data of current data, n & n - 1
         * @param isLeftArea   true means left area of view
         */
        void onAppear(TrendData data, TrendData previousData, boolean isLeftArea);

        void onDisappear();
    }

    private List<TrendData> mDataList;
    private SparseArray<TrendData> mVisibleList;
    private int mFirstVisibleIndex;
    private int mLastVisibleIndex;
    private Date mDate;
    private SimpleDateFormat mDateFormat;
    private Settings mSettings;

    // visible points index range
    private int mStart;
    private int mEnd;
    private int mLength;
    private OnTouchLinesAppearListener mOnTouchLinesAppearListener;
    private OnDragListener mOnDragListener;
    private float mVolumeWidth;
    private float mDataLineWidth;
    private float mTouchLineWidth;
    private float mOuterCircleRadius;
    private float mInnerCircleRadius;

    public void setOnDragListener(OnDragListener onDragListener) {
        mOnDragListener = onDragListener;
    }

    protected void setRealTimeLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BLUE.get()));
        paint.setStrokeWidth(mDataLineWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
        applyColorConfiguration(paint, ColorCfg.REAL_TIME_LINE);
    }

    protected void setRealTimeFillPaint(Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, getHeight(),
                Color.parseColor("#51A4D3FF"),
                Color.parseColor("#51A4D3FF"),
                Shader.TileMode.CLAMP));
    }

    private void setCandleBodyPaint(Paint paint, String color) {
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    protected void setTouchLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BLACK.get()));
        paint.setStrokeWidth(mTouchLineWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
    }

    protected void setRectBgPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BLACK.get()));
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    protected void setTouchLineTextPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartView.ChartColor.WHITE.get()));
        paint.setTextSize(mBigFontSize);
        paint.setPathEffect(null);
    }

    private void setOuterCirclePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.WHITE.get()));
        paint.setStyle(Paint.Style.FILL);
    }

    private void setInnerCirclePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BLACK.get()));
        paint.setStyle(Paint.Style.FILL);
    }

    public InfiniteTrendChart(Context context) {
        super(context);
        init();
    }

    public InfiniteTrendChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected boolean enableDragChart() {
        return true;
    }

    private void init() {
        mVisibleList = new SparseArray<>();
        mDateFormat = new SimpleDateFormat("HH:mm");
        mDate = new Date();

        mFirstVisibleIndex = Integer.MAX_VALUE;
        mLastVisibleIndex = Integer.MIN_VALUE;

        mVolumeWidth = dp2Px(VOLUME_WIDTH);
        mDataLineWidth = dp2Px(DATA_LINE_WIDTH);
        mTouchLineWidth = dp2Px(TOUCH_LINE_WIDTH);
        mOuterCircleRadius = dp2Px(OUTER_CIRCLE_RADIUS);
        mInnerCircleRadius = dp2Px(INNER_CIRCLE_RADIUS);
    }

    public void initWithData(List<TrendData> dataList) {
        mDataList = dataList;
        resetChart();
        redraw();
    }

    public void addHistoryData(List<TrendData> dataList) {
        mDataList.addAll(0, dataList);
        redraw();
    }

    @Override
    protected void resetChart() {
        super.resetChart();
        mStart = 0;
        mEnd = 0;
        mLength = 0;
        mVisibleList.clear();

        mFirstVisibleIndex = Integer.MAX_VALUE;
        mLastVisibleIndex = Integer.MIN_VALUE;
    }

    @Override
    protected float calculateMaxTransactionX() {
        if (mDataList != null) {
            return Math.max((mDataList.size() - mSettings.getXAxis()) * mOneXAxisWidth, 0);
        }
        return super.calculateMaxTransactionX();
    }

    @Override
    protected float calculateOneXAxisWidth() {
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mPriceAreaWidth;
        return width / mSettings.getXAxis();
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
        if (mDataList != null && mDataList.size() > 0) {
            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;
            for (int i = mStart; i < mEnd; i++) {
                TrendData data = mDataList.get(i);
                max = Math.max(max, data.getClosePrice());
                min = Math.min(min, data.getClosePrice());
            }

            float priceRange = BigDecimal.valueOf(max).subtract(new BigDecimal(min))
                    .divide(new BigDecimal(baselines.length - 1),
                            mSettings.getNumberScale() + 1, RoundingMode.HALF_EVEN).floatValue();

            if (priceRange == 0) {
                int scale = mSettings.getNumberScale();
                priceRange = (float) Math.pow(10, -scale);
                max += priceRange * (baselines.length - 1);
                min -= priceRange * (baselines.length - 1);
            }

            // 额外扩大最大值 最小值
            max += priceRange;
            //min -= priceRange;
            priceRange = BigDecimal.valueOf(max).subtract(new BigDecimal(min))
                    .divide(new BigDecimal(baselines.length - 1),
                            mSettings.getNumberScale() + 1, RoundingMode.HALF_EVEN).floatValue();

            baselines[0] = max;
            baselines[baselines.length - 1] = min;
            for (int i = baselines.length - 2; i > 0; i--) {
                baselines[i] = baselines[i + 1] + priceRange;
            }
        }
    }

    @Override
    protected void calculateIndexesBaseLines(double[] indexesBaseLines) {
        if (mDataList != null && mDataList.size() > 0) {
            double maxVolume = mDataList.get(mStart).getNowVolume();
            for (int i = mStart; i < mEnd; i++) {
                if (maxVolume < mDataList.get(i).getNowVolume()) {
                    maxVolume = mDataList.get(i).getNowVolume();
                }
            }
            double volumeRange = maxVolume / (indexesBaseLines.length - 1);
            indexesBaseLines[0] = maxVolume;
            indexesBaseLines[indexesBaseLines.length - 1] = 0;
            for (int i = indexesBaseLines.length - 2; i > 0; i--) {
                indexesBaseLines[i] = indexesBaseLines[i + 1] + volumeRange;
            }
        }
    }

    @Override
    protected void drawBaseLines(boolean indexesEnable,
                                 float[] baselines, int left, int top, int width, int height,
                                 double[] indexesBaseLines, int left2, int top2, int width2, int height2, Canvas canvas) {
        if (baselines == null || baselines.length < 2) return;

        if (mDataList == null || mDataList.isEmpty()) return;

        float verticalInterval = height * 1.0f / (baselines.length - 1);
        mPriceAreaWidth = calculatePriceWidth(baselines[0]);
        float topY = top;
        for (int i = 0; i < baselines.length; i++) {
            Path path = getPath();
            path.moveTo(left, topY);
            path.lineTo(left + width, topY);
            setBaseLinePaint(sPaint);
            canvas.drawPath(path, sPaint);

            if (i != 0) {
                setDefaultTextPaint(sPaint);
                String baseLineValue = formatNumber(baselines[i]);
                float textWidth = sPaint.measureText(baseLineValue);
                float x = left + width - mPriceAreaWidth + (mPriceAreaWidth - textWidth) / 2;
                float y = topY - mTextMargin - mFontHeight / 2 + mOffset4CenterText;
                canvas.drawText(baseLineValue, x, y, sPaint);
            }

            topY += verticalInterval;
        }

        if (indexesEnable && indexesBaseLines.length >= 2) {
        }
    }

    @Override
    protected void drawRealTimeData(boolean indexesEnable,
                                    int left, int top, int width, int height,
                                    int left2, int top2, int width2, int height2, Canvas canvas) {
        if (mDataList != null && mDataList.size() > 0) {
            Path path = getPath();
            float chartX = 0;
            float chartY = 0;
            float firstChartX = 0;
            for (int i = mStart; i < mEnd; i++) {
                TrendData data = mDataList.get(i);
                chartX = getChartXOfScreen(i, data);
                chartY = getChartY(data.getClosePrice());
                if (path.isEmpty()) {
                    firstChartX = chartX;
                    path.moveTo(chartX, chartY);
                } else {
                    path.lineTo(chartX, chartY);
                }
            }

            setRealTimeLinePaint(sPaint);
            canvas.drawPath(path, sPaint);

            //fill area
            path.lineTo(chartX, top + height);
            path.lineTo(firstChartX, top + height);
            path.close();
            setRealTimeFillPaint(sPaint);
            canvas.drawPath(path, sPaint);
            sPaint.setShader(null);

            if (indexesEnable) {
                for (int i = mStart; i < mEnd; i++) {
                    ChartColor color = ChartColor.RED;
                    chartX = getChartXOfScreen(i);
                    if (i > 0 && mDataList.get(i).getClosePrice() < mDataList.get(i - 1).getClosePrice()) {
                        color = ChartColor.GREEN;
                    }
                    setCandleBodyPaint(sPaint, color.get());
                    RectF rectf = getRectF();
                    rectf.left = chartX - mVolumeWidth / 2;
                    rectf.top = getIndexesChartY(mDataList.get(i).getNowVolume());
                    rectf.right = chartX + mVolumeWidth / 2;
                    rectf.bottom = getIndexesChartY(0);
                    canvas.drawRect(rectf, sPaint);
                }
            }
        }
    }

    protected float getChartXOfScreen(int index) {
        index = index - mStart; // visible index 0 ~ 39
        return getChartX(index);
    }

    protected float getChartXOfScreen(int index, TrendData data) {
        index = index - mStart; // visible index 0 ~ 39
        updateFirstLastVisibleIndex(index);
        mVisibleList.put(index, data);
        return getChartX(index);
    }

    private void updateFirstLastVisibleIndex(int indexOfXAxis) {
        mFirstVisibleIndex = Math.min(indexOfXAxis, mFirstVisibleIndex);
        mLastVisibleIndex = Math.max(indexOfXAxis, mLastVisibleIndex);
    }

    @Override
    protected float getChartX(int index) {
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mPriceAreaWidth;
        float chartX = getPaddingLeft() + index * width * 1.0f / mSettings.getXAxis();
        return chartX;
    }

    @Override
    protected int getIndexOfXAxis(float chartX) {
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mPriceAreaWidth;
        chartX = chartX - getPaddingLeft();
        return (int) (chartX * mSettings.getXAxis() / width);
    }

    @Override
    public void setSettings(ChartSettings settings) {
        mSettings = (Settings) settings;
        super.setSettings(settings);
    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
        float textY = top + mTextMargin * 2.5f + mFontHeight / 2 + mOffset4CenterText;
        setDefaultTextPaint(sPaint);
        for (int i = mStart + 4; i < mEnd; i += 10) {
            TrendData data = mDataList.get(i);
            String displayTime = formatTimestamp(data.getTimeStamp());
            float textWidth = sPaint.measureText(displayTime);
            float textX = getChartXOfScreen(i) - textWidth / 2;
            canvas.drawText(displayTime, textX, textY, sPaint);
        }
    }

    private String formatTimestamp(long timeStamp) {
        mDate.setTime(timeStamp);
        return mDateFormat.format(mDate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calculateStartAndEndPosition();
        super.onDraw(canvas);
        if (isDragging()) {
            if (mOnDragListener != null && mDataList != null &&
                    mDataList.size() > mSettings.getXAxis() && mStart == 0) {
                mOnDragListener.onArriveLeft(mDataList.get(mStart));
            }
            if (mOnDragListener != null && mDataList != null &&
                    mDataList.size() > mSettings.getXAxis() && mEnd == mDataList.size()) {
                mOnDragListener.onArriveRight(mDataList.get(mEnd - 1));
            }
        }
    }

    @Override
    protected int calculateTouchIndex(MotionEvent e) {
        float touchX = e.getX();
        return getIndexOfXAxis(touchX);
    }

    @Override
    protected boolean hasThisTouchIndex(int touchIndex) {
        if (touchIndex != -1 && mVisibleList != null && mVisibleList.get(touchIndex) != null) {
            return true;
        }
        return super.hasThisTouchIndex(touchIndex);
    }

    @Override
    protected boolean enableDrawTouchLines() {
        return true;
    }

    @Override
    protected void drawTouchLines(boolean indexesEnable, int touchIndex,
                                  int left, int top, int width, int height,
                                  int left2, int top2, int width2, int height2, Canvas canvas) {
        if (hasThisTouchIndex(touchIndex)) {
            TrendData data = mVisibleList.get(touchIndex);
            float touchX = getChartX(touchIndex);
            float touchY = getChartY(data.getClosePrice());

            // draw cross line: vertical line and horizontal line
            setTouchLinePaint(sPaint);
            Path path = getPath();
            path.moveTo(touchX, top);
            path.lineTo(touchX, top + height);
            canvas.drawPath(path, sPaint);
            path = getPath();
            path.moveTo(left, touchY);
            path.lineTo(left + width - mPriceAreaWidth, touchY);
            canvas.drawPath(path, sPaint);

            // draw date connect to vertical line
            String date = formatTimestamp(data.getTimeStamp());
            setTouchLineTextPaint(sPaint);
            float dateWidth = sPaint.measureText(date);
            RectF redRect = getBigFontBgRectF(0, 0, dateWidth);
            float rectHeight = redRect.height();
            float rectWidth = redRect.width();
            redRect.left = touchX - rectWidth / 2;
            redRect.top = top + height;
            if (redRect.left < left) { // rect will touch left border
                redRect.left = left;
            }
            if (redRect.left + rectWidth > left + width) { // rect will touch right border
                redRect.left = left + width - rectWidth;
            }
            redRect.right = redRect.left + rectWidth;
            redRect.bottom = redRect.top + rectHeight;
            setRectBgPaint(sPaint);
            canvas.drawRoundRect(redRect, 2, 2, sPaint);
            float dateX = redRect.left + (rectWidth - dateWidth) / 2;
            float dateY = top + height + rectHeight / 2 + mOffset4CenterBigText;
            setTouchLineTextPaint(sPaint);
            canvas.drawText(date, dateX, dateY, sPaint);

            // draw price connect to horizontal line
            String price = formatNumber(data.getClosePrice());
            setTouchLineTextPaint(sPaint);
            float priceWidth = sPaint.measureText(price);
            float priceMargin = (mPriceAreaWidth - priceWidth) / 2;
            float priceX = left + width - priceMargin - priceWidth;
            redRect = getBigFontBgRectF(priceX, touchY + mOffset4CenterBigText, priceWidth);
            rectHeight = redRect.height();
            redRect.top -= rectHeight / 2;
            if (redRect.top < top) {
                redRect.top = top;
            }
            redRect.bottom = redRect.top + rectHeight;
            setRectBgPaint(sPaint);
            canvas.drawRoundRect(redRect, 2, 2, sPaint);
            float priceY = redRect.top + rectHeight / 2 + mOffset4CenterBigText;
            setTouchLineTextPaint(sPaint);
            canvas.drawText(price, priceX, priceY, sPaint);

            // center circle
            setOuterCirclePaint(sPaint);
            canvas.drawCircle(touchX, touchY, mOuterCircleRadius, sPaint);
            setInnerCirclePaint(sPaint);
            canvas.drawCircle(touchX, touchY, mInnerCircleRadius, sPaint);
        }
    }

    private void calculateStartAndEndPosition() {
        if (mDataList != null) {
            mStart = mDataList.size() - mSettings.getXAxis() < 0
                    ? 0 : (mDataList.size() - mSettings.getXAxis() - getStartPointOffset());
            mLength = Math.min(mDataList.size(), mSettings.getXAxis());
            mEnd = mStart + mLength;
        }
    }

    public static class Settings extends ChartSettings {
        public static final int INDEXES_VOL = 1;

        private int indexesType;

        public Settings() {
            super();
            indexesType = INDEXES_VOL;
        }

        public int getIndexesType() {
            return indexesType;
        }

        public void setIndexesType(int indexesType) {
            this.indexesType = indexesType;
        }
    }
}
