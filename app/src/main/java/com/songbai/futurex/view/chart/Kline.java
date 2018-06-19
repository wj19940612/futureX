package com.songbai.futurex.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Modified by john on 08/04/2018
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class Kline extends BaseChart {

    public static class Data implements Comparable<Data> {
        private float openPrice;
        private float maxPrice;
        private float minPrice;
        private float closePrice;
        private double nowVolume;
        private String time;
        private long timeStamp;

        // local cal data
        private SparseArray<Float> mas;

        public Data(float openPrice, float maxPrice, float minPrice, float closePrice, long timestamp) {
            this.openPrice = openPrice;
            this.maxPrice = maxPrice;
            this.minPrice = minPrice;
            this.closePrice = closePrice;
            this.timeStamp = timestamp;
            this.mas = new SparseArray<>();
        }

        public float getOpenPrice() {
            return openPrice;
        }

        public float getMaxPrice() {
            return maxPrice;
        }

        public float getMinPrice() {
            return minPrice;
        }

        public float getClosePrice() {
            return closePrice;
        }

        public long getTimestamp() {
            return timeStamp;
        }

        public String getTime() {
            return time;
        }

        public double getNowVolume() {
            return nowVolume;
        }

        public void addMas(int maKey, float mvValue) {
            checkMasIfEmpty();
            mas.put(maKey, Float.valueOf(mvValue));
        }

        public Float getMas(int maKey) {
            checkMasIfEmpty();
            return mas.get(maKey);
        }

        private void checkMasIfEmpty() {
            if (mas == null) {
                mas = new SparseArray<>();
            }
        }

        @Override
        public int compareTo(@NonNull Data o) {
            return (int) (this.timeStamp - o.getTimestamp());
        }
    }

    public interface OnCrossLineAppearListener {
        void onAppear(Data data);

        void onDisappear();
    }

    public interface OnMADataChangedListener {
        void onMADataChanged(Data data);
    }

    public interface OnSidesReachedListener {
        void onStartSideReached(Data data);

        void onEndSideReached(Data data);
    }

    private OnMADataChangedListener mOnMADataChangedListener;
    private OnCrossLineAppearListener mOnCrossLineAppearListener;
    private OnSidesReachedListener mOnSidesReachedListener;
    private int[] mMas;
    private float mBaseLineWidth;
    private SparseArray<Data> mVisibleList;
    private SimpleDateFormat mDateFormat;
    private Date mDate;
    private float mLastPrice;

    // visible points index range
    protected List<Data> mDataList;
    protected int mStart;
    protected int mEnd;
    private int mFirstVisibleIndex;
    private int mLastVisibleIndex;

    private float mCandleWidth;
    private float mCandleLineWidth;
    private float mPriceAreaWidth;
    private float mRightPadding;
    private float mMALineWidth;
    private int mTouchIndex;

    private ChartColor mChartColor;

    public Kline(Context context) {
        super(context);
    }

    public Kline(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        mDataList = new ArrayList<>();
        mVisibleList = new SparseArray<>();
        mMas = new int[]{5, 10, 30};
        mChartColor = new ChartColor();
        mBaseLineWidth = dp2Px(1);
        mCandleWidth = dp2Px(7f);
        mCandleLineWidth = dp2Px(1f);
        mMALineWidth = dp2Px(1f);
        mDateFormat = new SimpleDateFormat();
        mDate = new Date();
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (int i = mStart; i < mEnd; i++) {
            Data data = mDataList.get(i);
            max = Math.max(max, data.getMaxPrice());
            min = Math.min(min, data.getMinPrice());

            for (int ma : mMas) {
                Float mvValue = mDataList.get(i).getMas(ma);
                if (mvValue == null) continue;
                max = Math.max(max, mvValue.floatValue());
                min = Math.min(min, mvValue.floatValue());
            }
        }

        if (mLastPrice != 0 && mEnd != 0 && mEnd == mDataList.size()) {
            max = Math.max(max, mLastPrice);
            min = Math.min(min, mLastPrice);
        }

        if (max == Float.MIN_VALUE || min == Float.MAX_VALUE) return;

        float priceRange = BigDecimal.valueOf(max).subtract(new BigDecimal(min))
                .divide(new BigDecimal(baselines.length - 1),
                        mChartCfg.getNumberScale() + 1, RoundingMode.HALF_EVEN).floatValue();

        // 额外扩大最大值 最小值
        max += priceRange / 2;
        min -= priceRange / 2;

        priceRange = BigDecimal.valueOf(max).subtract(new BigDecimal(min))
                .divide(new BigDecimal(baselines.length - 1),
                        mChartCfg.getNumberScale() + 1, RoundingMode.HALF_EVEN).floatValue();

        baselines[0] = max;
        baselines[baselines.length - 1] = min;
        for (int i = baselines.length - 2; i > 0; i--) {
            baselines[i] = baselines[i + 1] + priceRange;
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
    protected float calculateOneXAxisWidth() {
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mPriceAreaWidth;
        return width / mChartCfg.getXAxis();
    }

    @Override
    protected float calculateMaxTransactionX() {
        return Math.max((mDataList.size() - mChartCfg.getXAxis()) * mOneXAxisWidth, 0);
    }

    @Override
    protected void drawBaseLines(boolean indexesEnable,
                                 float[] baselines, int left, int top, int width, int height,
                                 double[] indexesBaseLines, int left2, int top2, int width2, int height2,
                                 Canvas canvas) {
        if (baselines.length == 0) return;

        float verticalInterval = height * 1.0f / (baselines.length - 1);
        mPriceAreaWidth = calculatePriceWidth(baselines[0]);
        float topY = top;
        for (int i = 0; i < baselines.length; i++) {
            if (i != 0) {
                Path path = getPath();
                path.moveTo(left, topY);
                path.lineTo(left + width, topY);
                setBaseLinePaint(sPaint);
                canvas.drawPath(path, sPaint);

                setBaseTextPaint(sPaint);
                String baseLineValue = formatNumber(baselines[i]);
                float textW = sPaint.measureText(baseLineValue);
                mRightPadding = mPriceAreaWidth - textW;
                float x = left + width - mPriceAreaWidth;
                float y = topY - mTextMargin - mBigFontHeight / 2 + mOffset4CenterText;
                canvas.drawText(baseLineValue, x, y, sPaint);
            }

            topY += verticalInterval;
        }
    }

    @Override
    protected void drawData(boolean indexesEnable,
                            int left, int top, int width, int height,
                            int left2, int top2, int width2, int height2, Canvas canvas) {
        for (int i = mStart; i < mEnd; i++) {
            Data data = mDataList.get(i);
            float chartX = getChartXOfScreen(i, data);
            if (i == mDataList.size() - 1 && mLastPrice != 0) { // last data
                drawCandle(chartX, data, mLastPrice, canvas);
            } else {
                drawCandle(chartX, data, canvas);
            }

            if (indexesEnable) {
                drawIndexes(chartX, data, canvas);
            }
        }

        // draw MAs
        for (int ma : mMas) {
            setMovingAveragesPaint(sPaint, ma);
            float startX = -1;
            float startY = -1;
            for (int i = mStart; i < mEnd; i++) {
                Float movingAverageValue = mDataList.get(i).getMas(ma);
                if (movingAverageValue == null) continue;
                float chartX = getChartXOfScreen(i);
                float chartY = getChartY(movingAverageValue.floatValue());
                if (startX == -1 && startY == -1) { // start
                    startX = chartX;
                    startY = chartY;
                } else {
                    canvas.drawLine(startX, startY, chartX, chartY, sPaint);
                    startX = chartX;
                    startY = chartY;
                }
            }
        }

        // draw last price line
//        float[] baseLines = mChartCfg.getBaseLineArray();
//        if (mLastPrice != 0 && baseLines.length > 0 &&
//                mLastPrice < baseLines[0] && mLastPrice > baseLines[baseLines.length - 1]) {
//            setCrossLinePaint(sPaint);
//            String lastPrice = formatNumber(mLastPrice);
//            sPaint.setTextSize(mFontSize);
//            float priceWidth = sPaint.measureText(lastPrice);
//            float lineY = getChartY(mLastPrice);
//            float priceY = lineY + mOffset4CenterText;
//            float priceX = left + width - mPriceAreaWidth + (mPriceAreaWidth - priceWidth) / 2;
//            RectF priceRect = getTextBgRectF(priceX, priceY, priceWidth);
//            canvas.drawRect(priceRect, sPaint);
//            canvas.drawLine(left, lineY, priceRect.left, lineY, sPaint);
//            setPriceTextPaint(sPaint);
//            canvas.drawText(lastPrice, priceX, priceY, sPaint);
//        }
    }

    protected void drawIndexes(float chartX, Data data, Canvas canvas) {
        drawVolumes(chartX, data, canvas);
    }

    private void drawVolumes(float chartX, Data data, Canvas canvas) {
        int color = mChartColor.getPositiveColor();
        if (data.getClosePrice() < data.getOpenPrice()) {
            color = mChartColor.getNegativeColor();
        }
        setCandleBodyPaint(sPaint, color);
        RectF rectf = getRectF();
        rectf.left = chartX - mCandleWidth / 2;
        rectf.top = getIndexesChartY(data.nowVolume);
        rectf.right = chartX + mCandleWidth / 2;
        rectf.bottom = getIndexesChartY(0);
        canvas.drawRect(rectf, sPaint);
    }

    private void drawCandle(float chartX, Data data, float lastPrice, Canvas canvas) {
        int color = mChartColor.getPositiveColor();
        float topPrice = lastPrice;
        float bottomPrice = data.getOpenPrice();
        if (lastPrice < data.getOpenPrice()) { // negative line
            color = mChartColor.getNegativeColor();
            topPrice = data.getOpenPrice();
            bottomPrice = lastPrice;
        }
        drawTopCandleLine(data.getMaxPrice(), topPrice, color, chartX, canvas);
        drawCandleBody(topPrice, bottomPrice, color, chartX, canvas);
        drawBottomCandleLine(data.getMinPrice(), bottomPrice, color, chartX, canvas);
    }

    private void drawCandle(float chartX, Data data, Canvas canvas) {
        int color = mChartColor.getPositiveColor();
        float topPrice = data.getClosePrice();
        float bottomPrice = data.getOpenPrice();
        if (data.getClosePrice() < data.getOpenPrice()) { // negative line
            color = mChartColor.getNegativeColor();
            topPrice = data.getOpenPrice();
            bottomPrice = data.getClosePrice();
        }
        drawTopCandleLine(data.getMaxPrice(), topPrice, color, chartX, canvas);
        drawCandleBody(topPrice, bottomPrice, color, chartX, canvas);
        drawBottomCandleLine(data.getMinPrice(), bottomPrice, color, chartX, canvas);
    }

    private void drawTopCandleLine(Float maxPrice, float topPrice, int color, float chartX, Canvas canvas) {
        setCandleLinePaint(sPaint, color);
        canvas.drawLine(chartX, getChartY(maxPrice), chartX, getChartY(topPrice), sPaint);
    }

    private void drawCandleBody(float topPrice, float bottomPrice, int color, float chartX, Canvas canvas) {
        if (topPrice == bottomPrice) {
            setCandleLinePaint(sPaint, color);
            canvas.drawLine(chartX - mCandleWidth / 2, getChartY(topPrice),
                    chartX + mCandleWidth / 2, getChartY(bottomPrice),
                    sPaint);
        } else {
            setCandleBodyPaint(sPaint, color);
            RectF rectf = getRectF();
            rectf.left = chartX - mCandleWidth / 2;
            rectf.top = getChartY(topPrice);
            rectf.right = chartX + mCandleWidth / 2;
            rectf.bottom = getChartY(bottomPrice);
            canvas.drawRect(rectf, sPaint);
        }
    }

    private void drawBottomCandleLine(Float minPrice, float bottomPrice, int color, float chartX, Canvas canvas) {
        setCandleLinePaint(sPaint, color);
        canvas.drawLine(chartX, getChartY(bottomPrice),
                chartX, getChartY(minPrice),
                sPaint);
    }

    protected float getChartXOfScreen(int index) {
        index = index - mStart; // visible index 0 ~ (xAxis - 1)
        return getChartX(index);
    }

    protected float getChartXOfScreen(int index, Data data) {
        index = index - mStart; // visible index 0 ~ (xAxis - 1)
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
        float offset = mCandleWidth / 2;
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mRightPadding;
        float chartX = getPaddingLeft() + index * width * 1.0f / mChartCfg.getXAxis();
        return chartX + offset;
    }

    @Override
    protected int getIndexOfXAxis(float chartX) {
        float offset = mCandleWidth / 2;
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mRightPadding;
        chartX = chartX - offset - getPaddingLeft();
        return (int) (chartX * mChartCfg.getXAxis() / width);
    }

    @Override
    protected void drawTimeLine(int left, int top, int width, Canvas canvas) {
        int interval = mChartCfg.getXAxis() / 4;
        float textY = top + mTextMargin / 2 + mBigFontHeight / 2 + mOffset4CenterBigText;
        for (int i = mStart; i < mEnd; i += interval) {
            Data data = mDataList.get(i);
            String timestamp = formatTimestamp(data.getTimestamp());
            float textWidth = sPaint.measureText(timestamp);
            setBaseTextPaint(sPaint);
            float textX = getChartXOfScreen(i) - textWidth / 2;
            if (i == mStart) {
                textX = left + mTextMargin;
            } else if (i == mEnd - 1) {
                textX = left + width - textWidth - mTextMargin;
            }
            canvas.drawText(timestamp, textX, textY, sPaint);
        }
    }

    private String formatTimestamp(long timestamp) {
        mDate.setTime(timestamp);
        return mDateFormat.format(mDate);
    }

    @Override
    protected void drawCrossLines(boolean indexesEnable, int touchIndex,
                                  int left, int top, int width, int height,
                                  int left2, int top2, int width2, int height2, Canvas canvas) {
        if (!checkTouchIndexValid(touchIndex)) return;

        Data data = mVisibleList.get(touchIndex);
        float touchX = getChartX(touchIndex);
        float touchY = getChartY(data.getClosePrice());

        // draw cross line: vertical line and horizontal line
        setCrossLinePaint(sPaint);
//            Path path = getPath();
//            path.moveTo(touchX, top);
//            path.lineTo(touchX, top + height);
//            canvas.drawPath(path, sPaint);
        canvas.drawLine(touchX, top, touchX, top + height, sPaint);
//            path = getPath();
//            path.moveTo(left, touchY);
//            path.lineTo(left + width - mPriceAreaWidth, touchY);
//            canvas.drawPath(path, sPaint);
        canvas.drawLine(left, touchY, left + width - mPriceAreaWidth, touchY, sPaint);

        // draw date connect to vertical line
        String date = formatTimestamp(data.getTimestamp());
        setCrossLineTextPaint(sPaint);
        float dateWidth = sPaint.measureText(date);
        RectF bgRect = getTextBgRectF(0, 0, dateWidth);
        float rectHeight = bgRect.height();
        float rectWidth = bgRect.width();
        bgRect.left = touchX - rectWidth / 2;
        bgRect.top = top + height;
        if (bgRect.left < left) { // rect will touch left border
            bgRect.left = left;
        }
        if (bgRect.left + rectWidth > left + width) { // rect will touch right border
            bgRect.left = left + width - rectWidth;
        }
        bgRect.right = bgRect.left + rectWidth;
        bgRect.bottom = bgRect.top + rectHeight;
        setCrossLineTextBgPaint(sPaint);
        canvas.drawRect(bgRect, sPaint);
        float dateX = bgRect.left + (rectWidth - dateWidth) / 2;
        float dateY = top + height + rectHeight / 2 + mOffset4CenterBigText;
        setCrossLineTextPaint(sPaint);
        canvas.drawText(date, dateX, dateY, sPaint);

        // draw price connect to horizontal line
        String price = formatNumber(data.getClosePrice());
        setCrossLineTextPaint(sPaint);
        float priceWidth = sPaint.measureText(price);
        float priceMargin = (mPriceAreaWidth - priceWidth) / 2;
        float priceX = left + width - priceMargin - priceWidth;
        bgRect = getTextBgRectF(priceX, touchY + mOffset4CenterBigText, priceWidth);
        setCrossLineTextBgPaint(sPaint);
        canvas.drawRect(bgRect, sPaint);
        float priceY = bgRect.top + rectHeight / 2 + mOffset4CenterBigText;
        setCrossLineTextPaint(sPaint);
        canvas.drawText(price, priceX, priceY, sPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calculateStartAndEndPosition();
        calculateMovingAverageValues();

        super.onDraw(canvas);

        onMADataChanged();

        if (isDragging()) {
            onSidesReached();
        }
    }

    private void onSidesReached() {
        if (mOnSidesReachedListener != null && mDataList.size() > mChartCfg.getXAxis()) {
            if (mStart == 0) {
                mOnSidesReachedListener.onStartSideReached(mDataList.get(mStart));
            }
            if (mEnd == mDataList.size()) {
                mOnSidesReachedListener.onEndSideReached(mDataList.get(mEnd - 1));
            }
        }
    }

    private void onMADataChanged() {
        Data maData = null;
        if (mVisibleList.size() > 0) {
            maData = mVisibleList.get(mVisibleList.size() - 1);
        }
        if (mTouchIndex >= 0) {
            maData = mVisibleList.get(mTouchIndex);
        }
        if (maData != null && mOnMADataChangedListener != null) {
            mOnMADataChangedListener.onMADataChanged(maData);
        }
    }

    @Override
    protected float getChartY(float y) {
        float[] baseLines = mChartCfg.getBaseLineArray();

        // When values beyond baselines, eg. ma. return -1
        if (y > baseLines[0] || y < baseLines[baseLines.length - 1]) {
            return -1;
        }

        int height = getTopPartHeight() - 2 * mTextMargin; //
        y = (baseLines[0] - y) / (baseLines[0] - baseLines[baseLines.length - 1]) * height;
        return y + getPaddingTop() + mTextMargin;
    }

    private void calculateMovingAverageValues() {
        for (int movingAverage : mMas) {
            for (int i = mStart; i < mEnd; i++) {
                int start = i - movingAverage + 1;
                if (start < 0) continue;
                if (mDataList.get(i).getMas(movingAverage) != null) continue;

                float maValue = calculateMovingAverageValue(start, movingAverage);
                mDataList.get(i).addMas(movingAverage, maValue);
            }
        }
    }

    private float calculateMovingAverageValue(int start, int movingAverage) {
        float result = 0;
        for (int i = start; i < start + movingAverage; i++) {
            result += mDataList.get(i).getClosePrice();
        }
        return result / movingAverage;
    }

    private void calculateStartAndEndPosition() {
        mStart = mDataList.size() - mChartCfg.getXAxis() < 0
                ? 0 : (mDataList.size() - mChartCfg.getXAxis() - getStartPointOffset());
        int length = Math.min(mDataList.size(), mChartCfg.getXAxis());
        mEnd = mStart + length;
    }

    /**
     * this method is used to calculate the width of the big font size price text with rect bg
     *
     * @param price
     * @return
     */
    protected float calculatePriceWidth(float price) {
        String preClosePrice = formatNumber(price);
        sPaint.setTextSize(mFontSize);
        float priceWidth = sPaint.measureText(preClosePrice);
        return getTextBgRectF(0, 0, priceWidth).width();
    }

    @Override
    protected void reset() {
        super.reset();
        mStart = 0;
        mEnd = 0;
        mDataList.clear();
        mVisibleList.clear();
        mLastPrice = 0;

        mFirstVisibleIndex = Integer.MAX_VALUE;
        mLastVisibleIndex = Integer.MIN_VALUE;
    }

    @Override
    protected void onCrossLinesAppear(int touchIndex) {
        mTouchIndex = touchIndex;
        if (mOnCrossLineAppearListener != null) {
            Data curData = mVisibleList.get(touchIndex);
            mOnCrossLineAppearListener.onAppear(curData);
        }
    }

    @Override
    protected void onCrossLinesDisappear(int touchIndex) {
        mTouchIndex = touchIndex;
        if (mOnCrossLineAppearListener != null) {
            mOnCrossLineAppearListener.onDisappear();
        }
    }

    @Override
    protected int calculateTouchIndex(MotionEvent e) {
        float touchX = e.getX();
        int touchIndex = getIndexOfXAxis(touchX);
        if (mVisibleList.size() > 0) {
            touchIndex = Math.max(touchIndex, mFirstVisibleIndex);
            touchIndex = Math.min(touchIndex, mLastVisibleIndex);
        }
        return touchIndex;
    }

    @Override
    protected boolean checkTouchIndexValid(int newTouchIndex) {
        return mVisibleList.get(newTouchIndex) != null;
    }

    public void setOnCrossLineAppearListener(OnCrossLineAppearListener onCrossLineAppearListener) {
        mOnCrossLineAppearListener = onCrossLineAppearListener;
    }

    public void setOnMADataChangedListener(OnMADataChangedListener onMADataChangedListener) {
        mOnMADataChangedListener = onMADataChangedListener;
    }

    public void setOnSidesReachedListener(OnSidesReachedListener onSidesReachedListener) {
        mOnSidesReachedListener = onSidesReachedListener;
    }

    private void setBaseLinePaint(Paint paint) {
        paint.setColor(mChartColor.getBaseLineColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mBaseLineWidth);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 2}, 1));
    }

    private void setBaseTextPaint(Paint paint) {
        paint.setColor(mChartColor.getBaseTextColor());
        paint.setTextSize(mBigFontSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    private void setCandleLinePaint(Paint paint, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mCandleLineWidth);
        paint.setPathEffect(null);
    }

    private void setCandleBodyPaint(Paint paint, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    private void setMovingAveragesPaint(Paint paint, int ma) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mMALineWidth);
        paint.setPathEffect(null);
        paint.setColor(mChartColor.getMaColor(ma));
    }

    private void setCrossLinePaint(Paint paint) {
        paint.setColor(mChartColor.getCrossLineColor());
        paint.setStrokeWidth(mBaseLineWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
    }

    private void setPriceTextPaint(Paint paint) {
        paint.setColor(mChartColor.getCrossLineColor());
        paint.setTextSize(mFontSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    private void setCrossLineTextPaint(Paint paint) {
        paint.setColor(mChartColor.getCrossLinePriceColor());
        paint.setTextSize(mBigFontSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    private void setCrossLineTextBgPaint(Paint paint) {
        paint.setColor(mChartColor.getCrossLineColor());
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    public void setDataList(List<Data> dataList) {
        mDataList = dataList;
        redraw();
    }

    public void setLastPrice(float lastPrice) {
        if (mLastPrice != lastPrice) {
            mLastPrice = lastPrice;
            redraw();
        }
    }

    public ChartColor getChartColor() {
        return mChartColor;
    }

    public void setDateFormatStr(String dateFormatStr) {
        mDateFormat.applyPattern(dateFormatStr);
    }
}
