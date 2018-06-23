package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.utils.NumUtils;
import com.songbai.futurex.websocket.model.DealData;

import java.util.LinkedList;
import java.util.List;

/**
 * Modified by john on 2018/6/21
 * <p>
 * Description: 实时成交显示控件
 * <p>
 * APIs:
 */
public class RealtimeDealView extends LinearLayout {

    private static final int MAX_CAPACITY = 20;

    private View mDealHeader;

    private TextView mHeaderPrice;
    private TextView mHeaderVolume;

    private LinearLayout mDealFlowParent;

    private List<DealData> mDealDataList;
    private int mPriceScale;

    public RealtimeDealView(Context context) {
        super(context);
        init();
    }

    public RealtimeDealView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        mDealDataList = new LinkedList<>();

        mDealHeader = LayoutInflater.from(getContext()).inflate(R.layout.view_deal_header, this, false);
        addView(mDealHeader);

        mHeaderPrice = mDealHeader.findViewById(R.id.headerPrice);
        mHeaderVolume = mDealHeader.findViewById(R.id.headerVolume);

        mDealFlowParent = new LinearLayout(getContext());
        mDealFlowParent.setOrientation(VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mDealFlowParent, params);

        for (int i = 0; i < MAX_CAPACITY; i++) {
            View view = new DealFlowView(getContext());
            if (i % 2 == 0) {
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgF5));
            }
            mDealFlowParent.addView(view);
        }
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        mHeaderPrice.setText(getContext().getString(R.string.price_x,
                currencyPair.getSuffixSymbol().toUpperCase()));
        mHeaderVolume.setText(getContext().getString(R.string.volume_x,
                currencyPair.getPrefixSymbol().toUpperCase()));
    }

    public void addDealData(List<DealData> dealDataList) {
        if (dealDataList == null) return;

        mDealDataList.addAll(0, dealDataList);
        int removeCount = mDealDataList.size() - MAX_CAPACITY;
        if (removeCount > 0) {
            while (removeCount > 0) {
                mDealDataList.remove(mDealDataList.size() - 1);
                removeCount--;
            }
        }

        updateView();
    }

    private void updateView() {
        for (int i = 0; i < mDealDataList.size() && i < MAX_CAPACITY; i++) {
            View childAt = mDealFlowParent.getChildAt(i);
            if (childAt instanceof DealFlowView) {
                childAt.setVisibility(VISIBLE);
                DealData dealData = mDealDataList.get(i);
                ((DealFlowView) childAt).setDirection(dealData.getDirection());
                ((DealFlowView) childAt).setPrice(NumUtils.getPrice(dealData.getLastPrice(), mPriceScale));
                ((DealFlowView) childAt).setTime(dealData.getUpTime());
                ((DealFlowView) childAt).setVolume(NumUtils.getVolume(dealData.getLastVolume()));
            }
        }

        for (int i = mDealDataList.size(); i < MAX_CAPACITY; i++) {
            View childAt = mDealFlowParent.getChildAt(i);
            childAt.setVisibility(GONE);
        }
    }

    public void setPriceScale(int priceScale) {
        mPriceScale = priceScale;
    }
}
