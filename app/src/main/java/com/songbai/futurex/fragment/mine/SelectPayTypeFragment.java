package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
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
    @BindView(R.id.bankCard)
    IconTextRow mBankCard;
    @BindView(R.id.aliPay)
    IconTextRow mAliPay;
    @BindView(R.id.wechatPay)
    IconTextRow mWechatPay;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_pay_type, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {

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
                UniqueActivity.launcher(this, AddBankingCardFragment.class).execute();
                break;
            case R.id.aliPay:
                UniqueActivity.launcher(this, AddPayFragment.class).putExtra(ExtraKeys.IS_ALIPAY,true).execute();
                break;
            case R.id.wechatPay:
                UniqueActivity.launcher(this, AddPayFragment.class).putExtra(ExtraKeys.IS_ALIPAY,false).execute();
                break;
            default:
        }
    }
}