package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.text.TextUtils;
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
public class TradeLimitController extends SmartDialog.CustomViewController {
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private EditText mMinTurnover;
    private EditText mMaxTurnover;
    private double mMinTurnoverNum;
    private double mMaxTurnoverNum;

    public TradeLimitController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_trade_limit_selector, null);
    }

    @Override
    protected void onInitView(View view, final SmartDialog dialog) {
        mMinTurnover = view.findViewById(R.id.minTurnover);
        mMaxTurnover = view.findViewById(R.id.maxTurnover);
        restoreLimit(mMinTurnoverNum, mMaxTurnoverNum);
        TextView confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String minTurnoverStr = mMinTurnover.getText().toString();
                String maxTurnoverStr = mMaxTurnover.getText().toString();
                if (!TextUtils.isEmpty(minTurnoverStr) && !TextUtils.isEmpty(minTurnoverStr)) {
                    int minTurnover = Integer.valueOf(minTurnoverStr);
                    int maxTurnover = Integer.valueOf(maxTurnoverStr);
                    if (maxTurnover > minTurnover) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onConfirmClick(minTurnover, maxTurnover);
                            dialog.dismiss();
                        }
                    }
                }
            }
        });
    }

    public void restoreLimit(double minTurnover, double maxTurnover) {
        mMinTurnoverNum = minTurnover;
        mMaxTurnoverNum = maxTurnover;
        if (mMinTurnover != null) {
            mMinTurnover.setText(String.valueOf(minTurnover));
            mMinTurnover.setSelection(mMinTurnover.getText().length());
        }
        if (mMinTurnover != null) {
            mMaxTurnover.setText(String.valueOf(maxTurnover));
            mMaxTurnover.setSelection(mMaxTurnover.getText().length());
        }
    }

    public interface OnItemClickListener {
        void onConfirmClick(int minTurnover, int maxTurnover);
    }

    public TradeLimitController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }
}
