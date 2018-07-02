package com.songbai.futurex.swipeload;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.songbai.futurex.R;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

/**
 * Created by ${wangJie} on 2018/1/29.
 * 如果使用包含一个recycleView 的activity 可以继承该类
 * 该类封装了 recycleview的上拉加载和滑动事件
 * <p>
 * 该类不可直接使用
 * <p>
 */

public abstract class RVSwipeLoadActivity extends BaseSwipeLoadActivity<RecyclerView> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ButterKnife.bind(this);
//        if (getSwipeTargetView() != null) {
//            getSwipeTargetView().addOnScrollListener(mOnScrollListener);
//        }
    }

    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (getSwipeTargetView() != null) {
            getSwipeTargetView().addOnScrollListener(mOnScrollListener);
        }
    }

    protected void onRecycleViewScrollStateChanged(RecyclerView recyclerView, int newState) {
    }

    protected void onRecycleViewScrolled(RecyclerView recyclerView, int dx, int dy) {
    }

    protected RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (!recyclerView.canScrollVertically(RecyclerView.VERTICAL)) {
                    triggerLoadMore();
                }
            }
            onRecycleViewScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            onRecycleViewScrolled(recyclerView, dx, dy);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getSwipeTargetView() != null) {
            getSwipeTargetView().removeOnScrollListener(mOnScrollListener);
        }
    }

    /**
     * @return 根布局view
     */
    public abstract View getContentView();

    @NonNull
    @Override
    public SwipeToLoadLayout getSwipeToLoadLayout() {
        return getContentView().findViewById(R.id.swipeToLoadLayout);
    }

    @NonNull
    @Override
    public RecyclerView getSwipeTargetView() {
        return getContentView().findViewById(R.id.swipe_target);
    }


    @NonNull
    @Override
    public RefreshHeaderView getRefreshHeaderView() {
        return getContentView().findViewById(R.id.swipe_refresh_header);
    }

    @NonNull
    @Override
    public LoadMoreFooterView getLoadMoreFooterView() {
        return getContentView().findViewById(R.id.swipe_load_more_footer);
    }

}
