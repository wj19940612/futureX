package com.songbai.futurex.activity.mine;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yangguangda
 * @date 2018/5/31
 */
public class InviteActivity extends BaseActivity {
    @BindView(R.id.rules)
    TextView mRules;
    @BindView(R.id.inviteCode)
    TextView mInviteCode;
    @BindView(R.id.checkDetail)
    TextView mCheckDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.checkDetail, R.id.joinNow, R.id.copy, R.id.inviteBuddies, R.id.createPoster})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.checkDetail:
                boolean b = mRules.getVisibility() == View.GONE;
                Log.e("wtf", b + "aaa");
                if (b) {
                    expand(mRules);
                } else {
                    collapse(mRules);
                }
                break;
            case R.id.joinNow:
                break;
            case R.id.copy:
                break;
            case R.id.inviteBuddies:
                break;
            case R.id.createPoster:
                break;
            default:
        }
    }

    // 展开
    private void expand(final View view) {
        view.setVisibility(View.VISIBLE);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int measuredHeight = view.getMeasuredHeight();
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float precent = animation.getAnimatedFraction();
                int height = (int) (measuredHeight * precent);
                setHeight(view, height);
                if (precent == 1) {
                    mCheckDetail.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_invitation_angle_up, 0);
                    valueAnimator.removeAllUpdateListeners();
                }
            }
        });
        valueAnimator.start();

    }

    private void setHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
        view.requestLayout();
    }

    // 折叠
    private void collapse(final View view) {
        final int measuredHeight = view.getMeasuredHeight();
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float precent = animation.getAnimatedFraction();
                int height = (int) (measuredHeight - measuredHeight * precent);
                setHeight(view, height);
//                动画执行结束的时候，设置View为View.GONE，同时移除监听器
                if (precent == 1) {
                    view.setVisibility(View.GONE);
                    mCheckDetail.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_invitation_angle_down, 0);
                    valueAnimator.removeAllUpdateListeners();
                }
            }
        });
        valueAnimator.start();
    }
}
