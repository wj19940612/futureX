package com.songbai.futurex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.fragment.TradeFragment;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.websocket.model.TradeDir;

public class SingleTradeActivity extends BaseActivity{

    private CurrencyPair mCurrencyPair;
    private int mDirection;
    private boolean mNotMain;

    private boolean mShouldHideInputMethod;
    private View mLastFocusView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trade);
//        translucentStatusBar();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initData();
        loadFragment();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            mLastFocusView = v;
            if (KeyBoardUtils.isShouldHideKeyboard(v, ev)) {
                mShouldHideInputMethod = true;
                mLastFocusView.clearFocus();
            }
            return super.dispatchTouchEvent(ev);
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (mShouldHideInputMethod && !(v instanceof EditText)) {
                KeyBoardUtils.closeKeyboard(mLastFocusView);
                mShouldHideInputMethod = false;
            }
            return super.dispatchTouchEvent(ev);
        } else if (ev.getAction() == MotionEvent.ACTION_CANCEL) {
            mShouldHideInputMethod = false;
            return super.dispatchTouchEvent(ev);
        }
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
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
