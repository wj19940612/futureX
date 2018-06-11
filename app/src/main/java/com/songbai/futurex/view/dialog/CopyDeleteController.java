package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.CoinAddress;
import com.songbai.futurex.view.SmartDialog;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class CopyDeleteController extends SmartDialog.CustomViewController {
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private CoinAddress mCoinAddress;

    public CopyDeleteController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_bottom_copy_delete, null);
    }

    @Override
    protected void onInitView(View view, final SmartDialog dialog) {
        TextView copy = view.findViewById(R.id.copy);
        TextView delete = view.findViewById(R.id.delete);
        TextView cancel = view.findViewById(R.id.cancel);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDrawWalletAddr(mCoinAddress.getId());
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onCopyClick();
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

    public void setCoinAddress(CoinAddress coinAddress) {
        mCoinAddress = coinAddress;
    }

    public interface OnItemClickListener {
        void onCopyClick();

        void onDeleteClick(CoinAddress coinAddress);
    }

    public CopyDeleteController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }

    private void removeDrawWalletAddr(int id) {
        Apic.removeDrawWalletAddr(id)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onDeleteClick(mCoinAddress);
                        }
                    }
                })
                .fire();
    }
}
