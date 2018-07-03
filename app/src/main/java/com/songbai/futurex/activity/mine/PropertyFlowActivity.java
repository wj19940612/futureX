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
import com.songbai.futurex.model.status.FlowStatus;
import com.songbai.futurex.swipeload.BaseSwipeLoadActivity;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.PropertyFlowFilter;
import com.zcmrr.swipelayout.foot.LoadMoreFooterView;
import com.zcmrr.swipelayout.header.RefreshHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    private final int[] flowStatus = new int[]{TYPE_ALL,
            FlowStatus.SUCCESS, FlowStatus.FREEZE, FlowStatus.DRAW_REJECT,
            FlowStatus.ENTRUS_RETURN, FlowStatus.FREEZE_DEDUCT, FlowStatus.ENTRUSE_RETURN_SYS,
            FlowStatus.FREEZE_RETURN};
    private final int[] flowStatusStrRes = new int[]{
            R.string.all_status, R.string.completed, R.string.freeze, R.string.withdraw_coin_rejected,
            R.string.entrust_return, R.string.freeze_deduct, R.string.sys_withdraw, R.string.freeze_return};
    private OptionsPickerView<String> mPvOptions;
    private ArrayList<String> mFlowStatusStr;
    private ArrayList<Integer> mFlowStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prorerty_flow);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mAllType = getIntent().getBooleanExtra(ExtraKeys.PROPERTY_FLOW_FILTER_TYPE_ALL, false);
        mCoinType = getIntent().getStringExtra(ExtraKeys.COIN_TYPE);
        mTitleBar.setTitle(mAllType ? R.string.property_flow : R.string.history_record);
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAllType) {
                    showAllPropertyFilter();
                } else {
                    if (mFlowStatusStr == null) {
                        mFlowStatusStr = new ArrayList<>();
                        for (int flowStatusStrRe : flowStatusStrRes) {
                            mFlowStatusStr.add(getString(flowStatusStrRe));
                        }
                    }
                    showSelector(mFlowStatusStr, mFlowStatus);
                }
            }
        });
        mFlowStatus = new ArrayList<>();
        for (int status : flowStatus) {
            mFlowStatus.add(status);
        }
        mSwipeTarget.setEmptyView(mEmptyView);
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PropertyFlowAdapter();
        mAdapter.setOnClickListener(new PropertyFlowAdapter.OnClickListener() {
            @Override
            public void onItemClick(int id) {
                UniqueActivity.launcher(PropertyFlowActivity.this, PropertyFlowDetailFragment.class)
                        .putExtra(ExtraKeys.PROPERTY_FLOW_ID, id)
                        .execute();
            }
        });
        mSwipeTarget.setAdapter(mAdapter);
        mGetUserFinanceFlowData = new GetUserFinanceFlowData();
        getUserFinanceFlow();
    }

    private void showAllPropertyFilter() {
        PropertyFlowFilter propertyFlowFilter = new PropertyFlowFilter(this, mFiltrateGroup);
        propertyFlowFilter.restoreData(
                mGetUserFinanceFlowData.getCoinType(), mGetUserFinanceFlowData.getFlowType(),
                mGetUserFinanceFlowData.getStatus(), mGetUserFinanceFlowData.getStartTime(),
                mGetUserFinanceFlowData.getEndTime());
        propertyFlowFilter.setOnSelectCallBack(new PropertyFlowFilter.OnSelectCallBack() {
            @Override
            public void onSelected(String coinSymbol, int flowType, int flowStatus, String startTime, String endTime) {
                mGetUserFinanceFlowData.setCoinType(coinSymbol);
                mGetUserFinanceFlowData.setCoinType(coinSymbol);
                mGetUserFinanceFlowData.setFlowType(flowType == PropertyFlowFilter.ALL ? "" : String.valueOf(flowType));
                mGetUserFinanceFlowData.setStartTime(startTime);
                mGetUserFinanceFlowData.setEndTime(endTime);
                mPage = 0;
                getUserFinanceFlow();
            }

            @Override
            public void onResetClick() {
                mGetUserFinanceFlowData = new GetUserFinanceFlowData();
                mPage = 0;
                getUserFinanceFlow();
            }
        });
        propertyFlowFilter.showOrDismiss();
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
        Apic.getUserFinanceFlow(mGetUserFinanceFlowData, mPage, mPageSize)
                .callback(new Callback<Resp<PagingWrap<CoinPropertyFlow>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingWrap<CoinPropertyFlow>> resp) {
                        mAdapter.setList(resp.getData());
                        mAdapter.notifyDataSetChanged();
                        stopFreshOrLoadAnimation();
                        if (mPage == 0) {
                            mSwipeTarget.hideAll(false);
                        }
                        mPage++;
                        if (mPage >= resp.getData().getTotal()) {
                            mSwipeToLoadLayout.setLoadMoreEnabled(false);
                        }
                    }
                })
                .fire();
    }

    private void showSelector(final List<String> item, final ArrayList<Integer> origin) {
        String status = mGetUserFinanceFlowData.getStatus();
        int selectedOption = 0;
        if (!TextUtils.isEmpty(status)) {
            selectedOption = mFlowStatus.indexOf(Integer.valueOf(status));
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
        mGetUserFinanceFlowData.setStatus(status == TYPE_ALL ? "" : String.valueOf(status));
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
}
