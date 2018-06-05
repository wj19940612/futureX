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
import com.songbai.futurex.R;
import com.songbai.futurex.swipeload.RecycleViewSwipeLoadActivity;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_history);
        mUnbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(this));
        mSwipeTarget.setAdapter(new RechargeHistoryAdapter());
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

    static class RechargeHistoryAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recharge_history, parent, false);
            return new RechargeHistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        static class RechargeHistoryHolder extends RecyclerView.ViewHolder {
            RechargeHistoryHolder(View view) {
                super(view);
            }
        }
    }
}
