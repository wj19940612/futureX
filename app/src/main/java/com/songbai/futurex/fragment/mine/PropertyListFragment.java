package com.songbai.futurex.fragment.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.MainActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.mine.MyPropertyActivity;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.AccountBean;
import com.songbai.futurex.model.mine.AccountList;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.SmartDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/31
 */
public class PropertyListFragment extends BaseFragment {
    private static final String PROPERTY_TYPE = "property_type";
    private static final int REQUEST_COIN_PROPERTY = 12322;

    @BindView(R.id.searchProperty)
    EditText mSearchProperty;
    @BindView(R.id.hideZero)
    LinearLayout mHideZero;
    @BindView(R.id.check)
    ImageView mCheck;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    private Unbinder mBind;
    private int mPropertyType;
    private PropertyListAdapter mAdapter;
    private List<AccountBean> mAccountBeans;
    private SmartDialog mSmartDialog;
    private double mPrice;

    public static PropertyListFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(PROPERTY_TYPE, position);
        PropertyListFragment messageFragment = new PropertyListFragment();
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPropertyType = bundle.getInt(PROPERTY_TYPE, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_property_list, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PropertyListAdapter();
        mRecyclerView.setEmptyView(mEmptyView);
        mAdapter.setPrice(mPrice);
        mAdapter.setOnRVItemClickListener(new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                AccountBean accountBean = (AccountBean) obj;
                if (view.getId() == R.id.trade) {
                    Launcher.with(getContext(), MainActivity.class)
                            .putExtra(ExtraKeys.PAGE_INDEX, MainActivity.PAGE_LEGAL_CURRENCY)
                            .execute();
                } else {
                    UniqueActivity.launcher(PropertyListFragment.this, CoinPropertyFragment.class)
                            .putExtra(ExtraKeys.ACCOUNT_BEAN, accountBean)
                            .putExtra(ExtraKeys.PROPERTY_FLOW_ACCOUNT_TYPE, mPropertyType)
                            .execute(PropertyListFragment.this, PropertyListFragment.REQUEST_COIN_PROPERTY);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        requestData();
        mSearchProperty.addTextChangedListener(new ValidationWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String keyWord = s.toString();
                filterData(keyWord);
            }
        });
    }

    private void showTransferDialog(final AccountBean accountBean) {
        TransferController transferController = new TransferController(getContext(), new TransferController.OnClickListener() {
            @Override
            public void onConfirmClick() {
                userTransfer(accountBean.getCoinType());
            }
        });
        mSmartDialog = SmartDialog.solo(getActivity());
        mSmartDialog
                .setWidthScale(0.8f)
                .setWindowGravity(Gravity.CENTER)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(transferController)
                .show();
    }

    private void userTransfer(String coins) {
        Apic.userTransfer(coins).tag(TAG).callback(new Callback<Resp>() {
            @Override
            protected void onRespSuccess(Resp resp) {
                ToastUtil.show(R.string.transfer_success);
                if (mSmartDialog != null) {
                    mSmartDialog.dismiss();
                }
            }
        }).fire();
    }

