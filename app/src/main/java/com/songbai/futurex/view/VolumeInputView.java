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
    @BindView(R.id.volume)
    EditText mVolume;
    @BindView(R.id.baseCurrency)
    TextView mBaseCurrency;

    private int mScale;
    private boolean mTextWatcherDisable;

    public VolumeInputView(@NonNull Context context) {
        super(context);
        init();
    }

    public VolumeInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_volume_input, this, true);
        ButterKnife.bind(this);

        mVolume.addTextChangedListener(new ValidationWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (mTextWatcherDisable) return;

                String number = s.toString();
                if (!isValid(number)) {
                    mTextWatcherDisable = true;
                    mVolume.setText(formatVolume(number));
                    mTextWatcherDisable = false;
                }
            }
        });
    }

    private String formatVolume(String number) {
        int pointIndex = number.indexOf('.');
        if (pointIndex > -1) {
            return number.substring(0, pointIndex + mScale + 1);
        }
        return number;
    }

    private boolean isValid(String number) {
        int pointIndex = number.indexOf('.');
        int lastIndex = number.length() - 1;
        if (pointIndex > -1 && lastIndex - pointIndex > mScale) {
            return false;
        }
        return true;
    }

    public void setScale(int scale) {
        mScale = scale;
    }

    public void setBaseCurrency(String baseCurrency) {
        mBaseCurrency.setText(baseCurrency);
    }
}
