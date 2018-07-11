package com.songbai.futurex.fragment.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.mine.MyPropertyActivity;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.AccountList;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.EmptyRecyclerView;

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
    private List<AccountList.AccountBean> mAccountBeans;

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

    private void filterData(String keyWord) {
        if (mAccountBeans == null) {
            return;
        }
        ArrayList<AccountList.AccountBean> filteredData = new ArrayList<>();
        for (AccountList.AccountBean accountBean : mAccountBeans) {
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
                if (selected) {
                    filterData(mSearchProperty.getText().toString());
                } else {
                    mAdapter.setList(mAccountBeans);
                    mAdapter.notifyDataSetChanged();
                }
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

    private void userAccount() {
        Apic.userAccount().tag(TAG)
                .callback(new Callback<Resp<AccountList>>() {

                    @Override
                    protected void onRespSuccess(Resp<AccountList> resp) {
                        setAdapter(2, resp.getData());
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
        } else if (mPropertyType == 2) {
            userAccount();
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

    class PropertyListAdapter extends RecyclerView.Adapter {
        private List<AccountList.AccountBean> mList;
        private Context mContext;
        private int mType;

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
                ((PropertyListHolder) holder).bindData(mType, mContext, mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (mList == null) {
                return 0;
            }
            return mList.size();
        }

        public void setList(List<AccountList.AccountBean> list) {
            mList = list;
        }

        public void setType(int type) {
            mType = type;
        }

        class PropertyListHolder extends RecyclerView.ViewHolder {
            private final View mRootView;
            @BindView(R.id.coinType)
            TextView mCoinType;
            @BindView(R.id.availableAmount)
            TextView mAvailableAmount;
            @BindView(R.id.freezeAmount)
            TextView mFreezeAmount;
            @BindView(R.id.transfer)
            TextView mTransfer;

            PropertyListHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mRootView = itemView;
            }

            private void bindData(int type, final Context context, final AccountList.AccountBean accountBean) {
                mCoinType.setText(accountBean.getCoinType().toUpperCase());
                double ableCoin = accountBean.getAbleCoin();
                String formattedAbleCoin = FinanceUtil.formatWithScale(ableCoin, 8);
                SpannableStringBuilder ableCoinStr = new SpannableStringBuilder(context.getString(R.string.amount_available_x, formattedAbleCoin));
                ableCoinStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text22)),
                        0, formattedAbleCoin.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ableCoinStr.setSpan(new AbsoluteSizeSpan(16, true),
                        0, formattedAbleCoin.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mAvailableAmount.setText(ableCoinStr);
                double freezeCoin = accountBean.getFreezeCoin();
                String formattedFreeze = FinanceUtil.formatWithScale(freezeCoin, 8);
                SpannableStringBuilder freezeCoinStr = new SpannableStringBuilder(context.getString(R.string.amount_freeze_x, formattedFreeze));
                freezeCoinStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text22)),
                        0, formattedFreeze.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                freezeCoinStr.setSpan(new AbsoluteSizeSpan(16, true),
                        0, formattedFreeze.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mFreezeAmount.setText(freezeCoinStr);
                mTransfer.setVisibility(type < 2 ? View.GONE : View.VISIBLE);
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UniqueActivity.launcher(PropertyListFragment.this, CoinPropertyFragment.class)
                                .putExtra(ExtraKeys.ACCOUNT_BEAN, accountBean)
                                .execute(PropertyListFragment.this, PropertyListFragment.REQUEST_COIN_PROPERTY);
                    }
                });
            }
        }
    }
}
