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
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.swipeload.BaseSwipeLoadFragment;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setEmptyView(mEmptyView);
        mAdapter = new InviteAwardHistoryAdapter();
        mRecyclerView.setAdapter(mAdapter);
        getAwardHistory();
    }

    private void getAwardHistory() {
        Apic.inviteAward(mPage, mPageSize)
                .callback(new Callback<Resp<PagingWrap<Object>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingWrap<Object>> resp) {
                        mAdapter.notifyDataSetChanged();
                        if (resp.getData().getTotal() - 1 > mPage) {
                            mPage++;
                            mSwipeToLoadLayout.setLoadMoreEnabled(true);
                        } else {
                            mSwipeToLoadLayout.setLoadMoreEnabled(false);
                        }
                        if (resp.getData().getStart() == 0) {
                            mRecyclerView.hideAll(false);
                        }
                    }
                }).fireFreely();
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

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_invite_award_history, parent, false);
            ButterKnife.bind(this, view);
            return new InviteAwardHistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
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

            public void bindData() {

            }
        }
    }
}
