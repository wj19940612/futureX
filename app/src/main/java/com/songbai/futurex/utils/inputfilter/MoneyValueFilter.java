package com.songbai.futurex.utils.inputfilter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.ToastUtil;

import java.math.BigDecimal;

/**
 * @author yangguangda
 * @date 2018/6/26
 */
public class MoneyValueFilter extends DigitsKeyListener {

    private static final String TAG = "MoneyValueFilter";
    private static final String DECIMAL_POINT = ".";
    private static final String MINUS_SIGN = "-";
    private static final String PLUS_SIGN = "+";
    private Context mContext;
    private double mMaxValue = Integer.MAX_VALUE;
    private double mMinValue = 0;
    private boolean mFilterMax;
    private boolean mFilterMin;
    private int digits = 2;

    private OnFilterMinListener mOnFilterMinListener;
    private OnFilterMaxListener mOnFilterMaxListener;

    public interface OnFilterMinListener {
        void filterMin(double dold, double minValue);
    }

    public interface OnFilterMaxListener {
        void filterMax(double dold, double maxValue);
    }

    public MoneyValueFilter(Context context) {
        super(false, true);
        mContext = context;
    }

    public MoneyValueFilter(Context context, boolean sign, boolean decimal) {
        super(sign, decimal);
        mContext = context;
    }

    public MoneyValueFilter setDigits(int d) {
        digits = d;
        return this;
    }

    public MoneyValueFilter filterMax(double maxValue) {
        return filterMax(maxValue, null);
    }

    public MoneyValueFilter filterMin(double minValue) {
        return filterMin(minValue, null);
    }

    public MoneyValueFilter filterMax(double maxValue, OnFilterMaxListener onFilterMaxListener) {
        mMaxValue = Double.valueOf(FinanceUtil.formatWithScale(maxValue, digits));
        mFilterMax = true;
        mOnFilterMaxListener = onFilterMaxListener;
        return this;
    }

    public MoneyValueFilter filterMin(double minValue, OnFilterMinListener onFilterMinListener) {
        mMinValue = Double.valueOf(FinanceUtil.formatWithScale(minValue, digits));
        mFilterMin = true;
        mOnFilterMinListener = onFilterMinListener;
        return this;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        CharSequence out = super.filter(source, start, end, dest, dstart, dend);

        Log.e("wtf", "filter: " + source + ",start" + start + ",end" + end + ",dest" + dest + ",dstart" + dstart + ",dend" + dend);
        // if changed, replace the source
        if (out != null) {
            source = out;
            start = 0;
            end = out.length();
        }

        int len = end - start;

        // if deleting, source is empty
        // and deleting can't break anything
        if (len == 0) {
            return source;
        }

        //以点开始的时候，自动在前面添加0
        if (source.toString().equals(DECIMAL_POINT) && dstart == 0) {
            return "0.";
        }
        //以-开始的时候，自动在前面添加0
        if (source.toString().equals(DECIMAL_POINT) && dstart == 1 && dest.toString().equals(MINUS_SIGN)) {
            return "0.";
        }
        //如果起始位置为0,且第二位跟的不是".",则无法后续输入
        if (!source.toString().equals(DECIMAL_POINT) && "0".equals(dest.toString())) {
            return "";
        }

        //验证输入金额的最大值
        if (mFilterMax && !"".equals(source.toString())) {
            if (!source.toString().equals(MINUS_SIGN) && !source.toString().equals(PLUS_SIGN)) {
                StringBuilder sb = new StringBuilder(dest.toString());
                if (dstart < sb.toString().length()) {
                    //在指定的位置，插入
                    sb.insert(dstart, source.toString());
                } else {
                    sb.append(source.toString());
                }
                double dold = new BigDecimal(sb.toString()).doubleValue();
                if (dold > mMaxValue) {
                    if (mOnFilterMaxListener != null) {
                        mOnFilterMaxListener.filterMax(dold, mMaxValue);
                    } else {
                        ToastUtil.show(mContext.getString(R.string.max_can_not_greater_than, FinanceUtil.subZeroAndDot(mMaxValue, digits)));
                    }
                    return dest.subSequence(dstart, dend);
                } else if (dold == mMaxValue) {
                    if (DECIMAL_POINT.equals(source.toString())) {
                        if (mOnFilterMaxListener != null) {
                            mOnFilterMaxListener.filterMax(dold, mMaxValue);
                        } else {
                            ToastUtil.show(mContext.getString(R.string.max_can_not_greater_than, FinanceUtil.subZeroAndDot(mMaxValue, digits)));
                        }
                        return dest.subSequence(dstart, dend);
                    }
                }
            }
        }

        //验证输入金额的最小值
        if (mFilterMin && !"".equals(source.toString())) {
            if (!source.toString().equals(MINUS_SIGN) && !source.toString().equals(PLUS_SIGN)) {
                StringBuilder sb = new StringBuilder(dest.toString());
                if (dstart < sb.toString().length()) {
                    //在指定的位置，插入
                    sb.insert(dstart, source.toString());
                } else {
                    sb.append(source.toString());
                }
                double dold = new BigDecimal(sb.toString()).doubleValue();
                if (dold < mMinValue) {
                    if (mMinValue < 0) {
                        if (mOnFilterMinListener != null) {
                            mOnFilterMinListener.filterMin(dold, mMinValue);
                        } else {
                            ToastUtil.show(mContext.getString(R.string.min_can_not_less_than, FinanceUtil.subZeroAndDot(mMinValue, digits)));
                        }
                        return dest.subSequence(dstart, dend);
                    }
                } else if (dold == mMinValue) {
                    if (DECIMAL_POINT.equals(source.toString())) {
                        if (mMinValue < 0) {
                            if (mOnFilterMinListener != null) {
                                mOnFilterMinListener.filterMin(dold, mMinValue);
                            } else {
                                ToastUtil.show(mContext.getString(R.string.min_can_not_less_than, FinanceUtil.subZeroAndDot(mMinValue, digits)));
                            }
                            return dest.subSequence(dstart, dend);
                        }
                    }
                }
            }
        }

        int dlen = dest.length();

        // Find the position of the decimal .
        for (int i = 0; i < dstart; i++) {
            if (dest.charAt(i) == '.') {
                // being here means, that a number has
                // been inserted after the dot
                // check if the amount of digits is right
                return (dlen - (i + 1) + len > digits) ?
                        "" :
                        new SpannableStringBuilder(source, start, end);
            }
        }
        for (int i = start; i < end; ++i) {
            if (source.charAt(i) == '.') {
                // being here means, dot has been inserted
                // check if the amount of digits is right
                if ((dlen - dend) + (end - (i + 1)) > digits) {
                    return "";
                } else {
                    break;  // return new SpannableStringBuilder(source, start, end);
                }
            }
        }
        // if the dot is after the inserted part,
        // nothing can break
        return new SpannableStringBuilder(source, start, end);
    }
}