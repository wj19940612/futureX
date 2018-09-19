package com.songbai.futurex.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.httplib.ReqError;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.SingleTradeActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.activity.mine.MyPropertyActivity;
import com.songbai.futurex.fragment.mine.ReChargeCoinFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CoinIntroduce;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.PairDesc;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.local.MakeOrder;
import com.songbai.futurex.model.mine.CoinAbleAmount;
import com.songbai.futurex.utils.AnimatorUtil;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.UmengCountEventId;
import com.songbai.futurex.view.ChartsRadio;
import com.songbai.futurex.view.FastTradingView;
import com.songbai.futurex.view.IntroduceView;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.RealtimeDealView;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.TradeVolumeView;
import com.songbai.futurex.view.chart.BaseChart;
import com.songbai.futurex.view.chart.ChartCfg;
import com.songbai.futurex.view.chart.ChartColor;
import com.songbai.futurex.view.chart.DeepView;
import com.songbai.futurex.view.chart.Kline;
import com.songbai.futurex.view.chart.KlineDataDetailView;
import com.songbai.futurex.view.chart.KlineUtils;
import com.songbai.futurex.view.chart.KlineView;
import com.songbai.futurex.view.chart.TrendV;
import com.songbai.futurex.view.chart.TrendView;
import com.songbai.futurex.view.dialog.WithDrawPsdViewController;
import com.songbai.futurex.view.popup.CurrencyPairsPopup;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.PushDestUtils;
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

    private static final int REQ_CODE_SEARCH = 99;

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
    @BindView(R.id.introduceView)
    IntroduceView mIntroduceView;
    @BindView(R.id.dimView)
    View mDimView;

    @BindView(R.id.chartRadioDropMenu)
    LinearLayout mChartRadioDropMenu;
    @BindView(R.id.indexDropMenu)
    ConstraintLayout mIndexDropMenu;

    @BindView(R.id.tradeButtons)
    LinearLayout mTradeButtons;
    @BindView(R.id.tradePause)
    TextView mTradePause;
    @BindView(R.id.tradeLayout)
    FastTradingView mTradeLayout;
    @BindView(R.id.viewStub)
    View mViewStub;

    private ValueAnimator mValueAnimator;

    private CurrencyPair mCurrencyPair;
    private MarketSubscriber mMarketSubscriber;
    private PairDesc mPairDesc;
    private CurrencyPairsPopup mPairsPopup;
    TextView mPairName;
    ImageView mPairArrow;

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
        initTitleBar();

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
                    requestKlineData(true);
                }
            }
        });
        mChartRadio.setChartsRadioDropMenu(mChartRadioDropMenu);
        mChartRadio.setIndexesDropMenu(mIndexDropMenu);
        mTradeDetailRadio.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                if (position == 0) {
                    mDeepView.setVisibility(View.VISIBLE);
                    mTradeVolumeView.setVisibility(View.VISIBLE);
                    mTradeDealView.setVisibility(View.GONE);
                    mIntroduceView.setVisibility(View.GONE);
                } else if (position == 1) {
                    mDeepView.setVisibility(View.GONE);
                    mTradeVolumeView.setVisibility(View.GONE);
                    mTradeDealView.setVisibility(View.VISIBLE);
                    mIntroduceView.setVisibility(View.GONE);
                } else {
                    mDeepView.setVisibility(View.GONE);
                    mTradeVolumeView.setVisibility(View.GONE);
                    mTradeDealView.setVisibility(View.GONE);
                    mIntroduceView.setVisibility(View.VISIBLE);
                }
            }
        });

        mTradeVolumeView.setOnItemClickListener(new TradeVolumeView.OnItemClickListener() {
            @Override
            public void onItemClick(String price, String volume) {
                if (mTradeLayout.getHeight() == ((RelativeLayout.LayoutParams) mTradeLayout.getLayoutParams()).bottomMargin) {
                    return;
                }
                mTradeLayout.updatePrice(price);
            }
        });

        mMarketSubscriber = new MarketSubscriber(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code, String dest) {
                if (PushDestUtils.isSoloMarket(dest)) {
                    new DataParser<Response<PairMarket>>(data) {
                        @Override
                        public void onSuccess(Response<PairMarket> pairMarketResponse) {
                            if (pairMarketResponse.getContent() == null || mPairDesc == null)
                                return;

                            updateMarketDataView(pairMarketResponse.getContent().getQuota());
                            updateDeepDataView(pairMarketResponse.getContent().getDeep());
                            updateTradeDealView(pairMarketResponse.getContent().getDetail());
                        }
                    }.parse();
                }
            }
        });

        mTradeLayout.setonFastTradeClickListener(new FastTradingView.onFastTradeClickListener() {
            @Override
            public void onMakeOrder(MakeOrder makeOrder) {
                requestMakeOrder(makeOrder);
                requestUserAccount();
            }

            @Override
            public void onFullTradeClick(int direction) {
                jumpFullTrade(direction);
                sinkAnim();
            }

            @Override
            public void onCloseTradeClick() {
                sinkAnim();
            }

            @Override
            public void onRecharge() {
                umengEventCount(UmengCountEventId.COIN0005);
                if (LocalUser.getUser().isLogin()) {
                    if (mCurrencyPair != null) {
                        UniqueActivity.launcher(getContext(), ReChargeCoinFragment.class)
                                .putExtra(ExtraKeys.COIN_TYPE, mCurrencyPair.getSuffixSymbol())
                                .execute();
                    } else {
                        Launcher.with(getActivity(), MyPropertyActivity.class).execute();
                    }
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });

        requestPairDescription();
    }

    private void initTitleBar() {
        mTitleBar.setBackClickListener(new TitleBar.OnBackClickListener() {
            @Override
            public void onClick() {
                umengEventCount(UmengCountEventId.MARKET00011);
            }
        });
        View customView = mTitleBar.getCustomView();
        mPairName = customView.findViewById(R.id.pairName);
        mPairArrow = customView.findViewById(R.id.pairArrow);
        mPairName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrencyPair == null) return;
                if (mPairsPopup != null && mPairsPopup.isShowing()) {
                    mPairsPopup.dismiss();
                } else {
                    showCurrencyPairPopup();
                }
            }
        });
        mPairArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPairName.performClick();
            }
        });
        mPairName.setText(mCurrencyPair.getUpperCasePairName());
    }

    private void showCurrencyPairPopup() {
        if (mPairsPopup == null) {
            mPairsPopup = new CurrencyPairsPopup(getActivity(), mCurrencyPair,
                    new CurrencyPairsPopup.OnCurrencyChangeListener() {
                        @Override
                        public void onCounterCurrencyChange(String counterCurrency, List<CurrencyPair> newDisplayList) {
                            requestCurrencyPairList(counterCurrency);
                        }

                        @Override
                        public void onBaseCurrencyChange(String baseCurrency, CurrencyPair currencyPair) {
                            switchNewCurrencyPair(currencyPair);
                        }
                    },
                    new CurrencyPairsPopup.OnSearchBoxClickListener() {
                        @Override
                        public void onSearchBoxClick() {
                            openSearchPage();
                        }
                    });
            mPairsPopup.setDimView(mDimView);
        }
        mPairsPopup.show(mTitleBar);
        mPairsPopup.selectCounterCurrency(mCurrencyPair.getSuffixSymbol());
    }

    private void switchNewCurrencyPair(CurrencyPair currencyPair) {
        mMarketSubscriber.unSubscribe(mCurrencyPair.getPairs());
        mCurrencyPair = currencyPair;
        mMarketSubscriber.subscribe(mCurrencyPair.getPairs());
        mPairDesc = null;

        mLastPrice.setText("--");
        mPriceChange.setText("--");
        mHighestPrice.setText("--");
        mLowestPrice.setText("--");
        mTradeVolume.setText("--");

        mTrend.setDataList(null);
        mTrend.flush();
        mKline.setDataList(null);
        mKline.flush();
        mDeepView.reset();
        mTradeVolumeView.reset();
        mTradeDealView.reset();

        mPairsPopup.setCurCurrencyPair(mCurrencyPair);
        mTradeVolumeView.setCurrencyPair(mCurrencyPair);
        mTradeDealView.setCurrencyPair(mCurrencyPair);
        mPairName.setText(mCurrencyPair.getUpperCasePairName());

        mTradeLayout.resetView();
        requestPairDescription();
        mTradeLayout.updateDirection();
    }

    private void openSearchPage() {
        UniqueActivity.launcher(getActivity(), SearchCurrencyFragment.class)
                .putExtra(ExtraKeys.SERACH_FOR_TRADE, true)
                .execute(this, REQ_CODE_SEARCH);
    }

    private void requestCurrencyPairList(final String counterCurrency) {
        if (counterCurrency.equalsIgnoreCase(getString(R.string.optional_text))) {
            Apic.getOptionalList().tag(TAG)
                    .id(counterCurrency)
                    .callback(new Callback4Resp<Resp<List<CurrencyPair>>, List<CurrencyPair>>() {
                        @Override
                        protected void onRespData(List<CurrencyPair> data) {
                            if (getId().equalsIgnoreCase(mPairsPopup.getSelectCounterCurrency())) {
                                mPairsPopup.showDisplayList(counterCurrency, data);
                            }
                        }
                    }).fire();
        } else {
            Apic.getCurrencyPairList(counterCurrency).tag(TAG)
                    .id(counterCurrency)
                    .callback(new Callback4Resp<Resp<List<CurrencyPair>>, List<CurrencyPair>>() {
                        @Override
                        protected void onRespData(List<CurrencyPair> data) {
                            if (getId().equalsIgnoreCase(mPairsPopup.getSelectCounterCurrency())) {
                                mPairsPopup.showDisplayList(counterCurrency, data);
                            }
                        }
                    }).fire();
        }
    }

    private void updateTradeDealView(List<DealData> dealDataList) {
        mTradeDealView.addDealData(dealDataList);
    }

    private void updateDeepDataView(PairMarket.Deep deep) {
        if (deep != null) {
            mBuyDeepList = deep.getBuyDeep();
            mSellDeepList = deep.getSellDeep();
            new CalcDeepTask(mBuyDeepList, mSellDeepList, this).execute();
        }
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
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), LoginActivity.class).execute();
            return;
        }

        switch (view.getId()) {
            case R.id.buyIn:
                umengEventCount(UmengCountEventId.MARKET0012);
                clickBuySell(TradeDir.DIR_BUY_IN);
                break;
            case R.id.sellOut:
                umengEventCount(UmengCountEventId.MARKET0013);
                clickBuySell(TradeDir.DIR_SELL_OUT);
                break;
            case R.id.optional:
                toggleOptionalStatus();
                break;
        }
    }

    public boolean onBackPressed() {
        if (mTradeLayout.isVisible()) {
            sinkAnim();
            return true;
        }

        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
        return false;
    }

    private void clickBuySell(int direction) {
        if (!Preference.get().isQuickExchange()) {
            jumpFullTrade(direction);
        } else {
            if (mPairDesc == null) return;

            if (!mTradeLayout.isFlowing()) {
                mTradeLayout.setDirection(direction);
            }
            flowAnim();
        }

    }

    private void jumpFullTrade(int direction) {
        Launcher.with(MarketDetailFragment.this, SingleTradeActivity.class)
                .putExtra(ExtraKeys.CURRENCY_PAIR, mCurrencyPair)
                .putExtra(ExtraKeys.TRADE_DIRECTION, direction)
                .putExtra(ExtraKeys.NOT_MAIN, true)
                .execute();
    }

    private void flowAnim() {
        if (mTradeLayout.isGone()) {
            mValueAnimator = AnimatorUtil.flowView(mTradeLayout, new AnimatorUtil.OnStartAndListener() {
                @Override
                public void onStart(Animator animation) {
                    mTradeLayout.setViewStatus(FastTradingView.VIEW_FLOWING);
                    mViewStub.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(Animator animation) {
                    mTradeLayout.setViewStatus(FastTradingView.VIEW_VISIBLE);
                }
            });
            mValueAnimator.start();
        }
    }

    private void sinkAnim() {
        if (!mTradeLayout.isSinking()) {
            mValueAnimator = AnimatorUtil.sinkView(mTradeLayout, new AnimatorUtil.OnStartAndListener() {
                @Override
                public void onStart(Animator animation) {
                    mTradeLayout.setViewStatus(FastTradingView.VIEW_SINKING);
                    mViewStub.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(Animator animation) {
                    mTradeLayout.setViewStatus(FastTradingView.VIEW_GONE);
                }
            });
            mValueAnimator.start();
        }
    }

    private void toggleOptionalStatus() {
        if (mOptional.isSelected()) { // 已经添加自选
            umengEventCount(UmengCountEventId.MARKET0015);
            Apic.cancelOptional(mCurrencyPair.getPairs()).tag(TAG)
                    .callback(new Callback<Resp>() {
                        @Override
                        protected void onRespSuccess(Resp resp) {
                            mOptional.setSelected(false);
                            mOptional.setText(R.string.add_optional_no_plus);
                            ToastUtil.show(R.string.optional_cancel);
                        }
                    }).fire();
        } else {
            umengEventCount(UmengCountEventId.MARKET0014);
            Apic.addOptional(mCurrencyPair.getPairs()).tag(TAG)
                    .callback(new Callback<Resp>() {
                        @Override
                        protected void onRespSuccess(Resp resp) {
                            mOptional.setSelected(true);
                            mOptional.setText(R.string.already_added);
                            ToastUtil.show(R.string.optional_added);

                        }
                    }).fire();
        }
        Preference.get().setOptionalListRefresh(true);
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
                        mCurrencyPair = mPairDesc.getPairs().getCurrencyPair();

                        initCharts();
                        initMarketViews();
                        requestDeepList();
                        requestDealList();
                        requestTrendData();
                        requestIntroduceData();
                        updateTradeButtons();
                        updateOptionalStatus();
                        requestUserAccount();
                        mChartRadio.performChildClick(0);
                        mTradeDetailRadio.selectTab(0);

                        mTradeLayout.updateData(mCurrencyPair, mPairDesc, mTradeVolumeView);
                    }
                }).fireFreely();
    }

    private void requestDeepList() {
        Apic.getMarketDeepList(mCurrencyPair.getPairs()).tag(TAG)
                .callback(new Callback4Resp<Resp<PairMarket.Deep>, PairMarket.Deep>() {
                    @Override
                    protected void onRespData(PairMarket.Deep data) {
                        updateDeepDataView(data);
                    }
                }).fireFreely();
    }

    private void requestDealList() {
        Apic.getTradeDealList(mCurrencyPair.getPairs()).tag(TAG)
                .callback(new Callback4Resp<Resp<List<DealData>>, List<DealData>>() {
                    @Override
                    protected void onRespData(List<DealData> data) {
                        updateTradeDealView(data);
                    }
                }).fireFreely();
    }

    private void requestIntroduceData() {
        Apic.requestIntroduce(mCurrencyPair.getPrefixSymbol()).tag(TAG)
                .callback(new Callback4Resp<Resp<CoinIntroduce>, CoinIntroduce>() {
                    @Override
                    protected void onRespData(CoinIntroduce data) {
                        updateIntroduce(data);
                    }
                }).fireFreely();
    }

    private void requestMakeOrder(final MakeOrder makeOrder) {
        Apic.makeOrder(makeOrder).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        ToastUtil.show(R.string.entrust_success);
                        if (mTradeLayout.getViewStatus() != FastTradingView.VIEW_SINKING) {
                            sinkAnim();
                        }
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.Code.NEEDS_FUND_PASSWORD) {
                            showFundPasswordDialog(makeOrder);
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
    }

    private void showFundPasswordDialog(final MakeOrder makeOrder) {
        WithDrawPsdViewController withDrawPsdViewController = new WithDrawPsdViewController(getActivity(),
                new WithDrawPsdViewController.OnClickListener() {
                    @Override
                    public void onForgetClick() {
                        umengEventCount(UmengCountEventId.TRADE0004);
                    }

                    @Override
                    public void onConfirmClick(String cashPwd, String googleAuth) {
                        makeOrder.setDrawPass(md5Encrypt(cashPwd));
                        requestMakeOrder(makeOrder);
                    }
                });
        withDrawPsdViewController.setShowGoogleAuth(false);
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog.setCustomViewController(withDrawPsdViewController)
                .show();
        withDrawPsdViewController.setTitle(R.string.fund_password_verification);
    }

    private void requestUserAccount() {
        if (LocalUser.getUser().isLogin()) {
            Apic.getAccountByUserForMuti(mCurrencyPair.getPrefixSymbol() + "," + mCurrencyPair.getSuffixSymbol()).tag(TAG)
                    .callback(new Callback<Resp<List<CoinAbleAmount>>>() {
                        @Override
                        protected void onRespSuccess(Resp<List<CoinAbleAmount>> resp) {
                            mTradeLayout.updateCoinAbleAmount(resp);
                            mTradeLayout.updateSelectPercentView();
                        }

                        @Override
                        public void onFailure(ReqError reqError) {
                            super.onFailure(reqError);
                        }
                    }).fireFreely();
        } else {
            mTradeLayout.updateNoLoginUserAccount();
        }
    }

    private void updateIntroduce(CoinIntroduce data) {
        if (mIntroduceView != null) {
            mIntroduceView.updateData(data);
        }
    }

    private void updateTradeButtons() {
        if (mPairDesc.getPairs().getStatus() != CurrencyPair.STATUS_START) {
            mTradePause.setVisibility(View.VISIBLE);
            mTradeButtons.setVisibility(View.GONE);
        } else {
            mTradePause.setVisibility(View.GONE);
            mTradeButtons.setVisibility(View.VISIBLE);
        }
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
        mTradeVolumeView.setVolumeScale(mPairDesc.getPrefixSymbol().getBalancePoint());
        mTradeDealView.setPriceScale(mPairDesc.getPairs().getPricePoint());
        mTradeDealView.setVolumeScale(mPairDesc.getPrefixSymbol().getBalancePoint());
        mKlineDataDetailView.setPriceScale(mPairDesc.getPairs().getPricePoint());
    }

    private void updateMarketDataView(MarketData marketData) {
        if (marketData == null) return;
        if (mPairDesc != null) {
            int scale = mPairDesc.getPairs().getPricePoint();
            mLastPrice.setText(CurrencyUtils.getPrice(marketData.getLastPrice(), scale));
            mHighestPrice.setText(CurrencyUtils.getPrice(marketData.getHighestPrice(), scale));
            mLowestPrice.setText(CurrencyUtils.getPrice(marketData.getLowestPrice(), scale));
        }
        mDeepView.setLastPrice(marketData.getLastPrice());
        mPriceChange.setText(CurrencyUtils.getPrefixPercent(marketData.getUpDropSpeed()));
        if (marketData.getUpDropSpeed() >= 0) {
            mLastPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
            mPriceChange.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
        } else {
            mLastPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            mPriceChange.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
        }
        mTradeVolume.setText(CurrencyUtils.get24HourVolume(marketData.getVolume()));

        mTradeLayout.updatePriceView(marketData);
    }

    @Override
    public void onTimeUp(int count) {
        if (mChartRadio.getSelectedPosition() == 0) {
            requestTrendData();
        } else {
            requestKlineData(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMarketSubscriber.resume();
        mMarketSubscriber.subscribe(mCurrencyPair.getPairs());
        if (getUserVisibleHint() && isAdded()) {
            requestPairDescription();
        }
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
                            mKlineDataDetailView.setDateFormatStr(KlineUtils.getHeaderDateFormat(mChartRadio.getSelectedPosition()));

                            startScheduleJobNext(KlineUtils.getRefreshInterval(mChartRadio.getSelectedPosition()));
                        } else {
                            if (data.isEmpty()) ToastUtil.show(R.string.no_more_data);
                            mTrend.addHistoryData(data);
                        }
                    }
                }).fire();
    }

    private void requestKlineData(String endTime) {
        requestKlineData(endTime, false);
    }

    private void requestKlineData(boolean flush) {
        requestKlineData(null, flush);
    }

    private void requestKlineData(final String endTime, final boolean flush) {
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
                            mKlineDataDetailView.setDateFormatStr(KlineUtils.getHeaderDateFormat(mChartRadio.getSelectedPosition()));
                            if (flush) mKline.flush();

                            int refreshInterval = KlineUtils.getRefreshInterval(mChartRadio.getSelectedPosition());
                            if (refreshInterval > 0) {
                                startScheduleJobNext(refreshInterval);
                            } else {
                                stopScheduleJob();
                            }
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
        klineCfg.setViewScale(1);

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
        mKline.setOnViewScaleChangedListener(new BaseChart.OnViewScaleChangedListener() {
            @Override
            public void onViewScaleChanged(float scale) {
                mTrend.getChartCfg().setViewScale(scale);
            }
        });

        ChartCfg trendCfg = mTrend.getChartCfg();
        trendCfg.setChartCfg(klineCfg);
        ChartColor trendColor = mTrend.getChartColor();
        trendColor.setChartColor(klineColor);
        trendColor.setClosePriceColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        mTrend.setDateFormatStr("HH:mm");
        mTrend.setOnCrossLineAppearListener(new Kline.OnCrossLineAppearListener() {
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
        mTrend.setOnViewScaleChangedListener(new BaseChart.OnViewScaleChangedListener() {
            @Override
            public void onViewScaleChanged(float scale) {
                mKline.getChartCfg().setViewScale(scale);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}