    private void filterData(String keyWord) {
        if (mAccountBeans == null) {
            return;
        }
        ArrayList<AccountBean> filteredData = new ArrayList<>();
        for (AccountBean accountBean : mAccountBeans) {
            if ((TextUtils.isEmpty(keyWord) || accountBean.getCoinType().toUpperCase().contains(keyWord.toUpperCase()))) {
                if ((mHideZero.isSelected())) {
                    if (accountBean.getEstimateBtc() != 0 || accountBean.getAbleCoin() != 0 || accountBean.getFreezeCoin() != 0) {
                        filteredData.add(accountBean);
                    }
                } else {
                    filteredData.add(accountBean);
                }
            }
        }
        mAdapter.setList(filteredData);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.hideZero})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.hideZero:
                mHideZero.setSelected(!mHideZero.isSelected());
                boolean selected = mHideZero.isSelected();
                mCheck.setImageResource(selected ? R.drawable.ic_common_checkmark : 0);
                filterData(mSearchProperty.getText().toString());
                break;
            default:
        }
    }

    private void getAccountByUser() {
        Apic.getAccountByUser("").tag(TAG)
                .callback(new Callback<Resp<AccountList>>() {
                    @Override
                    protected void onRespSuccess(Resp<AccountList> resp) {
                        setAdapter(0, resp.getData());
                    }
                })
                .fireFreely();
    }

    private void accountList() {
        Apic.otcAccountList().tag(TAG)
                .callback(new Callback<Resp<AccountList>>() {
                    @Override
                    protected void onRespSuccess(Resp<AccountList> resp) {
                        setAdapter(1, resp.getData());
                    }
                })
                .fireFreely();
    }

    private void setAdapter(int position, AccountList accountList) {
        ((MyPropertyActivity) getActivity()).setAccountAmount(position, accountList);
        mAccountBeans = accountList.getAccount();
        if (mAccountBeans != null) {
            mAdapter.setList(mAccountBeans);
        }
        mAdapter.setType(position);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.hideAll(false);
    }

    public void requestData() {
        if (mPropertyType == 0) {
            getAccountByUser();
        }
        if (mPropertyType == 1) {
            accountList();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COIN_PROPERTY) {
            if (data != null) {
                boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, false);
                if (shouldRefresh) {
                    requestData();
                }
            }
        }
    }

    public void setPrice(double price) {
        mPrice = price;
        if (mAdapter != null) {
            mAdapter.setPrice(price);
            mAdapter.notifyDataSetChanged();
        }
    }

    static class TransferController extends SmartDialog.CustomViewController {
        @BindView(R.id.close)
        ImageView mClose;
        @BindView(R.id.confirm)
        TextView mConfirm;
        private Context mContext;
        private TransferController.OnClickListener mOnClickListener;

        public interface OnClickListener {
            void onConfirmClick();
        }

        public TransferController(Context context, TransferController.OnClickListener onClickListener) {
            mContext = context;
            mOnClickListener = onClickListener;
        }

        @Override
        protected View onCreateView() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_transfer, null);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        protected void onInitView(View view, final SmartDialog dialog) {
            mClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onConfirmClick();
                    }
                }
            });
        }
    }

    class PropertyListAdapter extends RecyclerView.Adapter {

        private OnRVItemClickListener mOnRVItemClickListener;
        private List<AccountBean> mList;
        private Context mContext;
        private int mType;
        private double mPrice;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_my_property, parent, false);
            return new PropertyListHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PropertyListHolder) {
                ((PropertyListHolder) holder).bindData(mContext, position, mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (mList == null) {
                return 0;
            }
            return mList.size();
        }

        public void setList(List<AccountBean> list) {
            mList = list;
        }

        public void setOnRVItemClickListener(OnRVItemClickListener onRVItemClickListener) {
            mOnRVItemClickListener = onRVItemClickListener;
        }

        public void setType(int type) {
            mType = type;
        }

        public void setPrice(double price) {
            mPrice = price;
        }

        class PropertyListHolder extends RecyclerView.ViewHolder {
            private final View mRootView;
            @BindView(R.id.coinType)
            TextView mCoinType;
            @BindView(R.id.availableAmount)
            TextView mAvailableAmount;
            @BindView(R.id.freezeAmount)
            TextView mFreezeAmount;
            @BindView(R.id.trade)
            TextView mTrade;
            @BindView(R.id.equivalentText)
            TextView mEquivalentText;
            @BindView(R.id.equivalent)
            TextView mEquivalent;

            PropertyListHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mRootView = itemView;
            }

            private void bindData(Context context, final int position, final AccountBean accountBean) {
                mTrade.setVisibility(accountBean.getLegal() == 1 && !Preference.get().getCloseOTC() ? View.VISIBLE : View.GONE);
                mCoinType.setText(accountBean.getCoinType().toUpperCase());
                mAvailableAmount.setText(FinanceUtil.formatWithScale(accountBean.getAbleCoin(), 8));
                mFreezeAmount.setText(FinanceUtil.formatWithScale(accountBean.getFreezeCoin(), 8));
                mEquivalentText.setText(context.getString(R.string.equivalent_x, Preference.get().getPricingMethod().toUpperCase()));
                mEquivalent.setText(FinanceUtil.formatWithScale(accountBean.getEstimateBtc() * mPrice));
                mTrade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnRVItemClickListener != null) {
                            mOnRVItemClickListener.onItemClick(mTrade, position, accountBean);
                        }
                    }
                });
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnRVItemClickListener != null) {
                            mOnRVItemClickListener.onItemClick(mRootView, position, accountBean);
                        }
                    }
                });
            }
        }
    }
}
