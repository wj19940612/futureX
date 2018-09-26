package com.songbai.futurex.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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

    public static final int LEFT_BTN = 0;
    public static final int RIGHT_BTN = 1;

    private CharSequence[] mTabArray;

    @BindView(R.id.leftBtn)
    TextView mLeftBtn;
    @BindView(R.id.rightBtn)
    TextView mRightBtn;

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
        mTabArray = typedArray.getTextArray(R.styleable.BuySellSwitcher_tabStrings);
        typedArray.recycle();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_buy_sell_switcher, this, true);
        ButterKnife.bind(this);
        setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_buy_sell_corner));
        mLeftBtn.setSelected(true);

        if(mTabArray!=null && !TextUtils.isEmpty(mTabArray[0])){
            mLeftBtn.setText(mTabArray[0]);
        }

        if(mTabArray!=null && !TextUtils.isEmpty(mTabArray[1])){
            mRightBtn.setText(mTabArray[1]);
        }
    }

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        mOnTabSelectedListener = onTabSelectedListener;
    }

    @OnClick({R.id.leftBtn, R.id.rightBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.leftBtn:
                selectTab(LEFT_BTN);
                break;
            case R.id.rightBtn:
                selectTab(RIGHT_BTN);
                break;
        }
    }

    public void selectTab(int selectedPosition) {
        if (mSelectedPosition == selectedPosition) return;

        mSelectedPosition = selectedPosition;

        if (selectedPosition == LEFT_BTN) {
            mLeftBtn.setSelected(true);
            mRightBtn.setSelected(false);
        } else {
            mLeftBtn.setSelected(false);
            mRightBtn.setSelected(true);
        }

        if (mOnTabSelectedListener != null) {
            mOnTabSelectedListener.onTabSelected(selectedPosition, selectedPosition == LEFT_BTN ? mLeftBtn.getText().toString() : mRightBtn.getText().toString());
        }
    }
}
