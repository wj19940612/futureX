package com.songbai.futurex.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.httplib.ReqError;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.fragment.dialog.UploadUserImageDialogFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.OtcChatMessage;
import com.songbai.futurex.model.OtcChatUserInfo;
import com.songbai.futurex.model.OtcOrderDetail;
import com.songbai.futurex.model.WaresUserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.local.OtcBankInfoMsg;
import com.songbai.futurex.model.mine.BankCardBean;
import com.songbai.futurex.model.mine.SysMessage;
import com.songbai.futurex.model.status.OTCOrderStatus;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.Network;
import com.songbai.futurex.utils.ThumbTransform;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.CountDownView;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.PushDestUtils;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.msg.MsgProcessor;
import com.songbai.futurex.websocket.otc.OtcProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sbai.com.glide.GlideApp;

public class OtcTradeChatActivity extends BaseActivity {
    private static final int CONTENT_MAX_LENGTH = 500;


    @BindView(R.id.recycleView)
    RecyclerView mRecycleView;
    @BindView(R.id.send)
    TextView mSend;
    @BindView(R.id.editText)
    EditText mEditText;
    @BindView(R.id.addPic)
    ImageButton mAddPic;
    @BindView(R.id.bottomLayout)
    RelativeLayout mBottomLayout;
    @BindView(R.id.tradeType)
    TextView mTradeType;
    @BindView(R.id.turnoverText)
    TextView mTurnoverText;
    @BindView(R.id.turnover)
    TextView mTurnover;
    @BindView(R.id.orderStatus)
    TextView mOrderStatus;
    @BindView(R.id.countDownView)
    CountDownView mCountDownView;
    @BindView(R.id.orderInfo)
    ConstraintLayout mOrderInfo;
    @BindView(R.id.title)
    TextView mTitle;

    private boolean mKeyboardInit = false;

    private List<Object> mOtcChatMessages;
    private ChatAdapter mChatAdapter;

    private OtcProcessor mOtcProcessor;

    private int mOrderId;
    private int mTradeDirection;
    private static OtcChatUserInfo mRightOtcChatUserInfo;
    private static OtcChatUserInfo mLeftOtcChatUserInfo;

