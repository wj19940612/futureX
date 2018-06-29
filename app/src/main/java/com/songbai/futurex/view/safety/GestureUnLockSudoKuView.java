package com.songbai.futurex.view.safety;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.songbai.futurex.R;

/**
 * Modified by $nishuideyu$ on 2018/6/27
 * <p>
 * Description:
 * </p>
 */
public class GestureUnLockSudoKuView extends NineGridView {

    private static final String TAG = "GestureUnLockSudoKuView";

    private int mLinkLineWidth;

    private int mSelectCircleRadius;
    private Paint mSelectCirclePaint;
    private Paint mPathPaint;

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

        mLinkLineWidth = typedArray.getDimensionPixelOffset(R.styleable.GestureUnLockSudoKuView_linkLineHeight, dp2px(1));
        mSelectCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.GestureUnLockSudoKuView_selectCircleRadius, 32);
        typedArray.recycle();
    }

    private void init() {

        mPath = new Path();
        mLinePath = new Path();

        createSelectCirclePaint();
        createPathPaint();
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

    @Override
    protected void resetAll() {
        super.resetAll();
        isShowError = false;
        isUnlocking = false;
        mPath.reset();
        mLinePath.reset();

        mSelectCirclePaint.setColor(mSelectColor);
        mPathPaint.setColor(mSelectColor);
    }

    @Override
    protected void drawCircles(LockCircle lockCircle, Canvas canvas) {
        switch (lockCircle.getNineGridCircleStatus()) {
            case NORMAL:
                canvas.drawCircle(lockCircle.getX(), lockCircle.getY(), mNormalCircleRadius, mNormalCirclePaint);
                break;
            case SELECT:
                canvas.drawCircle(lockCircle.getX(), lockCircle.getY(), mNormalCircleRadius, mNormalCirclePaint);
                canvas.drawCircle(lockCircle.getX(), lockCircle.getY(), mSelectCircleRadius, mSelectCirclePaint);
                break;
            case ERROR:
                canvas.drawCircle(lockCircle.getX(), lockCircle.getY(), mNormalCircleRadius, mNormalCirclePaint);
                canvas.drawCircle(lockCircle.getX(), lockCircle.getY(), mSelectCircleRadius, getErrorSelectCirclePaint());
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
                    lockCircle.setNineGridCircleStatus(NineGridCircleStatus.SELECT);
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
        if (lockCircle != null && lockCircle.getNineGridCircleStatus() != NineGridCircleStatus.SELECT) {
            lockCircle.setNineGridCircleStatus(NineGridCircleStatus.SELECT);
            mRootX = lockCircle.getX();
            mRootY = lockCircle.getY();
            mLinePath.lineTo(mRootX, mRootY);
            mSelectLockCircleList.add(lockCircle);
        }
    }

    @Nullable
    private LockCircle getOuterCircle(int x, int y) {
        for (int i = 0; i < mCircleList.size(); i++) {
            LockCircle circle = mCircleList.get(i);
            if ((x - circle.getX()) * (x - circle.getX()) + (y - circle.getY()) * (y - circle.getY()) <= mSelectCircleRadius * mSelectCircleRadius) {
                if (circle.getNineGridCircleStatus() != NineGridCircleStatus.SELECT) {
                    return circle;
                }
            }
        }
        return null;
    }

    public enum LockStatus {
        NORMAL,
        CHECK,
        ERROR,
    }
}
