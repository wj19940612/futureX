package com.songbai.futurex.view.chart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.songbai.futurex.R;

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
    @BindView(R.id.priceChangeAmt)
    TextView mPriceChangeAmt;
    @BindView(R.id.highestPrice)
    TextView mHighestPrice;
    @BindView(R.id.priceChangePercent)
    TextView mPriceChangePercent;
    @BindView(R.id.lowestPrice)
    TextView mLowestPrice;
    @BindView(R.id.tradeVolume)
    TextView mTradeVolume;
    @BindView(R.id.closePrice)
    TextView mClosePrice;
    @BindView(R.id.date)
    TextView mDate;

    public KlineDataDetailView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_kline_data_detail, this, true);
        ButterKnife.bind(this);
    }


}
