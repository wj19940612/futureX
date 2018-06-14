package com.songbai.futurex.fragment.mine;

import android.content.Context;
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
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingResp;
import com.songbai.futurex.model.local.GetUserFinanceFlowData;
import com.songbai.futurex.model.mine.CoinPropertyFlow;
import com.songbai.futurex.swipeload.RecycleViewSwipeLoadActivity;
import com.songbai.futurex.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class RechargeHistoryActivity extends RecycleViewSwipeLoadActivity {
    @BindView(R.id.swipe_target)
    RecyclerView mSwipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @BindView(R.id.rootView)
    LinearLayout mRootView;
    private Unbinder mUnbinder;
    private int mPage;
    private int mPageSize = 20;
    private String mCoinType;
    private RechargeHistoryAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_history);
        mUnbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mCoinType = getIntent().getStringExtra(ExtraKeys.COIN_TYPE);
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RechargeHistoryAdapter();
        mSwipeTarget.setAdapter(mAdapter);
        GetUserFinanceFlowData getUserFinanceFlowData = new GetUserFinanceFlowData();
        getUserFinanceFlowData.setCoinType(mCoinType);
        getUserFinanceFlowData.setFlowType(String.valueOf(1));
        getRechargeFlow(getUserFinanceFlowData, mPage, mPageSize);
    }

    private void getRechargeFlow(GetUserFinanceFlowData getUserFinanceFlowData, int page, int pageSize) {
        Apic.getUserFinanceFlow(getUserFinanceFlowData, page, pageSize)
                .callback(new Callback<PagingResp<CoinPropertyFlow>>() {
                    @Override
                    protected void onRespSuccess(PagingResp<CoinPropertyFlow> resp) {
                        mAdapter.setList(resp.getList());
                    }
                })
                .fire();
    }

    @Override
    public View getContentView() {
        return mRootView;
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    class RechargeHistoryAdapter extends RecyclerView.Adapter {

        private List<CoinPropertyFlow> mList = new ArrayList<>();
        private Context mContext;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_recharge_history, parent, false);
            return new RechargeHistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof RechargeHistoryHolder) {
                ((RechargeHistoryHolder) holder).bindData(mContext, mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(List<CoinPropertyFlow> list) {
            mList.addAll(list);
        }

        class RechargeHistoryHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.rechargeAmount)
            TextView mRechargeAmount;
            @BindView(R.id.timestamp)
            TextView mTimestamp;
            @BindView(R.id.status)
            TextView mStatus;

            RechargeHistoryHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            void bindData(Context context, CoinPropertyFlow coinPropertyFlow) {
                mRechargeAmount.setText(context.getString(R.string.recharge_amount_coin_x, coinPropertyFlow.getValue(), mCoinType));
                mTimestamp.setText(DateUtil.format(coinPropertyFlow.getCreateTime(),"HH:mm MM/dd"));
                int status = coinPropertyFlow.getStatus();
                switch (status) {
                    case 0:
                        break;
                    default:
                }
                mStatus.setText(status);
            }
        }
    }
}
