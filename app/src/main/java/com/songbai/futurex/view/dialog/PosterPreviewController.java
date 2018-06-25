package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.OtcWarePoster;
import com.songbai.futurex.model.WaresPreview;
import com.songbai.futurex.view.SmartDialog;

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
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private String mId;

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
        Apic.otcWaresGet(mId)
                .callback(new Callback<Resp<WaresPreview>>() {
                    @Override
                    protected void onRespSuccess(Resp<WaresPreview> resp) {
                        setView(resp.getData());
                    }
                })
                .fire();
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setView(WaresPreview otcWaresAdd) {
        switch (otcWaresAdd.getDealType()) {
            case OtcWarePoster.DEAL_TYPE_BUY:
                mTreadContent.setText(mContext.getString(R.string.buy_symbol_x,
                        otcWaresAdd.getCoinSymbol().toUpperCase()));
                break;
            case OtcWarePoster.DEAL_TYPE_SELL:
                mTreadContent.setText(mContext.getString(R.string.sell_symbol_x,
                        otcWaresAdd.getCoinSymbol().toUpperCase()));
                break;
            default:
        }
        switch (otcWaresAdd.getPriceType()) {
            case OtcWarePoster.FIXED_PRICE:
                mPriceContent.setText(mContext.getString(R.string.fixed_price_x, String.valueOf(otcWaresAdd.getFixedPrice())));
                break;
            case OtcWarePoster.FLOATING_PRICE:
                mPriceContent.setText(mContext.getString(R.string.floating_price_x, String.valueOf(otcWaresAdd.getPercent())));
                break;
            default:
        }
        mTreadLimit.setText(otcWaresAdd.getMinTurnover() + "-" + otcWaresAdd.getMaxTurnover() + otcWaresAdd.getPayCurrency().toUpperCase());
        mTreadAmount.setText(String.valueOf(otcWaresAdd.getTradeCount()));
        mRemark.setText(otcWaresAdd.getRemark());
    }

    @OnClick(R.id.confirm)
    public void onViewClicked() {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onConfirmClick();
        }
    }

    public void setPosterId(String id) {
        mId = id;
    }

    public interface OnItemClickListener {
        void onConfirmClick();
    }

    public PosterPreviewController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }
}
