package com.songbai.futurex.fragment.mine;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.mine.PropertyFlowActivity;
import com.songbai.futurex.fragment.mine.adapter.PropertyFlowAdapter;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.GetUserFinanceFlowData;
import com.songbai.futurex.model.mine.AccountList;
import com.songbai.futurex.model.mine.CoinAbleAmount;
import com.songbai.futurex.model.mine.CoinPropertyFlow;
import com.songbai.futurex.utils.Display;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.TitleBar;

import java.util.ArrayList;

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
    public static final int REQUEST_FUNDS_TRANSFER = 12533;
    public static final int REQUEST_RECHARGE_COIN = 12534;
    public static final int REQUEST_WITHDRAW_COIN = 12535;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.ableCoin)
    TextView mAbleCoin;
    @BindView(R.id.freezeCoin)
    TextView mFreezeCoin;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.transfer)
    FrameLayout mTransfer;
    @BindView(R.id.recharge)
    FrameLayout mRecharge;
    @BindView(R.id.withDraw)
    FrameLayout mWithDraw;
    @BindView(R.id.operateGroup)
    LinearLayout mOperateGroup;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    @BindView(R.id.history)
    TextView mHistory;
    private Unbinder mBind;
    private AccountList.AccountBean mAccountBean;
    private int mTransferType;
    private PropertyFlowAdapter mAdapter;
    private GetUserFinanceFlowData mGetUserFinanceFlowData;

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
        mTitleBar.setTitle(mAccountBean.getCoinType().toUpperCase());
        setAbleCoin(mAccountBean.getAbleCoin());
        SpannableStringBuilder freezeCoinStr = new SpannableStringBuilder(getString(R.string.freeze_amount_x,
                FinanceUtil.formatWithScale(mAccountBean.getFreezeCoin(), 8)));
        freezeCoinStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.text99)),
                0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mFreezeCoin.setText(freezeCoinStr);
        boolean isLegal = mAccountBean.getLegal() == AccountList.AccountBean.IS_LEGAL;
        mTransfer.setVisibility(isLegal ? View.VISIBLE : View.GONE);
        boolean canRecharge = mAccountBean.getRecharge() == AccountList.AccountBean.CAN_RECHARGE;
        mRecharge.setVisibility(canRecharge ? View.VISIBLE : View.GONE);
        boolean canDraw = mAccountBean.getIsCanDraw() == AccountList.AccountBean.CAN_DRAW;
        mWithDraw.setVisibility(canDraw ? View.VISIBLE : View.GONE);
        if (!isLegal && !canRecharge && !canDraw) {
            mOperateGroup.setVisibility(View.GONE);
        } else {
            mOperateGroup.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setNestedScrollingEnabled(false);
        mHistory.post(new Runnable() {
            @Override
            public void run() {
                Rect r = new Rect();
                mHistory.getGlobalVisibleRect(r);
                mEmptyView.setMinimumHeight((int) (Display.getScreenHeight() - r.bottom - Display.dp2Px(49, getResources())));
            }
        });
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PropertyFlowAdapter();
        mAdapter.setOnClickListener(new PropertyFlowAdapter.OnClickListener() {
            @Override
            public void onItemClick(int id) {
                UniqueActivity.launcher(CoinPropertyFragment.this, PropertyFlowDetailFragment.class)
                        .putExtra(ExtraKeys.PROPERTY_FLOW_ID, id)
                        .execute();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mGetUserFinanceFlowData = new GetUserFinanceFlowData();
        mGetUserFinanceFlowData.setCoinType(mAccountBean.getCoinType());
        getAccountByUserForMuti(mAccountBean.getCoinType());
        getUserFinanceFlow(mGetUserFinanceFlowData);
    }

    private void setAbleCoin(double ableCoin) {
        SpannableStringBuilder ableCoinStr = new SpannableStringBuilder(getString(R.string.available_amount_x,
                FinanceUtil.formatWithScale(ableCoin, 8)));
        ableCoinStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.text99)),
                0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAbleCoin.setText(ableCoinStr);
    }

    private void getAccountByUserForMuti(String coinType) {
        Apic.getAccountByUserForMuti(coinType).tag(TAG)
                .callback(new Callback<Resp<ArrayList<CoinAbleAmount>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<CoinAbleAmount>> resp) {
                        ArrayList<CoinAbleAmount> data = resp.getData();
                        if (data.size() > 0) {
                            CoinAbleAmount coinAbleAmount = data.get(0);
                            setAbleCoin(coinAbleAmount.getAbleCoin());
                        }
                    }
                })
                .fireFreely();
    }

    private void getUserFinanceFlow(GetUserFinanceFlowData getUserFinanceFlowData) {
        Apic.getUserFinanceFlow(getUserFinanceFlowData, 0, 5).tag(TAG)
                .callback(new Callback<Resp<PagingWrap<CoinPropertyFlow>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingWrap<CoinPropertyFlow>> resp) {
                        mAdapter.setList(resp.getData());
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.hideAll(false);
                    }
                })
                .fireFreely();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, false);
            if (shouldRefresh) {
                getUserFinanceFlow(mGetUserFinanceFlowData);
                getAccountByUserForMuti(mAccountBean.getCoinType());
                getActivity().setResult(COIN_PROPERTY_RESULT, new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
            }
        }
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
                        .putExtra(ExtraKeys.COIN_TYPE, mAccountBean.getCoinType()).execute();
                break;
            case R.id.transfer:
                if (mAccountBean.getLegal() == AccountList.AccountBean.IS_LEGAL) {
                    ArrayList<AccountList.AccountBean> accountBeans = new ArrayList<>();
                    accountBeans.add(mAccountBean);
                    UniqueActivity.launcher(getContext(), FundsTransferFragment.class)
                            .putExtra(ExtraKeys.TRANSFER_TYPE, mTransferType)
                            .putExtra(ExtraKeys.ACCOUNT_BEANS, accountBeans)
                            .execute(this, REQUEST_FUNDS_TRANSFER);
                }
                break;
            case R.id.recharge:
                if (mAccountBean.getRecharge() == AccountList.AccountBean.CAN_RECHARGE) {
                    UniqueActivity.launcher(getContext(), ReChargeCoinFragment.class)
                            .putExtra(ExtraKeys.COIN_TYPE, mAccountBean.getCoinType())
                            .execute(this, REQUEST_RECHARGE_COIN);
                }
                break;
            case R.id.withDraw:
                if (mAccountBean.getIsCanDraw() == AccountList.AccountBean.CAN_DRAW) {
                    UniqueActivity.launcher(getContext(), WithDrawCoinFragment.class)
                            .putExtra(ExtraKeys.ACCOUNT_BEAN, mAccountBean)
                            .execute(this, REQUEST_WITHDRAW_COIN);
                }
                break;
            default:
        }
    }
}
