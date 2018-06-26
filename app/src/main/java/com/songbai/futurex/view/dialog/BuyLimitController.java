package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.OtcWarePoster;
import com.songbai.futurex.view.SmartDialog;

import java.util.ArrayList;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class BuyLimitController extends SmartDialog.CustomViewController {
    private Context mContext;
    private String[] payType = new String[]{OtcWarePoster.CONDITION_AUTH, OtcWarePoster.CONDITION_TRADE};
    private ArrayList<String> conditionType = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private String mConditionType;

    public BuyLimitController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_buy_limit_selector, null);
    }

    @Override
    protected void onInitView(final View view, final SmartDialog dialog) {
        if (!TextUtils.isEmpty(mConditionType)) {
            String[] split = mConditionType.split(",");
            for (String s : split) {
                if (!conditionType.contains(s)) {
                    conditionType.add(s);
                }
            }
        }
        RelativeLayout authenticationPay = view.findViewById(R.id.authentication);
        RelativeLayout dealCount = view.findViewById(R.id.dealCount);
        TextView confirm = view.findViewById(R.id.confirm);
        TextView cancel = view.findViewById(R.id.cancel);
        authenticationPay.setSelected(conditionType.contains(payType[0]));
        dealCount.setSelected(conditionType.contains(payType[1]));
        authenticationPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    if (!conditionType.contains(payType[0])) {
                        conditionType.add(payType[0]);
                    }
                } else {
                    conditionType.remove(payType[0]);
                }
            }
        });
        dealCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    if (!conditionType.contains(payType[1])) {
                        conditionType.add(payType[1]);
                    }
                } else {
                    conditionType.remove(payType[1]);
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
                    if (conditionType.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (String s : conditionType) {
                            sb.append(s);
                            sb.append(",");
                        }
                        mOnItemClickListener.onConfirmClick(sb.toString().substring(0, sb.length() - 1));
                    }
                    dialog.dismiss();
                }
            }
        });
    }

    public void setConditionType(String conditionType) {
        mConditionType = conditionType;
    }

    public interface OnItemClickListener {
        void onConfirmClick(String conditionType);
    }

    public BuyLimitController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }
}
