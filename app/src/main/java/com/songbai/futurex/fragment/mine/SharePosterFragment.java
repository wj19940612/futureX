package com.songbai.futurex.fragment.mine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.utils.Display;
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
    private static final String POSTER = "poster";
    @BindView(R.id.shareTitle)
    TextView mShareTitle;
    @BindView(R.id.shareDesc)
    TextView mShareDesc;
    @BindView(R.id.cover)
    FrameLayout mCover;
    private OnSelectListener mOnSelectListener;

    @BindView(R.id.poster)
    ImageView mPoster;
    @BindView(R.id.qcCode)
    ImageView mQcCode;
    @BindView(R.id.shareMsg)
    ConstraintLayout mShareMsg;
    Unbinder unbinder;
    @BindView(R.id.rootView)
    FrameLayout mRootView;
    private String mCode;
    private int mPosition;
    private int mSelectedPosition = -1;
    private boolean mPrepared;
    private int mPosterRes;

    public static SharePosterFragment newInstance(String code, int position, int poster, OnSelectListener onSelectListener) {
        Bundle args = new Bundle();
        args.putString(QC_CODE, code);
        args.putInt(POSITION, position);
        args.putInt(POSTER, poster);
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
            mPosterRes = arguments.getInt(POSTER);
        }
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCover.getVisibility() == View.GONE) {
                    if (mOnSelectListener != null) {
                        mOnSelectListener.onSelect(mPosition);
                    }
                    mCover.setVisibility(View.VISIBLE);
                } else {
                    if (mOnSelectListener != null) {
                        mOnSelectListener.onSelect(-1);
                    }
                    mCover.setVisibility(View.GONE);
                }
            }
        });
        GlideApp.with(getContext())
                .load(mPosterRes)
                .into(mPoster);
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                int measuredWidth = mRootView.getMeasuredWidth();
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mQcCode.getLayoutParams();
                int mQcCodeWidth = (int) (Display.dp2Px(64, getResources()) / Display.dp2Px(375, getResources()) * measuredWidth);
                layoutParams.width = mQcCodeWidth;
                layoutParams.height = mQcCodeWidth;
                layoutParams.rightMargin = (int) (Display.dp2Px(25, getResources()) * measuredWidth / Display.dp2Px(375, getResources()));
                layoutParams.topMargin = (int) (Display.dp2Px(13, getResources()) * measuredWidth / Display.dp2Px(375, getResources()));
                layoutParams.bottomMargin = (int) (Display.dp2Px(13, getResources()) * measuredWidth / Display.dp2Px(375, getResources()));
                mQcCode.setLayoutParams(layoutParams);
                ConstraintLayout.LayoutParams layoutTextParams = (ConstraintLayout.LayoutParams) mShareTitle.getLayoutParams();
                layoutTextParams.leftMargin = (int) (Display.dp2Px(22, getResources()) * measuredWidth / Display.dp2Px(375, getResources()));
                mShareTitle.setLayoutParams(layoutTextParams);
                mShareTitle.setTextSize(20f * measuredWidth / Display.dp2Px(375, getResources()));
                mShareDesc.setTextSize(12f * measuredWidth / Display.dp2Px(375, getResources()));
                Bitmap bitmap = ZXingUtils.createQRImage("https://bitfutu.re/pro/" + mCode, mQcCodeWidth, mQcCodeWidth);
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
            if (mCover != null) {
                mCover.setVisibility(mSelectedPosition == mPosition ? View.VISIBLE : View.GONE);
            }
        }
    }

    public void setSelectedPosition(int selectedPosition) {
        mSelectedPosition = selectedPosition;
        setSelection();
    }

    public interface OnSelectListener {
        void onSelect(int position);
    }

    public Bitmap getShareBitmap() {
        /**
         * View组件显示的内容可以通过cache机制保存为bitmap
         * 我们要获取它的cache先要通过setDrawingCacheEnable方法把cache开启，
         * 然后再调用getDrawingCache方法就可 以获得view的cache图片了
         * 。buildDrawingCache方法可以不用调用，因为调用getDrawingCache方法时，
         * 若果 cache没有建立，系统会自动调用buildDrawingCache方法生成cache。
         * 若果要更新cache, 必须要调用destoryDrawingCache方法把旧的cache销毁，才能建立新的。
         */
        mPoster.setDrawingCacheEnabled(true);
        mPoster.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //设置绘制缓存背景颜色
        mPoster.setDrawingCacheBackgroundColor(Color.WHITE);

        // 把一个View转换成图片
        Bitmap poster = loadBitmapFromView(mPoster);
        mShareMsg.setDrawingCacheEnabled(true);
        mShareMsg.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //设置绘制缓存背景颜色
        mShareMsg.setDrawingCacheBackgroundColor(Color.WHITE);
        Bitmap shareMsg = loadBitmapFromView(mShareMsg);
        Bitmap newbmp = Bitmap.createBitmap(poster.getWidth(), poster.getHeight() + shareMsg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newbmp);
        canvas.drawBitmap(poster, 0, 0, null);
        canvas.drawBitmap(shareMsg, 0, poster.getHeight(), null);
        canvas.save();
        canvas.restore();
        return newbmp;
    }

    private static Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        /** 如果不设置canvas画布为白色，则生成透明 */
//        c.drawColor(Color.WHITE);

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
