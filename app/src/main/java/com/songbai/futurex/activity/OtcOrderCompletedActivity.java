package com.songbai.futurex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.fragment.legalcurrency.OtcSellUserInfoFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.OtcOrderDetail;
import com.songbai.futurex.model.WaresUserInfo;
import com.songbai.futurex.model.status.AuthenticationStatus;
import com.songbai.futurex.model.status.OTCOrderStatus;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/6/29
 */
public class OtcOrderCompletedActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.headPortrait)
    ImageView mHeadPortrait;
    @BindView(R.id.certification)
    ImageView mCertification;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.countDealRate)
    TextView mCountDealRate;
    @BindView(R.id.contractEachOther)
    TextView mContractEachOther;
    @BindView(R.id.turnover)
    TextView mTurnover;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.tradeAmount)
    TextView mTradeAmount;
    @BindView(R.id.orderNo)
    TextView mOrderNo;
    @BindView(R.id.orderTime)
    TextView mOrderTime;
    @BindView(R.id.orderStatus)
    TextView mOrderStatus;
    Unbinder unbinder;
    private int mOrderId;
    private int mTradeDirection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc_order_complated);
        unbinder = ButterKnife.bind(this);
        translucentStatusBar();
        setStatusBarDarkModeForM(true);
        ((BaseActivity) getActivity()).addStatusBarHeightPaddingTop(mTitleBar);
        Intent intent = getIntent();
        mOrderId = intent.getIntExtra(ExtraKeys.ORDER_ID, 0);
        mTradeDirection = intent.getIntExtra(ExtraKeys.TRADE_DIRECTION, 0);
        otcOrderDetail(mOrderId, mTradeDirection);
        otcWaresMine("", String.valueOf(mOrderId));
    }

    private void otcWaresMine(String waresId, String orderId) {
        Apic.otcWaresMine(waresId, orderId, 1).tag(TAG)
                .callback(new Callback<Resp<WaresUserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<WaresUserInfo> resp) {
                        setWaresUserInfo(resp.getData());
                    }
                }).fire();
    }

    private void otcOrderDetail(int id, int direct) {
        Apic.otcOrderDetail(id, direct).tag(TAG)
                .callback(new Callback<Resp<OtcOrderDetail>>() {
                    @Override
                    protected void onRespSuccess(Resp<OtcOrderDetail> resp) {
                        setView(resp.getData());
                    }
                }).fire();
    }

    private void setView(OtcOrderDetail otcOrderDetail) {
        OtcOrderDetail.OrderBean order = otcOrderDetail.getOrder();
        mTurnover.setText(getString(R.string.x_space_x,
                FinanceUtil.formatWithScale(order.getOrderAmount()),
                order.getPayCurrency().toUpperCase()));
        mPrice.setText(getString(R.string.order_detail_price_x,
                FinanceUtil.formatWithScale(order.getOrderPrice()),
                order.getPayCurrency().toUpperCase(), order.getCoinSymbol().toUpperCase()));
        mTradeAmount.setText(getString(R.string.order_detail_amount_x,
                order.getOrderCount(),
                order.getCoinSymbol().toUpperCase()));
        mOrderNo.setText(getString(R.string.pound_sign_x, order.getOrderId()));
        switch (order.getStatus()) {
            case OTCOrderStatus.ORDER_CANCLED:
                mOrderStatus.setText(R.string.canceled);
                mTitleBar.setTitle(R.string.canceled);
                break;
            case OTCOrderStatus.ORDER_COMPLATED:
                mOrderStatus.setText(R.string.completed);
                mTitleBar.setTitle(R.string.completed);
                break;
            default:
        }
        mOrderTime.setText(getString(R.string.order_time_with_symbol_x, DateUtil.format(order.getOrderTime(), DateUtil.FORMAT_SPECIAL_SLASH_ALL)));
    }

    private void setWaresUserInfo(WaresUserInfo waresUserInfo) {
        GlideApp
                .with(this)
                .load(waresUserInfo.getUserPortrait())
                .circleCrop()
                .into(mHeadPortrait);
        int authStatus = waresUserInfo.getAuthStatus();
        mCertification.setVisibility(authStatus > AuthenticationStatus.AUTHENTICATION_NONE ? View.VISIBLE : View.GONE);
        switch (authStatus) {
            case AuthenticationStatus.AUTHENTICATION_PRIMARY:
            case AuthenticationStatus.AUTHENTICATION_SENIOR_GOING:
            case AuthenticationStatus.AUTHENTICATION_SENIOR_FAIL:
                mCertification.setImageResource(R.drawable.ic_primary_star);
                break;
            case AuthenticationStatus.AUTHENTICATION_SENIOR:
                mCertification.setImageResource(R.drawable.ic_senior_star);
                break;
            default:
        }
        mUserName.setText(waresUserInfo.getUsername());
        mCountDealRate.setText(getString(R.string.x_done_count_done_rate_x,
                waresUserInfo.getCountDeal(),
                FinanceUtil.formatToPercentage(waresUserInfo.getDoneRate())));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.headPortrait, R.id.contractEachOther})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.headPortrait:
                UniqueActivity.launcher(this, OtcSellUserInfoFragment.class)
                        .putExtra(ExtraKeys.ORDER_ID, mOrderId)
                        .putExtra(ExtraKeys.TRADE_DIRECTION, mTradeDirection == OTCOrderStatus.ORDER_DIRECT_BUY ?
                                OTCOrderStatus.ORDER_DIRECT_SELL : OTCOrderStatus.ORDER_DIRECT_BUY)
                        .execute();
                break;
            case R.id.contractEachOther:
                Launcher.with(this, OtcTradeChatActivity.class)
                        .putExtra(ExtraKeys.ORDER_ID, mOrderId)
                        .putExtra(ExtraKeys.TRADE_DIRECTION, mTradeDirection)
                        .execute();
                break;
            default:
        }
    }
}
