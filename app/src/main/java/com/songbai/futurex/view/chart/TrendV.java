package com.songbai.futurex.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;

/**
 * Modified by john on 2018/6/16
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class TrendV extends Kline {

    private float mTrendLineWidth;

    public TrendV(Context context) {
        super(context);
    }

    public TrendV(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        mTrendLineWidth = dp2Px(2);
        mMas = new int[]{60};
    }

    @Override
    protected void drawMainData(int left, int top, int width, int height, Canvas canvas) {
        Path path = getPath();
        float chartX = 0;
        float chartY = 0;
        float firstChartX = 0;
        for (int i = mStart; i < mEnd; i++) {
            Data data = mDataList.get(i);
            chartX = getChartXOfScreen(i, data);
            chartY = getChartY(data.getClosePrice());
            if (path.isEmpty()) {
                firstChartX = chartX;
                path.moveTo(chartX, chartY);
            } else {
                path.lineTo(chartX, chartY);
            }
        }

        setTrendLinePaint(sPaint);
        canvas.drawPath(path, sPaint);

        //fill area
        path.lineTo(chartX, top + height);
        path.lineTo(firstChartX, top + height);
        path.close();
        setTrendLineFillPaint(sPaint);
        canvas.drawPath(path, sPaint);
        sPaint.setShader(null);
    }

    private void setTrendLinePaint(Paint paint) {
        paint.setColor(getChartColor().getClosePriceColor());
        paint.setStrokeWidth(mTrendLineWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(null);
        paint.setShader(null);
    }

    private void setTrendLineFillPaint(Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, getHeight() * 3 / 5,
                Color.parseColor("#51228CD7"),
                Color.parseColor("#00228CD7"),
                Shader.TileMode.CLAMP));
    }
}
