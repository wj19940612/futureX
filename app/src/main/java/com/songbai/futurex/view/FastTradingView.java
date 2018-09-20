package com.songbai.futurex.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.Preference;
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
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.adapter.SimpleRVAdapter;
import com.songbai.futurex.view.autofit.AutofitTextView;
import com.songbai.futurex.view.dialog.ItemSelectController;
import com.songbai.futurex.websocket.model.MarketData;
import com.songbai.futurex.websocket.model.TradeDir;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.songbai.futurex.model.order.Order.LIMIT_TRADE;
import static com.songbai.futurex.model.order.Order.MARKET_TRADE;

public class FastTradingView extends LinearLayout {

    public static final int VIEW_FLOWING = 1;
    public static final int VIEW_SINKING = 2;
    public static final int VIEW_GONE = 3;
    public static final int VIEW_VISIBLE = 4;


    @BindView(R.id.marketSwitcher)
    BuySellSwitcher mMarketSwitcher;
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
    @BindView(R.id.quickBtnLayout)
    LinearLayout mQuickBtnLayout;
    @BindView(R.id.quickStub)
    View mQuickStub;
    @BindView(R.id.normalStub)
    View mNormalStub;
    @BindView(R.id.tradeType)
    TextView mTradeType;
    @BindView(R.id.tipPrice)
    AutofitTextView mTipPrice;

    private int mTradeTypeValue;

    private TradeVolumeView mTradeVolumeView;

    private CurrencyPair mCurrencyPair;
    private int mTradeDir;
    private PairDesc mPairDesc;
    private double mTradeCurrencyVolume;
    private List<CoinAbleAmount> mAvailableCurrencyList;
    private onFastTradeClickListener mOnFastTradeClickListener;

    private int mViewStatus = VIEW_GONE;

    private boolean isQuickExchange;
    private double mPrice;

    public void setPrice(double price) {
        mPrice = price;
    }

    public interface onFastTradeClickListener {
        void onMakeOrder(MakeOrder makeOrder);

        void onFullTradeClick(int direction);

        void onCloseTradeClick();

        void onRecharge();
    }

    public void setonFastTradeClickListener(onFastTradeClickListener onFastTradeClickListener) {
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
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FastTradingView);
        isQuickExchange = typedArray.getBoolean(R.styleable.FastTradingView_isQuickTrade, false);
        typedArray.recycle();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_fast_trading, this, true);
        ButterKnife.bind(this);

        mTradeDir = TradeDir.DIR_BUY_IN;
        mTradeTypeValue = LIMIT_TRADE;

        if (isQuickExchange) {
            mTradeType.setVisibility(View.GONE);
            mMarketSwitcher.setVisibility(View.VISIBLE);
            mQuickStub.setVisibility(View.VISIBLE);
            mNormalStub.setVisibility(View.GONE);
            mQuickBtnLayout.setVisibility(View.VISIBLE);
        } else {
            mTradeType.setVisibility(View.VISIBLE);
            mMarketSwitcher.setVisibility(View.GONE);
            mQuickStub.setVisibility(View.GONE);
            mNormalStub.setVisibility(View.VISIBLE);
            mQuickBtnLayout.setVisibility(View.GONE);
        }

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
                updateEquivalent(price);
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

        mMarketSwitcher.setOnTabSelectedListener(new BuySellSwitcher.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                if (mCurrencyPair == null) return;

                if (position == 0) {
                    mTradeTypeValue = LIMIT_TRADE;
                } else {
                    mTradeTypeValue = MARKET_TRADE;
                }
                updateTradeDirectionView();
            }
        });
    }

    private void updateEquivalent(double price) {
        mTipPrice.setText(getContext().getString(R.string.equivalent_x_x,
                FinanceUtil.formatWithScale(mPrice * price),
                Preference.get().getPricingMethod().toUpperCase()));
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

    @OnClick({R.id.recharge, R.id.tradeButton, R.id.fullTrade, R.id.closeTrade, R.id.tradeType})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recharge:
                if (mOnFastTradeClickListener != null) {
                    mOnFastTradeClickListener.onRecharge();
                }
                break;
            case R.id.tradeButton:
                if (LocalUser.getUser().isLogin()) {
                    makeOrder();
                } else {
                    Launcher.with(getContext(), LoginActivity.class).execute();
                }
                break;
            case R.id.fullTrade:
                if (mOnFastTradeClickListener != null) {
                    mOnFastTradeClickListener.onFullTradeClick(mTradeDir);
                }
                break;
            case R.id.closeTrade:
                if (mOnFastTradeClickListener != null) {
                    mOnFastTradeClickListener.onCloseTradeClick();
                }
                break;
            case R.id.tradeType:
                showTradeTypeSelector();
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
                mTipPrice.setVisibility(VISIBLE);
            } else {
                tradeTypeRes = R.string.market_price;
                mChangePriceView.setVisibility(View.GONE);
                mMarketPriceView.setVisibility(View.VISIBLE);
                mTipPrice.setVisibility(INVISIBLE);
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
                mTipPrice.setVisibility(VISIBLE);
            } else {
                tradeTypeRes = R.string.market_price;
                mChangePriceView.setVisibility(View.GONE);
                mMarketPriceView.setVisibility(View.VISIBLE);
                mTipPrice.setVisibility(INVISIBLE);
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
//            mTradeDirSplitLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
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

    public void updateSelectPercentView() {
        mPercentSelectView.updateSelectPercent();
    }

    private void resetTradeAmount() {
        if (mCurrencyPair != null) {
            String unit = mCurrencyPair.getSuffixSymbol();
            mTradeAmount.setText("--  " + unit.toUpperCase());
        }
    }

    public void updatePrice(String price) {
        if (mChangePriceView.getVisibility() == View.GONE) return;

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

//        mTradeCurrencyRange.setText("");
    }

    public void setDirection(int direction) {
        mTradeDir = direction;
        mTradeDirRadio.selectTab(mTradeDir == TradeDir.DIR_BUY_IN ? 0 : 1);
        updateTradeDirectionView();
    }

    public void updateDirectionTab() {
        mTradeDirRadio.selectTab(mTradeDir == TradeDir.DIR_BUY_IN ? 0 : 1);
    }

    public void updateDirection() {
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

        if (mOnFastTradeClickListener != null) {
            mOnFastTradeClickListener.onMakeOrder(makeOrder);
        }
        resetMakeOrder();
    }

    private void resetMakeOrder() {
        mVolumeInput.reset();
        mPercentSelectView.reset();
    }

    private void showTradeTypeSelector() {
        if (!(getContext() instanceof Activity)) {
            return;
        }

        final SmartDialog dialog = SmartDialog.solo((Activity) getContext());

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
        itemSelectController.setHintText(getContext().getString(R.string.please_choose_limited_condition));
        itemSelectController.setAdapter(simpleRVAdapter);

        dialog.setCustomViewController(itemSelectController)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setWidthScale(1)
                .show();
    }

    public int getViewStatus() {
        return mViewStatus;
    }

    public void setViewStatus(int status) {
        mViewStatus = status;
    }

    public boolean isSinking() {
        return mViewStatus == VIEW_SINKING;
    }

    public boolean isFlowing() {
        return mViewStatus == VIEW_FLOWING;
    }

    public boolean isGone() {
        return mViewStatus == VIEW_GONE;
    }

    public boolean isVisible() {
        return mViewStatus == VIEW_VISIBLE;
    }
}
