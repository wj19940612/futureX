package com.songbai.futurex.utils.inputfilter;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;

import com.songbai.futurex.utils.ToastUtil;

/**
 * @author yangguangda
 * @date 2018/6/26
 */
public class MoneyValueFilter extends DigitsKeyListener {

    private static final String TAG = "MoneyValueFilter";
    private static final String DECIMAIL_POINT = ".";
    private static final String MINUS_SIGN = "-";
    private double mMaxValue = Integer.MAX_VALUE;
    private double mMinValue = 0;
    private boolean mFilterMax;
    private boolean mFilterMin;

    public MoneyValueFilter() {
        super(false, true);
    }

    public MoneyValueFilter(boolean sign, boolean decimal) {
        super(sign, decimal);
    }

    private int digits = 2;

    public MoneyValueFilter setDigits(int d) {
        digits = d;
        return this;
    }

    public MoneyValueFilter filterMax(double maxValue) {
        mMaxValue = maxValue;
        mFilterMax = true;
        return this;
    }

    public MoneyValueFilter filterMin(int minValue) {
        mMinValue = minValue;
        mFilterMin = true;
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
        if (source.toString().equals(DECIMAIL_POINT) && dstart == 0) {
            return "0.";
        }
        //以-开始的时候，自动在前面添加0
        if (source.toString().equals(DECIMAIL_POINT) && dstart == 1 && dest.toString().equals(MINUS_SIGN)) {
            return "0.";
        }
        //如果起始位置为0,且第二位跟的不是".",则无法后续输入
        if (!source.toString().equals(DECIMAIL_POINT) && dest.toString().equals("0")) {
            return "";
        }
        //如果起始位置为0,且第二位跟的不是".",则无法后续输入
        if (!source.toString().equals(DECIMAIL_POINT) && dest.toString().equals("0")) {
            return "";
        }

        //验证输入金额的最大值
        if (mFilterMax && !"".equals(source.toString())) {
            if (!source.toString().equals(MINUS_SIGN)) {
                double dold = Double.parseDouble(dest.toString() + source.toString());
                if (dold > mMaxValue) {
                    ToastUtil.show("最大不能大于" + mMaxValue);
                    return dest.subSequence(dstart, dend);
                } else if (dold == mMaxValue) {
                    if (DECIMAIL_POINT.equals(source.toString())) {
                        ToastUtil.show("最大不能大于" + mMaxValue);
                        return dest.subSequence(dstart, dend);
                    }
                }
            }
        }

        //验证输入金额的最小值
        if (mFilterMin && !"".equals(source.toString())) {
            if (!source.toString().equals(MINUS_SIGN)) {
                double dold = Double.parseDouble(dest.toString() + source.toString());
                if (dold < mMinValue) {
                    ToastUtil.show("最小不能小于" + mMinValue);
                    return dest.subSequence(dstart, dend);
                } else if (dold == mMinValue) {
                    if (DECIMAIL_POINT.equals(source.toString())) {
                        ToastUtil.show("最小不能小于" + mMinValue);
                        return dest.subSequence(dstart, dend);
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