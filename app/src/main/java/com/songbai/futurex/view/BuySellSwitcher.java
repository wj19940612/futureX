package com.songbai.futurex.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BuySellSwitcher extends RelativeLayout {

    public static final int BUY = 0;
    public static final int SELL = 1;

    @BindView(R.id.buyBtn)
    TextView mBuyBtn;
    @BindView(R.id.sellBtn)
    TextView mSellBtn;

    private int mSelectedPosition = -1;

    private OnTabSelectedListener mOnTabSelectedListener;

    public interface OnTabSelectedListener {
        void onTabSelected(int position, String content);
    }

    public BuySellSwitcher(Context context) {
        this(context, null);
    }

    public BuySellSwitcher(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BuySellSwitcher(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BuySellSwitcher);

        typedArray.recycle();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_buy_sell_switcher, this, true);
        ButterKnife.bind(this);
        setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_buy_sell_corner));
        mBuyBtn.setSelected(true);
    }

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        mOnTabSelectedListener = onTabSelectedListener;
    }

    @OnClick({R.id.buyBtn, R.id.sellBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buyBtn:
                selectTab(BUY);
                break;
            case R.id.sellBtn:
                selectTab(SELL);
                break;
        }
    }

    public void selectTab(int selectedPosition) {
        if (mSelectedPosition == selectedPosition) return;

        mSelectedPosition = selectedPosition;

        if (selectedPosition == BUY) {
            mBuyBtn.setSelected(true);
            mSellBtn.setSelected(false);
        } else {
            mBuyBtn.setSelected(false);
            mSellBtn.setSelected(true);
        }

        if (mOnTabSelectedListener != null) {
            mOnTabSelectedListener.onTabSelected(selectedPosition, selectedPosition == BUY ? mBuyBtn.getText().toString() : mSellBtn.getText().toString());
        }
    }
}
