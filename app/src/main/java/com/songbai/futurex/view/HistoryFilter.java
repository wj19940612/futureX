package com.songbai.futurex.view;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.songbai.futurex.R;
import com.songbai.futurex.utils.AnimUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.songbai.futurex.model.order.Order.DIR_BUY;
import static com.songbai.futurex.model.order.Order.DIR_SELL;

public class HistoryFilter {

    public static class FilterResult {
        private String baseCurrency;
        private String counterCurrency;
        private Integer tradeDirection;

        public void setBaseCurrency(String baseCurrency) {
            this.baseCurrency = baseCurrency;
        }

        public void setCounterCurrency(String counterCurrency) {
            this.counterCurrency = counterCurrency;
        }

        public void setTradeDirection(Integer tradeDirection) {
            this.tradeDirection = tradeDirection;
        }

        public String getBaseCurrency() {
            return baseCurrency;
        }

        public String getCounterCurrency() {
            return counterCurrency;
        }

        public Integer getTradeDirection() {
            return tradeDirection;
        }

        public void clear() {
            baseCurrency = null;
            counterCurrency = null;
            tradeDirection = null;
        }
    }

    @BindView(R.id.currency)
    EditText mCurrency;
    @BindView(R.id.counterCurrency)
    TextView mCounterCurrency;
    @BindView(R.id.buyBtn)
    TextView mBuyBtn;
    @BindView(R.id.sellBtn)
    TextView mSellBtn;
    @BindView(R.id.reset)
    TextView mReset;
    @BindView(R.id.filterBtn)
    TextView mFilterBtn;
    private View mView;

    private OptionsPickerView mPvOptions;
    private List<String> mCounterCurrencyList;
    private OnFilterListener mOnFilterListener;
    private View mDimView;
    private FilterResult mFilterResult;
    private OnCurrencySelectorShowListener mOnCurrencySelectorShowListener;

    public FilterResult getFilterResult() {
        return mFilterResult;
    }

    public interface OnFilterListener {
        void onFilter(FilterResult filterResult);
    }

    public interface OnCurrencySelectorShowListener {
        void onCurrencySelectorShow();
    }

    public HistoryFilter(View view, final View dimView) {
        this.mView = view;
        ButterKnife.bind(this, view);

        String[] stringArray = view.getContext().getResources().getStringArray(R.array.market_radio_header);
        mCounterCurrencyList = new ArrayList<>(Arrays.asList(stringArray));
        mCounterCurrencyList.remove(mCounterCurrencyList.size() - 1); // remove optional
        mDimView = dimView;
        mFilterResult = new FilterResult();
        mDimView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @OnClick({R.id.counterCurrency, R.id.buyBtn, R.id.sellBtn, R.id.reset, R.id.filterBtn})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.counterCurrency:
                showCounterCurrencySelector();
                break;
            case R.id.buyBtn:
            case R.id.sellBtn:
                setBuyOrSellBtn(view);
                break;
            case R.id.reset:
                resetAll();
                break;
            case R.id.filterBtn:
                filter();
                break;
            default:
        }
    }

    private void setBuyOrSellBtn(View view) {
        boolean selected = view.isSelected();
        view.setSelected(!selected);
        if (!selected) {
            if (view.getId() == R.id.buyBtn) {
                mSellBtn.setSelected(false);
            } else if (view.getId() == R.id.sellBtn) {
                mBuyBtn.setSelected(false);
            }
        }
    }

    private void resetAll() {
        mCurrency.setText("");
        mCounterCurrency.setText(R.string.select_currency);

        mBuyBtn.setSelected(false);
        mSellBtn.setSelected(false);
        mFilterResult.clear();
    }

    private void showCounterCurrencySelector() {
        if (mPvOptions == null) {
            mPvOptions = new OptionsPickerBuilder(mView.getContext(), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3, View v) {
                    if (options1 < mCounterCurrencyList.size()) {
                        String counterCurrency = mCounterCurrencyList.get(options1);
                        selectCounterCurrency(counterCurrency);
                    }
                }
            }).setLayoutRes(R.layout.pickerview_custom_view, new CustomListener() {
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
            }).setCyclic(false, false, false)
                    .setTextColorCenter(ContextCompat.getColor(mView.getContext(), R.color.text22))
                    .setTextColorOut(ContextCompat.getColor(mView.getContext(), R.color.text99))
                    .setDividerColor(ContextCompat.getColor(mView.getContext(), R.color.bgDD))
                    .build();
            mPvOptions.setPicker(mCounterCurrencyList);
        }
        mPvOptions.show();

        if (mOnCurrencySelectorShowListener != null) {
            mOnCurrencySelectorShowListener.onCurrencySelectorShow();
        }
    }

    private void selectCounterCurrency(String counterCurrency) {
        mCounterCurrency.setText(counterCurrency);
    }

    public void setOnFilterListener(OnFilterListener onFilterListener) {
        mOnFilterListener = onFilterListener;
    }

    public void setOnCurrencySelectorShowListener(OnCurrencySelectorShowListener onCurrencySelectorShowListener) {
        mOnCurrencySelectorShowListener = onCurrencySelectorShowListener;
    }

    public void show() {
        if (mView.getVisibility() == View.GONE || mView.getVisibility() == View.INVISIBLE) {
            Animation expendY = AnimUtils.createExpendY(mView, 300);
            mView.startAnimation(expendY);
            if (mDimView != null) {
                mDimView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void dismiss() {
        if (mView.getVisibility() == View.VISIBLE) {
            mView.clearAnimation();
            Animation collapseY = AnimUtils.createCollapseY(mView, 300);
            mView.startAnimation(collapseY);
            if (mDimView != null) {
                mDimView.setVisibility(View.GONE);
            }
        }
    }

    public boolean isShowing() {
        return mView.getVisibility() == View.VISIBLE;
    }

    private void filter() {
        String baseCurrency = mCurrency.getText().toString().trim();
        String counterCurrency = mCounterCurrency.getText().toString().trim();
        Integer direction;

        if (TextUtils.isEmpty(baseCurrency)) {
            baseCurrency = null;
        }

        if (counterCurrency.equals(mView.getResources().getString(R.string.select_currency))) {
            counterCurrency = null;
        }

        if (mBuyBtn.isSelected()) {
            direction = DIR_BUY;
        } else if (mSellBtn.isSelected()) {
            direction = DIR_SELL;
        } else {
            direction = null;
        }

        mFilterResult.setBaseCurrency(baseCurrency);
        mFilterResult.setCounterCurrency(counterCurrency);
        mFilterResult.setTradeDirection(direction);

        if (mOnFilterListener != null) {
            mOnFilterListener.onFilter(mFilterResult);
        }
    }
}
