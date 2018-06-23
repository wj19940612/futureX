package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.SmartDialog;

import java.util.ArrayList;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class PayTypeController extends SmartDialog.CustomViewController {
    private Context mContext;
    private String[] payType = new String[]{"bankPay", "aliPay", "wxPay"};
    private ArrayList<String> selectedPayType = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public PayTypeController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_pay_type_selector, null);
    }

    @Override
    protected void onInitView(final View view, final SmartDialog dialog) {
        RelativeLayout unionPay = view.findViewById(R.id.unionPay);
        RelativeLayout aliPay = view.findViewById(R.id.aliPay);
        RelativeLayout wechatPay = view.findViewById(R.id.wechatPay);
        TextView confirm = view.findViewById(R.id.confirm);
        TextView cancel = view.findViewById(R.id.cancel);
        unionPay.setSelected(selectedPayType.contains(payType[0]));
        aliPay.setSelected(selectedPayType.contains(payType[1]));
        wechatPay.setSelected(selectedPayType.contains(payType[2]));
        unionPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    if (!selectedPayType.contains(payType[0])) {
                        selectedPayType.add(payType[0]);
                    }
                } else {
                    selectedPayType.remove(payType[0]);
                }
            }
        });
        aliPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    if (!selectedPayType.contains(payType[1])) {
                        selectedPayType.add(payType[1]);
                    }
                } else {
                    selectedPayType.remove(payType[1]);
                }
            }
        });
        wechatPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    if (!selectedPayType.contains(payType[2])) {
                        selectedPayType.add(payType[2]);
                    }
                } else {
                    selectedPayType.remove(payType[2]);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onConfirmClick();
                    dialog.dismiss();
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onConfirmClick();
    }

    public PayTypeController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }
}
