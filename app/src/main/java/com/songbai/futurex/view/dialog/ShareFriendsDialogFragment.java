package com.songbai.futurex.view.dialog;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.BuildConfig;
import com.songbai.futurex.R;
import com.songbai.futurex.fragment.dialog.BottomDialogFragment;
import com.songbai.futurex.fragment.mine.SharePosterFragment;
import com.songbai.futurex.service.AccessibilityUtils;
import com.songbai.futurex.service.AssistantService;
import com.songbai.futurex.utils.Display;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ZXingUtils;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.SmartDialog;
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
    private static final String SHARE_CONTENT = "share_content";
    private static final int SHARE = 12312;
    private static final int OPEN_ACCESSIBILITY = 12313;
    public static String textString;
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
            R.drawable.ic_share_telegram, R.drawable.ic_invitation_link};

    private int[] shareTexts = new int[]{
            R.string.wechat_friends, R.string.wechat_moments,
            R.string.twitter, R.string.facebook,
            R.string.telegram, R.string.copy_link};
    private boolean mHasPoster;
    private Unbinder mBind;
    private String mQcCode;
    private ArrayList<SharePosterFragment> mList = new ArrayList<>();
    private int mSelectedPosition = -1;
    private UMShareListener mUmShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA shareMedia) {
            Log.e("wtf", "onStart");
        }

        @Override
        public void onResult(SHARE_MEDIA shareMedia) {
            dismiss();
        }

        @Override
        public void onError(SHARE_MEDIA shareMedia, Throwable throwable) {
            Log.e("wtf", throwable.toString());
        }

        @Override
        public void onCancel(SHARE_MEDIA shareMedia) {
            Log.e("wtf", "onCancel");
        }
    };
    private File mTemp;

    public static ShareFriendsDialogFragment newInstance(boolean hasPoster, String code, String promotionShare) {
        Bundle args = new Bundle();
        args.putBoolean(HAS_POSTER, hasPoster);
        args.putString(QC_CODE, code);
        args.putString(SHARE_CONTENT, promotionShare);
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
            textString = arguments.getString(SHARE_CONTENT);
            int[] posters = new int[]{R.drawable.ic_poster1, R.drawable.ic_poster2};
            for (int i = 0; i < posters.length; i++) {
                mList.add(SharePosterFragment.newInstance(mQcCode, i, posters[i], this));
            }
        }
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mSelectHint.setText(mHasPoster ? R.string.share_exclusive_poster : R.string.share_link);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        ShareItemAdapter adapter = new ShareItemAdapter();
        adapter.setOnRVItemClickListener(new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                switch (((int) obj)) {
                    case R.string.wechat_friends:
//                        shareWithPackageName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                        share(SHARE_MEDIA.WEIXIN);
                        break;
                    case R.string.wechat_moments:
//                        shareWithPackageName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");/**/
                        share(SHARE_MEDIA.WEIXIN_CIRCLE);
                        break;
                    case R.string.twitter:
                        shareWithPackageName("com.twitter.android", "");
                        break;
                    case R.string.facebook:
                        shareWithPackageName("com.facebook.katana", "");
                        break;
                    case R.string.telegram:
                        shareWithPackageName("org.telegram.messenger", "");
                        break;
                    case R.string.copy_link:
                        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setPrimaryClip(ClipData.newPlainText(null, "https://bitfutu.re/pro/" + mQcCode));
                        ToastUtil.show(R.string.copy_success);
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
                mViewPager.setAdapter(new ImageAdapter(getChildFragmentManager(), mList));
                mViewPager.setOffscreenPageLimit(3);
                mViewPager.setPageMargin((int) Display.dp2Px(10, getResources()));
            }
        });
    }

    private void shareWithPackageName(final String packageName, final String className) {
        if (isAPPInstalled(getContext(), packageName)) {
            final String title = "";
            if (mHasPoster) {
                if (mSelectedPosition < 0) {
                    ToastUtil.show(R.string.select_a_poster);
                    return;
                }
                Bitmap shareBitmap = mList.get(mSelectedPosition).getShareBitmap();
                mTemp = ImageUtils.getUtil().saveBitmap(shareBitmap, "temp.jpeg");
                shareStream(packageName, className, title, textString, mTemp);
            } else {
                if ("com.tencent.mm.ui.tools.ShareToTimeLineUI".equals(className)) {
                    if (!AccessibilityUtils.isAccessibilitySettingsOn(AssistantService.class.getName(), getContext())) {
                        MsgHintController msgHintController = new MsgHintController(getContext(), new MsgHintController.OnClickListener() {
                            @Override
                            public void onConfirmClick() {
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                startActivityForResult(intent, OPEN_ACCESSIBILITY);
                            }
                        });
                        SmartDialog smartDialog = SmartDialog.solo(getActivity());
                        smartDialog.setCustomViewController(msgHintController)
                                .show();
                        msgHintController.setConfirmText(R.string.go_to_set);
                        msgHintController.setCloseText(R.string.share);
                        msgHintController.setOnColseClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareWechat();
                            }
                        });
                        msgHintController.setMsg(R.string.can_open_accessibility_service);
                        msgHintController.setImageRes(R.drawable.ic_popup_attention);
                    } else {
                        shareWechat();
                    }
                } else {
                    shareText(packageName, className, mQcCode, textString);
                }
            }
        } else {
            ToastUtil.show(R.string.app_not_installed);
        }
    }

    private void shareWechat() {
        Bitmap shareBitmap = ZXingUtils.createQRImage(mQcCode, 100, 100);
        File temp = ImageUtils.getUtil().saveBitmap(shareBitmap, "temp.jpeg");
        shareStream("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI", "", textString.toString(), temp);
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
                .withText(textString)
                .setCallback(mUmShareListener)
                .share();
    }

    private void shareWithImage(SHARE_MEDIA platform) {
        if (mSelectedPosition < 0) {
            ToastUtil.show(R.string.select_a_poster);
            return;
        }
        Bitmap shareBitmap = mList.get(mSelectedPosition).getShareBitmap();
        new ShareAction(getActivity())
                .setPlatform(platform)
                .withText(textString)
                .withMedia(new UMImage(getContext(), shareBitmap))
                .setCallback(mUmShareListener)
                .share();
    }

    public void shareText(String packageName, String className, String title, String content) {
        try {
            Intent vIt = new Intent("android.intent.action.SEND");
            vIt.setType("text/plain");
            vIt.setPackage(packageName);
            if (!TextUtils.isEmpty(className)) {
                vIt.setClassName(packageName, className);
            }
            vIt.putExtra(Intent.EXTRA_SUBJECT, title);
            vIt.putExtra(Intent.EXTRA_TEXT, content);
            vIt.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(vIt, SHARE);
        } catch (Exception ex) {
            Log.e(TAG, "shareText:" + ex);
        }
    }

    public void shareStream(String packageName, String className, String title, String content, File file) {
        try {
            Intent vIt = new Intent("android.intent.action.SEND");
            vIt.setType("image/*");
            vIt.setPackage(packageName);
            if (!TextUtils.isEmpty(className)) {
                vIt.setClassName(packageName, className);
            }
            vIt.putExtra(Intent.EXTRA_SUBJECT, title);
            vIt.putExtra(Intent.EXTRA_TEXT, content);
            // 授予目录临时共享权限
            vIt.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            vIt.putExtra(Intent.EXTRA_STREAM, getImageContentUri(getContext(), file));
            startActivityForResult(vIt, SHARE);
        } catch (Exception ex) {
            Log.e(TAG, "shareStream:" + ex);
        }
    }

    public static Uri getFileUri(Context context, File file) {
        Uri uri;
        // 低版本直接用 Uri.fromFile
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            //  使用 FileProvider 会在某些 app 下不支持（在使用FileProvider 方式情况下QQ不能支持图片、视频分享，微信不支持视频分享）
            // TODO: 2018/8/9 FileProvider 还未完成
            uri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            ContentResolver cR = context.getContentResolver();
            if (uri != null && !TextUtils.isEmpty(uri.toString())) {
                String fileType = cR.getType(uri);
                // 使用 MediaStore 的 content:// 而不是自己 FileProvider 提供的uri，不然有些app无法适配
                if (!TextUtils.isEmpty(fileType)) {
                    if (fileType.contains("video/")) {
                        uri = getVideoContentUri(context, file);
                    } else if (fileType.contains("image/")) {
                        uri = getImageContentUri(context, file);
                    } else if (fileType.contains("audio/")) {
                        uri = getAudioContentUri(context, file);
                    }
                }
            }
        }
        return uri;
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param videoFile
     * @return content Uri
     */
    public static Uri getVideoContentUri(Context context, File videoFile) {
        String filePath = videoFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/video/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (videoFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param audioFile
     * @return content Uri
     */
    public static Uri getAudioContentUri(Context context, File audioFile) {
        String filePath = audioFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID}, MediaStore.Audio.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/audio/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (audioFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
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
        if (requestCode == SHARE) {
            dismiss();
            if (mTemp != null) {
                mTemp.delete();
            }
        } else if (requestCode == OPEN_ACCESSIBILITY) {
            shareWechat();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onSelect(int position) {
        for (SharePosterFragment sharePosterFragment : mList) {
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
