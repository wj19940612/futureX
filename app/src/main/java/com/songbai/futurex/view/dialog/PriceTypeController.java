package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.OtcWarePoster;
import com.songbai.futurex.view.SmartDialog;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class PriceTypeController extends SmartDialog.CustomViewController {
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public PriceTypeController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_price_type_selector, null);
    }

    @Override
    protected void onInitView(View view, final SmartDialog dialog) {
        TextView fixedType = view.findViewById(R.id.fixedType);
        TextView floatingType = view.findViewById(R.id.floatingType);
        TextView cancel = view.findViewById(R.id.cancel);
        fixedType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(OtcWarePoster.FIXED_PRICE);
                    dialog.dismiss();
                }
            }
        });
        floatingType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(OtcWarePoster.FLOATING_PRICE);
                    dialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(int fixedPrice);
    }

    public PriceTypeController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }
}
