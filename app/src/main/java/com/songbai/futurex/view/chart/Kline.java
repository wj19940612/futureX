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

    public static class IndexData {

        private SparseArray<Float> mas;
        private float bollUp;
        private float bollLow;

        public IndexData() {
            this.mas = new SparseArray<>();
        }

        public void put(int maKey, float mvValue) {
            mas.put(maKey, Float.valueOf(mvValue));
        }

        public Float get(int maKey) {
            return mas.get(maKey);
        }

        public void setBollUp(float bollUp) {
            this.bollUp = bollUp;
        }

        public float getBollLow() {
            return bollLow;
        }

        public void setBollLow(float bollLow) {
            this.bollLow = bollLow;
        }

        public float getBollUp() {
            return bollUp;
        }
    }


    public static class Data implements Comparable<Data> {
        private float openPrice;
        private float maxPrice;
        private float minPrice;
        private float closePrice;
        private double nowVolume;
        private String time;
        private long timeStamp;

        // local index data
        private IndexData indexData;

        public Data(float openPrice, float maxPrice, float minPrice, float closePrice, long timestamp) {
            this.openPrice = openPrice;
            this.maxPrice = maxPrice;
            this.minPrice = minPrice;
            this.closePrice = closePrice;
            this.timeStamp = timestamp;
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
            checkIndexDataIfEmpty();
            indexData.put(maKey, Float.valueOf(mvValue));
        }

        public Float getMas(int maKey) {
            checkIndexDataIfEmpty();
            return indexData.get(maKey);
        }

        public IndexData getIndexData() {
            checkIndexDataIfEmpty();
            return indexData;
        }

        private void checkIndexDataIfEmpty() {
            if (indexData == null) {
                indexData = new IndexData();
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

    public interface OnSidesReachedListener {
        void onStartSideReached(Data data);

        void onEndSideReached(Data data);
    }

    private OnCrossLineAppearListener mOnCrossLineAppearListener;
    private OnSidesReachedListener mOnSidesReachedListener;
    private float mBaseLineWidth;
    private SimpleDateFormat mDateFormat;
    private Date mDate;
    private float mLastPrice;

    // index
    protected int[] mMas;
    protected int[] mBoll;

    protected SparseArray<Data> mVisibleList;
    protected List<Data> mDataList;
    protected List<Data> mBufferDataList;
    protected int mTouchIndex;
    protected int mStart;
    protected int mEnd;
    // visible points index range
    private int mFirstVisibleIndex;
    private int mLastVisibleIndex;
    private int mCandleNum;

    private float mCandleGap;
    private float mCandleLineWidth;
    private float mPriceAreaWidth;
    private float mRightPadding;
    private float mMALineWidth;

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
        mBufferDataList = new ArrayList<>();
        mVisibleList = new SparseArray<>();
        mChartColor = new ChartColor();
        mBaseLineWidth = dp2Px(1);
        mCandleGap = dp2Px(0.5f);
        mCandleLineWidth = dp2Px(1f);
        mMALineWidth = dp2Px(1f);
        mDateFormat = new SimpleDateFormat();
        mDate = new Date();

        mMas = new int[]{5, 10, 30};
        mBoll = new int[]{20, 2};
    }

    @Override
    protected void calculateBaseLines(float[] baselines) {
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (int i = mStart; i < mEnd; i++) {
            Data data = mDataList.get(i);
            max = Math.max(max, data.getMaxPrice());
            min = Math.min(min, data.getMinPrice());

            if (mChartCfg.getMainIndex() == ChartCfg.INDEX_MA) {
                for (int ma : mMas) {
                    Float mvValue = mDataList.get(i).getMas(ma);
                    if (mvValue == null) continue;
                    max = Math.max(max, mvValue.floatValue());
                    min = Math.min(min, mvValue.floatValue());
                }
            } else if (mChartCfg.getMainIndex() == ChartCfg.INDEX_BOLL) {
                Float mid = mDataList.get(i).getMas(mBoll[0]);
                if (mid == null) continue;
                IndexData indexData = mDataList.get(i).getIndexData();
                max = Math.max(max, indexData.getBollUp());
                min = Math.min(min, indexData.getBollLow());
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
    protected void calculateVolBaseLines(double[] indexesBaseLines) {
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
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - mRightPadding;
        return width / mCandleNum;
    }

    @Override
    protected float calculateMaxTransactionX() {
        return Math.max((mDataList.size() - mCandleNum) * mOneXAxisWidth, 0);
    }

    @Override
    protected void drawBaseLines(float[] baselines, int left, int top, int width, int height, Canvas canvas) {
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
    protected void drawData(int left, int top, int width, int height,
                            boolean volEnable, int volTop, int volHeight, Canvas canvas) {
        for (int i = mStart; i < mEnd; i++) {
            Data data = mDataList.get(i);
            float chartX = getChartXOfScreen(i, data);
            if (i == mDataList.size() - 1 && mLastPrice != 0) { // last data
                drawCandle(chartX, data, mLastPrice, canvas);
            } else {
                drawCandle(chartX, data, canvas);
            }

            if (volEnable) {
                drawVol(chartX, data, canvas);
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

    protected void drawVol(float chartX, Data data, Canvas canvas) {
        drawVolumes(chartX, data, canvas);
    }

    private void drawVolumes(float chartX, Data data, Canvas canvas) {
        float oneXAxisWidth = calculateOneXAxisWidth();
        float candleWidth = oneXAxisWidth - mCandleGap * 2;
        int color = mChartColor.getPositiveColor();
        if (data.getClosePrice() < data.getOpenPrice()) {
            color = mChartColor.getNegativeColor();
        }
        setCandleBodyPaint(sPaint, color);
        RectF rectf = getRectF();
        rectf.left = chartX - candleWidth / 2;
        rectf.top = getVolChartY(data.nowVolume);
        rectf.right = chartX + candleWidth / 2;
        rectf.bottom = getVolChartY(0);
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
        if (maxPrice != 0 && topPrice != 0) {
            canvas.drawLine(chartX, getChartY(maxPrice), chartX, getChartY(topPrice), sPaint);
        }
    }

    private void drawCandleBody(float topPrice, float bottomPrice, int color, float chartX, Canvas canvas) {
        float oneXAxisWidth = calculateOneXAxisWidth();
        float candleWidth = oneXAxisWidth - mCandleGap * 2;
        if (topPrice == bottomPrice) {
            setCandleLinePaint(sPaint, color);
            canvas.drawLine(chartX - candleWidth / 2, getChartY(topPrice),
                    chartX + candleWidth / 2, getChartY(bottomPrice),
                    sPaint);
        } else {
            setCandleBodyPaint(sPaint, color);
            RectF rectf = getRectF();
            rectf.left = chartX - candleWidth / 2;
            rectf.top = getChartY(topPrice);
            rectf.right = chartX + candleWidth / 2;
            rectf.bottom = getChartY(bottomPrice);
            canvas.drawRect(rectf, sPaint);
        }
    }

    private void drawBottomCandleLine(Float minPrice, float bottomPrice, int color, float chartX, Canvas canvas) {
        setCandleLinePaint(sPaint, color);
        if (minPrice != 0 && bottomPrice != 0) {
            canvas.drawLine(chartX, getChartY(bottomPrice), chartX, getChartY(minPrice), sPaint);
        }

    }

    protected float getChartXOfScreen(int index) {
        index = index - mStart; // visible index 0 ~ getCandleNum() - 1
        return getChartX(index);
    }

    protected float getChartXOfScreen(int index, Data data) {
        index = index - mStart; // visible index 0 ~ getCandleNum() - 1
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
        float oneXAxisWidth = calculateOneXAxisWidth();
        float offset = oneXAxisWidth / 2;
        float chartX = getPaddingLeft() + index * oneXAxisWidth;
        return chartX + offset;
    }

    @Override
    protected int getIndexOfXAxis(float chartX) {
        float oneXAxisWidth = calculateOneXAxisWidth();
        float offset = oneXAxisWidth / 2;
        chartX = chartX - offset - getPaddingLeft();
        return (int) (chartX / oneXAxisWidth);
    }

    @Override
    protected void drawTimeLine(int left, int top, int width, int timeLineHeight, Canvas canvas) {
        int interval = mCandleNum / 4;
        float textY = top + mTextMargin / 2 + mBigFontHeight / 2 + mOffset4CenterBigText;
        setBaseTextPaint(sPaint);
        for (int i = mStart; i < mEnd; i += interval) {
            Data data = mDataList.get(i);
            String timestamp = formatTimestamp(data.getTimestamp());
            float textWidth = sPaint.measureText(timestamp);
            float textX = getChartXOfScreen(i) - textWidth / 2;
            if (i == mStart) {
                textX = left + mTextMargin;
            } else if (i == mEnd - 1) {
                break;
            }
            canvas.drawText(timestamp, textX, textY, sPaint);
        }
        if (mEnd > 0 && mEnd - mStart == mCandleNum) { // full width screen
            Data data = mDataList.get(mEnd - 1);
            String timestamp = formatTimestamp(data.getTimestamp());
            float textWidth = sPaint.measureText(timestamp);
            float textX = left + width - textWidth - mTextMargin;
            canvas.drawText(timestamp, textX, textY, sPaint);
        }
    }

    @Override
    protected void drawIndex(int left, int top, int width,
                             int mainChartHeight, int timeLineHeight, int volChartHeight, int subChartHeight,
                             Canvas canvas) {

        if (mChartCfg.getMainIndex() == ChartCfg.INDEX_MA) {
            drawMALines(canvas);

            Data data = null;
            if (mVisibleList.size() > 0) {
                data = mVisibleList.get(mVisibleList.size() - 1);
            }
            if (mTouchIndex >= 0) {
                data = mVisibleList.get(mTouchIndex);
            }
            if (data == null) return;

            float textX = left + width - mTextMargin;
            int textY = (int) (top + mTextMargin * 3 + mOffset4CenterText);
            for (int i = mMas.length - 1; i >= 0; i--) {
                int ma = mMas[i];
                Float maValue = data.getMas(ma);
                if (maValue == null) continue;

                String maText = "MA" + ma + ": " + formatNumber(maValue.floatValue());
                setMovingAveragesTextPaint(sPaint, ma);
                float maTextLength = sPaint.measureText(maText);
                textX -= maTextLength;
                canvas.drawText(maText, textX, textY, sPaint);
                textX -= mTextMargin * 1.5;
            }
        } else if (mChartCfg.getMainIndex() == ChartCfg.INDEX_BOLL) {
            drawBollLines(canvas);

            Data data = null;
            if (mVisibleList.size() > 0) {
                data = mVisibleList.get(mVisibleList.size() - 1);
            }
            if (mTouchIndex >= 0) {
                data = mVisibleList.get(mTouchIndex);
            }
            if (data == null) return;

            Float mid = data.getMas(mBoll[0]);
            if (mid == null) return;

            float textX = left + width - mTextMargin;
            int textY = (int) (top + mTextMargin * 3 + mOffset4CenterText);
            IndexData indexData = data.getIndexData();
            for (int i = mMas.length - 1; i >= 0; i--) { // low 2, high 1, mid 0
                String maText = "BOLL: " + formatNumber(mid.floatValue());
                if (i == 2) {
                    maText = "LB: " + formatNumber(indexData.getBollLow());
                } else if (i == 1) {
                    maText = "UB: " + formatNumber(indexData.getBollUp());
                }
                setBollTextPaint(sPaint, i);
                float bollTextLength = sPaint.measureText(maText);
                textX -= bollTextLength;
                canvas.drawText(maText, textX, textY, sPaint);
                textX -= mTextMargin * 1.5;
            }
        }
    }

    private void drawBollLines(Canvas canvas) {
        for (int i = 0; i < mMas.length; i++) { // mid 0, high 1, low 2
            setBollLinePaint(sPaint, i);
            float startX = -1;
            float startY = -1;
            for (int k = mStart; k < mEnd; k++) {
                Float mid = mDataList.get(k).getMas(mBoll[0]);
                if (mid == null) continue;

                float priceValue = mid.floatValue();
                IndexData indexData = mDataList.get(k).getIndexData();
                if (i == 1) { // high
                    priceValue = indexData.getBollUp();
                } else if (i == 2) { // low
                    priceValue = indexData.getBollLow();
                }
                float chartX = getChartXOfScreen(k);
                float chartY = getChartY(priceValue);
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
    }

    protected void drawMALines(Canvas canvas) {
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
    }

    private String formatTimestamp(long timestamp) {
        mDate.setTime(timestamp);
        return mDateFormat.format(mDate);
    }

    @Override
    protected void drawCrossLines(boolean indexesEnable, int touchIndex,
                                  int left, int top, int width, int height,
                                  int left2, int volTop, int width2, int volHeight, Canvas canvas) {
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
        calculateCandlesRange();
        calculateIndexValues();

        super.onDraw(canvas);

        if (isDragging()) {
            onSidesReached();
        }
    }

    private void onSidesReached() {
        if (mOnSidesReachedListener != null && mDataList.size() > mCandleNum) {
            if (mStart == 0) {
                mOnSidesReachedListener.onStartSideReached(mDataList.get(mStart));
            }
            if (mEnd == mDataList.size()) {
                mOnSidesReachedListener.onEndSideReached(mDataList.get(mEnd - 1));
            }
        }
    }

    @Override
    protected float getChartY(float y) {
        float[] baseLines = mChartCfg.getBaseLineArray();

        // When values beyond baselines, eg. ma. return -1
        if (y > baseLines[0] || y < baseLines[baseLines.length - 1]) {
            return -1;
        }

        int height = getMainChartHeight() - 2 * mTextMargin; //
        y = (baseLines[0] - y) / (baseLines[0] - baseLines[baseLines.length - 1]) * height;
        return y + getPaddingTop() + mTextMargin;
    }


    private void calculateIndexValues() {
        if (mChartCfg.getMainIndex() == ChartCfg.INDEX_MA) {
            calculateMovingAverageValues();
        } else if (mChartCfg.getMainIndex() == ChartCfg.INDEX_BOLL) {
            calculateBollValues();
        }
    }

    private void calculateBollValues() {
        int movingAverage = mBoll[0];
        for (int i = mStart; i < mEnd; i++) {
            int start = i - movingAverage + 1;
            if (start < 0) continue;
            if (mDataList.get(i).getMas(movingAverage) != null) continue;

            float maValue = calculateMovingAverageValue(start, movingAverage);
            mDataList.get(i).addMas(movingAverage, maValue);
            float mdValue = calculateBollMdValue(start, maValue, movingAverage);
            mDataList.get(i).getIndexData().setBollUp(maValue + mBoll[1] * mdValue);
            mDataList.get(i).getIndexData().setBollLow(maValue - mBoll[1] * mdValue);
        }
    }

    private float calculateBollMdValue(int start, float maValue, int movingAverage) {
        double result = 0;
        for (int i = start; i < start + movingAverage; i++) {
            result += Math.pow(mDataList.get(i).getClosePrice() - maValue, 2);
        }
        return (float) Math.sqrt(result / movingAverage);
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

    private void calculateCandlesRange() {
        mCandleNum = (int) (mChartCfg.getXAxis() / mChartCfg.getViewScale());
        mStart = mDataList.size() - mCandleNum < 0
                ? 0 : (mDataList.size() - mCandleNum - getStartPointOffset());
        int length = Math.min(mDataList.size(), mCandleNum);
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

    private void setBollLinePaint(Paint paint, int index) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mMALineWidth);
        paint.setPathEffect(null);
        paint.setColor(mChartColor.getMaColor(mMas[index]));
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

    private void setBollTextPaint(Paint paint, int index) {
        paint.setColor(mChartColor.getMaColor(mMas[index]));
        paint.setTextSize(mFontSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    private void setMovingAveragesTextPaint(Paint paint, int ma) {
        paint.setColor(mChartColor.getMaColor(ma));
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
        mBufferDataList.clear();
        if (dataList != null) {
            mBufferDataList.addAll(dataList);
        }
        if (getDragTransX() == 0) {
            flush();
        }
    }

    public void flush() {
        reset();
        mVisibleList.clear();
        mDataList.clear();
        mDataList.addAll(mBufferDataList);
        redraw();
    }

    public void addHistoryData(List<Data> data) {
        mDataList.addAll(0, data);
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
