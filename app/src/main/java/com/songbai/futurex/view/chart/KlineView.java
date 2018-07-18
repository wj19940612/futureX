package com.songbai.futurex.view.chart;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.R;

import java.util.List;

/**
 * Modified by john on 09/04/2018
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class KlineView extends RelativeLayout {

    private Kline mKline;
    private View mMAsView;

    private TextView mMa5;
    private TextView mMa10;
    private TextView mMa30;

    public KlineView(Context context) {
        super(context);
        init();
    }

    public KlineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mKline = new Kline(getContext());
        mKline.setOnMADataChangedListener(new Kline.OnMADataChangedListener() {
            @Override
            public void onMADataChanged(Kline.Data data) {
                if (data != null) {
                    mMa5.setText(formatMaValue(5, data.getMas(5)));
                    mMa10.setText(formatMaValue(10, data.getMas(10)));
                    mMa30.setText(formatMaValue(30, data.getMas(30)));
                } else {
                    mMa5.setText(formatMaValue(5, null));
                    mMa10.setText(formatMaValue(10, null));
                    mMa30.setText(formatMaValue(30, null));
                }
            }
        });

        mMAsView = LayoutInflater.from(getContext()).inflate(R.layout.view_moving_averages, null);
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int rightMargin = (int) mKline.dp2Px(12);
        params.setMargins(0, 0, rightMargin, 0);
        params.addRule(ALIGN_PARENT_RIGHT);

        mMa5 = mMAsView.findViewById(R.id.ma5);
        mMa10 = mMAsView.findViewById(R.id.ma10);
        mMa30 = mMAsView.findViewById(R.id.ma30);

        addView(mKline);
        addView(mMAsView, params);
    }

    private String formatMaValue(int maKey, Float maValue) {
        if (maValue == null) return "MA" + maKey + ": --";
        return "MA" + maKey + ": " + mKline.formatNumber(maValue);
    }

    public void setDateFormatStr(String dateFormatStr) {
        mKline.setDateFormatStr(dateFormatStr);
    }

    public void setDataList(List<Kline.Data> dataList) {
        mKline.setDataList(dataList);
    }

    public ChartCfg getChartCfg() {
        return mKline.getChartCfg();
    }

    public ChartColor getChartColor() {
        return mKline.getChartColor();
    }

    public void setOnCrossLineAppearListener(Kline.OnCrossLineAppearListener listener) {
        mKline.setOnCrossLineAppearListener(listener);
    }

    public void setOnSidesReachedListener(Kline.OnSidesReachedListener onSidesReachedListener) {
        mKline.setOnSidesReachedListener(onSidesReachedListener);
    }

    public void reset() {
        mKline.reset();
    }

    public void setLastPrice(double lastPrice) {
        mKline.setLastPrice((float) lastPrice);
    }

    public void addHistoryData(List<Kline.Data> data) {
        mKline.addHistoryData(data);
    }

    public void flush() {
        mKline.flush();
    }
}