    private Network.NetworkChangeReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                initImPush();
                loadData();
            }
        }
    };
    private OtcBankInfoMsg mBankInfoMsg;
    private int mOtcChatUserId;
    private MsgProcessor mMsgProcessor;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc_trade_chat);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mOrderId = intent.getIntExtra(ExtraKeys.ORDER_ID, 0);
        mTradeDirection = intent.getIntExtra(ExtraKeys.TRADE_DIRECTION, 0);
        LocalUser user = LocalUser.getUser();
        if (user.isLogin()) {
            mRightOtcChatUserInfo = new OtcChatUserInfo();
            mRightOtcChatUserInfo.setId(user.getUserInfo().getId());
            mRightOtcChatUserInfo.setUserName(user.getUserInfo().getUserName());
            mRightOtcChatUserInfo.setUserPortrait(user.getUserInfo().getUserPortrait());
            mRightOtcChatUserInfo.setCertificationLevel(user.getUserInfo().getCertificationLevel());
        }
        initView();
        loadData();
        initSocketListener();
        if (LocalUser.getUser().isLogin()) {
            initImPush();
        }
        Network.registerNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOtcProcessor.resume();
        mMsgProcessor.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOtcProcessor.pause();
        mMsgProcessor.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOtcProcessor != null) {
            mOtcProcessor.unregisterMsg(mOrderId);
            mOtcProcessor.unregisterEntrust(mOtcChatUserId);
            mMsgProcessor.unregisterMsg();
        }
        Network.unregisterNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mOtcChatMessages = new ArrayList<>();
        mChatAdapter = new ChatAdapter(mOtcChatMessages, getActivity(), getOnRetryClickListener());
        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mEditText.clearFocus();
                }
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(mLinearLayoutManager);
        mChatAdapter.setRightDirection(mTradeDirection);
        mRecycleView.setAdapter(mChatAdapter);

        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyboard();
                } else {
                    hideKeyboard();
                }
            }
        });
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    checkUserStatus();
                }
                return false;
            }
        });
    }

    private void checkUserStatus() {
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(OtcTradeChatActivity.this, LoginActivity.class).execute(REQ_CODE_LOGIN);
        }
    }

    private void initSocketListener() {
        mOtcProcessor = new OtcProcessor(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code, String dest) {
                if (PushDestUtils.isOtcChat(mOrderId, dest)) {
                    new DataParser<Response<OtcChatMessage>>(data) {

                        @Override
                        public void onSuccess(Response<OtcChatMessage> resp) {
                            OtcChatMessage otcChatMessage = resp.getContent();
                            if (otcChatMessage.getDirection() == mTradeDirection) {
                                return;
                            }
                            mOtcChatMessages.add(otcChatMessage);
                            mChatAdapter.notifyDataSetChanged();
                            updateRecyclerViewPosition(true);
                        }
                    }.parse();
                }
            }
        });

        mMsgProcessor = new MsgProcessor(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code, String dest) {
                if (PushDestUtils.isMsg(dest)) {
                    new DataParser<Response<SysMessage>>(data) {

                        @Override
                        public void onSuccess(Response<SysMessage> resp) {
                            if (resp.getContent().getDataId() == mOrderId) {
                                otcOrderDetail();
                            }
                        }
                    }.parse();
                }
            }
        });
    }

    private void initAll() {
        loadData();
        initImPush();
    }

    private void initImPush() {
        mOtcProcessor.registerMsg(mOrderId);
        mMsgProcessor.registerMsg();
    }

    private void showKeyboard() {
        if (!mKeyboardInit) {
            mKeyboardInit = true;
        }
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(mEditText, 0);
    }

    private void hideKeyboard() {
        if (mKeyboardInit) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(mEditText.getWindowToken(), InputMethodManager
                            .HIDE_NOT_ALWAYS);
        }
    }

    private void otcWaresMine() {
        Apic.otcWaresMine("", String.valueOf(mOrderId), 1).tag(TAG)
                .callback(new Callback<Resp<WaresUserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<WaresUserInfo> resp) {
                        mTitle.setText(resp.getData().getUsername());
                        mTitle.setSelected(true);
                    }
                }).fire();
    }

    private void otcOrderDetail() {
        Apic.otcOrderDetail(mOrderId, mTradeDirection).tag(TAG)
                .callback(new Callback<Resp<OtcOrderDetail>>() {
                    @Override
                    protected void onRespSuccess(Resp<OtcOrderDetail> resp) {
                        setOrderInfo(resp.getData());
                    }
                }).fire();
    }

    private void otcOrderUser() {
        Apic.otcChatUser(mOrderId).tag(TAG)
                .callback(new Callback<Resp<ArrayList<OtcChatUserInfo>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<OtcChatUserInfo>> resp) {
                        setUserInfo(resp.getData());
                    }
                }).fire();
    }

    private void setUserInfo(ArrayList<OtcChatUserInfo> data) {
        LocalUser user = LocalUser.getUser();
        int id = 0;
        if (user.isLogin()) {
            id = user.getUserInfo().getId();
        }
        for (OtcChatUserInfo otcChatUserInfo : data) {
            if (otcChatUserInfo.getId() == id) {
                mRightOtcChatUserInfo = otcChatUserInfo;
            } else {
                mLeftOtcChatUserInfo = otcChatUserInfo;
                mOtcChatUserId = mLeftOtcChatUserInfo.getId();
                mOtcProcessor.registerEntrust(mOtcChatUserId);
            }
        }
    }

    private void orderPayInfo() {
        Apic.orderPayInfo(mOrderId).tag(TAG)
                .callback(new Callback<Resp<List<BankCardBean>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<BankCardBean>> resp) {
                        mBankInfoMsg = new OtcBankInfoMsg();
                        mBankInfoMsg.setTradeDirect(mTradeDirection);
                        mBankInfoMsg.setBankCardBeans(resp.getData());
                        mOtcChatMessages.add(0, mBankInfoMsg);
                        mChatAdapter.notifyDataSetChanged();
                        scrollToBottom();
                    }
                }).fire();
    }

    private void setOrderInfo(OtcOrderDetail otcOrderDetail) {
        OtcOrderDetail.OrderBean order = otcOrderDetail.getOrder();
        mTurnover.setText(getString(R.string.x_space_x,
                FinanceUtil.formatWithScale(order.getOrderAmount()),
                order.getPayCurrency().toUpperCase()));
        mCountDownView.setVisibility(View.INVISIBLE);
        mCountDownView.setColonColor(ContextCompat.getColor(this, R.color.text22));
        switch (order.getStatus()) {
            case OTCOrderStatus.ORDER_UNPAIED:
                mCountDownView.setVisibility(View.VISIBLE);
                long endTime = System.currentTimeMillis() + order.getCountDown() * 1000;
                if (System.currentTimeMillis() < endTime) {
                    mCountDownView.setTimes(endTime);
                    mCountDownView.beginRun();
                    mCountDownView.setOnStateChangeListener(new CountDownView.OnStateChangeListener() {
                        @Override
                        public void onStateChange(int countDownState) {
                            if (countDownState == CountDownView.STOPPED) {
                                mCountDownView.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                mOrderStatus.setText(R.string.unpaid);
                break;
            case OTCOrderStatus.ORDER_PAIED:
                mOrderStatus.setText(R.string.paid);
                break;
            case OTCOrderStatus.ORDER_CANCLED:
                mOrderStatus.setText(R.string.canceled);
                break;
            case OTCOrderStatus.ORDER_COMPLATED:
                mOrderStatus.setText(R.string.completed);
                break;
            default:
        }
        switch (mTradeDirection) {
            case OTCOrderStatus.ORDER_DIRECT_BUY:
                mTradeType.setText(getString(R.string.buy_x,
                        order.getCoinSymbol().toUpperCase()));
                break;
            case OTCOrderStatus.ORDER_DIRECT_SELL:
                mTradeType.setText(getString(R.string.sold_x,
                        order.getCoinSymbol().toUpperCase()));
                break;
            default:
        }
    }

    private void loadData() {
        otcOrderUser();
        otcWaresMine();
        otcOrderDetail();
        orderPayInfo();
        loadHistoryData();
    }

    private void loadHistoryData() {
        Apic.otcChatHistory(mOrderId, "", 200).tag(TAG).callback(new Callback<Resp<List<OtcChatMessage>>>() {
            @Override
            protected void onRespSuccess(Resp<List<OtcChatMessage>> resp) {
                if (resp != null) {
                    loadChatData(resp.getData());
                }
            }
        }).fireFreely();
    }

    private void loadChatData(List<OtcChatMessage> data) {
        Collections.reverse(data);
        mOtcChatMessages.clear();
        mOtcChatMessages.addAll(data);
        if (mBankInfoMsg != null) {
            mOtcChatMessages.add(0, mBankInfoMsg);
        }
        scrollToBottom();
    }

    private void loadChatData(OtcChatMessage data) {
        mOtcChatMessages.add(data);
        mChatAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            initAll();
        }
    }

    @OnClick({R.id.ivBack, R.id.send, R.id.addPic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.send:
                sendMsg();
                break;
            case R.id.addPic:
                sendPicToCustomer();
                break;
            default:
        }
    }

    private void sendMsg() {
        if (!Network.isNetworkAvailable()) {
            ToastUtil.show(R.string.http_error_network);
        } else if (!LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        } else {
            if (mEditText.getText().length() > CONTENT_MAX_LENGTH) {
                ToastUtil.show(R.string.over_500);
            } else if (!TextUtils.isEmpty(mEditText.getText().toString().trim())) {
                requestSendTxtMsg(mEditText.getText().toString().trim());
                mEditText.setText("");
            }
        }
    }

    private void requestSendTxtMsg(final String text) {
        Apic.otcChatSend(text, mOrderId, 1).tag(TAG).callback(new Callback<Resp>() {
            @Override
            protected void onRespSuccess(Resp resp) {
                updateSendMsgUI(text, OtcChatMessage.SEND_SUCCESS);
            }

            @Override
            public void onFailure(ReqError reqError) {
                super.onFailure(reqError);
                updateSendMsgUI(text, OtcChatMessage.SEND_FAILED);
            }
        }).fire();
    }

    private void requestSendPhotoMsg(final String photoAddress) {
        Apic.otcChatSend(photoAddress, mOrderId, 2).tag(TAG).callback(new Callback<Resp>() {
            @Override
            protected void onRespSuccess(Resp resp) {
                updateSendPhotoUI(photoAddress, OtcChatMessage.SEND_SUCCESS);
            }

            @Override
            public void onFailure(ReqError reqError) {
                super.onFailure(reqError);
                updateSendPhotoUI(photoAddress, OtcChatMessage.SEND_FAILED);
            }
        }).fireFreely();
    }

    private void updateSendMsgUI(String text, int status) {
        OtcChatMessage otcChatMessage = new OtcChatMessage(OtcChatMessage.MSG_TEXT, mTradeDirection, text, mLeftOtcChatUserInfo.getId(), mRightOtcChatUserInfo.getId(), status);
        loadChatData(otcChatMessage);
        updateRecyclerViewPosition(true);
    }

    private void updateSendPhotoUI(String photoAddress, int status) {
        OtcChatMessage otcChatMessage = new OtcChatMessage(OtcChatMessage.MSG_PHOTO, mTradeDirection, photoAddress, mLeftOtcChatUserInfo.getId(), mRightOtcChatUserInfo.getId(), status);
        loadChatData(otcChatMessage);
        updateRecyclerViewPosition(true);
    }

    private void sendPicToCustomer() {
        UploadUserImageDialogFragment.newInstance(UploadUserImageDialogFragment.IMAGE_TYPE_OPEN_CUSTOM_GALLERY)
                .setOnImagePathListener(new UploadUserImageDialogFragment.OnImagePathListener() {
                    @Override
                    public void onImagePath(int index, String imagePath) {
                        sendImage(imagePath);
                    }
                }).show(getSupportFragmentManager());
    }

    private void sendImage(final String path) {
        String image = ImageUtils.compressImageToBase64(path, getActivity());
        Apic.uploadImage(image).tag(TAG)
                .callback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        if (resp != null && resp.getData() != null) {
                            requestSendPhotoMsg(resp.getData());
                        }
                    }
                })
                .fireFreely();
    }

    private ChatAdapter.OnRetryClickListener getOnRetryClickListener() {
        return new ChatAdapter.OnRetryClickListener() {
            @Override
            public void onRetry(OtcChatMessage otcChatMessage) {
                mOtcChatMessages.remove(otcChatMessage);
                mChatAdapter.notifyDataSetChanged();
                updateRecyclerViewPosition(true);
                resendMsg(otcChatMessage);
            }

            @Override
            public void onReScroll() {
                updateRecyclerViewPosition(false);
            }
        };
    }

    private void resendMsg(OtcChatMessage otcChatMessage) {
        if (otcChatMessage.getMsgType() == OtcChatMessage.MSG_PHOTO) {
            requestSendPhotoMsg(otcChatMessage.getMessage());
        } else {
            requestSendTxtMsg(otcChatMessage.getMessage());
        }
    }

    private void updateRecyclerViewPosition(boolean isSend) {
        if (isSend) {
            mRecycleView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
        } else if (((LinearLayoutManager) mRecycleView.getLayoutManager()).findLastCompletelyVisibleItemPosition() == mChatAdapter.getItemCount() - 2) {
            mRecycleView.scrollToPosition(mChatAdapter.getItemCount() - 1);
        }
    }

    private void scrollToBottom() {
        mLinearLayoutManager.scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);//先要滚动到这个位置
        mRecycleView.post(new Runnable() {
            @Override
            public void run() {
                View target = mLinearLayoutManager.findViewByPosition(mChatAdapter.getItemCount() - 1);//然后才能拿到这个View
                if (target != null) {
                    mLinearLayoutManager.scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1,
                            mRecycleView.getMeasuredHeight() - target.getMeasuredHeight());//滚动偏移到底部
                }
            }
        });
    }

    static class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        static final int TYPE_LEFT = 1;
        static final int TYPE_LEFT_PHOTO = 2;
        static final int TYPE_RIGHT = 3;
        static final int TYPE_RIGHT_PHOTO = 4;
        static final int TYPE_BANK_INFO = 5;

        private List<Object> mList;
        private Context mContext;
        private OnRetryClickListener mOnRetryClickListener;
        private int mRightDirection;

        void setRightDirection(int rightDirection) {
            mRightDirection = rightDirection;
        }

        interface OnRetryClickListener {
            void onRetry(OtcChatMessage customServiceChat);

            void onReScroll();
        }

        public ChatAdapter(List<Object> list, Context context, OnRetryClickListener onRetryClickListener) {
            mList = list;
            mContext = context;
            mOnRetryClickListener = onRetryClickListener;
        }

        @Override
        public int getItemViewType(int position) {
            if (mList == null || mList.size() == 0) {
                return TYPE_LEFT;
            }
            Object obj = mList.get(position);
            if (obj instanceof OtcChatMessage) {
                OtcChatMessage message = (OtcChatMessage) obj;
                if (message.getDirection() == mRightDirection && (message.getMsgType() != OtcChatMessage.MSG_PHOTO)) {
                    return TYPE_RIGHT;
                } else if (message.getDirection() == mRightDirection && message.getMsgType() == OtcChatMessage.MSG_PHOTO) {
                    return TYPE_RIGHT_PHOTO;
                } else if (message.getMsgType() == OtcChatMessage.MSG_PHOTO) {
                    return TYPE_LEFT_PHOTO;
                }
            }
            if (obj instanceof OtcBankInfoMsg) {
                return TYPE_BANK_INFO;
            }
            return TYPE_LEFT;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_LEFT) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left_content, parent, false);
                return new LeftTextHolder(view);
            } else if (viewType == TYPE_RIGHT) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.row_otc_chat_right_content, parent, false);
                return new RightTextHolder(view);
            } else if (viewType == TYPE_RIGHT_PHOTO) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right_photo, parent, false);
                return new RightPhotoHolder(view);
            } else if (viewType == TYPE_LEFT_PHOTO) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left_photo, parent, false);
                return new LeftPhotoHolder(view);
            } else if (viewType == TYPE_BANK_INFO) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_otc_chat_pay_info, parent, false);
                return new PayInfoHolder(view);
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left_content, parent, false);
            return new LeftTextHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PayInfoHolder) {
                ((PayInfoHolder) holder).bindData((OtcBankInfoMsg) mList.get(position), mContext);
            } else if (holder instanceof LeftTextHolder) {
                ((LeftTextHolder) holder).bindingData(mContext, (OtcChatMessage) mList.get(position));
            } else if (holder instanceof RightTextHolder) {
                ((RightTextHolder) holder).bindingData(mOnRetryClickListener, mContext, (OtcChatMessage) mList.get(position));
            } else if (holder instanceof RightPhotoHolder) {
                ((RightPhotoHolder) holder).bindingData(mOnRetryClickListener, mContext, (OtcChatMessage) mList.get(position));
            } else if (holder instanceof LeftPhotoHolder) {
                ((LeftPhotoHolder) holder).bindingData(mContext, (OtcChatMessage) mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        static class LeftTextHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.head)
            ImageView mHead;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.content)
            TextView mContent;
            @BindView(R.id.time)
            TextView mTime;

            LeftTextHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, OtcChatMessage otcChatMessage) {
                if (mLeftOtcChatUserInfo != null) {
                    mName.setText(mLeftOtcChatUserInfo.getUserName());
                    GlideApp.with(context)
                            .load(mLeftOtcChatUserInfo.getUserPortrait())
                            .circleCrop()
                            .into(mHead);
                    mContent.setText(otcChatMessage.getMessage());
                }
                mTime.setText(DateUtil.getFormatTime(otcChatMessage.getCreateTime()));
            }
        }

        static class LeftPhotoHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.head)
            ImageView mHead;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.photo)
            ImageView mPhoto;
            @BindView(R.id.time)
            TextView mTime;

            LeftPhotoHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            void bindingData(Context context, OtcChatMessage otcChatMessage) {
                if (mLeftOtcChatUserInfo != null) {
                    mName.setText(mLeftOtcChatUserInfo.getUserName());
                    GlideApp.with(context)
                            .load(mLeftOtcChatUserInfo.getUserPortrait())
                            .circleCrop()
                            .into(mHead);
                }
                mTime.setText(DateUtil.getFormatTime(otcChatMessage.getCreateTime()));

                if (otcChatMessage.getMsgType() == OtcChatMessage.MSG_PHOTO) {
                    GlideApp.with(context).load(otcChatMessage.getMessage())
                            .centerCrop()
                            .transform(new ThumbTransform(context))
                            .into(mPhoto);
                }
            }
        }

        static class RightTextHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.head)
            ImageView mHead;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.content)
            TextView mContent;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.retry)
            ImageView mRetry;

            RightTextHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            void bindingData(final OnRetryClickListener onRetryClickListener, Context context, final OtcChatMessage otcChatMessage) {
                if (mRightOtcChatUserInfo != null) {
                    mName.setText(mRightOtcChatUserInfo.getUserName());
                    GlideApp.with(context)
                            .load(mRightOtcChatUserInfo.getUserPortrait())
                            .circleCrop()
                            .into(mHead);
                }
                mContent.setText(otcChatMessage.getMessage());

                mTime.setText(DateUtil.getFormatTime(otcChatMessage.getCreateTime()));
                mRetry.setVisibility(otcChatMessage.isSuccess() ? View.GONE : View.VISIBLE);
                mRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onRetryClickListener != null) {
                            onRetryClickListener.onRetry(otcChatMessage);
                        }
                    }
                });

            }
        }

        static class RightPhotoHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.head)
            ImageView mHead;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.photo)
            ImageView mPhoto;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.retry)
            ImageView mRetry;

            RightPhotoHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            void bindingData(final OnRetryClickListener onRetryClickListener, Context context, final OtcChatMessage otcChatMessage) {
                if (mRightOtcChatUserInfo != null) {
                    mName.setText(mRightOtcChatUserInfo.getUserName());
                    GlideApp.with(context)
                            .load(mRightOtcChatUserInfo.getUserPortrait())
                            .circleCrop()
                            .into(mHead);
                }
                mTime.setText(DateUtil.getFormatTime(otcChatMessage.getCreateTime()));

                if (otcChatMessage.getMsgType() == OtcChatMessage.MSG_PHOTO) {
                    GlideApp.with(context).load(otcChatMessage.getMessage())
                            .centerCrop()
                            .transform(new ThumbTransform(context))
                            .into(mPhoto);
                }

                mRetry.setVisibility(otcChatMessage.isSuccess() ? View.GONE : View.VISIBLE);
                mRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onRetryClickListener != null) {
                            onRetryClickListener.onRetry(otcChatMessage);
                        }
                    }
                });
            }
        }

        static class PayInfoHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.statusHint)
            TextView mStatusHint;
            @BindView(R.id.recyclerView)
            RecyclerView mRecyclerView;
            @BindView(R.id.payInfoHint)
            TextView mPayInfoHint;

            PayInfoHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindData(OtcBankInfoMsg otcBankInfoMsg, Context context) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                mRecyclerView.setAdapter(new BankInfoAdapter(otcBankInfoMsg.getBankCardBeans()));
                switch (otcBankInfoMsg.getTradeDirect()) {
                    case OTCOrderStatus.ORDER_DIRECT_BUY:
                        mStatusHint.setText(R.string.pay_info_hint_to_buyer);
                        mPayInfoHint.setText(R.string.pay_info_safety_hint_to_buyer);
                        break;
                    case OTCOrderStatus.ORDER_DIRECT_SELL:
                        mStatusHint.setText(R.string.pay_info_hint_to_seller);
                        mPayInfoHint.setText(R.string.pay_info_safety_hint_to_seller);
                        break;
                    default:
                }
            }

            class BankInfoAdapter extends RecyclerView.Adapter {

                private final List<BankCardBean> mBankCardBeans;

                BankInfoAdapter(List<BankCardBean> bankCardBeans) {
                    mBankCardBeans = bankCardBeans;
                }

                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_otc_chat_bank_list, parent, false);
                    return new BankInfoHolder(view);
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                    if (holder instanceof BankInfoHolder) {
                        ((BankInfoHolder) holder).bandData(mBankCardBeans.get(position));
                    }
                }

                @Override
                public int getItemCount() {
                    return mBankCardBeans.size();
                }

                class BankInfoHolder extends RecyclerView.ViewHolder {
                    @BindView(R.id.accountType)
                    TextView mAccountType;
                    @BindView(R.id.bankName)
                    TextView mBankName;
                    @BindView(R.id.bankBranch)
                    TextView mBankBranch;
                    @BindView(R.id.name)
                    TextView mName;
                    @BindView(R.id.cardNum)
                    TextView mCardNum;

                    BankInfoHolder(View view) {
                        super(view);
                        ButterKnife.bind(this, view);
                    }

                    void bandData(BankCardBean bankCardBean) {
                        switch (bankCardBean.getPayType()) {
                            case BankCardBean.PAYTYPE_ALIPAY:
                                mAccountType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pay_alipay_s, 0, 0, 0);
                                mAccountType.setText(R.string.alipay);
                                break;
                            case BankCardBean.PAYTYPE_WX:
                                mAccountType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pay_wechat_s, 0, 0, 0);
                                mAccountType.setText(R.string.wechatpay);
                                break;
                            case BankCardBean.PAYTYPE_BANK:
                                mAccountType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pay_unionpay_s, 0, 0, 0);
                                mAccountType.setText(R.string.bank_card);
                                break;
                            default:
                        }
                        String bankName = bankCardBean.getBankName();
                        if (!TextUtils.isEmpty(bankName)) {
                            mBankName.setText(bankName);
                            mBankName.setVisibility(View.VISIBLE);
                        } else {
                            mBankName.setVisibility(View.GONE);
                        }
                        String realName = bankCardBean.getRealName();
                        if (!TextUtils.isEmpty(realName)) {
                            mName.setText(realName);
                            mName.setVisibility(View.VISIBLE);
                        } else {
                            mName.setVisibility(View.GONE);
                        }
                        mCardNum.setText(bankCardBean.getCardNumber());
                        String bankBranch = bankCardBean.getBankBranch();
                        if (!TextUtils.isEmpty(bankBranch)) {
                            mBankBranch.setText(bankBranch);
                            mBankBranch.setVisibility(View.VISIBLE);
                        } else {
                            mBankBranch.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (KeyBoardUtils.isOutside(ev, mBottomLayout)) {
                if (KeyBoardUtils.isShouldHideKeyboard(v, ev)) {
                    KeyBoardUtils.closeKeyboard(v);
                    v.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }
}
