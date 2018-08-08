package com.songbai.futurex.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.image.ImageUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private static final int SHARE_TELEGRAM = 12312;
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
            R.string.wechat_friends, R.string.wechat_moments,
            R.string.twitter, R.string.facebook,
            R.string.telegram};
    private boolean mHasPoster;
    private Unbinder mBind;
    private String mQcCode;
    private ArrayList<SharePosterFragment> list = new ArrayList<>();
    private int mSelectedPosition = -1;
    private UMShareListener mUmShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            Log.e("wtf", "onStart");
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            dismiss();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Log.e("wtf", throwable.toString());
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Log.e("wtf", "onCancel");
        }
    };

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

    @SuppressLint("ClickableViewAccessibility")
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
        mSelectHint.setText(mHasPoster ? R.string.share_exclusive_poster : R.string.share_link);
        list.add(SharePosterFragment.newInstance(mQcCode, 0, this));
        list.add(SharePosterFragment.newInstance(mQcCode, 1, this));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        ShareItemAdapter adapter = new ShareItemAdapter();
        adapter.setOnRVItemClickListener(new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                switch (((int) obj)) {
                    case R.string.wechat_friends:
                        share(SHARE_MEDIA.WEIXIN);
                        break;
                    case R.string.wechat_moments:
                        share(SHARE_MEDIA.WEIXIN_CIRCLE);
                        break;
                    case R.string.twitter:
                        share(SHARE_MEDIA.TWITTER);
                        break;
                    case R.string.facebook:
                        share(SHARE_MEDIA.FACEBOOK);
                        break;
                    case R.string.telegram:
                        shareTelegram();
                        break;
                    default:
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
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

    private void shareTelegram() {
        if (isAPPInstalled(getContext(), "org.telegram.messenger")) {
            if (mHasPoster) {
                if (mSelectedPosition < 0) {
                    ToastUtil.show(R.string.select_a_poster);
                    return;
                }
                Bitmap shareBitmap = list.get(mSelectedPosition).getShareBitmap();
                File temp = ImageUtils.getUtil().saveBitmap(shareBitmap, "temp");
                telegramShareImage(temp);
            } else {
                telegramShare("", mQcCode);
            }
        } else {
            ToastUtil.show(R.string.app_not_installed);
        }
    }

    private void share(SHARE_MEDIA platform) {
        if (UMShareAPI.get(getContext()).isInstall(getActivity(), platform)) {
            if (mHasPoster) {
                shareWithImage(platform);
            } else {
                shareWithText(platform);
            }
        } else {
            ToastUtil.show(R.string.app_not_installed);
        }
    }

    private void shareWithText(SHARE_MEDIA platform) {
        new ShareAction(getActivity())
                .setPlatform(platform)
                .withText(mQcCode)//分享内容
                .setCallback(mUmShareListener)
                .share();
    }

    private void shareWithImage(SHARE_MEDIA platform) {
        if (mSelectedPosition < 0) {
            ToastUtil.show(R.string.select_a_poster);
            return;
        }
        Bitmap shareBitmap = list.get(mSelectedPosition).getShareBitmap();
        new ShareAction(getActivity())
                .setPlatform(platform)
                .withText("hello")//分享内容
                .withMedia(new UMImage(getContext(), shareBitmap))
                .setCallback(mUmShareListener)
                .share();
    }

    public void telegramShare(String title, String content) {
        try {
            Intent vIt = new Intent("android.intent.action.SEND");
            vIt.setPackage("org.telegram.messenger");
            vIt.setType("text/plain");
            vIt.putExtra(Intent.EXTRA_TITLE, title);
            vIt.putExtra(Intent.EXTRA_TEXT, content);
            startActivityForResult(vIt, SHARE_TELEGRAM);
        } catch (Exception ex) {
            Log.e(TAG, "telegramShare:" + ex);
        }
    }

    public void telegramShareImage(File file) {
        try {
            Intent vIt = new Intent("android.intent.action.SEND");
            vIt.setPackage("org.telegram.messenger");
            vIt.setType("image/*");
            vIt.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            startActivityForResult(vIt, SHARE_TELEGRAM);
        } catch (Exception ex) {
            Log.e(TAG, "telegramShare:" + ex);
        }
    }

    public static boolean isAPPInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> pinfo = pm.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHARE_TELEGRAM) {
            dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onSelect(int position) {
        for (SharePosterFragment sharePosterFragment : list) {
            sharePosterFragment.setSelectedPosition(position);
        }
        mSelectedPosition = position;
    }

    class ShareItemAdapter extends RecyclerView.Adapter {
        private OnRVItemClickListener mOnRVItemClickListener;

        public void setOnRVItemClickListener(OnRVItemClickListener onRVItemClickListener) {
            mOnRVItemClickListener = onRVItemClickListener;
        }

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
            private final View mRootView;
            @BindView(R.id.platformIcon)
            ImageView mPlatformIcon;
            @BindView(R.id.platformName)
            TextView mPlatformName;

            public ShareItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mRootView = itemView;
            }

            public void bindData(final int position) {
                mPlatformIcon.setImageResource(shareIcons[position]);
                mPlatformName.setText(shareTexts[position]);
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnRVItemClickListener != null) {
                            mOnRVItemClickListener.onItemClick(mRootView, position, shareTexts[position]);
                        }
                    }
                });
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
