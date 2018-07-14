package com.songbai.futurex.view.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.view.recycler.DividerItemDecor;
import com.songbai.futurex.websocket.model.MarketData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Modified by john on 2018/7/3
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class CurrencyPairsPopup {
    private Map<String, List<CurrencyPair>> mPairListMap;
    private View mView;
    private PopupWindow mPopupWindow;
    private View mDimView;

    private RecyclerView mCounterCurrencyList;
    private RecyclerView mBaseCurrencyList;

    private CounterCurrencyAdapter mCounterCurrencyAdapter;
    private BaseCurrencyAdapter mBaseBaseCurrencyAdapter;
    private CurrencyPair mCurCurrencyPair;
    private OnCurrencyChangeListener mOnCurrencyChangeListener;
    private OnSearchBoxClickListener mOnSearchBoxClickListener;

    public interface OnSearchBoxClickListener {
        void onSearchBoxClick();
    }

    public interface OnCurrencyChangeListener {
        void onCounterCurrencyChange(String counterCurrency, List<CurrencyPair> newDisplayList);

        void onBaseCurrencyChange(String baseCurrency, CurrencyPair currencyPair);
    }

    public CurrencyPairsPopup(Context context, CurrencyPair currencyPair,
                              OnCurrencyChangeListener onCurrencyChangeListener,
                              OnSearchBoxClickListener onSearchBoxClickListener) {
        mPairListMap = new HashMap<>();
        mCurCurrencyPair = currencyPair;
        mOnCurrencyChangeListener = onCurrencyChangeListener;
        mOnSearchBoxClickListener = onSearchBoxClickListener;

        mView = LayoutInflater.from(context).inflate(R.layout.view_popup_currency_pairs, null, false);
        mCounterCurrencyList = mView.findViewById(R.id.counterCurrencyList);
        mBaseCurrencyList = mView.findViewById(R.id.baseCurrencyList);
        mView.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSearchBoxClickListener.onSearchBoxClick();
            }
        });

        String[] stringArray = context.getResources().getStringArray(R.array.market_radio_header);
        List<String> counterCurrency = new ArrayList<>(Arrays.asList(stringArray));
        mCounterCurrencyList.setLayoutManager(new LinearLayoutManager(context));
        mCounterCurrencyAdapter = new CounterCurrencyAdapter(counterCurrency, mCurCurrencyPair.getSuffixSymbol(),
                new OnRVItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, Object obj) {
                        String newCounterCurrency = (String) obj;
                        if (!mCounterCurrencyAdapter.getSelectedCurrency().equalsIgnoreCase(newCounterCurrency)) {
                            selectCounterCurrency(newCounterCurrency);
                        }
                    }
                });
        mCounterCurrencyList.setAdapter(mCounterCurrencyAdapter);

        mBaseCurrencyList.setLayoutManager(new LinearLayoutManager(context));
        mBaseCurrencyList.addItemDecoration(new DividerItemDecor(context, DividerItemDecor.VERTICAL));
        mBaseBaseCurrencyAdapter = new BaseCurrencyAdapter(mCurCurrencyPair, new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                if (obj instanceof CurrencyPair) {
                    mPopupWindow.dismiss();

                    if (((CurrencyPair) obj).getPairs().equalsIgnoreCase(mCurCurrencyPair.getPairs()))
                        return;

                    if (mOnCurrencyChangeListener != null) {
                        mOnCurrencyChangeListener.onBaseCurrencyChange(((CurrencyPair) obj).getPrefixSymbol(), (CurrencyPair) obj);
                    }
                }
            }
        });
        mBaseCurrencyList.setAdapter(mBaseBaseCurrencyAdapter);
    }

    public void setDimView(View dimView) {
        mDimView = dimView;
        mDimView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void selectCounterCurrency(String counterCurrency) {
        mCounterCurrencyAdapter.setSelectedCurrency(counterCurrency);
        List<CurrencyPair> pairList = mPairListMap.get(counterCurrency);
        if (pairList != null) {
            mBaseBaseCurrencyAdapter.setCurrencyPairList(pairList);
        }
        if (mOnCurrencyChangeListener != null) {
            mOnCurrencyChangeListener.onCounterCurrencyChange(counterCurrency, pairList);
        }
    }

    public void setCurCurrencyPair(CurrencyPair currencyPair) {
        mCurCurrencyPair = currencyPair;
        mCounterCurrencyAdapter.setSelectedCurrency(currencyPair.getSuffixSymbol());
        mBaseBaseCurrencyAdapter.setCurCurrencyPair(currencyPair);
    }

    public String getSelectCounterCurrency() {
        return mCounterCurrencyAdapter.getSelectedCurrency();
    }

    public void show(View view) {
        mPopupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mDimView != null && mDimView.getVisibility() == View.VISIBLE) {
                    mDimView.setVisibility(View.GONE);
                }
            }
        });
        mPopupWindow.showAsDropDown(view);

        if (mDimView != null && mDimView.getVisibility() != View.VISIBLE) {
            mDimView.setVisibility(View.VISIBLE);
        }
    }

    public boolean isShowing() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            return true;
        }
        return false;
    }

    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public void showDisplayList(String counterCurrency, List<CurrencyPair> pairList) {
        mPairListMap.put(counterCurrency, pairList);
        mBaseBaseCurrencyAdapter.setCurrencyPairList(pairList);
    }

    public void setMarketDataList(Map<String, MarketData> marketDataList) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mBaseBaseCurrencyAdapter.setMarketDataList(marketDataList);
        }
    }

    static class CounterCurrencyAdapter extends RecyclerView.Adapter<CounterCurrencyAdapter.VHolder> {

        private List<String> mCurrencyList;
        private OnRVItemClickListener mOnRVItemClickListener;
        private String mSelectedCurrency;

        public CounterCurrencyAdapter(List<String> currencyList, String selectedCurrency, OnRVItemClickListener onRVItemClickListener) {
            mCurrencyList = currencyList;
            mOnRVItemClickListener = onRVItemClickListener;
            mSelectedCurrency = selectedCurrency;
        }

        @NonNull
        @Override
        public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_counter_currency, parent, false);
            return new VHolder(row);
        }

        @Override
        public void onBindViewHolder(@NonNull final VHolder holder, final int position) {
            holder.bind(mCurrencyList.get(position), mSelectedCurrency);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRVItemClickListener.onItemClick(holder.itemView, position, mCurrencyList.get(position));
                }
            });
        }

        public void setSelectedCurrency(String selectedCurrency) {
            mSelectedCurrency = selectedCurrency;
            notifyDataSetChanged();
        }

        public String getSelectedCurrency() {
            return mSelectedCurrency;
        }

        @Override
        public int getItemCount() {
            return mCurrencyList.size();
        }

        static class VHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.text)
            TextView mText;

            public VHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(String s, String selectedPair) {
                mText.setText(s);
                if (selectedPair.equalsIgnoreCase(s)) {
                    mText.setSelected(true);
                } else {
                    mText.setSelected(false);
                }
            }
        }
    }

    static class BaseCurrencyAdapter extends RecyclerView.Adapter<BaseCurrencyAdapter.VHolder> {

        private Context mContext;
        private OnRVItemClickListener mOnRVItemClickListener;
        private List<CurrencyPair> mCurrencyPairList;
        private CurrencyPair mCurCurrencyPair;
        private Map<String, MarketData> mMarketDataList;

        public BaseCurrencyAdapter(CurrencyPair currencyPair, OnRVItemClickListener onRVItemClickListener) {
            mOnRVItemClickListener = onRVItemClickListener;
            mCurrencyPairList = new ArrayList<>();
            mCurCurrencyPair = currencyPair;
        }

        public void setCurrencyPairList(List<CurrencyPair> currencyPairList) {
            mCurrencyPairList = currencyPairList;
            notifyDataSetChanged();
        }

        public void setCurCurrencyPair(CurrencyPair currencyPair) {
            mCurCurrencyPair = currencyPair;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_base_currency, parent, false);
            return new VHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final VHolder holder, final int position) {
            holder.bind(mCurrencyPairList.get(position), mCurCurrencyPair, mMarketDataList, mContext);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRVItemClickListener.onItemClick(holder.itemView, position, mCurrencyPairList.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCurrencyPairList.size();
        }

        public void setMarketDataList(Map<String, MarketData> marketDataList) {
            mMarketDataList = marketDataList;
            notifyDataSetChanged();
        }

        static class VHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.mark)
            ImageView mMark;
            @BindView(R.id.currencyName)
            TextView mCurrencyName;
            @BindView(R.id.counterCurrencyName)
            TextView mCounterCurrencyName;
            @BindView(R.id.priceChange)
            TextView mPriceChange;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;

            public VHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(CurrencyPair currencyPair, CurrencyPair selectedPair, Map<String, MarketData> marketDataList, Context context) {
                mCurrencyName.setText(currencyPair.getPrefixSymbol().toUpperCase());
                mCounterCurrencyName.setText(currencyPair.getSuffixSymbol().toUpperCase());
                if (currencyPair.getPairs().equalsIgnoreCase(selectedPair.getPairs())) {
                    mMark.setVisibility(View.VISIBLE);
                } else {
                    mMark.setVisibility(View.INVISIBLE);
                }
                if (marketDataList != null) {
                    MarketData marketData = marketDataList.get(currencyPair.getPairs());
                    if (marketData != null) {
                        mLastPrice.setText(CurrencyUtils.getPrice(marketData.getLastPrice(), currencyPair.getPricePoint()));
                        mPriceChange.setText(CurrencyUtils.getPrefixPercent(marketData.getUpDropSpeed()));
                        if (marketData.getUpDropSpeed() < 0) {
                            mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.red));
                        } else {
                            mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.green));
                        }
                    }
                }
            }
        }
    }
}
