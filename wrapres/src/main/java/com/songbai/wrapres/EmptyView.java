package com.songbai.wrapres;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2018\2\1 0001.
 */

public class EmptyView extends RelativeLayout {
    TextView mErrorTxt;
    ImageView mIcon;
    Button mBtnRefresh;
    TextView mNoData;

    private Context mContext;
    private OnRefreshButtonClickListener mOnRefreshButtonClickListener;

    public interface OnRefreshButtonClickListener {
        public void onRefreshClick();
    }

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_empty_error, this, true);
        mErrorTxt = (TextView) findViewById(R.id.errorTxt);
        mIcon = (ImageView) findViewById(R.id.icon);
        mBtnRefresh = (Button) findViewById(R.id.btnRefresh);
        mNoData = (TextView) findViewById(R.id.noData);

        mBtnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRefreshButtonClickListener != null) {
                    mOnRefreshButtonClickListener.onRefreshClick();
                }
            }
        });

        setBackgroundColor(ContextCompat.getColor(mContext, R.color.background));
    }

    public void setRefreshButtonClickListener(OnRefreshButtonClickListener onRefreshButtonClickListener) {
        mOnRefreshButtonClickListener = onRefreshButtonClickListener;
    }

    public void setNoNet() {
        mErrorTxt.setVisibility(View.VISIBLE);
        mBtnRefresh.setVisibility(View.VISIBLE);
        mNoData.setVisibility(View.GONE);
    }

    public void setNoData(String tip) {
        mErrorTxt.setVisibility(View.GONE);
        mBtnRefresh.setVisibility(View.GONE);
        mNoData.setVisibility(View.VISIBLE);
        mNoData.setText(tip);
    }
}
