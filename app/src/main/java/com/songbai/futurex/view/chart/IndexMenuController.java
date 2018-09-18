package com.songbai.futurex.view.chart;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.ChartsRadio;

/**
 * Modified by john on 2018/9/18
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class IndexMenuController implements ChartsRadio.IndexMenuController {

    public interface OnIndexSelectedListener {
        void onIndexSelected(int indexes);
    }

    private OnIndexSelectedListener mOnIndexSelectedListener;

    private ViewGroup mViewGroup;

    private TextView mMa;
    private TextView mBoll;
    private TextView mMacd;
    private TextView mKdj;
    private TextView mRsi;
    private TextView mWr;

    public interface Indexes {
        int MA = 0;
        int BOLL = 1;
        int MACD = 2;
        int KDJ = 3;
        int RSI = 4;
        int WR = 5;
        int MAIN_HIDE = 6;
        int SUB_HIDE = 7;
    }

    public void setOnIndexSelectedListener(OnIndexSelectedListener onIndexSelectedListener) {
        mOnIndexSelectedListener = onIndexSelectedListener;
    }

    public IndexMenuController(ViewGroup viewGroup) {
        mViewGroup = viewGroup;
        initViews(mViewGroup);
    }

    private void initViews(ViewGroup view) {
        mMa = (TextView) view.findViewById(R.id.ma);
        mBoll = (TextView) view.findViewById(R.id.boll);
        mMacd = (TextView) view.findViewById(R.id.macd);
        mKdj = (TextView) view.findViewById(R.id.kdj);
        mRsi = (TextView) view.findViewById(R.id.rsi);
        mWr = (TextView) view.findViewById(R.id.wr);

        for (int i = 0; i < view.getChildCount(); i++) {
            view.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleViewClick(view);
                }
            });
        }

        reset();
    }

    public void reset() {
        clearMainIndex();
        clearViceIndex();
        mMa.setSelected(true);
    }

    private void handleViewClick(View view) {
        int index = Indexes.MA;
        switch (view.getId()) {
            case R.id.ma:
                clearMainIndex();
                mMa.setSelected(true);
                index = Indexes.MA;
                break;
            case R.id.boll:
                clearMainIndex();
                mBoll.setSelected(true);
                index = Indexes.BOLL;
                break;
            case R.id.macd:
                clearViceIndex();
                mMacd.setSelected(true);
                index = Indexes.MACD;
                break;
            case R.id.kdj:
                clearViceIndex();
                mKdj.setSelected(true);
                index = Indexes.KDJ;
                break;
            case R.id.rsi:
                clearViceIndex();
                mRsi.setSelected(true);
                index = Indexes.RSI;
                break;
            case R.id.wr:
                clearViceIndex();
                mWr.setSelected(true);
                index = Indexes.WR;
                break;
            case R.id.mainHide:
                clearMainIndex();
                index = Indexes.MAIN_HIDE;
                break;
            case R.id.viceHide:
                clearViceIndex();
                index = Indexes.SUB_HIDE;
                break;
            default:
                return;
        }

        if (index >= 0 && mOnIndexSelectedListener != null) {
            mOnIndexSelectedListener.onIndexSelected(index);
        }
    }

    private void clearViceIndex() {
        mMacd.setSelected(false);
        mKdj.setSelected(false);
        mRsi.setSelected(false);
        mWr.setSelected(false);
    }

    private void clearMainIndex() {
        mMa.setSelected(false);
        mBoll.setSelected(false);
    }

    @Override
    public View getView() {
        return mViewGroup;
    }
}
