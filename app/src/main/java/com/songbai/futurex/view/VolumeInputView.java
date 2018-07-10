package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Modified by john on 2018/6/25
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class VolumeInputView extends FrameLayout {
    private static final int DEFAULT_VOLUME_SCALE = 8;

    @BindView(R.id.volume)
    EditText mVolume;
    @BindView(R.id.baseCurrency)
    TextView mBaseCurrency;

    private int mVolumeScale;
    private boolean mTextWatcherDisable;
    private OnVolumeChangeListener mOnVolumeChangeListener;

    public interface OnVolumeChangeListener {
        void onVolumeChange(double volume);
    }

    public VolumeInputView(@NonNull Context context) {
        super(context);
        init();
    }

    public VolumeInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnVolumeChangeListener(OnVolumeChangeListener onVolumeChangeListener) {
        mOnVolumeChangeListener = onVolumeChangeListener;
    }

    private void onVolumeChange() {
        if (mOnVolumeChangeListener != null) {
            try {
                double v = Double.parseDouble(mVolume.getText().toString());
                mOnVolumeChangeListener.onVolumeChange(v);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public double getVolume() {
        return CurrencyUtils.getDouble(mVolume.getText().toString());
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_volume_input, this, true);
        ButterKnife.bind(this);
        mVolumeScale = DEFAULT_VOLUME_SCALE;

        mVolume.addTextChangedListener(new ValidationWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (mTextWatcherDisable) return;

                String number = s.toString();
                if (!isValid(number)) {
                    mTextWatcherDisable = true;
                    mVolume.setText(formatVolume(number));
                    mVolume.setSelection(mVolume.getText().toString().length());
                    mTextWatcherDisable = false;
                    onVolumeChange();
                    return;
                }
                onVolumeChange();
            }
        });
    }

    public void setVolume(double volume) {
        mVolume.setText(formatVolume(String.valueOf(volume)));
        mVolume.setSelection(mVolume.getText().toString().length());
        onVolumeChange();
    }

    public void reset() {
        mTextWatcherDisable = true;
        mVolume.setText("");
        mTextWatcherDisable = false;
        mBaseCurrency.setText("");
    }

    private String formatVolume(String number) {
        int pointIndex = number.indexOf('.');
        if (pointIndex > -1) {
            int endIndex = Math.min(number.length(), pointIndex + mVolumeScale + 1);
            return number.substring(0, endIndex);
        }
        return number;
    }

    private boolean isValid(String number) {
        int pointIndex = number.indexOf('.');
        int lastIndex = number.length() - 1;
        if (pointIndex > -1 && lastIndex - pointIndex > mVolumeScale) {
            return false;
        }
        return true;
    }

    public void setVolumeScale(int scale) {
        mVolumeScale = scale;
    }

    public void setBaseCurrency(String baseCurrency) {
        mBaseCurrency.setText(baseCurrency);
    }
}
