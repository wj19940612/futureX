package com.songbai.futurex.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.songbai.futurex.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 统一普通弹框
 */
public class SmartDialog {

    private TextView mTitleView;
    private TextView mMessageView;
    private TextView mNegativeBtn;
    private TextView mPositionBtn;

    private AppCompatDialog mDialog;
    private Activity mActivity;

    private String mTitleText;
    private int mTitleMaxLines;
    private int mTitleTextColor;

    private String mMessageText;
    private int mMessageGravity;
    private int mMessageTextSize;
    private int mMessageTextColor;
    private int mMessageTextMaxLines;

    private View mCustomView;
    private View mDialogView;

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
    private int mPositiveTextColor;
    private int mNegativeVisible;

    private boolean mCancelableOnTouchOutside;

    public interface OnClickListener {
        void onClick(Dialog dialog);
    }

    public interface OnCancelListener {
        void onCancel(Dialog dialog);
    }

    public interface OnDismissListener {
        void onDismiss(Dialog dialog);
    }

    private static Map<String, List<SmartDialog>> mListMap = new HashMap<>();

    public static SmartDialog solo(Activity activity, String msg) {
        String key = activity.getClass().getSimpleName();
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
        mTitleTextColor = ContextCompat.getColor(mActivity, R.color.text22);
        mTitleMaxLines = 2;

        mMessageText = null;
        mMessageGravity = Gravity.CENTER_VERTICAL;
        mMessageTextSize = 14;
        mMessageTextColor = ContextCompat.getColor(mActivity, R.color.text66);
        mMessageTextMaxLines = 3;
        mWidthScale = 0;
        mHeightScale = 0;

        mPositiveId = R.string.ok;
        mNegativeId = R.string.cancel;
        mPositiveListener = null;
        mNegativeListener = null;
        mOnCancelListener = null;
        mDismissListener = null;
        mPositiveTextColor = ContextCompat.getColor(mActivity, R.color.text22);
        mNegativeVisible = View.VISIBLE;

        mCancelableOnTouchOutside = true;
        mCustomView = null;
        mWindowGravity = -1;
        mWindowAnim = -1;
    }

    private void scaleDialog() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = mWidthScale == 0 ? ViewGroup.LayoutParams.MATCH_PARENT :
                (int) (displayMetrics.widthPixels * mWidthScale);
        int height = mHeightScale == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT :
                (int) (displayMetrics.heightPixels * mHeightScale);

        mDialog.getWindow().setLayout(width, height);
    }

    public SmartDialog setOnDismissListener(OnDismissListener onDismissListener) {
        mDismissListener = onDismissListener;
        return this;
    }

    public SmartDialog setCustomView(View customView) {
        mCustomView = customView;
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

    public SmartDialog setPositiveTextColor(int resColorId) {
        mPositiveTextColor = resColorId;
        return this;
    }

    public SmartDialog setMessageGravity(int gravity) {
        mMessageGravity = gravity;
        return this;
    }

    public SmartDialog setWindowGravity(int windowGravity) {
        mWindowGravity = windowGravity;
        return this;
    }

    public SmartDialog setTitleMaxLines(int titleMaxLines) {
        mTitleMaxLines = titleMaxLines;
        return this;
    }

    public SmartDialog setNegative(int textId, OnClickListener listener) {
        mNegativeId = textId;
        mNegativeListener = listener;
        return this;
    }

    public SmartDialog setNegativeVisible() {
        mNegativeVisible = View.VISIBLE;
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

    private SmartDialog setMessageView(int messageId) {
        mMessageText = mActivity.getText(messageId).toString();
        return this;
    }

    public SmartDialog setMessage(String message) {
        mMessageText = message;
        return this;
    }

    public SmartDialog setMessageTextColor(int textColor) {
        mMessageTextColor = textColor;
        return this;
    }

    public SmartDialog setMessageTextSize(int textSize) {
        mMessageTextSize = textSize;
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

    public SmartDialog setTitleTextColor(int titleTextColor) {
        mTitleTextColor = titleTextColor;
        return this;
    }

    public SmartDialog setMessageMaxLines(int maxLines) {
        mMessageTextMaxLines = maxLines;
        return this;
    }

    public SmartDialog setWidthScale(float widthScale) {
        mWidthScale = widthScale;
        return this;
    }

    public SmartDialog defaultScale() {
        mWidthScale = 0.8f;
        return this;
    }

    public SmartDialog setHeightScale(float heightScale) {
        mHeightScale = heightScale;
        return this;
    }

    public void show() {
        if (mDialog != null) { // solo dialog, the dialog of this is already existed.
            setupDialog();
        } else {
            createDialog();
        }

        if (!mActivity.isFinishing()) {
            mDialog.show();
            scaleDialog();
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void createDialog() {
        mDialog = new AlertDialog.Builder(mActivity).create();
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mOnCancelListener != null) {
                    mOnCancelListener.onCancel(mDialog);

                } else if (!mCancelableOnTouchOutside) {
                    // finish current page when not allow user to cancel on touch outside
                    if (mActivity != null) {
                        mActivity.finish();
                    }
                }
            }
        });
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mDismissListener != null) {
                    mDismissListener.onDismiss(mDialog);
                }
            }
        });

        mDialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_smart, null);
        mTitleView = mDialogView.findViewById(R.id.title);
        mMessageView = mDialogView.findViewById(R.id.message);
        mNegativeBtn = mDialogView.findViewById(R.id.negative);
        mPositionBtn = mDialogView.findViewById(R.id.position);

        setupDialog();
    }

    private void setupDialog() {
        mDialog.setCanceledOnTouchOutside(mCancelableOnTouchOutside);
        mDialog.setCancelable(mCancelableOnTouchOutside);

        if (mCustomView != null) {
            mDialog.setContentView(mCustomView);
        } else {
            mDialog.setContentView(mDialogView);

            if (TextUtils.isEmpty(mMessageText)) {
                mMessageView.setVisibility(View.GONE);
            } else {
                mMessageView.setVisibility(View.VISIBLE);
            }
            mMessageView.setText(mMessageText);
            mMessageView.setGravity(mMessageGravity);
            mMessageView.setMaxLines(mMessageTextMaxLines);
            mMessageView.setTextColor(mMessageTextColor);
            mMessageView.setTextSize(mMessageTextSize);

            mTitleView.setMaxLines(mTitleMaxLines);
            mTitleView.setText(mTitleText);
            mTitleView.setTextColor(mTitleTextColor);
            if (TextUtils.isEmpty(mTitleText)) {
                mTitleView.setVisibility(View.GONE);
            } else {
                mTitleView.setVisibility(View.VISIBLE);
            }

            mPositionBtn.setText(mPositiveId);
            mPositionBtn.setTextColor(mPositiveTextColor);
            mPositionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPositiveListener != null) {
                        mPositiveListener.onClick(mDialog);
                    } else {
                        mDialog.dismiss();
                    }
                }
            });
            mNegativeBtn.setVisibility(mNegativeVisible);
            mNegativeBtn.setText(mNegativeId);
            mNegativeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mNegativeListener != null) {
                        mNegativeListener.onClick(mDialog);
                    } else {
                        mDialog.dismiss();
                    }
                }
            });
        }

        if (mWindowGravity != -1) {
            WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
            params.gravity = mWindowGravity;
            mDialog.getWindow().setAttributes(params);
        }

        if (mWindowAnim != -1) {
            WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
            params.windowAnimations = mWindowAnim;
            mDialog.getWindow().setAttributes(params);
        }
    }
}
