package com.songbai.futurex.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.songbai.futurex.R;
import com.songbai.futurex.swipeload.RecycleViewSwipeLoadActivity;
import com.songbai.futurex.view.TitleBar;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yangguangda
 * @date 2018/6/1
 */
public class PropertyFlowActivity extends RecycleViewSwipeLoadActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_target)
    RecyclerView mSwipeTarget;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;
    @BindView(R.id.filtrateGroup)
    LinearLayout mFiltrateGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prorerty_flow);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/6/4 动画
//                mFiltrateGroup.setVisibility(mFiltrateGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                TimePickerView pvTime = new TimePickerBuilder(PropertyFlowActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        Toast.makeText(PropertyFlowActivity.this, "title", Toast.LENGTH_SHORT).show();
                    }
                }).build();
                pvTime.show();
            }
        });
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(this));
        mSwipeTarget.setAdapter(new PropertyFlowAdapter());
    }

    @Override
    public View getContentView() {
        return mRootView;
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }

    static class PropertyFlowAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_property_flow, parent, false);
            return new PropertyFlowHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        static class PropertyFlowHolder extends RecyclerView.ViewHolder {
            PropertyFlowHolder(View view) {
                super(view);
            }
        }
    }
}