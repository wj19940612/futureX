package com.songbai.futurex.view.chart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.CurrencyUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Modified by john on 2018/6/16
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class KlineDataDetailView extends FrameLayout {

    @BindView(R.id.openPrice)
    TextView mOpenPrice;
    @BindView(R.id.highestPrice)
    TextView mHighestPrice;
    @BindView(R.id.lowestPrice)
    TextView mLowestPrice;
    @BindView(R.id.tradeVolume)
    TextView mTradeVolume;
    @BindView(R.id.closePrice)
    TextView mClosePrice;
    @BindView(R.id.date)
    TextView mDate;

    private int mPriceScale;

    private SimpleDateFormat mSimpleDateFormat;
    private Date mSimpleDate;
    private String mDateFormatStr;

    public KlineDataDetailView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_kline_data_detail, this, true);
        ButterKnife.bind(this);

        mDateFormatStr = "yyyy/MM/dd HH:mm";
        mSimpleDateFormat = new SimpleDateFormat(mDateFormatStr);
        mSimpleDate = new Date();
    }

    public void setDateFormatStr(String dateFormatStr) {
        mDateFormatStr = dateFormatStr;
        mSimpleDateFormat.applyPattern(mDateFormatStr);
    }

    public void setKlineData(Kline.Data data) {
        mOpenPrice.setText(getPrice(R.string.open_price_x, data.getOpenPrice()));
        mHighestPrice.setText(getPrice(R.string.highest_price_x, data.getMaxPrice()));
        mLowestPrice.setText(getPrice(R.string.lowest_price_x, data.getMinPrice()));
        mClosePrice.setText(getPrice(R.string.close_price_x, data.getClosePrice()));
        mTradeVolume.setText(getVolume(R.string.trade_volume_x, data.getNowVolume()));
        mSimpleDate.setTime(data.getTimestamp());
        mDate.setText(mSimpleDateFormat.format(mSimpleDate));
    }

    private String getPrice(int res, double value) {
        return getContext().getString(res, CurrencyUtils.getPrice(value, mPriceScale));
    }

    private String getVolume(int res, double volume) {
        return getContext().getString(res, CurrencyUtils.getVolume(volume));
    }

    public void setPriceScale(int pricePoint) {
        mPriceScale = pricePoint;
    }
}
