package com.songbai.futurex.wrapper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.wrapres.ExtraKeys;
import com.songbai.wrapres.KlineDataPlane;
import com.songbai.wrapres.TitleBar;
import com.songbai.wrapres.autofit.AutofitTextView;
import com.songbai.wrapres.chart.InfiniteTrendChart;
import com.songbai.wrapres.chart.KlineChart;
import com.songbai.wrapres.chart.KlineViewData;
import com.songbai.wrapres.chart.TrendData;
import com.songbai.wrapres.model.MarketData;
import com.songbai.wrapres.tabLayout.TabLayout;
import com.songbai.wrapres.utils.MarketDataUtils;
import com.songbai.futurex.http.Resp;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarketDetailActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.lastPrice)
    AutofitTextView mLastPrice;
    @BindView(R.id.priceChange)
    TextView mPriceChange;
    @BindView(R.id.volume)
    TextView mVolume;
    @BindView(R.id.highest)
    TextView mHighest;
    @BindView(R.id.bidPrice1)
    TextView mBidPrice1;
    @BindView(R.id.marketValue)
    TextView mMarketValue;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.lowestPrice)
    TextView mLowestPrice;
    @BindView(R.id.askPrice1)
    TextView mAskPrice1;
    @BindView(R.id.klinePlane)
    KlineDataPlane mKlinePlane;
    @BindView(R.id.klineChart)
    KlineChart mKlineChart;
    @BindView(R.id.trendChart)
    InfiniteTrendChart mTrendChart;

    private MarketData mMarketData;
    private String mCode;
    private String mExchangeCode;

    private boolean mRefreshTrendWhenArriveRight;
    private boolean mRefreshKlineWhenArriveRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail);
        ButterKnife.bind(this);

        initData(getIntent());

        initTitleBar();
        initTabLayout();
        initCharts();

        updateMarketView();

        requestSingleMarket();

        showTrendView();
        requestTrendData(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startScheduleJobRightNow(5000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScheduleJob();
    }

    private void initCharts() {
        InfiniteTrendChart.Settings settings = new InfiniteTrendChart.Settings();
        settings.setBaseLines(5);
        settings.setNumberScale(mMarketData.getHighestPrice() >= 10 ? 2 : 4);
        settings.setXAxis(45);
        settings.setIndexesType(KlineChart.Settings.INDEXES_VOL);
        settings.setIndexesEnable(true);
        settings.setIndexesBaseLines(2);
        mTrendChart.setSettings(settings);
        mTrendChart.setOnDragListener(new InfiniteTrendChart.OnDragListener() {
            @Override
            public void onArriveLeft(TrendData theLeft) {
                requestTrendData(theLeft.getTime());
            }

            @Override
            public void onArriveRight(TrendData theRight) {
                if (mRefreshTrendWhenArriveRight) {
                    requestTrendData(null);
                }
            }
        });

        KlineChart.Settings klineSettings = new KlineChart.Settings();
        klineSettings.setBaseLines(5);
        klineSettings.setNumberScale(mMarketData.getHighestPrice() >= 10 ? 2 : 4);
        klineSettings.setXAxis(45);
        klineSettings.setIndexesType(KlineChart.Settings.INDEXES_VOL);
        klineSettings.setIndexesEnable(true);
        klineSettings.setIndexesBaseLines(2);
        mKlineChart.setSettings(klineSettings);
        mKlineChart.setOnTouchLinesAppearListener(new KlineChart.OnTouchLinesAppearListener() {
            @Override
            public void onAppear(KlineViewData data, KlineViewData previousData, boolean isLeftArea) {
                mTabLayout.setVisibility(View.INVISIBLE);
                mKlinePlane.setVisibility(View.VISIBLE);
                mKlinePlane.setPrices(data.getOpenPrice(), data.getMaxPrice(), data.getMinPrice(), data.getClosePrice());
                mKlinePlane.setDate(data.getTimeStamp());
            }

            @Override
            public void onDisappear() {
                mTabLayout.setVisibility(View.VISIBLE);
                mKlinePlane.setVisibility(View.INVISIBLE);
            }
        });
        mKlineChart.setOnDragListener(new KlineChart.OnDragListener() {
            @Override
            public void onArriveLeft(KlineViewData theLeft) {
                String klineType = (String) mKlineChart.getTag();
                requestKlineMarket(klineType, theLeft.getTime());
            }

            @Override
            public void onArriveRight(KlineViewData theRight) {
                if (mRefreshKlineWhenArriveRight) {
                    String klineType = (String) mKlineChart.getTag();
                    requestKlineMarket(klineType, null);
                }
            }
        });
    }

    /*设置最新价，人民币换算，涨跌幅以及颜色，以及一些基本的数据*/
    private void updateMarketView() {
        double lastPrice = mMarketData.getLastPrice();
        mLastPrice.setText(MarketDataUtils.formatDollar(lastPrice));
        // mPriceChange show last price in RMB, and price change

        mPriceChange.setText(MarketDataUtils.formatRmbWithSign(lastPrice * mMarketData.getRate()) + '\n'
                + MarketDataUtils.formatDollarWithPrefix(mMarketData.getUpDropPrice())
                + "  " + MarketDataUtils.percentWithPrefix(mMarketData.getUpDropSpeed()));

        int color = mMarketData.getUpDropSpeed() < 0 ? R.color.redPrimary : R.color.greenPrimary;
        mLastPrice.setTextColor(ContextCompat.getColor(getActivity(), color));
        mPriceChange.setTextColor(ContextCompat.getColor(getActivity(), color));

        mVolume.setText(MarketDataUtils.formatVolume(mMarketData.getLastVolume()));
        mHighest.setText(MarketDataUtils.formatDollarWithSign(mMarketData.getHighestPrice())
                + " " + MarketDataUtils.formatRmbWithSign(mMarketData.getHighestPrice() * mMarketData.getRate()));
        mBidPrice1.setText(MarketDataUtils.formatDollarWithSign(mMarketData.getBidPrice())
                + " " + MarketDataUtils.formatRmbWithSign(mMarketData.getBidPrice() * mMarketData.getRate()));

        mMarketValue.setText(MarketDataUtils.formatMarketValue(mMarketData.getMarketValue()));
        mLowestPrice.setText(MarketDataUtils.formatDollarWithSign(mMarketData.getLowestPrice())
                + " " + MarketDataUtils.formatRmbWithSign(mMarketData.getLowestPrice() * mMarketData.getRate()));
        mAskPrice1.setText(MarketDataUtils.formatDollarWithSign(mMarketData.getAskPrice())
                + " " + MarketDataUtils.formatRmbWithSign(mMarketData.getAskPrice() * mMarketData.getRate()));
    }

    private void initData(Intent intent) {
        mMarketData = intent.getParcelableExtra(ExtraKeys.DIGITAL_CURRENCY);
        mCode = mMarketData.getCode();
        mExchangeCode = mMarketData.getExchangeCode();
    }

    private void initTitleBar() {
        String baseCurrency = mMarketData.getName();
        String counterCurrency = mMarketData.getCurrencyMoney();
        String exchangeCode = mMarketData.getExchangeCode();

        View view = mTitleBar.getCustomView();
        TextView currencyPair = view.findViewById(R.id.currencyPair);
        TextView exchange = view.findViewById(R.id.exchange);
        currencyPair.setText(baseCurrency.toUpperCase() + "/" + counterCurrency.toUpperCase());
        exchange.setText(exchangeCode);
    }

    private void initTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.trend_chart));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.five_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.fifteen_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.thirty_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.sixty_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.day_k_line));
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
    }

    @Override
    public void onTimeUp(int count) {
        requestSingleMarket();

        int seconds = count * 5;
        if (mTrendChart.getVisibility() == View.VISIBLE && seconds % 60 == 0) {
            if (mTrendChart.getTransactionX() != 0) {
                mRefreshTrendWhenArriveRight = true;
            } else {
                requestTrendData(null);
            }
        }

        if (mKlineChart.getVisibility() == View.VISIBLE) {
            String klineType = (String) mKlineChart.getTag();
            if (klineType == null || klineType.equals("day")) return;

            int klineSeconds = Integer.valueOf(klineType) * 60;
            if (seconds % klineSeconds == 0) {
                if (mKlineChart.getTransactionX() != 0) {
                    mRefreshKlineWhenArriveRight = true;
                } else {
                    requestKlineMarket(klineType, null);
                }
            }
        }
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
                case 0:
                    showTrendView();
                    requestTrendData(null);
                    break;
                case 1:
                    showKlineView(false);
                    requestKlineMarket("5", null);
                    break;
                case 2:
                    showKlineView(false);
                    requestKlineMarket("15", null);
                    break;
                case 3:
                    showKlineView(false);
                    requestKlineMarket("30", null);
                    break;
                case 4:
                    showKlineView(false);
                    requestKlineMarket("60", null);
                    break;
                case 5:
                    showKlineView(true);
                    requestKlineMarket("day", null);
                    break;

            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void showKlineView(boolean dayLine) {
        mKlineChart.setVisibility(View.VISIBLE);
        mKlineChart.setDayLine(dayLine);
        mTrendChart.setVisibility(View.GONE);
    }

    private void showTrendView() {
        mKlineChart.setVisibility(View.GONE);
        mTrendChart.setVisibility(View.VISIBLE);
    }

    private void requestSingleMarket() {
        Apic.reqSingleMarket(mMarketData.getCode(), mMarketData.getExchangeCode()).tag(TAG)
                .callback(new Callback4Resp<Resp<MarketData>, MarketData>() {
                    @Override
                    protected void onRespData(MarketData data) {
                        if (mMarketData != null && !TextUtils.isEmpty(mMarketData.getName())) {
                            data.setName(mMarketData.getName());
                        }
                        mMarketData = data;
                        updateMarketView();
                    }
                }).fireFreely();
    }

    private void requestKlineMarket(String klineType, final String endTime) {
        mKlineChart.setTag(klineType);
        Apic.reqKlineMarket(mCode, mExchangeCode, klineType, Uri.encode(endTime)).tag(TAG)
                .callback(new Callback4Resp<Resp<List<KlineViewData>>, List<KlineViewData>>() {
                    @Override
                    protected void onRespData(List<KlineViewData> data) {
                        Collections.sort(data);
                        if (TextUtils.isEmpty(endTime)) {
                            mKlineChart.initWithData(data);
                            mRefreshKlineWhenArriveRight = false;
                        } else {
                            if (data.isEmpty()) {
                                ToastUtil.show(R.string.there_is_no_more_data);
                                return;
                            }
                            mKlineChart.addHistoryData(data);
                        }
                    }
                }).fireFreely();
    }

    private void requestTrendData(final String endTime) {
        Apic.reqTrendData(mCode, mExchangeCode, Uri.encode(endTime)).tag(TAG)
                .callback(new Callback4Resp<Resp<List<TrendData>>, List<TrendData>>() {
                    @Override
                    protected void onRespData(List<TrendData> data) {
                        Collections.sort(data);
                        if (TextUtils.isEmpty(endTime)) {
                            mTrendChart.initWithData(data);
                            mRefreshTrendWhenArriveRight = false;
                        } else {
                            if (data.isEmpty()) {
                                ToastUtil.show(R.string.there_is_no_more_data);
                                return;
                            }
                            mTrendChart.addHistoryData(data);
                        }
                    }
                }).fireFreely();
    }
}
