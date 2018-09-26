package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.SmartDialog;

/**
 * Modified by john on 2018/6/1
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class BalanceNotEnoughController extends SmartDialog.CustomViewController {

    private TextView mHintMsg;
    private OnClickListener mOnClickListener;
    private Context mContext;
    private TextView mConfirm;
    private int mImgRes;
    private int mMsgRes;
    private int mConfirmText;
    private View.OnClickListener mOnColseClickListener;
    private ImageView mClose;
    private String mMsg;
    private int mCloseRes;
    private TextView mBtnClose;

    public void setOnColseClickListener(View.OnClickListener onColseClickListener) {
        mOnColseClickListener = onColseClickListener;
    }

    public interface OnClickListener {
        void onConfirmClick();
    }

    public BalanceNotEnoughController(Context context, OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_balance_not_enough, null);
    }

    @Override
    public void onInitView(View view, final SmartDialog dialog) {
        mHintMsg = (TextView) view.findViewById(R.id.hintMsg);
        mClose = (ImageView) view.findViewById(R.id.close);
        mConfirm = (TextView) view.findViewById(R.id.confirm);
        mBtnClose = (TextView) view.findViewById(R.id.btnClose);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mOnClickListener.onConfirmClick();
            }
        });
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnColseClickListener != null) {
                    mOnColseClickListener.onClick(v);
                }
                dialog.dismiss();
            }
        });
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (mMsgRes != 0) {
            setMsg(mMsgRes);
        }
        if (!TextUtils.isEmpty(mMsg)) {
            setMsg(mMsg);
        }
        setCloseText(mCloseRes);
        setImageRes(mImgRes);
        if (mConfirmText != 0) {
            setConfirmText(mConfirmText);
        }
    }

    public void setImageRes(int imgRes) {
        mImgRes = imgRes;
        if (isViewInitialized()) {
            mHintMsg.setCompoundDrawablesWithIntrinsicBounds(0, imgRes, 0, 0);
        }
    }

    public void setMsg(int msgRes) {
        mMsgRes = msgRes;
        if (isViewInitialized()) {
            mHintMsg.setText(msgRes);
        }
    }

    public void setMsg(String msg) {
        mMsg = msg;
        if (isViewInitialized()) {
            mHintMsg.setText(msg);
        }
    }

    public void setConfirmText(int confirmText) {
        mConfirmText = confirmText;
        if (isViewInitialized()) {
            mConfirm.setText(mConfirmText);
        }
    }

    public void mConfirm(int titleRes) {
        if (isViewInitialized()) {
            mConfirm.setText(titleRes);
        }
    }

    public void setCloseText(int closeRes) {
        mCloseRes = closeRes;
        if (isViewInitialized()) {
            mBtnClose.setVisibility(closeRes == 0 ? View.GONE : View.VISIBLE);
            mBtnClose.setText(closeRes);
        }
    }

    public void setCrossVisibility(int visibility) {
        if (isViewInitialized()) {
            mClose.setVisibility(visibility);
        }
    }
}
