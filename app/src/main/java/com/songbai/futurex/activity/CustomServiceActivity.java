package com.songbai.futurex.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.fragment.dialog.UploadUserImageDialogFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CustomerService;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.Network;
import com.songbai.futurex.utils.ThumbTransform;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.im.IMProcessor;
import com.songbai.futurex.websocket.model.CustomServiceChat;

import java.util.ArrayList;
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

    private boolean mKeyboardInit = false;

    private List<CustomServiceChat> mCustomServiceChats;
    private ChatAdapter mChatAdapter;

    private IMProcessor mIMProcessor;

    private Network.NetworkChangeReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
//                initImPush();
//                loadData();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_service);
        ButterKnife.bind(this);
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
        if (mIMProcessor != null) {
            mIMProcessor.pause();
        }
        Network.unregisterNetworkChangeReceiver(getActivity(), mNetworkChangeReceiver);
    }

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
            Launcher.with(CustomServiceActivity.this, LoginActivity.class).executeForResult(REQ_LOGIN);
        }
    }

    private void initSocketListener() {
        //初始化推送回调
        mIMProcessor = new IMProcessor(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code) {
                new DataParser<Response<CustomServiceChat>>(data) {

                    @Override
                    public void onSuccess(Response<CustomServiceChat> customServiceChatResponse) {
                        mCustomServiceChats.add(customServiceChatResponse.getContent());
                        mChatAdapter.notifyDataSetChanged();
                    }
                }.parse();
            }
        });
        mIMProcessor.resume();
    }

    private void initAll() {
        loadData();
        initImPush();
    }

    private void initImPush() {
        mIMProcessor.registerMsg();
    }

    private void showKeyboard() {
        if (!mKeyboardInit) mKeyboardInit = true;
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(mEditText, 0);
    }

    private void hideKeyboard() {
        if (mKeyboardInit) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(mEditText.getWindowToken(), InputMethodManager
                            .HIDE_NOT_ALWAYS);
        }
    }

    private void loadData() {
        Apic.chat().tag(TAG).callback(new Callback<Resp<CustomerService>>() {
            @Override
            protected void onRespSuccess(Resp<CustomerService> resp) {
                updateCustomerService(resp.getData());
            }
        }).fireFreely();
    }

    private void updateCustomerService(CustomerService data) {
        if (data != null) {
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

    private void loadSystemData(CustomerService data) {
        CustomServiceChat customServiceChat = new CustomServiceChat();
        customServiceChat.setChatType(CustomServiceChat.MSG_SYSTEM);
        customServiceChat.setContent(getString(R.string.hello_customer_for_you, data.getName()));
        mCustomServiceChats.add(customServiceChat);
        mChatAdapter.notifyDataSetChanged();
    }


    private void loadChatData(List<CustomServiceChat> data) {
        mCustomServiceChats.addAll(data);
        mChatAdapter.notifyDataSetChanged();
    }

    private void loadChatData(CustomServiceChat data) {
        mCustomServiceChats.add(data);
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
        Apic.sendTextChat(text).tag(TAG).callback(new Callback<Resp>() {
            @Override
            protected void onRespSuccess(Resp resp) {
//                updateSendMsgUI(text, CustomServiceChat.SEND_SUCCESS);
            }

            @Override
            public void onFailure(ReqError reqError) {
                super.onFailure(reqError);
//                updateSendMsgUI(text, CustomServiceChat.SEND_FAILED);
            }
        }).fireFreely();
    }

    private void requestSendPhotoMsg(final String photoAddress) {
        Apic.sendPhotoChat(photoAddress).tag(TAG).callback(new Callback<Resp>() {
            @Override
            protected void onRespSuccess(Resp resp) {
//                updateSendPhotoUI(photoAddress, CustomServiceChat.SEND_SUCCESS);
            }

            @Override
            public void onFailure(ReqError reqError) {
                super.onFailure(reqError);
//                updateSendPhotoUI(photoAddress, CustomServiceChat.SEND_FAILED);
            }
        }).fireFreely();
    }

    private void updateSendMsgUI(String text, int status) {
        CustomServiceChat customServiceChat = new CustomServiceChat(text, status);
        loadChatData(customServiceChat);
        updateRecyclerViewPosition(true);
    }

    private void updateSendPhotoUI(String photoAddress, int status) {
        CustomServiceChat customServiceChat = new CustomServiceChat(photoAddress, status, true);
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
            mRecycleView.smoothScrollToPosition(mChatAdapter.getItemCount());
        } else if (((LinearLayoutManager) mRecycleView.getLayoutManager()).findLastCompletelyVisibleItemPosition() == mChatAdapter.getItemCount() - 2) {
            mRecycleView.smoothScrollToPosition(mChatAdapter.getItemCount());
        }
    }

    static class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int TYPE_LEFT = 1;
        public static final int TYPE_LEFT_PHOTO = 2;
        public static final int TYPE_RIGHT = 3;
        public static final int TYPE_RIGHT_PHOTO = 4;
        public static final int TYPE_SYSTEM = 5;

        private List<CustomServiceChat> mCustomServiceChats;
        private Context mContext;
        private CustomerService mCustomerService;
        private OnRetryClickListener mOnRetryClickListener;

        interface OnRetryClickListener {
            public void onRetry(CustomServiceChat customServiceChat);

            public void onRightReScroll();

            public void onLeftReScroll();
        }

        public ChatAdapter(List<CustomServiceChat> customServiceChats, Context context, OnRetryClickListener onRetryClickListener) {
            mCustomServiceChats = customServiceChats;
            mContext = context;
            mOnRetryClickListener = onRetryClickListener;
        }

        public void setCustomerService(CustomerService customerService) {
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
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right_content, parent, false);
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
            if (position > 0)
                lastChat = mCustomServiceChats.get(position - 1);
            if (holder instanceof LeftTextHolder) {
                ((LeftTextHolder) holder).bindingData(mCustomerService, mContext, mCustomServiceChats.get(position), lastChat, position, getItemCount());
            } else if (holder instanceof RightTextHolder) {
                ((RightTextHolder) holder).bindingData(mOnRetryClickListener, mContext, mCustomServiceChats.get(position), lastChat, position, getItemCount());
            } else if (holder instanceof RightPhotoHolder) {
                ((RightPhotoHolder) holder).bindingData(mOnRetryClickListener, mContext, mCustomServiceChats.get(position), lastChat, position, getItemCount());
            } else if (holder instanceof LeftPhotoHolder) {
                ((LeftPhotoHolder) holder).bindingData(mOnRetryClickListener,mCustomerService, mContext, mCustomServiceChats.get(position), lastChat, position, getItemCount());
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

            public void bindingData(final OnRetryClickListener onRetryClickListener, CustomerService customerService, Context context, CustomServiceChat customServiceChat, CustomServiceChat lastChat, int position, int itemCount) {
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
                    GlideApp.with(context).load(customServiceChat.getContent())
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
                                        onRetryClickListener.onLeftReScroll();
                                    }
                                    return false;
                                }
                            })
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
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

            public void bindingData(final OnRetryClickListener onRetryClickListener, Context context, final CustomServiceChat customServiceChat, CustomServiceChat lastChat, int position, int itemCount) {
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
                    GlideApp.with(context).load(customServiceChat.getContent())
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
                                        onRetryClickListener.onRightReScroll();
                                    }
                                    return false;
                                }
                            })
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mPhoto);
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
}