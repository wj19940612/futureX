package com.songbai.futurex.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.KTrend;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.adapter.GroupAdapter;
import com.songbai.futurex.view.chart.TimeShareChart;
import com.songbai.futurex.view.chart.TimeShareSurfaceView;
import com.songbai.futurex.view.recycler.DividerItemDecor;
import com.songbai.futurex.websocket.model.MarketData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/8/22
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class MarketListFragment extends BaseFragment {

    public static MarketListFragment newInstance(String counterCurrency) {
        MarketListFragment fragment = new MarketListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("counterCurrency", counterCurrency);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.pairList)
    RecyclerView mPairList;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    Unbinder unbinder;

    private CurrencyPairAdapter mCurrencyPairAdapter;

    private String mCounterCurrency;
    private Map<String, MarketData> mMarketDataList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCounterCurrency = getArguments().getString("counterCurrency");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPairList.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecor dividerItemDecor = new DividerItemDecor(getActivity(), DividerItemDecoration.VERTICAL);
        mPairList.addItemDecoration(dividerItemDecor);
        mCurrencyPairAdapter = new CurrencyPairAdapter(getActivity(), new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                if (getParentFragment() != null && getParentFragment() instanceof MarketFragment
                        && obj instanceof CurrencyPair) {
                    ((MarketFragment) getParentFragment()).openMarketDetailPage((CurrencyPair) obj);
                }
            }
        });
        mPairList.setAdapter(mCurrencyPairAdapter);

        requestPairList(mCounterCurrency);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mCurrencyPairAdapter != null) {
            mCurrencyPairAdapter.setMarketDataList(mMarketDataList);
        }
    }

    public void setMarketDataList(Map<String, MarketData> marketDataList) {
        mMarketDataList = marketDataList;
        if (getUserVisibleHint() && mCurrencyPairAdapter != null) {
            mCurrencyPairAdapter.setMarketDataList(mMarketDataList);
        }
    }

    private void requestPairList(String counterCurrency) {
        Apic.getCurrencyPairList(counterCurrency).tag(TAG)
                .callback(new Callback4Resp<Resp<List<CurrencyPair>>, List<CurrencyPair>>() {
                    @Override
                    protected void onRespData(List<CurrencyPair> data) {
//                        data.addAll(data);
                        Collections.sort(data);
                        mCurrencyPairAdapter.setGroupableList(data);

                        getKTrendData(data);
                        showEmptyView(data);

                        if (TextUtils.isEmpty(Preference.get().getDefaultTradePair())) {
                            if (data.isEmpty()) return;
                            Preference.get().setDefaultTradePair(data.get(0).getPairs());
                        } // Save the default trade pair
                    }
                }).fireFreely();
    }

    private void showEmptyView(List<CurrencyPair> data) {
        if (data.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mPairList.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mPairList.setVisibility(View.VISIBLE);
        }
    }

    private void getKTrendData(List<CurrencyPair> data) {
        if (data == null || data.size() == 0) return;

        String paris = getParis(data);
        Apic.requestKTrendPairs(paris).tag(TAG).callback(new Callback4Resp<Resp<HashMap<String, List<KTrend>>>, HashMap<String, List<KTrend>>>() {
            @Override
            protected void onRespData(HashMap<String, List<KTrend>> data) {
                updateKTrendData(data);
            }
        }).fireFreely();
    }

    private void updateKTrendData(HashMap<String, List<KTrend>> data) {
        if (mCurrencyPairAdapter != null) {
            HashMap<String, List<KTrend>> hashMap = new HashMap<>();
            hashMap.put("eth_usdt",data.get("eth_usdt"));
            mCurrencyPairAdapter.setKTrendListMap(hashMap);
            mCurrencyPairAdapter.notifyDataSetChanged();
        }
    }

    private String getParis(List<CurrencyPair> data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (CurrencyPair currencyPair : data) {
            stringBuilder.append(currencyPair.getPairs());
            stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    static class CurrencyPairAdapter extends GroupAdapter<CurrencyPair> {

        private Map<String, MarketData> mMarketDataList;
        private Context mContext;
        private OnRVItemClickListener mOnRVItemClickListener;
        private Map<String, List<KTrend>> mKTrendListMap;

        public CurrencyPairAdapter(Context context, OnRVItemClickListener onRVItemClickListener) {
            super();
            mMarketDataList = new HashMap<>();
            mContext = context;
            mOnRVItemClickListener = onRVItemClickListener;
        }

        public void setMarketDataList(Map<String, MarketData> marketDataList) {
            mMarketDataList = marketDataList;
            notifyDataSetChanged();
        }

        public void setKTrendListMap(Map<String, List<KTrend>> kTrendListMap) {
            mKTrendListMap = kTrendListMap;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == HEAD) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency_pair_header, parent, false);
                return new GroupHeaderHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency_pair, parent, false);
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof GroupHeaderHolder) {
                ((GroupHeaderHolder) holder).bind(getItem(position));
            } else {
                final Groupable item = getItem(position);
                if (item instanceof CurrencyPair) {
                    final CurrencyPair pair = (CurrencyPair) item;
                    ((ViewHolder) holder).bind(pair, mMarketDataList, mContext,mKTrendListMap==null?null:mKTrendListMap.get(pair.getPairs()));
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRVItemClickListener.onItemClick(v, position, pair);
                        }
                    });
                }
            }
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.baseCurrency)
            TextView mBaseCurrency;
            @BindView(R.id.counterCurrency)
            TextView mCounterCurrency;
            @BindView(R.id.tradeVolume)
            TextView mTradeVolume;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.priceChange)
            TextView mPriceChange;
            @BindView(R.id.chart)
            TimeShareSurfaceView mTimeShareChart;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(CurrencyPair pair, Map<String, MarketData> marketDataList, Context context,List<KTrend> mKTrends) {
                mTimeShareChart.updateData(mKTrends);
                mBaseCurrency.setText(pair.getPrefixSymbol().toUpperCase());
                mCounterCurrency.setText(pair.getSuffixSymbol().toUpperCase());
                if (marketDataList != null && marketDataList.get(pair.getPairs()) != null) {
                    MarketData marketData = marketDataList.get(pair.getPairs());
                    mTradeVolume.setText(context.getString(R.string.volume_24h_x, CurrencyUtils.get24HourVolume(marketData.getVolume())));
                    mLastPrice.setText(CurrencyUtils.getPrice(marketData.getLastPrice(), pair.getPricePoint()));
                    mPriceChange.setText(CurrencyUtils.getPrefixPercent(marketData.getUpDropSpeed()));
                    if (marketData.getUpDropSpeed() < 0) {
                        mPriceChange.setBackgroundResource(R.drawable.bg_red_r2);
                    } else {
                        mPriceChange.setBackgroundResource(R.drawable.bg_green_r2);
                    }
                } else {
                    mLastPrice.setText(CurrencyUtils.getPrice(pair.getLastPrice(), pair.getPricePoint()));
                    mPriceChange.setText(CurrencyUtils.getPrefixPercent(pair.getUpDropSpeed()));
                    if (pair.getUpDropSpeed() < 0) {
                        mPriceChange.setBackgroundResource(R.drawable.bg_red_r2);
                    } else {
                        mPriceChange.setBackgroundResource(R.drawable.bg_green_r2);
                    }
                }
            }
        }

        static class GroupHeaderHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.headerName)
            TextView mHeaderName;

            public GroupHeaderHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(Groupable groupable) {
                mHeaderName.setText(groupable.getGroupNameRes());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
