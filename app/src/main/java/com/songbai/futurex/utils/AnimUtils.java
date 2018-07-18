package com.songbai.futurex.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

/**
 * 动画工具类，用于创建简单动画
 */
public class AnimUtils {

    public abstract static class AnimEndListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public static Animation createExpendY(final View view, long duration) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        final int targetHeight = view.getMeasuredHeight();
        view.getLayoutParams().height = 0;
        //view.setVisibility(View.VISIBLE);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                if (interpolatedTime > 0 && view.getVisibility() == View.GONE) {
                    view.setVisibility(View.VISIBLE);
                }
                view.getLayoutParams().height = (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(duration);
        return animation;
    }

    public static Animation createCollapseY(final View view, long duration) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        final int targetHeight = view.getMeasuredHeight();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = targetHeight - (int) (targetHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(duration);
        return animation;
    }


    public static Animation createTransYFromParent(long duration) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        animation.setDuration(duration);
        return animation;
    }

    public static Animation createTransYFromParent(long duration, Animation.AnimationListener listener) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        animation.setDuration(duration);
        animation.setAnimationListener(listener);
        return animation;
    }

    public static Animation createTransYFromParent(long duration, long startOffset, Animation.AnimationListener listener) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        animation.setDuration(duration);
        animation.setStartOffset(startOffset);
        animation.setAnimationListener(listener);
        return animation;
    }

    public static Animation createSimpleScaleAnim(float scale, long duration) {
        Animation animation = new ScaleAnimation(1, scale, 1, scale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        animation.setRepeatCount(1); // 重复一次并且是反向执行，即缩小
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

}
