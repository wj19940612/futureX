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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.MainActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.NumUtils;
import com.songbai.futurex.utils.OnNavigationListener;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.adapter.GroupAdapter;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.RadioHeader;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.market.MarketSubscriber;
import com.songbai.futurex.websocket.model.MarketData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class MarketFragment extends BaseFragment {

    private static final int REQ_CODE_LOGIN = 91;
    private static final int REQ_CODE_MARKET_DETAIL = 95;

    Unbinder unbinder;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.radioHeader)
    RadioHeader mRadioHeader;
    @BindView(R.id.currencyPairList)
    EmptyRecyclerView mCurrencyPairList;
    @BindView(R.id.optionalList)
    EmptyRecyclerView mOptionalList;
    @BindView(R.id.addOptional)
    TextView mAddOptional;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    @BindView(R.id.optionalEmptyView)
    LinearLayout mOptionalEmptyView;

    private CurrencyPairAdapter mCurrencyPairAdapter;
    private OptionalAdapter mOptionalAdapter;

    private Map<String, List<CurrencyPair>> mListMap; // memory cache
    private MarketSubscriber mMarketSubscriber;
    private OnNavigationListener mOnNavigationListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationListener) {
            mOnNavigationListener = (OnNavigationListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseActivity) getActivity()).addStatusBarHeightPaddingTop(mTitleBar);
        mListMap = new HashMap<>();

        mRadioHeader.setOnTabSelectedListener(new RadioHeader.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String content) {
                List<CurrencyPair> pairList = mListMap.get(content);
                if (mRadioHeader.getTabCount() - 1 == position) { // 自选
                    mCurrencyPairList.hideAll(true);
                    mOptionalList.hideAll(false);
                    if (pairList != null) {
                        mOptionalAdapter.setPairList(pairList);
                    }
                    requestOptionalList(content);
                } else {
                    mCurrencyPairList.hideAll(false);
                    mOptionalList.hideAll(true);
                    if (pairList != null) {
                        mCurrencyPairAdapter.setGroupableList(pairList);
                    }
                    requestCurrencyPairList(content);
                }
            }
        });

        initCurrencyPairList();
        initOptionalList();

        if (mRadioHeader.getSelectedPosition() == mRadioHeader.getTabCount() - 1) { // 自选
            mCurrencyPairList.hideAll(true);
            requestOptionalList(mRadioHeader.getSelectTab());
        } else {
            mOptionalList.hideAll(true);
            requestCurrencyPairList(mRadioHeader.getSelectTab());
        }

        mMarketSubscriber = new MarketSubscriber(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code) {
                new DataParser<Response<Map<String, MarketData>>>(data) {
                    @Override
                    public void onSuccess(Response<Map<String, MarketData>> mapResponse) {
                        mCurrencyPairAdapter.setMarketDataList(mapResponse.getContent());
                        mOptionalAdapter.setMarketDataList(mapResponse.getContent());
                    }
                }.parse();
            }
        });

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchPage();
            }
        });
    }

    private void openSearchPage() {
        UniqueActivity.launcher(getActivity(), SearchCurrencyFragment.class).execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LOGIN && resultCode == Activity.RESULT_OK) { // 登录，刷新自选列表
            updateOptionalList();
        }
        if (requestCode == REQ_CODE_MARKET_DETAIL && resultCode == Activity.RESULT_FIRST_USER) { // 行情详情选择交易
            if (mOnNavigationListener != null) {
                mOnNavigationListener.onNavigation(MainActivity.PAGE_TRADE, data);
            }
        }
    }

    public void updateOptionalList() {
        if (mRadioHeader.getSelectedPosition() == mRadioHeader.getTabCount() - 1) {
            requestOptionalList(mRadioHeader.getSelectTab());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMarketSubscriber.resume();
        mMarketSubscriber.subscribeAll();

        if (Preference.get().getOptionalListRefresh()) {
            updateOptionalList();
            Preference.get().setOptionalListRefresh(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMarketSubscriber.pause();
        mMarketSubscriber.unSubscribeAll();
    }

    private void initOptionalList() {
        mOptionalList.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mOptionalList.addItemDecoration(dividerItemDecoration);
        mOptionalAdapter = new OptionalAdapter(getActivity(), new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                if (obj instanceof CurrencyPair) {
                    openMarketDetailPage((CurrencyPair) obj);
                }
            }
        });
        mOptionalList.setAdapter(mOptionalAdapter);
        mOptionalList.setEmptyView(mOptionalEmptyView);
    }

    private void initCurrencyPairList() {
        mCurrencyPairList.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mCurrencyPairList.addItemDecoration(dividerItemDecoration);
        mCurrencyPairAdapter = new CurrencyPairAdapter(getActivity(), new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                if (obj instanceof CurrencyPair) {
                    openMarketDetailPage((CurrencyPair) obj);
                }
            }
        });
        mCurrencyPairList.setAdapter(mCurrencyPairAdapter);
        mCurrencyPairList.setEmptyView(mEmptyView);
    }

    private void openMarketDetailPage(CurrencyPair currencyPair) {
        UniqueActivity.launcher(this, MarketDetailFragment.class)
                .putExtra(ExtraKeys.CURRENCY_PAIR, currencyPair)
                .execute(this, REQ_CODE_MARKET_DETAIL);
    }

    private void requestOptionalList(String content) {
        Apic.getOptionalList().tag(TAG)
                .id(content)
                .callback(new Callback4Resp<Resp<List<CurrencyPair>>, List<CurrencyPair>>() {
                    @Override
                    protected void onRespData(List<CurrencyPair> data) {
                        if (getId().equalsIgnoreCase(mRadioHeader.getSelectTab())) {
                            mListMap.put(getId(), data);
                            mOptionalAdapter.setPairList(data);
                        }
                    }
                }).fireFreely();
    }

    private void requestCurrencyPairList(String counterCurrency) {
        Apic.getCurrencyPairList(counterCurrency).tag(TAG)
                .id(counterCurrency)
                .callback(new Callback4Resp<Resp<List<CurrencyPair>>, List<CurrencyPair>>() {

                    @Override
                    protected void onRespData(List<CurrencyPair> data) {
                        if (getId().equalsIgnoreCase(mRadioHeader.getSelectTab())) {
                            Collections.sort(data);
                            mListMap.put(getId(), data);
                            mCurrencyPairAdapter.setGroupableList(data);

                            if (TextUtils.isEmpty(Preference.get().getDefaultTradePair())) {
                                if (data.isEmpty()) return;
                                Preference.get().setDefaultTradePair(data.get(0).getPairs());
                            } // Save the default trade pair
                        }
                    }
                }).fireFreely();
    }

    @OnClick(R.id.addOptional)
    public void onViewClicked() {
        if (LocalUser.getUser().isLogin()) {
            openSearchPage();
        } else {
            Launcher.with(this, LoginActivity.class)
                    .execute(this, REQ_CODE_LOGIN);
        }
    }

    static class OptionalAdapter extends RecyclerView.Adapter<OptionalAdapter.ViewHolder> {

        private OnRVItemClickListener mOnRVItemClickListener;
        private List<CurrencyPair> mPairList;
        private Map<String, MarketData> mMarketDataList;
        private Context mContext;

        public OptionalAdapter(Context context, OnRVItemClickListener onRVItemClickListener) {
            mOnRVItemClickListener = onRVItemClickListener;
            mPairList = new ArrayList<>();
            mContext = context;
        }

        public void setPairList(List<CurrencyPair> pairList) {
            mPairList = pairList;
            notifyDataSetChanged();
        }

        public List<CurrencyPair> getPairList() {
            return mPairList;
        }

        public void setMarketDataList(Map<String, MarketData> marketDataList) {
            mMarketDataList = marketDataList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency_pair, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            final CurrencyPair pair = mPairList.get(position);
            holder.bind(pair, mMarketDataList, mContext);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRVItemClickListener.onItemClick(v, position, pair);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPairList.size();
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

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(CurrencyPair pair, Map<String, MarketData> marketDataList, Context context) {
                mBaseCurrency.setText(pair.getPrefixSymbol().toUpperCase());
                mCounterCurrency.setText(pair.getSuffixSymbol().toUpperCase());
                if (marketDataList != null) {
                    MarketData marketData = marketDataList.get(pair.getPairs());
                    if (marketData == null) return;
                    mTradeVolume.setText(context.getString(R.string.volume_24h_x, NumUtils.getVolume(marketData.getVolume())));
                    mLastPrice.setText(NumUtils.getPrice(marketData.getLastPrice()));
                    mPriceChange.setText(NumUtils.getPrefixPercent(marketData.getUpDropSpeed()));
                    if (marketData.getUpDropSpeed() < 0) {
                        mPriceChange.setBackgroundResource(R.drawable.bg_red_r2);
                    } else {
                        mPriceChange.setBackgroundResource(R.drawable.bg_green_r2);
                    }
                }
            }
        }
    }

    static class CurrencyPairAdapter extends GroupAdapter<CurrencyPair> {

        private Map<String, MarketData> mMarketDataList;
        private Context mContext;
        private OnRVItemClickListener mOnRVItemClickListener;

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
                    ((ViewHolder) holder).bind(pair, mMarketDataList, mContext);
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

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(CurrencyPair pair, Map<String, MarketData> marketDataList, Context context) {
                mBaseCurrency.setText(pair.getPrefixSymbol().toUpperCase());
                mCounterCurrency.setText(pair.getSuffixSymbol().toUpperCase());
                if (marketDataList != null) {
                    MarketData marketData = marketDataList.get(pair.getPairs());
                    if (marketData == null) return;
                    mTradeVolume.setText(context.getString(R.string.volume_24h_x, NumUtils.getVolume(marketData.getVolume())));
                    mLastPrice.setText(NumUtils.getPrice(marketData.getLastPrice()));
                    mPriceChange.setText(NumUtils.getPrefixPercent(marketData.getUpDropSpeed()));
                    if (marketData.getUpDropSpeed() < 0) {
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
