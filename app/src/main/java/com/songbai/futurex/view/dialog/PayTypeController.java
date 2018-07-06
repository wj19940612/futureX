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
import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class PayTypeController extends SmartDialog.CustomViewController {
    private Context mContext;
    private ArrayList<String> mPayInfos = new ArrayList<>();
    private String mBankId;
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
                    StringBuilder sb = new StringBuilder();
                    for (String payInfo : mPayInfos) {
                        sb.append(payInfo);
                        sb.append(",");
                    }
                    mOnItemClickListener.onConfirmClick(sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "", mBankId);
                    dialog.dismiss();
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        final PayTypeAdapter adapter = new PayTypeAdapter();
        adapter.setPayInfo(mPayInfos);
        adapter.setSelectedBankId(mBankId);
        adapter.setList(mBankList);
        adapter.setOnItemClickListener(new PayTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String payType, BankCardBean bankCardBean) {
                int id = bankCardBean.getId();
                if (mPayInfos.contains(payType)) {
                    mPayInfos.remove(payType);
                    if (String.valueOf(id).equals(mBankId)) {
                        mBankId = "";
                    }
                } else {
                    mPayInfos.add(payType);
                    if (PayType.BANK_PAY.equals(payType)) {
                        mBankId = String.valueOf(id);
                    }
                }
                adapter.setPayInfo(mPayInfos);
                adapter.setSelectedBankId(mBankId);
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void setPayInfo(ArrayList<String> payInfo) {
        mPayInfos.clear();
        mPayInfos.addAll(payInfo);
    }

    public void setBankList(BindBankList bankList) {
        mBankList = bankList;
//        ArrayList<BankCardBean> list = new ArrayList<>();
//        BankCardBean aliPay = bankList.getAliPay();
//        if (aliPay.getBind() == BankCardBean.ALIPAY_WECHATPAY_BIND) {
//            list.add(aliPay);
//        }
//        BankCardBean wechat = bankList.getWechat();
//        if (wechat.getBind() == BankCardBean.ALIPAY_WECHATPAY_BIND) {
//            list.add(wechat);
//        }
//        List<BankCardBean> bankCard = bankList.getBankCard();
//        for (BankCardBean bankCardBean : bankCard) {
//            list.add(bankCardBean);
//        }
//        for (BankCardBean bankCardBean : list) {
//            String payTyp = "";
//            switch (bankCardBean.getPayType()) {
//                case BankCardBean.PAYTYPE_ALIPAY:
//                    payType = PayType.ALIPAY;
//                    break;
//                case BankCardBean.PAYTYPE_WX:
//                    payType = PayType.WXPAY;
//                    break;
//                case BankCardBean.PAYTYPE_BANK:
//                    payType = PayType.BANK_PAY;
//                    break;
//                default:
//            }
//            if (mPayInfos.contains(payType)) {
//
//            }
//        }
    }

    public void setSelectedBankId(String selectedBankId) {
        mBankId = selectedBankId;
    }

    public interface OnItemClickListener {
        void onConfirmClick(String payInfo, String id);
    }

    public PayTypeController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }

    static class PayTypeAdapter extends RecyclerView.Adapter {
        private OnItemClickListener mListener;
        ArrayList<BankCardBean> mList = new ArrayList<>();
        private ArrayList<String> mPayInfo;
        private String mSelectedBankId;

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
            if (bindBankList == null) {
                return;
            }
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

        public void setPayInfo(ArrayList<String> payInfo) {
            mPayInfo = payInfo;
        }

        public void setSelectedBankId(String selectedBankId) {
            mSelectedBankId = selectedBankId;
        }

        public interface OnItemClickListener {
            void onItemClick(String finalPayType, BankCardBean bankCardBean);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mListener = onItemClickListener;
        }

        private class PayTypeHolder extends RecyclerView.ViewHolder {

            private View mRootView;
            private TextView mBankName;
            private TextView mCardNum;
            private ImageView mBankIcon;
            private ImageView mCheckMark;

            public PayTypeHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mBankName = itemView.findViewById(R.id.bankName);
                mCardNum = itemView.findViewById(R.id.cardNum);
                mBankIcon = itemView.findViewById(R.id.bankIcon);
                mCheckMark = itemView.findViewById(R.id.checkMark);
            }

            public void bindData(final BankCardBean bankCardBean) {
                String payType = "";
                boolean match = false;
                switch (bankCardBean.getPayType()) {
                    case BankCardBean.PAYTYPE_ALIPAY:
                        mBankIcon.setImageResource(R.drawable.ic_pay_alipay_s);
                        mBankName.setText(R.string.alipay);
                        payType = PayType.ALIPAY;
                        break;
                    case BankCardBean.PAYTYPE_WX:
                        mBankIcon.setImageResource(R.drawable.ic_pay_wechat_s);
                        mBankName.setText(R.string.wechatpay);
                        payType = PayType.WXPAY;
                        break;
                    case BankCardBean.PAYTYPE_BANK:
                        mBankIcon.setImageResource(R.drawable.ic_pay_unionpay_s);
                        mBankName.setText(bankCardBean.getBankName());
                        payType = PayType.BANK_PAY;
                        break;
                    default:
                }
                mCardNum.setText(bankCardBean.getCardNumber());
                if (mPayInfo != null) {
                    for (String s : mPayInfo) {
                        if (s.equals(payType)) {
                            match = true;
                            if (payType.equals(PayType.BANK_PAY)) {
                                match = String.valueOf(bankCardBean.getId()).equals(mSelectedBankId);
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
