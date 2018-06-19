package com.songbai.futurex.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;

/**
 * Modified by john on 2018/6/7
 * <p>
 * Description: 单选头部（支持红点通知，选中后消失）
 */
public class RadioHeader extends LinearLayout {

    private CharSequence[] mTabArray;
    private SparseIntArray mSparseIntArray;

    private Paint mPaint;
    private int mTabInterval;
    private int mPointColor;
    private int mSelectedPosition;
    private boolean mHasMore;
    private CharSequence mTextOfMore;

    private float mRadius;
    private float mRadiusWithNum;
    private float mTextOffset;
    private float mPointMarginLeft;

    private OnTabSelectedListener mOnTabSelectedListener;

    public interface OnTabSelectedListener {
        void onTabSelected(int position, String content);
    }

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        mOnTabSelectedListener = onTabSelectedListener;
    }

    public RadioHeader(Context context) {
        super(context);
        init();
    }

    public RadioHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RadioHeader);

        mTabArray = typedArray.getTextArray(R.styleable.RadioHeader_tabArray);
        mTabInterval = typedArray.getDimensionPixelOffset(R.styleable.RadioHeader_tabInterval, 0);
        mPointColor = typedArray.getColor(R.styleable.RadioHeader_pointColor,
                ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
        mHasMore = typedArray.getBoolean(R.styleable.RadioHeader_hasMore, false);
        mTextOfMore = typedArray.getText(R.styleable.RadioHeader_textOfMore);

        typedArray.recycle();
    }

    private void init() {
        setWillNotDraw(false);
        setOrientation(HORIZONTAL);

        mSparseIntArray = new SparseIntArray();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mRadiusWithNum = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
        mPointMarginLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

        float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9, getResources().getDisplayMetrics());
        mPaint.setTextSize(fontSize);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        mPaint.getFontMetrics(fontMetrics);
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        mTextOffset = fontHeight / 2 - fontMetrics.bottom;

        for (int i = 0; i < mTabArray.length; i++) {
            View view = createTab(mTabArray[i]);
            LinearLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
            if (params == null) {
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            if (i != 0) {
                params.setMargins(mTabInterval, 0, 0, 0);
            }
            addView(view, params);

            final int finalI = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectTab(finalI);
                }
            });
        }

        if (mHasMore) {
            View view = createTab(mTextOfMore, true);
            LinearLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
            if (params == null) {
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            params.setMargins(mTabInterval, 0, 0, 0);
            addView(view, params);
        }

        if (mTabArray.length > 0) {
            mSelectedPosition = 0;
            getChildAt(0).setSelected(true);
        }
    }

    private void selectTab(int position) {
        if (position == mSelectedPosition) return;

        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setSelected(false);
        }
        mSelectedPosition = position;
        getChildAt(mSelectedPosition).setSelected(true);
        onTabSelected(mSelectedPosition, mTabArray[mSelectedPosition]);
    }

    private void onTabSelected(int position, CharSequence title) {
        if (mOnTabSelectedListener != null) {
            mOnTabSelectedListener.onTabSelected(position, title.toString());
        }
    }

    private View createTab(CharSequence sequence) {
        return createTab(sequence, false);
    }

    private View createTab(CharSequence sequence, boolean triangleVisible) {
        View tab = LayoutInflater.from(getContext()).inflate(R.layout.radio_header_tab, this, false);
        TextView text = tab.findViewById(R.id.text);
        text.setText(sequence);
        ImageView triangle = tab.findViewById(R.id.triangle);
        triangle.setVisibility(triangleVisible ? VISIBLE : INVISIBLE);
        return tab;
    }

    public void notify(int index) {
        notify(index, -1);
    }

    public void notify(int index, int number) {
        if (index >= 0 && index < getChildCount()) {
            mSparseIntArray.put(index, number);
            invalidate();
        }
    }

    public String getSelectTab() {
        if (mTabArray != null && mTabArray.length > 0) {
            return mTabArray[mSelectedPosition].toString();
        }
        return null;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public int getTabCount() {
        return getChildCount();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mSparseIntArray.delete(mSelectedPosition);

        for (int i = 0; i < mSparseIntArray.size(); i++) {
            int key = mSparseIntArray.keyAt(i);
            int value = mSparseIntArray.valueAt(i);

            if (key < 0 || key >= getChildCount() || key == mSelectedPosition) continue;

            View tab = getChildAt(key);
            drawNotifyPoint(tab, value, canvas);
        }
    }

    private void drawNotifyPoint(View tab, int value, Canvas canvas) {
        float cx = tab.getRight() + mPointMarginLeft;
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
}
