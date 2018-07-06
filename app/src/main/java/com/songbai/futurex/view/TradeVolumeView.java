package com.songbai.futurex.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.NumUtils;
import com.songbai.futurex.websocket.model.DeepData;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Modified by john on 2018/6/21
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class TradeVolumeView extends LinearLayout {

    private static final int DEFAULT_MAX_ROWS = 10;

    private View mTradeTitleView;
    private LinearLayout mBidPriceParent;
    private LinearLayout mAskPriceParent;

    private TextView mBidQuantity;
    private TextView mPrice;
    private TextView mQuantityAsk;

    private int mPriceScale;
    private int mMergeScale;
    private int mVolumeScale;
    private int mMaxRows;

    private List<DeepData> mBuyDeepList;
    private List<DeepData> mSellDeepList;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(double price, double volume);
    }

    public TradeVolumeView(Context context) {
        super(context);
        init();
    }

    public TradeVolumeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TradeVolumeView);

        mMaxRows = typedArray.getInt(R.styleable.TradeVolumeView_maxRows, DEFAULT_MAX_ROWS);

        typedArray.recycle();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
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

        for (int i = 0; i < mMaxRows; i++) {
            View view = new BidPriceView(getContext());
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof BidPriceView && !((BidPriceView) v).isEmptyValue()) {
                        String price = ((BidPriceView) v).getPrice();
                        String volume = ((BidPriceView) v).getVolume();
                        onItemClick(price, volume);
                    }
                }
            });
            mBidPriceParent.addView(view);
        }

        for (int i = 0; i < mMaxRows; i++) {
            View view = new AskPriceView(getContext());
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof AskPriceView && !((AskPriceView) v).isEmptyValue()) {
                        String price = ((AskPriceView) v).getPrice();
                        String volume = ((AskPriceView) v).getVolume();
                        onItemClick(price, volume);
                    }
                }
            });
            mAskPriceParent.addView(view);
        }
    }

    public double getAskPrice1() {
        View childAt = mAskPriceParent.getChildAt(0);
        if (childAt instanceof AskPriceView) {
            return NumUtils.getDouble(((AskPriceView) childAt).getPrice());
        }
        return 0;
    }

    public double getBidPrice1() {
        View childAt = mBidPriceParent.getChildAt(0);
        if (childAt instanceof BidPriceView) {
            return NumUtils.getDouble(((BidPriceView) childAt).getPrice());
        }
        return 0;
    }

    private void onItemClick(String price, String volume) {
        try {
            double p = Double.parseDouble(price);
            double v = Double.parseDouble(volume);
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(p, v);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
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
        mPriceScale = scale;
    }

    public void setMergeScale(int mergeScale) {
        mMergeScale = mergeScale;

        updateAskBidPriceViews(mBuyDeepList, mSellDeepList);
    }

    public void setVolumeScale(int scale) {
        mVolumeScale = scale;
    }

    public void setDeepList(List<DeepData> buyDeepList, List<DeepData> sellDeepList) {
        mBuyDeepList = buyDeepList;
        mSellDeepList = sellDeepList;

        updateAskBidPriceViews(mBuyDeepList, mSellDeepList);
    }

    public void reset() {
        if (mBuyDeepList != null && mSellDeepList != null) {
            mBuyDeepList.clear();
            mSellDeepList.clear();
        }
        updateAskBidPriceViews(mBuyDeepList, mSellDeepList);
    }

    private void updateAskBidPriceViews(List<DeepData> buyDeepList, List<DeepData> sellDeepList) {
        if (buyDeepList == null || sellDeepList == null) return;

        buyDeepList = mergeDeep(buyDeepList);
        sellDeepList = mergeDeep(sellDeepList);
        int scale = mMergeScale == mPriceScale ? mPriceScale : mMergeScale;

        if (mBidPriceParent != null && mBidPriceParent.getChildCount() > 0) {
            double maxVolume = 0;
            for (int i = 0; i < buyDeepList.size() && i < mMaxRows; i++) {
                DeepData deepData = buyDeepList.get(i);
                maxVolume = Math.max(maxVolume, deepData.getCount());
            }

            for (int i = 0; i < buyDeepList.size() && i < mMaxRows; i++) {
                DeepData deepData = buyDeepList.get(i);
                BidPriceView view = (BidPriceView) mBidPriceParent.getChildAt(i);
                view.setRank(i + 1);
                view.setPrice(NumUtils.getPrice(deepData.getPrice(), scale));
                view.setVolume(NumUtils.getVolume(deepData.getCount(), mVolumeScale));
                view.setValueAndMax(deepData.getCount(), maxVolume);
            }

            for (int i = buyDeepList.size(); i < mMaxRows; i++) {
                BidPriceView view = (BidPriceView) mBidPriceParent.getChildAt(i);
                if (!view.isEmptyValue()) {
                    view.setEmptyValue();
                }
            }
        }

        if (mAskPriceParent != null && mAskPriceParent.getChildCount() > 0) {
            double maxVolume = 0;
            for (int i = 0; i < sellDeepList.size() && i < mMaxRows; i++) {
                DeepData deepData = sellDeepList.get(i);
                maxVolume = Math.max(maxVolume, deepData.getCount());
            }

            for (int i = 0; i < sellDeepList.size() && i < mMaxRows; i++) {
                DeepData deepData = sellDeepList.get(i);
                AskPriceView view = (AskPriceView) mAskPriceParent.getChildAt(i);
                view.setRank(i + 1);
                view.setPrice(NumUtils.getPrice(deepData.getPrice(), scale));
                view.setVolume(NumUtils.getVolume(deepData.getCount(), mVolumeScale));
                view.setValueAndMax(deepData.getCount(), maxVolume);
            }

            for (int i = sellDeepList.size(); i < mMaxRows; i++) {
                AskPriceView view = (AskPriceView) mAskPriceParent.getChildAt(i);
                if (!view.isEmptyValue()) {
                    view.setEmptyValue();
                }
            }
        }
    }

    private List<DeepData> mergeDeep(List<DeepData> deepList) {
        if (mPriceScale == mMergeScale) {
            return deepList;
        }

        Map<String, DeepData> map = new LinkedHashMap<>();
        for (DeepData deepData : deepList) {
            double price = deepData.getPrice();
            String mergePrice = FinanceUtil.formatWithScale(price, mMergeScale, RoundingMode.DOWN);
            DeepData data = map.get(mergePrice);
            if (data == null) {
                data = new DeepData(mergePrice);
            }
            data.setCount(data.getCount() + deepData.getCount());
            data.setTotalCount(data.getTotalCount() + deepData.getTotalCount());
            map.put(mergePrice, data);
        }
        return new ArrayList<>(map.values());
    }
}
