package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.PairDesc;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.local.MakeOrder;
import com.songbai.futurex.model.mine.CoinAbleAmount;
import com.songbai.futurex.model.order.Order;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.view.autofit.AutofitTextView;
import com.songbai.futurex.websocket.model.MarketData;
import com.songbai.futurex.websocket.model.TradeDir;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.songbai.futurex.model.order.Order.LIMIT_TRADE;
import static com.songbai.futurex.model.order.Order.MARKET_TRADE;

public class FastTradingView extends LinearLayout {
    @BindView(R.id.tradeType)
    TextView mTradeType;
    @BindView(R.id.marketPriceView)
    TextView mMarketPriceView;
    @BindView(R.id.changePriceView)
    ChangePriceView mChangePriceView;
    @BindView(R.id.volumeInput)
    VolumeInputView mVolumeInput;
    @BindView(R.id.recharge)
    TextView mRecharge;
    @BindView(R.id.tradeAmount)
    TextView mTradeAmount;
    @BindView(R.id.tradeDirRadio)
    BuySellSwitcher mTradeDirRadio;
    @BindView(R.id.availableCurrency)
    AutofitTextView mAvailableCurrency;
    @BindView(R.id.obtainableCurrency)
    AutofitTextView mObtainableCurrency;
    @BindView(R.id.percentSelectView)
    TradePercentSelectView mPercentSelectView;
    @BindView(R.id.tradeButton)
    TextView mTradeButton;

    private int mTradeTypeValue;

    private TradeVolumeView mTradeVolumeView;

    private CurrencyPair mCurrencyPair;
    private int mTradeDir;
    private PairDesc mPairDesc;
    private double mTradeCurrencyVolume;
    private List<CoinAbleAmount> mAvailableCurrencyList;
    private onFastTradeClickListener mOnFastTradeClickListener;

    public interface onFastTradeClickListener{
        public void onMakeOrder(MakeOrder makeOrder);
    }

    public void setonFastTradeClickListener(onFastTradeClickListener onFastTradeClickListener){
        mOnFastTradeClickListener = onFastTradeClickListener;
    }


    public FastTradingView(Context context) {
        this(context, null);
    }

