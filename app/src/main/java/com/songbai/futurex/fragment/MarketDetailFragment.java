package com.songbai.futurex.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.PairDesc;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.NumUtils;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.view.ChartsRadio;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.RealtimeDealView;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.TradeVolumeView;
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
import com.songbai.futurex.websocket.model.DealData;
import com.songbai.futurex.websocket.model.DeepData;
import com.songbai.futurex.websocket.model.MarketData;
import com.songbai.futurex.websocket.model.PairMarket;
import com.songbai.futurex.websocket.model.TradeDir;

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
    @BindView(R.id.optional)
    TextView mOptional;
    @BindView(R.id.tradeVolumeView)
    TradeVolumeView mTradeVolumeView;
    @BindView(R.id.tradeDealView)
    RealtimeDealView mTradeDealView;
    @BindView(R.id.buyIn)
    TextView mBuyIn;
    @BindView(R.id.sellOut)
    TextView mSellOut;

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
        mTradeVolumeView.setCurrencyPair(mCurrencyPair);
        mTradeDealView.setCurrencyPair(mCurrencyPair);

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
        mTradeDetailRadio.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                if (position == 0) {
                    mDeepView.setVisibility(View.VISIBLE);
                    mTradeVolumeView.setVisibility(View.VISIBLE);
                    mTradeDealView.setVisibility(View.GONE);
                } else {
                    mDeepView.setVisibility(View.GONE);
                    mTradeVolumeView.setVisibility(View.GONE);
                    mTradeDealView.setVisibility(View.VISIBLE);
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
                        updateTradeDealView(pairMarketResponse.getContent().getDetail());
                    }
                }.parse();
            }
        });

        requestPairDescription();
    }

    private void updateTradeDealView(List<DealData> dealDataList) {
        mTradeDealView.addDealData(dealDataList);
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
            mTradeVolumeView.setDeepList(mBuyDeepList, mSellDeepList);
        }
    }

    @OnClick({R.id.buyIn, R.id.sellOut, R.id.optional})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buyIn:
                Intent intent = new Intent()
                        .putExtra(ExtraKeys.TRADE_DIRECTION, TradeDir.DIR_BUY_IN)
                        .putExtra(ExtraKeys.CURRENCY_PAIR, mCurrencyPair);
                setResult(Activity.RESULT_FIRST_USER, intent);
                finish();
                break;
            case R.id.sellOut:
                Intent intent1 = new Intent()
                        .putExtra(ExtraKeys.TRADE_DIRECTION, TradeDir.DIR_SELL_OUT)
                        .putExtra(ExtraKeys.CURRENCY_PAIR, mCurrencyPair);
                setResult(Activity.RESULT_FIRST_USER, intent1);
                finish();
                break;
            case R.id.optional:
                if (LocalUser.getUser().isLogin()) {
                    toggleOptionalStatus();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
        }
    }

    private void toggleOptionalStatus() {
        if (mOptional.isSelected()) { // 已经添加自选
            Apic.cancelOptional(mCurrencyPair.getPairs()).tag(TAG)
                    .callback(new Callback<Resp>() {
                        @Override
                        protected void onRespSuccess(Resp resp) {
                            mOptional.setSelected(false);
                            mOptional.setText(R.string.add_optional_no_plus);
                            ToastUtil.show(R.string.optional_cancel);

                            setResult(Activity.RESULT_OK); // refresh optional page
                        }
                    }).fire();
        } else {
            Apic.addOptional(mCurrencyPair.getPairs()).tag(TAG)
                    .callback(new Callback<Resp>() {
                        @Override
                        protected void onRespSuccess(Resp resp) {
                            mOptional.setSelected(true);
                            mOptional.setText(R.string.already_added);
                            ToastUtil.show(R.string.optional_added);

                            setResult(Activity.RESULT_OK);
                        }
                    }).fire();
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
                        initMarketViews();
                        requestTrendData();
                        updateOptionalStatus();
                    }
                }).fireFreely();
    }

    private void updateOptionalStatus() {
        if (mPairDesc.getPairs().getOption() == CurrencyPair.OPTIONAL_ADDED) {
            mOptional.setSelected(true);
            mOptional.setText(R.string.already_added);
        } else {
            mOptional.setSelected(false);
            mOptional.setText(R.string.add_optional_no_plus);
        }
    }

    private void initMarketViews() {
        mDeepView.setPriceScale(mPairDesc.getPairs().getPricePoint());
        mTradeVolumeView.setPriceScale(mPairDesc.getPairs().getPricePoint());
        mTradeVolumeView.setMergeScale(mPairDesc.getPairs().getPricePoint());
        mTradeDealView.setPriceScale(mPairDesc.getPairs().getPricePoint());
        mKlineDataDetailView.setPriceScale(mPairDesc.getPairs().getPricePoint());
    }

    private void updateMarketDataView(MarketData marketData) {
        if (marketData == null) return;
        mLastPrice.setText(NumUtils.getPrice(marketData.getLastPrice()));
        mDeepView.setLastPrice(marketData.getLastPrice());

        mPriceChange.setText(NumUtils.getPrefixPercent(marketData.getUpDropSpeed()));
        mHighestPrice.setText(NumUtils.getPrice(marketData.getHighestPrice()));
        mLowestPrice.setText(NumUtils.getPrice(marketData.getLowestPrice()));
        mTradeVolume.setText(NumUtils.getVolume(marketData.getVolume()));
    }

    @Override
    public void onTimeUp(int count) {
        if (mChartRadio.getSelectedPosition() == 0) {
            requestTrendData();
        } else {
            requestKlineData();
        }
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

                            startScheduleJobNext(KlineUtils.getRefreshInterval(mChartRadio.getSelectedPosition()));
                        } else {
                            if (data.isEmpty()) ToastUtil.show(R.string.no_more_data);
                            mTrend.addHistoryData(data);
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

                            startScheduleJobNext(KlineUtils.getRefreshInterval(mChartRadio.getSelectedPosition()));
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
                mKlineDataDetailView.setKlineData(data);
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
                requestKlineData(Uri.encode(data.getTime()));
            }

            @Override
            public void onEndSideReached(Kline.Data data) {
                mKline.flush();
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
                requestTrendData(Uri.encode(data.getTime()));
            }

            @Override
            public void onEndSideReached(Kline.Data data) {
                mTrend.flush();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}