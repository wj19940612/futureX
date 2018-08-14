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

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.InviteSubordinate;
import com.songbai.futurex.swipeload.BaseSwipeLoadFragment;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.autofit.AutofitTextView;
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
public class InviteHistoryFragment extends BaseSwipeLoadFragment {

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
    private InviteHistoryAdapter mAdapter;
    private int mPage;

    public static InviteHistoryFragment newInstance() {
        Bundle bundle = new Bundle();
        InviteHistoryFragment inviteHistory = new InviteHistoryFragment();
        inviteHistory.setArguments(bundle);
        return inviteHistory;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_hisotory, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setEmptyView(mEmptyView);
        mAdapter = new InviteHistoryAdapter();
        mRecyclerView.setAdapter(mAdapter);
        findCommissionOfSubordinate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    private void findCommissionOfSubordinate() {
        Apic.findCommissionOfSubordinate(mPage, 20).tag(TAG)
                .callback(new Callback<Resp<InviteSubordinate>>() {

                    @Override
                    protected void onRespSuccess(Resp<InviteSubordinate> resp) {
                        InviteSubordinate inviteSubordinate = resp.getData();
                        mAdapter.setList(inviteSubordinate);
                        mAdapter.notifyDataSetChanged();
                        stopFreshOrLoadAnimation();
                        mRecyclerView.hideAll(false);
                        if (inviteSubordinate.getTotal() - 1 > mPage) {
                            mPage++;
                            mSwipeToLoadLayout.setLoadMoreEnabled(true);
                        } else {
                            mSwipeToLoadLayout.setLoadMoreEnabled(false);
                        }
                    }

                    @Override
                    public void onFailure(ReqError reqError) {
                        stopFreshOrLoadAnimation();
                    }
                })
                .fire();
    }

    @Override
    public void onLoadMore() {
        findCommissionOfSubordinate();
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        findCommissionOfSubordinate();
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

    class InviteHistoryAdapter extends RecyclerView.Adapter {

        private List<InviteSubordinate.DataBean> mList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_invite_history, parent, false);
            ButterKnife.bind(this, view);
            return new InviteHistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof InviteHistoryHolder) {
                ((InviteHistoryHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(InviteSubordinate inviteSubordinate) {
            if (inviteSubordinate.getStart() == 0) {
                mList.clear();
            }
            mList.addAll(inviteSubordinate.getData());
        }

        class InviteHistoryHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.invitees)
            AutofitTextView mInvitees;
            @BindView(R.id.registerTime)
            AutofitTextView mRegisterTime;
            @BindView(R.id.dealCount)
            AutofitTextView mDealCount;
            @BindView(R.id.totalContribution)
            AutofitTextView mTotalContribution;

            public InviteHistoryHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindData(InviteSubordinate.DataBean dataBean) {
                mInvitees.setText(dataBean.getUsername());
                mRegisterTime.setText(DateUtil.format(dataBean.getRegisterTime(), "yyyy/MM/dd"));
                mDealCount.setText(String.valueOf(dataBean.getDealCount()));
                mTotalContribution.setText(String.valueOf(dataBean.getCommission()));
            }
        }
    }
}
