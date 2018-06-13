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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.sbai.httplib.ReqError;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.mine.MyPropertyActivity;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.AccountList;
import com.songbai.futurex.utils.ValidationWatcher;

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
    TextView mHideZero;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
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
        mRecyclerView.setAdapter(mAdapter);
        requestData();
        mSearchProperty.addTextChangedListener(new ValidationWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String keyWord = s.toString();
                filterDataByWord(keyWord);
            }
        });
    }

    private void filterDataByWord(String keyWord) {
        ArrayList<AccountList.AccountBean> filteredData = new ArrayList<>();
        for (AccountList.AccountBean accountBean : mAccountBeans) {
            if (TextUtils.isEmpty(keyWord) || accountBean.getCoinType().toUpperCase().contains(keyWord.toUpperCase())) {
                filteredData.add(accountBean);
            }
        }
        mAdapter.setList(filteredData);
        mAdapter.notifyDataSetChanged();
    }

    private void filterZeroData() {
        ArrayList<AccountList.AccountBean> filteredData = new ArrayList<>();
        for (AccountList.AccountBean accountBean : mAccountBeans) {
            if (Double.valueOf(accountBean.getEstimateBtc()) != 0) {
                filteredData.add(accountBean);
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
                filterZeroData();
                break;
            default:
        }
    }

    private void getAccountByUser() {
        Apic.getAccountByUser("")
                .callback(new Callback<Resp<AccountList>>() {
                    @Override
                    protected void onRespSuccess(Resp<AccountList> resp) {
                        setAdapter(0, resp.getData());
                    }
                })
                .fire();
    }

    private void accountList() {
        Apic.otcAccountList()
                .callback(new Callback<Resp<AccountList>>() {
                    @Override
                    protected void onRespSuccess(Resp<AccountList> resp) {
                        setAdapter(1, resp.getData());
                    }
                })
                .fire();
    }

    private void userAccount() {
        Apic.userAccount()
                .callback(new Callback<Resp<AccountList>>() {

                    @Override
                    public void onFailure(ReqError reqError) {

                    }

                    @Override
                    protected void onRespSuccess(Resp<AccountList> resp) {
                        setAdapter(2, resp.getData());
                    }
                })
                .fire();
    }

    private void setAdapter(int position, AccountList accountList) {
        ((MyPropertyActivity) getActivity()).setAccountAmount(position, accountList);
        mAccountBeans = accountList.getAccount();
        mAdapter.setList(mAccountBeans);
        mAdapter.setType(position);
        mAdapter.notifyDataSetChanged();
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
        Log.e("wtf","aaaaa");
        if (requestCode == REQUEST_COIN_PROPERTY) {
            Log.e("wtf","aa");
            if (data != null) {
                Log.e("tag","bb");
                boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIDE_SHOULD_REFRESH, false);
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
                String ableCoin = accountBean.getAbleCoin();
                SpannableStringBuilder ableCoinStr = new SpannableStringBuilder(context.getString(R.string.amount_available_x, ableCoin));
                if (!TextUtils.isEmpty(ableCoin)) {
                    ableCoinStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text22)),
                            0, ableCoin.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ableCoinStr.setSpan(new AbsoluteSizeSpan(16, true),
                            0, ableCoin.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                mAvailableAmount.setText(ableCoinStr);
                String freezeCoin = accountBean.getFreezeCoin();
                SpannableStringBuilder freezeCoinStr = new SpannableStringBuilder(context.getString(R.string.amount_freeze_x, freezeCoin));
                if (!TextUtils.isEmpty(freezeCoin)) {
                    freezeCoinStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text22)),
                            0, freezeCoin.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    freezeCoinStr.setSpan(new AbsoluteSizeSpan(16, true),
                            0, freezeCoin.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                mFreezeAmount.setText(freezeCoinStr);
                mTransfer.setVisibility(type < 2 ? View.GONE : View.VISIBLE);
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UniqueActivity.launcher(PropertyListFragment.this, CoinPropertyFragment.class).putExtra(ExtraKeys.ACCOUNT_BEAN, accountBean).execute(PropertyListFragment.REQUEST_COIN_PROPERTY);
                    }
                });
            }
        }
    }
}
