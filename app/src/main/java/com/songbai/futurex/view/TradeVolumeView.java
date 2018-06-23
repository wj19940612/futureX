package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.utils.NumUtils;
import com.songbai.futurex.websocket.model.DeepData;

import java.util.List;

/**
 * Modified by john on 2018/6/21
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class TradeVolumeView extends LinearLayout {

    private View mTradeTitleView;
    private LinearLayout mBidPriceParent;
    private LinearLayout mAskPriceParent;

    private TextView mBidQuantity;
    private TextView mPrice;
    private TextView mQuantityAsk;

    private int mScale;

    public TradeVolumeView(Context context) {
        super(context);
        init();
    }

    public TradeVolumeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        mTradeTitleView = LayoutInflater.from(getContext()).inflate(R.layout.view_trade_header, this, false);
        addView(mTradeTitleView);

        mBidQuantity = mTradeTitleView.findViewById(R.id.bidQuantity);
        mPrice = mTradeTitleView.findViewById(R.id.price);
        mQuantityAsk = mTradeTitleView.findViewById(R.id.quantityAsk);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(layout, params);

        mBidPriceParent = new LinearLayout(getContext());
        mBidPriceParent.setOrientation(VERTICAL);
        params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        mAskPriceParent = new LinearLayout(getContext());
        mAskPriceParent.setOrientation(VERTICAL);
        params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        layout.addView(mBidPriceParent, params);
        layout.addView(mAskPriceParent, params);

        for (int i = 0; i < 10; i++) {
            View view = new BidPriceView(getContext());
            view.setVisibility(GONE);
            mBidPriceParent.addView(view);
        }

        for (int i = 0; i < 10; i++) {
            View view = new AskPriceView(getContext());
            view.setVisibility(GONE);
            mAskPriceParent.addView(view);
        }
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        mBidQuantity.setText(getContext().getString(R.string.bid_volume_x,
                currencyPair.getPrefixSymbol().toUpperCase()));
        mPrice.setText(getContext().getString(R.string.price_x,
                currencyPair.getSuffixSymbol().toUpperCase()));
        mQuantityAsk.setText(getContext().getString(R.string.volume_x_ask,
                currencyPair.getPrefixSymbol().toUpperCase()));
    }

    public void setPriceScale(int scale) {
        mScale = scale;
    }

    public void setDeepList(List<DeepData> buyDeepList, List<DeepData> sellDeepList) {
        if (mBidPriceParent != null && mBidPriceParent.getChildCount() > 0) {
            double maxVolume = 0;
            for (int i = 0; i < buyDeepList.size() && i < 10; i++) {
                DeepData deepData = buyDeepList.get(i);
                maxVolume = Math.max(maxVolume, deepData.getCount());
            }

            for (int i = 0; i < buyDeepList.size() && i < 10; i++) {
                DeepData deepData = buyDeepList.get(i);
                BidPriceView view = (BidPriceView) mBidPriceParent.getChildAt(i);
                if (view.getVisibility() == GONE) {
                    view.setVisibility(VISIBLE);
                }
                view.setRank(i + 1);
                view.setPrice(NumUtils.getPrice(deepData.getPrice()));
                view.setVolume(NumUtils.getVolume(deepData.getCount()));
                view.setValueAndMax(deepData.getCount(), maxVolume);
            }

            for (int i = buyDeepList.size(); i < 10; i++) {
                BidPriceView view = (BidPriceView) mBidPriceParent.getChildAt(i);
                if (view.getVisibility() == VISIBLE) {
                    view.setVisibility(GONE);
                }
            }
        }

        if (mAskPriceParent != null && mAskPriceParent.getChildCount() > 0) {
            double maxVolume = 0;
            for (int i = 0; i < sellDeepList.size() && i < 10; i++) {
                DeepData deepData = sellDeepList.get(i);
                maxVolume = Math.max(maxVolume, deepData.getCount());
            }

            for (int i = 0; i < sellDeepList.size() && i < 10; i++) {
                DeepData deepData = sellDeepList.get(i);
                AskPriceView view = (AskPriceView) mAskPriceParent.getChildAt(i);
                if (view.getVisibility() == GONE) {
                    view.setVisibility(VISIBLE);
                }
                view.setRank(i + 1);
                view.setPrice(NumUtils.getPrice(deepData.getPrice()));
                view.setVolume(NumUtils.getVolume(deepData.getCount()));
                view.setValueAndMax(deepData.getCount(), maxVolume);
            }

            for (int i = sellDeepList.size(); i < 10; i++) {
                AskPriceView view = (AskPriceView) mAskPriceParent.getChildAt(i);
                if (view.getVisibility() == VISIBLE) {
                    view.setVisibility(GONE);
                }
            }
        }
    }
}
