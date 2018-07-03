package com.songbai.futurex.swipeload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.songbai.futurex.activity.BaseActivity;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

/**
 * Created by ${wangJie} on 2018/1/29.
 * 提供的基础刷新 加载基类
 * 有两个子类 可以直接使用 {@link ListSwipeLoadActivity }和{@link RVSwipeLoadActivity}
 */

public abstract class BaseSwipeLoadActivity<T extends View> extends BaseActivity
        implements SwipeLoader<T>, OnLoadMoreListener, OnRefreshListener {

    protected SwipeToLoadLayout mSwipeToLoadLayout;
    protected T mSwipeTargetView;
    protected RefreshHeaderView mRefreshHeaderView;
    protected LoadMoreFooterView mLoadMoreFooterView;

    protected void stopFreshOrLoadAnimation() {
        stopFreshAnimation();
        stopLoadMoreAnimation();
    }

    protected void stopFreshAnimation() {
        if (mSwipeToLoadLayout != null) {
            if (mSwipeToLoadLayout.isRefreshing()) {
                mSwipeToLoadLayout.setRefreshing(false);
            }
        }
    }

    protected void stopLoadMoreAnimation() {
        if (mSwipeToLoadLayout != null) {
            if (mSwipeToLoadLayout.isLoadingMore()) {
                mSwipeToLoadLayout.setLoadingMore(false);
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setup(getSwipeTargetView(), getSwipeToLoadLayout(), getRefreshHeaderView(), getLoadMoreFooterView());
    }

    @Override
    public void triggerRefresh() {
        if (mSwipeToLoadLayout != null) {
            mSwipeToLoadLayout.setRefreshing(true);
        }
    }

    @Override
    public void triggerLoadMore() {
        if (mSwipeToLoadLayout != null) {
            mSwipeToLoadLayout.setLoadingMore(true);
        }
    }

    @Override
    public void loadMoreComplete(@StringRes int msgRes) {
        loadMoreComplete(getString(msgRes));
    }

    @Override
    public void loadMoreComplete(CharSequence msg) {
//        if (mLoadMoreFooterView != null) {
//            mLoadMoreFooterView.setLoadMoreSuccess(msg);
//        }
        stopLoadMoreAnimation();
    }

    @Override
    public void refreshComplete(@StringRes int resId) {
        refreshComplete(getString(resId));
    }

    @Override
    public void refreshComplete(CharSequence msg) {
//        if (mRefreshHeaderView != null) {
//            mRefreshHeaderView.refreshSuccess(msg);
//        }
        stopFreshAnimation();
    }

    private void setup(T swipeTargetView, SwipeToLoadLayout swipeToLoadLayout,
                       RefreshHeaderView refreshHeaderView, LoadMoreFooterView loadMoreFooterView) {
        mSwipeTargetView = swipeTargetView;
        mSwipeToLoadLayout = swipeToLoadLayout;
        mRefreshHeaderView = refreshHeaderView;
        mLoadMoreFooterView = loadMoreFooterView;

        if (mSwipeToLoadLayout != null) {
            mSwipeToLoadLayout.setOnLoadMoreListener(this);
            mSwipeToLoadLayout.setOnRefreshListener(this);
        }
    }
}
