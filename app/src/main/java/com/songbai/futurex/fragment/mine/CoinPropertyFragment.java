package com.songbai.futurex.fragment.mine;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.MainActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.mine.PropertyFlowActivity;
import com.songbai.futurex.fragment.mine.adapter.PropertyFlowAdapter;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.PairsMoney;
import com.songbai.futurex.model.local.GetUserFinanceFlowData;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.AccountBean;
import com.songbai.futurex.model.mine.CoinAccountBalance;
import com.songbai.futurex.model.mine.CoinPropertyFlow;
import com.songbai.futurex.utils.Display;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.PairMoneyUtil;
import com.songbai.futurex.utils.UmengCountEventId;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.MsgHintController;

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
    @BindView(R.id.equivalent)
    TextView mEquivalent;
    @BindView(R.id.equivalentText)
    TextView mEquivalentText;
    private Unbinder mBind;
    private AccountBean mAccountBean;
    private int mTransferType;
    private PropertyFlowAdapter mAdapter;
    private GetUserFinanceFlowData mGetUserFinanceFlowData;
    private int mAccountType;
    private double mPrice;

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
        mAccountType = extras.getInt(ExtraKeys.PROPERTY_FLOW_ACCOUNT_TYPE, 0);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mTitleBar.setTitle(mAccountBean.getCoinType().toUpperCase());
        setCoinAccountInfo(mAccountBean);
        boolean isLegal = mAccountBean.getLegal() == AccountBean.IS_LEGAL;
        mTransfer.setVisibility(isLegal ? View.VISIBLE : View.GONE);
        boolean canRecharge = mAccountBean.getRecharge() == AccountBean.CAN_RECHARGE;
        mRecharge.setVisibility(canRecharge ? View.VISIBLE : View.GONE);
        boolean canDraw = mAccountBean.getIsCanDraw() == AccountBean.CAN_DRAW;
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
                mEmptyView.setMinimumHeight((int) (Display.getScreenHeight() - r.bottom - Display.dp2Px(49, getResources())));
            }
        });
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PropertyFlowAdapter();
        mAdapter.setSingleType(true);
        mAdapter.setOnClickListener(new PropertyFlowAdapter.OnClickListener() {
            @Override
            public void onItemClick(String id) {
                UniqueActivity.launcher(CoinPropertyFragment.this, PropertyFlowDetailFragment.class)
                        .putExtra(ExtraKeys.PROPERTY_FLOW_ID, id)
                        .putExtra(ExtraKeys.PROPERTY_FLOW_ACCOUNT_TYPE, mAccountType)
                        .execute();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mGetUserFinanceFlowData = new GetUserFinanceFlowData();
        mGetUserFinanceFlowData.setCoinType(mAccountBean.getCoinType());
        mTitleBar.setRightVisible(mAccountBean.getLegal() == 1 && !Preference.get().getCloseOTC());
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getContext(), MainActivity.class)
                        .putExtra(ExtraKeys.PAGE_INDEX, MainActivity.PAGE_LEGAL_CURRENCY)
                        .execute();
            }
        });
        Apic.pairsMoney().tag(TAG).callback(new Callback<Resp<PairsMoney>>() {
            @Override
            protected void onRespSuccess(Resp<PairsMoney> resp) {
                PairsMoney data = resp.getData();
                mPrice = PairMoneyUtil.getCoinPrice("btc", data);
                setCoinAccountInfo(mAccountBean);
            }
        }).fire();
        getCoinBalance();
        getFlow();
    }

    private void getCoinBalance() {
        if (mAccountType == 0) {
            getAccountByUser(mAccountBean.getCoinType());
        } else {
            otcAccountList(mAccountBean.getCoinType());
        }
    }

    private void getFlow() {
        if (mAccountType == 0) {
            getUserFinanceFlow(mGetUserFinanceFlowData);
        } else {
            otcAccountDetail(mGetUserFinanceFlowData);
        }
    }

    private void setCoinAccountInfo(AccountBean accountBean) {
        mAbleCoin.setText(FinanceUtil.formatWithScale(accountBean.getAbleCoin(), 8));
        mFreezeCoin.setText(FinanceUtil.formatWithScale(accountBean.getFreezeCoin(), 8));
        mEquivalentText.setText(getString(R.string.equivalent_x, Preference.get().getPricingMethod().toUpperCase()));
        mEquivalent.setText(FinanceUtil.formatWithScale(accountBean.getEstimateBtc() * mPrice));
    }

    private void getAccountByUser(String coinType) {
        if (TextUtils.isEmpty(coinType)) {
            return;
        }
        Apic.getAccountByUser(coinType).tag(TAG)
                .callback(new Callback<Resp<CoinAccountBalance>>() {
                    @Override
                    protected void onRespSuccess(Resp<CoinAccountBalance> resp) {
                        CoinAccountBalance data = resp.getData();
                        List<AccountBean> account = data.getAccount();
                        if (account.size() > 0) {
                            mAccountBean = account.get(0);
                            setCoinAccountInfo(mAccountBean);
                        }
                    }
                })
                .fireFreely();
    }

    private void otcAccountList(String coinType) {
        if (TextUtils.isEmpty(coinType)) {
            return;
        }
        Apic.otcAccountList(coinType).tag(TAG)
                .callback(new Callback<Resp<CoinAccountBalance>>() {
                    @Override
                    protected void onRespSuccess(Resp<CoinAccountBalance> resp) {
                        CoinAccountBalance data = resp.getData();
                        List<AccountBean> account = data.getAccount();
                        if (account.size() > 0) {
                            mAccountBean = account.get(0);
                            setCoinAccountInfo(mAccountBean);
                        }
                    }
                })
                .fireFreely();
    }

    private void getUserFinanceFlow(GetUserFinanceFlowData getUserFinanceFlowData) {
        Apic.getUserFinanceFlow(getUserFinanceFlowData, 0, 5, "").tag(TAG)
                .callback(new Callback<Resp<PagingWrap<CoinPropertyFlow>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingWrap<CoinPropertyFlow>> resp) {
                        PagingWrap<CoinPropertyFlow> pagingWrap = resp.getData();
                        mAdapter.setList(pagingWrap);
                        mAdapter.setAccount(mAccountType);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.hideAll(false);
                    }
                })
                .fireFreely();
    }

    private void otcAccountDetail(GetUserFinanceFlowData getUserFinanceFlowData) {
        Apic.otcAccountDetail(getUserFinanceFlowData, 0, 5).tag(TAG)
                .callback(new Callback<Resp<PagingWrap<CoinPropertyFlow>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingWrap<CoinPropertyFlow>> resp) {
                        mAdapter.setList(resp.getData());
                        mAdapter.setAccount(mAccountType);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.hideAll(false);
                    }
                })
                .fire();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, false);
            if (shouldRefresh) {
                getFlow();
                getCoinBalance();
                setResult(COIN_PROPERTY_RESULT, new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
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
                        .putExtra(ExtraKeys.PROPERTY_FLOW_ACCOUNT_TYPE, mAccountType)
                        .putExtra(ExtraKeys.COIN_TYPE, mAccountBean.getCoinType()).execute();
                break;
            case R.id.transfer:
                umengEventCount(UmengCountEventId.COINACT0001);
                if (mAccountBean.getLegal() == AccountBean.IS_LEGAL) {
                    ArrayList<AccountBean> accountBeans = new ArrayList<>();
                    accountBeans.add(mAccountBean);
                    UniqueActivity.launcher(this, FundsTransferFragment.class)
                            .putExtra(ExtraKeys.TRANSFER_TYPE, mTransferType)
                            .execute(this, REQUEST_FUNDS_TRANSFER);
                }
                break;
            case R.id.recharge:
                umengEventCount(UmengCountEventId.COINACT0002);
                if (mAccountBean.getRecharge() == AccountBean.CAN_RECHARGE) {
                    UniqueActivity.launcher(this, ReChargeCoinFragment.class)
                            .putExtra(ExtraKeys.COIN_TYPE, mAccountBean.getCoinType())
                            .execute(this, REQUEST_RECHARGE_COIN);
                }
                break;
            case R.id.withDraw:
                umengEventCount(UmengCountEventId.COINACT0003);
                if (mAccountBean.getIsCanDraw() == AccountBean.CAN_DRAW) {
                    if (LocalUser.getUser().getUserInfo().getSafeSetting() != 1) {
                        showAlertMsgHint();
                        return;
                    }
                    UniqueActivity.launcher(this, WithDrawCoinFragment.class)
                            .putExtra(ExtraKeys.ACCOUNT_BEAN, mAccountBean)
                            .execute(this, REQUEST_WITHDRAW_COIN);
                }
                break;
            default:
        }
    }

    private void showAlertMsgHint() {
        MsgHintController withDrawPsdViewController = new MsgHintController(getActivity(), new MsgHintController.OnClickListener() {
            @Override
            public void onConfirmClick() {
                UniqueActivity.launcher(CoinPropertyFragment.this, CashPwdFragment.class)
                        .putExtra(ExtraKeys.HAS_WITH_DRAW_PASS, false)
                        .execute();
            }
        });
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog.setCustomViewController(withDrawPsdViewController)
                .show();
        withDrawPsdViewController.setConfirmText(R.string.go_to_set);
        withDrawPsdViewController.setMsg(R.string.set_draw_cash_pwd_hint);
        withDrawPsdViewController.setImageRes(R.drawable.ic_popup_attention);
    }
}
