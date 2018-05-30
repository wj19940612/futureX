package com.songbai.futurex.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;

/**
 * Modified by john on 2018/5/29
 * <p>
 * Description: 单选头部（支持红点通知，选中后消失）
 * <p>
 * APIs:
 */
public class RadioButtonHeader extends LinearLayout {

    private TabLayout mTabLayout;
    private CharSequence[] mTabArray;
    private SparseIntArray mSparseIntArray;
    private Paint mPaint;
    private float mTabMargin;
    private int mPointColor;

    private float mRadius;
    private float mRadiusWithNum;
    private float mTextOffset;
    private float mPointMarginLeft;

    public RadioButtonHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RadioButtonHeader);

        mTabArray = typedArray.getTextArray(R.styleable.RadioButtonHeader_tabArray);
        mTabMargin = typedArray.getDimension(R.styleable.RadioButtonHeader_tabMargin, 0);
        mPointColor = typedArray.getColor(R.styleable.RadioButtonHeader_pointColor,
                ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));

        typedArray.recycle();
    }

    private void init() {
        setWillNotDraw(false);
        setOrientation(VERTICAL);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSparseIntArray = new SparseIntArray();
        mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mRadiusWithNum = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
        mPointMarginLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9, getResources().getDisplayMetrics());
        mPaint.setTextSize(fontSize);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        mPaint.getFontMetrics(fontMetrics);
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        mTextOffset = fontHeight / 2 - fontMetrics.bottom;

        mTabLayout = (TabLayout) LayoutInflater.from(getContext()).inflate(R.layout.radio_button_header, null);
        addView(mTabLayout);

        for (CharSequence sequence : mTabArray) {
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTab(sequence)));
        }

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            if (i == 0) continue;
            p.setMargins((int) mTabMargin, 0, 0, 0);
            tab.requestLayout();
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mSparseIntArray.delete(mTabLayout.getSelectedTabPosition());
                invalidate();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private View createTab(CharSequence sequence) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.radio_button_header_tab, null);

        TextView textView = view.findViewById(R.id.text);
        textView.setText(sequence);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        view.setLayoutParams(params);

        return view;
    }

    public void notify(int index) {
        notify(index, -1);
    }

    public void notify(int index, int number) {
        if (index >= 0 && index < mTabLayout.getTabCount()) {
            mSparseIntArray.put(index, number);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mSparseIntArray.delete(mTabLayout.getSelectedTabPosition());

        Log.d("Temp", "onDraw: " + mSparseIntArray.size()); // todo remove later

        for (int i = 0; i < mSparseIntArray.size(); i++) {
            int key = mSparseIntArray.keyAt(i);
            int value = mSparseIntArray.valueAt(i);
            int selectedPos = mTabLayout.getSelectedTabPosition();

            if (key < 0 || key >= mTabLayout.getTabCount() || key == selectedPos) continue;

            View tab = getTab(key);
            drawNotifyPoint(tab, value, canvas);
        }
    }

    private void drawNotifyPoint(View tabView, int value, Canvas canvas) {
        float cx = tabView.getRight() + mPointMarginLeft;
        float cy = getHeight() / 2;
        if (value < 0) {
            mPaint.setColor(mPointColor);
            canvas.drawCircle(cx, cy, mRadius, mPaint);
        } else if (value < 9) {
            String text = String.valueOf(value);
            mPaint.setColor(mPointColor);
            canvas.drawCircle(cx, cy, mRadiusWithNum, mPaint);
            float textW = mPaint.measureText(text);
            mPaint.setColor(Color.WHITE);
            canvas.drawText(text, cx - textW / 2, cy + mTextOffset, mPaint);
        } else {
            String text = String.valueOf("9+");
            mPaint.setColor(mPointColor);
            canvas.drawCircle(cx, cy, mRadiusWithNum, mPaint);
            float textW = mPaint.measureText(text);
            mPaint.setColor(Color.WHITE);
            canvas.drawText(text, cx - textW / 2, cy + mTextOffset, mPaint);
        }
    }

    private View getTab(int index) {
        View tabAt = ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(index);
        return tabAt;
    }
}
