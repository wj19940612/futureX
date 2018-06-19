package com.songbai.futurex.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.chart.ChartCfg;
import com.songbai.futurex.view.chart.ChartColor;
import com.songbai.futurex.view.chart.Kline;
import com.songbai.futurex.view.chart.KlineDataDetailView;
import com.songbai.futurex.view.chart.KlineUtils;
import com.songbai.futurex.view.chart.KlineView;
import com.songbai.futurex.view.chart.TrendV;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/6/14
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class MarketDetailFragment extends UniqueActivity.UniFragment {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    Unbinder unbinder;
    @BindView(R.id.lastPrice)
    TextView mLastPrice;
    @BindView(R.id.priceChange)
    TextView mPriceChange;
    @BindView(R.id.highestPrice)
    TextView mHighestPrice;
    @BindView(R.id.highText)
    TextView mHighText;
    @BindView(R.id.lowestPrice)
    TextView mLowestPrice;
    @BindView(R.id.lowText)
    TextView mLowText;
    @BindView(R.id.tradeVolume)
    TextView mTradeVolume;
    @BindView(R.id.chartRadio)
    RadioHeader mChartRadio;
    @BindView(R.id.kline)
    KlineView mKline;
    @BindView(R.id.tradeDetailRadio)
    RadioHeader mTradeDetailRadio;
    @BindView(R.id.klineDataDetailView)
    KlineDataDetailView mKlineDataDetailView;
    @BindView(R.id.trend)
    TrendV mTrend;

    private CurrencyPair mCurrencyPair;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mCurrencyPair = extras.getParcelable(ExtraKeys.CURRENCY_PAIR);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mTitleBar.setTitle(mCurrencyPair.getUpperCasePairName());
        mChartRadio.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                if (position == 0) {
                    showTrendView();
                    requestTrendData();
                } else {
                    showKlineView();
                    requestKlineData();
                }
            }
        });

        initCharts();

        requestTrendData();
    }

    private void requestTrendData() {
        requestTrendData(null);
    }

    private void requestTrendData(final String endTime) {
        String code = mCurrencyPair.getPairs();
        Apic.getTrendData(code, endTime).tag(TAG)
                .callback(new Callback4Resp<Resp<List<TrendV.Data>>, List<TrendV.Data>>() {
            @Override
            protected void onRespData(List<TrendV.Data> data) {
                Collections.sort(data);
                if (TextUtils.isEmpty(endTime)) {
                    mTrend.setDataList(data);
                } else {

                }
            }
        }).fire();
    }

    private void requestKlineData() {
        requestKlineData(null);
    }

    private void requestKlineData(final String endTime) {
        String klineType = KlineUtils.getKlineType(mChartRadio.getSelectedPosition());
        String code = mCurrencyPair.getPairs();
        Apic.getKlineData(code, klineType, endTime).tag(TAG)
                .callback(new Callback4Resp<Resp<List<Kline.Data>>, List<Kline.Data>>() {
                    @Override
                    protected void onRespData(List<Kline.Data> data) {
                        Collections.sort(data);
                        if (TextUtils.isEmpty(endTime)) {
                            mKline.setDataList(data);
                            mKline.setDateFormatStr(KlineUtils.getDateFormat(mChartRadio.getSelectedPosition()));
                        } else {
                            if (data.isEmpty()) ToastUtil.show(R.string.no_more_data);
                            mKline.addHistoryData(data);
                        }
                    }
                }).fire();
    }

    private void showTrendView() {
        mKline.setVisibility(View.GONE);
        mTrend.setVisibility(View.VISIBLE);
    }

    private void showKlineView() {
        mTrend.setVisibility(View.GONE);
        mKline.setVisibility(View.VISIBLE);
    }

    private void initCharts() {
        // kline
        ChartCfg klineCfg = mKline.getChartCfg();
        klineCfg.setBaseLines(6);
        klineCfg.setIndexesBaseLines(2);
        klineCfg.setXAxis(45);
        klineCfg.setNumberScale(2);
        klineCfg.setEnableCrossLine(true);
        klineCfg.setEnableDrag(true);
        klineCfg.setIndexesEnable(true);

        ChartColor klineColor = mKline.getChartColor();
        klineColor.setBaseLineColor(ContextCompat.getColor(getActivity(), R.color.bgF5));
        klineColor.setBaseTextColor(ContextCompat.getColor(getActivity(), R.color.text99));
        klineColor.setPositiveColor(ContextCompat.getColor(getActivity(), R.color.green));
        klineColor.setNegativeColor(ContextCompat.getColor(getActivity(), R.color.red));
        klineColor.setCrossLineColor(Color.parseColor("#939EA5"));
        klineColor.setCrossLinePriceColor(ContextCompat.getColor(getActivity(), R.color.white));
        klineColor.setMaColor(5, Color.parseColor("#1A7AD5"));
        klineColor.setMaColor(10, Color.parseColor("#FFB405"));
        klineColor.setMaColor(30, Color.parseColor("#7C3BB9"));

        mKline.setOnCrossLineAppearListener(new Kline.OnCrossLineAppearListener() {
            @Override
            public void onAppear(Kline.Data data) {
                mChartRadio.setVisibility(View.GONE);
                mKlineDataDetailView.setVisibility(View.VISIBLE);
//                mDataDetail.setText(getString(R.string.open_x_close_x_max_x_min_x,
//                        FinanceUtil.formatWithScale(data.getOpenPrice(), mScale),
//                        FinanceUtil.formatWithScale(data.getClosePrice(), mScale),
//                        FinanceUtil.formatWithScale(data.getMaxPrice(), mScale),
//                        FinanceUtil.formatWithScale(data.getMinPrice(), mScale),
//                        DateUtil.format(data.getTimestamp(), "yyyy/MM/dd HH:mm:ss")));
            }

            @Override
            public void onDisappear() {
                mChartRadio.setVisibility(View.VISIBLE);
                mKlineDataDetailView.setVisibility(View.GONE);
            }
        });
        mKline.setOnSidesReachedListener(new Kline.OnSidesReachedListener() {
            @Override
            public void onStartSideReached(Kline.Data data) {

            }

            @Override
            public void onEndSideReached(Kline.Data data) {
//                if (mRefreshKlineLater) {
//                    requestKline();
//                }
            }
        });

        ChartCfg trendCfg = mTrend.getChartCfg();
        trendCfg.setChartCfg(klineCfg);
        ChartColor trendColor = mTrend.getChartColor();
        trendColor.setChartColor(klineColor);
        trendColor.setClosePriceColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        mTrend.setDateFormatStr("HH:mm");
        mTrend.setOnSidesReachedListener(new Kline.OnSidesReachedListener() {
            @Override
            public void onStartSideReached(Kline.Data data) {

            }

            @Override
            public void onEndSideReached(Kline.Data data) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}