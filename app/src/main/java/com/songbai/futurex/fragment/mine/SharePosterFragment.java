package com.songbai.futurex.fragment.mine;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.songbai.futurex.R;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.utils.ZXingUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/8/6
 */
public class SharePosterFragment extends BaseFragment {
    private static final String QC_CODE = "qc_code";
    private static final String POSITION = "position";
    private OnSelectListener mOnSelectListener;

    @BindView(R.id.poster)
    ImageView mPoster;
    @BindView(R.id.qcCode)
    ImageView mQcCode;
    @BindView(R.id.shareMsg)
    FrameLayout mShareMsg;
    Unbinder unbinder;
    @BindView(R.id.rootView)
    FrameLayout mRootView;
    private String mCode;
    private int mPosition;
    private int mSelectedPosition = -1;
    private boolean mPrepared;

    public static SharePosterFragment newInstance(String code, int position, OnSelectListener onSelectListener) {
        Bundle args = new Bundle();
        args.putString(QC_CODE, code);
        args.putInt(POSITION, position);
        SharePosterFragment fragment = new SharePosterFragment();
        fragment.setOnSelectListener(onSelectListener);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_poster, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPrepared = true;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCode = arguments.getString(QC_CODE);
            mPosition = arguments.getInt(POSITION);
        }
        mShareMsg.setVisibility(View.GONE);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShareMsg.getVisibility() == View.GONE) {
                    mShareMsg.setVisibility(View.VISIBLE);
                    if (mOnSelectListener != null) {
                        mOnSelectListener.onSelect(mPosition);
                    }
                } else {
                    mShareMsg.setVisibility(View.GONE);
                    if (mOnSelectListener != null) {
                        mOnSelectListener.onSelect(-1);
                    }
                }
            }
        });
        GlideApp.with(getContext())
                .load("http://img.zcool.cn/community/0117e2571b8b246ac72538120dd8a4.jpg@1280w_1l_2o_100sh.jpg")
                .into(mPoster);
        mQcCode.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ZXingUtils.createQRImage(mCode, 100, 100);
                mQcCode.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        setSelection();
    }

    private void setSelection() {
        if (mPrepared) {
            if (mShareMsg != null) {
                mShareMsg.setVisibility(mSelectedPosition == mPosition ? View.VISIBLE : View.GONE);
            }
        }
    }

    public void setSelectedPosition(int selectedPosition) {
        mSelectedPosition = selectedPosition;
        setSelection();
    }

    public interface OnSelectListener {
        void onSelect(int postion);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
