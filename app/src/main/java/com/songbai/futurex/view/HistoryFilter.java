package com.songbai.futurex.view;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.LegalCoin;
import com.songbai.futurex.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.songbai.futurex.model.Order.DIR_BUY;
import static com.songbai.futurex.model.Order.DIR_SELL;

public class HistoryFilter {
    @BindView(R.id.currency)
    EditText mCurrency;
    @BindView(R.id.selectCurrency)
    TextView mSelectCurrency;
    @BindView(R.id.selectIcon)
    ImageView mSelectIcon;
    @BindView(R.id.selectCurrencyLayout)
    RelativeLayout mSelectCurrencyLayout;
    @BindView(R.id.buyBtn)
    TextView mBuyBtn;
    @BindView(R.id.sellBtn)
    TextView mSellBtn;
    @BindView(R.id.has_finish)
    TextView mHasFinish;
    @BindView(R.id.has_withdrawn)
    TextView mHasWithdrawn;
    @BindView(R.id.reset)
    TextView mReset;
    @BindView(R.id.filterBtn)
    TextView mFilterBtn;
    @BindView(R.id.historyFilter)
    LinearLayout mHistoryFilter;
    private View mView;

    private OptionsPickerView<LegalCoin> mPvOptions;

    private List<LegalCoin> mLegalCoinArrayList;

    private OnFilterListener mOnFilterListener;

    public interface OnFilterListener {
        public void onFilter(String suffixSymbol, String prefixSymbol, int direction);
    }

    public HistoryFilter(View view) {
        this.mView = view;
        ButterKnife.bind(this, view);
    }

    @OnClick({R.id.selectCurrency, R.id.buyBtn, R.id.sellBtn, R.id.has_finish, R.id.has_withdrawn, R.id.reset, R.id.filterBtn})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.selectCurrency:
                showLegalCurrencySelect();
                break;
            case R.id.buyBtn:
                setBuyOrSellBtn(true);
                break;
            case R.id.sellBtn:
                setBuyOrSellBtn(false);
                break;
            case R.id.has_finish:
                setStatusBtn(true);
                break;
            case R.id.has_withdrawn:
                setStatusBtn(false);
                break;
            case R.id.reset:
                resetAll();
                break;
            case R.id.filterBtn:
                filter();
                break;
        }
    }

    private void setBuyOrSellBtn(boolean isBuy) {
        mBuyBtn.setSelected(isBuy);
        mSellBtn.setSelected(!isBuy);
        mBuyBtn.setTextColor(isBuy ? ContextCompat.getColor(mView.getContext(), R.color.colorPrimary) : ContextCompat.getColor(mView.getContext(), R.color.text22));
        mSellBtn.setTextColor(!isBuy ? ContextCompat.getColor(mView.getContext(), R.color.colorPrimary) : ContextCompat.getColor(mView.getContext(), R.color.text22));
    }

    private void setStatusBtn(boolean isFinish) {
        mHasFinish.setSelected(isFinish);
        mHasWithdrawn.setSelected(!isFinish);
        mHasFinish.setTextColor(isFinish ? ContextCompat.getColor(mView.getContext(), R.color.colorPrimary) : ContextCompat.getColor(mView.getContext(), R.color.text22));
        mHasWithdrawn.setTextColor(!isFinish ? ContextCompat.getColor(mView.getContext(), R.color.colorPrimary) : ContextCompat.getColor(mView.getContext(), R.color.text22));
    }

    private void resetAll() {
        mCurrency.setText("");
        mSelectIcon.setVisibility(View.VISIBLE);
        mSelectCurrency.setText(R.string.select_currency);

        mBuyBtn.setSelected(false);
        mSellBtn.setSelected(false);
        mBuyBtn.setTextColor(ContextCompat.getColor(mView.getContext(), R.color.text22));
        mSellBtn.setTextColor(ContextCompat.getColor(mView.getContext(), R.color.text22));

        mHasFinish.setSelected(false);
        mHasWithdrawn.setSelected(false);
        mHasFinish.setTextColor(ContextCompat.getColor(mView.getContext(), R.color.text22));
        mHasWithdrawn.setTextColor(ContextCompat.getColor(mView.getContext(), R.color.text22));
    }

    private void showLegalCurrencySelect() {
        if (mLegalCoinArrayList == null) {
            getLegalCoin();
            return;
        }
        if (mPvOptions == null) {
            mPvOptions = new OptionsPickerBuilder(mView.getContext(), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3, View v) {
                    if (options1 < mLegalCoinArrayList.size()) {
                        LegalCoin legalCoin = mLegalCoinArrayList.get(options1);
                        selectLegal(legalCoin);
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
            })
                    .setCyclic(false, false, false)
                    .setTextColorCenter(ContextCompat.getColor(mView.getContext(), R.color.text22))
                    .setTextColorOut(ContextCompat.getColor(mView.getContext(), R.color.text99))
                    .setDividerColor(ContextCompat.getColor(mView.getContext(), R.color.bgDD))
                    .build();
            mPvOptions.setPicker(mLegalCoinArrayList, null, null);
        }
        mPvOptions.show();
    }

    private void selectLegal(LegalCoin legalCoin) {
        mSelectCurrency.setText(legalCoin.getSymbol());
        mSelectIcon.setVisibility(View.GONE);
    }

    private void getLegalCoin() {
        Apic.getLegalCoin()
                .callback(new Callback<Resp<ArrayList<LegalCoin>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<LegalCoin>> resp) {
                        mLegalCoinArrayList = resp.getData();
                        showLegalCurrencySelect();
                    }
                }).fire();
    }

    public void setOnFilterListener(OnFilterListener onFilterListener) {
        mOnFilterListener = onFilterListener;
    }

    private void filter() {
        if (TextUtils.isEmpty(mCurrency.getText())) {
            ToastUtil.show(R.string.please_input_currency);
            return;
        }

        if (mSelectIcon.getVisibility() != View.GONE) {
            ToastUtil.show(R.string.please_select_currency);
            return;
        }

        if (!mBuyBtn.isSelected() && !mSellBtn.isSelected()) {
            ToastUtil.show(R.string.please_select_direction);
            return;
        }

        if (mOnFilterListener != null) {
            mOnFilterListener.onFilter(mCurrency.getText().toString(), mSelectCurrency.getText().toString(), mBuyBtn.isSelected() ? DIR_BUY : DIR_SELL);
        }
    }
}
