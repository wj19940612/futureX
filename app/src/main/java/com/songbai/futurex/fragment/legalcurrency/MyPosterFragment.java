package com.songbai.futurex.fragment.legalcurrency;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.OtcWarePoster;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.swipeload.BaseSwipeLoadFragment;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.EditTypeController;
import com.songbai.futurex.view.dialog.MsgHintController;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/6/21
 */
public class MyPosterFragment extends BaseSwipeLoadFragment {
    private static final int REQUEST_PUBLISH_POSTER = 12312;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    @BindView(R.id.swipe_target)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    private Unbinder mBind;
    private MyAdAdapter mAdapter;
    private int mPage;
    private boolean isPrepared;
    private int mPageSize = 20;
    private String mCoinType;
    private String mPayCurrency;
    private boolean mShouldRefresh;
    private SmartDialog mSmartDialog;
    private boolean mRequested;
    private boolean mPairChanged;

    public static MyPosterFragment newInstance(String coinType, String payCurrency) {
        MyPosterFragment wantBuyOrSellFragment = new MyPosterFragment();
        Bundle bundle = new Bundle();
        bundle.putString("coinType", coinType);
        bundle.putString("payCurrency", payCurrency);
        wantBuyOrSellFragment.setArguments(bundle);
        return wantBuyOrSellFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_poster, container, false);
        mBind = ButterKnife.bind(this, view);
        isPrepared = true;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCoinType = arguments.getString("coinType");
            mPayCurrency = arguments.getString("payCurrency");
        }
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MyAdAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onEditClick(OtcWarePoster otcWarePoster) {
                showEditTypeSelector(otcWarePoster);
            }

            @Override
            public void onShelfClick(OtcWarePoster otcWarePoster) {
                switch (otcWarePoster.getStatus()) {
                    case OtcWarePoster.OFF_SHELF:
                        updateStatus(otcWarePoster, OtcWarePoster.ON_SHELF);
                        break;
                    case OtcWarePoster.ON_SHELF:
                        showOffShelfView(otcWarePoster);
                        break;
                    default:
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        lazyLoad();
    }

    private void showOffShelfView(final OtcWarePoster otcWarePoster) {
        MsgHintController withDrawPsdViewController = new MsgHintController(getActivity(), new MsgHintController.OnClickListener() {
            @Override
            public void onConfirmClick() {
                updateStatus(otcWarePoster, OtcWarePoster.OFF_SHELF);
            }
        });
        mSmartDialog = SmartDialog.solo(getActivity());
        mSmartDialog.setCustomViewController(withDrawPsdViewController)
                .show();
        withDrawPsdViewController.setMsg(R.string.off_shelf_hint_msg);
        withDrawPsdViewController.setImageRes(R.drawable.ic_ad_xiajia_pic);
    }

    private void showEditTypeSelector(final OtcWarePoster otcWarePoster) {
        EditTypeController editTypeController = new EditTypeController(getContext());
        editTypeController.setOnItemClickListener(new EditTypeController.OnItemClickListener() {
            @Override
            public void onEditClick() {
                UniqueActivity.launcher(MyPosterFragment.this, PublishPosterFragment.class)
                        .putExtra(ExtraKeys.OTC_WARE_POSTER_ID, otcWarePoster.getId())
                        .putExtra(ExtraKeys.SELECTED_LEGAL_COIN_SYMBOL, otcWarePoster.getCoinSymbol())
                        .putExtra(ExtraKeys.SELECTED_CURRENCY_SYMBOL, otcWarePoster.getPayCurrency())
                        .execute(MyPosterFragment.this, REQUEST_PUBLISH_POSTER);
            }

            @Override
            public void onDeleteClick(final SmartDialog dialog) {
                dialog.dismiss();
                showDeleteView(otcWarePoster);
            }
        });
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(editTypeController)
                .show();
    }

    private void showDeleteView(final OtcWarePoster otcWarePoster) {
        MsgHintController withDrawPsdViewController = new MsgHintController(getActivity(), new MsgHintController.OnClickListener() {
            @Override
            public void onConfirmClick() {
                Apic.otcWaresDelete(otcWarePoster.getId())
                        .callback(new Callback<Resp<Object>>() {
                            @Override
                            protected void onRespSuccess(Resp<Object> resp) {
                                mAdapter.getList().remove(otcWarePoster);
                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.getList().size() == 0) {
                                    mPage = 0;
                                    otcWaresList(mPage, mPageSize);
                                }
                            }
                        })
                        .fire();
            }
        });
        mSmartDialog = SmartDialog.solo(getActivity());
        mSmartDialog.setCustomViewController(withDrawPsdViewController)
                .show();
        withDrawPsdViewController.setMsg(R.string.delete_poster_hint_msg);
        withDrawPsdViewController.setImageRes(R.drawable.ic_ad_delete_pic);
    }

    private void updateStatus(final OtcWarePoster otcWarePoster, int onShelf) {
        Apic.otcWaresUpdateStatus(otcWarePoster.getId(), onShelf).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        switch (otcWarePoster.getStatus()) {
                            case OtcWarePoster.OFF_SHELF:
                                otcWarePoster.setStatus(OtcWarePoster.ON_SHELF);
                                break;
                            case OtcWarePoster.ON_SHELF:
                                otcWarePoster.setStatus(OtcWarePoster.OFF_SHELF);
                                break;
                            default:
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .fire();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) {
            lazyLoad();
            if (mShouldRefresh) {
                mPage = 0;
                otcWaresList(mPage, mPageSize);
                mShouldRefresh = false;
            }
            if (!mRequested) {
                otcWaresList(mPage, mPageSize);
            }
        }
        if (isVisibleToUser && isPrepared && mPairChanged) {
            otcWaresList(mPage, mPageSize);
        }
    }

    private void lazyLoad() {
        if (isPrepared && getUserVisibleHint()) {
            otcWaresList(mPage, mPageSize);
        }
    }

    public void setRequestParamAndRefresh(String coinType, String payCurrency) {
        mPage = 0;
        mCoinType = coinType;
        mPayCurrency = payCurrency;
        mPairChanged = true;
        if (getUserVisibleHint() && isPrepared && mPairChanged) {
            otcWaresList(mPage, mPageSize);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mRequested) {
            otcWaresList(mPage, mPageSize);
        }
    }

    private void otcWaresList(int page, int pageSize) {
        if (!LocalUser.getUser().isLogin()) {
            return;
        }
        mRequested = true;
        Apic.otcWaresList(page, pageSize, mCoinType, mPayCurrency).tag(TAG)
                .callback(new Callback<Resp<PagingWrap<OtcWarePoster>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingWrap<OtcWarePoster>> resp) {
                        if (mPairChanged) {
                            mPairChanged = false;
                        }
                        mSwipeToLoadLayout.setLoadMoreEnabled(true);
                        mAdapter.setList(resp.getData().getData());
                        mAdapter.notifyDataSetChanged();
                        stopFreshOrLoadAnimation();
                        if (mPage == 0) {
                            mRecyclerView.hideAll(false);
                        }
                        mPage++;
                        if (mPage >= resp.getData().getTotal()) {
                            mSwipeToLoadLayout.setLoadMoreEnabled(false);
                        }
                    }

                    @Override
                    public void onFailure(ReqError reqError) {
                        super.onFailure(reqError);
                        stopFreshOrLoadAnimation();
                        if (mPage == 0) {
                            mRecyclerView.hideAll(false);
                        }
                    }
                }).fireFreely();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PUBLISH_POSTER) {
            if (data != null) {
                boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, false);
                if (shouldRefresh) {
                    mPage = 0;
                    otcWaresList(mPage, mPageSize);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
        isPrepared = false;
    }

    @Override
    public void onLoadMore() {
        otcWaresList(mPage, mPageSize);
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        otcWaresList(mPage, mPageSize);
    }

    @NonNull
    @Override
    public Object getSwipeTargetView() {
        return mRecyclerView;
    }

    @NonNull
    @Override
    public SwipeToLoadLayout getSwipeToLoadLayout() {
        return mSwipeToLoadLayout;
    }

    @NonNull
    @Override
    public RefreshHeaderView getRefreshHeaderView() {
        return mSwipeRefreshHeader;
    }

    @NonNull
    @Override
    public LoadMoreFooterView getLoadMoreFooterView() {
        return mSwipeLoadMoreFooter;
    }

    public void refresh(boolean shouldRefresh) {
        mShouldRefresh = shouldRefresh;
        if (getUserVisibleHint()) {
            mPage = 0;
            otcWaresList(mPage, mPageSize);
            mShouldRefresh = false;
        }
    }

    public interface OnItemClickListener {
        void onEditClick(OtcWarePoster otcWarePoster);

        void onShelfClick(OtcWarePoster otcWarePoster);
    }

    class MyAdAdapter extends RecyclerView.Adapter {
        private OnItemClickListener mOnItemClickListener;
        private List<OtcWarePoster> mList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_otc_ware_poster, parent, false);
            return new MyAdHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyAdHolder) {
                ((MyAdHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(List<OtcWarePoster> list) {
            if (mPage == 0) {
                mList.clear();
            }
            mList.addAll(list);
        }

        public List<OtcWarePoster> getList() {
            return mList;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        class MyAdHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.posterType)
            TextView mPosterType;
            @BindView(R.id.edit)
            TextView mEdit;
            @BindView(R.id.operateArea)
            TextView mOperateArea;
            @BindView(R.id.price)
            TextView mPrice;
            @BindView(R.id.legalAmount)
            TextView mLegalAmount;
            @BindView(R.id.limit)
            TextView mLimit;
            @BindView(R.id.tradeAmount)
            TextView mTreadAmount;
            @BindView(R.id.updateTime)
            TextView mUpdateTime;
            @BindView(R.id.status)
            TextView mStatus;

            MyAdHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindData(final OtcWarePoster otcWarePoster) {
                switch (otcWarePoster.getDealType()) {
                    case OtcWarePoster.DEAL_TYPE_BUY:
                        mPosterType.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                        mPosterType.setText(getString(R.string.buy_blank_symbol_x, otcWarePoster.getCoinSymbol().toUpperCase()));
                        break;
                    case OtcWarePoster.DEAL_TYPE_SELL:
                        mPosterType.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                        mPosterType.setText(getString(R.string.sell_blank_symbol_x, otcWarePoster.getCoinSymbol().toUpperCase()));
                        break;
                    default:
                }

                switch (otcWarePoster.getPriceType()) {
                    case OtcWarePoster.FIXED_PRICE:
                        mPrice.setText(getString(R.string.fixed_price_x, FinanceUtil.trimTrailingZero(otcWarePoster.getFixedPrice())));
                        break;
                    case OtcWarePoster.FLOATING_PRICE:
                        mPrice.setText(getString(R.string.floating_price_x, FinanceUtil.trimTrailingZero(otcWarePoster.getPercent())));
                        break;
                    default:
                }
                mLegalAmount.setText(otcWarePoster.getPayCurrency().toUpperCase());
                mLimit.setText(getString(R.string.limit_range_x, FinanceUtil.subZeroAndDot(otcWarePoster.getMinTurnover(), 8),
                        FinanceUtil.subZeroAndDot(otcWarePoster.getMaxTurnover(), 8)));
                switch (otcWarePoster.getStatus()) {
                    case OtcWarePoster.OFF_SHELF:
                        mEdit.setEnabled(true);
                        mPosterType.setTextColor(ContextCompat.getColor(getContext(), R.color.text22));
                        mOperateArea.setText(R.string.on_shelf);
                        mStatus.setText(R.string.off_shelf);
                        break;
                    case OtcWarePoster.ON_SHELF:
                        mEdit.setEnabled(false);
                        mOperateArea.setText(R.string.off_shelf);
                        mStatus.setText(R.string.on_shelf);
                        break;
                    default:
                }
                mTreadAmount.setText(FinanceUtil.trimTrailingZero(otcWarePoster.getTradeCount()));
                mUpdateTime.setText(DateUtil.format(otcWarePoster.getUpdateTime(), DateUtil.FORMAT_HOUR_MINUTE_DATE));
                mEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onEditClick(otcWarePoster);
                        }
                    }
                });
                mOperateArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onShelfClick(otcWarePoster);
                        }
                    }
                });
            }
        }
    }
}
