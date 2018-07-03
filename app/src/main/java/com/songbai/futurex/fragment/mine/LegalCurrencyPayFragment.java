package com.songbai.futurex.fragment.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.BankCardBean;
import com.songbai.futurex.model.mine.BindBankList;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.WithDrawPsdViewController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/6/4
 */
public class LegalCurrencyPayFragment extends UniqueActivity.UniFragment {
    public static final int REQUEST_SELECT_PAY = 16524;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.addGathering)
    FrameLayout mAddGathering;
    Unbinder unbinder;
    private LegalCurrencyPayAdapter mLegalCurrencyPayAdapter;
    private BindBankList mBindBankList;
    private SmartDialog mSmartDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_legal_currency_pay, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLegalCurrencyPayAdapter = new LegalCurrencyPayAdapter();
        mLegalCurrencyPayAdapter.setmOnItemClickListener(new LegalCurrencyPayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {

            }

            @Override
            public void onDeleteClick(BankCardBean bankCardBean) {
                int id = bankCardBean.getId();
                showDrawPass(id);
            }
        });
        mRecyclerView.setAdapter(mLegalCurrencyPayAdapter);
        getBindListData();
    }

    private void showDrawPass(final int id) {
        WithDrawPsdViewController withDrawPsdViewController = new WithDrawPsdViewController(getActivity(), new WithDrawPsdViewController.OnClickListener() {
            @Override
            public void onConfirmClick(String cashPwd,String googleAuth) {
                bindUntie(id, md5Encrypt(cashPwd));
            }
        });

        mSmartDialog = SmartDialog.solo(getActivity());
        mSmartDialog.setCustomViewController(withDrawPsdViewController)
                .show();
    }

    private void getBindListData() {
        Apic.bindList(0)
                .callback(new Callback<Resp<BindBankList>>() {
                    @Override
                    protected void onRespSuccess(Resp<BindBankList> resp) {
                        setBindBankList(resp.getData());
                    }
                })
                .fire();
    }

    private void bindUntie(int id, String withDrawPass) {
        Apic.bindUntie(id, withDrawPass)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        mSmartDialog.dismiss();
                        getBindListData();
                    }
                })
                .fire();
    }

    private void setBindBankList(BindBankList bindBankList) {
        mBindBankList = bindBankList;
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
        mLegalCurrencyPayAdapter.setList(list);
        mLegalCurrencyPayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PAY) {
            if (data != null) {
                boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, false);
                if (shouldRefresh) {
                    getBindListData();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.addGathering)
    public void onViewClicked() {
        UniqueActivity.launcher(this, SelectPayTypeFragment.class)
                .putExtra(ExtraKeys.BIND_BANK_LIST, mBindBankList)
                .execute(this, REQUEST_SELECT_PAY);
    }

    static class LegalCurrencyPayAdapter extends RecyclerView.Adapter {

        private ArrayList<BankCardBean> mList = new ArrayList<>();
        private static OnItemClickListener mOnItemClickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_legal_currency, parent, false);
            return new LegalCurrencyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof LegalCurrencyHolder) {
                ((LegalCurrencyHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(ArrayList<BankCardBean> list) {
            mList.clear();
            mList.addAll(list);
        }

        interface OnItemClickListener {
            void onItemClick();

            void onDeleteClick(BankCardBean bankCardBean);
        }

        void setmOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        static class LegalCurrencyHolder extends RecyclerView.ViewHolder {
            private final View mRootView;
            @BindView(R.id.icon)
            ImageView mIcon;
            @BindView(R.id.accountName)
            TextView mAccountName;
            @BindView(R.id.account)
            TextView mAccount;
            @BindView(R.id.delete)
            TextView mDelete;

            LegalCurrencyHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                mRootView = view;
            }

            void bindData(final BankCardBean bankCardBean) {
                switch (bankCardBean.getPayType()) {
                    case BankCardBean.PAYTYPE_ALIPAY:
                        mIcon.setImageResource(R.drawable.ic_legaltender_icon_alipay);
                        mAccountName.setText(R.string.alipay);
                        mAccount.setText(bankCardBean.getCardNumber());
                        break;
                    case BankCardBean.PAYTYPE_WX:
                        mIcon.setImageResource(R.drawable.ic_legaltender_icon_wechatpay);
                        mAccountName.setText(R.string.wechatpay);
                        mAccount.setText(bankCardBean.getCardNumber());
                        break;
                    case BankCardBean.PAYTYPE_BANK:
                        mIcon.setImageResource(R.drawable.ic_legaltender_icon_bankcard);
                        mAccountName.setText(bankCardBean.getBankName());
                        mAccount.setText(bankCardBean.getCardNumber());
                        break;
                    default:
                }
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick();
                        }
                    }
                });
                mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onDeleteClick(bankCardBean);
                        }
                    }
                });
            }
        }
    }
}
