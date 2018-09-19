package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.InviteAwardHistory;
import com.songbai.futurex.swipeload.BaseSwipeLoadFragment;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/7/31
 */
public class InviteAwardHistoryFragment extends BaseSwipeLoadFragment {
    @BindView(R.id.swipe_target)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    private Unbinder mBind;
    private int mPage = 0;
    private int mPageSize = 20;
    private InviteAwardHistoryAdapter mAdapter;
    private String mWid;

    public static InviteAwardHistoryFragment newInstance() {
        Bundle bundle = new Bundle();
        InviteAwardHistoryFragment inviteAwardHistoryFragment = new InviteAwardHistoryFragment();
        inviteAwardHistoryFragment.setArguments(bundle);
        return inviteAwardHistoryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_award_hisotory, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setEmptyView(mEmptyView);
        mAdapter = new InviteAwardHistoryAdapter();
        mRecyclerView.setAdapter(mAdapter);
        getAwardHistory();
    }

    private void getAwardHistory() {
        Apic.inviteAward(mPage, mPageSize, mWid).tag(TAG)
                .callback(new Callback<Resp<PagingWrap<InviteAwardHistory>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingWrap<InviteAwardHistory>> resp) {
                        if (mRecyclerView != null) {
                            PagingWrap<InviteAwardHistory> pagingWrap = resp.getData();
                            List<InviteAwardHistory> data = pagingWrap.getData();
                            if (data.size() > 0) {
                                InviteAwardHistory inviteAwardHistory = data.get(data.size() - 1);
                                mWid = inviteAwardHistory.getWid();
                            }
                            pagingWrap.setStart(mPage);
                            mAdapter.setList(pagingWrap);
                            mAdapter.notifyDataSetChanged();
                            stopFreshOrLoadAnimation();
                            mRecyclerView.hideAll(false);
                            if (pagingWrap.isLast()) {
                                mSwipeToLoadLayout.setLoadMoreEnabled(false);
                            } else {
                                mPage++;
                                mSwipeToLoadLayout.setLoadMoreEnabled(true);
                            }
                        }
                    }

                    @Override
                    public void onFailure(ReqError reqError) {
                        if (mSwipeToLoadLayout != null) {
                            stopFreshOrLoadAnimation();
                        }
                    }
                }).fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onLoadMore() {
        getAwardHistory();
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        mWid = "";
        getAwardHistory();
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

    class InviteAwardHistoryAdapter extends RecyclerView.Adapter {

        private List<InviteAwardHistory> mList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_invite_award_history, parent, false);
            ButterKnife.bind(this, view);
            return new InviteAwardHistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof InviteAwardHistoryHolder) {
                ((InviteAwardHistoryHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(PagingWrap<InviteAwardHistory> pagingWrap) {
            if (pagingWrap.getStart() == 0) {
                mList.clear();
            }
            mList.addAll(pagingWrap.getData());
        }

        class InviteAwardHistoryHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.timestamp)
            TextView mTimestamp;
            @BindView(R.id.coinType)
            TextView mCoinType;
            @BindView(R.id.volume)
            TextView mVolume;

            public InviteAwardHistoryHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindData(InviteAwardHistory inviteAwardHistory) {
                mTimestamp.setText(DateUtil.format(inviteAwardHistory.getCreateTime(), "yyyy/MM/dd"));
                mCoinType.setText(String.valueOf(inviteAwardHistory.getCoinType().toUpperCase()));
                mVolume.setText(String.valueOf(inviteAwardHistory.getValue()));
            }
        }
    }
}
