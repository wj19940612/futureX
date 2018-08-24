package com.songbai.futurex.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.gson.Gson;
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.UmengCountEventId;
import com.songbai.futurex.view.recycler.DividerItemDecor;
import com.songbai.futurex.websocket.model.MarketData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
public class OptionalListFragment extends BaseFragment {

    private static final int REQ_CODE_LOGIN = 91;

    @BindView(R.id.optionalList)
    RecyclerView mOptionalList;
    @BindView(R.id.optionalEmptyView)
    LinearLayout mOptionalEmptyView;
    Unbinder unbinder;

    private OptionalAdapter mOptionalAdapter;
    private Map<String, MarketData> mMarketDataList;

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
            mOptionalAdapter.setMarketDataList(mMarketDataList);
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
                    }
                }).fireFreely();
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

        public OptionalAdapter(Context context, OnRVItemClickListener onRVItemClickListener) {
            mOnRVItemClickListener = onRVItemClickListener;
            mPairList = new ArrayList<>();
            mContext = context;
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

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency_pair, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            final CurrencyPair pair = mPairList.get(position);
            holder.bind(pair, mMarketDataList, mContext, mEditMode);
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

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(CurrencyPair pair, Map<String, MarketData> marketDataList, Context context, final boolean editMode) {
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
                    mTradeVolume.setText(context.getString(R.string.volume_24h_x, CurrencyUtils.get24HourVolume(pair.getLastVolume())));
                    mLastPrice.setText(CurrencyUtils.getPrice(pair.getLastPrice(), pair.getPricePoint()));
                    mPriceChange.setText(CurrencyUtils.getPrefixPercent(pair.getUpDropSpeed()));
                    if (pair.getUpDropSpeed() < 0) {
                        mPriceChange.setBackgroundResource(R.drawable.bg_red_r2);
                    } else {
                        mPriceChange.setBackgroundResource(R.drawable.bg_green_r2);
                    }
                }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
