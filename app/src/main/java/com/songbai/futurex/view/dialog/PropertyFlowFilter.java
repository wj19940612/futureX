package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.CoinInfo;
import com.songbai.futurex.model.status.FlowStatus;
import com.songbai.futurex.model.status.FlowType;
import com.songbai.futurex.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yangguangda
 * @date 2018/6/26
 */
public class PropertyFlowFilter {
    private final Context mContext;
    public static final int ALL = -10;
    @BindView(R.id.reset)
    TextView mReset;
    @BindView(R.id.filtrate)
    TextView mFiltrate;
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
    @BindView(R.id.filtrateGroup)
    LinearLayout mFiltrateGroup;
    private OnSelectCallBack mOnSelectCallBack;
    private final View mView;
    private OptionsPickerView<String> mPvOptions;
    private ArrayList<CoinInfo> mCoinInfos;

    private final int[] flowType = new int[]{ALL,
            FlowType.DRAW, FlowType.DEPOSITE, FlowType.ENTRUST_BUY,
            FlowType.ENTRUST_SELL, FlowType.OTC_TRADE_OUT, FlowType.DRAW_FEE,
            FlowType.TRADE_FEE, FlowType.PROMOTER_TO, FlowType.OTC_TRADE_IN,
            FlowType.AGENCY_TO, FlowType.LEGAL_ACCOUNT_IN, FlowType.COIN_ACCOUNT_OUT
    };
    private final int[] flowTypeStrRes = new int[]{
            R.string.all_type, R.string.withdraw_cash, R.string.recharge_coin,
            R.string.buy_order, R.string.sell_order, R.string.otc_transfer_out,
            R.string.withdraw_fee, R.string.deal_fee, R.string.promoter_account_transfer_into,
            R.string.otc_trade_in, R.string.agency_to, R.string.legal_account_in, R.string.coin_account_out};

    private final int[] flowStatus = new int[]{ALL,
            FlowStatus.SUCCESS, FlowStatus.FREEZE, FlowStatus.DRAW_REJECT,
            FlowStatus.ENTRUS_RETURN, FlowStatus.FREEZE_DEDUCT, FlowStatus.ENTRUSE_RETURN_SYS,
            FlowStatus.FREEZE_RETURN};

    private final int[] flowStatusStrRes = new int[]{
            R.string.all_status, R.string.completed, R.string.freeze, R.string.withdraw_coin_rejected,
            R.string.entrust_return, R.string.freeze_deduct, R.string.sys_withdraw, R.string.freeze_return};

    private ArrayList<String> mFlowTypeStr;
    private ArrayList<String> mFlowStatusStr;

    private String mTempCoinSymbol;
    private int mTempFlowType = ALL;
    private int mTempFlowStatus = ALL;
    private String mTempStartTime;
    private String mTempEndTime;
    private final ArrayList<Integer> mFlowTypes;
    private final ArrayList<Integer> mFlowStatus;
    private ArrayList<String> mCoinSymbols;
    private TimePickerView mPvTime;

