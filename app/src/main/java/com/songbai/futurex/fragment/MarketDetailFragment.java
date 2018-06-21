package com.songbai.futurex.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
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
import com.songbai.futurex.model.PairDesc;
import com.songbai.futurex.utils.NumUtils;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.view.ChartsRadio;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.chart.ChartCfg;
import com.songbai.futurex.view.chart.ChartColor;
import com.songbai.futurex.view.chart.DeepView;
import com.songbai.futurex.view.chart.Kline;
import com.songbai.futurex.view.chart.KlineDataDetailView;
import com.songbai.futurex.view.chart.KlineUtils;
import com.songbai.futurex.view.chart.KlineView;
import com.songbai.futurex.view.chart.TrendV;
import com.songbai.futurex.view.chart.TrendView;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.market.MarketSubscriber;
import com.songbai.futurex.websocket.model.DeepData;
import com.songbai.futurex.websocket.model.MarketData;
import com.songbai.futurex.websocket.model.PairMarket;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    ChartsRadio mChartRadio;
    @BindView(R.id.kline)
    KlineView mKline;
    @BindView(R.id.tradeDetailRadio)
    RadioHeader mTradeDetailRadio;
    @BindView(R.id.klineDataDetailView)
    KlineDataDetailView mKlineDataDetailView;
    @BindView(R.id.trend)
    TrendView mTrend;
    @BindView(R.id.deepView)
    DeepView mDeepView;
    @BindView(R.id.minBidPrice)
    TextView mMinBidPrice;
    @BindView(R.id.maxAskPrice)
    TextView mMaxAskPrice;
    @BindView(R.id.midLastPrice)
    TextView mMidLastPrice;
    @BindView(R.id.optional)
    TextView mOptional;

    private CurrencyPair mCurrencyPair;
    private MarketSubscriber mMarketSubscriber;
    private PairDesc mPairDesc;

    private List<DeepData> mBuyDeepList;
    private List<DeepData> mSellDeepList;

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
        mChartRadio.setOnTabSelectedListener(new ChartsRadio.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                if (position == 0) {
                    showTrendView();
                    requestTrendData();
                } else {
                    showKlineView();
                    requestKlineData();
                }
            }
        });

        mMarketSubscriber = new MarketSubscriber(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code) {
                new DataParser<Response<PairMarket>>(data) {
                    @Override
                    public void onSuccess(Response<PairMarket> pairMarketResponse) {
                        updateMarketDataView(pairMarketResponse.getContent().getQuota());
                        updateDeepDataView(pairMarketResponse.getContent().getDeep());
                    }
                }.parse();
            }
        });

        requestPairDescription();
    }

    private void updateDeepDataView(PairMarket.Deep deep) {
        mBuyDeepList = deep.getBuyDeep();
        mSellDeepList = deep.getSellDeep();
        new CalcDeepTask(mBuyDeepList, mSellDeepList, this).execute();
    }

    private void updateDeepDataView() {
        if (mBuyDeepList != null && !mBuyDeepList.isEmpty()
                && mSellDeepList != null && !mSellDeepList.isEmpty()) {
            mDeepView.setDeepList(mBuyDeepList, mSellDeepList);
            mMinBidPrice.setText(NumUtils.getPrice(mBuyDeepList.get(mBuyDeepList.size() - 1).getPrice()));
            mMaxAskPrice.setText(NumUtils.getPrice(mSellDeepList.get(mSellDeepList.size() - 1).getPrice()));
        }
    }

    @OnClick({R.id.buyIn, R.id.sellOut})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buyIn:
                break;
            case R.id.sellOut:
                break;
        }
    }

    static class CalcDeepTask extends AsyncTask<Void, Void, Void> {

        private List<DeepData> mBuyDeepList;
        private List<DeepData> mSellDeepList;
        private WeakReference<MarketDetailFragment> mReference;

        public CalcDeepTask(List<DeepData> buyDeepList, List<DeepData> sellDeepList, MarketDetailFragment fragment) {
            mBuyDeepList = buyDeepList;
            mSellDeepList = sellDeepList;
            mReference = new WeakReference<>(fragment);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < mBuyDeepList.size(); i++) {
                DeepData deepData = mBuyDeepList.get(i);
                if (i == 0) {
                    deepData.setTotalCount(deepData.getCount());
                } else {
                    deepData.setTotalCount(deepData.getCount() + mBuyDeepList.get(i - 1).getTotalCount());
                }
            }

            for (int i = 0; i < mSellDeepList.size(); i++) {
                DeepData deepData = mSellDeepList.get(i);
                if (i == 0) {
                    deepData.setTotalCount(deepData.getCount());
                } else {
                    deepData.setTotalCount(deepData.getCount() + mSellDeepList.get(i - 1).getTotalCount());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mReference != null && mReference.get() != null) {
                mReference.get().updateDeepDataView();
            }
        }
    }


    private void requestPairDescription() {
        Apic.getPairDescription(mCurrencyPair.getPairs()).tag(TAG)
                .callback(new Callback4Resp<Resp<PairDesc>, PairDesc>() {
                    @Override
                    protected void onRespData(PairDesc data) {
                        mPairDesc = data;
                        initCharts();
                        requestTrendData();
                    }
                }).fireFreely();
    }

    private void updateMarketDataView(MarketData marketData) {
        if (marketData == null) return;
        mLastPrice.setText(NumUtils.getPrice(marketData.getLastPrice()));
        mMidLastPrice.setText(NumUtils.getPrice(marketData.getLastPrice()));
        
        mPriceChange.setText(NumUtils.getPrefixPercent(marketData.getUpDropSpeed()));
        mHighestPrice.setText(NumUtils.getPrice(marketData.getHighestPrice()));
        mLowestPrice.setText(NumUtils.getPrice(marketData.getLowestPrice()));
        mTradeVolume.setText(NumUtils.getVolume(marketData.getVolume()));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMarketSubscriber.resume();
        mMarketSubscriber.subscribe(mCurrencyPair.getPairs());
    }

    @Override
    public void onPause() {
        super.onPause();
        mMarketSubscriber.unSubscribe(mCurrencyPair.getPairs());
        mMarketSubscriber.pause();
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
        klineCfg.setNumberScale(mPairDesc.getPairs().getPricePoint());
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