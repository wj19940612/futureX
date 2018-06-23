package com.songbai.futurex.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.songbai.futurex.R;

public class BadgeTextView extends AppCompatTextView {
    Rect mTextBounds = new Rect();
    RectF mTextBorderRectF = new RectF();
    private Paint mBgPaint;
    private Paint mTextPaint;
    private int mNum;
    float verticalSpacing = 3;
    float horizontalSpacing = 3;
    /**
     * 需要绘制的数字大小
     * 默认大小为12sp
     */
    private float mTextSize = sp2px(12);

    /**
     * 默认背景颜色
     */
    private int mBgColor = 0xfff25b57;

    /**
     * 默认字体颜色
     */
    private int mTextColor = 0xffffffff;
    private int mMinRadius;
    private String mCommentStr = "";

    public BadgeTextView(Context context) {
        this(context, null);
    }

    public BadgeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeTextView);
        mTextColor = typedArray.getColor(R.styleable.BadgeTextView_badgeTextColor, 0xffffffff);
        mBgColor = typedArray.getColor(R.styleable.BadgeTextView_badgeBgColor, ContextCompat.getColor(getContext(), R.color.red));
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.BadgeTextView_badgeTextSize, (int) sp2px(10));
        mNum = typedArray.getInteger(R.styleable.BadgeTextView_badgeTextNum, -1);
        mNum = typedArray.getInteger(R.styleable.BadgeTextView_badgeTextNum, -1);

        //画背景圆形
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);

        //绘制数字
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        typedArray.recycle();
        mMinRadius = dp2px(14);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTextPaint.setTextSize(mTextSize);
        if (mNum > 0) {
            mCommentStr = getCommentStr(mNum);
            mTextPaint.getTextBounds(mCommentStr, 0, mCommentStr.length(), mTextBounds);
            int textWidth = mTextBounds.width();
            int textHeight = mTextBounds.height();
            verticalSpacing = (mMinRadius - textHeight) / 2;
            int toFixSpace = (mMinRadius - textWidth) / 2;
            this.horizontalSpacing = toFixSpace > 0 ?
                    Math.min(toFixSpace, dp2px(3)) : dp2px(3);
            calculateTextContainer(textWidth, textHeight);
            drawBackground(canvas);
            drawText(canvas, textWidth, textHeight);
        }
    }

    private void calculateTextContainer(int textWidth, int textHeight) {
        Drawable drawable = getCompoundDrawables()[1];
        RectF contentRect = new RectF();
        if (drawable != null) {
            Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
            float contentHeight = (drawable.getMinimumHeight() + getCompoundDrawablePadding() - fontMetrics.top + fontMetrics.bottom);
            contentRect.set(
                    getWidth() / 2 - drawable.getMinimumWidth() / 2,
                    getHeight() / 2 - contentHeight / 2,
                    getWidth() / 2 + drawable.getMinimumWidth() / 2,
                    getHeight() / 2 + contentHeight / 2);
        }
        int textContainerWidth = (int) (textWidth + 2 * horizontalSpacing);
        int textContainerHeight = (int) (textHeight + 2 * verticalSpacing);
        if (textContainerWidth < textContainerHeight) {
            textContainerWidth = textContainerHeight;
        }
        mTextBorderRectF.set(
                contentRect.right - textContainerWidth / 2,
                contentRect.top - textContainerHeight * 0.312f,
                contentRect.right + textContainerWidth / 2,
                contentRect.top + textContainerHeight * 0.618f);
    }

    private void drawBackground(Canvas canvas) {
        mBgPaint.reset();
        mBgPaint.setAntiAlias(true);
//        mBgPaint.setColor(Color.WHITE);
//        float borderWidth = dip2px(1);
//        RectF borderRect = new RectF(
//                mTextBorderRectF.left - borderWidth,
//                mTextBorderRectF.top - borderWidth,
//                mTextBorderRectF.right + borderWidth,
//                mTextBorderRectF.bottom + borderWidth);
//        canvas.drawRoundRect(borderRect, borderRect.height() / 2, borderRect.height() / 2, mBgPaint);
        mBgPaint.setColor(mBgColor);
        canvas.drawRoundRect(mTextBorderRectF, mTextBorderRectF.height() / 2, mTextBorderRectF.height() / 2, mBgPaint);
    }

    private void drawText(Canvas canvas, int textWidth, int textHeight) {
        mTextPaint.reset();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        float extra = (mTextPaint.measureText(mCommentStr) - textWidth) / 2;
        canvas.drawText(mCommentStr, mTextBorderRectF.centerX() - textWidth / 2 - extra,
                mTextBorderRectF.centerY() + textHeight / 2, mTextPaint);
    }

    private int dip2px(float dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    /**
     * 设置提醒数字
     *
     * @param mNum
     */
    public void setNum(int mNum) {
        this.mNum = mNum;
        invalidate();
    }

    public String getCommentStr(int commentNum) {
        if (commentNum < 1) {
            return "";
        }
        if (commentNum > 999) {
            return "999+";
        }
        return String.valueOf(commentNum);
    }

    /**
     * 清除提醒数字
     */
    public void clearNum() {
        this.mNum = -1;
        invalidate();
    }

    /**
     * 设置数字颜色
     *
     * @param textColor
     */
    public void setBadgeTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    /**
     * 设置数字背景
     *
     * @param bgColor
     */
    public void setBadgeBgColor(int bgColor) {
        this.mBgColor = bgColor;
    }

    /**
     * 设置提示数字大小，单位sp
     *
     * @param textSize
     */
    public void setBadgeTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    /**
     * 将dp转为px
     *
     * @param dp
     * @return
     */
    private int dp2px(int dp) {
        int ret = 0;
        ret = (int) (dp * getContext().getResources().getDisplayMetrics().density);
        return ret;
    }

    /**
     * 将sp值转换为px值
     *
     * @param sp
     * @return
     */
    private float sp2px(int sp) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }
}
