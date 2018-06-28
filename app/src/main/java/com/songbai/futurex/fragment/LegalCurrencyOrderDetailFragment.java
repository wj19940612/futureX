package com.songbai.futurex.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.OtcOrderDetail;
import com.songbai.futurex.model.WaresUserInfo;
import com.songbai.futurex.model.mine.BankCardBean;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.view.CountDownView;
import com.songbai.futurex.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/6/28
 */
public class LegalCurrencyOrderDetailFragment extends UniqueActivity.UniFragment {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.turnover)
    TextView mTurnover;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.tradeAmount)
    TextView mTradeAmount;
    @BindView(R.id.orderNo)
    TextView mOrderNo;
    @BindView(R.id.orderStatus)
    TextView mOrderStatus;
    @BindView(R.id.timer)
    CountDownView mTimer;
    @BindView(R.id.headPortrait)
    ImageView mHeadPortrait;
    @BindView(R.id.certification)
    ImageView mCertification;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.payInfo)
    RecyclerView mPayInfo;
    @BindView(R.id.countDealRate)
    TextView mCountDealRate;
    private int mOrderId;
    private int mTradeDirection;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_legal_currency_order_detail, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mOrderId = extras.getInt(ExtraKeys.ORDER_ID);
        mTradeDirection = extras.getInt(ExtraKeys.TRADE_DIRECTION);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        otcOrderDetail(mOrderId, mTradeDirection);
        otcWaresMine("", String.valueOf(mOrderId), 1);
        orderPayInfo();
    }

    private void otcOrderDetail(int id, int direct) {
        Apic.otcOrderDetail(id, direct)
                .callback(new Callback<Resp<OtcOrderDetail>>() {
                    @Override
                    protected void onRespSuccess(Resp<OtcOrderDetail> resp) {
                        setView(resp.getData());
                    }
                }).fire();
    }

    private void otcWaresMine(String waresId, String orderId, int orientation) {
        Apic.otcWaresMine(waresId, orderId, orientation)
                .callback(new Callback<Resp<WaresUserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<WaresUserInfo> resp) {
                        setWaresUserInfo(resp.getData());
                    }
                }).fire();
    }

    private void orderPayInfo() {
        Apic.orderPayInfo(mOrderId)
                .callback(new Callback<Resp<List<BankCardBean>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<BankCardBean>> resp) {
                        setBankList(resp.getData());
                    }
                }).fire();
    }

    private void setWaresUserInfo(WaresUserInfo waresUserInfo) {
        GlideApp
                .with(getContext())
                .load(waresUserInfo.getUserPortrait())
                .circleCrop()
                .into(mHeadPortrait);
        int authStatus = waresUserInfo.getAuthStatus();
        mCertification.setVisibility(authStatus == 1 || authStatus == 2 ? View.VISIBLE : View.GONE);
        mCertification.setImageResource(authStatus == 1 || authStatus == 2 ? R.drawable.ic_primary_star : R.drawable.ic_senior_star);
        mUserName.setText(waresUserInfo.getUsername());
        mCountDealRate.setText(getString(R.string.x_done_count_done_rate_x,
                waresUserInfo.getCountDeal(),
                FinanceUtil.formatToPercentage(waresUserInfo.getDoneRate())));
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
                FinanceUtil.formatWithScale(order.getOrderCount()),
                order.getCoinSymbol().toUpperCase()));
        mOrderNo.setText(order.getOrderId());
        GlideApp
                .with(getContext())
                .load(order.getBuyerPortrait())
                .circleCrop()
                .into(mHeadPortrait);
        mUserName.setText(order.getBuyerName());
        switch (order.getStatus()) {
            default:
        }
    }

    private void setBankList(List<BankCardBean> bankList) {
        mPayInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        BankListAdapter adapter = new BankListAdapter();
        adapter.setList(bankList);
        mPayInfo.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    class BankListAdapter extends RecyclerView.Adapter {

        private List<BankCardBean> mList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bank_list, parent, false);
            return new BankListHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof BankListHolder) {
                ((BankListHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(List<BankCardBean> list) {
            mList.clear();
            mList.addAll(list);
        }

        class BankListHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.payTypeIcon)
            ImageView mPayTypeIcon;
            @BindView(R.id.accountType)
            TextView mAccountType;
            @BindView(R.id.bankName)
            TextView mBankName;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.cardNum)
            TextView mCardNum;
            @BindView(R.id.bankBranch)
            TextView mBankBranch;

            public BankListHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindData(BankCardBean bankCardBean) {
                switch (bankCardBean.getPayType()) {
                    case BankCardBean.PAYTYPE_ALIPAY:
                        mPayTypeIcon.setImageResource(R.drawable.ic_pay_alipay_s);
                        mAccountType.setText(R.string.alipay);
                        break;
                    case BankCardBean.PAYTYPE_WX:
                        mPayTypeIcon.setImageResource(R.drawable.ic_pay_wechat_s);
                        mAccountType.setText(R.string.wechatpay);
                        break;
                    case BankCardBean.PAYTYPE_BANK:
                        mPayTypeIcon.setImageResource(R.drawable.ic_pay_unionpay_s);
                        mAccountType.setText(bankCardBean.getBankName());
                        break;
                    default:
                }
                String bankName = bankCardBean.getBankName();
                if (!TextUtils.isEmpty(bankName)) {
                    mBankName.setText(bankName);
                    mBankName.setVisibility(View.VISIBLE);
                } else {
                    mBankName.setVisibility(View.GONE);
                }
                String realName = bankCardBean.getRealName();
                if (!TextUtils.isEmpty(realName)) {
                    mName.setText(realName);
                    mName.setVisibility(View.VISIBLE);
                } else {
                    mName.setVisibility(View.GONE);
                }
                mCardNum.setText(bankCardBean.getCardNumber());
                String bankBranch = bankCardBean.getBankBranch();
                if (!TextUtils.isEmpty(bankBranch)) {
                    mBankBranch.setText(bankBranch);
                    mBankBranch.setVisibility(View.VISIBLE);
                } else {
                    mBankBranch.setVisibility(View.GONE);
                }
            }
        }
    }
}
