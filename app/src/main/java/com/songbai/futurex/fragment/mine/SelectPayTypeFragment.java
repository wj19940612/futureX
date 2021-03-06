package com.songbai.futurex.fragment.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.BankCardBean;
import com.songbai.futurex.model.mine.BindBankList;
import com.songbai.futurex.utils.UmengCountEventId;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class SelectPayTypeFragment extends UniqueActivity.UniFragment {
    public static final int REQUEST_ADD_PAY = 12432;
    public static final int SELECT_PAY_RESULT = 12532;
    @BindView(R.id.bankCard)
    IconTextRow mBankCard;
    @BindView(R.id.aliPay)
    IconTextRow mAliPay;
    @BindView(R.id.wechatPay)
    IconTextRow mWechatPay;
    private Unbinder mBind;
    private BindBankList mBindBankList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_pay_type, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mBindBankList = extras.getParcelable(ExtraKeys.BIND_BANK_LIST);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        if (mBindBankList != null) {
            mAliPay.setVisibility(mBindBankList.getAliPay().getBind() == BankCardBean.ALIPAY_WECHATPAY_BIND ? View.GONE : View.VISIBLE);
            mWechatPay.setVisibility(mBindBankList.getWechat().getBind() == BankCardBean.ALIPAY_WECHATPAY_BIND ? View.GONE : View.VISIBLE);
            mBankCard.setVisibility(mBindBankList.getBankCard().size() >= 5 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_PAY) {
            if (data != null) {
                boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, false);
                if (shouldRefresh) {
                    LocalUser user = LocalUser.getUser();
                    if (user.isLogin()) {
                        UserInfo userInfo = user.getUserInfo();
                        int payment = userInfo.getPayment();
                        if (payment < 1) {
                            userInfo.setPayment(1);
                            LocalUser.getUser().setUserInfo(userInfo);
                        }
                    }
                    setResult(SELECT_PAY_RESULT, new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
                    finish();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.bankCard, R.id.aliPay, R.id.wechatPay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bankCard:
                umengEventCount(UmengCountEventId.LEGALPAY0003);
                UniqueActivity.launcher(this, AddBankingCardFragment.class)
                        .execute(this, REQUEST_ADD_PAY);
                break;
            case R.id.aliPay:
                UniqueActivity.launcher(this, AddPayFragment.class)
                        .putExtra(ExtraKeys.IS_ALIPAY, true)
                        .execute(this, REQUEST_ADD_PAY);
                break;
            case R.id.wechatPay:
                UniqueActivity.launcher(this, AddPayFragment.class)
                        .putExtra(ExtraKeys.IS_ALIPAY, false)
                        .execute(this, REQUEST_ADD_PAY);
                break;
            default:
        }
    }
}
