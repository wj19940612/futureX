package com.songbai.futurex.fragment.dialog;

import android.view.Gravity;

import com.songbai.futurex.R;


/**
 * 底部弹窗基础类
 */
public class BottomDialogFragment extends BaseDialogFragment {

    protected float getWidthRatio() {
        return 1f;
    }

    protected int getWindowGravity() {
        return Gravity.BOTTOM;
    }

    protected int getDialogTheme() {
        return R.style.BaseDialog_Bottom;
    }

}
