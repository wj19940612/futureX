package com.songbai.futurex.view;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    public HistoryFilter(View view) {
//        this.mView = view;
//        ButterKnife.bind(this, view);
    }

    @OnClick({R.id.selectCurrencyLayout, R.id.selectCurrency, R.id.buyBtn, R.id.sellBtn, R.id.has_finish, R.id.has_withdrawn,R.id.reset})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.selectCurrencyLayout:
                showSelectCurrencyDialog();
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
        }
    }

    private void showSelectCurrencyDialog() {

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

    private void resetAll(){
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
}
