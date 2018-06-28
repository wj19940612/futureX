package com.songbai.futurex.fragment.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.dialog.UploadUserImageDialogFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.Feedback;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.TitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.songbai.futurex.model.Feedback.CONTENT_TYPE_PICTURE;
import static com.songbai.futurex.model.Feedback.CONTENT_TYPE_TEXT;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class CustomerServiceFragment extends UniqueActivity.UniFragment implements AbsListView.OnScrollListener,
        SwipeRefreshLayout.OnRefreshListener {
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
    private Unbinder mBind;

    private List<Feedback> mFeedbackList;
    private FeedbackAdapter mFeedbackAdapter;

    private boolean mLoadMoreEnable = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_service, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(200);
        mCommentContent.setFilters(filters);
        mCommentContent.addTextChangedListener(mContentWatcher);
        mFeedbackList = new ArrayList<>();
        mFeedbackAdapter = new FeedbackAdapter(getContext(), mFeedbackList);
        mListView.setAdapter(mFeedbackAdapter);
        mListView.setOnScrollListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSend.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    private void requestSendFeedback(final String content, final int contentType) {
        Apic.chatSend(content, "", contentType).tag(TAG)
                .callback(new Callback4Resp<Resp<Object>, Object>() {
                    @Override
                    protected void onRespData(Object data) {
                        refreshChatList(content, contentType);
                    }

                    @Override
                    public void onFailure(ReqError reqError) {
                        super.onFailure(reqError);
                        if (contentType == CONTENT_TYPE_TEXT) {
                            mSend.setEnabled(true);
                        }
                    }
                }).fireFreely();
    }

    //请求最新的服务器数据  并取第一条
    private void refreshChatList(final String content, final int contentType) {
//        Apic.requestFeedbackList(0)
//                .tag(TAG)
//                .callback(new Callback2D<Resp<FeedbackResp<List<Feedback>>>, FeedbackResp<List<Feedback>>>() {
//                    @Override
//                    protected void onRespData(FeedbackResp<List<Feedback>> data) {
//                        if (data.getContent() != null) {
//                            updateTheLastMessage(data.getContent(), content, contentType);
//                        }
//                    }
//                })
//                .fire();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @OnClick({R.id.addPic})
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
                requestSendFeedback(content, CONTENT_TYPE_TEXT);
                break;
            default:
        }
    }

    private void sendPicToCustomer() {
        UploadUserImageDialogFragment.newInstance(UploadUserImageDialogFragment.IMAGE_TYPE_OPEN_CUSTOM_GALLERY)
                .setOnImagePathListener(new UploadUserImageDialogFragment.OnImagePathListener() {
                    @Override
                    public void onImagePath(int index, String imagePath) {
                        sendFeedbackImage(imagePath);
                    }
                }).show(getChildFragmentManager());
    }

    private void sendFeedbackImage(final String path) {
        String content = ImageUtils.compressImageToBase64(path, getActivity());
        requestSendFeedback(content, CONTENT_TYPE_PICTURE);
    }

    private void listViewScrollBottom() {
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setStackFromBottom(true);
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

    @Override
    public void onRefresh() {
//        requestFeedbackData(false);
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

        static final int TYPE_USER = 0;
        static final int TYPE_CUSTOMER = 1;

        private Context mContext;
        private List<Feedback> mFeedbackList;

        FeedbackAdapter(Context context, List<Feedback> list) {
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
                default:
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


            UserViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }

            private void bindingData(final Feedback feedback, boolean needTitle, final Context context) {
//                if (needTitle) {
//                    mTimeLayout.setVisibility(View.VISIBLE);
//                    mEndLineTime.setText(DateUtil.getFeedbackFormatTime(feedback.getCreateTime()));
//
//                } else {
//                    mTimeLayout.setVisibility(View.GONE);
//                }
//                mTimestamp.setText(DateUtil.format(feedback.getCreateTime(), FORMAT_HOUR_MINUTE));
//                //判断是否图片
//                if (feedback.getContentType() == CONTENT_TYPE_TEXT) {
//                    mText.setVisibility(View.VISIBLE);
//                    mImage.setVisibility(View.GONE);
//                    mText.setText(feedback.getContent());
//                } else {
//                    mText.setVisibility(View.GONE);
//                    mImage.setVisibility(View.VISIBLE);
//                    GlideApp.with(context).load(feedback.getContent())
//                            .centerCrop()
//                            .transform(new ThumbTransform(context))
////                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .into(mImage);
//                }
//                if (!TextUtils.isEmpty(feedback.getUserPortrait())) {
//                    GlideApp.with(context).load(feedback.getUserPortrait())
//                            .circleCrop()
//                            .placeholder(R.drawable.ic_avatar_feedback)
//                            .into(mHeadImage);
//                } else {
//                    GlideApp.with(context).load(feedback.getPortrait())
//                            .circleCrop()
//                            .placeholder(R.drawable.ic_avatar_feedback)
//                            .into(mHeadImage);
//                }
//                if (mImage != null) {
//                    mImage.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (feedback.getContentType() == CONTENT_TYPE_PICTURE) {
//                                PreviewDialogFragment.newInstance(feedback.getContent())
//                                        .showOrDismiss(((FeedbackActivity) context).getSupportFragmentManager());
//                            }
//                        }
//                    });
//                }
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

            CustomerViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(final Feedback feedback, boolean needTitle, final Context context) {
//                if (needTitle) {
//                    mTimeLayout.setVisibility(View.VISIBLE);
//                    mEndLineTime.setText(DateUtil.getFeedbackFormatTime(feedback.getCreateTime()));
//
//                } else {
//                    mTimeLayout.setVisibility(View.GONE);
//                }
//                mTimestamp.setText(DateUtil.format(feedback.getCreateTime(), FORMAT_HOUR_MINUTE));
//                if (feedback.getContentType() == CONTENT_TYPE_TEXT) {
//                    mText.setVisibility(View.VISIBLE);
//                    mImage.setVisibility(View.GONE);
//                    mText.setText(feedback.getContent());
//                } else {
//                    mText.setVisibility(View.GONE);
//                    mImage.setVisibility(View.VISIBLE);
//                    GlideApp.with(context).load(feedback.getContent())
//                            .centerCrop()
//                            .transform(new ThumbTransform(context))
//                            .into(mImage);
//                }
//                GlideApp.with(context).load(feedback.getReplyUserPortrait())
//                        .placeholder(R.drawable.ic_feedback_service)
//                        .circleCrop()
//                        .into(mHeadImage);
//                mImage.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        PreviewDialogFragment.newInstance(feedback.getContent())
//                                .showOrDismiss(((FeedbackActivity) context).getSupportFragmentManager());
//                    }
//                });
            }
        }
    }

}
