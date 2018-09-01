package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TradePercentSelectView extends LinearLayout {

    public static final int FIRST = 25;
    public static final int SECOND = 50;
    public static final int THIRD = 75;
    public static final int FOURTH = 100;

    public static final double DIFF = 0.1;

    @BindView(R.id.firstBtn)
    TextView mFirthBtn;
    @BindView(R.id.secondBtn)
    TextView mSecondBtn;
    @BindView(R.id.thirdBtn)
    TextView mThirdBtn;
    @BindView(R.id.fourthBtn)
    TextView mFourthBtn;

    private OnPercentSelectListener mOnPercentSelectListener;
    private int mSelectPercent;

    public void reset() {
        mFirthBtn.setSelected(false);
        mSecondBtn.setSelected(false);
        mThirdBtn.setSelected(false);
        mFourthBtn.setSelected(false);
    }

    public interface OnPercentSelectListener {
        public void onPercentSelect(int percent, int max);
    }

    public TradePercentSelectView(Context context) {
        this(context, null);
    }

    public TradePercentSelectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TradePercentSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_trade_percent_select, this, true);
        ButterKnife.bind(this);
    }

    public void setOnPercentSelectListener(OnPercentSelectListener onPercentSelectListener) {
        mOnPercentSelectListener = onPercentSelectListener;
    }

    @OnClick({R.id.firstBtn, R.id.secondBtn, R.id.thirdBtn, R.id.fourthBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.firstBtn:
                selectPercent(FIRST);
                break;
            case R.id.secondBtn:
                selectPercent(SECOND);
                break;
            case R.id.thirdBtn:
                selectPercent(THIRD);
                break;
            case R.id.fourthBtn:
                selectPercent(FOURTH);
                break;
        }
    }

    public int getMax() {
        return FOURTH;
    }

    public void updatePercent(double percent) {
//        Log.e("zzz", "updatePercent:" + percent);
        int selectPercent = inDiff(percent);
        if(selectPercent == mSelectPercent) return;

//        Log.e("zzz", "updatePercent:" + percent);

        mFirthBtn.setSelected(false);
        mSecondBtn.setSelected(false);
        mThirdBtn.setSelected(false);
        mFourthBtn.setSelected(false);
        mSelectPercent = -1;
        switch (selectPercent) {
            case FIRST:
                mFirthBtn.setSelected(true);
                mSelectPercent = FIRST;
                break;
            case SECOND:
                mSecondBtn.setSelected(true);
                mSelectPercent = SECOND;
                break;
            case THIRD:
                mThirdBtn.setSelected(true);
                mSelectPercent = THIRD;
                break;
            case FOURTH:
                mFourthBtn.setSelected(true);
                mSelectPercent = FOURTH;
                break;
        }
    }

    /**
    不属于四档范围里的返回-1
     */
    private int inDiff(double percent) {
        if (Math.abs(percent - FIRST) <= DIFF) {
            return FIRST;
        } else if (Math.abs(percent - SECOND) <= DIFF) {
            return SECOND;
        } else if (Math.abs(percent - THIRD) <= DIFF) {
            return THIRD;
        } else if (Math.abs(percent - FOURTH) <= DIFF) {
            return FOURTH;
        }
        return -1;
    }

    private void selectPercent(int selectPercent) {
//        Log.e("zzz", "selectPercent");
        if (mSelectPercent == selectPercent) return;

        mSelectPercent = selectPercent;
        mFirthBtn.setSelected(false);
        mSecondBtn.setSelected(false);
        mThirdBtn.setSelected(false);
        mFourthBtn.setSelected(false);

        switch (selectPercent) {
            case FIRST:
                mFirthBtn.setSelected(true);
                break;
            case SECOND:
                mSecondBtn.setSelected(true);
                break;
            case THIRD:
                mThirdBtn.setSelected(true);
                break;
            case FOURTH:
                mFourthBtn.setSelected(true);
                break;
        }

        if (mOnPercentSelectListener != null) {
            mOnPercentSelectListener.onPercentSelect(selectPercent, FOURTH);
        }
    }
}
