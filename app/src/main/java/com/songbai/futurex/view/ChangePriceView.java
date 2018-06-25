package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.NumUtils;
import com.songbai.futurex.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Modified by john on 2018/6/25
 * <p>
 * Description: 改变价格控件
 * <p>
 * APIs:
 */
public class ChangePriceView extends FrameLayout {
    @BindView(R.id.price)
    EditText mPrice;
    @BindView(R.id.minus)
    LinearLayout mMinus;
    @BindView(R.id.plus)
    RelativeLayout mPlus;

    private int mScale;
    private boolean mTextWatcherDisable;
    private double mChangeSize;

    public ChangePriceView(@NonNull Context context) {
        super(context);
        init();
    }

    public ChangePriceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_change_price, this, true);
        ButterKnife.bind(this);

        mPrice.addTextChangedListener(new ValidationWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (mTextWatcherDisable) return;

                String number = s.toString();
                if (!isValid(number)) {
                    mTextWatcherDisable = true;
                    mPrice.setText(formatPrice(number));
                    mTextWatcherDisable = false;
                }
            }
        });
    }

    private boolean isValid(String number) {
        int pointIndex = number.indexOf('.');
        int lastIndex = number.length() - 1;
        if (pointIndex > -1 && lastIndex - pointIndex > mScale) {
            return false;
        }
        return true;
    }

    public void setPriceScale(int scale) {
        mScale = scale;
        mChangeSize = Math.pow(10, -mScale);
    }

    public void setPrice(double price) {
        mPrice.setText(formatPrice(price));
    }

    public String getPrice() {
        return mPrice.toString();
    }

    private String formatPrice(double price) {
        return formatPrice(String.valueOf(price));
    }

    private String formatPrice(String price) {
        int pointIndex = price.indexOf('.');
        if (pointIndex > -1) {
            return price.substring(0, pointIndex + mScale + 1);
        }
        return price;
    }

    @OnClick({R.id.minus, R.id.plus})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.minus:
                mTextWatcherDisable = true;
                minus();
                mTextWatcherDisable = false;
                break;
            case R.id.plus:
                mTextWatcherDisable = true;
                plus();
                mTextWatcherDisable = false;
                break;
        }
    }

    private void plus() {
        String text = mPrice.getText().toString();
        double value = 0;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        value += mChangeSize;
        mPrice.setText(NumUtils.getPrice(value, mScale));
        mPrice.setSelection(mPrice.getText().toString().length());
    }

    private void minus() {
        String text = mPrice.getText().toString();
        double value = 0;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        value -= mChangeSize;
        value = Math.max(0, value);
        mPrice.setText(NumUtils.getPrice(value, mScale));
        mPrice.setSelection(mPrice.getText().toString().length());
    }
}
