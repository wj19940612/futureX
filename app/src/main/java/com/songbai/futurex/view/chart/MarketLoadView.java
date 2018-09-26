package com.songbai.futurex.view.chart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.songbai.futurex.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Modified by john on 2018/9/26
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class MarketLoadView extends FrameLayout {

    public interface OnViewClickListener {
        void onMarketRefreshClick(View view);
    }

    @BindView(R.id.marketLoadFailureView)
    LinearLayout mMarketLoadFailureView;
    @BindView(R.id.marketNoDataView)
    LinearLayout mMarketNoDataView;
    @BindView(R.id.marketLoadingView)
    ProgressBar mMarketLoadingView;

    private OnViewClickListener mOnViewClickListener;

    public MarketLoadView(@NonNull Context context) {
        super(context);
        init();
    }

    public MarketLoadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_market_load, this, true);

        ButterKnife.bind(this);
    }

    @OnClick({R.id.marketLoadFailureView, R.id.marketNoDataView, R.id.marketLoadingView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.marketLoadFailureView:
                onRefreshButtonClick(view);
                break;
            case R.id.marketNoDataView:
                onRefreshButtonClick(view);
                break;
        }
    }

    public void showMarketLoadFailureView() {
        mMarketLoadFailureView.setVisibility(VISIBLE);
        mMarketLoadingView.setVisibility(INVISIBLE);
        mMarketNoDataView.setVisibility(INVISIBLE);
    }

    public void showMarketNoDataView() {
        mMarketLoadFailureView.setVisibility(INVISIBLE);
        mMarketLoadingView.setVisibility(INVISIBLE);
        mMarketNoDataView.setVisibility(VISIBLE);
    }

    public void showMarketLoadingView() {
        mMarketLoadFailureView.setVisibility(INVISIBLE);
        mMarketLoadingView.setVisibility(VISIBLE);
        mMarketNoDataView.setVisibility(INVISIBLE);
    }

    public void hide() {
        mMarketLoadFailureView.setVisibility(INVISIBLE);
        mMarketLoadingView.setVisibility(INVISIBLE);
        mMarketNoDataView.setVisibility(INVISIBLE);
    }

    private void onRefreshButtonClick(View view) {
        if (mOnViewClickListener != null) {
            mOnViewClickListener.onMarketRefreshClick(view);
        }
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }
}
