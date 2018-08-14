package com.songbai.futurex.wrapper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.httplib.ReqError;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.ThumbTransform;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.wrapper.fragment.PreviewDialogFragment;
import com.songbai.futurex.wrapper.fragment.WrapUploadUserImageDialogFragment;
import com.songbai.wrapres.TitleBar;
import com.songbai.wrapres.model.Feedback;
import com.songbai.wrapres.model.FeedbackResp;
import com.songbai.wrapres.utils.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sbai.com.glide.GlideApp;

/**
 * 意见反馈`
 */

public class FeedbackActivity extends WrapBaseActivity
        implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.commentContent)
    EditText mCommentContent;
    @BindView(R.id.addPic)
    ImageButton mAddPic;
    @BindView(R.id.send)
    TextView mSend;
    @BindView(R.id.operateArea)
    LinearLayout mOperateArea;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;

    private int mPage = 0;
    private boolean mLoadMoreEnable = true;

    private List<Feedback> mFeedbackList;
    private FeedbackAdapter mFeedbackAdapter;
    private boolean firstLoadData = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        initData(getIntent());
        initViews();

        requestFeedbackData(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData(intent);
    }

    private void initData(Intent intent) {
        String imagePath = intent.getStringExtra(ExtraKeys.IMAGE_PATH);
        if (imagePath != null) {
            sendFeedbackImage(imagePath);
        }
    }

    private void initViews() {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(200);
        mCommentContent.setFilters(filters);
        mCommentContent.addTextChangedListener(mContentWatcher);
        mFeedbackList = new ArrayList<>();
        mFeedbackAdapter = new FeedbackAdapter(this, mFeedbackList);
        mListView.setAdapter(mFeedbackAdapter);
        mListView.setOnScrollListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSend.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCommentContent.removeTextChangedListener(mContentWatcher);
    }

    private void requestFeedbackData(final boolean needScrollToLast) {
        Apic.getFeedbackList(mPage).host(Apic.BC_NEWS_HOST).tag(TAG)
                .callback(new Callback4Resp<Resp<FeedbackResp<List<Feedback>>>, FeedbackResp<List<Feedback>>>() {
                    @Override
                    protected void onRespData(FeedbackResp<List<Feedback>> data) {
                        if (data.getContent() != null) {
                            updateFeedbackList(data.getContent(), needScrollToLast);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void listViewScrollBottom() {
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setStackFromBottom(true);
    }

    private void createServiceTalk(List<Feedback> data) {
        Feedback feedback = new Feedback();
        feedback.setType(1);
        feedback.setContent(getString(R.string.send_message_connect));
        feedback.setCreateTime(System.currentTimeMillis());
        data.add(0, feedback);
    }

    private void updateFeedbackList(List<Feedback> data, boolean needScrollToLast) {
        if (mPage == 0) {
            mFeedbackAdapter.clear();
        }
        if (data.size() < Apic.DEFAULT_PAGE_SIZE) {
            mLoadMoreEnable = false;
            mSwipeRefreshLayout.setEnabled(false);
        } else {
            mPage++;
        }
        if (firstLoadData) {
            //自己创建一个客服对话
            createServiceTalk(data);
            firstLoadData = false;
        }
        mFeedbackAdapter.addFeedbackList(data);
        if (needScrollToLast) {
            mListView.setSelection(View.FOCUS_DOWN);
            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.setSelection(View.FOCUS_DOWN);
                }
            }, 240);
        }
    }


    private void requestSendFeedback(final String content, final int contentType) {
        Apic.sendFeedback(content, contentType).tag(TAG)
                .callback(new Callback4Resp<Resp<Object>, Object>() {
                    @Override
                    protected void onRespData(Object data) {
                        refreshChatList(content, contentType);
                    }

                    @Override
                    public void onFailure(ReqError reqError) {
                        super.onFailure(reqError);
                        if (contentType == Feedback.CONTENT_TYPE_TEXT) {
                            mSend.setEnabled(true);
                        }
                    }
                }).fireFreely();
    }

    //请求最新的服务器数据  并取第一条
    private void refreshChatList(final String content, final int contentType) {
        Apic.getFeedbackList(0)
                .tag(TAG)
                .callback(new Callback4Resp<Resp<FeedbackResp<List<Feedback>>>, FeedbackResp<List<Feedback>>>() {
                    @Override
                    protected void onRespData(FeedbackResp<List<Feedback>> data) {
                        if (data.getContent() != null) {
                            updateTheLastMessage(data.getContent(), content, contentType);
                        }
                    }
                })
                .fire();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @OnClick({R.id.addPic, R.id.send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.addPic:
                if (mFeedbackAdapter.getCount() > 6) {
                    listViewScrollBottom();
                }
                sendPicToCustomer();

                break;
            case R.id.send:
                if (mFeedbackAdapter.getCount() > 6) {
                    listViewScrollBottom();
                }
                String content = mCommentContent.getText().toString().trim();
                mSend.setEnabled(false);
                requestSendFeedback(content, Feedback.CONTENT_TYPE_TEXT);
                break;
        }
    }

    private void sendPicToCustomer() {
        WrapUploadUserImageDialogFragment.newInstance(WrapUploadUserImageDialogFragment.IMAGE_TYPE_OPEN_CUSTOM_GALLERY)
                .setOnImagePathListener(new WrapUploadUserImageDialogFragment.OnImagePathListener() {
                    @Override
                    public void onImagePath(int index, String imagePath) {
                        sendFeedbackImage(imagePath);
                    }
                }).show(getSupportFragmentManager());
    }

    private void sendFeedbackImage(final String path) {
        String content = ImageUtils.compressImageToBase64(path, getActivity());
        requestSendFeedback(content, Feedback.CONTENT_TYPE_PICTURE);
    }

    @Override
    public void onRefresh() {
        requestFeedbackData(false);
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        if (mLoadMoreEnable) {
//            mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            if (firstVisibleItem == 0 && topRowVerticalPosition >= 0) {
                mSwipeRefreshLayout.setEnabled(true);
            } else {
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }

        }
    }

    private void updateTheLastMessage(List<Feedback> data, String content, int contentType) {
        if (data.isEmpty()) return;
        Feedback feedback = data.get(0);
        mFeedbackAdapter.addFeedbackItem(feedback);

        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.setSelection(mFeedbackAdapter.getCount() - 1);
            }
        }, 240);
        if (contentType == Feedback.CONTENT_TYPE_TEXT) {
            feedback.setContent(content);
            mCommentContent.setText("");
        }
    }

    private ValidationWatcher mContentWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(mCommentContent.getText().toString().trim())) {
                mSend.setEnabled(false);
            } else {
                mSend.setEnabled(true);
            }
        }
    };


    static class FeedbackAdapter extends BaseAdapter {

        public static final int TYPE_USER = 0;
        public static final int TYPE_CUSTOMER = 1;

        private Context mContext;
        private List<Feedback> mFeedbackList;

        public FeedbackAdapter(Context context, List<Feedback> list) {
            mContext = context;
            mFeedbackList = list;
        }

        public void addFeedbackList(List<Feedback> list) {
            int length = list.size();
            //如果原来已经有数据 则倒序插入
            if (mFeedbackList.size() > 0) {
                for (int i = 0; i < length; i++) {
                    mFeedbackList.add(0, list.get(i));
                }
            } else {
                Collections.reverse(list);
                mFeedbackList.addAll(list);
            }
            notifyDataSetChanged();
        }

        public void addFeedbackItem(Feedback feedback) {
            mFeedbackList.add(feedback);
            notifyDataSetChanged();

        }

        public void clear() {
            mFeedbackList.clear();
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return mFeedbackList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFeedbackList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if (mFeedbackList.get(position).getType() == TYPE_USER) {
                return TYPE_USER;
            } else {
                return TYPE_CUSTOMER;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        private boolean needTitle(int position) {
            if (position == 0) {
                return true;
            }

            if (position < 0) {
                return false;
            }

            Feedback pre = mFeedbackList.get(position - 1);
            Feedback next = mFeedbackList.get(position);
            //判断两个时间在不在一天内  不是就要显示标题
            long preTime = pre.getCreateTime();
            long nextTime = next.getCreateTime();
            return !DateUtil.isToday(nextTime, preTime);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserViewHolder userViewHolder = null;
            CustomerViewHolder customerViewHolder = null;
            switch (getItemViewType(position)) {
                case TYPE_USER:
                    if (convertView == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.row_feedback_user, null);
                        userViewHolder = new UserViewHolder(convertView);
                        convertView.setTag(userViewHolder);
                    } else {
                        userViewHolder = (UserViewHolder) convertView.getTag();
                    }
                    userViewHolder.bindingData(mFeedbackList.get(position), needTitle(position), mContext);
                    break;
                case TYPE_CUSTOMER:
                    if (convertView == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.row_feedback_customer, null);
                        customerViewHolder = new CustomerViewHolder(convertView);
                        convertView.setTag(customerViewHolder);
                    } else {
                        customerViewHolder = (CustomerViewHolder) convertView.getTag();
                    }
                    customerViewHolder.bindingData(mFeedbackList.get(position), needTitle(position), mContext);
                    break;
            }
            return convertView;
        }


        static class UserViewHolder {
            @BindView(R.id.endLineTime)
            TextView mEndLineTime;
            @BindView(R.id.timeLayout)
            RelativeLayout mTimeLayout;
            @BindView(R.id.contentWrapper)
            RelativeLayout mWrapper;
            @BindView(R.id.timestamp)
            TextView mTimestamp;
            @BindView(R.id.image)
            ImageView mImage;
            @BindView(R.id.text)
            TextView mText;
            @BindView(R.id.headImage)
            ImageView mHeadImage;


            public UserViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }

            private void bindingData(final Feedback feedback, boolean needTitle, final Context context) {
                if (needTitle) {
                    mTimeLayout.setVisibility(View.VISIBLE);
                    mEndLineTime.setText(DateUtil.getFeedbackFormatTime(feedback.getCreateTime()));

                } else {
                    mTimeLayout.setVisibility(View.GONE);
                }
                mTimestamp.setText(DateUtil.format(feedback.getCreateTime(), DateUtil.FORMAT_HOUR_MINUTE));
                //判断是否图片
                if (feedback.getContentType() == Feedback.CONTENT_TYPE_TEXT) {
                    mText.setVisibility(View.VISIBLE);
                    mImage.setVisibility(View.GONE);
                    mText.setText(feedback.getContent());
                } else {
                    mText.setVisibility(View.GONE);
                    mImage.setVisibility(View.VISIBLE);
                    GlideApp.with(context).load(feedback.getContent())
                            .centerCrop()
                            .transform(new ThumbTransform(context))
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mImage);
                }
                if (!TextUtils.isEmpty(feedback.getUserPortrait())) {
                    GlideApp.with(context).load(feedback.getUserPortrait())
                            .circleCrop()
                            .placeholder(R.drawable.ic_avatar_feedback)
                            .into(mHeadImage);
                } else {
                    GlideApp.with(context).load(feedback.getPortrait())
                            .circleCrop()
                            .placeholder(R.drawable.ic_avatar_feedback)
                            .into(mHeadImage);
                }
                if (mImage != null) {
                    mImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (feedback.getContentType() == Feedback.CONTENT_TYPE_PICTURE) {
                                PreviewDialogFragment.newInstance(feedback.getContent())
                                        .show(((FeedbackActivity) context).getSupportFragmentManager());
                            }
                        }
                    });
                }
            }

        }

        static class CustomerViewHolder {
            @BindView(R.id.endLineTime)
            TextView mEndLineTime;
            @BindView(R.id.timeLayout)
            RelativeLayout mTimeLayout;
            @BindView(R.id.headImage)
            ImageView mHeadImage;
            @BindView(R.id.timestamp)
            TextView mTimestamp;
            @BindView(R.id.image)
            ImageView mImage;
            @BindView(R.id.text)
            TextView mText;

            public CustomerViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(final Feedback feedback, boolean needTitle, final Context context) {
                if (needTitle) {
                    mTimeLayout.setVisibility(View.VISIBLE);
                    mEndLineTime.setText(DateUtil.getFeedbackFormatTime(feedback.getCreateTime()));

                } else {
                    mTimeLayout.setVisibility(View.GONE);
                }
                mTimestamp.setText(DateUtil.format(feedback.getCreateTime(), DateUtil.FORMAT_HOUR_MINUTE));
                if (feedback.getContentType() == Feedback.CONTENT_TYPE_TEXT) {
                    mText.setVisibility(View.VISIBLE);
                    mImage.setVisibility(View.GONE);
                    mText.setText(feedback.getContent());
                } else {
                    mText.setVisibility(View.GONE);
                    mImage.setVisibility(View.VISIBLE);
                    GlideApp.with(context).load(feedback.getContent())
                            .centerCrop()
                            .transform(new ThumbTransform(context))
                            .into(mImage);
                }
                GlideApp.with(context).load(feedback.getReplyUserPortrait())
                        .placeholder(R.mipmap.ic_launcher)
                        .circleCrop()
                        .into(mHeadImage);
                mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreviewDialogFragment.newInstance(feedback.getContent())
                                .show(((FeedbackActivity) context).getSupportFragmentManager());
                    }
                });
            }
        }
    }
}