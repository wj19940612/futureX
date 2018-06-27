package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.mine.BankCardBean;
import com.songbai.futurex.model.mine.BindBankList;
import com.songbai.futurex.model.status.PayType;
import com.songbai.futurex.view.SmartDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class PayTypeController extends SmartDialog.CustomViewController {
    private Context mContext;
    private String[] payType = new String[]{PayType.BANK_PAY, PayType.ALIPAY, PayType.WXPAY};
    private HashMap<String, String> mSelectedPayTypes = new HashMap<>();
    private OnItemClickListener mOnItemClickListener;
    private BindBankList mBankList;

    public PayTypeController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_pay_type_selector, null);
    }

    @Override
    protected void onInitView(final View view, final SmartDialog dialog) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextView confirm = view.findViewById(R.id.confirm);
        TextView cancel = view.findViewById(R.id.cancel);
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
                    mOnItemClickListener.onConfirmClick(mSelectedPayTypes);
                    dialog.dismiss();
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        final PayTypeAdapter adapter = new PayTypeAdapter();
        adapter.setPayInfo(mSelectedPayTypes);
        adapter.setList(mBankList);
        adapter.setOnItemClickListener(new PayTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String payType, BankCardBean bankCardBean) {
                int id = bankCardBean.getId();
                if (mSelectedPayTypes.containsKey(payType)) {
                    if (mSelectedPayTypes.get(payType).equals(String.valueOf(id))) {
                        mSelectedPayTypes.remove(payType);
                    } else {
                        mSelectedPayTypes.put(payType, String.valueOf(id));
                    }
                } else {
                    mSelectedPayTypes.put(payType, String.valueOf(id));
                }
                adapter.setPayInfo(mSelectedPayTypes);
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void setPayInfo(HashMap<String, String> payInfo) {
        mSelectedPayTypes = payInfo;
    }

    public void setBankList(BindBankList bankList) {
        mBankList = bankList;
    }

    public interface OnItemClickListener {
        void onConfirmClick(HashMap<String, String> payInfo);
    }

    public PayTypeController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }

    static class PayTypeAdapter extends RecyclerView.Adapter {
        private OnItemClickListener mListener;
        ArrayList<BankCardBean> mList = new ArrayList<>();
        private HashMap<String, String> mPayInfo;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pay_type, parent, false);
            return new PayTypeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PayTypeHolder) {
                ((PayTypeHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(BindBankList bindBankList) {
            ArrayList<BankCardBean> list = new ArrayList<>();
            BankCardBean aliPay = bindBankList.getAliPay();
            if (aliPay.getBind() == BankCardBean.ALIPAY_WECHATPAY_BIND) {
                list.add(aliPay);
            }
            BankCardBean wechat = bindBankList.getWechat();
            if (wechat.getBind() == BankCardBean.ALIPAY_WECHATPAY_BIND) {
                list.add(wechat);
            }
            List<BankCardBean> bankCard = bindBankList.getBankCard();
            for (BankCardBean bankCardBean : bankCard) {
                list.add(bankCardBean);
            }
            mList.clear();
            mList.addAll(list);
        }

        public void setPayInfo(HashMap<String, String> payInfo) {
            mPayInfo = payInfo;
        }

        public interface OnItemClickListener {
            void onItemClick(String finalPayType, BankCardBean bankCardBean);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mListener = onItemClickListener;
        }

        private class PayTypeHolder extends RecyclerView.ViewHolder {

            private View mRootView;
            private TextView mTextView;
            private ImageView mCheckMark;

            public PayTypeHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mTextView = itemView.findViewById(R.id.textView);
                mCheckMark = itemView.findViewById(R.id.checkMark);
            }

            public void bindData(final BankCardBean bankCardBean) {
                String payType = "";
                switch (bankCardBean.getPayType()) {
                    case BankCardBean.PAYTYPE_ALIPAY:
                        mTextView.setText(R.string.alipay);
                        payType = PayType.ALIPAY;
                        break;
                    case BankCardBean.PAYTYPE_WX:
                        mTextView.setText(R.string.wechatpay);
                        payType = PayType.WXPAY;
                        break;
                    case BankCardBean.PAYTYPE_BANK:
                        mTextView.setText(bankCardBean.getBankName() + bankCardBean.getCardNumber());
                        payType = PayType.BANK_PAY;
                        break;
                    default:
                }
                boolean match = false;
                if (mPayInfo != null) {
                    for (Map.Entry<String, String> entry : mPayInfo.entrySet()) {
                        if (entry.getKey().equals(payType)) {
                            if (entry.getValue().equals(String.valueOf(bankCardBean.getId()))) {
                                match = true;
                            }
                        }
                    }
                }
                mCheckMark.setVisibility(match ? View.VISIBLE : View.GONE);
                final String finalPayType = payType;
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onItemClick(finalPayType, bankCardBean);
                        }
                    }
                });
            }
        }
    }
}
