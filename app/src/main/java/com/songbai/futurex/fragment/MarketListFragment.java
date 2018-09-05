package com.songbai.futurex.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.KTrend;
import com.songbai.futurex.swipeload.BaseSwipeLoadFragment;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.adapter.GroupAdapter;
import com.songbai.futurex.view.chart.TimeShareChart;
import com.songbai.futurex.view.recycler.DividerItemDecor;
import com.songbai.futurex.websocket.model.MarketData;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class MarketListFragment extends BaseSwipeLoadFragment<RecyclerView> {

    public static MarketListFragment newInstance(String counterCurrency) {
        MarketListFragment fragment = new MarketListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("counterCurrency", counterCurrency);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @BindView(R.id.swipe_target)
    RecyclerView mPairList;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    Unbinder unbinder;

    private CurrencyPairAdapter mCurrencyPairAdapter;

    private String mCounterCurrency;
    private Map<String, MarketData> mMarketDataList;
    private MarketDiffCallback mMarketDiffCallback;

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
        mSwipeToLoadLayout.setLoadMoreEnabled(false);
        mMarketDiffCallback = new MarketDiffCallback();
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
        mPairList.setItemAnimator(null);
        mPairList.setAdapter(mCurrencyPairAdapter);

        requestPairList(mCounterCurrency);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mCurrencyPairAdapter != null && isAdded()) {
            mCurrencyPairAdapter.setMarketDataListData(mMarketDataList);
            requestPairList(mCounterCurrency);
        }
    }

    public void setMarketDataList(Map<String, MarketData> marketDataList) {
        if (mCurrencyPairAdapter != null && getUserVisibleHint()) {
            mMarketDiffCallback.setOldList(mCurrencyPairAdapter.getGroupList());
            mMarketDiffCallback.setNewList(mCurrencyPairAdapter.getGroupList());
            mMarketDiffCallback.setMarketMap(marketDataList);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(mMarketDiffCallback);
            mCurrencyPairAdapter.setMarketDataListData(mMarketDataList);
            diffResult.dispatchUpdatesTo(mCurrencyPairAdapter);
        }

        mMarketDataList = marketDataList;
//        if (getUserVisibleHint() && mCurrencyPairAdapter != null) {
//            mCurrencyPairAdapter.setMarketDataList(mMarketDataList);
//        }
    }

    private void requestPairList(String counterCurrency) {
        Apic.getCurrencyPairList(counterCurrency).tag(TAG)
                .callback(new Callback4Resp<Resp<List<CurrencyPair>>, List<CurrencyPair>>() {
                    @Override
                    protected void onRespData(List<CurrencyPair> data) {
                        Collections.sort(data);
                        mCurrencyPairAdapter.setGroupableList(data);

                        getKTrendData(data);
                        showEmptyView(data);

                        if (TextUtils.isEmpty(Preference.get().getDefaultTradePair())) {
                            if (data.isEmpty()) return;
                            Preference.get().setDefaultTradePair(data.get(0).getPairs());
                        } // Save the default trade pair
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopFreshOrLoadAnimation();
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
            mCurrencyPairAdapter.setKTrendListMap(data);
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

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {
        requestPairList(mCounterCurrency);
    }

    @NonNull
    @Override
    public RecyclerView getSwipeTargetView() {
        return mPairList;
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
        return mLoadMoreFooterView;
    }

    static class CurrencyPairAdapter extends GroupAdapter<CurrencyPair> {

        private Map<String, MarketData> mMarketDataList;
        private Context mContext;
        private OnRVItemClickListener mOnRVItemClickListener;
        private Map<String, List<KTrend>> mKTrendListMap;
        private Set<String> mPairsSet;

        public CurrencyPairAdapter(Context context, OnRVItemClickListener onRVItemClickListener) {
            super();
            mMarketDataList = new HashMap<>();
            mContext = context;
            mOnRVItemClickListener = onRVItemClickListener;
            mPairsSet = new HashSet<>();
        }

        public void setMarketDataList(Map<String, MarketData> marketDataList) {
            mMarketDataList = marketDataList;
            notifyDataSetChanged();
        }

        public void setMarketDataListData(Map<String, MarketData> marketDataList) {
            mMarketDataList = marketDataList;
        }

        public void setKTrendListMap(Map<String, List<KTrend>> kTrendListMap) {
            mKTrendListMap = kTrendListMap;
            mPairsSet.clear();
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
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position);
            } else if (holder instanceof ViewHolder) {
                Bundle bundle = (Bundle) payloads.get(0);
                MarketData marketData = bundle.getParcelable(ExtraKeys.MARKET_DATA);
                CurrencyPair currencyPair = bundle.getParcelable(ExtraKeys.CURRENCY_PAIR);
                ((ViewHolder) holder).bind(currencyPair, marketData, mContext);
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
                    ((ViewHolder) holder).bind(position,pair, mMarketDataList, mContext, mKTrendListMap == null ? null : mKTrendListMap.get(pair.getPairs()), mPairsSet);
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
            TimeShareChart mTimeShareChart;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(int position,CurrencyPair pair, Map<String, MarketData> marketDataList, Context context, List<KTrend> mKTrends, Set<String> pairsSet) {
                double upDropSeed;
                mBaseCurrency.setText(pair.getPrefixSymbol().toUpperCase());
                mCounterCurrency.setText(pair.getSuffixSymbol().toUpperCase());
                if (marketDataList != null && marketDataList.get(pair.getPairs()) != null) {
                    MarketData marketData = marketDataList.get(pair.getPairs());
                    upDropSeed = marketData.getUpDropSpeed();
                    mTradeVolume.setText(context.getString(R.string.volume_24h_x, CurrencyUtils.get24HourVolume(marketData.getVolume())));
                    mLastPrice.setText(CurrencyUtils.getPrice(marketData.getLastPrice(), pair.getPricePoint()));
                    mPriceChange.setText(CurrencyUtils.getPrefixPercent(marketData.getUpDropSpeed()));
                    if (marketData.getUpDropSpeed() < 0) {
                        mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.red));
                    } else {
                        mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.green));
                    }
                } else {
                    mLastPrice.setText(CurrencyUtils.getPrice(pair.getLastPrice(), pair.getPricePoint()));
                    mPriceChange.setText(CurrencyUtils.getPrefixPercent(pair.getUpDropSpeed()));
                    upDropSeed = pair.getUpDropSpeed();
                    if (pair.getUpDropSpeed() < 0) {
                        mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.red));
                    } else {
                        mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.green));
                    }
                }


                if (!pairsSet.contains(pair.getPairs())) {
                    mTimeShareChart.updateData(pair.getPairs(), mKTrends, upDropSeed);
                    pairsSet.add(pair.getPairs());
                } else {
                    mTimeShareChart.justDraw(pair.getPairs(),upDropSeed);
                }
            }

            public void bind(CurrencyPair pair, MarketData marketData, Context context) {
                double upDropSeed;
                if (marketData != null && marketData != null) {
                    upDropSeed = marketData.getUpDropSpeed();
                    mTradeVolume.setText(context.getString(R.string.volume_24h_x, CurrencyUtils.get24HourVolume(marketData.getVolume())));
                    mLastPrice.setText(CurrencyUtils.getPrice(marketData.getLastPrice(), pair.getPricePoint()));
                    mPriceChange.setText(CurrencyUtils.getPrefixPercent(marketData.getUpDropSpeed()));
                    if (marketData.getUpDropSpeed() < 0) {
                        mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.red));
                    } else {
                        mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.green));
                    }
                } else {
                    mLastPrice.setText(CurrencyUtils.getPrice(pair.getLastPrice(), pair.getPricePoint()));
                    mPriceChange.setText(CurrencyUtils.getPrefixPercent(pair.getUpDropSpeed()));
                    upDropSeed = pair.getUpDropSpeed();
                    if (pair.getUpDropSpeed() < 0) {
                        mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.red));
                    } else {
                        mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.green));
                    }
                }

                mTimeShareChart.justDraw(pair.getPairs(),upDropSeed);
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

    public static class MarketDiffCallback extends DiffUtil.Callback {
        private Map<String, MarketData> mMarketMap;
        private List<GroupAdapter.Group<CurrencyPair>> mOldList;
        private List<GroupAdapter.Group<CurrencyPair>> mNewList;

        public void setOldList(List<GroupAdapter.Group<CurrencyPair>> oldList) {
            mOldList = oldList;
        }

        public void setNewList(List<GroupAdapter.Group<CurrencyPair>> newList) {
            mNewList = newList;
        }

        public void setMarketMap(Map<String, MarketData> marketMap) {
            mMarketMap = marketMap;
        }

        @Override
        public int getOldListSize() {
            return mOldList == null ? 0 : getItemCount(mOldList);
        }

        @Override
        public int getNewListSize() {
            return mNewList == null ? 0 : getItemCount(mNewList);
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (oldItemPosition != newItemPosition || mOldList == null || mNewList == null) {
                return false;
            }
            if (mOldList.size() != mNewList.size()) {
                return false;
            }
            final GroupAdapter.Groupable oldItem = getItem(oldItemPosition, mOldList);
            final GroupAdapter.Groupable newItem = getItem(oldItemPosition, mNewList);
            if (oldItem == null || newItem == null || !(oldItem instanceof CurrencyPair) || !(newItem instanceof CurrencyPair)) {
                return false;
            }
            return ((CurrencyPair) oldItem).getPairs().equals(((CurrencyPair) newItem).getPairs());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            // 定向刷新中的部分更新
            // 效率最高


            final GroupAdapter.Groupable newItem = getItem(newItemPosition, mNewList);
            if (newItem == null || !(newItem instanceof CurrencyPair)) {
                return null;
            }

            Bundle payload = new Bundle();
            payload.putParcelable(ExtraKeys.MARKET_DATA, mMarketMap.get(((CurrencyPair) newItem).getPairs()));
            payload.putParcelable(ExtraKeys.CURRENCY_PAIR, (CurrencyPair) newItem);
            return payload;
        }

        public GroupAdapter.Groupable getItem(int position, List<GroupAdapter.Group<CurrencyPair>> mGroupList) {
            int firstIndex = 0;
            for (GroupAdapter.Group group : mGroupList) {
                int size = group.getItemCount();
                int index = position - firstIndex;
                if (index < size) {
                    return group.getItem(index);
                }
                firstIndex += size;
            }
            return null;
        }

        public int getItemCount(List<GroupAdapter.Group<CurrencyPair>> mGroupList) {
            int count = 0;
            for (GroupAdapter.Group group : mGroupList) {
                count += group.getItemCount();
            }
            return count;
        }

    }


}
