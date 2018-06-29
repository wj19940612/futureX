package com.songbai.futurex.view.safety;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.songbai.futurex.R;

/**
 * Modified by $nishuideyu$ on 2018/6/29
 * <p>
 * Description: 九宫格view
 * </p>
 */
public class SudokuView extends View {

    protected int mNormalColor;
    protected int mSelectColor;

    public SudokuView(Context context) {
        this(context, null);
    }

    public SudokuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SudokuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        handleAttrs(attrs);

    }

    private void handleAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SudokuView);
        mNormalColor = typedArray.getColor(R.styleable.GestureUnLockSudoKuView_normalColor, Color.BLACK);
        mSelectColor = typedArray.getColor(R.styleable.GestureUnLockSudoKuView_selectColor, Color.GREEN);


        typedArray.recycle();
    }
}
