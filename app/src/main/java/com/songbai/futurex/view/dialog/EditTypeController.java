package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.mine.CoinAddress;
import com.songbai.futurex.view.SmartDialog;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class EditTypeController extends SmartDialog.CustomViewController {
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private CoinAddress mCoinAddress;

    public EditTypeController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_edit_type, null);
    }

    @Override
    protected void onInitView(View view, final SmartDialog dialog) {
        TextView edit = view.findViewById(R.id.edit);
        TextView delete = view.findViewById(R.id.delete);
        TextView cancel = view.findViewById(R.id.cancel);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onEditClick();
                    dialog.dismiss();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onDeleteClick(dialog);
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

    public void setCoinAddress(CoinAddress coinAddress) {
        mCoinAddress = coinAddress;
    }

    public interface OnItemClickListener {
        void onEditClick();

        void onDeleteClick(SmartDialog dialog);
    }

    public EditTypeController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }
}
