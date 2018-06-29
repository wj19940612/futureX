package com.songbai.futurex.view.safety;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.Display;

import java.util.ArrayList;

/**
 * Modified by $nishuideyu$ on 2018/6/29
 * <p>
 * Description: 九宫格view
 * </p>
 */
public class NineGridView extends View {

    protected int mNormalColor;
    protected int mSelectColor;
    protected int mErrorColor;

    protected int mNormalCircleRadius;

    protected ArrayList<LockCircle> mCircleList;
    protected ArrayList<LockCircle> mSelectLockCircleList;

    protected Paint mNormalCirclePaint;


    private int mMeasuredWidth;
    private int mMeasuredHeight;

    public NineGridView(Context context) {
        this(context, null);
    }

    public NineGridView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NineGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        handleAttrs(attrs);

        init();
    }

    private void init() {
        mCircleList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            LockCircle lockCircle = new LockCircle();
            lockCircle.setNineGridCircleStatus(NineGridCircleStatus.NORMAL);
            lockCircle.setPosition(i);
            mCircleList.add(lockCircle);
        }

        mSelectLockCircleList = new ArrayList<>();
        createBigCirclePaint();
    }

    protected void createBigCirclePaint() {
        mNormalCirclePaint = new Paint();
        mNormalCirclePaint.setAntiAlias(true);
        mNormalCirclePaint.setColor(mNormalColor);
        mNormalCirclePaint.setStyle(Paint.Style.FILL);
        mNormalCirclePaint.setDither(true);
    }

    private void handleAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.NineGridView);
        mNormalColor = typedArray.getColor(R.styleable.NineGridView_normalColor, Color.BLACK);
        mSelectColor = typedArray.getColor(R.styleable.NineGridView_selectColor, Color.GREEN);
        mNormalCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.NineGridView_normalCircleRadius, 10);
        mErrorColor = typedArray.getColor(R.styleable.NineGridView_errorColor, Color.RED);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredHeight = getMeasuredHeight();
        mMeasuredWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int hor = mMeasuredWidth / 6;
        int ver = mMeasuredHeight / 6;
        for (int i = 0; i < mCircleList.size(); i++) {
            LockCircle lockCircle = mCircleList.get(i);
            int tempX = (i % 3 + 1) * 2 * hor - hor;
            int tempY = (i / 3 + 1) * 2 * ver - ver;
            lockCircle.setX(tempX);
            lockCircle.setY(tempY);
        }

        for (int i = 0; i < mCircleList.size(); i++) {
            drawCircles(mCircleList.get(i), canvas);
        }
    }

    protected void drawCircles(LockCircle lockCircle, Canvas canvas) {
        switch (lockCircle.getNineGridCircleStatus()) {
            case NORMAL:
                mNormalCirclePaint.setColor(mNormalColor);
                break;
            case SELECT:
                mNormalCirclePaint.setColor(mSelectColor);
                break;
            case ERROR:
                mNormalCirclePaint.setColor(mErrorColor);
                break;
        }
        canvas.drawCircle(lockCircle.getX(), lockCircle.getY(), mNormalCircleRadius, mNormalCirclePaint);
    }


    public void setSelectLockCircleList(ArrayList<LockCircle> selectLockCircleList, NineGridCircleStatus nineGridCircleStatus) {
        if (selectLockCircleList != null) {
            for (LockCircle selectLock : selectLockCircleList) {
                for (LockCircle result : mCircleList) {
                    if (result.getPosition() == selectLock.getPosition()) {
                        result.setNineGridCircleStatus(nineGridCircleStatus);
                    }
                }
            }
        }
    }

    protected int dp2px(float dp) {
        return (int) Display.dp2Px(dp, getResources());
    }

    protected void resetAll() {
        for (LockCircle lockCircle : mCircleList) {
            lockCircle.setNineGridCircleStatus(NineGridCircleStatus.NORMAL);
        }
        mSelectLockCircleList.clear();
        mNormalCirclePaint.setColor(mNormalColor);
    }

    public class LockCircle {

        private int x;

        private int y;

        private int position;

        private NineGridCircleStatus mNineGridCircleStatus;

        public LockCircle(int x, int y, int position, NineGridCircleStatus nineGridCircleStatus) {
            this.x = x;
            this.y = y;
            this.position = position;
            mNineGridCircleStatus = nineGridCircleStatus;
        }

        public LockCircle() {
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getPosition() {
            return position;
        }

        public NineGridCircleStatus getNineGridCircleStatus() {
            return mNineGridCircleStatus;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setNineGridCircleStatus(NineGridCircleStatus nineGridCircleStatus) {
            mNineGridCircleStatus = nineGridCircleStatus;
        }
    }

    public enum NineGridCircleStatus {
        NORMAL,
        SELECT,
        ERROR,
    }
}