    public FastTradingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastTradingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_fast_trading, this, true);
        ButterKnife.bind(this);

        mTradeDir = TradeDir.DIR_BUY_IN;
        initView();
    }

    private void initView() {
        mTradeDirRadio.setOnTabSelectedListener(new BuySellSwitcher.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
//                Log.e("zzz", "onTabSelected:" + mCurrencyPair == null ? "true" : "false");
                if (mCurrencyPair == null) return;

                if (position == 0) { // buy in
                    mTradeDir = TradeDir.DIR_BUY_IN;
                    updateTradeDirectionView();
                    updateTradeCurrencyView();
                    resetTradeAmount();
                    mPercentSelectView.reset();
                    mVolumeInput.reset();
                    mVolumeInput.setBaseCurrency(mCurrencyPair.getPrefixSymbol().toUpperCase());
                } else {
                    mTradeDir = TradeDir.DIR_SELL_OUT;
                    updateTradeDirectionView();
                    updateTradeCurrencyView();
                    resetTradeAmount();
                    mPercentSelectView.reset();
                    mVolumeInput.reset();
                    mVolumeInput.setBaseCurrency(mCurrencyPair.getPrefixSymbol().toUpperCase());
                }
            }
        });


        mChangePriceView.setOnPriceChangeListener(new ChangePriceView.OnPriceChangeListener() {
            @Override
            public void onPriceChange(double price) {
                updateTradeCurrencyView();
                updateTradeAmount();
                updateSelectPercentView();
//                updateVolumeSeekBar();
            }
        });

        mVolumeInput.setOnVolumeChangeListener(new VolumeInputView.OnVolumeChangeListener() {
            @Override
            public void onVolumeChange(double volume) {
                updateTradeAmount();
//                updateVolumeSeekBar();

                if (LocalUser.getUser().isLogin() && volume > mTradeCurrencyVolume) {
                    ToastUtil.show(R.string.no_enough_balance);
                }
            }

            @Override
            public void onVolumeInputChange(double volume) {
                updateVolumeSeekBar();
            }
        });

        mPercentSelectView.setOnPercentSelectListener(new TradePercentSelectView.OnPercentSelectListener() {
            @Override
            public void onPercentSelect(int percent, int max) {
                updateVolumeInputView(percent, max);
            }
        });
    }

    private void updateVolumeSeekBar() {
        int max = mPercentSelectView.getMax();
        double progress = (mVolumeInput.getVolume() / mTradeCurrencyVolume * max);
        mPercentSelectView.updatePercent(progress);
    }

    private void updateVolumeInputView(int progress, int max) {
        if (progress > 0) {
            mVolumeInput.setVolume(FinanceUtil.subZeroAndDot(mTradeCurrencyVolume * progress / max, 100));
        }
    }

    @OnClick({R.id.recharge, R.id.tradeButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recharge:
                break;
            case R.id.tradeButton:
                if (LocalUser.getUser().isLogin()) {
                    makeOrder();
                } else {
                    Launcher.with(getContext(), LoginActivity.class).execute();
                }
                break;
        }
    }

    public void updateData(CurrencyPair currencyPair, PairDesc pairDesc, TradeVolumeView tradeVolumeView) {
        mCurrencyPair = currencyPair;
        mPairDesc = pairDesc;
        mTradeVolumeView = tradeVolumeView;

        mVolumeInput.setVolumeScale(pairDesc.getPrefixSymbol().getBalancePoint());
        mChangePriceView.setPriceScale(currencyPair.getMaxPoint());

        mTradeTypeValue = LIMIT_TRADE;
        updateTradeDirectionView();

        mVolumeInput.setBaseCurrency(currencyPair.getPrefixSymbol().toUpperCase());
    }

    private void updateTradeDirectionView() {
        if (mTradeDir == TradeDir.DIR_BUY_IN) {
            int tradeTypeRes;
            if (mTradeTypeValue == LIMIT_TRADE) {
                tradeTypeRes = R.string.limit_price;
                mChangePriceView.setVisibility(View.VISIBLE);
                mMarketPriceView.setVisibility(View.GONE);
            } else {
                tradeTypeRes = R.string.market_price;
                mChangePriceView.setVisibility(View.GONE);
                mMarketPriceView.setVisibility(View.VISIBLE);
            }
            mTradeType.setText(tradeTypeRes);
            if (mCurrencyPair.getStatus() != CurrencyPair.STATUS_START) {
                mTradeButton.setEnabled(false);
                mTradeButton.setBackgroundResource(R.drawable.btn_green_r18);
                mTradeButton.setText(R.string.pause_trade);
            } else {
                mTradeButton.setEnabled(true);
                mTradeButton.setBackgroundResource(R.drawable.btn_green_r18);
                mTradeButton.setText(R.string.buy_in);
            }
//            mTradeDirSplitLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
        } else {
            int tradeTypeRes;
            if (mTradeTypeValue == LIMIT_TRADE) {
                tradeTypeRes = R.string.limit_price;
                mChangePriceView.setVisibility(View.VISIBLE);
                mMarketPriceView.setVisibility(View.GONE);
            } else {
                tradeTypeRes = R.string.market_price;
                mChangePriceView.setVisibility(View.GONE);
                mMarketPriceView.setVisibility(View.VISIBLE);
            }
            mTradeType.setText(tradeTypeRes);
            mTradeButton.setText(R.string.sell_out);
            if (mCurrencyPair.getStatus() != CurrencyPair.STATUS_START) {
                mTradeButton.setEnabled(false);
                mTradeButton.setBackgroundResource(R.drawable.btn_red_r18);
                mTradeButton.setText(R.string.pause_trade);
            } else {
                mTradeButton.setEnabled(true);
                mTradeButton.setBackgroundResource(R.drawable.btn_red_r18);
                mTradeButton.setText(R.string.sell_out);
            }
        }
    }

    private void updateTradeCurrencyView() {
        if (mCurrencyPair == null || mPairDesc == null) return;

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

            mAvailableCurrency.setText(getContext().getString(R.string.available_currency_x_x,
                    CurrencyUtils.getPrice(availableCurrencyVolume, availableCurrencyScale), availableCurrencySign.toUpperCase()));
            mObtainableCurrency.setText(getContext().getString(R.string.obtainable_currency_x_x,
                    CurrencyUtils.getPrice(obtainableCurrencyVolume, obtainableCurrencyScale), obtainableCurrencySign.toUpperCase()));
//            mTradeCurrencyRange.setText(getString(R.string.trade_currency_range_0_to_x_x,
//                    CurrencyUtils.getPrice(mTradeCurrencyVolume, tradeCurrencyScale), tradeCurrencySign.toUpperCase()));
        } else {
            String availableCurrencySign = mCurrencyPair.getSuffixSymbol();
            String obtainableCurrencySign = mCurrencyPair.getPrefixSymbol();

            if (mTradeDir == TradeDir.DIR_SELL_OUT) {
                availableCurrencySign = mCurrencyPair.getPrefixSymbol();
                obtainableCurrencySign = mCurrencyPair.getSuffixSymbol();
            }

            mAvailableCurrency.setText(getContext().getString(R.string.available_currency_x_x,
                    "--", availableCurrencySign.toUpperCase()));
            mObtainableCurrency.setText(getContext().getString(R.string.obtainable_currency_x_x,
                    "--", obtainableCurrencySign.toUpperCase()));
//            mTradeCurrencyRange.setText(getString(R.string.trade_currency_range_0_to_x_x,
//                    "0", obtainableCurrencySign.toUpperCase()));
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
        if (mChangePriceView == null) return 0;

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

    private void updateTradeAmount() {
        if (mPairDesc != null && mCurrencyPair != null) {
            double price = mChangePriceView.getPrice();
            double volume = mVolumeInput.getVolume();
            int scale = mPairDesc.getSuffixSymbol().getBalancePoint();
            String unit = mCurrencyPair.getSuffixSymbol();
            if (volume == 0) {
                mTradeAmount.setText("--  " + unit.toUpperCase());
                return;
            }
            double amt = price * volume;
            String tradeAmt = "0";
            if (amt != 0) {
                tradeAmt = CurrencyUtils.getPrice(amt, scale);
            }
            mTradeAmount.setText(tradeAmt + " " + unit.toUpperCase());
        }
    }

    private void updateSelectPercentView() {
        mPercentSelectView.updateSelectPercent();
    }

    private void resetTradeAmount() {
        if (mCurrencyPair != null) {
            String unit = mCurrencyPair.getSuffixSymbol();
            mTradeAmount.setText("--  " + unit.toUpperCase());
        }
    }

    public void updatePrice(String price) {
        mChangePriceView.setPrice(price);
        mChangePriceView.startScaleAnim();
        mChangePriceView.setModifiedManually(true);
    }

    public void updateCoinAbleAmount(Resp<List<CoinAbleAmount>> resp) {
        mAvailableCurrencyList = resp.getData();

        if (mAvailableCurrencyList.size() > 0) {
            updateTradeCurrencyView();
        }
    }

    public void updateNoLoginUserAccount() {
        updateTradeCurrencyView();
    }

    public void updatePriceView(MarketData marketData) {
        if (!mChangePriceView.isModifiedManually()) {
            mChangePriceView.setPrice(marketData.getLastPrice());
        }
    }

    public void resetView() {
        mTradeVolumeView.reset();
        mChangePriceView.reset();
        mVolumeInput.reset();
        mPercentSelectView.updatePercent(0);
        mTradeDirRadio.selectTab(mTradeDir == TradeDir.DIR_BUY_IN ? 0 : 1);
    }

    public void updateDirection(){
        updateTradeDirectionView();
    }

    private void makeOrder() {
        if (mCurrencyPair == null) return;

        if (mVolumeInput.getVolume() == 0) {
            ToastUtil.show(R.string.please_input_right_amount);
            return;
        }
        final MakeOrder makeOrder = new MakeOrder();
        int direction = mTradeDir == TradeDir.DIR_BUY_IN ? Order.DIR_BUY : Order.DIR_SELL;
        makeOrder.setDirection(direction);
        makeOrder.setEntrustCount(mVolumeInput.getVolume());
        makeOrder.setPairs(mCurrencyPair.getPairs());
        makeOrder.setEntrustType(mTradeTypeValue);
        makeOrder.setEntrustPrice(getTradePrice());

        if(mOnFastTradeClickListener!=null){
            mOnFastTradeClickListener.onMakeOrder(makeOrder);
        }
        resetMakeOrder();
    }

    private void resetMakeOrder() {
        mVolumeInput.reset();
        mPercentSelectView.reset();
    }
}
