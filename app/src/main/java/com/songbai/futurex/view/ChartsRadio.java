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

    public interface IndexMenuController {
        View getView();
    }

    private OnTabSelectedListener mOnTabSelectedListener;
    private LinearLayout mChartsRadioMain;
    private ViewGroup mMoreDropMenu;
    private ViewGroup mIndexDropMenu;
    private IndexMenuController mIndexMenuController;

    private int mMoreTabIndex;
    private TextView mMoreTab;
    private ImageView mMoreTriangle;

    private int mIndexTabIndex;
    private ImageView mIndexTriangle;

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
        addView(mChartsRadioMain);

        mMoreTabIndex = mChartsRadioMain.getChildCount() - 2;
        mIndexTabIndex = mChartsRadioMain.getChildCount() - 1;

        mMoreTab = mChartsRadioMain.findViewById(R.id.moreTab);
        mMoreTriangle = mChartsRadioMain.findViewById(R.id.moreTriangle);
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

    public void setIndexMenuController(IndexMenuController indexMenuController) {
        mIndexMenuController = indexMenuController;
        if (mIndexMenuController.getView() instanceof ViewGroup) {
            mIndexDropMenu = (ViewGroup) mIndexMenuController.getView();
        }
    }

    public void setMoreDropMenu(ViewGroup moreDropMenu) {
        mMoreDropMenu = moreDropMenu;
        if (mMoreDropMenu == null) return;

        for (int i = 0; i < mMoreDropMenu.getChildCount(); i++) {
            final int finalI = i;
            mMoreDropMenu.getChildAt(i).setOnClickListener(new OnClickListener() {
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
        if (mMoreDropMenu != null && mMoreDropMenu.getVisibility() == VISIBLE) {
            return true;
        }
        return false;
    }

    private boolean isIndexDropMenuShow() {
        if (mIndexDropMenu != null && mIndexDropMenu.getVisibility() == VISIBLE) {
            return true;
        }
        return false;
    }

    public void showMoreDropMenu() {
        if (isIndexDropMenuShow()) {
            closeIndexDropMenu();
        }
        mMoreTriangle.setImageResource(R.drawable.ic_triangle_highlight);
        Animation expendY = AnimUtils.createExpendY(mMoreDropMenu, 300);
        mMoreDropMenu.startAnimation(expendY);
    }

    public void closeMoreDropMenu() {
        mMoreTriangle.setImageResource(R.drawable.ic_triangle);
        Animation collapseY = AnimUtils.createCollapseY(mMoreDropMenu, 300);
        mMoreDropMenu.startAnimation(collapseY);
    }

    public void showIndexDropMenu() {
        if (isMoreDropMenuShow()) {
            closeMoreDropMenu();
        }
        mIndexTriangle.setImageResource(R.drawable.ic_triangle_highlight);
        Animation expendY = AnimUtils.createExpendY(mIndexDropMenu, 300);
        mIndexDropMenu.startAnimation(expendY);
    }

    public void closeIndexDropMenu() {
        mIndexTriangle.setImageResource(R.drawable.ic_triangle);
        Animation collapseY = AnimUtils.createCollapseY(mIndexDropMenu, 300);
        mIndexDropMenu.startAnimation(collapseY);
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
