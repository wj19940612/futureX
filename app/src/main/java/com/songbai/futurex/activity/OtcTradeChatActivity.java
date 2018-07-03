package com.songbai.futurex.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import com.songbai.futurex.model.status.OtcOrderStatus;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.Network;
import com.songbai.futurex.utils.ThumbTransform;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.CountDownView;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.model.CustomServiceChat;
import com.songbai.futurex.websocket.otc.OtcProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sbai.com.glide.GlideApp;

public class OtcTradeChatActivity extends BaseActivity {
    private static final int DIRECTION_RIGHT = 2;
    private static final int DIRECTION_LEFT = 1;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc_trade_chat);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mOrderId = intent.getIntExtra(ExtraKeys.ORDER_ID, 0);
        mTradeDirection = intent.getIntExtra(ExtraKeys.TRADE_DIRECTION, 0);
        initView();
        loadData();
        initSocketListener();
        if (LocalUser.getUser().isLogin()) {
            initImPush();
        }
        Network.registerNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOtcProcessor != null) {
            mOtcProcessor.pause();
        }
        Network.unregisterNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
    }

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
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
            Launcher.with(OtcTradeChatActivity.this, LoginActivity.class).executeForResult(REQ_LOGIN);
        }
    }

    private void initSocketListener() {
        //初始化推送回调
        mOtcProcessor = new OtcProcessor(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code) {
                new DataParser<Response<OtcChatMessage>>(data) {

                    @Override
                    public void onSuccess(Response<OtcChatMessage> resp) {
                        Log.e(TAG, "onSuccess: 收到消息啦");
                        OtcChatMessage otcChatMessage = resp.getContent();
                        if (otcChatMessage.getDirection()==DIRECTION_RIGHT) {
                            return;
                        }
                        mOtcChatMessages.add(otcChatMessage);
                        mChatAdapter.notifyDataSetChanged();
                        updateRecyclerViewPosition(true);
                    }
                }.parse();
            }
        });
        mOtcProcessor.resume();
    }

    private void initAll() {
        loadData();
        initImPush();
    }

    private void initImPush() {
        mOtcProcessor.registerMsg(mOrderId);
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

    private void otcWaresMine(int orderId) {
        Apic.otcWaresMine("", orderId, 1)
                .callback(new Callback<Resp<WaresUserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<WaresUserInfo> resp) {
                        mTitleBar.setTitle(resp.getData().getUsername());
                    }
                }).fire();
    }

    private void otcOrderDetail() {
        Apic.otcOrderDetail(mOrderId, mTradeDirection)
                .callback(new Callback<Resp<OtcOrderDetail>>() {
                    @Override
                    protected void onRespSuccess(Resp<OtcOrderDetail> resp) {
                        setOrderInfo(resp.getData());
                    }
                }).fire();
    }

    private void otcOrderUser() {
        Apic.otcChatUser(mOrderId)
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
            }
        }
    }

    private void orderPayInfo() {
        Apic.orderPayInfo(mOrderId)
                .callback(new Callback<Resp<List<BankCardBean>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<BankCardBean>> resp) {
                        // TODO: 2018/6/30 增加消息类型
                        OtcBankInfoMsg bankInfoMsg = new OtcBankInfoMsg();
                        bankInfoMsg.setTradeDirect(mTradeDirection);
                        bankInfoMsg.setBankCardBeans(resp.getData());
                        mOtcChatMessages.add(0, bankInfoMsg);
                    }
                }).fire();
    }

    private void setOrderInfo(OtcOrderDetail otcOrderDetail) {
        OtcOrderDetail.OrderBean order = otcOrderDetail.getOrder();
        mTurnover.setText(getString(R.string.x_space_x,
                FinanceUtil.formatWithScale(order.getOrderAmount()),
                order.getPayCurrency().toUpperCase()));
        mCountDownView.setVisibility(View.INVISIBLE);
        switch (order.getStatus()) {
            case OtcOrderStatus.ORDER_UNPAIED:
                mCountDownView.setVisibility(View.VISIBLE);
                mCountDownView.setTimes(order.getOrderTime() + 15 * 60 * 1000);
                mCountDownView.setOnStateChangeListener(new CountDownView.OnStateChangeListener() {
                    @Override
                    public void onStateChange(int countDownState) {
                        if (countDownState == CountDownView.STOPPED) {
                            mCountDownView.setVisibility(View.GONE);
                        }
                    }
                });
                mOrderStatus.setText(R.string.wait_to_pay_and_confirm);
                break;
            case OtcOrderStatus.ORDER_PAIED:
                mOrderStatus.setText(R.string.wait_sell_transfer_coin);
                break;
            case OtcOrderStatus.ORDER_CANCLED:
                mOrderStatus.setText(R.string.canceled);
                break;
            case OtcOrderStatus.ORDER_COMPLATED:
                mOrderStatus.setText(R.string.completed);
                break;
            default:
        }
        switch (mTradeDirection) {
            case OtcOrderStatus.ORDER_DIRECT_BUY:
                mTradeType.setText(getString(R.string.buy_x,
                        order.getCoinSymbol().toUpperCase()));
                break;
            case OtcOrderStatus.ORDER_DIRECT_SELL:
                mTradeType.setText(getString(R.string.sold_x,
                        order.getCoinSymbol().toUpperCase()));
                break;
            default:
        }
    }

    private void loadData() {
        otcWaresMine(mOrderId);
        otcOrderDetail();
        otcOrderUser();
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
        mChatAdapter.notifyDataSetChanged();
        updateRecyclerViewPosition(true);
    }

    private void loadChatData(OtcChatMessage data) {
        mOtcChatMessages.add(data);
        mChatAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_LOGIN && resultCode == RESULT_OK) {
            initAll();
        }
    }

    @OnClick({R.id.send, R.id.addPic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
        } else if (mEditText.getText().length() > 50) {
            ToastUtil.show(R.string.over_50);
        } else if (!TextUtils.isEmpty(mEditText.getText())) {
            requestSendTxtMsg(mEditText.getText().toString());
            mEditText.setText("");
        }
    }

    private void requestSendTxtMsg(final String text) {
        Apic.otcChatSend(text, mOrderId, 1).tag(TAG).callback(new Callback<Resp>() {
            @Override
            protected void onRespSuccess(Resp resp) {
                updateSendMsgUI(text, CustomServiceChat.SEND_SUCCESS);
            }

            @Override
            public void onFailure(ReqError reqError) {
                super.onFailure(reqError);
                updateSendMsgUI(text, CustomServiceChat.SEND_FAILED);
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
        OtcChatMessage customServiceChat = new OtcChatMessage(OtcChatMessage.MSG_TEXT, text, 0, 0, status);
        loadChatData(customServiceChat);
        updateRecyclerViewPosition(true);
    }

    private void updateSendPhotoUI(String photoAddress, int status) {
        OtcChatMessage customServiceChat = new OtcChatMessage(OtcChatMessage.MSG_PHOTO, photoAddress, 0, 0, status);
        loadChatData(customServiceChat);
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
        Apic.uploadImage(image)
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
                updateRecyclerViewPosition(true);
            }
        };
    }

    private void resendMsg(OtcChatMessage otcChatMessage) {
        if (otcChatMessage.getMsgType() == 2) {
            requestSendPhotoMsg(otcChatMessage.getMessage());
        } else {
            requestSendTxtMsg(otcChatMessage.getMessage());
        }
    }

    private void updateRecyclerViewPosition(boolean isSend) {
        if (isSend) {
            mRecycleView.smoothScrollToPosition(Integer.MAX_VALUE);
        } else if (((LinearLayoutManager) mRecycleView.getLayoutManager()).findLastCompletelyVisibleItemPosition() == mChatAdapter.getItemCount() - 2) {
            mRecycleView.smoothScrollToPosition(Integer.MAX_VALUE);
        }
    }

    static class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int TYPE_LEFT = 1;
        public static final int TYPE_LEFT_PHOTO = 2;
        public static final int TYPE_RIGHT = 3;
        public static final int TYPE_RIGHT_PHOTO = 4;
        public static final int TYPE_SYSTEM = 5;
        public static final int TYPE_BANKINFO = 6;

        private List<Object> mList;
        private Context mContext;
        private OnRetryClickListener mOnRetryClickListener;

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
                if (message.getDirection() == DIRECTION_LEFT && !(message.getMsgType() == 2)) {
                    return TYPE_LEFT;
                } else if (message.getDirection() == DIRECTION_RIGHT && message.getMsgType() == 2) {
                    return TYPE_LEFT_PHOTO;
                } else if (message.getMsgType() == 2) {
                    return TYPE_RIGHT_PHOTO;
                }
            }
            if (obj instanceof OtcBankInfoMsg) {
                return TYPE_BANKINFO;
            }
            return TYPE_RIGHT;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_LEFT) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left_content, parent, false);
                return new LeftTextHolder(view);
            } else if (viewType == TYPE_RIGHT) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right_content, parent, false);
                return new RightTextHolder(view);
            } else if (viewType == TYPE_RIGHT_PHOTO) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right_photo, parent, false);
                return new RightPhotoHolder(view);
            } else if (viewType == TYPE_LEFT_PHOTO) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left_photo, parent, false);
                return new LeftPhotoHolder(view);
            } else if (viewType == TYPE_BANKINFO) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_otc_chat_pay_info, parent, false);
                return new PayInfoHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            OtcChatMessage lastChat = null;
            if (position > 0) {
                Object obj = mList.get(position - 1);
                if (obj instanceof OtcChatMessage) {
                    lastChat = (OtcChatMessage) obj;
                }
            }
            if (holder instanceof PayInfoHolder) {
                ((PayInfoHolder) holder).bindData((OtcBankInfoMsg) mList.get(position), mContext);
            } else if (holder instanceof LeftTextHolder) {
                ((LeftTextHolder) holder).bindingData(mContext, (OtcChatMessage) mList.get(position), lastChat, position, getItemCount());
            } else if (holder instanceof RightTextHolder) {
                ((RightTextHolder) holder).bindingData(mOnRetryClickListener, mContext, (OtcChatMessage) mList.get(position), lastChat, position, getItemCount());
            } else if (holder instanceof RightPhotoHolder) {
                ((RightPhotoHolder) holder).bindingData(mOnRetryClickListener, mContext, (OtcChatMessage) mList.get(position), lastChat, position, getItemCount());
            } else if (holder instanceof LeftPhotoHolder) {
                ((LeftPhotoHolder) holder).bindingData(mContext, (OtcChatMessage) mList.get(position), lastChat, position, getItemCount());
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

            private void bindingData(Context context, OtcChatMessage otcChatMessage, OtcChatMessage lastChat, int position, int itemCount) {
                boolean right = otcChatMessage.getDirection() == DIRECTION_RIGHT;
                mName.setText(right ? mRightOtcChatUserInfo.getUserName() : mLeftOtcChatUserInfo.getUserName());
                GlideApp.with(context)
                        .load(right ? mRightOtcChatUserInfo.getUserPortrait() : mLeftOtcChatUserInfo.getUserPortrait())
                        .centerCrop()
                        .into(mHead);
                mContent.setText(otcChatMessage.getMessage());
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

            public void bindingData(Context context, OtcChatMessage otcChatMessage, OtcChatMessage lastChat, int position, int itemCount) {
                boolean right = otcChatMessage.getDirection() == DIRECTION_RIGHT;
                mName.setText(right ? mRightOtcChatUserInfo.getUserName() : mLeftOtcChatUserInfo.getUserName());
                GlideApp.with(context)
                        .load(right ? mRightOtcChatUserInfo.getUserPortrait() : mLeftOtcChatUserInfo.getUserPortrait())
                        .centerCrop()
                        .into(mHead);
                mTime.setText(DateUtil.getFormatTime(otcChatMessage.getCreateTime()));

                if (otcChatMessage.getMsgType() == 2) {
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

            public void bindingData(final OnRetryClickListener onRetryClickListener, Context context, final OtcChatMessage otcChatMessage, OtcChatMessage lastChat, int position, int itemCount) {
                boolean right = otcChatMessage.getDirection() == DIRECTION_RIGHT;
                mName.setText(right ? mRightOtcChatUserInfo.getUserName() : mLeftOtcChatUserInfo.getUserName());
                GlideApp.with(context)
                        .load(right ? mRightOtcChatUserInfo.getUserPortrait() : mLeftOtcChatUserInfo.getUserPortrait())
                        .centerCrop()
                        .into(mHead);
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

            public void bindingData(final OnRetryClickListener onRetryClickListener, Context context, final OtcChatMessage otcChatMessage, OtcChatMessage lastChat, int position, int itemCount) {
                boolean right = otcChatMessage.getDirection() == DIRECTION_RIGHT;
                mName.setText(right ? mRightOtcChatUserInfo.getUserName() : mLeftOtcChatUserInfo.getUserName());
                GlideApp.with(context)
                        .load(right ? mRightOtcChatUserInfo.getUserPortrait() : mLeftOtcChatUserInfo.getUserPortrait())
                        .centerCrop()
                        .into(mHead);

                mTime.setText(DateUtil.getFormatTime(otcChatMessage.getCreateTime()));

                if (otcChatMessage.getMsgType() == 2) {
                    GlideApp.with(context).load(otcChatMessage.getMessage())
                            .centerCrop()
                            .transform(new ThumbTransform(context))
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    if (onRetryClickListener != null) {
                                        onRetryClickListener.onReScroll();
                                    }
                                    return false;
                                }
                            })
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
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
                    case OtcOrderStatus.ORDER_DIRECT_BUY:
                        mStatusHint.setText(R.string.pay_info_hint_to_buyer);
                        mPayInfoHint.setText(R.string.pay_info_safety_hint_to_buyer);
                        break;
                    case OtcOrderStatus.ORDER_DIRECT_SELL:
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

                    public void bandData(BankCardBean bankCardBean) {
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
                                mAccountType.setText(bankCardBean.getBankName());
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
}
