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
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingBean;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.SysMessage;
import com.songbai.futurex.model.status.MessageType;
import com.songbai.futurex.swipeload.RecycleViewSwipeLoadActivity;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    private MessageListAdapter mAdapter;
    private int mPage;
    private int mSize = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        ButterKnife.bind(this);
        initView();
        getMessageList();
    }

    private void initView() {
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readAll();
            }
        });
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessageListAdapter();
        mAdapter.setOnItemClickListener(new MessageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SysMessage sysMessage) {
                msgRead(sysMessage.getId());
            }
        });
        mSwipeTarget.setAdapter(mAdapter);
    }

    private void msgRead(int id) {
        Apic.msgRead(id)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                    }
                })
                .fire();
    }

    private void readAll() {
        Apic.msgReadAll()
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                    }
                })
                .fire();
    }

    private void getMessageList() {
        Apic.msgList(mPage, mSize)
                .callback(new Callback<Resp<PagingBean<SysMessage>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingBean<SysMessage>> resp) {
                        mAdapter.setList(resp.getData());
                        mAdapter.notifyDataSetChanged();
                        stopFreshOrLoadAnimation();
                        if (resp.getData().getTotal() > mPage) {
                            mPage++;
                        }else {
                        }
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
        getMessageList();
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        getMessageList();
    }

    static class MessageListAdapter extends RecyclerView.Adapter {
        private List<SysMessage> mList = new ArrayList<>();
        private OnItemClickListener mOnItemClickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MessageViewHolder) {
                ((MessageViewHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(PagingBean<SysMessage> resp) {
            if (resp.getStart() == 0) {
                mList.clear();
            }
            mList.addAll(resp.getData());
        }

        public interface OnItemClickListener {
            void onItemClick(SysMessage sysMessage);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            private View mRootView;
            @BindView(R.id.timestamp)
            TextView mTimestamp;
            @BindView(R.id.content)
            TextView mContent;
            @BindView(R.id.hint)
            TextView mHint;

            MessageViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                mRootView = view;
            }

            void bindData(final SysMessage sysMessage) {
                mRootView.setSelected(sysMessage.getStatus() == SysMessage.READ);
                mTimestamp.setText(DateUtil.format(sysMessage.getCreateTime(), DateUtil.FORMAT_HOUR_MINUTE_SECOND));
                int textId = 0;
                switch (sysMessage.getType()) {
                    case MessageType.PAY_ADDR_CHANGE:
                        textId = R.string.pay_addr_change;
                        break;
                    case MessageType.OTC_BUY_ORDER:
                        textId = R.string.otc_buy_order;
                        break;
                    case MessageType.OTC_SELL_ORDER:
                        textId = R.string.otc_sell_order;
                        break;
                    case MessageType.OTC_BUY_PAY:
                        textId = R.string.otc_buy_pay;
                        break;
                    case MessageType.OTC_SELL_PAY:
                        textId = R.string.otc_sell_pay;
                        break;
                    case MessageType.OTC_ORDER_MSG:
                        textId = R.string.otc_order_msg;
                        break;
                    case MessageType.USER_AUTH_FAIL:
                        textId = R.string.user_auth_fail;
                        break;
                    default:
                }
                if (textId!=0) {
                    mContent.setText(textId);
                }
                mHint.setVisibility(View.GONE);
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(sysMessage);
                        }
                    }
                });
            }
        }
    }
}
