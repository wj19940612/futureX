package com.songbai.futurex.wrapper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.AnimUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.SecurityUtil;
import com.songbai.wrapres.ExtraKeys;
import com.songbai.wrapres.TrainingAchievementView;
import com.songbai.wrapres.model.KData;
import com.songbai.wrapres.model.Question;
import com.songbai.wrapres.model.Training;
import com.songbai.wrapres.model.TrainingDetail;
import com.songbai.wrapres.model.TrainingResult;
import com.songbai.wrapres.model.TrainingSubmit;
import com.songbai.wrapres.model.TrainingTarget;
import com.songbai.wrapres.utils.FinanceUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 训练结果页面
 */
public class TrainingResultActivity extends BaseActivity {

    private static final long ANIM_DURATION = 500;
    private static final long ENTER_OFFSET = (long) (0.4f * ANIM_DURATION);

    // Activity enter anim duration is 400ms.
    // And onEnterAnimationComplete is not work when user close screen before activity show completely
    private static final long ANIM_DELAY = 410;

    @BindView(R.id.myGrade)
    TextView mMyGrade;
    @BindView(R.id.failedMessage)
    TextView mFailedMessage;

    TrainingAchievementView[] mAchievementViews;

    @BindView(R.id.retry)
    TextView mRetry;

