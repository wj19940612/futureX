package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.AnimUtils;

/**
 * Modified by john on 2018/6/19
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class ChartsRadio extends LinearLayout {

    public interface OnTabSelectedListener {
        void onTabSelected(int position);
    }

    public interface OnIndexSelectedListener {
        void onIndexSelected(int indexes);
    }

    private OnTabSelectedListener mOnTabSelectedListener;

    private LinearLayout mChartsRadioMain;
    private ViewGroup mChartsRadioDropMenu;
    private ViewGroup mIndexesDropMenu;

    private int mMoreTabIndex;
    private TextView mMoreTab;
    private ImageView mMoreTriangle;

    private int mIndexTabIndex;
    private TextView mIndexTab;
    private ImageView mIndexTriangle;

    private int mSelectedPosition;

    private OnIndexSelectedListener mOnIndexSelectedListener;

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
    }

    public ChartsRadio(@NonNull Context context) {
        super(context);
        init();
    }

    public ChartsRadio(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        mChartsRadioMain = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_charts_radio, this, false);
        addView(mChartsRadioMain);

        mMoreTabIndex = mChartsRadioMain.getChildCount() - 2;
        mIndexTabIndex = mChartsRadioMain.getChildCount() - 1;

        mMoreTab = mChartsRadioMain.findViewById(R.id.moreTab);
        mMoreTriangle = mChartsRadioMain.findViewById(R.id.moreTriangle);
        mIndexTab = mChartsRadioMain.findViewById(R.id.indexesTab);
        mIndexTriangle = mChartsRadioMain.findViewById(R.id.indexesTriangle);

        initChartsRadioMain();

        select(0);
    }

    public void performChildClick(int childIndex) {
        if (childIndex >= 0 && childIndex < mChartsRadioMain.getChildCount()) {
            mChartsRadioMain.getChildAt(childIndex).performClick();
        }
    }

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        mOnTabSelectedListener = onTabSelectedListener;
    }

    public void setIndexesDropMenu(ViewGroup indexesDropMenu) {
        mIndexesDropMenu = indexesDropMenu;
        if (mIndexesDropMenu == null) return;

        mMa = (TextView) mIndexesDropMenu.findViewById(R.id.ma);
        mBoll = (TextView) mIndexesDropMenu.findViewById(R.id.boll);
        mMacd = (TextView) mIndexesDropMenu.findViewById(R.id.macd);
        mKdj = (TextView) mIndexesDropMenu.findViewById(R.id.kdj);
        mRsi = (TextView) mIndexesDropMenu.findViewById(R.id.rsi);
        mWr = (TextView) mIndexesDropMenu.findViewById(R.id.wr);
        for (int i = 0; i < mIndexesDropMenu.getChildCount(); i++) {
            mIndexesDropMenu.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleViewClick(view);
                }
            });
        }
    }


    private void handleViewClick(View view) {
        int index = -1;

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
                break;
            case R.id.viceHide:
                clearViceIndex();
                break;
            default:
                return;
        }

        if (index >= 0 && mOnIndexSelectedListener != null) {
            mOnIndexSelectedListener.onIndexSelected(index);
        }

        closeIndexDropMenu();
    }

    private void clearMainIndex() {
        mMa.setSelected(false);
        mBoll.setSelected(false);
    }

    private void clearViceIndex() {
        mMacd.setSelected(false);
        mKdj.setSelected(false);
        mRsi.setSelected(false);
        mWr.setSelected(false);
    }

    public void setChartsRadioDropMenu(ViewGroup chartsRadioDropMenu) {
        mChartsRadioDropMenu = chartsRadioDropMenu;
        if (mChartsRadioDropMenu == null) return;

        for (int i = 0; i < mChartsRadioDropMenu.getChildCount(); i++) {
            final int finalI = i;
            mChartsRadioDropMenu.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMoreTriangle.setImageResource(R.drawable.ic_triangle);
                    closeMoreDropMenu();

                    if (v instanceof ViewGroup && ((ViewGroup) v).getChildAt(0) instanceof TextView) {
                        View childAt = ((ViewGroup) v).getChildAt(0);
                        mMoreTab.setText(((TextView) childAt).getText());
                    }

                    select(mMoreTabIndex);
                    onTabSelected(mMoreTabIndex + finalI);
                }
            });
        }
    }

    private void initChartsRadioMain() {
        for (int i = 0; i < mMoreTabIndex; i++) {
            final int finalI = i;
            mChartsRadioMain.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isMoreDropMenuShow()) {
                        mMoreTab.setText(R.string.more);
                        closeMoreDropMenu();
                    }
                    if (mMoreTab.isSelected()) {
                        mMoreTab.setText(R.string.more);
                    }
                    if (isIndexDropMenuShow()) {
                        closeIndexDropMenu();
                    }

                    select(finalI);
                    onTabSelected(finalI);
                }
            });
        }

        // moreTab
        mChartsRadioMain.getChildAt(mMoreTabIndex).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMoreDropMenuShow()) {
                    closeMoreDropMenu();
                } else {
                    showMoreDropMenu();
                }
            }
        });

        // indexTab
        mChartsRadioMain.getChildAt(mIndexTabIndex).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isIndexDropMenuShow()) {
                    closeIndexDropMenu();
                } else {
                    showIndexDropMenu();
                }
            }
        });
    }

    private boolean isMoreDropMenuShow() {
        if (mChartsRadioDropMenu != null && mChartsRadioDropMenu.getVisibility() == VISIBLE) {
            return true;
        }
        return false;
    }

    private boolean isIndexDropMenuShow() {
        if (mIndexesDropMenu != null && mIndexesDropMenu.getVisibility() == VISIBLE) {
            return true;
        }
        return false;
    }

    private void showMoreDropMenu() {
        if (isIndexDropMenuShow()) {
            closeIndexDropMenu();
        }
        mMoreTriangle.setImageResource(R.drawable.ic_triangle_highlight);
        Animation expendY = AnimUtils.createExpendY(mChartsRadioDropMenu, 300);
        mChartsRadioDropMenu.startAnimation(expendY);
    }

    private void closeMoreDropMenu() {
        mMoreTriangle.setImageResource(R.drawable.ic_triangle);
        Animation collapseY = AnimUtils.createCollapseY(mChartsRadioDropMenu, 300);
        mChartsRadioDropMenu.startAnimation(collapseY);
    }

    private void showIndexDropMenu() {
        if (isMoreDropMenuShow()) {
            closeMoreDropMenu();
        }
        mIndexTriangle.setImageResource(R.drawable.ic_triangle_highlight);
        Animation expendY = AnimUtils.createExpendY(mIndexesDropMenu, 300);
        mIndexesDropMenu.startAnimation(expendY);
    }

    private void closeIndexDropMenu() {
        mIndexTriangle.setImageResource(R.drawable.ic_triangle);
        Animation collapseY = AnimUtils.createCollapseY(mIndexesDropMenu, 300);
        mIndexesDropMenu.startAnimation(collapseY);
    }

    private void onTabSelected(int index) {
        mSelectedPosition = index;
        if (mOnTabSelectedListener != null) {
            mOnTabSelectedListener.onTabSelected(index);
        }
    }

    private void select(int pos) {
        if (pos < 0 || pos >= mChartsRadioMain.getChildCount()) return;

        clearAll();

        mChartsRadioMain.getChildAt(pos).setSelected(true);
    }

    private void clearAll() {
        for (int i = 0; i < mChartsRadioMain.getChildCount(); i++) {
            View child = mChartsRadioMain.getChildAt(i);
            child.setSelected(false);
        }
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }
}
