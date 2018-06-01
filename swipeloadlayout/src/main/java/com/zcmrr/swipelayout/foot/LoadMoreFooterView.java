package com.zcmrr.swipelayout.foot;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreFooterLayout;
import com.zcmrr.swipelayout.R;

/**
 * Created by ${wangJie} on 2018/1/25.
 */

public class LoadMoreFooterView extends SwipeLoadMoreFooterLayout {

    private TextView tvLoadMore;
    private ImageView ivSuccess;
    private ProgressBar progressBar;

    private int mFooterHeight;

    private CharSequence mLoadMoreCompleteText;


    public LoadMoreFooterView(Context context) {
        this(context, null);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFooterHeight = getResources().getDimensionPixelOffset(R.dimen.load_more_footer_height_twitter);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_load_more_footer, this, false);
        tvLoadMore = view.findViewById(R.id.tvLoadMore);
        ivSuccess = view.findViewById(R.id.ivSuccess);
        progressBar = view.findViewById(R.id.progressbar);
        mLoadMoreCompleteText = getContext().getString(R.string.swipe_load_more);
        addView(view);
    }


    @Override
    public void onPrepare() {
        ivSuccess.setVisibility(GONE);
    }

    @Override
    public void onMove(int y, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            ivSuccess.setVisibility(GONE);
            progressBar.setVisibility(GONE);
//            if (-y >= mFooterHeight) {
//                tvLoadMore.setText(R.string.restart_load_more);
//            } else {
            tvLoadMore.setText(R.string.swipe_load_more);
//            }
        }
    }

    @Override
    public void onLoadMore() {
        tvLoadMore.setText(R.string.load_more);
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
        progressBar.setVisibility(GONE);
        ivSuccess.setVisibility(VISIBLE);
    }

    @Override
    public void onReset() {
        ivSuccess.setVisibility(GONE);
        mLoadMoreCompleteText = "";
    }

    public void setLoadMoreCompleteText(@StringRes int resid) {
        setLoadMoreCompleteText(getContext().getString(resid));
    }

    public void setLoadMoreCompleteText(CharSequence text) {
        mLoadMoreCompleteText = text;
        tvLoadMore.setText(mLoadMoreCompleteText);
    }

    public void setLoadMoreSuccess(CharSequence loadMoreCompleteText) {
        setLoadMoreCompleteText(loadMoreCompleteText);
    }

    public void setLoadMoreSuccess(@StringRes int resid) {
        setLoadMoreCompleteText(getContext().getString(resid));
    }

    public void setLoadMoreFailure(CharSequence loadMoreCompleteText) {
        setLoadMoreCompleteText(loadMoreCompleteText);
    }

    public void setLoadMoreFailure(@StringRes int resId) {
        setLoadMoreCompleteText(getContext().getString(resId));
    }

}
