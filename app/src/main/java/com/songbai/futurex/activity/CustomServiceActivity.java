package com.songbai.futurex.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.fragment.dialog.UploadUserImageDialogFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CustomerService;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.CustomServiceInfo;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.Network;
import com.songbai.futurex.utils.ThumbTransform;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.MsgHintController;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.PushDestUtils;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.im.IMProcessor;
import com.songbai.futurex.websocket.model.CustomServiceChat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sbai.com.glide.GlideApp;

public class CustomServiceActivity extends BaseActivity {
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
    @BindView(R.id.inputGroup)
    RelativeLayout mInputGroup;

    private boolean mKeyboardInit = false;

    private List<CustomServiceChat> mCustomServiceChats;
    private ChatAdapter mChatAdapter;

    private IMProcessor mIMProcessor;

    private Network.NetworkChangeReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
//                registerImPush();
//                loadServiceData();
            }
        }
    };
    private CustomerService mCustomerService;
    private long idleStart = System.currentTimeMillis();
    private SmartDialog mReconnectSmartDialog;
    private boolean mHasShowReconnectDialog;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean mConnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_service);
        ButterKnife.bind(this);
        initView();
        loadServiceData();
        initSocketListener();
        registerImPush();
        Network.registerNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScheduleJobRightNow(30 * 1000);
        chatOnline();
        mIMProcessor.resume();
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        if (mConnected) {
            if (System.currentTimeMillis() - idleStart > 10 * 60 * 1000) {
                showReconnectDialog();
            } else {
                chatOnline();
            }
        }
    }

    private void showReconnectDialog() {
        if (mHasShowReconnectDialog) {
            return;
        }
        MsgHintController withDrawPsdViewController = new MsgHintController(getActivity(), new MsgHintController.OnClickListener() {
            @Override
            public void onConfirmClick() {
                unregisterImPush();
                loadServiceData();
                mHasShowReconnectDialog = false;
            }
        });
        mReconnectSmartDialog = SmartDialog.solo(getActivity());
        mReconnectSmartDialog.setCustomViewController(withDrawPsdViewController)
                .setCancelableOnTouchOutside(false)
                .show();
        withDrawPsdViewController.setConfirmText(R.string.reconnect);
        withDrawPsdViewController.setCloseText(R.string.close_session);
        withDrawPsdViewController.setOnColseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                mHasShowReconnectDialog = false;
            }
        });
        withDrawPsdViewController.setMsg(R.string.long_time_not_contract_need_reconnect);
        withDrawPsdViewController.setImageRes(R.drawable.ic_popup_attention);
        withDrawPsdViewController.setCroseVisibility(View.GONE);
        mHasShowReconnectDialog = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScheduleJob();
        mIMProcessor.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterImPush();
        Network.unregisterNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
    }

    private void unregisterImPush() {
        if (mIMProcessor != null) {
            mIMProcessor.unregisterMsg();
            mIMProcessor.unregisterOffline();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mCustomServiceChats = new ArrayList<>();
        mChatAdapter = new ChatAdapter(mCustomServiceChats, getActivity(), getOnRetryClickListener());
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
    }

    private void initSocketListener() {
        mIMProcessor = new IMProcessor(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code, final String dest) {
                if (PushDestUtils.isCustomerChat(dest)) {
                    new DataParser<Response<CustomServiceChat>>(data) {

                        @Override
                        public void onSuccess(Response<CustomServiceChat> customServiceChatResponse) {
                            CustomServiceChat content = customServiceChatResponse.getContent();
                            if (content.getDirection() == 1) {
                                return;
                            }
                            mCustomServiceChats.add(content);
                            mChatAdapter.notifyDataSetChanged();
                            updateRecyclerViewPosition(true);
                        }
                    }.parse();
                } else if (PushDestUtils.isServiceOffline(dest)) {
                    new DataParser<Response<CustomServiceInfo>>(data) {

                        @Override
                        public void onSuccess(Response<CustomServiceInfo> customServiceChatResponse) {
                            if (mCustomerService != null) {
                                if (mCustomerService.getId() == customServiceChatResponse.getContent().getId()) {
                                    showChangeServiceDialog();
                                }
                            }
                        }
                    }.parse();
                }
            }
        });
    }

    private void showChangeServiceDialog() {
        MsgHintController withDrawPsdViewController = new MsgHintController(getActivity(), new MsgHintController.OnClickListener() {
            @Override
            public void onConfirmClick() {
                unregisterImPush();
                loadServiceData();
            }
        });
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog.setCustomViewController(withDrawPsdViewController)
                .setCancelableOnTouchOutside(false)
                .show();
        withDrawPsdViewController.setConfirmText(R.string.confirm);
        withDrawPsdViewController.setCloseText(R.string.close_session);
        withDrawPsdViewController.setOnColseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        withDrawPsdViewController.setMsg(R.string.current_service_is_offline_change_service);
        withDrawPsdViewController.setCroseVisibility(View.GONE);
        withDrawPsdViewController.setImageRes(R.drawable.ic_popup_attention);
    }

    private void initAll() {
        loadServiceData();
        registerImPush();
    }

    private void registerImPush() {
        mIMProcessor.registerMsg();
        mIMProcessor.registerOffline();
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

    private void loadServiceData() {
        Apic.chat().tag(TAG).callback(new Callback<Resp<CustomerService>>() {
            @Override
            protected void onRespSuccess(Resp<CustomerService> resp) {
                mConnected = true;
                idleStart = System.currentTimeMillis();
                updateCustomerService(resp.getData());
                registerImPush();
                chatOnline();
            }

            @Override
            protected void onRespFailure(Resp failedResp) {
                super.onRespFailure(failedResp);
                mConnected = false;
                stopScheduleJob();
            }
        }).fireFreely();
    }

    private void updateCustomerService(CustomerService data) {
        if (data != null) {
            mCustomerService = data;
            updateStatus(data);
            mChatAdapter.setCustomerService(data);
            loadHistoryData(data);
        }
    }

    private void updateStatus(CustomerService data) {
        mTitleBar.setTitle(data.getName());
    }

    private void loadHistoryData(final CustomerService data) {
        Apic.requestChatHistory().tag(TAG).callback(new Callback<Resp<List<CustomServiceChat>>>() {
            @Override
            protected void onRespSuccess(Resp<List<CustomServiceChat>> resp) {
                if (resp != null) {
                    loadChatData(resp.getData());
                    loadSystemData(data);
                }
            }
        }).fireFreely();
    }

    private void chatOnline() {
        Apic.chatOnline().tag(TAG).callback(new Callback<Resp<Object>>() {
            @Override
            protected void onRespSuccess(Resp<Object> resp) {
            }
        }).fireFreely();
    }

    private void loadSystemData(CustomerService data) {
        CustomServiceChat customServiceChat = new CustomServiceChat();
        customServiceChat.setChatType(CustomServiceChat.MSG_SYSTEM);
        customServiceChat.setContent(getString(R.string.hello_customer_for_you, data.getName()));
        mCustomServiceChats.add(customServiceChat);
        mChatAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void loadChatData(List<CustomServiceChat> data) {
        mCustomServiceChats.clear();
        Collections.reverse(data);
        mCustomServiceChats.addAll(data);
        mChatAdapter.notifyDataSetChanged();
        updateRecyclerViewPosition(false);
    }

    private void loadChatData(CustomServiceChat data) {
        mCustomServiceChats.add(data);
        mChatAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            initAll();
        }
    }

    @OnClick({R.id.send, R.id.addPic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.send:
                sendMsg();
                idleStart = System.currentTimeMillis();
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
        } else if (mEditText.getText().length() > 500) {
            ToastUtil.show(R.string.over_500);
        } else if (!TextUtils.isEmpty(mEditText.getText().toString().trim())) {
            requestSendTxtMsg(mEditText.getText().toString());
            mEditText.setText("");
        }
    }

    private void requestSendTxtMsg(final String text) {
        Apic.sendTextChat(text).tag(TAG).callback(new Callback<Resp>() {
            @Override
            protected void onRespSuccess(Resp resp) {
                updateSendMsgUI(text, CustomServiceChat.SEND_SUCCESS);
            }

            @Override
            protected void onRespFailure(Resp failedResp) {
                if (failedResp.getCode() == 6003) {
                    showReconnectDialog();
                } else {
                    super.onRespFailure(failedResp);
                }
            }
        }).fireFreely();
    }

    private void requestSendPhotoMsg(final String photoAddress) {
        Apic.sendPhotoChat(photoAddress).tag(TAG).callback(new Callback<Resp>() {
            @Override
            protected void onRespSuccess(Resp resp) {
                updateSendPhotoUI(photoAddress, CustomServiceChat.SEND_SUCCESS);
            }

            @Override
            protected void onRespFailure(Resp failedResp) {
                if (failedResp.getCode() == 6003) {
                    showReconnectDialog();
                } else {
                    super.onRespFailure(failedResp);
                }
            }
        }).fireFreely();
    }

    private void updateSendMsgUI(String text, int status) {
        CustomServiceChat customServiceChat = new CustomServiceChat(text, status);
        customServiceChat.setDirection(1);
        if (LocalUser.getUser().isLogin()) {
            customServiceChat.setUsernick(LocalUser.getUser().getUserInfo().getUserName());
        } else {
            customServiceChat.setUsernick("临时用户");
        }
        loadChatData(customServiceChat);
        updateRecyclerViewPosition(true);
    }

    private void updateSendPhotoUI(String photoAddress, int status) {
        CustomServiceChat customServiceChat = new CustomServiceChat(photoAddress, status, true);
        customServiceChat.setDirection(1);
        if (LocalUser.getUser().isLogin()) {
            customServiceChat.setUsernick(LocalUser.getUser().getUserInfo().getUserName());
        } else {
            customServiceChat.setUsernick("临时用户");
        }
        loadChatData(customServiceChat);
        updateRecyclerViewPosition(true);
    }

    private void sendPicToCustomer() {
        UploadUserImageDialogFragment.newInstance(UploadUserImageDialogFragment.REQ_CODE_TAKE_PICTURE_FROM_GALLERY,
                "", -1, getString(R.string.please_select_image))
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
                        idleStart = System.currentTimeMillis();
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
            public void onRetry(CustomServiceChat customServiceChat) {
                mCustomServiceChats.remove(customServiceChat);
                mChatAdapter.notifyDataSetChanged();
                updateRecyclerViewPosition(true);
                resendMsg(customServiceChat);
            }

            @Override
            public void onRightReScroll() {
                updateRecyclerViewPosition(false);
            }

            @Override
            public void onLeftReScroll() {
                updateRecyclerViewPosition(false);
            }
        };
    }

    private void resendMsg(CustomServiceChat customServiceChat) {
        if (customServiceChat.isPhoto()) {
            requestSendPhotoMsg(customServiceChat.getContent());
        } else {
            requestSendTxtMsg(customServiceChat.getContent());
        }
    }

    private void updateRecyclerViewPosition(boolean isSend) {
        if (isSend) {
            mRecycleView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
        } else if (((LinearLayoutManager) mRecycleView.getLayoutManager()).findLastCompletelyVisibleItemPosition() == mChatAdapter.getItemCount() - 2) {
            mRecycleView.smoothScrollToPosition(mChatAdapter.getItemCount());
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
        static final int TYPE_SYSTEM = 5;

        private List<CustomServiceChat> mCustomServiceChats;
        private Context mContext;
        private CustomerService mCustomerService;
        private OnRetryClickListener mOnRetryClickListener;

        interface OnRetryClickListener {
            void onRetry(CustomServiceChat customServiceChat);

            void onRightReScroll();

            void onLeftReScroll();
        }

        public ChatAdapter(List<CustomServiceChat> customServiceChats, Context context, OnRetryClickListener onRetryClickListener) {
            mCustomServiceChats = customServiceChats;
            mContext = context;
            mOnRetryClickListener = onRetryClickListener;
        }

        void setCustomerService(CustomerService customerService) {
            mCustomerService = customerService;
        }

        @Override
        public int getItemViewType(int position) {
            if (mCustomServiceChats == null || mCustomServiceChats.size() == 0) {
                return TYPE_LEFT;
            }
            if (mCustomServiceChats.get(position).isSystemMsg()) {
                return TYPE_SYSTEM;
            } else if (mCustomServiceChats.get(position).isCustomerService() && !mCustomServiceChats.get(position).isPhoto()) {
                return TYPE_LEFT;
            } else if (mCustomServiceChats.get(position).isCustomerService() && mCustomServiceChats.get(position).isPhoto()) {
                return TYPE_LEFT_PHOTO;
            } else if (mCustomServiceChats.get(position).isPhoto()) {
                return TYPE_RIGHT_PHOTO;
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
                View view = LayoutInflater.from(mContext).inflate(R.layout.row_otc_chat_right_content, parent, false);
                return new RightTextHolder(view);
            } else if (viewType == TYPE_RIGHT_PHOTO) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right_photo, parent, false);
                return new RightPhotoHolder(view);
            } else if (viewType == TYPE_LEFT_PHOTO) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left_photo, parent, false);
                return new LeftPhotoHolder(view);
            } else if (viewType == TYPE_SYSTEM) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_system, parent, false);
                return new SystemHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CustomServiceChat lastChat = null;
            if (position > 0) {
                lastChat = mCustomServiceChats.get(position - 1);
            }
            if (holder instanceof LeftTextHolder) {
                ((LeftTextHolder) holder).bindingData(mCustomerService, mContext, mCustomServiceChats.get(position), lastChat, position, getItemCount());
            } else if (holder instanceof RightTextHolder) {
                ((RightTextHolder) holder).bindingData(mOnRetryClickListener, mContext, mCustomServiceChats.get(position), lastChat, position, getItemCount());
            } else if (holder instanceof RightPhotoHolder) {
                ((RightPhotoHolder) holder).bindingData(mOnRetryClickListener, mContext, mCustomServiceChats.get(position), lastChat, position, getItemCount());
            } else if (holder instanceof LeftPhotoHolder) {
                ((LeftPhotoHolder) holder).bindingData(mOnRetryClickListener, mCustomerService, mContext, mCustomServiceChats.get(position), lastChat, position, getItemCount());
            } else if (holder instanceof SystemHolder) {
                ((SystemHolder) holder).bindingData(mCustomerService, mContext, mCustomServiceChats.get(position), lastChat, position, getItemCount());
            }
        }

        @Override
        public int getItemCount() {
            return mCustomServiceChats == null ? 0 : mCustomServiceChats.size();
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

            private void bindingData(CustomerService customerService, Context context, CustomServiceChat customServiceChat, CustomServiceChat lastChat, int position, int itemCount) {
                if (customerService != null) {
                    GlideApp.with(context).load(customerService.getUserPortrait())
                            .placeholder(R.drawable.ic_default_head_portrait)
                            .circleCrop()
                            .into(mHead);
                }
                if (customServiceChat.isCustomerService()) {
                    mName.setText(customServiceChat.getCusnick());
                } else {
                    mName.setText(customServiceChat.getUsernick());
                }
                mContent.setText(customServiceChat.getContent());

                mTime.setText(DateUtil.getFormatTime(customServiceChat.getCreateTime()));
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

            public void bindingData(final OnRetryClickListener onRetryClickListener, CustomerService customerService, final Context context, CustomServiceChat customServiceChat, CustomServiceChat lastChat, int position, int itemCount) {
                if (customerService != null) {
                    GlideApp.with(context).load(customerService.getUserPortrait())
                            .placeholder(R.drawable.ic_default_head_portrait)
                            .circleCrop()
                            .into(mHead);
                }
                if (customServiceChat.isCustomerService()) {
                    mName.setText(customServiceChat.getCusnick());
                } else {
                    mName.setText(customServiceChat.getUsernick());
                }
                mTime.setText(DateUtil.getFormatTime(customServiceChat.getCreateTime()));

                if (customServiceChat.isPhoto()) {
                    final String url = customServiceChat.getContent();
                    GlideApp.with(context).load(url)
                            .centerCrop()
                            .transform(new ThumbTransform(context))
                            .into(mPhoto);
                    mPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Launcher.with(context, LookBigPictureActivity.class)
                                    .putExtra(ExtraKeys.IMAGE_PATH, url)
                                    .execute();
                        }
                    });
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

            public void bindingData(final OnRetryClickListener onRetryClickListener, Context context, final CustomServiceChat customServiceChat, CustomServiceChat lastChat, int position, int itemCount) {
                if (LocalUser.getUser().isLogin()) {
                    GlideApp.with(context).load(LocalUser.getUser().getUserInfo().getUserPortrait())
                            .placeholder(R.drawable.ic_default_head_portrait)
                            .circleCrop()
                            .into(mHead);
                } else {
                    GlideApp.with(context).load(customServiceChat.getCusnick())
                            .placeholder(R.drawable.ic_default_head_portrait)
                            .circleCrop()
                            .into(mHead);
                }
                if (customServiceChat.isCustomerService()) {
                    mName.setText(customServiceChat.getCusnick());
                } else {
                    mName.setText(customServiceChat.getUsernick());
                }
                mContent.setText(customServiceChat.getContent());

                mTime.setText(DateUtil.getFormatTime(customServiceChat.getCreateTime()));
                mRetry.setVisibility(customServiceChat.isSuccess() ? View.GONE : View.VISIBLE);
                mRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onRetryClickListener != null) {
                            onRetryClickListener.onRetry(customServiceChat);
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

            public void bindingData(final OnRetryClickListener onRetryClickListener, final Context context, final CustomServiceChat customServiceChat, CustomServiceChat lastChat, int position, int itemCount) {
                if (LocalUser.getUser().getUserInfo() != null) {
                    GlideApp.with(context).load(LocalUser.getUser().getUserInfo().getUserPortrait())
                            .placeholder(R.drawable.ic_default_head_portrait)
                            .circleCrop()
                            .into(mHead);
                }
                if (customServiceChat.isCustomerService()) {
                    mName.setText(customServiceChat.getCusnick());
                } else {
                    mName.setText(customServiceChat.getUsernick());
                }

                mTime.setText(DateUtil.getFormatTime(customServiceChat.getCreateTime()));

                if (customServiceChat.isPhoto()) {
                    final String url = customServiceChat.getContent();
                    GlideApp.with(context).load(url)
                            .centerCrop()
                            .transform(new ThumbTransform(context))
                            .into(mPhoto);
                    mPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Launcher.with(context, LookBigPictureActivity.class)
                                    .putExtra(ExtraKeys.IMAGE_PATH, url)
                                    .execute();
                        }
                    });
                }

                mRetry.setVisibility(customServiceChat.isSuccess() ? View.GONE : View.VISIBLE);
                mRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onRetryClickListener != null) {
                            onRetryClickListener.onRetry(customServiceChat);
                        }
                    }
                });
            }
        }

        static class SystemHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.content)
            TextView mContent;

            SystemHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindingData(CustomerService customerService, Context context, CustomServiceChat customServiceChat, CustomServiceChat lastChat, int position, int itemCount) {
                mContent.setText(customServiceChat.getContent());
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (KeyBoardUtils.isOutside(ev, mInputGroup)) {
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
