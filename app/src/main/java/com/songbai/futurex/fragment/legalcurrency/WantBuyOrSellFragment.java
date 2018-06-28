package com.songbai.futurex.fragment.legalcurrency;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingBean;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.LegalCurrencyTrade;
import com.songbai.futurex.model.local.GetOtcWaresHome;
import com.songbai.futurex.swipeload.BaseSwipeLoadFragment;
import com.songbai.futurex.utils.FinanceUtil;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/6/21
 */
public class WantBuyOrSellFragment extends BaseSwipeLoadFragment {

    @BindView(R.id.swipe_target)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    private Unbinder mBind;
    private WantByAdapter mAdapter;
    private String mCoinType;
    private String mPayCurrency;
    private int mType = 1;
    private GetOtcWaresHome mGetOtcWaresHome;
    private int mPage;
    private boolean isPrepared;
    private boolean isFirstLoad;
    private boolean mPairChanged;

    public static WantBuyOrSellFragment newInstance(int type, String coinType, String payCurrency) {
        WantBuyOrSellFragment wantBuyOrSellFragment = new WantBuyOrSellFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("coinType", coinType);
        bundle.putString("payCurrency", payCurrency);
        wantBuyOrSellFragment.setArguments(bundle);
        return wantBuyOrSellFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        View view = inflater.inflate(R.layout.fragment_want_buy, container, false);
        mBind = ButterKnife.bind(this, view);
        isPrepared = true;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getInt("type", 1);
            mCoinType = arguments.getString("coinType");
            mPayCurrency = arguments.getString("payCurrency");
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new WantByAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mGetOtcWaresHome = new GetOtcWaresHome();
        mGetOtcWaresHome.setPage(mPage);
        mGetOtcWaresHome.setPageSize(20);
        mGetOtcWaresHome.setType(mType);
        mGetOtcWaresHome.setSortName("synthesis");
        mGetOtcWaresHome.setSort("asc");
        mGetOtcWaresHome.setCoinType(mCoinType);
        mGetOtcWaresHome.setPayCurrency(mPayCurrency);
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) {
            lazyLoad();
        }
        if (isVisibleToUser && isPrepared && mPairChanged) {
            otcWaresCommend(mGetOtcWaresHome);
        }
    }

    private void lazyLoad() {
        if (isPrepared && getUserVisibleHint()) {
            if (isFirstLoad) {
                isFirstLoad = false;
                otcWaresCommend(mGetOtcWaresHome);
            }
        }
    }

    public void setRequestParamAndRefresh(String coinType, String payCurrency) {
        mPage = 0;
        mCoinType = coinType;
        mPayCurrency = payCurrency;
        if (mGetOtcWaresHome == null) {
            mGetOtcWaresHome = new GetOtcWaresHome();
            mGetOtcWaresHome.setPageSize(20);
            mGetOtcWaresHome.setType(mType);
            mGetOtcWaresHome.setSortName("synthesis");
            mGetOtcWaresHome.setSort("asc");
        }
        mGetOtcWaresHome.setPage(mPage);
        mGetOtcWaresHome.setCoinType(mCoinType);
        mGetOtcWaresHome.setPayCurrency(mPayCurrency);
        mPairChanged = true;
        if (getUserVisibleHint() && isPrepared && mPairChanged) {
            otcWaresCommend(mGetOtcWaresHome);
        }
    }

    private void otcWaresCommend(GetOtcWaresHome getOtcWaresHome) {
        Apic.otcWaresHome(getOtcWaresHome)
                .callback(new Callback<Resp<PagingBean<LegalCurrencyTrade>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingBean<LegalCurrencyTrade>> resp) {
                        if (mPairChanged) {
                            mPairChanged = false;
                        }
                        mSwipeToLoadLayout.setLoadMoreEnabled(true);
                        mAdapter.setList(resp.getData().getData());
                        mAdapter.notifyDataSetChanged();
                        stopFreshOrLoadAnimation();
                        mPage++;
                        mGetOtcWaresHome.setPage(mPage);
                        if (mPage >= resp.getData().getTotal()) {
                            mSwipeToLoadLayout.setLoadMoreEnabled(false);
                        }
                    }

                    @Override
                    public void onFailure(ReqError reqError) {
                        super.onFailure(reqError);
                        stopFreshOrLoadAnimation();
                    }
                }).fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
        isPrepared = false;
    }

    @Override
    public void onLoadMore() {
        otcWaresCommend(mGetOtcWaresHome);
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        mGetOtcWaresHome.setPage(mPage);
        otcWaresCommend(mGetOtcWaresHome);
    }

    @NonNull
    @Override
    public Object getSwipeTargetView() {
        return mRecyclerView;
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
        return mSwipeLoadMoreFooter;
    }

    class WantByAdapter extends RecyclerView.Adapter {

        private List<LegalCurrencyTrade> mList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_want_buy, parent, false);
            return new WantBuyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof WantBuyHolder) {
                ((WantBuyHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(List<LegalCurrencyTrade> list) {
            if (mPage == 0) {
                mList.clear();
            }
            mList.addAll(list);
        }

        class WantBuyHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.headPortrait)
            ImageView mHeadPortrait;
            @BindView(R.id.certification)
            ImageView mCertification;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.countDealRate)
            TextView mCountDealRate;
            @BindView(R.id.price)
            TextView mPrice;
            @BindView(R.id.sumSection)
            TextView mSumSection;
            @BindView(R.id.ownCount)
            TextView mOwnCount;
            @BindView(R.id.wechatPayIcon)
            ImageView mWechatPayIcon;
            @BindView(R.id.unionPayIcon)
            ImageView mUnionPayIcon;
            @BindView(R.id.aliPayIcon)
            ImageView mAliPayIcon;

            WantBuyHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindData(LegalCurrencyTrade legalCurrencyTrade) {
                GlideApp
                        .with(getContext())
                        .load(legalCurrencyTrade.getUserPortrait())
                        .circleCrop()
                        .into(mHeadPortrait);
                int authStatus = legalCurrencyTrade.getAuthStatus();
                mCertification.setVisibility(authStatus == 1 || authStatus == 2 ? View.VISIBLE : View.GONE);
                mCertification.setImageResource(authStatus == 1 || authStatus == 2 ? R.drawable.ic_primary_star : R.drawable.ic_senior_star);
                mUserName.setText(legalCurrencyTrade.getUsername());
                mPrice.setText(getString(R.string.x_space_x,
                        FinanceUtil.formatWithScale(legalCurrencyTrade.getFixedPrice()),
                        legalCurrencyTrade.getPayCurrency()));
                mSumSection.setText(getString(R.string.amount_limit, String.valueOf(legalCurrencyTrade.getMinTurnover()), String.valueOf(legalCurrencyTrade.getMaxTurnover())));
                mOwnCount.setText(getString(R.string.own_amount, legalCurrencyTrade.getChangeCount()));
                mCountDealRate.setText(getString(R.string.x_done_count_done_rate_x,
                        legalCurrencyTrade.getCountDeal(),
                        FinanceUtil.formatToPercentage(legalCurrencyTrade.getDoneRate())));
                String payInfo = legalCurrencyTrade.getPayInfo();
                mWechatPayIcon.setVisibility(payInfo.contains("wxPay") ? View.VISIBLE : View.GONE);
                mAliPayIcon.setVisibility(payInfo.contains("aliPay") ? View.VISIBLE : View.GONE);
                mUnionPayIcon.setVisibility(payInfo.contains("bankPay") ? View.VISIBLE : View.GONE);
            }
        }
    }
}
