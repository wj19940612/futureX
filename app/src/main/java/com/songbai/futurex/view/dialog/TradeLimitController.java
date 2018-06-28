package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.inputfilter.MoneyValueFilter;
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
    private String mPayCurrencySymbol;

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
        mMinTurnover.setFilters(new InputFilter[]{new MoneyValueFilter().filterMax(1000000)});
        mMaxTurnover.setFilters(new InputFilter[]{new MoneyValueFilter().filterMax(1000000)});
        TextView payCurrency = view.findViewById(R.id.payCurrency);
        payCurrency.setText(mPayCurrencySymbol.toUpperCase());
        restoreLimit(mMinTurnoverNum, mMaxTurnoverNum);
        TextView confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String minTurnoverStr = mMinTurnover.getText().toString();
                String maxTurnoverStr = mMaxTurnover.getText().toString();
                if (!TextUtils.isEmpty(minTurnoverStr) && !TextUtils.isEmpty(maxTurnoverStr)) {
                    double minTurnover = Double.valueOf(minTurnoverStr);
                    double maxTurnover = Double.valueOf(maxTurnoverStr);
                    if (maxTurnover > minTurnover) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onConfirmClick(minTurnover, maxTurnover);
                            dialog.dismiss();
                        }
                    } else {
                        ToastUtil.show(R.string.please_input_right_tread_limit);
                    }
                }
            }
        });
    }

    public void restoreLimit(double minTurnover, double maxTurnover) {
        mMinTurnoverNum = minTurnover;
        mMaxTurnoverNum = maxTurnover;
        if (mMinTurnover != null && minTurnover != 0) {
            mMinTurnover.setText(FinanceUtil.trimTrailingZero(minTurnover));
            mMinTurnover.setSelection(mMinTurnover.getText().length());
        }
        if (mMinTurnover != null && maxTurnover != 0) {
            mMaxTurnover.setText(FinanceUtil.trimTrailingZero(maxTurnover));
            mMaxTurnover.setSelection(mMaxTurnover.getText().length());
        }
    }

    public void setPayCurrencySymbol(String payCurrencySymbol) {
        mPayCurrencySymbol = payCurrencySymbol;
    }

    public interface OnItemClickListener {
        void onConfirmClick(double minTurnover, double maxTurnover);
    }

    public TradeLimitController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }
}
