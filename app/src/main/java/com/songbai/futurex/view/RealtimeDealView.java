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

/**
 * Modified by john on 2018/6/21
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class RealtimeDealView extends LinearLayout {

    private View mDealHeader;

    private TextView mHeaderPrice;
    private TextView mHeaderVolume;

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

        mDealHeader = LayoutInflater.from(getContext()).inflate(R.layout.view_deal_header, this, false);
        addView(mDealHeader);

        mHeaderPrice = mDealHeader.findViewById(R.id.headerPrice);
        mHeaderVolume = mDealHeader.findViewById(R.id.headerVolume);

        for (int i = 0; i < 20; i++) {
            View view = new DealFlowView(getContext());
            if (i % 2 == 0) {
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgF5));
            }
            addView(view);
        }
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        mHeaderPrice.setText(getContext().getString(R.string.price_x,
                currencyPair.getSuffixSymbol().toUpperCase()));
        mHeaderVolume.setText(getContext().getString(R.string.volume_x,
                currencyPair.getPrefixSymbol().toUpperCase()));
    }
}
