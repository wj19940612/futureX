package com.songbai.futurex.swipeload;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.songbai.futurex.R;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

/**
 * Created by ${wangJie} on 2018/1/29.
 * 提供的基础的包含一个ListView 的activity ，可直接使用，里面封装了ListView的滚动事件监听和刷新 加载方法
 * 如果该activity包含一个ListView
 * <p>
 * <p>
 * 该类不可直接使用
 */

public abstract class ListSwipeLoadActivity extends BaseSwipeLoadActivity<ListView> {

    /**
     * 根布局的view
     * @return
     */
    @NonNull
    public abstract View getContentView();

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSwipeTargetView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1
                        && !view.canScrollVertically(1)) {
                    triggerLoadMore();
                }
                onListViewScrollStateChanged(view, scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                onListViewScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
    }

    protected void onListViewScrollStateChanged(AbsListView view, int scrollState) {

    }

    protected void onListViewScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @NonNull
    @Override
    public ListView getSwipeTargetView() {
        return getContentView().findViewById(R.id.swipe_target);
    }

    @NonNull
    @Override
    public SwipeToLoadLayout getSwipeToLoadLayout() {
        return getContentView().findViewById(R.id.swipeToLoadLayout);
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
