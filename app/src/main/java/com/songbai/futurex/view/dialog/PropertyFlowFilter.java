package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import com.songbai.futurex.model.status.CurrencyFlowStatus;
import com.songbai.futurex.model.status.CurrencyFlowType;
import com.songbai.futurex.model.status.OTCFlowStatus;
import com.songbai.futurex.model.status.OTCFlowType;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.Display;

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

    private final int[] currencyFlowType = new int[]{ALL,
            CurrencyFlowType.DRAW, CurrencyFlowType.DEPOSITE, CurrencyFlowType.ENTRUST_BUY,
            CurrencyFlowType.ENTRUST_SELL, CurrencyFlowType.DRAW_FEE,
            CurrencyFlowType.TRADE_FEE,
            CurrencyFlowType.LEGAL_ACCOUNT_IN, CurrencyFlowType.COIN_ACCOUNT_OUT,
            CurrencyFlowType.PERIODIC_RELEASE, CurrencyFlowType.SPECIAL_TRADE, CurrencyFlowType.CASHBACK,
            CurrencyFlowType.INVT_REWARD, CurrencyFlowType.DISTRIBUTED_REV, CurrencyFlowType.RELEASED_BFB,
            CurrencyFlowType.MINERS_REWAR, CurrencyFlowType.SHARED_FEE, CurrencyFlowType.SUBSCRIPTION
            , CurrencyFlowType.EVENT_GRANT, CurrencyFlowType.EVENT_AWARD
    };
    private final int[] currencyFlowTypeStrRes = new int[]{
            R.string.all_type, R.string.withdraw_cash, R.string.recharge_coin,
            R.string.buy_order, R.string.sell_order,
            R.string.withdraw_fee, R.string.deal_fee,
            R.string.legal_account_in, R.string.coin_account_out,
            R.string.periodic_release, R.string.special_trade, R.string.cashback,
            R.string.invt_reward, R.string.distributed_rev, R.string.release_bfb,
            R.string.miners_rewar, R.string.shared_fee, R.string.subscription,
            R.string.event_grant, R.string.event_award
    };

    private final int[] otcFlowType = new int[]{ALL,
            OTCFlowType.TRADE_COMMISSION,
            OTCFlowType.COIN_ACCOUNT_IN, OTCFlowType.LEGAL_CURRENCY_ACCOUNT_OUT,
            OTCFlowType.OTC_TRADE_IN, OTCFlowType.OTC_TRADE_OUT
    };
    private final int[] otcFlowTypeStrRes = new int[]{R.string.all_type,
            R.string.trade_commission,
            R.string.coin_account_in, R.string.legal_account_out,
            R.string.otc_trade_in, R.string.otc_trade_out};

    private final int[] currencyFlowStatus = new int[]{ALL,
            CurrencyFlowStatus.SUCCESS, CurrencyFlowStatus.FREEZE, CurrencyFlowStatus.DRAW_REJECT,
            CurrencyFlowStatus.ENTRUS_RETURN, CurrencyFlowStatus.FREEZE_DEDUCT, CurrencyFlowStatus.ENTRUSE_RETURN_SYS,
            CurrencyFlowStatus.FREEZE_RETURN};

    private final int[] currencyFlowStatusStrRes = new int[]{
            R.string.all_status, R.string.completed, R.string.freeze, R.string.withdraw_coin_rejected,
            R.string.entrust_return, R.string.freeze_deduct, R.string.sys_withdraw, R.string.freeze_return};

    private final int[] otcFlowStatus = new int[]{ALL,
            OTCFlowStatus.SUCCESS, OTCFlowStatus.FREEZE, OTCFlowStatus.FREEZE_DEDUCT,
            OTCFlowStatus.CANCEL_TRADE_FREEZE, OTCFlowStatus.SYS_CANCEL_TRADE_FREEZE, OTCFlowStatus.POSTER_OFF_SHELF_RETURN,};

    private final int[] otcFlowStatusStrRes = new int[]{
            R.string.all_status, R.string.completed, R.string.freeze, R.string.freeze_deduct,
            R.string.cancel_trade_freeze, R.string.sys_cancel_trade_freeze, R.string.poster_off_shelf_return};

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
    private int mAccountType;

    public PropertyFlowFilter(Context context, View view, int accountType) {
        mView = view;
        ButterKnife.bind(this, mView);
        mAccountType = accountType;
        mContext = context;
        mFlowTypeStr = new ArrayList<>();
        mFlowStatusStr = new ArrayList<>();
        mFlowStatus = new ArrayList<>();
        mFlowTypes = new ArrayList<>();
        createStr();
        restoreView();
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
            }
        });
        mFiltrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSelectCallBack != null) {
                    mOnSelectCallBack.onSelected(mTempCoinSymbol, mTempFlowType, mTempFlowStatus, mTempStartTime, mTempEndTime);
                }
            }
        });
    }

    private void restoreView() {
        if (!TextUtils.isEmpty(mTempCoinSymbol)) {
            mSelectCoinType.setText(mTempCoinSymbol.toUpperCase());
        }
        if (mTempFlowType != 0) {
            mFlowType.setText(mFlowTypeStr.get(mFlowTypes.indexOf(mTempFlowType)));
        }
        if (mTempFlowStatus != 0) {
            mStatus.setText(mFlowStatusStr.get(mFlowStatus.indexOf(mTempFlowStatus)));
        }
        if (!TextUtils.isEmpty(mTempStartTime)) {
            long timeMillion = getTimeMillion(mTempStartTime);
            if (timeMillion > 0) {
                mStartTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mStartTime.setPadding(0, 0, 0, 0);
                mStartTime.setText(DateUtil.format(timeMillion, DateUtil.FORMAT_SPECIAL_SLASH_NO_HOUR));
            }
        } else {
            mStartTime.setPadding((int) Display.dp2Px(15, mContext.getResources()), 0, 0, 0);
            mStartTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_history_calendar, 0, 0, 0);
            mStartTime.setText("");
        }
        if (!TextUtils.isEmpty(mTempEndTime)) {
            long timeMillion = getTimeMillion(mTempEndTime);
            if (timeMillion > 0) {
                mEndTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mStartTime.setPadding(0, 0, 0, 0);
                mEndTime.setText(DateUtil.format(timeMillion, DateUtil.FORMAT_SPECIAL_SLASH_NO_HOUR));
            }
        } else {
            mEndTime.setPadding((int) Display.dp2Px(15, mContext.getResources()), 0, 0, 0);
            mEndTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_history_calendar, 0, 0, 0);
            mEndTime.setText("");
        }
    }

    private void createStr() {
        switch (mAccountType) {
            case 0:
                for (int flowTypeStrRe : currencyFlowTypeStrRes) {
                    mFlowTypeStr.add(mContext.getString(flowTypeStrRe));
                }
                for (int flowStatusStrRe : currencyFlowStatusStrRes) {
                    mFlowStatusStr.add(mContext.getString(flowStatusStrRe));
                }
                for (int i : currencyFlowType) {
                    mFlowTypes.add(i);
                }
                for (int status : currencyFlowStatus) {
                    mFlowStatus.add(status);
                }
                break;
            case 1:
                for (int flowTypeStrRe : otcFlowTypeStrRes) {
                    mFlowTypeStr.add(mContext.getString(flowTypeStrRe));
                }
                for (int flowStatusStrRe : otcFlowStatusStrRes) {
                    mFlowStatusStr.add(mContext.getString(flowStatusStrRe));
                }
                for (int i : otcFlowType) {
                    mFlowTypes.add(i);
                }
                for (int status : otcFlowStatus) {
                    mFlowStatus.add(status);
                }
                break;
            case 2:
                break;
            default:
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
        restoreView();
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
        long timeMillion = getTimeMillion(date);
        if (timeMillion != 0) {
            lastDate.setTimeInMillis(timeMillion);
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

    private long getTimeMillion(String date) {
        if (!TextUtils.isEmpty(date)) {
            //注意是空格+UTC
            date = date.replace("Z", " UTC");
            return DateUtil.getDate(date, DateUtil.FORMAT_UTZ_STANDARD);
        }
        return 0;
    }

    public void setSelectedTime(View view, Date date) {
        TextView textView = (TextView) view;
        textView.setText(DateUtil.format(date.getTime(), DateUtil.FORMAT_SPECIAL_SLASH_NO_HOUR));
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        textView.setPadding(0, 0, 0, 0);
        switch (view.getId()) {
            case R.id.startTime:
                mTempStartTime = DateUtil.format(date.getTime(), DateUtil.FORMAT_UTZ_STANDARD);
                break;
            case R.id.endTime:
                mTempEndTime = DateUtil.format(date.getTime(), DateUtil.FORMAT_UTZ_STANDARD);
                break;
            default:
        }
    }
}
