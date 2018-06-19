package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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

    private OnTabSelectedListener mOnTabSelectedListener;

    private LinearLayout mChartsRadioMain;
    private LinearLayout mChartsRadioDropMenu;

    private ImageView mMoreTriangle;
    private TextView mMoreTab;

    private int mSelectedPosition;

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
        mChartsRadioDropMenu = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_charts_radio_dropmenu, this, false);
        addView(mChartsRadioMain);
        addView(mChartsRadioDropMenu);
        mChartsRadioDropMenu.setVisibility(GONE);
        mMoreTriangle = mChartsRadioMain.findViewById(R.id.moreTriangle);
        mMoreTab = mChartsRadioMain.findViewById(R.id.moreTab);

        initChartsRadioMain();
        initChartsRadioDropMenu();

        select(0);
    }

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        mOnTabSelectedListener = onTabSelectedListener;
    }

    private void initChartsRadioDropMenu() {
        final int moreTabIndex = mChartsRadioMain.getChildCount() - 1;
        for (int i = 0; i < mChartsRadioDropMenu.getChildCount(); i++) {
            final int finalI = i;
            mChartsRadioDropMenu.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMoreTriangle.setImageResource(R.drawable.ic_triangle);
                    closeDropMenu();

                    if (v instanceof TextView) {
                        mMoreTab.setText(((TextView) v).getText());
                    }

                    select(moreTabIndex);
                    onTabSelected(moreTabIndex + finalI);
                }
            });
        }
    }

    private void initChartsRadioMain() {
        int moreTabIndex = mChartsRadioMain.getChildCount() - 1;
        for (int i = 0; i < moreTabIndex; i++) {
            final int finalI = i;
            mChartsRadioMain.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChartsRadioDropMenu.getVisibility() != View.GONE) {
                        mMoreTab.setText(R.string.more);
                        closeDropMenu();
                    }
                    if (mMoreTab.isSelected()) {
                        mMoreTab.setText(R.string.more);
                    }

                    select(finalI);
                    onTabSelected(finalI);
                }
            });
        }
        mChartsRadioMain.getChildAt(moreTabIndex)
                .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChartsRadioDropMenu.getVisibility() == View.GONE) {
                    showDropMenu();
                } else {
                    closeDropMenu();
                }
            }
        });
    }

    private void showDropMenu() {
        mMoreTriangle.setImageResource(R.drawable.ic_triangle_highlight);
        Animation expendY = AnimUtils.createExpendY(mChartsRadioDropMenu, 300);
        mChartsRadioDropMenu.startAnimation(expendY);
    }

    private void closeDropMenu() {
        mMoreTriangle.setImageResource(R.drawable.ic_triangle);
        Animation collapseY = AnimUtils.createCollapseY(mChartsRadioDropMenu, 300);
        mChartsRadioDropMenu.startAnimation(collapseY);
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
