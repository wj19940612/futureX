package com.songbai.futurex.fragment.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.dialog.UploadUserImageDialogFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.utils.image.ImageUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class FeedbackFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.editText)
    EditText mEditText;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.textSize)
    TextView mTextSize;
    @BindView(R.id.imageNum)
    TextView mImageNum;
    @BindView(R.id.submit)
    TextView mSubmit;
    private Unbinder mBind;
    private static final int MAX_TEXT_SIZE = 200;
    private static final int MAX_IMAGE_SIZE = 4;
    private ArrayList<String> mImages = new ArrayList<>();
    private FeedbackPicAdapter mPicAdapter;
    private String mContent;
    private int mPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mPicAdapter = new FeedbackPicAdapter(getContext());
        mPicAdapter.setList(mImages);
        mPicAdapter.setOnImageClickListener(new FeedbackPicAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int position) {
                selectImage(position);
            }
        });
        mRecyclerView.setAdapter(mPicAdapter);
        mEditText.addTextChangedListener(mValidationWatcher);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TEXT_SIZE)});
        mTextSize.setText(getString(R.string.x_faction_x, 0, MAX_TEXT_SIZE));
        mImageNum.setText(getString(R.string.x_faction_x, mImages.size(), MAX_IMAGE_SIZE));

    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mTextSize.setText(getString(R.string.x_faction_x, s.length(), MAX_TEXT_SIZE));
            boolean enable = checkSubmitButtonEnable();
            if (enable != mSubmit.isEnabled()) {
                mSubmit.setEnabled(enable);
            }
        }
    };

    private boolean checkSubmitButtonEnable() {
        return !TextUtils.isEmpty(mEditText.getText().toString());
    }

    private void selectImage(final int position) {
        int maxImageAmount = MAX_IMAGE_SIZE - mImages.size();
        if (mImages.size() > position) {
            maxImageAmount = MAX_IMAGE_SIZE - mImages.size() + 1;
        }
        mPosition = position;
        UploadUserImageDialogFragment.newInstance(UploadUserImageDialogFragment.IMAGE_TYPE_OPEN_CUSTOM_GALLERY,
                "", -1, getString(R.string.please_select_image), maxImageAmount)
                .setOnImagePathListener(new UploadUserImageDialogFragment.OnImagePathListener() {
                    @Override
                    public void onImagePath(int index, String imagePath) {
                        String[] split = imagePath.split(",");
                        for (String aSplit : split) {
                            if (!mImages.contains(aSplit)) {
                                if (mPosition != -1) {
                                    if (mImages.size() > mPosition) {
                                        mImages.remove(position);
                                    }
                                    mImages.add(position, aSplit);
                                    mPosition = -1;
                                } else {
                                    mImages.add(aSplit);
                                }
                            }
                        }
                        mPicAdapter.notifyDataSetChanged();
                        mImageNum.setText(getString(R.string.x_faction_x, mImages.size(), MAX_IMAGE_SIZE));
                    }
                }).show(getChildFragmentManager());
    }

    public void uploadImages(String data) {
        Apic.uploadImages(data).indeterminate(this).tag(TAG)
                .callback(new Callback<Resp<ArrayList<String>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<String>> resp) {
                        StringBuilder builder = new StringBuilder();
                        for (String url : resp.getData()) {
                            builder.append(url);
                            builder.append(",");
                        }
                        builder.substring(0, builder.length() - 1);
                        addFeedback(mContent, builder.toString());
                    }
                })
                .fire();
    }

    private void restView() {
        mEditText.setText(null);
        mImages.clear();
        mPicAdapter.notifyDataSetChanged();
        mImageNum.setText(getString(R.string.x_faction_x, mImages.size(), MAX_IMAGE_SIZE));
    }

    public void addFeedback(String content, String feedbackPic) {
        LocalUser user = LocalUser.getUser();
        String contactInfo = "";
        if (user.isLogin()) {
            contactInfo = user.getLastAct();
        }
        Apic.addFeedback(content, contactInfo, feedbackPic).tag(TAG)
                .callback(new Callback<Object>() {
                    @Override
                    protected void onRespSuccess(Object resp) {
                        restView();
                        ToastUtil.show(R.string.submit_success);
                    }
                }).fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {
        mContent = mEditText.getText().toString();
        if (!TextUtils.isEmpty(mContent)) {
            if (mImages.size() > 0) {
                StringBuilder data = new StringBuilder();
                for (String image : mImages) {
                    data.append(ImageUtils.compressImageToBase64(image));
                    data.append(";");
                }
                data.substring(0, data.length() - 1);
                uploadImages(data.toString());
                return;
            }
            addFeedback(mContent, null);
        }
    }

    static class FeedbackPicAdapter extends RecyclerView.Adapter {
        private final Context mContext;
        private ArrayList<String> mList;
        private static OnImageClickListener mOnImageClickListener;

        FeedbackPicAdapter(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_feedback_image, parent, false);
            return new ImageHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ImageHolder) {
                ((ImageHolder) holder).bindData(mContext, mList, position);
            }
        }

        @Override
        public int getItemCount() {
            if (mList.size() == 0) {
                return 1;
            }
            if (mList.size() >= MAX_IMAGE_SIZE) {
                return MAX_IMAGE_SIZE;
            }
            return mList.size() + 1;
        }

        public void setList(ArrayList<String> list) {
            mList = list;
        }

        void setOnImageClickListener(OnImageClickListener onImageClickListener) {
            mOnImageClickListener = onImageClickListener;
        }

        static class ImageHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.image)
            ImageView mImage;

            ImageHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            void bindData(Context context, final ArrayList<String> list, final int position) {
                if (list.size() > position) {
                    GlideApp
                            .with(context)
                            .load(list.get(position))
                            .into(mImage);
                } else {
                    GlideApp
                            .with(context)
                            .load(R.drawable.ic_authentication_add)
                            .into(mImage);
                }
                mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnImageClickListener != null) {
                            mOnImageClickListener.onImageClick(position);
                        }
                    }
                });
            }
        }

        interface OnImageClickListener {
            void onImageClick(int position);
        }
    }
}
