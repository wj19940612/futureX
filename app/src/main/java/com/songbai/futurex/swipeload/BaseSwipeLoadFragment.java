package com.songbai.futurex.swipeload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.songbai.futurex.R;
import com.songbai.futurex.fragment.BaseFragment;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

/**
 * Created by ${wangJie} on 2018/1/25.
 * 基础的刷新和加载总父类fragment  提供了基础的刷新加载方法
 * 提供了两个直接子类  BaseListViewSwipeLoadFragment  和 BaseRecycleViewSwipeLoadFragment
 * 可以直接继承这两个子类
 * <p>
 */

public abstract class BaseSwipeLoadFragment<T extends View> extends BaseFragment implements
        OnLoadMoreListener, OnRefreshListener, SwipeLoader<T> {

    protected SwipeToLoadLayout mSwipeToLoadLayout;

    protected T mSwipeTargetView;

    protected RefreshHeaderView mRefreshHeaderView;

    protected LoadMoreFooterView mLoadMoreFooterView;


    protected void stopFreshOrLoadAnimation() {
        if (mSwipeToLoadLayout != null) {
            if (mSwipeToLoadLayout.isRefreshing()) {
                mSwipeToLoadLayout.setRefreshing(false);
            }
            if (mSwipeToLoadLayout.isLoadingMore()) {
                mSwipeToLoadLayout.setLoadingMore(false);
            }
        }
    }

    protected void stopFreshAnimation() {
        if (mSwipeToLoadLayout != null) {
            if (mSwipeToLoadLayout.isRefreshing()) {
                mSwipeToLoadLayout.setRefreshing(false);
            }
        }
    }

    protected void stopLoadAnimation() {
        if (mSwipeToLoadLayout != null) {
            if (mSwipeToLoadLayout.isLoadingMore()) {
                mSwipeToLoadLayout.setLoadingMore(false);
            }
        }
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
        if (mLoadMoreFooterView != null) {
            mLoadMoreFooterView.setLoadMoreSuccess(msg);
        }
        stopLoadAnimation();
    }

    public void refreshFailure() {
        refreshComplete(getString(R.string.refresh_fail));
    }

    public void refreshSuccess() {
        refreshComplete(getString(R.string.refresh_complete));
    }

    @Override
    public void refreshComplete(@StringRes int resId) {
        refreshComplete(getString(resId));
    }

    @Override
    public boolean isUseDefaultLoadMoreConditions() {
        return true;
    }

    @Override
    public void refreshComplete(CharSequence msg) {
        if (mRefreshHeaderView != null) {
            mRefreshHeaderView.refreshSuccess(msg);
        }
        stopFreshAnimation();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeTargetView = getSwipeTargetView();
        mSwipeToLoadLayout = getSwipeToLoadLayout();
        mRefreshHeaderView = getRefreshHeaderView();
        mLoadMoreFooterView = getLoadMoreFooterView();

        if (mSwipeToLoadLayout != null) {
            mSwipeToLoadLayout.setOnLoadMoreListener(this);
            mSwipeToLoadLayout.setOnRefreshListener(this);
        }
    }
}
