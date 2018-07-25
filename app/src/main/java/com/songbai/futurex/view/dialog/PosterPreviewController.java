package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.OtcWarePoster;
import com.songbai.futurex.model.mine.BankCardBean;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.view.SmartDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class PosterPreviewController extends SmartDialog.CustomViewController {
    @BindView(R.id.close)
    ImageView mClose;
    @BindView(R.id.treadContent)
    TextView mTreadContent;
    @BindView(R.id.priceContent)
    TextView mPriceContent;
    @BindView(R.id.treadLimit)
    TextView mTreadLimit;
    @BindView(R.id.treadAmount)
    TextView mTreadAmount;
    @BindView(R.id.payType)
    TextView mPayType;
    @BindView(R.id.remark)
    TextView mRemark;
    @BindView(R.id.demand)
    TextView mDemand;
    @BindView(R.id.tel)
    TextView mTel;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private OtcWarePoster mOtcWarePoster;

    public PosterPreviewController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_tread_poster_preview, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onInitView(final View view, final SmartDialog dialog) {
        if (mOtcWarePoster != null) {
            setView(mOtcWarePoster);
        }
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void setOtcWarePoster(OtcWarePoster otcWarePoster) {
        mOtcWarePoster = otcWarePoster;
    }

    private void setView(OtcWarePoster otcWarePoster) {
        switch (otcWarePoster.getDealType()) {
            case OtcWarePoster.DEAL_TYPE_BUY:
                mTreadContent.setText(mContext.getString(R.string.buy_symbol_x,
                        otcWarePoster.getCoinSymbol().toUpperCase()));
                break;
            case OtcWarePoster.DEAL_TYPE_SELL:
                mTreadContent.setText(mContext.getString(R.string.sell_symbol_x,
                        otcWarePoster.getCoinSymbol().toUpperCase()));
                break;
            default:
        }
        switch (otcWarePoster.getPriceType()) {
            case OtcWarePoster.FIXED_PRICE:
                mPriceContent.setText(mContext.getString(R.string.fixed_price_x, FinanceUtil.trimTrailingZero(otcWarePoster.getFixedPrice())));
                break;
            case OtcWarePoster.FLOATING_PRICE:
                mPriceContent.setText(mContext.getString(R.string.floating_price_x_symbol, FinanceUtil.trimTrailingZero(otcWarePoster.getPercent())));
                break;
            default:
        }
        mTreadLimit.setText(mContext.getString(R.string.tread_limit_min_max_symbol_x, FinanceUtil.trimTrailingZero(otcWarePoster.getMinTurnover()), FinanceUtil.trimTrailingZero(otcWarePoster.getMaxTurnover()), otcWarePoster.getPayCurrency().toUpperCase()));
        mTreadAmount.setText(String.valueOf(otcWarePoster.getTradeCount()));
        setPayInfo(otcWarePoster.getBankList());
        mRemark.setText(otcWarePoster.getRemark());
        String telephone = otcWarePoster.getTelephone();
        if (!TextUtils.isEmpty(telephone)) {
            mTel.setText(otcWarePoster.getAreaCode() + "-" + telephone);
        }
        setDemand(otcWarePoster);
    }

    private void setDemand(OtcWarePoster waresPreview) {
        String conditionType = waresPreview.getConditionType();
        String conditionValue = waresPreview.getConditionValue();
        if (TextUtils.isEmpty(conditionType)) {
            return;
        }
        String[] types = conditionType.split(",");
        String[] values = conditionValue.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            sb.append((i + 1));
            sb.append(".");
            String type = types[i];
            if (type.equals(OtcWarePoster.CONDITION_AUTH)) {
                if ("1".equals(values[i])) {
                    sb.append(mContext.getString(R.string.passed_primary_certification));
                } else if ("2".equals(values[i])) {
                    sb.append(mContext.getString(R.string.passed_advanced_certification));
                }
            }
            if (type.equals(OtcWarePoster.CONDITION_TRADE)) {
                sb.append(mContext.getString(R.string.deal_count_more_than_x, values[i]));
            }
            sb.append(" ");
        }
        if (sb.length() > 0) {
            mDemand.setText(sb.toString());
        }
    }

    private void setPayInfo(List<BankCardBean> bankListBeans) {
        if (bankListBeans != null && !bankListBeans.isEmpty()) {
            StringBuilder payInfo = new StringBuilder();
            for (BankCardBean bankCardBean : bankListBeans) {
                switch (bankCardBean.getPayType()) {
                    case BankCardBean.PAYTYPE_ALIPAY:
                        payInfo.append(mContext.getString(R.string.alipay));
                        payInfo.append(" ");
                        break;
                    case BankCardBean.PAYTYPE_WX:
                        payInfo.append(mContext.getString(R.string.weichat));
                        payInfo.append(" ");
                        break;
                    case BankCardBean.PAYTYPE_BANK:
                        payInfo.append(mContext.getString(R.string.bank_card));
                        payInfo.append(" ");
                        break;
                    default:
                }
            }
            mPayType.setText(payInfo.subSequence(0, payInfo.length() - 1));
        }
    }

    @OnClick(R.id.confirm)
    public void onViewClicked() {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onConfirmClick();
        }
    }

    public interface OnItemClickListener {
        void onConfirmClick();
    }

    public PosterPreviewController setOnItemClickListener(OnItemClickListener
                                                                  onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }
}
