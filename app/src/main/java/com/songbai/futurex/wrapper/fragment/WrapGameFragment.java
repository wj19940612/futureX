package com.songbai.futurex.wrapper.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.Display;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.wrapper.Apic;
import com.songbai.futurex.wrapper.LocalWrapUser;
import com.songbai.futurex.wrapper.TrainingDetailActivity;
import com.songbai.wrapres.ExtraKeys;
import com.songbai.wrapres.TitleBar;
import com.songbai.wrapres.model.MyTrainingRecord;
import com.songbai.wrapres.model.Training;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * Modified by john on 2018/7/11
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class WrapGameFragment extends BaseFragment {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.list)
    ListView mList;
    Unbinder unbinder;

    private TrainAdapter mTrainAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wrap_game, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTrainAdapter = new TrainAdapter(getActivity());
        mList.setAdapter(mTrainAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyTrainingRecord myTrainingRecord = (MyTrainingRecord) parent.getAdapter().getItem(position);
                if (myTrainingRecord != null) {
                    Launcher.with(getActivity(), TrainingDetailActivity.class)
                            .putExtra(ExtraKeys.TRAINING, myTrainingRecord.getTrain())
                            .execute();
                }
            }
        });
        requestTrainingList();
    }

    private void requestTrainingList() {
        Apic.getTrainingList().tag(TAG)
                .callback(new Callback<Resp<List<MyTrainingRecord>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<MyTrainingRecord>> resp) {
                        if (resp.getData() != null) {
                            updateTrainingList(resp.getData());
                        }
                    }
                }).fireFreely();
    }

    private void updateTrainingList(List<MyTrainingRecord> data) {
        List<MyTrainingRecord> list = new ArrayList<>();
        for (MyTrainingRecord record : data) {
            if (record.getTrain().getType() == Training.TYPE_TECHNOLOGY
                    && record.getTrain().getPlayType() == Training.PLAY_TYPE_JUDGEMENT) {
                list.add(record);
                break;
            }
        }
        mTrainAdapter.addAll(list);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static class TrainAdapter extends ArrayAdapter<MyTrainingRecord> {

        public TrainAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_discover_train, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext(), position);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.splitBlock)
            View mSplitBlock;
            @BindView(R.id.content)
            CardView mContent;
            @BindView(R.id.trainImg)
            ImageView mTrainImg;
            @BindView(R.id.grade)
            TextView mGrade;
            @BindView(R.id.trainType)
            TextView mTrainType;
            @BindView(R.id.trainCount)
            TextView mTrainCount;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(MyTrainingRecord item, Context context, int position) {
                if (position == 0) {
                    mSplitBlock.setVisibility(View.VISIBLE);
                } else {
                    mSplitBlock.setVisibility(View.GONE);
                }

                GlideApp.with(context)
                        .load(item.getTrain().getImage2Url())
                        .into(mTrainImg);
                mTrainType.setText(item.getTrain().getTitle());
                if (!LocalWrapUser.getUser().isLogin()) {
                    mTrainCount.setText(context.getString(R.string.train_count, 0));
                } else {
                    if (item.getRecord() == null || item.getRecord().getFinish() == 0) {
                        mTrainCount.setText(context.getString(R.string.have_no_train));
                    } else {
                        mTrainCount.setText(context.getString(R.string.train_count, item.getRecord().getFinish()));
                    }
                }
                mGrade.setText(context.getString(R.string.level, item.getTrain().getLevel()));
                switch (item.getTrain().getType()) {
                    case Training.TYPE_THEORY:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mContent.setBackground(createDrawable(new int[]{Color.parseColor("#FFB269"), Color.parseColor("#FB857A")}, context));
                        } else {
                            mContent.setBackgroundDrawable(createDrawable(new int[]{Color.parseColor("#FFB269"), Color.parseColor("#FB857A")}, context));
                        }
                        break;
                    case Training.TYPE_TECHNOLOGY:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mContent.setBackground(createDrawable(new int[]{Color.parseColor("#A485FF"), Color.parseColor("#C05DD8")}, context));
                        } else {
                            mContent.setBackgroundDrawable(createDrawable(new int[]{Color.parseColor("#A485FF"), Color.parseColor("#C05DD8")}, context));
                        }
                        break;
                    case Training.TYPE_FUNDAMENTAL:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mContent.setBackground(createDrawable(new int[]{Color.parseColor("#EEA259"), Color.parseColor("#FDD35E")}, context));
                        } else {
                            mContent.setBackgroundDrawable(createDrawable(new int[]{Color.parseColor("#EEA259"), Color.parseColor("#FDD35E")}, context));
                        }
                        break;
                    case Training.TYPE_COMPREHENSIVE:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mContent.setBackground(createDrawable(new int[]{Color.parseColor("#64A0FE"), Color.parseColor("#995BF4")}, context));
                        } else {
                            mContent.setBackgroundDrawable(createDrawable(new int[]{Color.parseColor("#64A0FE"), Color.parseColor("#995BF4")}, context));
                        }
                        break;
                }
            }

            private Drawable createDrawable(int[] colors, Context context) {
                GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
                gradient.setCornerRadius(Display.dp2Px(8, context.getResources()));
                return gradient;
            }
        }
    }

}
