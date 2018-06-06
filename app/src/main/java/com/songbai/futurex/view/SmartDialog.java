package com.songbai.futurex.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 统一普通弹框
 */
public class SmartDialog {

    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;
    private Activity mActivity;

    private String mTitleText;
    private String mMessageText;
    private CustomViewController mCustomViewController;
    private View mCustomView;

    private float mWidthScale;
    private float mHeightScale;
    private int mWindowGravity;
    private int mWindowAnim;

    private OnCancelListener mOnCancelListener;
    private OnDismissListener mDismissListener;

    private int mPositiveId;
    private int mNegativeId;
    private OnClickListener mPositiveListener;
    private OnClickListener mNegativeListener;

    private boolean mCancelableOnTouchOutside;

    public interface OnClickListener {
        void onClick(DialogInterface dialog);
    }

    public interface OnCancelListener {
        void onCancel(DialogInterface dialog);
    }

    public interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }

    private static Map<String, List<SmartDialog>> mListMap = new HashMap<>();

    public static SmartDialog solo(Activity activity, String msg) {
        String key = String.valueOf(activity.hashCode());
        List<SmartDialog> dialogList = mListMap.get(key);
        SmartDialog dialog;
        if (dialogList != null && dialogList.size() > 0) {
            dialog = dialogList.get(0);
        } else {
            dialog = with(activity);
        }
        dialog.init();
        dialog.setMessage(msg);
        return dialog;
    }

    public static SmartDialog solo(Activity activity, int msgRes) {
        return solo(activity, activity.getText(msgRes).toString());
    }

    public static SmartDialog solo(Activity activity) {
        return solo(activity, null);
    }

    public static SmartDialog with(Activity activity, String msg) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        dialog.setMessage(msg);
        return dialog;
    }

    public static SmartDialog with(Activity activity, int msgRes) {
        return with(activity, activity.getText(msgRes).toString());
    }

    public static SmartDialog with(Activity activity) {
        return with(activity, null);
    }

    private static void addMap(Activity activity, SmartDialog dialog) {
        String key = activity.getClass().getSimpleName();
        List<SmartDialog> dialogList = mListMap.get(key);
        if (dialogList == null) {
            dialogList = new LinkedList<>();
        }
        dialogList.add(dialog);
        mListMap.put(key, dialogList);
    }

    public static void dismiss(Activity activity) {
        String key = activity.getClass().getSimpleName();
        List<SmartDialog> dialogList = mListMap.get(key);
        if (dialogList != null) {
            for (SmartDialog dialog : dialogList) {
                dialog.dismiss();
            }
            mListMap.remove(key);
        }
    }

    private SmartDialog(Activity activity) {
        mActivity = activity;
        init();
    }

    private void init() {
        mTitleText = null;

        mMessageText = null;

        mWidthScale = 0;
        mHeightScale = 0;

        mPositiveId = -1;
        mNegativeId = -1;
        mPositiveListener = null;
        mNegativeListener = null;
        mOnCancelListener = null;
        mDismissListener = null;

        mCancelableOnTouchOutside = true;
        mCustomViewController = null;

        mWindowGravity = -1;
        mWindowAnim = -1;
    }

    private void scaleDialog() {
        if (mCustomViewController == null) return;

        if (mWidthScale == 0 && mHeightScale == 0) return;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = mWidthScale == 0 ? ViewGroup.LayoutParams.MATCH_PARENT :
                (int) (displayMetrics.widthPixels * mWidthScale);
        int height = mHeightScale == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT :
                (int) (displayMetrics.heightPixels * mHeightScale);

        mAlertDialog.getWindow().setLayout(width, height);
    }

    public SmartDialog setOnDismissListener(OnDismissListener onDismissListener) {
        mDismissListener = onDismissListener;
        return this;
    }

    public SmartDialog setCustomViewController(CustomViewController customViewController) {
        mCustomViewController = customViewController;
        return this;
    }

    public SmartDialog setPositive(int textId, OnClickListener listener) {
        mPositiveId = textId;
        mPositiveListener = listener;
        return this;
    }

    public SmartDialog setPositive(int textId) {
        mPositiveId = textId;
        return this;
    }

    public SmartDialog setWindowAnim(int windowAnim) {
        mWindowAnim = windowAnim;
        return this;
    }

    public SmartDialog setWindowGravity(int windowGravity) {
        mWindowGravity = windowGravity;
        return this;
    }

    public SmartDialog setNegative(int textId, OnClickListener listener) {
        mNegativeId = textId;
        mNegativeListener = listener;
        return this;
    }

    public SmartDialog setCancelableOnTouchOutside(boolean cancelable) {
        mCancelableOnTouchOutside = cancelable;
        return this;
    }

    public SmartDialog setCancelListener(OnCancelListener cancelListener) {
        mOnCancelListener = cancelListener;
        return this;
    }

    public SmartDialog setMessage(int messageRes) {
        mMessageText = mActivity.getText(messageRes).toString();
        return this;
    }

    public SmartDialog setMessage(String message) {
        mMessageText = message;
        return this;
    }

    public SmartDialog setTitle(int titleId) {
        mTitleText = mActivity.getText(titleId).toString();
        return this;
    }

    public SmartDialog setTitle(String title) {
        mTitleText = title;
        return this;
    }

    public SmartDialog setWidthScale(float widthScale) {
        mWidthScale = widthScale;
        return this;
    }

    public SmartDialog setHeightScale(float heightScale) {
        mHeightScale = heightScale;
        return this;
    }

    public void show() {
        if (mBuilder == null) { // solo dialog, the dialog of this is already existed.
            mBuilder = new AlertDialog.Builder(mActivity);
        }

        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }

        setup();

        if (mAlertDialog != null && !mActivity.isFinishing()) {
            mAlertDialog.show();
            scaleDialog();
        }
    }

    public void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    private void setup() {
        if (mCustomViewController != null) {
            View view = mCustomViewController.onCreateView();
            mBuilder.setView(view);
            mCustomViewController.onInitView(view, this);
            mCustomViewController.finishInitialize();
        } else {
            mBuilder.setMessage(mMessageText);
            mBuilder.setTitle(mTitleText);

            if (mPositiveId != -1) {
                mBuilder.setPositiveButton(mPositiveId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mPositiveListener != null) {
                            mPositiveListener.onClick(dialog);
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
            }
            if (mNegativeId != -1) {
                mBuilder.setNegativeButton(mNegativeId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mNegativeListener != null) {
                            mNegativeListener.onClick(dialog);
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
            }
        } // else

        mBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mOnCancelListener != null) {
                    mOnCancelListener.onCancel(dialog);
                } else if (!mCancelableOnTouchOutside) {
                    // finish current page when not allow user to cancel on touch outside
                    if (mActivity != null) {
                        mActivity.finish();
                    }
                }
            }
        });
        mBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mDismissListener != null) {
                    mDismissListener.onDismiss(dialog);
                }
            }
        });

        mAlertDialog = mBuilder.create();
        mAlertDialog.setCanceledOnTouchOutside(mCancelableOnTouchOutside);
        mAlertDialog.setCancelable(mCancelableOnTouchOutside);

        if (mWindowGravity != -1) {
            WindowManager.LayoutParams params = mAlertDialog.getWindow().getAttributes();
            params.gravity = mWindowGravity;
            mAlertDialog.getWindow().setAttributes(params);
        }

        if (mWindowAnim != -1) {
            WindowManager.LayoutParams params = mAlertDialog.getWindow().getAttributes();
            params.windowAnimations = mWindowAnim;
            mAlertDialog.getWindow().setAttributes(params);
        }

        if (mCustomViewController != null) {
            mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (mWidthScale == 0) {
                mWidthScale = 0.85f; // default width scale for custom view
            }
        }
    }

    public static abstract class CustomViewController {
        private boolean mIsInitialized;

        protected abstract View onCreateView();

        protected abstract void onInitView(View view, SmartDialog dialog);

        public boolean isInitialized() {
            return mIsInitialized;
        }

        private void finishInitialize() {
            mIsInitialized = true;
        }
    }
}
