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
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingBean;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.OtcWarePoster;
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
public class MyAdFragment extends BaseSwipeLoadFragment {

    @BindView(R.id.swipe_target)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    private Unbinder mBind;
    private MyAdAdapter mAdapter;
    private int mPage;
    private boolean isPrepared;
    private boolean isFirstLoad;
    private int mPageSize = 20;

    public static MyAdFragment newInstance() {
        MyAdFragment wantBuyOrSellFragment = new MyAdFragment();
        Bundle bundle = new Bundle();
        wantBuyOrSellFragment.setArguments(bundle);
        return wantBuyOrSellFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        View view = inflater.inflate(R.layout.fragment_my_ad, container, false);
        mBind = ButterKnife.bind(this, view);
        isPrepared = true;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {

        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MyAdAdapter();
        mRecyclerView.setAdapter(mAdapter);
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) {
            lazyLoad();
        }
    }

    private void lazyLoad() {
        if (isPrepared && getUserVisibleHint()) {
            if (isFirstLoad) {
                isFirstLoad = false;
                otcWaresList(mPage, mPageSize);
            }
        }
    }

    private void otcWaresList(int page, int pageSize) {
        Apic.otcWaresList(page, pageSize)
                .callback(new Callback<Resp<PagingBean<OtcWarePoster>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingBean<OtcWarePoster>> resp) {
                        mSwipeToLoadLayout.setLoadMoreEnabled(true);
                        mAdapter.setList(resp.getData().getData());
                        mAdapter.notifyDataSetChanged();
                        stopFreshOrLoadAnimation();
                        mPage++;
                        if (mPage >= resp.getData().getTotal()) {
                            mSwipeToLoadLayout.setLoadMoreEnabled(false);
                        }
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
        otcWaresList(mPage, mPageSize);
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        otcWaresList(mPage, mPageSize);
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

    class MyAdAdapter extends RecyclerView.Adapter {

        private List<OtcWarePoster> mList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_otc_ware_poster, parent, false);
            return new MyAdHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyAdHolder) {
                ((MyAdHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(List<OtcWarePoster> list) {
            if (mPage == 0) {
                mList.clear();
            }
            mList.addAll(list);
        }

        class MyAdHolder extends RecyclerView.ViewHolder {
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

            MyAdHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindData(OtcWarePoster legalCurrencyTrade) {
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
