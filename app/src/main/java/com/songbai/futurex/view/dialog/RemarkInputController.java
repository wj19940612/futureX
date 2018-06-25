package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.SmartDialog;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class RemarkInputController extends SmartDialog.CustomViewController {
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private EditText mRemark;
    private String mRemarkText;

    public RemarkInputController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_remark_input_selector, null);
    }

    @Override
    protected void onInitView(final View view, final SmartDialog dialog) {
        mRemark = view.findViewById(R.id.remark);
        restoreRemark(mRemarkText);
        TextView confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    String string = mRemark.getText().toString();
                    mOnItemClickListener.onConfirmClick(string);
                    dialog.dismiss();
                }
            }
        });
    }

    public void restoreRemark(String remark) {
        mRemarkText = remark;
        if (mRemark != null) {
            mRemark.setText(remark);
            mRemark.setSelection(mRemark.getText().length());
        }
    }

    public interface OnItemClickListener {
        void onConfirmClick(String remark);
    }

    public RemarkInputController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }
}
