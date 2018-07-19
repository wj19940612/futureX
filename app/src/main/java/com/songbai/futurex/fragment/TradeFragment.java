package com.songbai.futurex.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.activity.mine.MyPropertyActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.PairDesc;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.local.MakeOrder;
import com.songbai.futurex.model.local.SysTime;
import com.songbai.futurex.model.mine.CoinAbleAmount;
import com.songbai.futurex.model.order.Order;
import com.songbai.futurex.model.order.OrderStatus;
import com.songbai.futurex.swipeload.BaseSwipeLoadFragment;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.adapter.SimpleRVAdapter;
import com.songbai.futurex.view.ChangePriceView;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.TradeVolumeView;
import com.songbai.futurex.view.VolumeInputView;
import com.songbai.futurex.view.dialog.ItemSelectController;
import com.songbai.futurex.view.popup.CurrencyPairsPopup;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.PushDestUtils;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.market.MarketSubscriber;
import com.songbai.futurex.websocket.model.MarketData;
import com.songbai.futurex.websocket.model.PairMarket;
import com.songbai.futurex.websocket.model.TradeDir;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.songbai.futurex.model.order.Order.LIMIT_TRADE;
import static com.songbai.futurex.model.order.Order.MARKET_TRADE;

/**
 * Modified by john on 2018/5/30
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class TradeFragment extends BaseSwipeLoadFragment<NestedScrollView> {

    private static final int REQ_CODE_SEARCH = 99;

    @BindView(R.id.tradeDirRadio)
    RadioHeader mTradeDirRadio;
    @BindView(R.id.orderListFloatRadio)
    RadioHeader mOrderListFloatRadio;
    @BindView(R.id.decimalScale)
    TextView mDecimalScale;
    @BindView(R.id.tradeVolumeView)
    TradeVolumeView mTradeVolumeView;
    @BindView(R.id.tradeDirSplitLine)
    View mTradeDirSplitLine;
    @BindView(R.id.tradeType)
    TextView mTradeType;
    @BindView(R.id.changePriceView)
    ChangePriceView mChangePriceView;
    @BindView(R.id.recharge)
    TextView mRecharge;
    @BindView(R.id.tradeAmount)
    TextView mTradeAmount;
    @BindView(R.id.lastPrice)
    TextView mLastPrice;
    @BindView(R.id.priceChange)
    TextView mPriceChange;
    @BindView(R.id.availableCurrency)
    TextView mAvailableCurrency;
    @BindView(R.id.obtainableCurrency)
    TextView mObtainableCurrency;
    @BindView(R.id.tradeButton)
    TextView mTradeButton;
    @BindView(R.id.marketPriceView)
    TextView mMarketPriceView;
    @BindView(R.id.tradeCurrencyRange)
    TextView mTradeCurrencyRange;
    @BindView(R.id.orderListRadio)
    RadioHeader mOrderListRadio;
    @BindView(R.id.volumeInput)
    VolumeInputView mVolumeInput;
    @BindView(R.id.tradeVolumeSeekBar)
    SeekBar mTradeVolumeSeekBar;

    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @BindView(R.id.swipe_target)
    NestedScrollView mSwipeTarget;

    @BindView(R.id.orderList)
    EmptyRecyclerView mOrderList;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.dimView)
    View mDimView;

    Unbinder unbinder;

    ImageView mOptionalStatus;
    TextView mPairName;
    ImageView mPairArrow;
    ImageView mSwitchToMarketPage;

    private CurrencyPair mCurrencyPair;
    private String mTradePair;
    private PairDesc mPairDesc;
    private int mTradeDir;
    private int mTradeTypeValue;

    private MarketSubscriber mMarketSubscriber;
    private OnOptionalClickListener mOnOptionalClickListener;
    private OptionsPickerView mPickerView;
    private List<CoinAbleAmount> mAvailableCurrencyList;
    private double mTradeCurrencyVolume;

    private int mPage;
    private OrderAdapter mOrderAdapter;
    private CurrencyPairsPopup mPairsPopup;

    @Override
    public void onLoadMore() {
        mPage++;
        requestOrderList();
    }

    @Override
    public void onRefresh() {
    }

    @NonNull
    @Override
    public NestedScrollView getSwipeTargetView() {
        return mSwipeTarget;
    }

    @NonNull
    @Override
    public SwipeToLoadLayout getSwipeToLoadLayout() {
        return mSwipeToLoadLayout;
    }

    @NonNull
    @Override
    public RefreshHeaderView getRefreshHeaderView() {
        return mSwipeRefreshHeader;
    }

    @NonNull
    @Override
    public LoadMoreFooterView getLoadMoreFooterView() {
        return mSwipeLoadMoreFooter;
    }

    public interface OnOptionalClickListener {
        void onOptionalClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOptionalClickListener) {
            mOnOptionalClickListener = (OnOptionalClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trade, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addTopPaddingWithStatusBar(mTitleBar);

        initTitleBar();

        mTradeTypeValue = LIMIT_TRADE;
        mTradeDirRadio.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                if (position == 0) { // buy in
                    mTradeDir = TradeDir.DIR_BUY_IN;
                    updateTradeDirectionView();
                    updateTradeCurrencyView();
                    mVolumeInput.reset();
                } else {
                    mTradeDir = TradeDir.DIR_SELL_OUT;
                    updateTradeDirectionView();
                    updateTradeCurrencyView();
                    mVolumeInput.reset();
                }
            }
        });

        mTradeVolumeView.setOnItemClickListener(new TradeVolumeView.OnItemClickListener() {
            @Override
            public void onItemClick(double price, double volume) {
                if (mChangePriceView.getVisibility() == View.GONE) return;
                mChangePriceView.setPrice(price);
                mChangePriceView.startScaleAnim();
                mChangePriceView.setModifiedManually(true);
            }
        });

        mChangePriceView.setOnPriceChangeListener(new ChangePriceView.OnPriceChangeListener() {
            @Override
            public void onPriceChange(double price) {
                updateTradeCurrencyView();
                updateTradeAmount();
                updateVolumeSeekBar();
            }
        });

        mVolumeInput.setOnVolumeChangeListener(new VolumeInputView.OnVolumeChangeListener() {
            @Override
            public void onVolumeChange(double volume) {
                updateTradeAmount();
                updateVolumeSeekBar();

                if (LocalUser.getUser().isLogin() && volume > mTradeCurrencyVolume) {
                    ToastUtil.show(R.string.no_enough_balance);
                }
            }
        });

        mTradeVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateVolumeInputView();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mOrderListRadio.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                if (position == 0) {
                    mOrderAdapter.setOrderType(Order.TYPE_CUR_ENTRUST);
                } else {
                    mOrderAdapter.setOrderType(Order.TYPE_HIS_ENTRUST);
                }
                mOrderListFloatRadio.selectTab(position);
                mPage = 0;
                requestOrderList();
            }
        });
        mOrderListFloatRadio.setOnTabClickListener(new RadioHeader.OnTabClickListener() {
            @Override
            public void onTabClick(int position, String content) {
                mOrderListRadio.selectTab(position);
            }
        });

        mOrderList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollVertically(1)) {
                        triggerLoadMore();
                    }
                }
            }
        });

        mOrderList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOrderAdapter = new OrderAdapter(new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                if (obj instanceof Order && ((Order) obj).hasDealDetail()) {
                    UniqueActivity.launcher(getActivity(), DealDetailFragment.class)
                            .putExtra(ExtraKeys.ORDER, (Parcelable) obj)
                            .execute();
                }
            }
        });
        mOrderAdapter.setOnOrderRevokeClickListener(new OrderAdapter.OnOrderRevokeClickListener() {
            @Override
            public void onOrderRevoke(Order order) {
                requestRevokeOrder(order);
            }
        });
        mOrderList.setAdapter(mOrderAdapter);
        mOrderList.setEmptyView(mEmptyView);

        mSwipeTarget.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int tradeDirRadioHeight = mTradeDirRadio.getHeight();
                if (scrollY >= mOrderListRadio.getTop() - tradeDirRadioHeight) {
                    int diff = scrollY - (mOrderListRadio.getTop() - tradeDirRadioHeight);
                    if (diff <= tradeDirRadioHeight) {
                        mTradeDirRadio.setTranslationY(-diff);
                    }
                } else if (mTradeDirRadio.getTranslationY() != 0) {
                    mTradeDirRadio.setTranslationY(0);
                }

                if (scrollY >= mOrderListRadio.getTop()) {
                    mOrderListFloatRadio.setVisibility(View.VISIBLE);
                } else {
                    mOrderListFloatRadio.setVisibility(View.INVISIBLE);
                }
            }
        });

        mMarketSubscriber = new MarketSubscriber(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code, String dest) {
                if (PushDestUtils.isSoloMarket(dest)) {
                    new DataParser<Response<PairMarket>>(data) {
                        @Override
                        public void onSuccess(Response<PairMarket> pairMarketResponse) {
                            PairMarket pairMarket = pairMarketResponse.getContent();
                            if (pairMarket != null && pairMarket.isVaild()) {
                                updateMarketView(pairMarketResponse.getContent());
                            }
                        }
                    }.parse();
                }
                if (PushDestUtils.isAllMarket(dest)) {
                    new DataParser<Response<Map<String, MarketData>>>(data) {
                        @Override
                        public void onSuccess(Response<Map<String, MarketData>> mapResponse) {
                            if (mPairsPopup == null) return;
                            mPairsPopup.setMarketDataList(mapResponse.getContent());
                        }
                    }.parse();
                }
                if (PushDestUtils.isEntrustOrder(dest)) {
                    requestOrderList();
                }
            }
        });
    }

    private void initTitleBar() {
        View customView = mTitleBar.getCustomView();
        mOptionalStatus = customView.findViewById(R.id.optionalStatus);
        mPairName = customView.findViewById(R.id.pairName);
        mPairArrow = customView.findViewById(R.id.pairArrow);
        mSwitchToMarketPage = customView.findViewById(R.id.switchToMarketPage);

        mOptionalStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalUser.getUser().isLogin()) {
                    toggleOptionalStatus();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });

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
        mSwitchToMarketPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrencyPair != null) {
                    openMarketDetailPage(mCurrencyPair);
                }
            }
        });
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
        mPairsPopup.show(mOrderListFloatRadio);
        mPairsPopup.selectCounterCurrency(mCurrencyPair.getSuffixSymbol());
    }

    private void openSearchPage() {
        UniqueActivity.launcher(getActivity(), SearchCurrencyFragment.class)
                .putExtra(ExtraKeys.SERACH_FOR_TRADE, true)
                .execute(this, REQ_CODE_SEARCH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SEARCH && resultCode == Activity.RESULT_OK) {
            mPairsPopup.dismiss();
            CurrencyPair pair = data.getParcelableExtra(ExtraKeys.CURRENCY_PAIR);
            switchNewCurrencyPair(pair);
        }
    }

    private void switchNewCurrencyPair(CurrencyPair currencyPair) {
        mCurrencyPair = currencyPair;

        mPairsPopup.setCurCurrencyPair(mCurrencyPair);
        unsubscribeMarket();
        resetView();

        subscribeMarket();
        requestPairDescription();
        updateTradeDirectionView();
    }

    private void requestRevokeOrder(Order order) {
        Apic.revokeOrder(order.getId()).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        requestOrderList();
                    }
                }).fire();
    }

    private void updateVolumeSeekBar() {
        int max = mTradeVolumeSeekBar.getMax();
        int progress = (int) (mVolumeInput.getVolume() / mTradeCurrencyVolume * max);
        mTradeVolumeSeekBar.setProgress(progress);
    }

    private void updateVolumeInputView() {
        int progress = mTradeVolumeSeekBar.getProgress();
        int max = mTradeVolumeSeekBar.getMax();
        double tradeVolume = mTradeCurrencyVolume * progress / max;
        mVolumeInput.setVolume(tradeVolume);
    }

    private void updateTradeAmount() {
        if (mPairDesc != null && mCurrencyPair != null) {
            double price = mChangePriceView.getPrice();
            double volume = mVolumeInput.getVolume();
            int scale = mPairDesc.getSuffixSymbol().getBalancePoint();
            String unit = mCurrencyPair.getSuffixSymbol();
            double amt = price * volume;
            String tradeAmt = "0";
            if (amt != 0) {
                tradeAmt = CurrencyUtils.getPrice(amt, scale);
            }
            mTradeAmount.setText(tradeAmt + " " + unit.toUpperCase());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            subscribeMarket();
            requestPairDescription();
            updateTradeDirectionView();
            mVolumeInput.reset();
        } else {
            unsubscribeMarket();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMarketSubscriber.resume();

        if (getUserVisibleHint()) {
            subscribeMarket();
            requestPairDescription();
            updateTradeDirectionView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMarketSubscriber.pause();

        if (getUserVisibleHint()) {
            unsubscribeMarket();
        }
    }

    private void resetView() {
        mTradeVolumeView.reset();
        mChangePriceView.reset();
        mVolumeInput.reset();
        mPage = 0;
        mOrderAdapter.setOrderList(new ArrayList<Order>());
        mLastPrice.setText("--.--");
        mPriceChange.setText("--.--");
        mTradeVolumeSeekBar.setProgress(0);
        mTradeCurrencyRange.setText("");
    }

    private void updateMarketView(PairMarket pairMarket) {
        mTradeVolumeView.setDeepList(pairMarket.getDeep().getBuyDeep(), pairMarket.getDeep().getSellDeep());
        MarketData quota = pairMarket.getQuota();
        if (mPairDesc != null && quota != null) {
//            mLastPrice.setText(CurrencyUtils.getPrice(quota.getLastPrice(), 8));
            mLastPrice.setText(CurrencyUtils.getPrice(quota.getLastPrice(), mPairDesc.getPairs().getPricePoint()));
            mPriceChange.setText(CurrencyUtils.getPrefixPercent(quota.getUpDropSpeed()));
            if (quota.getUpDropSpeed() >= 0) {
                mLastPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                mPriceChange.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
            } else {
                mLastPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                mPriceChange.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            }
            if (!mChangePriceView.isModifiedManually()) {
                mChangePriceView.setPrice(quota.getLastPrice());
            }
        }
    }

    private void requestUserAccount() {
        if (LocalUser.getUser().isLogin()) {
            Apic.getAccountByUserForMuti(mCurrencyPair.getPrefixSymbol() + "," + mCurrencyPair.getSuffixSymbol())
                    .callback(new Callback<Resp<List<CoinAbleAmount>>>() {
                        @Override
                        protected void onRespSuccess(Resp<List<CoinAbleAmount>> resp) {
                            mAvailableCurrencyList = resp.getData();
                            if (mAvailableCurrencyList.size() > 0) {
                                updateTradeCurrencyView();
                            }
                        }
                    }).fireFreely();
        } else {
            updateTradeCurrencyView();
        }
    }

    private void updateTradeCurrencyView() {
        if (mCurrencyPair == null) return;

        if (LocalUser.getUser().isLogin() && mAvailableCurrencyList != null) {
            // 默认买入
            String availableCurrencySign = mCurrencyPair.getSuffixSymbol();
            String obtainableCurrencySign = mCurrencyPair.getPrefixSymbol();
            String tradeCurrencySign = obtainableCurrencySign;
            int availableCurrencyScale = mPairDesc.getSuffixSymbol().getBalancePoint();
            int obtainableCurrencyScale = mPairDesc.getPrefixSymbol().getBalancePoint();
            int tradeCurrencyScale = obtainableCurrencyScale;

            if (mTradeDir == TradeDir.DIR_SELL_OUT) {
                availableCurrencySign = mCurrencyPair.getPrefixSymbol();
                obtainableCurrencySign = mCurrencyPair.getSuffixSymbol();
                tradeCurrencySign = availableCurrencySign;
                availableCurrencyScale = mPairDesc.getPrefixSymbol().getBalancePoint();
                obtainableCurrencyScale = mPairDesc.getSuffixSymbol().getBalancePoint();
                tradeCurrencyScale = availableCurrencyScale;
            }

            double availableCurrencyVolume = getAvailableCurrencyAmt(mAvailableCurrencyList, availableCurrencySign);
            double obtainableCurrencyVolume = getObtainableCurrencyAmt(availableCurrencyVolume);
            mTradeCurrencyVolume = mTradeDir == TradeDir.DIR_BUY_IN ? obtainableCurrencyVolume : availableCurrencyVolume;

            mAvailableCurrency.setText(getString(R.string.available_currency_x_x,
                    CurrencyUtils.getPrice(availableCurrencyVolume, availableCurrencyScale), availableCurrencySign.toUpperCase()));
            mObtainableCurrency.setText(getString(R.string.obtainable_currency_x_x,
                    CurrencyUtils.getPrice(obtainableCurrencyVolume, obtainableCurrencyScale), obtainableCurrencySign.toUpperCase()));
            mTradeCurrencyRange.setText(getString(R.string.trade_currency_range_0_to_x_x,
                    CurrencyUtils.getPrice(mTradeCurrencyVolume, tradeCurrencyScale), tradeCurrencySign.toUpperCase()));
        } else {
            String availableCurrencySign = mCurrencyPair.getSuffixSymbol();
            String obtainableCurrencySign = mCurrencyPair.getPrefixSymbol();

            if (mTradeDir == TradeDir.DIR_SELL_OUT) {
                availableCurrencySign = mCurrencyPair.getPrefixSymbol();
                obtainableCurrencySign = mCurrencyPair.getSuffixSymbol();
            }

            mAvailableCurrency.setText(getString(R.string.available_currency_x_x,
                    "--", availableCurrencySign.toUpperCase()));
            mObtainableCurrency.setText(getString(R.string.obtainable_currency_x_x,
                    "--", obtainableCurrencySign.toUpperCase()));
            mTradeCurrencyRange.setText(getString(R.string.trade_currency_range_0_to_x_x,
                    "0", obtainableCurrencySign.toUpperCase()));
        }
    }

    private double getAvailableCurrencyAmt(List<CoinAbleAmount> availableCurrencyList, String availableCurrencySign) {
        for (CoinAbleAmount amount : availableCurrencyList) {
            if (amount.getCoinType().equalsIgnoreCase(availableCurrencySign)) {
                return amount.getAbleCoin();
            }
        }
        return 0;
    }

    private double getObtainableCurrencyAmt(double availableCounterCurrency) {
        double price = getTradePrice();

        if (price == 0) return 0;

        if (mTradeDir == TradeDir.DIR_BUY_IN) {
            return availableCounterCurrency / price;
        } else {
            return availableCounterCurrency * price;
        }
    }

    private double getTradePrice() {
        double price = mChangePriceView.getPrice();
        if (mTradeTypeValue == MARKET_TRADE) {
            if (mTradeDir == TradeDir.DIR_BUY_IN) {
                price = mTradeVolumeView.getAskPrice1();
            } else {
                price = mTradeVolumeView.getBidPrice1();
            }
        }
        return price;
    }

    private void updateTradeDirectionView() {
        if (mTradeDir == TradeDir.DIR_BUY_IN) {
            int tradeTypeRes;
            if (mTradeTypeValue == LIMIT_TRADE) {
                tradeTypeRes = R.string.buy_limit;
                mChangePriceView.setVisibility(View.VISIBLE);
                mMarketPriceView.setVisibility(View.GONE);
            } else {
                tradeTypeRes = R.string.market_price;
                mChangePriceView.setVisibility(View.GONE);
                mMarketPriceView.setVisibility(View.VISIBLE);
            }
            mTradeType.setText(tradeTypeRes);
            mTradeButton.setText(R.string.buy_in);
            mTradeButton.setBackgroundResource(R.drawable.btn_green_r18);
            mTradeDirSplitLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
        } else {
            int tradeTypeRes;
            if (mTradeTypeValue == LIMIT_TRADE) {
                tradeTypeRes = R.string.sell_limit;
                mChangePriceView.setVisibility(View.VISIBLE);
                mMarketPriceView.setVisibility(View.GONE);
            } else {
                tradeTypeRes = R.string.market_price;
                mChangePriceView.setVisibility(View.GONE);
                mMarketPriceView.setVisibility(View.VISIBLE);
            }
            mTradeType.setText(tradeTypeRes);
            mTradeButton.setText(R.string.sell_out);
            mTradeButton.setBackgroundResource(R.drawable.btn_red_r18);
            mTradeDirSplitLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
        }
    }

    private void unsubscribeMarket() {
        if (mMarketSubscriber != null) {
            mMarketSubscriber.unSubscribe(mTradePair);
        }
    }

    private void subscribeMarket() {
        if (mCurrencyPair != null) {
            mTradePair = mCurrencyPair.getPairs();
            mMarketSubscriber.subscribe(mTradePair);
            Preference.get().setDefaultTradePair(mTradePair);
        } else {
            mTradeDir = TradeDir.DIR_BUY_IN;
            String tradePair = Preference.get().getDefaultTradePair();
            if (!TextUtils.isEmpty(tradePair)) {
                mTradePair = tradePair;
                mMarketSubscriber.subscribe(mTradePair);
            }
        }
    }

    private void requestPairDescription() {
        Apic.getPairDescription(mTradePair).tag(TAG)
                .callback(new Callback4Resp<Resp<PairDesc>, PairDesc>() {
                    @Override
                    protected void onRespData(PairDesc data) {
                        mPairDesc = data;
                        mCurrencyPair = mPairDesc.getPairs().getCurrencyPair();
                        updateOptionalStatus();
                        initViews();

                        requestUserAccount();
                        requestOrderList();
                    }
                }).fireFreely();
    }

    private void initViews() {
        mPairName.setText(mCurrencyPair.getPrefixSymbol().toUpperCase() +
                "/" + mCurrencyPair.getSuffixSymbol().toUpperCase());
        mDecimalScale.setText(getString(R.string.x_scale_decimal, String.valueOf(mPairDesc.getPairs().getPricePoint())));
        mTradeVolumeView.setPriceScale(mPairDesc.getPairs().getPricePoint());
        mTradeVolumeView.setMergeScale(mPairDesc.getPairs().getPricePoint());
        mTradeVolumeView.setVolumeScale(mPairDesc.getPrefixSymbol().getBalancePoint());
        mChangePriceView.setPriceScale(mPairDesc.getPairs().getPricePoint());
        mTradeVolumeView.setCurrencyPair(mCurrencyPair);
        mOrderAdapter.setScale(mPairDesc.getPrefixSymbol().getBalancePoint(),
                mPairDesc.getSuffixSymbol().getBalancePoint());
        mOrderAdapter.setOrderType(mOrderListRadio.getSelectedPosition() == 0
                ? Order.TYPE_CUR_ENTRUST : Order.TYPE_HIS_ENTRUST);
    }

    private void updateOptionalStatus() {
        if (mCurrencyPair.getOption() == CurrencyPair.OPTIONAL_ADDED) {
            mOptionalStatus.setSelected(true);
        } else {
            mOptionalStatus.setSelected(false);
        }
    }

    public void setTradeDir(int tradeDir) {
        mTradeDir = tradeDir;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        mCurrencyPair = currencyPair;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void toggleOptionalStatus() {
        if (mCurrencyPair == null) return;

        if (mOptionalStatus.isSelected()) { // 已经添加自选
            Apic.cancelOptional(mCurrencyPair.getPairs()).tag(TAG)
                    .callback(new Callback<Resp>() {
                        @Override
                        protected void onRespSuccess(Resp resp) {
                            mOptionalStatus.setSelected(false);
                            ToastUtil.show(R.string.optional_cancel);
                            onOptionalStatusClick();
                        }
                    }).fire();
        } else {
            Apic.addOptional(mCurrencyPair.getPairs()).tag(TAG)
                    .callback(new Callback<Resp>() {
                        @Override
                        protected void onRespSuccess(Resp resp) {
                            mOptionalStatus.setSelected(true);
                            ToastUtil.show(R.string.optional_added);
                            onOptionalStatusClick();
                        }
                    }).fire();
        }
    }

    private void onOptionalStatusClick() {
        if (mOnOptionalClickListener != null) {
            mOnOptionalClickListener.onOptionalClick();
        }
    }

    private void openMarketDetailPage(CurrencyPair currencyPair) {
        UniqueActivity.launcher(this, MarketDetailFragment.class)
                .putExtra(ExtraKeys.CURRENCY_PAIR, currencyPair)
                .execute();
    }

    @OnClick({R.id.decimalScale, R.id.tradeType, R.id.recharge, R.id.tradeButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.decimalScale:
                showDecimalScaleSelector();
                break;
            case R.id.tradeType:
                showTradeTypeSelector();
                break;
            case R.id.recharge:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), MyPropertyActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.tradeButton:
                if (LocalUser.getUser().isLogin()) {
                    requestMakeOrder();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
        }
    }

    private void requestMakeOrder() {
        if (mCurrencyPair == null) return;
        MakeOrder makeOrder = new MakeOrder();
        int direction = mTradeDir == TradeDir.DIR_BUY_IN ? Order.DIR_BUY : Order.DIR_SELL;
        makeOrder.setDirection(direction);
        makeOrder.setEntrustCount(mVolumeInput.getVolume());
        makeOrder.setPairs(mCurrencyPair.getPairs());
        makeOrder.setEntrustType(mTradeTypeValue);
        makeOrder.setEntrustPrice(getTradePrice());

        Apic.makeOrder(makeOrder).tag(TAG)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        mPage = 0;
                        requestOrderList();
                        ToastUtil.show(R.string.entrust_success);
                    }
                }).fire();
    }

    private void requestOrderList() {
        if (!LocalUser.getUser().isLogin()) return;
        if (mCurrencyPair == null) return;
        int type = mOrderListRadio.getSelectedPosition() == 0 ? Order.TYPE_CUR_ENTRUST : Order.TYPE_HIS_ENTRUST;
        String endTime = Uri.encode(DateUtil.format(SysTime.getSysTime().getSystemTimestamp()));
        Apic.getEntrustOrderList(mPage, type, endTime,
                mCurrencyPair.getPrefixSymbol(), mCurrencyPair.getSuffixSymbol())
                .id(mOrderListRadio.getSelectTab())
                .tag(TAG)
                .callback(new Callback4Resp<Resp<PagingWrap<Order>>, PagingWrap<Order>>() {
                    @Override
                    protected void onRespData(PagingWrap<Order> data) {
                        if (getId().equals(mOrderListRadio.getSelectTab())) {
                            if (mPage == 0) {
                                mOrderAdapter.setOrderList(data.getData());
                            } else {
                                mOrderAdapter.appendOrderList(data.getData());
                            }
                            stopLoadMoreAnimation();

                            if (data.getData().size() < Apic.DEFAULT_PAGE_SIZE) {
                                mSwipeToLoadLayout.setLoadMoreEnabled(false);
                            } else {
                                mSwipeToLoadLayout.setLoadMoreEnabled(true);
                            }
                        }
                    }
                }).fireFreely();
    }

    private void showTradeTypeSelector() {
        final SmartDialog dialog = SmartDialog.solo(getActivity());

        SimpleRVAdapter simpleRVAdapter = new SimpleRVAdapter(
                new int[]{R.string.limit_price, R.string.market_price},
                R.layout.row_select_text,
                new OnRVItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, Object obj) {
                        dialog.dismiss();
                        if (position == 0) {
                            mTradeTypeValue = LIMIT_TRADE;
                        } else {
                            mTradeTypeValue = MARKET_TRADE;
                        }
                        updateTradeDirectionView();
                    }
                });

        ItemSelectController itemSelectController = new ItemSelectController(getContext());
        itemSelectController.setHintText(getString(R.string.please_choose_limited_condition));
        itemSelectController.setAdapter(simpleRVAdapter);

        dialog.setCustomViewController(itemSelectController)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setWidthScale(1)
                .show();
    }

    private void showDecimalScaleSelector() {
        if (mPairDesc == null || mPairDesc.getPairs() == null ||
                TextUtils.isEmpty(mPairDesc.getPairs().getDeep())) {
            return;
        }

        final String[] deeps = mPairDesc.getPairs().getDeep().split(",");
        final List<String> deepList = new ArrayList<>();
        int selectedOption = 0;
        for (int i = 0; i < deeps.length; i++) {
            String deep = deeps[i];
            String deepStr = getString(R.string.x_scale_decimal, deep);
            deepList.add(deepStr);
            if (deepStr.equals(mDecimalScale.getText())) {
                selectedOption = i;
            }
        }
        mPickerView = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                int mergeScale;
                try {
                    mergeScale = Integer.parseInt(deeps[options1]);
                    mTradeVolumeView.setMergeScale(mergeScale);
                    mDecimalScale.setText(deepList.get(options1));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }).setLayoutRes(R.layout.pickerview_custom_view, new CustomListener() {
            @Override
            public void customLayout(View v) {
                TextView cancel = v.findViewById(R.id.cancel);
                TextView confirm = v.findViewById(R.id.confirm);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPickerView.dismiss();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPickerView.returnData();
                        mPickerView.dismiss();
                    }
                });
            }
        }).setCyclic(false, false, false)
                .setTextColorCenter(ContextCompat.getColor(getContext(), R.color.text22))
                .setTextColorOut(ContextCompat.getColor(getContext(), R.color.text99))
                .setDividerColor(ContextCompat.getColor(getContext(), R.color.bgDD))
                .build();
        mPickerView.setPicker(deepList);
        mPickerView.setSelectOptions(selectedOption);
        mPickerView.show();
    }

    static class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        static class HistoryViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.split)
            View mSplit;
            @BindView(R.id.tradePair)
            TextView mTradePair;
            @BindView(R.id.orderStatus)
            TextView mOrderStatus;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.entrustPrice)
            TextView mEntrustPrice;
            @BindView(R.id.entrustVolume)
            TextView mEntrustVolume;
            @BindView(R.id.dealTotalAmt)
            TextView mDealTotalAmt;
            @BindView(R.id.dealAveragePrice)
            TextView mDealAveragePrice;
            @BindView(R.id.dealVolume)
            TextView mDealVolume;
            @BindView(R.id.entrustPriceTitle)
            TextView mEntrustPriceTitle;
            @BindView(R.id.entrustVolumeTitle)
            TextView mEntrustVolumeTitle;
            @BindView(R.id.dealTotalAmtTitle)
            TextView mDealTotalAmtTitle;
            @BindView(R.id.dealAveragePriceTitle)
            TextView mDealAveragePriceTitle;
            @BindView(R.id.dealVolumeTitle)
            TextView mDealVolumeTitle;

            public HistoryViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(Order order, Context context, int prefixScale, int suffixScale, int position) {
                if (position == 0) {
                    mSplit.setVisibility(View.GONE);
                } else {
                    mSplit.setVisibility(View.VISIBLE);
                }
                int color = ContextCompat.getColor(context, R.color.green);
                String tradeDir = context.getString(R.string.buy_in);
                if (order.getDirection() == Order.DIR_SELL) {
                    color = ContextCompat.getColor(context, R.color.red);
                    tradeDir = context.getString(R.string.sell_out);
                }
                if (order.getStatus() == OrderStatus.REVOKED
                        || order.getStatus() == OrderStatus.REVOKING
                        || order.getStatus() == OrderStatus.SYSTEM_REVOKED) {
                    color = ContextCompat.getColor(context, R.color.text49);
                }
                mTradePair.setText(tradeDir + " " + CurrencyUtils.formatPairName(order.getPairs()));
                mTradePair.setTextColor(color);
                mTime.setText(DateUtil.format(order.getOrderTime(), "HH:mm MM/dd"));
                if (order.getEntrustType() == Order.LIMIT_TRADE) {
                    mEntrustPrice.setText(order.getEntrustPrice());
                } else {
                    mEntrustPrice.setText(R.string.market_price);
                }
                mEntrustVolume.setText(order.getEntrustCount());
                mOrderStatus.setText(getStatusTextRes(order.getStatus()));
                mEntrustPriceTitle.setText(context.getString(R.string.entrust_price_x, order.getSuffix().toUpperCase()));
                mEntrustVolumeTitle.setText(context.getString(R.string.entrust_volume_x, order.getPrefix().toUpperCase()));
                mDealTotalAmt.setText(CurrencyUtils.getAmt(order.getDealCountDouble() * order.getDealPriceDouble(), suffixScale));
                mDealAveragePrice.setText(order.getDealPrice());
                mDealVolume.setText(order.getDealCount());
                mDealTotalAmtTitle.setText(context.getString(R.string.deal_total_amt_x, order.getSuffix().toUpperCase()));
                mDealAveragePriceTitle.setText(context.getString(R.string.deal_average_price_x, order.getSuffix().toUpperCase()));
                mDealVolumeTitle.setText(context.getString(R.string.deal_volume_x, order.getPrefix().toUpperCase()));
            }

            private int getStatusTextRes(int status) {
                switch (status) {
                    case OrderStatus.PENDING_DEAL:
                        return R.string.not_deal;
                    case OrderStatus.REVOKING:
                        return R.string.revoking;
                    case OrderStatus.REVOKED:
                        return R.string.revoked;
                    case OrderStatus.ALL_DEAL:
                        return R.string.deal_with_arrow;
                    case OrderStatus.PART_DEAL:
                        return R.string.part_deal_with_arrow;
                    case OrderStatus.SYSTEM_REVOKED:
                        return R.string.system_revoked;
                    case OrderStatus.PART_DEAL_PART_REVOKED:
                        return R.string.part_deal_part_revoked_with_arrow;
                    default:
                        return R.string.unknown_status;
                }
            }
        }

        static class EntrustViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tradePair)
            TextView mTradePair;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.revoke)
            TextView mRevoke;
            @BindView(R.id.entrustPrice)
            TextView mEntrustPrice;
            @BindView(R.id.entrustVolume)
            TextView mEntrustVolume;
            @BindView(R.id.actualDealVolume)
            TextView mActualDealVolume;
            @BindView(R.id.entrustPriceTitle)
            TextView mEntrustPriceTitle;
            @BindView(R.id.entrustVolumeTitle)
            TextView mEntrustVolumeTitle;
            @BindView(R.id.actualDealVolumeTitle)
            TextView mActualDealVolumeTitle;

            public EntrustViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(final Order order, final Context context, final OnOrderRevokeClickListener onOrderRevokeClickListener) {
                int color = ContextCompat.getColor(context, R.color.green);
                String tradeDir = context.getString(R.string.buy_in);
                if (order.getDirection() == Order.DIR_SELL) {
                    color = ContextCompat.getColor(context, R.color.red);
                    tradeDir = context.getString(R.string.sell_out);
                }
                mTradePair.setText(tradeDir + " " + CurrencyUtils.formatPairName(order.getPairs()));
                mTradePair.setTextColor(color);
                mTime.setText(DateUtil.format(order.getOrderTime(), "HH:mm MM/dd"));
                if (order.getEntrustType() == Order.LIMIT_TRADE) {
                    mEntrustPrice.setText(order.getEntrustPrice());
                } else {
                    mEntrustPrice.setText(R.string.market_price);
                }
                mEntrustVolume.setText(order.getEntrustCount());
                mActualDealVolume.setText(order.getDealCount());
                mRevoke.setText(getStatusTextRes(order.getStatus()));
                if (order.getStatus() == OrderStatus.PENDING_DEAL) {
                    mRevoke.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onOrderRevokeClickListener != null) {
                                onOrderRevokeClickListener.onOrderRevoke(order);
                            }
                        }
                    });
                } else {
                    mRevoke.setOnClickListener(null);
                }
                mEntrustPriceTitle.setText(context.getString(R.string.entrust_price_x, order.getSuffix().toUpperCase()));
                mEntrustVolumeTitle.setText(context.getString(R.string.entrust_volume_x, order.getPrefix().toUpperCase()));
                mActualDealVolumeTitle.setText(context.getString(R.string.actual_deal_x, order.getPrefix().toUpperCase()));
            }

            private int getStatusTextRes(int status) {
                switch (status) {
                    case OrderStatus.PENDING_DEAL:
                        return R.string.revoke_order;
                    case OrderStatus.REVOKING:
                        return R.string.revoking;
                    default:
                        return R.string.unknown_status;
                }
            }
        }

        interface OnOrderRevokeClickListener {
            void onOrderRevoke(Order order);
        }

        private List<Order> mOrderList;
        private OnRVItemClickListener mOnRVItemClickListener;
        private Context mContext;
        private int mOrderType;
        private OnOrderRevokeClickListener mOnOrderRevokeClickListener;
        private int mPrefixScale;
        private int mSuffixScale;
        private Set<String> mOrderIdSet;

        public OrderAdapter(OnRVItemClickListener onRVItemClickListener) {
            mOrderList = new ArrayList<>();
            mOnRVItemClickListener = onRVItemClickListener;
            mOrderIdSet = new HashSet<>();
            mOrderType = Order.TYPE_CUR_ENTRUST;
        }

        public void setScale(int prefixScale, int suffixSclae) {
            mPrefixScale = prefixScale;
            mSuffixScale = suffixSclae;
        }

        public void setOrderType(int orderType) {
            mOrderType = orderType;
        }

        public void setOrderList(List<Order> orderList) {
            mOrderList = orderList;
            mOrderIdSet.clear();
            for (Order order : mOrderList) {
                mOrderIdSet.add(order.getId());
            }
            notifyDataSetChanged();
        }

        public void appendOrderList(List<Order> orderList) {
            for (Order order : orderList) {
                if (mOrderIdSet.add(order.getId())) {
                    mOrderList.add(order);
                }
            }
            notifyDataSetChanged();
        }

        public void setOnOrderRevokeClickListener(OnOrderRevokeClickListener onOrderRevokeClickListener) {
            mOnOrderRevokeClickListener = onOrderRevokeClickListener;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            if (viewType == Order.TYPE_CUR_ENTRUST) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_entrust_order, parent, false);
                return new EntrustViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history_order, parent, false);
                return new HistoryViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof EntrustViewHolder) {
                ((EntrustViewHolder) holder).bind(mOrderList.get(position), mContext, mOnOrderRevokeClickListener);
            } else if (holder instanceof HistoryViewHolder) {
                ((HistoryViewHolder) holder).bind(mOrderList.get(position), mContext, mPrefixScale, mSuffixScale, position);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRVItemClickListener.onItemClick(holder.itemView, position, mOrderList.get(position));
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return mOrderType;
        }

        @Override
        public int getItemCount() {
            return mOrderList.size();
        }
    }
}
