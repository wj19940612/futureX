package com.songbai.futurex.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.Preference;
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
import com.songbai.futurex.model.mine.CoinAbleAmount;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.NumUtils;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.adapter.SimpleRVAdapter;
import com.songbai.futurex.view.ChangePriceView;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TradeVolumeView;
import com.songbai.futurex.view.VolumeInputView;
import com.songbai.futurex.view.dialog.ItemSelectController;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.market.MarketSubscriber;
import com.songbai.futurex.websocket.model.MarketData;
import com.songbai.futurex.websocket.model.PairMarket;
import com.songbai.futurex.websocket.model.TradeDir;

import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.availableCurrency)
    TextView mAvailableCurrency;
    @BindView(R.id.obtainableCurrency)
    TextView mObtainableCurrency;
    @BindView(R.id.tradeButton)
    TextView mTradeButton;
    @BindView(R.id.marketPriceView)
    TextView mMarketPriceView;
    @BindView(R.id.obtainableCurrencyRange)
    TextView mObtainableCurrencyRange;
    @BindView(R.id.orderListRadio)
    RadioHeader mOrderListRadio;
    @BindView(R.id.orderListView)
    RecyclerView mOrderListView;
    @BindView(R.id.volumeInput)
    VolumeInputView mVolumeInput;
    @BindView(R.id.tradeVolumeSeekBar)
    SeekBar mTradeVolumeSeekBar;

    Unbinder unbinder;

    private CurrencyPair mCurrencyPair;
    private String mTradePair;
    private PairDesc mPairDesc;
    private int mTradeDir;
    private int mTradeTypeValue;

    private MarketSubscriber mMarketSubscriber;
    private OnOptionalClickListener mOnOptionalClickListener;
    private OptionsPickerView mPickerView;
    private List<CoinAbleAmount> mAvailableCurrencyList;
    private double mObtainableCurrencyVolume;

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
        mTradeTypeValue = LIMIT_TRADE;
        mTradeDirRadio.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                if (position == 0) { // buy in
                    mTradeDir = TradeDir.DIR_BUY_IN;
                    updateTradeDirectionView();
                    updateTradeCurrencyView();
                } else {
                    mTradeDir = TradeDir.DIR_SELL_OUT;
                    updateTradeDirectionView();
                    updateTradeCurrencyView();
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
            }
        });

        mVolumeInput.setOnVolumeChangeListener(new VolumeInputView.OnVolumeChangeListener() {
            @Override
            public void onVolumeChange(double volume) {
                updateTradeAmount();
            }
        });

        mTradeVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTradeVolumeView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mMarketSubscriber = new MarketSubscriber(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code) {
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
        });
    }

    private void updateTradeVolumeView() {
        int progress = mTradeVolumeSeekBar.getProgress();
        int max = mTradeVolumeSeekBar.getMax();
        double tradeVolume = mObtainableCurrencyVolume * progress / max;
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
                tradeAmt = NumUtils.getPrice(amt, scale);
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

    private void updateMarketView(PairMarket pairMarket) {
        mTradeVolumeView.setDeepList(pairMarket.getDeep().getBuyDeep(),
                pairMarket.getDeep().getSellDeep());
        MarketData quota = pairMarket.getQuota();
        if (mPairDesc != null && quota != null) {
            mLastPrice.setText(NumUtils.getPrice(quota.getLastPrice(), mPairDesc.getPairs().getPricePoint()));
            mPriceChange.setText(NumUtils.getPrefixPercent(quota.getUpDropSpeed()));
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
        if (LocalUser.getUser().isLogin() && mAvailableCurrencyList != null) {
            String availableCurrencySign = mCurrencyPair.getSuffixSymbol();
            String obtainableCurrencySign = mCurrencyPair.getPrefixSymbol();
            int availableCurrencyScale = mPairDesc.getSuffixSymbol().getBalancePoint();
            int obtainableCurrencyScale = mPairDesc.getPrefixSymbol().getBalancePoint();

            if (mTradeDir == TradeDir.DIR_SELL_OUT) {
                availableCurrencySign = mCurrencyPair.getPrefixSymbol();
                obtainableCurrencySign = mCurrencyPair.getSuffixSymbol();
                availableCurrencyScale = mPairDesc.getPrefixSymbol().getBalancePoint();
                obtainableCurrencyScale = mPairDesc.getSuffixSymbol().getBalancePoint();
            }

            double availableCurrencyVolume = getAvailableCurrencyAmt(mAvailableCurrencyList, availableCurrencySign);
            mObtainableCurrencyVolume = getObtainableCurrencyAmt(availableCurrencyVolume);

            mAvailableCurrency.setText(getString(R.string.available_currency_x_x,
                    NumUtils.getPrice(availableCurrencyVolume, availableCurrencyScale), availableCurrencySign.toUpperCase()));
            mObtainableCurrency.setText(getString(R.string.obtainable_currency_x_x,
                    NumUtils.getPrice(mObtainableCurrencyVolume, obtainableCurrencyScale), obtainableCurrencySign.toUpperCase()));
            mObtainableCurrencyRange.setText(getString(R.string.obtainable_currency_range_0_to_x_x,
                    NumUtils.getPrice(mObtainableCurrencyVolume, obtainableCurrencyScale), obtainableCurrencySign.toUpperCase()));
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
            mObtainableCurrencyRange.setText(getString(R.string.obtainable_currency_range_0_to_x_x,
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
        double price = mChangePriceView.getPrice();
        if (mTradeTypeValue == MARKET_TRADE) {
            if (mTradeDir == TradeDir.DIR_BUY_IN) {
                price = mTradeVolumeView.getAskPrice1();
            } else {
                price = mTradeVolumeView.getBidPrice1();
            }
        }

        if (price == 0) return 0;

        if (mTradeDir == TradeDir.DIR_BUY_IN) {
            return availableCounterCurrency / price;
        } else {
            return availableCounterCurrency * price;
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
                    }
                }).fireFreely();
    }

    private void initViews() {
        mPairName.setText(mCurrencyPair.getPrefixSymbol().toUpperCase() +
                "/" + mCurrencyPair.getSuffixSymbol().toUpperCase());
        mDecimalScale.setText(getString(R.string.x_scale_decimal, String.valueOf(mPairDesc.getPairs().getPricePoint())));
        mTradeVolumeView.setPriceScale(mPairDesc.getPairs().getPricePoint());
        mTradeVolumeView.setMergeScale(mPairDesc.getPairs().getPricePoint());
        mChangePriceView.setPriceScale(mPairDesc.getPairs().getPricePoint());
        mTradeVolumeView.setCurrencyPair(mCurrencyPair);
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
        if (mOptionalStatus.isSelected()) { // 已经添加自选
            Apic.cancelOptional(mCurrencyPair.getPairs()).tag(TAG)
                    .callback(new Callback<Resp>() {
                        @Override
                        protected void onRespSuccess(Resp resp) {
                            mOptionalStatus.setSelected(false);
                            ToastUtil.show(R.string.optional_cancel);
                        }
                    }).fire();
        } else {
            Apic.addOptional(mCurrencyPair.getPairs()).tag(TAG)
                    .callback(new Callback<Resp>() {
                        @Override
                        protected void onRespSuccess(Resp resp) {
                            mOptionalStatus.setSelected(true);
                            ToastUtil.show(R.string.optional_added);
                        }
                    }).fire();
        }
        if (mOnOptionalClickListener != null) {
            mOnOptionalClickListener.onOptionalClick();
        }
    }

    private void openMarketDetailPage(CurrencyPair currencyPair) {
        UniqueActivity.launcher(this, MarketDetailFragment.class)
                .putExtra(ExtraKeys.CURRENCY_PAIR, currencyPair)
                .execute();
    }

    @OnClick({R.id.optionalStatus, R.id.pairName, R.id.pairArrow, R.id.switchToMarketPage,
            R.id.decimalScale, R.id.tradeType, R.id.recharge, R.id.tradeButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.optionalStatus:
                if (LocalUser.getUser().isLogin()) {
                    toggleOptionalStatus();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.pairName:
            case R.id.pairArrow:

                break;
            case R.id.switchToMarketPage:
                if (mCurrencyPair != null) {
                    openMarketDetailPage(mCurrencyPair);
                }
                break;
            case R.id.decimalScale:
                showDecimalScaleSelector();
                break;
            case R.id.tradeType:
                showTradeTypeSelector();
                break;
            case R.id.recharge:
                break;
            case R.id.tradeButton:
                break;
        }
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
}