    public PropertyFlowFilter(Context context, View view) {
        mContext = context;
        mFlowTypeStr = new ArrayList<>();
        for (int flowTypeStrRe : flowTypeStrRes) {
            mFlowTypeStr.add(mContext.getString(flowTypeStrRe));
        }
        mFlowStatusStr = new ArrayList<>();
        for (int flowStatusStrRe : flowStatusStrRes) {
            mFlowStatusStr.add(mContext.getString(flowStatusStrRe));
        }
        mFlowTypes = new ArrayList<>();
        for (int i : flowType) {
            mFlowTypes.add(i);
        }
        mFlowStatus = new ArrayList<>();
        for (int status : flowStatus) {
            mFlowStatus.add(status);
        }
        mView = view;
        ButterKnife.bind(this, mView);
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectCoinType.setText(R.string.select_coin_type);
                mFlowType.setText(R.string.all_type);
                mStatus.setText(R.string.all_status);
                mStartTime.setText("");
                mEndTime.setText("");
                if (mOnSelectCallBack != null) {
                    mOnSelectCallBack.onResetClick();
                }
                showOrDismiss();
            }
        });
        mFiltrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSelectCallBack != null) {
                    mOnSelectCallBack.onSelected(mTempCoinSymbol, mTempFlowType, mTempFlowStatus, mTempStartTime, mTempEndTime);
                }
                showOrDismiss();
            }
        });
    }

    public void showOrDismiss() {
        if (mView.getVisibility() == View.VISIBLE) {
            mView.setVisibility(View.GONE);
        } else {
            mView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.selectCoinType, R.id.flowType, R.id.status, R.id.startTime, R.id.endTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.selectCoinType:
                showCoinInfo(view);
                break;
            case R.id.flowType:
                showSelector((TextView) view, mFlowTypeStr, mFlowTypes);
                break;
            case R.id.status:
                showSelector((TextView) view, mFlowStatusStr, mFlowStatus);
                break;
            case R.id.startTime:
                showTimePicker(view);
                break;
            case R.id.endTime:
                showTimePicker(view);
                break;
            default:
        }
    }

    public void restoreData(String coinSymbol, String flowType, String flowStatus, String startTime, String endTime) {
        mTempCoinSymbol = coinSymbol;
        if (TextUtils.isEmpty(flowType)) {
            mTempFlowType = ALL;
        } else {
            mTempFlowType = Integer.valueOf(flowType);
        }
        if (TextUtils.isEmpty(flowStatus)) {
            mTempFlowStatus = ALL;
        } else {
            mTempFlowStatus = Integer.valueOf(flowStatus);
        }
        mTempStartTime = startTime;
        mTempEndTime = endTime;
    }

    public interface OnSelectCallBack {
        void onSelected(String tempCoinSymbol, int tempFlowType, int tempFlowStatus, String tempStartTime, String tempEndTime);

        void onResetClick();
    }

    public void setOnSelectCallBack(OnSelectCallBack onSelectCallBack) {
        mOnSelectCallBack = onSelectCallBack;
    }

    private void showCoinInfo(final View view) {
        if (mCoinInfos == null) {
            Apic.coinLoadSimpleList()
                    .callback(new Callback<Resp<ArrayList<CoinInfo>>>() {
                        @Override
                        protected void onRespSuccess(Resp<ArrayList<CoinInfo>> resp) {
                            mCoinInfos = resp.getData();
                            mCoinInfos.add(0, new CoinInfo());
                            mCoinSymbols = new ArrayList<>();
                            for (CoinInfo coinInfo : mCoinInfos) {
                                String symbol = coinInfo.getSymbol();
                                if (!TextUtils.isEmpty(symbol)) {
                                    mCoinSymbols.add(symbol.toUpperCase());
                                } else {
                                    mCoinSymbols.add(mContext.getString(R.string.all_type));
                                }
                            }
                            showSelector((TextView) view, mCoinSymbols, mCoinInfos);
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
                    list.add(mContext.getString(R.string.all_type));
                }
            }
            showSelector((TextView) view, list, mCoinInfos);
        }
    }

    private void showSelector(final TextView target, final List<String> item, final List<?> origin) {
        int selectedOption = 0;
        switch (target.getId()) {
            case R.id.selectCoinType:
                selectedOption = mCoinSymbols.indexOf(mTempCoinSymbol);
                break;
            case R.id.flowType:
                selectedOption = mFlowTypes.indexOf(mTempFlowType);
                break;
            case R.id.status:
                selectedOption = mFlowStatus.indexOf(mTempFlowStatus);
                break;
            default:
        }
        mPvOptions = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
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
                .setSelectOptions(selectedOption)
                .setTextColorCenter(ContextCompat.getColor(mContext, R.color.text22))
                .setTextColorOut(ContextCompat.getColor(mContext, R.color.text99))
                .setDividerColor(ContextCompat.getColor(mContext, R.color.bgDD))
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
                    mTempCoinSymbol = ((CoinInfo) obj).getSymbol();
                }
                break;
            case R.id.flowType:
                mTempFlowType = (int) obj;
                break;
            case R.id.status:
                mTempFlowStatus = (int) obj;
                break;
            default:
        }
    }

    private void showTimePicker(final View view) {
        String date = "";
        Calendar lastDate = Calendar.getInstance();
        switch (view.getId()) {
            case R.id.startTime:
                date = mTempStartTime;
                break;
            case R.id.endTime:
                date = mTempEndTime;
                break;
            default:
        }
        if (!TextUtils.isEmpty(date)) {
            //注意是空格+UTC
            date = date.replace("Z", " UTC");
            lastDate.setTimeInMillis(DateUtil.getDate(date, DateUtil.FORMAT_UTZ_STANDARD));
        }
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(2018, 4, 1);
        endDate.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE));
        mPvTime = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                setSelectedTime(view, date);
            }
        })
                .setLayoutRes(R.layout.pickerview_date_custom_view, new CustomListener() {
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
                                mPvTime.returnData();
                                mPvTime.dismiss();
                            }
                        });
                    }
                })
                .setRangDate(startDate, endDate)
                .isCenterLabel(false)
                .setDate(lastDate)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setTextColorCenter(ContextCompat.getColor(mContext, R.color.text22))
                .setTextColorOut(ContextCompat.getColor(mContext, R.color.text99))
                .setDividerColor(ContextCompat.getColor(mContext, R.color.bgDD))
                .build();
        mPvTime.show();
    }

    public void setSelectedTime(View view, Date date) {
        ((TextView) view).setText(DateUtil.format(date.getTime(), DateUtil.FORMAT_SPECIAL_SLASH_NO_HOUR));
        switch (view.getId()) {
            case R.id.startTime:
                mTempStartTime = DateUtil.format(date.getTime(), DateUtil.FORMAT_UTZ_STANDARD);
                Log.e("wtf", "setSelectedTime: " + mTempStartTime);
                break;
            case R.id.endTime:
                mTempEndTime = DateUtil.format(date.getTime(), DateUtil.FORMAT_UTZ_STANDARD);
                Log.e("wtf", "setSelectedTime: " + mTempEndTime);
                break;
            default:
        }
    }
}
