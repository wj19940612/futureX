package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.WebActivity;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.SysMessage;
import com.songbai.futurex.model.status.MessageType;
import com.songbai.futurex.swipeload.RVSwipeLoadActivity;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.OnItemClickListener;
import com.songbai.futurex.view.TitleBar;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yangguangda
 * @date 2018/5/30
 */

public class MessageCenterActivity extends RVSwipeLoadActivity {

    public static final int PAGE_TYPE_MESSAGE = 0;
    public static final int PAGE_TYPE_NOTICE = 1;

    @BindView(R.id.swipe_target)
    RecyclerView mSwipeTarget;
    @BindView(R.id.rootView)
    LinearLayout mRootView;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;

    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;

    private MessageListAdapter mAdapter;

    private int mPage;
    private int mPageType;

    private int mOffset = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        ButterKnife.bind(this);
        mPageType = getIntent().getIntExtra(ExtraKeys.TAG, 0);
        initView();
        getData();
    }

    private void getData() {
        if (mPageType == PAGE_TYPE_NOTICE) {
            requestNotice();
        } else {
            getMessageList();
        }
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
        mAdapter.setOnItemClickListener(new OnItemClickListener<SysMessage>() {
            @Override
            public void onItemClick(SysMessage sysMessage, int position) {
                if (mPageType == PAGE_TYPE_NOTICE) {
                    String url = String.format(Apic.url.NOTICE_DETAIL_PAGE, sysMessage.getId());
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_URL, Api.getH5Url(url))
                            .execute();
                    return;
                }
                msgRead(sysMessage, position);
            }
        });
        mSwipeTarget.setAdapter(mAdapter);

        if (mPageType == PAGE_TYPE_NOTICE) {
            mTitleBar.setRightVisible(false);
            mTitleBar.setRightViewEnable(false);
            mTitleBar.setTitle(R.string.notice_center);
            mAdapter.setPageType(PAGE_TYPE_NOTICE);
        }
    }

    private void msgRead(final SysMessage sysMessage, final int position) {
        Apic.msgRead(sysMessage.getId())
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        sysMessage.setStatus(SysMessage.READ);
                        mAdapter.notifyItemChanged(position, sysMessage);
                    }
                })
                .fire();

    }

    private void readAll() {
        Apic.msgReadAll()
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        mAdapter.setAllIsRead(true);
                    }
                })
                .fire();
    }

    private void requestNotice() {
        // TODO: 2018/7/2 字体
        String lang = "";

        Apic.findNewsList(PAGE_TYPE_NOTICE, lang, mOffset, Apic.DEFAULT_PAGE_SIZE)
                .callback(new Callback<Resp<List<SysMessage>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<SysMessage>> resp) {
                        List<SysMessage> data = resp.getData();
                        updateNoticeList(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopFreshOrLoadAnimation();
                    }
                })
                .fire();
    }

    private void updateNoticeList(List<SysMessage> data) {
        if (mOffset == 0) mAdapter.clear();
        if (data != null) {
            if (data.size() < Apic.DEFAULT_PAGE_SIZE) {
                mOffset += data.size();
            } else {
                mSwipeToLoadLayout.setLoadMoreEnabled(false);
            }
            mAdapter.addAll(data);
        }
    }

    private void getMessageList() {
        Apic.msgList(mPage, Apic.DEFAULT_PAGE_SIZE)
                .callback(new Callback<Resp<PagingWrap<SysMessage>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingWrap<SysMessage>> resp) {
                        updateMessageList(resp);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopFreshOrLoadAnimation();
                    }
                }).fire();
    }

    private void updateMessageList(Resp<PagingWrap<SysMessage>> resp) {
        if (resp.getData() != null) {
            if (resp.getData().getTotal() > mPage) {
                mPage++;
            } else {
                mSwipeToLoadLayout.setLoadMoreEnabled(false);
            }

            if (mPage == 0) {
                mAdapter.clear();
            }
            if (resp.getData().getData() != null) {
                mAdapter.addAll(resp.getData().getData());
            }
        }
    }


    @Override
    public View getContentView() {
        return mRootView;
    }

    @Override
    public void onLoadMore() {
        getData();
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        mOffset = 0;
        mSwipeToLoadLayout.setRefreshEnabled(true);
        getData();
    }

    static class MessageListAdapter extends RecyclerView.Adapter {
        private List<SysMessage> mList = new ArrayList<>();

        private OnItemClickListener<SysMessage> mOnItemClickListener;

        private boolean allIsRead;
        private int mPageType;

        public void setAllIsRead(boolean allIsRead) {
            this.allIsRead = allIsRead;
            notifyDataSetChanged();
        }

        public void setPageType(int pageType) {
            mPageType = pageType;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MessageViewHolder) {
                ((MessageViewHolder) holder).bindData(mList.get(position), position, mPageType, allIsRead);
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setOnItemClickListener(OnItemClickListener<SysMessage> onItemClickListener) {
            mOnItemClickListener = onItemClickListener;

        }

        public void clear() {
            mList.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<SysMessage> data) {
            mList.addAll(data);
            notifyDataSetChanged();
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

            void bindData(final SysMessage sysMessage, final int position, int pageType, boolean allIsRead) {
                if (allIsRead) mContent.setSelected(true);
                else if (pageType == PAGE_TYPE_NOTICE) {
                    mContent.setSelected(true);
                    mTimestamp.setText(DateUtil.formatNoticeTime(sysMessage.getShowStartTime()));
                    mContent.setText(sysMessage.getTitle());
                    if (!TextUtils.isEmpty(sysMessage.getStyle())) {
                        mHint.setText(sysMessage.getStyle());
                        mHint.setVisibility(View.VISIBLE);
                    } else {
                        mHint.setVisibility(View.GONE);
                    }
                } else {
                    mContent.setSelected(sysMessage.getStatus() == SysMessage.READ);
                    mTimestamp.setText(DateUtil.format(sysMessage.getCreateTime(), DateUtil.FORMAT_SPECIAL_SLASH));
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
                    mContent.setText(textId);
                    mHint.setVisibility(View.GONE);
                }

                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(sysMessage, position);
                        }
                    }
                });
            }
        }
    }
}



