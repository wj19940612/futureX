package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingResp;
import com.songbai.futurex.model.mine.SysMessage;
import com.songbai.futurex.swipeload.RecycleViewSwipeLoadActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class MessageCenterActivity extends RecycleViewSwipeLoadActivity {
    @BindView(R.id.swipe_target)
    RecyclerView mSwipeTarget;
    @BindView(R.id.rootView)
    LinearLayout mRootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        ButterKnife.bind(this);
        initView();
        getMessageList();
    }

    private void initView() {
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(this));
        mSwipeTarget.setAdapter(new MessageListAdapter());
    }

    private void getMessageList() {
        Apic.msgList(0, 20)
                .callback(new Callback<PagingResp<SysMessage>>() {
                    @Override
                    protected void onRespSuccess(PagingResp<SysMessage> resp) {

                    }
                })
                .fire();
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

    private class MessageListAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MessageViewHolder) {
                ((MessageViewHolder) holder).bindData();
            }
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public MessageViewHolder(View view) {
                super(view);
            }

            public void bindData() {
            }
        }
    }
}
