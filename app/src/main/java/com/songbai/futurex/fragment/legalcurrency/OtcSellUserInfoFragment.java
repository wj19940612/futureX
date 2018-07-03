package com.songbai.futurex.fragment.legalcurrency;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.WaresUserInfo;
import com.songbai.futurex.utils.FinanceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/6/29
 */
public class OtcSellUserInfoFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.headPortrait)
    ImageView mHeadPortrait;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.authenticationStatus)
    TextView mAuthenticationStatus;
    @BindView(R.id.countDealRate)
    TextView mCountDealRate;
    @BindView(R.id.primaryCertification)
    RelativeLayout mPrimaryCertification;
    @BindView(R.id.seniorCertification)
    RelativeLayout mSeniorCertification;
    @BindView(R.id.mailCertification)
    RelativeLayout mMailCertification;
    @BindView(R.id.phoneCertification)
    RelativeLayout mPhoneCertification;
    private Unbinder mBind;
    private int mOrderId;
    private int mTradeDirection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otc_sell_user_info, container, false);
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
        otcWaresMine("", mOrderId, mTradeDirection);
    }

    private void otcWaresMine(String waresId, int orderId, int orientation) {
        Apic.otcWaresMine(waresId, orderId, orientation)
                .callback(new Callback<Resp<WaresUserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<WaresUserInfo> resp) {
                        setWaresUserInfo(resp.getData());
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
        switch (authStatus) {
            case 1:
                mAuthenticationStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_primary_star, 0, 0, 0);
                mAuthenticationStatus.setText(R.string.primary_certification);
                mSeniorCertification.setSelected(false);
                mPrimaryCertification.setSelected(true);
                break;
            case 2:
                mAuthenticationStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_senior_star, 0, 0, 0);
                mAuthenticationStatus.setText(R.string.senior_certification);
                mSeniorCertification.setSelected(true);
                mPrimaryCertification.setSelected(true);
                break;
            default:
        }
        if (waresUserInfo.getBindPhone() == 1) {
            mPhoneCertification.setSelected(true);
        }
        if (waresUserInfo.getBindEmail() == 1) {
            mMailCertification.setSelected(true);
        }
        mUserName.setText(waresUserInfo.getUsername());
        mCountDealRate.setText(getString(R.string.x_done_count_done_rate_x,
                waresUserInfo.getCountDeal(),
                FinanceUtil.formatToPercentage(waresUserInfo.getDoneRate())));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }
}
