package com.songbai.futurex.swipeload;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.songbai.futurex.R;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2018/1/26.
 * 使用listView作为控件 可以上拉加载，下拉刷新
 * 提供了默认的布局  如果要使用自定义布局 可以重写onCreateView
 */

public abstract class ListViewSwipeLoadFragment extends BaseSwipeLoadFragment<ListView> {

    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview_swipe_load, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @NonNull
    @Override
    public SwipeToLoadLayout getSwipeToLoadLayout() {
        return getView().findViewById(R.id.swipeToLoadLayout);
    }

    @NonNull
    @Override
    public ListView getSwipeTargetView() {
        return getView().findViewById(R.id.swipe_target);
    }


    @NonNull
    @Override
    public LoadMoreFooterView getLoadMoreFooterView() {
        if (getView() != null) {
            return getView().findViewById(R.id.swipe_load_more_footer);
        }
        return null;
    }

    @NonNull
    public RefreshHeaderView getRefreshHeaderView() {
        if (getView() != null) {
            return getView().findViewById(R.id.swipe_refresh_header);
        }
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getSwipeTargetView();
        listView.setOnScrollListener(mOnScrollChangeListener);
    }

    protected AbsListView.OnScrollListener mOnScrollChangeListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (view.getLastVisiblePosition() == view.getCount() - 1
                        && !view.canScrollVertically(1)) {
                    triggerLoadMore();
                }
            }
            onListViewScrollStateChanged(view, scrollState);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            onListViewScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    };

    protected void onListViewScrollStateChanged(AbsListView view, int scrollState) {

    }

    protected void onListViewScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    public void smoothScrollToTop() {
        ListView swipeTargetView = getSwipeTargetView();
        if (swipeTargetView != null) {
            swipeTargetView.setSelection(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBind != null) {
            mBind.unbind();
        }
    }
}
