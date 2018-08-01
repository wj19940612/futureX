package com.songbai.futurex.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.MainActivity;
import com.songbai.futurex.activity.StatusBarActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.WebActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.home.BFBInfo;
import com.songbai.futurex.model.home.Banner;
import com.songbai.futurex.model.home.EntrustPair;
import com.songbai.futurex.model.home.HomeNews;
import com.songbai.futurex.model.home.PairRiseListBean;
import com.songbai.futurex.utils.Display;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.Network;
import com.songbai.futurex.utils.OnNavigationListener;
import com.songbai.futurex.view.HomeBanner;
import com.songbai.futurex.view.autofit.AutofitTextView;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.PushDestUtils;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.market.MarketSubscriber;
import com.songbai.futurex.websocket.model.MarketData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/29
 */
public class HomeFragment extends BaseFragment implements HomeBanner.OnBannerClickListener {
    private static final int REQ_CODE_MARKET_DETAIL = 95;

    @BindView(R.id.homeBanner)
    HomeBanner mHomeBanner;
    @BindView(R.id.notice)
    TextSwitcher mNotice;
    @BindView(R.id.noticeWrapper)
    LinearLayout mNoticeWrapper;
    @BindView(R.id.entrustPairs)
    RecyclerView mEntrustPairs;
    @BindView(R.id.increaseRank)
    RecyclerView mIncreaseRank;
    @BindView(R.id.nestedScrollView)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.yesterdayAmount)
    AutofitTextView mYesterdayAmount;
    @BindView(R.id.todayAmount)
    AutofitTextView mTodayAmount;
    @BindView(R.id.bfbTotal)
    AutofitTextView mBfbTotal;

    private Unbinder mBind;
    private EntrustPairAdapter mAdapter;
    private IncreaseRankAdapter mIncreaseRankAdapter;
    private ArrayList<HomeNews> mNewsList;
    private boolean mPrepared;
    private int mBannerHeight;
    private Network.NetworkChangeReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                if (mNewsList == null) {
                    requestData();
                }
            }
        }
    };
    private OnNavigationListener mOnNavigationListener;
    private MarketSubscriber mMarketSubscriber;
    private List<EntrustPair.LatelyBean> mLatelyBeans;
    private ArrayList<PairRiseListBean> mPairRiseListBeans;

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEntrustPairs.setNestedScrollingEnabled(false);
        mEntrustPairs.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mAdapter = new EntrustPairAdapter();
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mEntrustPairs.setAdapter(mAdapter);
        mIncreaseRank.setNestedScrollingEnabled(false);
        mIncreaseRank.setLayoutManager(new LinearLayoutManager(getContext()));
        mIncreaseRankAdapter = new IncreaseRankAdapter();
        mIncreaseRankAdapter.setOnItemClickListener(mOnItemClickListener);
        mIncreaseRank.setAdapter(mIncreaseRankAdapter);
        mHomeBanner.setOnBannerClickListener(this);
        requestData();
        mNotice.setTag(0);
        mNotice.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                Context context = HomeFragment.this.getContext();
                TextView tv = new TextView(context);
                tv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextColor(ContextCompat.getColor(context, R.color.text66));
                return tv;
            }
        });
        mPrepared = true;
        mHomeBanner.post(new Runnable() {
            @Override
            public void run() {
                if (mHomeBanner != null) {
                    mBannerHeight = mHomeBanner.getMeasuredHeight();
                }
            }
        });
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                FragmentActivity activity = getActivity();
                if (activity instanceof StatusBarActivity) {
                    ((StatusBarActivity) activity).setStatusBarDarkModeForM(scrollY > mBannerHeight);
                }
                setStatusBarColor(scrollY > mBannerHeight ? R.color.white : android.R.color.transparent);
            }
        });
        Network.registerNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
        initMarketPush();
    }

    private void initMarketPush() {
        mMarketSubscriber = new MarketSubscriber(new OnDataRecListener() {
            @Override
            public void onDataReceive(final String data, final int code, String dest) {
                if (PushDestUtils.isAllMarket(dest)) {
                    new DataParser<Response<Map<String, MarketData>>>(data) {
                        @Override
                        public void onSuccess(Response<Map<String, MarketData>> mapResponse) {
                            Map<String, MarketData> content = mapResponse.getContent();
                            if (content != null) {
                                for (Map.Entry<String, MarketData> dataEntry : content.entrySet()) {
                                    MarketData value = dataEntry.getValue();
                                    if (mLatelyBeans != null) {
                                        for (EntrustPair.LatelyBean latelyBean : mLatelyBeans) {
                                            if (latelyBean.getPairs().equals(dataEntry.getKey())) {
                                                latelyBean.setLastVolume(value.getVolume());
                                                latelyBean.setUpDropSpeed(value.getUpDropSpeed());
                                                latelyBean.setLastPrice(value.getLastPrice());
                                            }
                                        }
                                    }
                                    if (mPairRiseListBeans != null) {
                                        for (PairRiseListBean pairRiseListBean : mPairRiseListBeans) {
                                            if (pairRiseListBean.getPairs().equals(dataEntry.getKey())) {
                                                pairRiseListBean.setVolume(value.getVolume());
                                                pairRiseListBean.setUpDropSpeed(value.getUpDropSpeed());
                                                pairRiseListBean.setLastPrice(value.getLastPrice());
                                            }
                                        }
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mIncreaseRankAdapter.notifyDataSetChanged();
                        }
                    }.parse();
                }
            }
        });
    }

    private void requestData() {
        findBannerList();
        findNewsList(1);
        entrustPairsList();
        bfbInfo();
        indexRiseList();
    }

    private void setStatusBarColor(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getContext(), color));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            requestData();
        }
        mMarketSubscriber.resume();
        mMarketSubscriber.subscribeAll();
        startScheduleJobRightNow(1000);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mPrepared) {
            startScheduleJobRightNow(1000);
            requestData();
        } else {
            stopScheduleJob();
        }
    }

    private void findBannerList() {
        Apic.findBannerList().tag(TAG)
                .callback(new Callback<Resp<ArrayList<Banner>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<Banner>> resp) {
                        mHomeBanner.setHomeAdvertisement(resp.getData());
                    }
                })
                .fire();
    }

    private void findNewsList(int type) {
        Apic.findNewsList(type, 0, 3).tag(TAG)
                .callback(new Callback<Resp<ArrayList<HomeNews>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<HomeNews>> resp) {
                        mNewsList = resp.getData();
                        mNoticeWrapper.setVisibility(View.VISIBLE);
                    }
                })
                .fire();
    }

    private void entrustPairsList() {
        Apic.entrustPairsList(0, 9, "").tag(TAG)
                .callback(new Callback<Resp<EntrustPair>>() {
                    @Override
                    protected void onRespSuccess(Resp<EntrustPair> resp) {
                        mLatelyBeans = resp.getData().getLately();
                        mAdapter.setList(mLatelyBeans);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .fire();
    }

    private void bfbInfo() {
        Apic.bfbInfo().tag(TAG)
                .callback(new Callback<Resp<BFBInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<BFBInfo> resp) {
                        BFBInfo bfbInfo = resp.getData();
                        mYesterdayAmount.setText(getString(R.string.x_bfb, String.valueOf(bfbInfo.getFrontProduce())));
                        mTodayAmount.setText(getString(R.string.x_btc, String.valueOf(bfbInfo.getNowProduce())));
                        mBfbTotal.setText(getString(R.string.x_bfb, String.valueOf(bfbInfo.getVolume())));
                    }
                })
                .fire();
    }

    private void indexRiseList() {
        Apic.indexRiseList().tag(TAG)
                .callback(new Callback<Resp<ArrayList<PairRiseListBean>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<PairRiseListBean>> resp) {
                        mPairRiseListBeans = resp.getData();
                        mIncreaseRankAdapter.setList(mPairRiseListBeans);
                        mIncreaseRankAdapter.notifyDataSetChanged();
                    }
                })
                .fire();
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        if (count % 3 == 0) {
            mHomeBanner.nextAdvertisement();
            if (mNewsList != null && mNewsList.size() > 0) {
                int index = (int) mNotice.getTag();
                final int position = index % mNewsList.size();
                final HomeNews homeNews = mNewsList.get(position);
                mNotice.setText(homeNews.getTitle());
                mNotice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_TITLE, homeNews.getTitle())
                                .putExtra(WebActivity.EX_HTML, homeNews.getContent())
                                .execute();
                    }
                });
                mNotice.setTag(++index);
            }
        }
    }

    @Override
    public void onBannerClick(Banner banner) {
        if (banner.getJumpType() == 1) {
            Launcher.with(getActivity(), WebActivity.class)
                    .putExtra(WebActivity.EX_TITLE, banner.getTitle())
                    .putExtra(WebActivity.EX_URL, banner.getJumpContent())
                    .execute();
        } else if (banner.getJumpType() == 2) {
            Launcher.with(getActivity(), WebActivity.class)
                    .putExtra(WebActivity.EX_HTML, banner.getJumpContent())
                    .putExtra(WebActivity.EX_TITLE, banner.getTitle())
                    .execute();
        }
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onEntrustPairItemClick(EntrustPair.LatelyBean latelyBean) {
            CurrencyPair currencyPair = new CurrencyPair();
            currencyPair.setPairs(latelyBean.getPairs());
            currencyPair.setPrefixSymbol(latelyBean.getPrefixSymbol());
            currencyPair.setSuffixSymbol(latelyBean.getSuffixSymbol());
            currencyPair.setCategory(latelyBean.getCategory());
            currencyPair.setOption(latelyBean.getOption());
            currencyPair.setSort(latelyBean.getSort());
            currencyPair.setUpDropPrice(latelyBean.getUpDropPrice());
            currencyPair.setUpDropSpeed(latelyBean.getUpDropSpeed());
            currencyPair.setLastPrice(latelyBean.getLastPrice());
            currencyPair.setLastVolume(latelyBean.getLastVolume());
            currencyPair.setPricePoint(latelyBean.getPricePoint());
            UniqueActivity.launcher(HomeFragment.this, MarketDetailFragment.class)
                    .putExtra(ExtraKeys.CURRENCY_PAIR, currencyPair)
                    .execute(HomeFragment.this, REQ_CODE_MARKET_DETAIL);
        }

        @Override
        public void onIncreaseRankItemClick(PairRiseListBean pairRiseListBean) {
            CurrencyPair currencyPair = new CurrencyPair();
            currencyPair.setPairs(pairRiseListBean.getPairs());
            currencyPair.setPrefixSymbol(pairRiseListBean.getPrefixSymbol());
            currencyPair.setSuffixSymbol(pairRiseListBean.getSuffixSymbol());
            currencyPair.setUpDropPrice(pairRiseListBean.getUpDropPrice());
            currencyPair.setUpDropSpeed(pairRiseListBean.getUpDropSpeed());
            currencyPair.setLastPrice(pairRiseListBean.getLastPrice());
            currencyPair.setLastVolume(pairRiseListBean.getVolume());
            currencyPair.setPricePoint(pairRiseListBean.getPricePoint());
            UniqueActivity.launcher(HomeFragment.this, MarketDetailFragment.class)
                    .putExtra(ExtraKeys.CURRENCY_PAIR, currencyPair)
                    .execute(HomeFragment.this, REQ_CODE_MARKET_DETAIL);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
        mMarketSubscriber.pause();
        mMarketSubscriber.unSubscribeAll();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_MARKET_DETAIL && resultCode == Activity.RESULT_FIRST_USER) { // 行情详情选择交易
            if (mOnNavigationListener != null) {
                mOnNavigationListener.onNavigation(MainActivity.PAGE_TRADE, data);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
        Network.unregisterNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
    }

    @OnClick(R.id.miningRules)
    public void onViewClicked() {
    }

    private interface OnItemClickListener {
        void onEntrustPairItemClick(EntrustPair.LatelyBean latelyBean);

        void onIncreaseRankItemClick(PairRiseListBean pairRiseListBean);
    }

    class EntrustPairAdapter extends RecyclerView.Adapter {

        private List<EntrustPair.LatelyBean> mList = new ArrayList<>();
        private OnItemClickListener mOnItemClickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_entrus_pair, parent, false);
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    (int) ((Display.getScreenWidth() - Display.dp2Px(26, getResources())) / 3),
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new EntrustPairHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof EntrustPairHolder) {
                ((EntrustPairHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(List<EntrustPair.LatelyBean> list) {
            mList.clear();
            mList.addAll(list);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        class EntrustPairHolder extends RecyclerView.ViewHolder {
            private final View mRootView;
            @BindView(R.id.entrustPairs)
            TextView mEntrustPairs;
            @BindView(R.id.price)
            TextView mPrice;
            @BindView(R.id.upDropSpeed)
            TextView mUpDropSpeed;

            EntrustPairHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mRootView = itemView;
            }

            public void bindData(final EntrustPair.LatelyBean latelyBean) {
                mEntrustPairs.setText(latelyBean.getPairs().replace("_", "/").toUpperCase());
                mPrice.setText(FinanceUtil.formatWithScale(latelyBean.getLastPrice(), latelyBean.getPricePoint()));
                double upDropSpeed = latelyBean.getUpDropSpeed();
                mUpDropSpeed.setSelected(upDropSpeed < 0);
                String dropSpeedNum = FinanceUtil.formatToPercentage(upDropSpeed);
                mUpDropSpeed.setText(upDropSpeed < 0 ? dropSpeedNum : getString(R.string.plus_sign_x, dropSpeedNum));
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onEntrustPairItemClick(latelyBean);
                        }
                    }
                });
            }
        }
    }

    class IncreaseRankAdapter extends RecyclerView.Adapter {

        private ArrayList<PairRiseListBean> mList = new ArrayList<>();
        private OnItemClickListener mOnItemClickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_increase_rank, parent, false);
            return new IncreaseRankHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof IncreaseRankHolder) {
                ((IncreaseRankHolder) holder).bindData(mList.get(position), position);
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(ArrayList<PairRiseListBean> list) {
            mList.clear();
            mList.addAll(list);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        class IncreaseRankHolder extends RecyclerView.ViewHolder {
            private final View mView;
            @BindView(R.id.rank)
            TextView mRank;
            @BindView(R.id.pairName)
            TextView mPairName;
            @BindView(R.id.tradeVolume)
            TextView mTradeVolume;
            @BindView(R.id.price)
            TextView mPrice;
            @BindView(R.id.upDropSpeed)
            TextView mUpDropSpeed;

            IncreaseRankHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                mView = view;
            }

            public void bindData(final PairRiseListBean pairRiseListBean, int position) {
                mRank.setText(String.valueOf(position + 1));
                mRank.setBackgroundResource(position < 3 ? R.drawable.bg_green_left_r10 : R.drawable.bg_blue_left_r10);
                String prefixSymbol = pairRiseListBean.getPrefixSymbol().toUpperCase();
                String nameStr = getString(R.string.x_faction_str_x, prefixSymbol, pairRiseListBean.getSuffixSymbol().toUpperCase());
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(nameStr);
                stringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.text49)),
                        0, prefixSymbol.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.setSpan(new AbsoluteSizeSpan(17, true),
                        0, prefixSymbol.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                        0, prefixSymbol.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mPairName.setText(stringBuilder);
                mTradeVolume.setText(getString(R.string.volume_24h_x, FinanceUtil.formatWithScale(pairRiseListBean.getVolume(), 0)));
                mPrice.setText(FinanceUtil.formatWithScale(pairRiseListBean.getLastPrice(), pairRiseListBean.getPricePoint()));
                double upDropSpeed = pairRiseListBean.getUpDropSpeed();
                mUpDropSpeed.setBackgroundResource(upDropSpeed < 0 ? R.drawable.bg_red_r2 : R.drawable.bg_green_r2);
                String dropSpeedNum = FinanceUtil.formatToPercentage(upDropSpeed);
                mUpDropSpeed.setText(upDropSpeed < 0 ? dropSpeedNum : getString(R.string.plus_sign_x, dropSpeedNum));
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onIncreaseRankItemClick(pairRiseListBean);
                        }
                    }
                });
            }
        }
    }
}
