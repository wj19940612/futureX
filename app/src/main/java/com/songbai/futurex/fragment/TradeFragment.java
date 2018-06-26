package com.songbai.futurex.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.PairDesc;
import com.songbai.futurex.utils.NumUtils;
import com.songbai.futurex.view.ChangePriceView;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.TradeVolumeView;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.market.MarketSubscriber;
import com.songbai.futurex.websocket.model.PairMarket;
import com.songbai.futurex.websocket.model.TradeDir;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/5/30
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class TradeFragment extends BaseFragment {

    private static final int LIMIT_TRADE = 1;
    private static final int MARKET_TRADE = 2;

    @BindView(R.id.optionalStatus)
    ImageView mOptionalStatus;
    @BindView(R.id.pairName)
    TextView mPairName;
    @BindView(R.id.pairArrow)
    ImageView mPairArrow;
    @BindView(R.id.switchToMarketPage)
    ImageView mSwitchToMarketPage;
    @BindView(R.id.tradeDirRadio)
    RadioHeader mTradeDirRadio;
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
    @BindView(R.id.availableCoins)
    TextView mAvailableCoins;
    @BindView(R.id.obtainableCoins)
    TextView mObtainableCoins;
    @BindView(R.id.tradeButton)
    TextView mTradeButton;
    @BindView(R.id.marketPriceView)
    TextView mMarketPriceView;

    Unbinder unbinder;

    private CurrencyPair mCurrencyPair;
    private String mTradePair;
    private PairDesc mPairDesc;
    private int mTradeDir;
    private int mTradeTypeValue;

    private MarketSubscriber mMarketSubscriber;

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
        mTradeTypeValue = LIMIT_TRADE;
        mTradeDirRadio.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                if (position == 0) { // buy in
                    mTradeDir = TradeDir.DIR_BUY_IN;
                    updateTradeDirectionView();
                } else {
                    mTradeDir = TradeDir.DIR_SELL_OUT;
                    updateTradeDirectionView();
                }
            }
        });

        mMarketSubscriber = new MarketSubscriber(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code) {
                new DataParser<Response<PairMarket>>(data) {
                    @Override
                    public void onSuccess(Response<PairMarket> pairMarketResponse) {
                        updateMarketView(pairMarketResponse.getContent());
                    }
                }.parse();
            }
        });
    }

    private void updateMarketView(PairMarket pairMarket) {
        mTradeVolumeView.setDeepList(pairMarket.getDeep().getBuyDeep(),
                pairMarket.getDeep().getSellDeep());
        if (mPairDesc != null) {
            mLastPrice.setText(NumUtils.getPrice(pairMarket.getQuota().getLastPrice(),
                    mPairDesc.getPairs().getPricePoint()));
            mPriceChange.setText(NumUtils.getPrefixPercent(pairMarket.getQuota().getUpDropSpeed()));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            subscribeMarket();
            requestPairDescription();
            updateTradeDirectionView();
        } else {
            unsubscribeMarket();
        }
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
                        if (mCurrencyPair == null) {
                            mCurrencyPair = mPairDesc.getPairs().getCurrencyPair();
                        }
                        updateOptionalStatus();
                        initViews();
//                        initMarketViews();
                    }
                }).fireFreely();
    }

    private void initViews() {
        mDecimalScale.setText(getString(R.string.x_scale_decimal, mPairDesc.getPairs().getPricePoint()));
        mTradeVolumeView.setPriceScale(mPairDesc.getPairs().getPricePoint());
        mTradeVolumeView.setCurrencyPair(mCurrencyPair);

    }

    private void updateOptionalStatus() {
        if (mCurrencyPair.getOption() == CurrencyPair.OPTIONAL_ADDED) {
            mOptionalStatus.setSelected(true);
        } else {
            mOptionalStatus.setSelected(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMarketSubscriber.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMarketSubscriber.pause();
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

    @OnClick({R.id.optionalStatus, R.id.pairName, R.id.pairArrow, R.id.switchToMarketPage,
            R.id.decimalScale, R.id.tradeType, R.id.recharge, R.id.tradeButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.optionalStatus:
                break;
            case R.id.pairName:
                break;
            case R.id.pairArrow:
                break;
            case R.id.switchToMarketPage:
                break;
            case R.id.decimalScale:
                break;
            case R.id.tradeType:
                break;
            case R.id.recharge:
                break;
            case R.id.tradeButton:
                break;
        }
    }
}
