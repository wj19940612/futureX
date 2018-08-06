package com.songbai.futurex.view.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.fragment.dialog.BottomDialogFragment;
import com.songbai.futurex.fragment.mine.SharePosterFragment;
import com.songbai.futurex.utils.Display;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/8/1
 */
public class ShareFriendsDialogFragment extends BottomDialogFragment implements SharePosterFragment.OnSelectListener {
    private static final String HAS_POSTER = "has_poster";
    private static final String QC_CODE = "qc_code";
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.viewPagerContainer)
    RelativeLayout mViewPagerContainer;
    @BindView(R.id.selectHint)
    TextView mSelectHint;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.cancel)
    TextView mCancel;
    private int[] shareIcons = new int[]{
            R.drawable.ic_share_wechat, R.drawable.ic_share_moment,
            R.drawable.ic_share_twitter, R.drawable.ic_share_facebook,
            R.drawable.ic_share_telegram};

    private int[] shareTexts = new int[]{
            R.string.weichat, R.string.weichat,
            R.string.weichat, R.string.weichat,
            R.string.weichat};
    private boolean mHasPoster;
    private Unbinder mBind;
    private String mQcCode;
    private ArrayList<SharePosterFragment> list = new ArrayList<>();

    public static ShareFriendsDialogFragment newInstance(boolean hasPoster, String code) {
        Bundle args = new Bundle();
        args.putBoolean(HAS_POSTER, hasPoster);
        args.putString(QC_CODE, code);
        ShareFriendsDialogFragment fragment = new ShareFriendsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.view_share_friends, null);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mHasPoster = arguments.getBoolean(HAS_POSTER);
            mQcCode = arguments.getString(QC_CODE);
        }
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        list.add(SharePosterFragment.newInstance(mQcCode, 0,this));
        list.add(SharePosterFragment.newInstance(mQcCode, 1,this));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mRecyclerView.setAdapter(new ShareItemAdapter());
        mViewPagerContainer.setVisibility(mHasPoster ? View.VISIBLE : View.INVISIBLE);
        mViewPagerContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.onTouchEvent(event);
            }
        });
        mViewPagerContainer.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = mViewPager.getLayoutParams();
                layoutParams.width = (int) (mViewPagerContainer.getMeasuredHeight() * 0.53);
                mViewPager.setLayoutParams(layoutParams);
                mViewPager.setAdapter(new ImageAdapter(getChildFragmentManager(), list));
                mViewPager.setOffscreenPageLimit(3);
                mViewPager.setPageMargin((int) Display.dp2Px(10, getResources()));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onSelect(int postion) {
        for (SharePosterFragment sharePosterFragment : list) {
            sharePosterFragment.setSelectedPosition(postion);
        }
    }

    class ShareItemAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_share, parent, false);
            return new ShareItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ShareItemHolder) {
                ((ShareItemHolder) holder).bindData(position);
            }
        }

        @Override
        public int getItemCount() {
            return shareIcons.length;
        }

        class ShareItemHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.platformIcon)
            ImageView mPlatformIcon;
            @BindView(R.id.platformName)
            TextView mPlatformName;

            public ShareItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindData(int position) {
                mPlatformIcon.setImageResource(shareIcons[position]);
                mPlatformName.setText(shareTexts[position]);
            }
        }
    }

    private class ImageAdapter extends FragmentPagerAdapter {

        private final ArrayList<SharePosterFragment> mList;

        public ImageAdapter(FragmentManager fm, ArrayList<SharePosterFragment> list) {
            super(fm);
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }
    }
}
