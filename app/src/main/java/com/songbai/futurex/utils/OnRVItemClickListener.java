package com.songbai.futurex.utils;

import android.view.View;

/**
 * Modified by john on 2018/6/3
 * <p>
 * Description: RecycleView item click listener
 * <p>
 * APIs:
 */
public interface OnRVItemClickListener {
    /**
     * @param view itemView
     * @param position thie position of itemView in RV
     * @param obj ths related data of this item
     */
    void onItemClick(View view, int position, Object obj);
}
