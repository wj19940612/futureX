package com.songbai.futurex.view.safety;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.Display;

import java.util.ArrayList;

/**
 * Modified by $nishuideyu$ on 2018/6/27
 * <p>
 * Description:
 * </p>
 */
public class GestureUnLockSudoKuView extends View {

    private static final String TAG = "GestureUnLockSudoKuView";


    private int mErrorColor;
    private int mLinkLineWidth;
    private int mNormalCircleRadius;
    private int mSelectCircleRadius;
    private Paint mBigCirclePaint;
    private Paint mSelectCirclePaint;
    private Paint mPathPaint;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private LockCircleStatus mLockCircleStatus;
    private ArrayList<LockCircle> mCircleList;
    private ArrayList<LockCircle> mSelectLockCircleList;

    private boolean isShowError;
    private boolean isUnlocking;

    private Path mLinePath;
    private Path mPath;

    private int mRootX;
    private int mRootY;


    public GestureUnLockSudoKuView(Context context) {
        this(context, null);
    }

    public GestureUnLockSudoKuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureUnLockSudoKuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        handleAttrs(attrs);

        init();
    }

    private void handleAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GestureUnLockSudoKuView);

        mErrorColor = typedArray.getColor(R.styleable.GestureUnLockSudoKuView_errorColor, Color.RED);
        mLinkLineWidth = typedArray.getDimensionPixelOffset(R.styleable.GestureUnLockSudoKuView_linkLineHeight, dp2px(1));
        mNormalCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.GestureUnLockSudoKuView_normalCircleRadius, 76);
        mSelectCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.GestureUnLockSudoKuView_selectCircleRadius, 32);
        typedArray.recycle();
    }

    private void init() {

        mPath = new Path();
        mLinePath = new Path();

        createBigCirclePaint();

        createSelectCirclePaint();

        createPathPaint();

        mLockCircleStatus = LockCircleStatus.NORMAL;

        mCircleList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            LockCircle lockCircle = new LockCircle();
            lockCircle.setLockCircleStatus(LockCircleStatus.NORMAL);
            lockCircle.setPosition(i);
            mCircleList.add(lockCircle);
        }

        mSelectLockCircleList = new ArrayList<>();
    }


    private void createBigCirclePaint() {
        mBigCirclePaint = new Paint();
        mBigCirclePaint.setAntiAlias(true);
        mBigCirclePaint.setColor(mNormalColor);
        mBigCirclePaint.setStyle(Paint.Style.FILL);
        mBigCirclePaint.setDither(true);
    }

    private void createSelectCirclePaint() {
        mSelectCirclePaint = new Paint();
        mSelectCirclePaint.setStyle(Paint.Style.FILL);
        mSelectCirclePaint.setColor(mSelectColor);
        mSelectCirclePaint.setAntiAlias(true);
        mSelectCirclePaint.setDither(true);
    }

    private void createPathPaint() {
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setColor(mSelectColor);
        mPathPaint.setStrokeWidth(mLinkLineWidth);
        mPathPaint.setStrokeCap(Paint.Cap.ROUND);
        mPathPaint.setStrokeJoin(Paint.Join.ROUND);
        mPathPaint.setDither(true);

    }

    public Paint getErrorPathPaint() {
        mPathPaint.setColor(mErrorColor);
        return mPathPaint;
    }

    public Paint getErrorSelectCirclePaint() {
        mSelectCirclePaint.setColor(mErrorColor);
        return mSelectCirclePaint;
    }

    private void resetAll() {
        isShowError = false;
        isUnlocking = false;
        mPath.reset();
        mLinePath.reset();

        for (LockCircle lockCircle : mCircleList) {
            lockCircle.setLockCircleStatus(LockCircleStatus.NORMAL);
        }
        mSelectLockCircleList.clear();

        mSelectCirclePaint.setColor(mSelectColor);
        mBigCirclePaint.setColor(mNormalColor);
        mPathPaint.setColor(mSelectColor);

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

        canvas.drawPath(mPath, mPathPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isShowError) return true;

        int curX = (int) event.getX();
        int curY = (int) event.getY();
        LockCircle lockCircle = getOuterCircle(curX, curY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.resetAll();
                if (lockCircle != null) {
                    mRootX = lockCircle.getX();
                    mRootY = lockCircle.getY();
                    lockCircle.setLockCircleStatus(LockCircleStatus.SELECT);
                    mSelectLockCircleList.add(lockCircle);
                    mLinePath.moveTo(mRootX, mRootY);
                    isUnlocking = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isUnlocking) {
                    mPath.reset();
                    mPath.addPath(mLinePath);
                    mPath.moveTo(mRootX, mRootY);
                    mPath.lineTo(curX, curY);
                    handleMove(lockCircle);
                }
                break;
            case MotionEvent.ACTION_UP:
                isUnlocking = false;
                if (mSelectLockCircleList.size() > 0) {
                    mPath.reset();
                    mPath.addPath(mLinePath);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resetAll();
                            invalidate();
                        }
                    }, 1000);
                }
                break;

        }
        invalidate();
        return true;
    }

    private synchronized void handleMove(LockCircle lockCircle) {
        if (lockCircle != null && lockCircle.getLockCircleStatus() != LockCircleStatus.SELECT) {
            lockCircle.setLockCircleStatus(LockCircleStatus.SELECT);
            mRootX = lockCircle.getX();
            mRootY = lockCircle.getY();
            mLinePath.lineTo(mRootX, mRootY);
            mSelectLockCircleList.add(lockCircle);
        }
    }

    private void drawCircles(LockCircle lockCircle, Canvas canvas) {
        switch (lockCircle.getLockCircleStatus()) {
            case NORMAL:
                canvas.drawCircle(lockCircle.getX(), lockCircle.getY(), mNormalCircleRadius, mBigCirclePaint);
                break;
            case SELECT:
                canvas.drawCircle(lockCircle.getX(), lockCircle.getY(), mNormalCircleRadius, mBigCirclePaint);
                canvas.drawCircle(lockCircle.getX(), lockCircle.getY(), mSelectCircleRadius, mSelectCirclePaint);
                break;
            case ERROR:
                canvas.drawCircle(lockCircle.getX(), lockCircle.getY(), mNormalCircleRadius, mBigCirclePaint);
                canvas.drawCircle(lockCircle.getX(), lockCircle.getY(), mSelectCircleRadius, getErrorSelectCirclePaint());
                break;

        }
    }

    @Nullable
    private LockCircle getOuterCircle(int x, int y) {
        for (int i = 0; i < mCircleList.size(); i++) {
            LockCircle circle = mCircleList.get(i);
            if ((x - circle.getX()) * (x - circle.getX()) + (y - circle.getY()) * (y - circle.getY()) <= mSelectCircleRadius * mSelectCircleRadius) {
                if (circle.getLockCircleStatus() != LockCircleStatus.SELECT) {
                    return circle;
                }
            }
        }
        return null;
    }

    private int dp2px(float dp) {
        return (int) Display.dp2Px(dp, getResources());
    }

    /**
     * 九宫格圆圈的状态
     */
    public enum LockCircleStatus {
        NORMAL,
        SELECT,
        ERROR
    }

    public enum LockStatus {
        NORMAL,
        CHECK,
        ERROR,
    }

    public class LockCircle {

        private int x;

        private int y;

        private int position;

        private LockCircleStatus mLockCircleStatus;

        public LockCircle(int x, int y, int position, LockCircleStatus lockCircleStatus) {
            this.x = x;
            this.y = y;
            this.position = position;
            mLockCircleStatus = lockCircleStatus;
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

        public LockCircleStatus getLockCircleStatus() {
            return mLockCircleStatus;
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

        public void setLockCircleStatus(LockCircleStatus lockCircleStatus) {
            mLockCircleStatus = lockCircleStatus;
        }
    }
}
