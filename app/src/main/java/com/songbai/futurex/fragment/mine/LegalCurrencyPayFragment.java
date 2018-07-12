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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.BankCardBean;
import com.songbai.futurex.model.mine.BindBankList;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.view.EmptyRecyclerView;

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
    public static final int REQUEST_ADD_PAY = 12432;

    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.addGathering)
    FrameLayout mAddGathering;
    Unbinder unbinder;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    private LegalCurrencyPayAdapter mLegalCurrencyPayAdapter;
    private BindBankList mBindBankList;

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
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLegalCurrencyPayAdapter = new LegalCurrencyPayAdapter();
        mLegalCurrencyPayAdapter.setmOnItemClickListener(new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                BankCardBean bankCardBean = (BankCardBean) obj;
                if (view.getId() == R.id.delete) {
                    int id = bankCardBean.getId();
                    bindUntie(id);
                } else {
                    int payType = bankCardBean.getPayType();
                    if (payType != BankCardBean.PAYTYPE_BANK) {
                        UniqueActivity.launcher(LegalCurrencyPayFragment.this, AddPayFragment.class)
                                .putExtra(ExtraKeys.IS_ALIPAY, payType == BankCardBean.PAYTYPE_ALIPAY)
                                .putExtra(ExtraKeys.BIND_BANK_LIST, mBindBankList)
                                .execute(LegalCurrencyPayFragment.this, REQUEST_ADD_PAY);
                    }
                }
            }
        });
        mRecyclerView.setAdapter(mLegalCurrencyPayAdapter);
        getBindListData();
    }

    private void getBindListData() {
        Apic.bindList(0).tag(TAG)
                .callback(new Callback<Resp<BindBankList>>() {
                    @Override
                    protected void onRespSuccess(Resp<BindBankList> resp) {
                        setBindBankList(resp.getData());
                    }
                })
                .fireFreely();
    }

    private void bindUntie(int id) {
        Apic.bindUntie(id).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        getBindListData();
                    }
                })
                .fireFreely();
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
        mRecyclerView.hideAll(false);
        mAddGathering.setVisibility(mLegalCurrencyPayAdapter.getItemCount() >= 7 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PAY || requestCode == REQUEST_ADD_PAY) {
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
        private static OnRVItemClickListener mOnItemClickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_legal_currency, parent, false);
            return new LegalCurrencyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof LegalCurrencyHolder) {
                ((LegalCurrencyHolder) holder).bindData(position, mList.get(position));
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

        void setmOnItemClickListener(OnRVItemClickListener onItemClickListener) {
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

            void bindData(final int position, final BankCardBean bankCardBean) {
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
                            mOnItemClickListener.onItemClick(mRootView, position, bankCardBean);
                        }
                    }
                });
                mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(mDelete, position, bankCardBean);
                        }
                    }
                });
            }
        }
    }
}
