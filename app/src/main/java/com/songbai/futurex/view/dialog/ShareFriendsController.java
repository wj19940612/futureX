package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yangguangda
 * @date 2018/8/1
 */
public class ShareFriendsController extends SmartDialog.CustomViewController {
    @BindView(R.id.selectHint)
    TextView mSelectHint;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.cancel)
    TextView mCancel;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private Context mContext;
    private int[] shareIcons = new int[]{
            R.drawable.ic_share_wechat, R.drawable.ic_share_moment,
            R.drawable.ic_share_twitter, R.drawable.ic_share_facebook,
            R.drawable.ic_share_telegram};

    private int[] shareTexts = new int[]{
            R.string.weichat, R.string.weichat,
            R.string.weichat, R.string.weichat,
            R.string.weichat};

    public ShareFriendsController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        View view = View.inflate(mContext, R.layout.view_share_friends, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onInitView(View view, final SmartDialog dialog) {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        mRecyclerView.setAdapter(new ShareItemAdapter());
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
}
