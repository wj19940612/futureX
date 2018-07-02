package com.songbai.futurex.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.model.AppVersion;
import com.songbai.futurex.service.DownloadService;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.PermissionUtil;
import com.songbai.futurex.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/7/4.
 */

public class UpdateVersionDialogFragment extends DialogFragment {

    @BindView(R.id.updateVersionMsg)
    TextView mUpdateVersionMsg;
    @BindView(R.id.update)
    TextView mUpdate;
    @BindView(R.id.dialogDelete)
    AppCompatImageView mDialogDelete;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.versionName)
    TextView mVersionName;
    private Unbinder mBind;
    private AppVersion mAppVersion;
    private boolean mIsCanceledOnTouchOutside;
    private OnDismissListener mOnDismissListener;

    public interface OnDismissListener {
        void onDismiss();
    }


    private BroadcastReceiver mDownloadBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ToastUtil.show(R.string.download_complete);
        }
    };

    public static UpdateVersionDialogFragment newInstance(AppVersion appVersion, boolean isCanceledOnTouchOutside) {
        Bundle args = new Bundle();
        UpdateVersionDialogFragment fragment = new UpdateVersionDialogFragment();
        args.putParcelable(ExtraKeys.APP_VERSION, appVersion);
        args.putBoolean(ExtraKeys.IS_CANCELEDONTOUCHOUTSIDE, isCanceledOnTouchOutside);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.BaseDialog);
        if (getArguments() != null) {
            mAppVersion = getArguments().getParcelable(ExtraKeys.APP_VERSION);
            mIsCanceledOnTouchOutside = getArguments().getBoolean(ExtraKeys.IS_CANCELEDONTOUCHOUTSIDE, true);
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDownloadBroadcastReceiver,
                new IntentFilter(DownloadService.ACTION_DOWNLOAD_COMPLETE));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_update_version, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDialog();
        mUpdateVersionMsg.setText(mAppVersion.getUpdateLog());
        mTitle.setText(getString(R.string.check_new_version));
        mVersionName.setText(mAppVersion.getLastVersion());
    }

    private void initDialog() {
        Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(!mIsCanceledOnTouchOutside);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * 1.0), WindowManager.LayoutParams.WRAP_CONTENT);
        }
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return mIsCanceledOnTouchOutside;
                }
                return false;
            }
        });
        if (mOnDismissListener != null) {
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mOnDismissListener.onDismiss();
                }
            });
        }
        mDialogDelete.setVisibility(mIsCanceledOnTouchOutside ? View.GONE : View.VISIBLE);
    }

    public void show(FragmentManager manager) {
//        this.show(manager, this.getClass().getSimpleName());
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, this.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    public UpdateVersionDialogFragment setOnDismissListener(OnDismissListener dismissListener) {
        mOnDismissListener = dismissListener;
        return this;
    }

    private boolean isStoragePermissionGranted() {
        return PermissionUtil.isStoragePermissionGranted(getActivity(), PermissionUtil.REQ_CODE_ASK_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.REQ_CODE_ASK_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mUpdate.performLongClick();
                } else {
                    ToastUtil.show(R.string.download_fail);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBind != null) {
            mBind.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(getActivity())
                    .unregisterReceiver(mDownloadBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.dialogDelete, R.id.update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialogDelete:
                dismissAllowingStateLoss();
                break;
            case R.id.update:
                updateApp();
                break;
        }
    }

    private void updateApp() {
        if (mAppVersion == null || TextUtils.isEmpty(mAppVersion.getDownloadUrl())) {
            return;
        }
        if (isStoragePermissionGranted()) {
            Intent intent = new Intent(getActivity(), DownloadService.class);
            intent.putExtra(DownloadService.KEY_DOWN_LOAD_URL, mAppVersion.getDownloadUrl());
            getActivity().startService(intent);
        }
    }

}
