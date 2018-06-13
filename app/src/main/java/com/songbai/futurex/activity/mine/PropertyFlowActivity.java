package com.songbai.futurex.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingResp;
import com.songbai.futurex.model.local.GetUserFinanceFlowData;
import com.songbai.futurex.model.mine.CoinPropertyFlow;
import com.songbai.futurex.swipeload.RecycleViewSwipeLoadActivity;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.view.TitleBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yangguangda
 * @date 2018/6/1
 */
public class PropertyFlowActivity extends RecycleViewSwipeLoadActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.swipe_target)
    RecyclerView mSwipeTarget;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;
    @BindView(R.id.filtrateGroup)
    LinearLayout mFiltrateGroup;
    private GetUserFinanceFlowData mGetUserFinanceFlowData;
    private int mPage = 0;
    private int mPageSize = 20;
    private boolean mAllType;
    private String mCoinType;
    private PropertyFlowAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prorerty_flow);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mAllType = getIntent().getBooleanExtra(ExtraKeys.PROPERTY_FLOW_FILTER_TYPE_ALL, false);
        mCoinType = getIntent().getStringExtra(ExtraKeys.COIN_TYPE);
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/6/4 动画
                mFiltrateGroup.setVisibility(mFiltrateGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PropertyFlowAdapter();
        mSwipeTarget.setAdapter(mAdapter);
        mGetUserFinanceFlowData = new GetUserFinanceFlowData();
        getUserFinanceFlow();
    }

    @Override
    public View getContentView() {
        return mRootView;
    }

    @Override
    public void onLoadMore() {
        getUserFinanceFlow();
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        getUserFinanceFlow();
    }

    private void getUserFinanceFlow() {
        Apic.getUserFinanceFlow(mGetUserFinanceFlowData, mPage, mPageSize)
                .callback(new Callback<PagingResp<CoinPropertyFlow>>() {
                    @Override
                    protected void onRespSuccess(PagingResp<CoinPropertyFlow> resp) {
                        if (resp.getData().getTotal() > mPage) {
                            mPage++;
                        }
                        mAdapter.setList(resp.getList());
                        mAdapter.notifyDataSetChanged();
                        stopFreshOrLoadAnimation();
                    }
                })
                .fire();
    }

    private void showTimePicker() {
        TimePickerView pvTime = new TimePickerBuilder(PropertyFlowActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Toast.makeText(PropertyFlowActivity.this, "title", Toast.LENGTH_SHORT).show();
            }
        }).build();
        pvTime.show();
    }

    class PropertyFlowAdapter extends RecyclerView.Adapter {
        private List<CoinPropertyFlow> mList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_coin_property, parent, false);
            return new PropertyFlowHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PropertyFlowHolder) {
                ((PropertyFlowHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(List<CoinPropertyFlow> list) {
            if (mPage == 0) {
                mList.clear();
            }
            mList.addAll(list);
        }

        class PropertyFlowHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.type)
            TextView mType;
            @BindView(R.id.amount)
            TextView mAmount;
            @BindView(R.id.status)
            TextView mStatus;
            @BindView(R.id.timestamp)
            TextView mTimestamp;

            PropertyFlowHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            void bindData(CoinPropertyFlow coinPropertyFlow) {
                mType.setText(coinPropertyFlow.getFlowType() + "");
                mAmount.setText(String.valueOf(coinPropertyFlow.getValue()));
                mStatus.setText(coinPropertyFlow.getStatus() + "");
                mTimestamp.setText(DateUtil.format(coinPropertyFlow.getCreateTime(), "HH:mm MM/dd"));
            }
        }
    }
}
