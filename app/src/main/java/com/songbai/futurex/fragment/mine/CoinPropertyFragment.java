package com.songbai.futurex.fragment.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.mine.PropertyFlowActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingResp;
import com.songbai.futurex.model.local.GetUserFinanceFlowData;
import com.songbai.futurex.model.mine.AccountList;
import com.songbai.futurex.model.mine.CoinPropertyFlow;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/6/12
 */
public class CoinPropertyFragment extends UniqueActivity.UniFragment {
    public static final int COIN_PROPERTY_RESULT = 12532;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.ableCoin)
    TextView mAbleCoin;
    @BindView(R.id.freezeCoin)
    TextView mFreezeCoin;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private Unbinder mBind;
    private AccountList.AccountBean mAccountBean;
    private int mTransferType;
    private CoinPropertyAdapter mAdapter;
    private boolean modified = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coin_property, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mTransferType = extras.getInt(ExtraKeys.TRANSFER_TYPE);
        mAccountBean = extras.getParcelable(ExtraKeys.ACCOUNT_BEAN);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        getActivity().setResult(COIN_PROPERTY_RESULT, new Intent().putExtra(ExtraKeys.MODIFIDE_SHOULD_REFRESH, modified));

        mTitleBar.setBackClickListener(new TitleBar.OnBackClickListener() {
            @Override
            public void onClick() {
                FragmentActivity activity = CoinPropertyFragment.this.getActivity();
                activity.finish();
            }
        });
        mTitleBar.setTitle(mAccountBean.getCoinType().toUpperCase());
        SpannableStringBuilder ableCoinStr = new SpannableStringBuilder(getString(R.string.available_amount_x, mAccountBean.getAbleCoin()));
        ableCoinStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.text99)),
                0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder freezeCoinStr = new SpannableStringBuilder(getString(R.string.freeze_amount_x, mAccountBean.getFreezeCoin()));
        freezeCoinStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.text99)),
                0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAbleCoin.setText(ableCoinStr);
        mFreezeCoin.setText(freezeCoinStr);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CoinPropertyAdapter();
        mRecyclerView.setAdapter(mAdapter);

        GetUserFinanceFlowData getUserFinanceFlowData = new GetUserFinanceFlowData();
        getUserFinanceFlowData.setCoinType(mAccountBean.getCoinType());
        getUserFinanceFlowData.setFlowType(String.valueOf(0));
        getUserFinanceFlowData.setStartTime("");
        getUserFinanceFlowData.setEndTime("");
        getUserFinanceFlow(getUserFinanceFlowData);
    }

    private void getUserFinanceFlow(GetUserFinanceFlowData getUserFinanceFlowData) {
        Apic.getUserFinanceFlow(getUserFinanceFlowData, 0, 5)
                .callback(new Callback<PagingResp<CoinPropertyFlow>>() {
                    @Override
                    protected void onRespSuccess(PagingResp<CoinPropertyFlow> resp) {
                        mAdapter.setList(resp.getList());
                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.history, R.id.transfer, R.id.recharge, R.id.withDraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.history:
                Launcher.with(getContext(), PropertyFlowActivity.class)
                        .putExtra(ExtraKeys.PROPERTY_FLOW_FILTER_TYPE_ALL, false)
                        .putExtra(ExtraKeys.COIN_TYPE,mAccountBean.getCoinType()).execute();
                break;
            case R.id.transfer:
                if (mAccountBean.getLegal() == AccountList.AccountBean.IS_LEGAL) {
                    ArrayList<AccountList.AccountBean> accountBeans = new ArrayList<>();
                    accountBeans.add(mAccountBean);
                    UniqueActivity.launcher(getContext(), FundsTransferFragment.class)
                            .putExtra(ExtraKeys.TRANSFER_TYPE, mTransferType)
                            .putExtra(ExtraKeys.ACCOUNT_BEANS, accountBeans)
                            .execute();
                }
                break;
            case R.id.recharge:
                if (mAccountBean.getRecharge() == AccountList.AccountBean.CAN_RECHAREGE) {
                    UniqueActivity.launcher(getContext(), ReChargeCoinFragment.class)
                            .putExtra(ExtraKeys.COIN_TYPE, mAccountBean.getCoinType())
                            .execute();
                }
                break;
            case R.id.withDraw:
                if (mAccountBean.getIsCanDraw() == AccountList.AccountBean.CAN_DRAW) {
                    UniqueActivity.launcher(getContext(), DrawCoinFragment.class)
                            .putExtra(ExtraKeys.ACCOUNT_BEAN, mAccountBean)
                            .execute();
                }
                break;
            default:
        }
    }

    private class CoinPropertyAdapter extends RecyclerView.Adapter {

        private List<CoinPropertyFlow> mList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_coin_property, parent, false);
            return new CoinPropertyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(List<CoinPropertyFlow> list) {
            mList.clear();
            mList.addAll(list);
        }
    }

    class CoinPropertyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.type)
        TextView mType;
        @BindView(R.id.amount)
        TextView mAmount;
        @BindView(R.id.status)
        TextView mStatus;
        @BindView(R.id.timestamp)
        TextView mTimestamp;

        CoinPropertyHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindData() {
        }
    }
}
