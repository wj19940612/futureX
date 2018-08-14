package com.songbai.futurex.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.mine.PropertyFlowDetailFragment;
import com.songbai.futurex.fragment.mine.adapter.PropertyFlowAdapter;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingWrap;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.GetUserFinanceFlowData;
import com.songbai.futurex.model.mine.CoinPropertyFlow;
import com.songbai.futurex.model.status.CurrencyFlowType;
import com.songbai.futurex.model.status.OTCFlowType;
import com.songbai.futurex.swipeload.BaseSwipeLoadActivity;
import com.songbai.futurex.utils.AnimatorUtil;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.PropertyFlowFilter;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yangguangda
 * @date 2018/6/1
 */
public class PropertyFlowActivity extends BaseSwipeLoadActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.swipe_target)
    EmptyRecyclerView mSwipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @BindView(R.id.shader)
    View shader;
    @BindView(R.id.filtrateGroup)
    LinearLayout mFiltrateGroup;
    private final int TYPE_ALL = 10;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView mSwipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView mSwipeLoadMoreFooter;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    private GetUserFinanceFlowData mGetUserFinanceFlowData;
    private int mPage = 0;
    private int mPageSize = 20;
    private boolean mAllType;
    private String mCoinType;
    private PropertyFlowAdapter mAdapter;
    private final int[] currencyFlowType = new int[]{TYPE_ALL,
            CurrencyFlowType.DRAW, CurrencyFlowType.DEPOSITE, CurrencyFlowType.ENTRUST_BUY,
            CurrencyFlowType.ENTRUST_SELL, CurrencyFlowType.OTC_TRADE_OUT, CurrencyFlowType.DRAW_FEE,
            CurrencyFlowType.TRADE_FEE, CurrencyFlowType.PROMOTER_TO, CurrencyFlowType.OTC_TRADE_IN,
            CurrencyFlowType.AGENCY_TO, CurrencyFlowType.LEGAL_ACCOUNT_IN, CurrencyFlowType.COIN_ACCOUNT_OUT,
            CurrencyFlowType.PERIODIC_RELEASE, CurrencyFlowType.SPECIAL_TRADE, CurrencyFlowType.CASHBACK,
            CurrencyFlowType.INVT_REWARD, CurrencyFlowType.DISTRIBUTED_REV, CurrencyFlowType.RELEASED_BFB,
            CurrencyFlowType.MINERS_REWAR, CurrencyFlowType.SHARED_FEE, CurrencyFlowType.SUBSCRIPTION,
            CurrencyFlowType.EVENT_GRANT, CurrencyFlowType.EVENT_AWARD
    };
    private final int[] currencyFlowTypeStrRes = new int[]{
            R.string.all_type, R.string.withdraw_cash, R.string.recharge_coin,
            R.string.buy_order, R.string.sell_order, R.string.otc_transfer_out,
            R.string.withdraw_fee, R.string.deal_fee, R.string.promoter_account_transfer_into,
            R.string.otc_trade_in, R.string.agency_to, R.string.legal_account_in, R.string.coin_account_out,
            R.string.periodic_release, R.string.special_trade, R.string.cashback,
            R.string.invt_reward, R.string.distributed_rev, R.string.release_bfb,
            R.string.miners_rewar, R.string.shared_fee, R.string.subscription,
            R.string.event_grant,R.string.event_award
    };

    private final int[] otcFlowType = new int[]{TYPE_ALL,
            OTCFlowType.COIN_ACCOUNT_IN, OTCFlowType.LEGAL_CURRENCY_ACCOUNT_OUT,
            OTCFlowType.OTC_TRADE_IN, OTCFlowType.OTC_TRADE_OUT
    };
    private final int[] otcFlowTypeStrRes = new int[]{R.string.all_type,
            R.string.coin_account_in, R.string.legal_account_out,
            R.string.otc_trade_in, R.string.otc_trade_out};
    private OptionsPickerView<String> mPvOptions;
    private ArrayList<String> mFlowTypeStr;
    private ArrayList<Integer> mFlowType;
    private PropertyFlowFilter mPropertyFlowFilter;
    private int mAccountType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prorerty_flow);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mAllType = getIntent().getBooleanExtra(ExtraKeys.PROPERTY_FLOW_FILTER_TYPE_ALL, false);
        mAccountType = getIntent().getIntExtra(ExtraKeys.PROPERTY_FLOW_ACCOUNT_TYPE, 0);
        mCoinType = getIntent().getStringExtra(ExtraKeys.COIN_TYPE);
        mTitleBar.setTitle(mAllType ? R.string.property_flow : R.string.history_record);
        mTitleBar.setRightVisible(mAccountType != 2);
        mTitleBar.setRightViewEnable(mAccountType != 2);
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAllType) {
                    showAllPropertyFilter();
                } else {
                    showSelector(mFlowTypeStr, mFlowType);
                }
            }
        });
        mFlowTypeStr = new ArrayList<>();
        mFlowType = new ArrayList<>();
        createStr();
        mSwipeTarget.setEmptyView(mEmptyView);
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PropertyFlowAdapter();
        mAdapter.setSingleType(!mAllType);
        mAdapter.setAccount(mAccountType);
        mAdapter.setOnClickListener(new PropertyFlowAdapter.OnClickListener() {
            @Override
            public void onItemClick(int id) {
                UniqueActivity.launcher(PropertyFlowActivity.this, PropertyFlowDetailFragment.class)
                        .putExtra(ExtraKeys.PROPERTY_FLOW_ID, id)
                        .putExtra(ExtraKeys.PROPERTY_FLOW_ACCOUNT_TYPE, mAccountType)
                        .execute();
            }
        });
        mSwipeTarget.setAdapter(mAdapter);
        mGetUserFinanceFlowData = new GetUserFinanceFlowData();
        if (!TextUtils.isEmpty(mCoinType)) {
            mGetUserFinanceFlowData.setCoinType(mCoinType);
        }
        getUserFinanceFlow();
    }

    private void createStr() {
        switch (mAccountType) {
            case 0:
                for (int status : currencyFlowType) {
                    mFlowType.add(status);
                }
                for (int flowStatusStrRe : currencyFlowTypeStrRes) {
                    mFlowTypeStr.add(getString(flowStatusStrRe));
                }
                break;
            case 1:
                for (int status : otcFlowType) {
                    mFlowType.add(status);
                }
                for (int flowStatusStrRe : otcFlowTypeStrRes) {
                    mFlowTypeStr.add(getString(flowStatusStrRe));
                }
                break;
            case 2:

                break;
            default:
        }
    }

    private void showAllPropertyFilter() {
        mPropertyFlowFilter = new PropertyFlowFilter(this, mFiltrateGroup, mAccountType);
        mPropertyFlowFilter.restoreData(
                mGetUserFinanceFlowData.getCoinType(), mGetUserFinanceFlowData.getFlowType(),
                mGetUserFinanceFlowData.getStatus(), mGetUserFinanceFlowData.getStartTime(),
                mGetUserFinanceFlowData.getEndTime());
        mPropertyFlowFilter.setOnSelectCallBack(new PropertyFlowFilter.OnSelectCallBack() {
            @Override
            public void onSelected(String coinSymbol, int flowType, int flowStatus, String startTime, String endTime) {
                mGetUserFinanceFlowData.setCoinType(coinSymbol);
                mGetUserFinanceFlowData.setFlowType(flowType == PropertyFlowFilter.ALL ? "" : String.valueOf(flowType));
                mGetUserFinanceFlowData.setStatus(flowStatus == PropertyFlowFilter.ALL ? "" : String.valueOf(flowStatus));
                mGetUserFinanceFlowData.setStartTime(startTime);
                mGetUserFinanceFlowData.setEndTime(endTime);
                mPage = 0;
                getUserFinanceFlow();
                showOrDismiss();
            }

            @Override
            public void onResetClick() {
                mGetUserFinanceFlowData = new GetUserFinanceFlowData();
                mPage = 0;
                getUserFinanceFlow();
                showOrDismiss();
            }
        });
        showOrDismiss();
    }

    public void showOrDismiss() {
        if (mFiltrateGroup.getVisibility() == View.VISIBLE) {
            AnimatorUtil.collapseVertical(mFiltrateGroup, new AnimatorUtil.OnAnimatorFactionListener() {
                @Override
                public void onFaction(float fraction) {
                    if (fraction == 1) {
                        mFiltrateGroup.setVisibility(View.GONE);
                    }
                }
            });
            shader.postDelayed(new Runnable() {
                @Override
                public void run() {
                    shader.setVisibility(View.GONE);
                }
            }, 200);
        } else {
            AnimatorUtil.expandVertical(mFiltrateGroup, new AnimatorUtil.OnAnimatorFactionListener() {
                @Override
                public void onFaction(float fraction) {
                    if (fraction == 1) {
                        mFiltrateGroup.setVisibility(View.VISIBLE);
                    }
                }
            });
            shader.postDelayed(new Runnable() {
                @Override
                public void run() {
                    shader.setVisibility(View.VISIBLE);
                }
            }, 200);
        }
    }

    @Override
    public void onLoadMore() {
        getUserFinanceFlow();
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        getUserFinanceFlow();
    }

    private void getUserFinanceFlow() {
        switch (mAccountType) {
            case 0:
                Apic.getUserFinanceFlow(mGetUserFinanceFlowData, mPage, mPageSize).tag(TAG)
                        .callback(new Callback<Resp<PagingWrap<CoinPropertyFlow>>>() {
                            @Override
                            protected void onRespSuccess(Resp<PagingWrap<CoinPropertyFlow>> resp) {
                                mAdapter.setList(resp.getData());
                                mAdapter.notifyDataSetChanged();
                                stopFreshOrLoadAnimation();
                                if (mPage == 0) {
                                    mSwipeTarget.hideAll(false);
                                    mSwipeToLoadLayout.setRefreshEnabled(mEmptyView.getVisibility() != View.VISIBLE);
                                }
                                if (mPage < resp.getData().getTotal() - 1) {
                                    mPage++;
                                } else {
                                    mSwipeToLoadLayout.setLoadMoreEnabled(false);
                                }
                            }
                        })
                        .fire();
                break;
            case 1:
                Apic.otcAccountDetail(mGetUserFinanceFlowData, mPage, mPageSize).tag(TAG)
                        .callback(new Callback<Resp<PagingWrap<CoinPropertyFlow>>>() {
                            @Override
                            protected void onRespSuccess(Resp<PagingWrap<CoinPropertyFlow>> resp) {
                                mAdapter.setList(resp.getData());
                                mAdapter.notifyDataSetChanged();
                                stopFreshOrLoadAnimation();
                                if (mPage == 0) {
                                    mSwipeTarget.hideAll(false);
                                    mSwipeToLoadLayout.setRefreshEnabled(mEmptyView.getVisibility() != View.VISIBLE);
                                }
                                if (mPage < resp.getData().getTotal() - 1) {
                                    mPage++;
                                } else {
                                    mSwipeToLoadLayout.setLoadMoreEnabled(false);
                                }
                            }
                        })
                        .fire();
                break;
            case 2:
                Apic.userFinanceDetail(mGetUserFinanceFlowData, mPage, mPageSize).tag(TAG)
                        .callback(new Callback<Resp<PagingWrap<CoinPropertyFlow>>>() {
                            @Override
                            protected void onRespSuccess(Resp<PagingWrap<CoinPropertyFlow>> resp) {
                                mAdapter.setList(resp.getData());
                                mAdapter.notifyDataSetChanged();
                                stopFreshOrLoadAnimation();
                                if (mPage == 0) {
                                    mSwipeTarget.hideAll(false);
                                    mSwipeToLoadLayout.setRefreshEnabled(mEmptyView.getVisibility() != View.VISIBLE);
                                }
                                if (mPage < resp.getData().getTotal() - 1) {
                                    mPage++;
                                } else {
                                    mSwipeToLoadLayout.setLoadMoreEnabled(false);
                                }
                            }
                        })
                        .fire();
                break;
            default:
        }
    }

    private void showSelector(final List<String> item, final ArrayList<Integer> origin) {
        String status = mGetUserFinanceFlowData.getFlowType();
        int selectedOption = 0;
        if (!TextUtils.isEmpty(status)) {
            selectedOption = mFlowType.indexOf(Integer.valueOf(status));
        }
        mPvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                filterStatus(options1, item, origin);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_view, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        TextView cancel = v.findViewById(R.id.cancel);
                        TextView confirm = v.findViewById(R.id.confirm);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPvOptions.dismiss();
                            }
                        });
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPvOptions.returnData();
                                mPvOptions.dismiss();
                            }
                        });
                    }
                })
                .setCyclic(false, false, false)
                .setSelectOptions(selectedOption)
                .setTextColorCenter(ContextCompat.getColor(this, R.color.text22))
                .setTextColorOut(ContextCompat.getColor(this, R.color.text99))
                .setDividerColor(ContextCompat.getColor(this, R.color.bgDD))
                .build();
        mPvOptions.setPicker(item, null, null);
        mPvOptions.show();
    }

    private void filterStatus(int options1, List<String> item, ArrayList<Integer> origin) {
        int status = (int) origin.get(options1);
        mGetUserFinanceFlowData.setFlowType(status == TYPE_ALL ? "" : String.valueOf(status));
        mPage = 0;
        getUserFinanceFlow();
    }

    @NonNull
    @Override
    public Object getSwipeTargetView() {
        return mSwipeTarget;
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

    @OnClick(R.id.shader)
    public void onViewClicked() {
        showOrDismiss();
    }
}
