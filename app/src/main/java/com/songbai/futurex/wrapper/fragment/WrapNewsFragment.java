package com.songbai.futurex.wrapper.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.swipeload.BaseSwipeLoadFragment;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.wrapper.Apic;
import com.songbai.wrapres.EmptyView;
import com.songbai.wrapres.TitleBar;
import com.songbai.wrapres.model.NewsFlash;
import com.songbai.wrapres.utils.SpannableUtil;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/7/11
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class WrapNewsFragment extends BaseSwipeLoadFragment<RecyclerView> {

    private static final int EARLIER_THAN_TIME = 0;
    private static final int LATER_THAN_TIME = 1;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_target)
    RecyclerView mSwipeTarget;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @BindView(R.id.emptyView)
    EmptyView mEmptyView;
    Unbinder unbinder;

    private NewsAdapter mNewsAdapter;
    private long mFirstDataTime;
    private long mLastDataTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wrap_news, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeToLoadLayout.setLoadMoreEnabled(false);
        initRecyclerView();
        requestNewsFlash(mFirstDataTime, LATER_THAN_TIME);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initRecyclerView() {
        mNewsAdapter = new NewsAdapter(getActivity());
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeTarget.setAdapter(mNewsAdapter);
        mEmptyView.setRefreshButtonClickListener(new EmptyView.OnRefreshButtonClickListener() {
            @Override
            public void onRefreshClick() {
                mFirstDataTime = 0;
                requestNewsFlash(mFirstDataTime, LATER_THAN_TIME);
            }
        });
    }

    private void requestNewsFlash(long time, int status) {
        Apic.getNewsFlash(time, status).tag(TAG)
                .callback(new Callback4Resp<Resp<List<NewsFlash>>, List<NewsFlash>>() {
                    @Override
                    protected void onRespData(List<NewsFlash> data) {
                        updateNewsFlashData(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopFreshOrLoadAnimation();
                    }
                }).fire();
    }

    private void updateNewsFlashData(List<NewsFlash> data) {
        int size = data.size();
        if (size == 0 && mNewsAdapter.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        if (size == 0) return;
        if (mFirstDataTime > 0 && data.get(size - 1).getReleaseTime() > mFirstDataTime) {
            mFirstDataTime = data.get(0).getReleaseTime();
            //定时刷新
            Collections.reverse(data);
            for (NewsFlash newsFlash : data) {
                mNewsAdapter.addFirst(newsFlash);
            }
        } else {
            if (mFirstDataTime == 0) {
                //重新刷新
                mFirstDataTime = data.get(0).getReleaseTime();
                mLastDataTime = data.get(size - 1).getReleaseTime();
                mNewsAdapter.clear();
                mNewsAdapter.addAllData(data);
            } else {
                //加载更多
                mLastDataTime = data.get(size - 1).getReleaseTime();
                mNewsAdapter.addAllData(data);
            }
            if (size < 30) {
                mSwipeToLoadLayout.setLoadMoreEnabled(false);
                mNewsAdapter.showFooterView(true);
            } else {
                mSwipeToLoadLayout.setLoadMoreEnabled(true);
                mNewsAdapter.showFooterView(false);
            }
        }
    }

    static class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
        private boolean showFooterView;
        private List<NewsFlash> dataList;
        private Context mContext;

        public NewsAdapter(Context context) {
            super();
            mContext = context;
            dataList = new ArrayList<>();
        }

        public void addAllData(List<NewsFlash> newsList) {
            dataList.addAll(newsList);
            notifyDataSetChanged();
        }

        public void addFirst(NewsFlash newsFlash) {
            dataList.add(0, newsFlash);
            notifyItemInserted(0);
        }

        public void clear() {
            dataList.clear();
            notifyDataSetChanged();
        }

        public void showFooterView(boolean isShow) {
            showFooterView = isShow;
            notifyDataSetChanged();
        }

        public void refresh() {
            notifyDataSetChanged();
        }

        public boolean isEmpty() {
            return dataList.isEmpty();
        }

        public NewsFlash getFirst() {
            return dataList.isEmpty() ? null : dataList.get(0);
        }

        @Override

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_news_flash, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(showFooterView && position == dataList.size() - 1, dataList.get(position), mContext);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.content)
            TextView mContent;
            @BindView(R.id.split)
            View mSplit;
            @BindView(R.id.footer)
            TextView mFooter;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            private void bindDataWithView(boolean showFooterView, final NewsFlash newsFlash, final Context context) {
                if (showFooterView) {
                    mSplit.setVisibility(View.GONE);
                    mFooter.setVisibility(View.VISIBLE);
                } else {
                    mSplit.setVisibility(View.VISIBLE);
                    mFooter.setVisibility(View.GONE);
                }
                mTime.setText(DateUtil.getFormatTime(newsFlash.getReleaseTime()));
                if (newsFlash.isImportant()) {
                    if (TextUtils.isEmpty(newsFlash.getTitle())) {
                        mContent.setText(newsFlash.getContent());
                        mContent.setTextColor(Color.parseColor("#476E92"));
                    } else {
                        mContent.setText(SpannableUtil.mergeTextWithRatioColorBold(newsFlash.getTitle(), newsFlash.getContent(), 1.0f,
                                Color.parseColor("#476E92"), Color.parseColor("#476E92")));
                    }
                } else {
                    if (TextUtils.isEmpty(newsFlash.getTitle())) {
                        mContent.setText(newsFlash.getContent());
                        mContent.setTextColor(Color.parseColor("#476E92"));
                    } else {
                        mContent.setText(SpannableUtil.mergeTextWithRatioColorBold(newsFlash.getTitle(), newsFlash.getContent(), 1.0f,
                                Color.parseColor("#494949"), Color.parseColor("#494949")));
                    }
                }

                mContent.setMaxLines(6);
                mContent.setEllipsize(TextUtils.TruncateAt.END);
                mContent.setOnClickListener(new View.OnClickListener() {
                    boolean flag = true;

                    @Override
                    public void onClick(View v) {
                        if (flag) {
                            flag = false;
                            mContent.setMaxLines(Integer.MAX_VALUE);
                            mContent.setEllipsize(null);
                        } else {
                            flag = true;
                            mContent.setMaxLines(6);
                            mContent.setEllipsize(TextUtils.TruncateAt.END);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onLoadMore() {
        requestNewsFlash(mLastDataTime, EARLIER_THAN_TIME);
    }

    @Override
    public void onRefresh() {
        mFirstDataTime = 0;
        requestNewsFlash(mFirstDataTime, LATER_THAN_TIME);
    }

    @NonNull
    @Override
    public RecyclerView getSwipeTargetView() {
        return mSwipeTarget;
    }

    @NonNull
    @Override
    public SwipeToLoadLayout getSwipeToLoadLayout() {
        return mSwipeToLoadLayout;
    }

    @NonNull
    @Override
    public RefreshHeaderView getRefreshHeaderView() {
        return mRefreshHeaderView;
    }

    @NonNull
    @Override
    public LoadMoreFooterView getLoadMoreFooterView() {
        return mLoadMoreFooterView;
    }
}
