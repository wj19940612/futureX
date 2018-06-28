package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.songbai.futurex.R;

/**
 * @author yangguangda
 * @date 2018/6/28
 */
public class CountDownView extends FrameLayout {
    public CountDownView(@NonNull Context context) {
        this(context, null);
    }

    public CountDownView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_count_down, this, false);
        addView(view);
    }
}
