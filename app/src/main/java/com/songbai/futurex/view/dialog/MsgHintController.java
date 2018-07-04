package com.songbai.futurex.view.dialog;

import android.content.Context;
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
public class MsgHintController extends SmartDialog.CustomViewController {

    private TextView mHintMsg;
    private OnClickListener mOnClickListener;
    private Context mContext;
    private TextView mConfirm;
    private int mImgRes;
    private int mMsgRes;
    private int mConfirmText;

    public interface OnClickListener {
        void onConfirmClick();
    }

    public MsgHintController(Context context, OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_msg_hint, null);
    }

    @Override
    public void onInitView(View view, final SmartDialog dialog) {
        mHintMsg = (TextView) view.findViewById(R.id.hintMsg);
        ImageView close = (ImageView) view.findViewById(R.id.close);
        mConfirm = (TextView) view.findViewById(R.id.confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mOnClickListener.onConfirmClick();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        setMsg(mMsgRes);
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
}