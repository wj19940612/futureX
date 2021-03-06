package com.songbai.futurex.utils;

import android.animation.ValueAnimator;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author yangguangda
 * @date 2018/6/19
 */
public class AnimatorUtil {
    /**
     * 展开一个view
     * 需要监听进度可以使用重载方法{@link #expandVertical(View, OnAnimatorFactionListener)}
     *
     * @param view 被展开的控件
     */
    public static void expandVertical(final View view) {
        expandVertical(view, null);
    }

    public static void expandVertical(final View view, final OnAnimatorFactionListener listener) {
        expandVertical(view, listener, 500);
    }

    public static void expandVertical(final View view, int duration, int viewHeight) {
        final int measuredHeight = viewHeight;
        // Older versions of android (pre API 21) cancel animations for views with a height of 0 so use 1 instead.
        view.getLayoutParams().height = 1;

        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                if (fraction > 0) {
                    view.setVisibility(View.VISIBLE);

                }
                int height = (int) (measuredHeight * fraction);
                setHeight(view, height);
                if (fraction == 1) {
                    valueAnimator.removeAllUpdateListeners();
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * 折叠一个view
     * 需要监听进度可以使用重载方法{@link #collapseVertical(View, OnAnimatorFactionListener)}
     *
     * @param view 被折叠的控件
     */
    public static void collapseVertical(final View view) {
        collapseVertical(view, null);
    }

    public static void collapseVertical(final View view, final OnAnimatorFactionListener listener) {
        collapseVertical(view, listener, 500);
    }

    public static void collapseVertical(final View view, int duration) {
        final int measuredHeight = view.getMeasuredHeight();
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                int height = (int) (measuredHeight * (1 - fraction));
                setHeight(view, height);
                //动画执行结束的时候，设置View为View.GONE，同时移除监听器
                if (fraction > 0.95) {
                    view.setVisibility(View.GONE);
                }
                if (fraction == 1) {
                    valueAnimator.removeAllUpdateListeners();
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * 动画进度监听器
     */
    public interface OnAnimatorFactionListener {
        /**
         * @param fraction 动画进度 [0,1]
         */
        void onFaction(float fraction);
    }

    /**
     * 展开一个view
     * https://stackoverflow.com/questions/4946295/android-expand-collapse-animation/33049412#33049412
     *
     * @param view     被展开的控件
     * @param listener {@link AnimatorUtil.OnAnimatorFactionListener}
     */
    public static void expandVertical(final View view, @Nullable final OnAnimatorFactionListener listener, int duration) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int width = ((View) view.getParent()).getWidth() - view.getPaddingLeft() - view.getPaddingRight() - layoutParams.leftMargin - layoutParams.rightMargin;
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int measuredHeight = view.getMeasuredHeight();
        // Older versions of android (pre API 21) cancel animations for views with a height of 0 so use 1 instead.
        view.getLayoutParams().height = 1;

        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                if (fraction > 0) {
                    view.setVisibility(View.VISIBLE);

                }
                int height = (int) (measuredHeight * fraction);
                setHeight(view, height);
                if (listener != null) {
                    listener.onFaction(fraction);
                }
                if (fraction == 1) {
                    valueAnimator.removeAllUpdateListeners();
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * 折叠一个view
     *
     * @param view     被折叠的控件
     * @param listener {@link AnimatorUtil.OnAnimatorFactionListener}
     */
    public static void collapseVertical(final View view, @Nullable final OnAnimatorFactionListener listener, int duration) {
        final int measuredHeight = view.getMeasuredHeight();
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                int height = (int) (measuredHeight * (1 - fraction));
                setHeight(view, height);
                if (listener != null) {
                    listener.onFaction(fraction);
                }
                //动画执行结束的时候，设置View为View.GONE，同时移除监听器
                if (fraction > 0.95) {
                    view.setVisibility(View.GONE);
                }
                if (fraction == 1) {
                    valueAnimator.removeAllUpdateListeners();
                }
            }
        });
        valueAnimator.start();
    }

    private static void setHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
        view.requestLayout();
    }
}
