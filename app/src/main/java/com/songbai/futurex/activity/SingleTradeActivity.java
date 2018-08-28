package com.songbai.futurex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.fragment.TradeFragment;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.websocket.model.TradeDir;

public class SingleTradeActivity extends BaseActivity{

    private CurrencyPair mCurrencyPair;
    private int mDirection;
    private boolean mNotMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trade);
        translucentStatusBar();
        initData();
        loadFragment();
    }

    private void initData(){
        Intent intent = getIntent();
        mCurrencyPair = intent.getParcelableExtra(ExtraKeys.CURRENCY_PAIR);
        mDirection = intent.getIntExtra(ExtraKeys.TRADE_DIRECTION, TradeDir.DIR_BUY_IN);
        mNotMain= intent.getBooleanExtra(ExtraKeys.NOT_MAIN, false);
    }

    private void loadFragment(){
        TradeFragment tradeFragment = TradeFragment.newsInstance(mCurrencyPair,mDirection,mNotMain);
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView,tradeFragment).commit();
    }
}
