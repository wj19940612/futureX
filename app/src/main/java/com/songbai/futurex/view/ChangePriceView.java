package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.AnimUtils;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.CurrencyUtils;
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
    private boolean mModifiedManually;
    private OnPriceChangeListener mOnPriceChangeListener;

    public interface OnPriceChangeListener {
        void onPriceChange(double price);
    }

    public ChangePriceView(@NonNull Context context) {
        super(context);
        init();
    }

    public ChangePriceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void onPriceChange() {
        if (mOnPriceChangeListener != null) {
            try {
                double v = Double.parseDouble(mPrice.getText().toString());
                mOnPriceChangeListener.onPriceChange(v);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnPriceChangeListener(OnPriceChangeListener onPriceChangeListener) {
        mOnPriceChangeListener = onPriceChangeListener;
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
                    mPrice.setSelection(mPrice.getText().toString().length());
                    mTextWatcherDisable = false;
                    onPriceChange();
                    return;
                }
                onPriceChange();
            }
        });
        mPrice.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !mModifiedManually) {
                    mModifiedManually = true;
                }
            }
        });
        ((ViewGroup) mPrice.getParent()).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrice.requestFocus();
                mPrice.setSelection(mPrice.getText().toString().length());
                KeyBoardUtils.openKeyBoard(mPrice);
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
        mPrice.setSelection(mPrice.getText().toString().length());
        onPriceChange();
    }

    public void reset() {
        mTextWatcherDisable = true;
        mPrice.setText("");
        mTextWatcherDisable = false;
        mModifiedManually = false;
    }

    public void startScaleAnim() {
        Animation animation = AnimUtils.createSimpleScaleAnim(1.2f, 100);
        mPrice.startAnimation(animation);
    }

    public double getPrice() {
        String s = mPrice.getText().toString();
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String formatPrice(double price) {
        return formatPrice(String.valueOf(price));
    }

    private String formatPrice(String price) {
        int pointIndex = price.indexOf('.');
        if (pointIndex > -1) {
            int endIndex = Math.min(price.length(), pointIndex + mScale + 1);
            return price.substring(0, endIndex);
        }
        return price;
    }

    @OnClick({R.id.minus, R.id.plus})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.minus:
                mTextWatcherDisable = true;
                minus();
                mModifiedManually = true;
                mTextWatcherDisable = false;
                onPriceChange();
                break;
            case R.id.plus:
                mTextWatcherDisable = true;
                plus();
                mModifiedManually = true;
                mTextWatcherDisable = false;
                onPriceChange();
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
        mPrice.setText(CurrencyUtils.getPrice(value, mScale));
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
        mPrice.setText(CurrencyUtils.getPrice(value, mScale));
        mPrice.setSelection(mPrice.getText().toString().length());
    }

    public void setModifiedManually(boolean modifiedManually) {
        mModifiedManually = modifiedManually;
    }

    public boolean isModifiedManually() {
        return mModifiedManually;
    }
}