    private Training mTraining;
    private TrainingDetail mTrainingDetail;
    private TrainingSubmit mTrainingSubmit;
    private int mStarCount;
    private int mSubmitCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_result);
        ButterKnife.bind(this);

        mAchievementViews = new TrainingAchievementView[3];
        mAchievementViews[0] = findViewById(R.id.achievement0);
        mAchievementViews[1] = findViewById(R.id.achievement1);
        mAchievementViews[2] = findViewById(R.id.achievement2);

        initData(getIntent());

        initView();

        submitTrainingResult();
    }

    private void submitTrainingResult() {
        mSubmitCount++;
        Apic.submitTrainingResult(SecurityUtil.AESEncrypt(new Gson().toJson(mTrainingSubmit))).tag(TAG)
                .callback(new Callback<Resp<TrainingResult>>() {

                    @Override
                    protected void onRespSuccess(Resp<TrainingResult> resp) {
                        //ToastUtil.show(resp.getData().toString());
                    }

                    @Override
                    public void onFailure(ReqError apiError) {
                        if (mSubmitCount < 3) {
                            submitTrainingResult();
                        } else {
                            saveTrainingSubmit();
                        }
                        super.onFailure(apiError);
                    }
                }).fireFreely();

    }
    private void initView() {
        List<TrainingTarget> targets = mTrainingDetail.getTargets();
        if (mTrainingSubmit.isFinish()) {
            mRetry.setVisibility(View.GONE);
            mMyGrade.setVisibility(View.VISIBLE);
            mFailedMessage.setVisibility(View.GONE);
            if (targets != null && !targets.isEmpty()
                    && targets.get(0).getType() == TrainingTarget.TYPE_RATE) {
                double accuracy = mTrainingSubmit.getRate();
                int decimalScale = 2;
                if (accuracy == Math.floor(accuracy) && !Double.isInfinite(accuracy)) {
                    decimalScale = 0;
                }
                mMyGrade.setText(getString(R.string.accuracy_,
                        FinanceUtil.formatToPercentage(accuracy, decimalScale)));
            } else {
                mMyGrade.setText(formatTime(mTrainingSubmit.getTime(),
                        R.string._seconds, R.string._minutes, R.string._minutes_x_seconds));
            }
        } else {
            mRetry.setVisibility(View.VISIBLE);
            mMyGrade.setVisibility(View.GONE);
            mFailedMessage.setVisibility(View.VISIBLE);
            mFailedMessage.setText(R.string.what_a_pity_you_do_not_finish);
        }

        updateTrainingResultDetail(targets);
    }

    private void initData(Intent intent) {
        mTrainingDetail = intent.getParcelableExtra(ExtraKeys.TRAINING_DETAIL);
        mTrainingSubmit = intent.getParcelableExtra(ExtraKeys.TRAINING_SUBMIT);
        mTraining = mTrainingDetail.getTrain();
    }

    private void saveTrainingSubmit() {
        String phone = LocalWrapUser.getUser().getPhone();
        List<TrainingSubmit> submits = Prefer.get().getTrainingSubmits(phone);
        submits.add(mTrainingSubmit);
        Prefer.get().setTrainingSubmits(phone, submits);
    }

    private void updateTrainingResultDetail(List<TrainingTarget> trainTargets) {
        if (!trainTargets.isEmpty()) {
            int targetType = trainTargets.get(0).getType();
            switch (targetType) {
                case TrainingTarget.TYPE_FINISH:
                    mAchievementViews[0].setAchieved(mTrainingSubmit.isFinish());
                    mAchievementViews[0].setContent(R.string.mission_complete);
                    if (mTrainingSubmit.isFinish()) {
                        mStarCount = trainTargets.get(0).getLevel();
                    }
                    showResultsWithAnim(trainTargets.size());
                    break;
                case TrainingTarget.TYPE_RATE:
                    for (int i = 0; i < mAchievementViews.length; i++) {
                        mAchievementViews[i].setContent(getString(R.string.accuracy_to_,
                                FinanceUtil.formatToPercentage(trainTargets.get(i).getRate(), 0)));
                        if (mTrainingSubmit.getRate() >= trainTargets.get(i).getRate() && mTrainingSubmit.isFinish()) {
                            mAchievementViews[i].setAchieved(true);
                            mStarCount = trainTargets.get(i).getLevel();
                        } else {
                            mAchievementViews[i].setAchieved(false);
                        }
                    }
                    showResultsWithAnim(trainTargets.size());
                    break;
                case TrainingTarget.TYPE_TIME:
                    for (int i = 0; i < mAchievementViews.length; i++) {
                        mAchievementViews[i].setContent(
                                formatTime(trainTargets.get(i).getTime(),
                                        R.string._seconds_complete,
                                        R.string._minutes_complete,
                                        R.string._minutes_x_seconds_complete));

                        if (mTrainingSubmit.getTime() <= trainTargets.get(i).getTime() && mTrainingSubmit.isFinish()) {
                            mAchievementViews[i].setAchieved(true);
                            mStarCount = trainTargets.get(i).getLevel();
                        } else {
                            mAchievementViews[i].setAchieved(false);
                        }
                    }
                    showResultsWithAnim(trainTargets.size());
                    break;
            }
        }
    }

    public void showResultsWithAnim(final int count) {
        mAchievementViews[0].postDelayed(new Runnable() {
            @Override
            public void run() {
                showWithAnim(count);
            }
        }, ANIM_DELAY);
    }

    private void showWithAnim(int count) {
        if (count > 0) {
            mAchievementViews[0].startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION,
                    new AnimUtils.AnimEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mAchievementViews[0].setVisibility(View.VISIBLE);
                        }
                    }));
        }
        if (count > 1) {
            mAchievementViews[1].startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION, ENTER_OFFSET,
                    new AnimUtils.AnimEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mAchievementViews[1].setVisibility(View.VISIBLE);
                        }
                    }));
        }
        if (count > 2) {
            mAchievementViews[2].startAnimation(AnimUtils.createTransYFromParent(ANIM_DURATION, ENTER_OFFSET * 2,
                    new AnimUtils.AnimEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mAchievementViews[2].setVisibility(View.VISIBLE);
                        }
                    }));
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_to_bottom);
    }

    @OnClick({R.id.retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.retry:
                requestTrainingContent();
                break;
        }
    }

    private String formatTime(int seconds, int secondRes, int minRes, int minSecondRes) {
        if (seconds / 60 == 0) {
            return getString(secondRes, seconds);
        } else if (seconds % 60 == 0) {
            return getString(minRes, seconds / 60);
        } else {
            return getString(minSecondRes, seconds / 60, seconds % 60);
        }
    }

    private void requestTrainingContent() {
        if (mTraining.getPlayType() == Training.PLAY_TYPE_REMOVE
                || mTraining.getPlayType() == Training.PLAY_TYPE_MATCH_STAR) {
//            Apic.getTrainingContent(mTraining.getId()).tag(TAG)
//                    .host(Apic.WRAP_HOST)
//                    .callback(new Callback2D<Resp<String>, List<Question<RemoveData>>>() {
//                        @Override
//                        protected String onInterceptData(String data) {
//                            return SecurityUtil.AESDecrypt(data);
//                        }
//
//                        @Override
//                        protected void onRespSuccessData(List<Question<RemoveData>> data) {
//                            if (!data.isEmpty()) {
//                                Question question = data.get(0);
//                                if (question.getType() == Question.TYPE_MATCH) {
//                                    startTraining(question);
//                                }
//                            }
//                        }
//                    }).fireFreely();

        } else if (mTraining.getPlayType() == Training.PLAY_TYPE_SORT) {
//            Apic.getTrainingContent(mTraining.getId()).tag(TAG)
//                    .host(Apic.WRAP_HOST)
//                    .callback(new Callback2D<Resp<String>, List<Question<SortData>>>() {
//                        @Override
//                        protected String onInterceptData(String data) {
//                            return SecurityUtil.AESDecrypt(data);
//                        }
//
//                        @Override
//                        protected void onRespSuccessData(List<Question<SortData>> data) {
//                            if (!data.isEmpty()) {
//                                Question question = data.get(0);
//                                if (question.getType() == Question.TYPE_SORT) {
//                                    startTraining(question);
//                                }
//                            }
//                        }
//                    }).fireFreely();

        } else if (mTraining.getPlayType() == Training.PLAY_TYPE_JUDGEMENT) {
            Apic.getTrainingContent(mTraining.getId()).tag(TAG)
                    .callback(new Callback4Resp<Resp<String>, List<Question<KData>>>() {
                        @Override
                        protected String onInterceptData(String data) {
                            return SecurityUtil.AESDecrypt(data);
                        }

                        @Override
                        protected void onRespData(List<Question<KData>> data) {
                            if (!data.isEmpty()) {
                                Question question = data.get(0);
                                if (question.getType() == Question.TYPE_TRUE_OR_FALSE) {
                                    startTraining(question);
                                }
                            }
                        }
                    }).fireFreely();
        }
    }

    private void startTraining(Question question) {
        Launcher.with(getActivity(), TrainingCountDownActivity.class)
                .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                .putExtra(ExtraKeys.QUESTION, question)
                .execute();
        finish();
    }
}
