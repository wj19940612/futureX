package com.songbai.futurex.wrapper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.view.recycler.DividerItemDecor;
import com.songbai.futurex.wrapper.Apic;
import com.songbai.futurex.wrapper.MarketDetailActivity;
import com.songbai.wrapres.Banner;
import com.songbai.wrapres.ExtraKeys;
import com.songbai.wrapres.http.ListResp;
import com.songbai.wrapres.model.BannerData;
import com.songbai.wrapres.model.CurrencyP;
import com.songbai.wrapres.model.MarketData;
import com.songbai.wrapres.utils.MarketDataUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/7/11
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class WrapHomeFragment extends BaseFragment {

    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private CurrencyPairAdapter mAdapter;
    private List<CurrencyP> mCurrencyPList;
    private List<MarketData> mMarketDataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wrap_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecor(getActivity(), DividerItemDecor.VERTICAL));
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new CurrencyPairAdapter(new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                if (obj instanceof ListData) {
                    Launcher.with(getActivity(), MarketDetailActivity.class)
                            .putExtra(ExtraKeys.DIGITAL_CURRENCY, ((ListData) obj).mMarketData)
                            .execute();
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        requestCurrencyPairList();
    }

    private void requestCurrencyPairList() {
        Apic.getCurrencyPairList().tag(TAG)
                .callback(new Callback<ListResp<CurrencyP>>() {

                    @Override
                    protected void onRespSuccess(ListResp<CurrencyP> resp) {
                        if (resp != null && resp.getListData() != null) {
                            mCurrencyPList = resp.getListData();
                        }
                        requestMarketListData();
                    }
                }).fireFreely();
    }

    static class ListData {
        public ListData(CurrencyP currencyP, MarketData marketData) {
            mCurrencyP = currencyP;
            mMarketData = marketData;
        }

        public CurrencyP mCurrencyP;
        public MarketData mMarketData;
    }

    private void requestMarketListData() {
        Apic.getMarkListData().tag(TAG)
                .callback(new Callback4Resp<Resp<List<MarketData>>, List<MarketData>>() {
                    @Override
                    protected void onRespData(List<MarketData> data) {
                        updateList(data);
                    }
                }).fireFreely();
    }

    private void updateList(List<MarketData> data) {
        if (mCurrencyPList == null) return;

        List<ListData> listData = new ArrayList<>();
        for (MarketData marketData : data) {
            for (CurrencyP currencyP : mCurrencyPList) {
                if (marketData.getCode().equalsIgnoreCase(currencyP.getCode())) {
                    listData.add(new ListData(currencyP, marketData));
                }
            }
        }
        mAdapter.setListDataList(listData);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            startScheduleJobNext(5000);
        } else {
            stopScheduleJob();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestBannerList();

        if (getUserVisibleHint()) {
            startScheduleJobNext(5000);
        }
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestMarketListData();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getUserVisibleHint()) {
            stopScheduleJob();
        }
    }

    private void requestBannerList() {
        Apic.getBannerList().tag(TAG)
                .callback(new Callback<Resp<List<BannerData>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<BannerData>> resp) {
                        List<BannerData> data = new ArrayList<>();
                        if (!resp.getData().isEmpty()) {
                            int rIndex = resp.getData().size() - 1;
                            data.add(resp.getData().get(rIndex));
                        }
                        mBanner.setHomeAdvertisement(data);
                    }
                }).fireFreely();
    }

    static class CurrencyPairAdapter extends RecyclerView.Adapter<CurrencyPairAdapter.ViewHolder> {

        private OnRVItemClickListener mOnRVItemClickListener;
        private List<ListData> mListDataList;
        private Context mContext;

        public CurrencyPairAdapter(OnRVItemClickListener onRVItemClickListener) {
            mOnRVItemClickListener = onRVItemClickListener;
            mListDataList = new ArrayList<>();
        }

        public void setListDataList(List<ListData> listDataList) {
            mListDataList = listDataList;
            notifyDataSetChanged();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.bourseName)
            TextView mBourseName;
            @BindView(R.id.marketName)
            TextView mMarketName;
            @BindView(R.id.dealNumber)
            TextView mDealNumber;
            @BindView(R.id.numberCurrency)
            TextView mNumberCurrency;
            @BindView(R.id.usPrice)
            TextView mUsPrice;
            @BindView(R.id.yuanPrice)
            TextView mYuanPrice;
            @BindView(R.id.priceChangeRatio)
            TextView mPriceChangeRatio;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(ListData listData, Context context) {
                MarketData marketData = listData.mMarketData;
                mBourseName.setText(marketData.getExchangeCode());
                mMarketName.setText(marketData.getName().toUpperCase());
                mNumberCurrency.setText(marketData.getCurrencyMoney());
                mDealNumber.setText(context.getString(R.string.market_volume,
                        " " + MarketDataUtils.formatVolume(marketData.getLastVolume())));

                if (marketData.getUpDropSpeed() >= 0) {
                    int greenColor = ContextCompat.getColor(context, R.color.greenPrimary);
                    mUsPrice.setTextColor(greenColor);
                    mYuanPrice.setTextColor(greenColor);
                    mPriceChangeRatio.setSelected(true);
                } else {
                    int redColor = ContextCompat.getColor(context, R.color.redPrimary);
                    mUsPrice.setTextColor(redColor);
                    mYuanPrice.setTextColor(redColor);
                    mPriceChangeRatio.setSelected(false);
                }

                mUsPrice.setText(MarketDataUtils.formatDollarWithSign(marketData.getLastPrice()));

                mYuanPrice.setText(MarketDataUtils.formatRmbWithSign(
                        FinanceUtil.multiply(marketData.getLastPrice(), marketData.getRate()).doubleValue()));

                mPriceChangeRatio.setText(MarketDataUtils.percentWithPrefix(marketData.getUpDropSpeed()));

            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_market, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.bind(mListDataList.get(position), mContext);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRVItemClickListener.onItemClick(holder.itemView, position, mListDataList.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mListDataList.size();
        }
    }

}
