package com.songbai.futurex.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.mine.PropertyFlowFragment;
import com.songbai.futurex.fragment.mine.adapter.PropertyFlowAdapter;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.PagingBean;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.GetUserFinanceFlowData;
import com.songbai.futurex.model.mine.CoinInfo;
import com.songbai.futurex.model.mine.CoinPropertyFlow;
import com.songbai.futurex.swipeload.RecycleViewSwipeLoadActivity;
import com.songbai.futurex.utils.AnimatorUtil;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.view.TitleBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yangguangda
 * @date 2018/6/1
 */
public class PropertyFlowActivity extends RecycleViewSwipeLoadActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.swipe_target)
    RecyclerView mSwipeTarget;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;
    @BindView(R.id.filtrateGroup)
    LinearLayout mFiltrateGroup;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    @BindView(R.id.selectCoinType)
    TextView mSelectCoinType;
    @BindView(R.id.flowType)
    TextView mFlowType;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.startTime)
    TextView mStartTime;
    @BindView(R.id.endTime)
    TextView mEndTime;
    private GetUserFinanceFlowData mGetUserFinanceFlowData;
    private int mPage = 0;
    private int mPageSize = 20;
    private boolean mAllType;
    private String mCoinType;
    private PropertyFlowAdapter mAdapter;
    private OptionsPickerView mPvOptions;
    private ArrayList<CoinInfo> mCoinInfos;

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
                    // TODO: 2018/6/4 动画
                    AnimatorUtil.expandVertical(mFiltrateGroup);
//                    mFiltrateGroup.setVisibility(mFiltrateGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                } else {

                }
            }
        });
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PropertyFlowAdapter();
        mAdapter.setOnClickListener(new PropertyFlowAdapter.OnClickListener() {
            @Override
            public void onItemClick() {
                UniqueActivity.launcher(PropertyFlowActivity.this, PropertyFlowFragment.class).execute();
            }
        });
        mSwipeTarget.setAdapter(mAdapter);
        mGetUserFinanceFlowData = new GetUserFinanceFlowData();
        getUserFinanceFlow();
    }

    @Override
    public View getContentView() {
        return mRootView;
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
                .callback(new Callback<Resp<PagingBean<CoinPropertyFlow>>>() {
                    @Override
                    protected void onRespSuccess(Resp<PagingBean<CoinPropertyFlow>> resp) {
                        mAdapter.setList(resp.getData());
                        mAdapter.notifyDataSetChanged();
                        stopFreshOrLoadAnimation();
                        if (resp.getData().getTotal() > mPage + 1) {
                            mPage++;
                        } else {
                            mSwipeToLoadLayout.setLoadMoreEnabled(false);
                        }
                    }
                })
                .fire();
    }

    @OnClick({R.id.selectCoinType, R.id.flowType, R.id.status, R.id.startTime, R.id.endTime, R.id.reset, R.id.filtrate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.selectCoinType:
                showCoinInfo(view);
                break;
            case R.id.flowType:
                break;
            case R.id.status:
                break;
            case R.id.startTime:
                showTimePicker(view);
                break;
            case R.id.endTime:
                showTimePicker(view);
                break;
            case R.id.reset:
                mGetUserFinanceFlowData = new GetUserFinanceFlowData();
                mSelectCoinType.setText(R.string.select_coin_type);
                mFlowType.setText(R.string.all_type);
                mStatus.setText(R.string.all_status);
                mStartTime.setText("");
                mEndTime.setText("");
                break;
            case R.id.filtrate:
                mPage = 0;
                getUserFinanceFlow();
                break;
            default:
        }
    }

    private void showCoinInfo(final View view) {
        if (mCoinInfos == null) {
            Apic.coinLoadSimpleList()
                    .callback(new Callback<Resp<ArrayList<CoinInfo>>>() {
                        @Override
                        protected void onRespSuccess(Resp<ArrayList<CoinInfo>> resp) {
                            mCoinInfos = resp.getData();
                            mCoinInfos.add(0, new CoinInfo());
                            ArrayList<String> list = new ArrayList<>();
                            for (CoinInfo coinInfo : mCoinInfos) {
                                String symbol = coinInfo.getSymbol();
                                if (!TextUtils.isEmpty(symbol)) {
                                    list.add(symbol.toUpperCase());
                                } else {
                                    list.add(getString(R.string.all_type));
                                }
                            }
                            showSelector((TextView) view, list, mCoinInfos);
                        }
                    })
                    .fire();
        } else {
            ArrayList<String> list = new ArrayList<>();
            for (CoinInfo coinInfo : mCoinInfos) {
                String symbol = coinInfo.getSymbol();
                if (!TextUtils.isEmpty(symbol)) {
                    list.add(symbol.toUpperCase());
                } else {
                    list.add(getString(R.string.all_type));
                }
            }
            showSelector((TextView) view, list, mCoinInfos);
        }
    }

    private void showSelector(final TextView target, final List<String> item, final List<?> origin) {
        mPvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                setSelectedItem(options1, item, target, origin);
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
                .setTextColorCenter(ContextCompat.getColor(this, R.color.text22))
                .setTextColorOut(ContextCompat.getColor(this, R.color.text99))
                .setDividerColor(ContextCompat.getColor(this, R.color.bgDD))
                .build();
        mPvOptions.setPicker(item, null, null);
        mPvOptions.show();
    }

    private void setSelectedItem(int options1, List<String> item, TextView target, List<?> origin) {
        target.setText(item.get(options1));
        Object obj = origin.get(options1);
        switch (target.getId()) {
            case R.id.selectCoinType:
                if (obj instanceof CoinInfo) {
                    mGetUserFinanceFlowData.setCoinType(((CoinInfo) obj).getSymbol());
                }
                break;
            case R.id.flowType:
                break;
            case R.id.status:
                break;
            default:
        }
    }

    private void showTimePicker(final View view) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(2018, 4, 1);
        endDate.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE));
        TimePickerView pvTime = new TimePickerBuilder(PropertyFlowActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                setSelectedTime(view, date);
            }
        })
                .setRangDate(startDate, endDate)
                .build();
        pvTime.show();
    }

    public void setSelectedTime(View view, Date date) {
        ((TextView) view).setText(DateUtil.format(date.getTime(), DateUtil.FORMAT_NOT_SECOND));
        switch (view.getId()) {
            case R.id.startTime:
                mGetUserFinanceFlowData.setStartTime(String.valueOf(date.getTime()));
                break;
            case R.id.endTime:
                mGetUserFinanceFlowData.setEndTime(String.valueOf(date.getTime()));
                break;
            default:
        }
    }
}
