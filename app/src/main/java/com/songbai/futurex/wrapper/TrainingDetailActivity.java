package com.songbai.futurex.wrapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.WebActivity;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.SecurityUtil;
import com.songbai.wrapres.ExtraKeys;
import com.songbai.wrapres.ObservableScrollView;
import com.songbai.wrapres.TitleBar;
import com.songbai.wrapres.TrainingAchievementView2;
import com.songbai.wrapres.model.KData;
import com.songbai.wrapres.model.Question;
import com.songbai.wrapres.model.Training;
import com.songbai.wrapres.model.TrainingDetail;
import com.songbai.wrapres.model.TrainingRecord;
import com.songbai.wrapres.model.TrainingTarget;
import com.songbai.wrapres.utils.FinanceUtil;
import com.songbai.wrapres.utils.SpannableUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TrainingDetailActivity extends WrapBaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.extraBackground)
    LinearLayout mExtraBackground;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.introduce)
    TextView mIntroduce;
    @BindView(R.id.scrollView)
    ObservableScrollView mObservableScrollView;
    @BindView(R.id.duration)
    TextView mDuration;
    @BindView(R.id.difficulty)
    TextView mDifficulty;
    @BindView(R.id.relatedKnowledge)
    TextView mRelatedKnowledge;
    @BindView(R.id.startTraining)
    Button mStartTraining;
    @BindView(R.id.background)
    LinearLayout mBackground;

    private TrainingDetail mTrainingDetail;
    private Training mTraining;
    private TrainingRecord mMyTrainingRecord;
    private TrainingAchievementView2[] mAchievementView2s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_detail);
        ButterKnife.bind(this);
        translucentStatusBar();

        initData(getIntent());

        initBackground();

        mObservableScrollView.setScrollChangedListener(new ObservableScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY) {
                float alpha = 0;
                if (y < 0) {
                    alpha = 0;
                } else if (y > 300) {
                    alpha = 1;
                } else {
                    alpha = y / 300.0f;
                }
                mTitleBar.setTitleAlpha(alpha);
            }
        });

        initAchievementViews();

        requestTrainDetail();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestMyTrainingRecord();
    }

    private void requestMyTrainingRecord() {
        if (LocalWrapUser.getUser().isLogin()) {
            Apic.getMyTrainingRecord(mTraining.getId()).tag(TAG)
                    .callback(new Callback4Resp<Resp<TrainingRecord>, TrainingRecord>() {
                        @Override
                        protected void onRespData(TrainingRecord data) {
                            mMyTrainingRecord = data;
                            updateAchievementViews();
                        }
                    }).fireFreely();
        }
    }

    private void initAchievementViews() {
        mAchievementView2s = new TrainingAchievementView2[3];
        mAchievementView2s[0] = findViewById(R.id.achievement0);
        mAchievementView2s[1] = findViewById(R.id.achievement1);
        mAchievementView2s[2] = findViewById(R.id.achievement2);
    }

    private void updateAchievementViews() {
        if (mTrainingDetail != null) {
            List<TrainingTarget> targets = mTrainingDetail.getTargets();
            if (targets != null && !targets.isEmpty()) {
                for (int i = 0; i < targets.size() && i < mAchievementView2s.length; i++) {
                    TrainingTarget target = targets.get(i);
                    mAchievementView2s[i].setVisibility(View.VISIBLE);

                    updateAchievementView(mAchievementView2s[i], target);

                    if (mMyTrainingRecord != null && mMyTrainingRecord.getMaxLevel() >= target.getLevel()) {
                        mAchievementView2s[i].setAchieved(true);
                    }
                }
            }
        }
    }

    private void updateAchievementView(TrainingAchievementView2 achievementView, TrainingTarget target) {
        switch (target.getType()) {
            case TrainingTarget.TYPE_FINISH:
                achievementView.setContent(R.string.mission_complete);
                break;
            case TrainingTarget.TYPE_RATE:
                achievementView.setContent(getString(R.string.accuracy_to_,
                        FinanceUtil.formatToPercentage(target.getRate(), 0)));
                break;
            case TrainingTarget.TYPE_TIME:
                achievementView.setContent(formatTime(target.getTime(),
                        R.string._seconds_complete,
                        R.string._minutes_complete,
                        R.string._minutes_x_seconds_complete));
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

    private void initData(Intent intent) {
        mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
    }

    private void initBackground() {
        if (mTraining != null) {
            switch (mTraining.getType()) {
                case Training.TYPE_THEORY:
                    mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.redTheoryTraining));
                    mBackground.setBackgroundResource(R.drawable.bg_training_detail_theory);
                    mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.redTheoryTraining));
                    mStartTraining.setBackgroundResource(R.drawable.bg_train_theory);
                    break;
                case Training.TYPE_TECHNOLOGY:
                    mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.violetTechnologyTraining));
                    mBackground.setBackgroundResource(R.drawable.bg_training_detail_technology);
                    mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.violetTechnologyTraining));
                    mStartTraining.setBackgroundResource(R.drawable.bg_train_technology);
                    break;
                case Training.TYPE_FUNDAMENTAL:
                    mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellowFundamentalTraining));
                    mBackground.setBackgroundResource(R.drawable.bg_training_detail_fundamental);
                    mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellowFundamentalTraining));
                    mStartTraining.setBackgroundResource(R.drawable.bg_train_fundamentals);
                    break;
                case Training.TYPE_COMPREHENSIVE:
                    mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blueComprehensiveTraining));
                    mBackground.setBackgroundResource(R.drawable.bg_training_detail_comprehensive);
                    mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blueComprehensiveTraining));
                    mStartTraining.setBackgroundResource(R.drawable.bg_train_comprehensive);
                    break;
            }
        }
    }

    private void requestTrainDetail() {
        Apic.getTrainingDetail(mTraining.getId()).tag(TAG)
                .indeterminate(this)
                .callback(new Callback4Resp<Resp<TrainingDetail>, TrainingDetail>() {
                    @Override
                    protected void onRespData(TrainingDetail data) {
                        mTrainingDetail = data;
                        updateTrainDetail(data);
                        updateAchievementViews();
                    }
                }).fire();
    }

    private void updateTrainDetail(TrainingDetail trainingDetail) {
        Training training = trainingDetail.getTrain();
        if (training != null) {
            mTitleBar.setTitle(training.getTitle());
            mTitle.setText(training.getTitle());
            mIntroduce.setText(training.getRemark());
            mDifficulty.setText(getString(R.string.train_level, training.getLevel()));
            mRelatedKnowledge.setText(SpannableUtil.mergeTextWithColor(getString(R.string.subject_so_difficult)
                    , "  " + getString(R.string.to_study)
                    , ContextCompat.getColor(getActivity(), R.color.yellowColor2)));
            mDuration.setText(formatTime(training.getTime(),
                    R.string._seconds,
                    R.string._minutes,
                    R.string._minutes_x_seconds));
        }
    }

    @OnClick({R.id.relatedKnowledge, R.id.startTraining})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relatedKnowledge:
                if (mTrainingDetail != null && mTrainingDetail.getTrain() != null) {
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_URL, Apic.LEMI_HOST + mTrainingDetail.getTrain().getKnowledgeUrl())
                            .execute();
                }
                break;
            case R.id.startTraining:
                if (LocalWrapUser.getUser().isLogin()) {
                    requestTrainingContent();
                } else {
                    Launcher.with(getActivity(), WrapLoginActivity.class).execute();
                }
                break;
        }
    }

    private void requestTrainingContent() {
        if (mTraining.getPlayType() == Training.PLAY_TYPE_REMOVE
                || mTraining.getPlayType() == Training.PLAY_TYPE_MATCH_STAR) {
//            Apic.getTrainingContent(mTraining.getId()).tag(TAG).indeterminate(this)
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
//                    }).fire();

        } else if (mTraining.getPlayType() == Training.PLAY_TYPE_SORT) {
//            Apic.getTrainingContent(mTraining.getId()).tag(TAG).indeterminate(this)
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
//                    }).fire();

        } else if (mTraining.getPlayType() == Training.PLAY_TYPE_JUDGEMENT) {
            Apic.getTrainingContent(mTraining.getId()).tag(TAG).indeterminate(this)
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
                    }).fire();
        }
    }

    private void startTraining(Question question) {
        if (mTrainingDetail != null) {
            Launcher.with(getActivity(), TrainingCountDownActivity.class)
                    .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                    .putExtra(ExtraKeys.QUESTION, question)
                    .execute();
        } else {
            requestTrainDetailAndStartTraining(question);
        }
    }

    private void requestTrainDetailAndStartTraining(final Question question) {
        Apic.getTrainingDetail(mTraining.getId()).tag(TAG)
                .callback(new Callback4Resp<Resp<TrainingDetail>, TrainingDetail>() {
                    @Override
                    protected void onRespData(TrainingDetail data) {
                        mTrainingDetail = data;
                        updateTrainDetail(data);
                        updateAchievementViews();
                        startTraining(question);
                    }
                }).fire();
    }
}
