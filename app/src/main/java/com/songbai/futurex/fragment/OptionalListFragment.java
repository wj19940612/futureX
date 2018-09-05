package com.songbai.futurex.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.google.gson.Gson;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.KTrend;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.swipeload.BaseSwipeLoadFragment;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.UmengCountEventId;
import com.songbai.futurex.view.chart.TimeShareChart;
import com.songbai.futurex.view.recycler.DividerItemDecor;
import com.songbai.futurex.websocket.model.MarketData;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/8/22
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class OptionalListFragment extends BaseSwipeLoadFragment<RecyclerView> {

    private static final int REQ_CODE_LOGIN = 91;

    @BindView(R.id.swipe_target)
    RecyclerView mOptionalList;
    @BindView(R.id.optionalEmptyView)
    LinearLayout mOptionalEmptyView;
    Unbinder unbinder;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @BindView(R.id.addOptional)
    TextView mAddOptional;

    private OptionalAdapter mOptionalAdapter;
    private Map<String, MarketData> mMarketDataList;
    private MarketDiffCallback mMarketDiffCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_optional_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeToLoadLayout.setLoadMoreEnabled(false);
        mMarketDiffCallback = new MarketDiffCallback();
        mOptionalList.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecor dividerItemDecor = new DividerItemDecor(getActivity(), DividerItemDecoration.VERTICAL);
        mOptionalList.addItemDecoration(dividerItemDecor);
        mOptionalAdapter = new OptionalAdapter(getActivity(), new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                umengEventCount(UmengCountEventId.MARKET0006);
                if (getParentFragment() != null && getParentFragment() instanceof MarketFragment
                        && obj instanceof CurrencyPair) {
                    ((MarketFragment) getParentFragment()).openMarketDetailPage((CurrencyPair) obj);
                }
            }
        });
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(mOptionalAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        mOptionalAdapter.setItemTouchHelper(itemTouchHelper);
        mOptionalList.setAdapter(mOptionalAdapter);
        itemTouchHelper.attachToRecyclerView(mOptionalList);

        requestOptionalList();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Preference.get().getOptionalListRefresh()) {
            requestOptionalList();
            Preference.get().setOptionalListRefresh(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mOptionalAdapter != null) {
            mOptionalAdapter.setMarketDataListData(mMarketDataList);
            requestOptionalList();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LOGIN && resultCode == Activity.RESULT_OK) { // 登录，刷新自选列表
            requestOptionalList();
        }
    }

    public void setMarketDataList(Map<String, MarketData> marketDataList) {

        if (mOptionalAdapter != null && getUserVisibleHint()) {
            mMarketDiffCallback.setOldList(mOptionalAdapter.getPairList());
            mMarketDiffCallback.setNewList(mOptionalAdapter.getPairList());
            mMarketDiffCallback.setMarketMap(marketDataList);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(mMarketDiffCallback);
            mOptionalAdapter.setMarketDataListData(mMarketDataList);
            diffResult.dispatchUpdatesTo(mOptionalAdapter);
        }

        mMarketDataList = marketDataList;

        mMarketDataList = marketDataList;
        if (getUserVisibleHint() && mOptionalAdapter != null) {
            mOptionalAdapter.setMarketDataList(mMarketDataList);
        }
    }

    public void requestOptionalList() {
        Apic.getOptionalList().tag(TAG)
                .callback(new Callback4Resp<Resp<List<CurrencyPair>>, List<CurrencyPair>>() {
                    @Override
                    protected void onRespData(List<CurrencyPair> data) {
                        mOptionalAdapter.setPairList(data);
                        showOptionalEmptyView(data);
                        getKTrendData(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopFreshOrLoadAnimation();
                    }
                }).fireFreely();
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
        if (mOptionalAdapter != null) {
            mOptionalAdapter.setKTrendListMap(data);
            mOptionalAdapter.notifyDataSetChanged();
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

    private void showOptionalEmptyView(List<CurrencyPair> data) {
        if (data.isEmpty()) {
            mOptionalEmptyView.setVisibility(View.VISIBLE);
            mOptionalList.setVisibility(View.GONE);
        } else {
            mOptionalEmptyView.setVisibility(View.GONE);
            mOptionalList.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.addOptional)
    public void onViewClicked() {
        if (LocalUser.getUser().isLogin()) {
            if (getParentFragment() != null && getParentFragment() instanceof MarketFragment) {
                ((MarketFragment) getParentFragment()).openSearchPage();
            }
        } else {
            Launcher.with(this, LoginActivity.class)
                    .execute(this, REQ_CODE_LOGIN);
        }
    }

    public void setEditMode(boolean editMode) {
        mOptionalAdapter.setEditMode(editMode);
    }

    public void requestUpdateOptionalSort() {
        for (int i = 0; i < mOptionalAdapter.mPairList.size(); i++) {
            mOptionalAdapter.mPairList.get(i).setSort(i + 1);
        }
        String jsonArray = new Gson().toJson(mOptionalAdapter.mPairList);
        Apic.updateOptionalList(jsonArray).tag(TAG).fireFreely();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {
        requestOptionalList();
    }

    @NonNull
    @Override
    public RecyclerView getSwipeTargetView() {
        return mOptionalList;
    }

    @NonNull
    @Override
    public SwipeToLoadLayout getSwipeToLoadLayout() {
        return mSwipeToLoadLayout;
    }

    @NonNull
    @Override
    public RefreshHeaderView getRefreshHeaderView() {
        return mRefreshHeaderView;
    }

    @NonNull
    @Override
    public LoadMoreFooterView getLoadMoreFooterView() {
        return mLoadMoreFooterView;
    }

    interface ItemTouchHelperAdapter {
        void onItemMove(int fromPosition, int toPosition);
    }

    static class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private ItemTouchHelperAdapter mTouchHelperAdapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter touchHelperAdapter) {
            mTouchHelperAdapter = touchHelperAdapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            mTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }
    }

    static class OptionalAdapter extends RecyclerView.Adapter<OptionalAdapter.ViewHolder>
            implements ItemTouchHelperAdapter {

        private OnRVItemClickListener mOnRVItemClickListener;
        private List<CurrencyPair> mPairList;
        private Map<String, MarketData> mMarketDataList;
        private Context mContext;
        private boolean mEditMode;
        private ItemTouchHelper mItemTouchHelper;
        private Map<String, List<KTrend>> mKTrendListMap;
        private Set<String> mPairsSet;

        public OptionalAdapter(Context context, OnRVItemClickListener onRVItemClickListener) {
            mOnRVItemClickListener = onRVItemClickListener;
            mPairList = new ArrayList<>();
            mContext = context;
            mPairsSet = new HashSet<>();
        }

        public void setKTrendListMap(HashMap<String, List<KTrend>> KTrendListMap) {
            mKTrendListMap = KTrendListMap;
            mPairsSet.clear();
        }

        public List<CurrencyPair> getPairList() {
            return mPairList;
        }

        public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
            mItemTouchHelper = itemTouchHelper;
        }

        public void setEditMode(boolean editMode) {
            mEditMode = editMode;
            notifyDataSetChanged();
        }

        public void setPairList(List<CurrencyPair> pairList) {
            mPairList = pairList;
            if (!mEditMode) {
                notifyDataSetChanged();
            }
        }

        public void setMarketDataList(Map<String, MarketData> marketDataList) {
            mMarketDataList = marketDataList;
            if (!mEditMode) {
                notifyDataSetChanged();
            }
        }

        public void setMarketDataListData(Map<String, MarketData> marketDataListData) {
            mMarketDataList = marketDataListData;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency_pair, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position);
            } else if (holder instanceof ViewHolder) {
                Bundle bundle = (Bundle) payloads.get(0);
                MarketData marketData = bundle.getParcelable(ExtraKeys.MARKET_DATA);
                CurrencyPair currencyPair = bundle.getParcelable(ExtraKeys.CURRENCY_PAIR);
                holder.bind(currencyPair, marketData, mContext, mEditMode);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            final CurrencyPair pair = mPairList.get(position);
            holder.bind(mPairsSet, mKTrendListMap == null ? null : mKTrendListMap.get(pair.getPairs()), pair, mMarketDataList, mContext, mEditMode);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mEditMode) {
                        mOnRVItemClickListener.onItemClick(v, position, pair);
                    }
                }
            });
            holder.mDragIcon.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mEditMode && event.getAction() == MotionEvent.ACTION_DOWN) {
                        mItemTouchHelper.startDrag(holder);
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPairList.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            Collections.swap(mPairList, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
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
            @BindView(R.id.priceLine)
            LinearLayout mPriceLine;
            @BindView(R.id.dragIcon)
            ImageView mDragIcon;
            @BindView(R.id.chart)
            TimeShareChart mTimeShareChart;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(Set<String> pairsSet, List<KTrend> mKTrends, CurrencyPair pair, Map<String, MarketData> marketDataList, Context context, final boolean editMode) {

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
                    mTradeVolume.setText(context.getString(R.string.volume_24h_x, CurrencyUtils.get24HourVolume(pair.getLastVolume())));
                    mLastPrice.setText(CurrencyUtils.getPrice(pair.getLastPrice(), pair.getPricePoint()));
                    mPriceChange.setText(CurrencyUtils.getPrefixPercent(pair.getUpDropSpeed()));
                    upDropSeed = pair.getUpDropSpeed();
                    if (pair.getUpDropSpeed() < 0) {
                        mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.red));
                    } else {
                        mPriceChange.setTextColor(ContextCompat.getColor(context, R.color.green));
                    }
                }

                if (editMode) {
                    mPriceLine.setVisibility(View.GONE);
                    mDragIcon.setVisibility(View.VISIBLE);
                } else {
                    mPriceLine.setVisibility(View.VISIBLE);
                    mDragIcon.setVisibility(View.GONE);
                }

                if (editMode) {
                    mTimeShareChart.setVisibility(View.GONE);
                } else {
                    mTimeShareChart.setVisibility(View.VISIBLE);
                    if (!pairsSet.contains(pair.getPairs())) {
                        mTimeShareChart.updateData(pair.getPairs(), mKTrends, upDropSeed);
                        pairsSet.add(pair.getPairs());
                    } else {
                        mTimeShareChart.justDraw(pair.getPairs(),upDropSeed);
                    }
                }
            }

            public void bind(CurrencyPair pair, MarketData marketData, Context context, boolean editMode) {
                double upDropSeed;
                mBaseCurrency.setText(pair.getPrefixSymbol().toUpperCase());
                mCounterCurrency.setText(pair.getSuffixSymbol().toUpperCase());
                if (marketData != null) {
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
                    mTradeVolume.setText(context.getString(R.string.volume_24h_x, CurrencyUtils.get24HourVolume(pair.getLastVolume())));
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

                if (editMode) {
                    mPriceLine.setVisibility(View.GONE);
                    mDragIcon.setVisibility(View.VISIBLE);
                } else {
                    mPriceLine.setVisibility(View.VISIBLE);
                    mDragIcon.setVisibility(View.GONE);
                }

            }
        }
    }

    public static class MarketDiffCallback extends DiffUtil.Callback {
        private Map<String, MarketData> mMarketMap;
        private List<CurrencyPair> mOldList;
        private List<CurrencyPair> mNewList;

        public void setOldList(List<CurrencyPair> oldList) {
            mOldList = oldList;
        }

        public void setNewList(List<CurrencyPair> newList) {
            mNewList = newList;
        }

        public void setMarketMap(Map<String, MarketData> marketMap) {
            mMarketMap = marketMap;
        }

        @Override
        public int getOldListSize() {
            return mOldList == null ? 0 : mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList == null ? 0 : mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (oldItemPosition != newItemPosition || mOldList == null || mNewList == null) {
                return false;
            }
            if (mOldList.size() != mNewList.size()) {
                return false;
            }
            CurrencyPair oldItem = mOldList.get(oldItemPosition);
            CurrencyPair newItem = mNewList.get(newItemPosition);
            if (oldItem == null || newItem == null) {
                return false;
            }
            return oldItem.getPairs().equals(newItem.getPairs());
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


            CurrencyPair newItem = mNewList.get(newItemPosition);
            if (newItem == null) {
                return null;
            }

            Bundle payload = new Bundle();
            payload.putParcelable(ExtraKeys.MARKET_DATA, mMarketMap.get(newItem.getPairs()));
            payload.putParcelable(ExtraKeys.CURRENCY_PAIR, newItem);
            return payload;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
