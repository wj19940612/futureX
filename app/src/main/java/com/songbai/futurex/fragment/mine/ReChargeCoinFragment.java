package com.songbai.futurex.fragment.mine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.songbai.futurex.model.mine.DrawLimit;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ZXingUtils;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class ReChargeCoinFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.qcCode)
    ImageView mQcCode;
    @BindView(R.id.address)
    TextView mAddress;
    @BindView(R.id.rechargeCoinRule)
    TextView mRechargeCoinRule;
    private Unbinder mBind;
    private String mCoinType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recharge_coin, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mCoinType = extras.getString(ExtraKeys.COIN_TYPE);

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        getDepositWalletAddrByCoinType(mCoinType);
        getCoinTypeDrawLimit(mCoinType);
    }

    public void getDepositWalletAddrByCoinType(String coinType) {
        Apic.getDepositWalletAddrByCoinType(coinType).tag(TAG)
                .callback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        String url = resp.getData();
                        mAddress.setText(url);
                        final Bitmap bitmap = ZXingUtils.createQRImage(url, mQcCode.getMeasuredWidth(), mQcCode.getMeasuredHeight());
                        mQcCode.setImageBitmap(bitmap);
                        mQcCode.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                ImageUtils.saveImageToGallery(getContext(), bitmap);
                                ToastUtil.show(R.string.save_success);
                                return true;
                            }
                        });
                    }
                })
                .fire();
    }

    private void getCoinTypeDrawLimit(String coinType) {
        Apic.getCoinTypeDrawLimit(coinType).tag(TAG)
                .callback(new Callback<Resp<DrawLimit>>() {
                    @Override
                    protected void onRespSuccess(Resp<DrawLimit> resp) {
                        DrawLimit drawLimit = resp.getData();
                        mRechargeCoinRule.setText(getString(R.string.recharge_coin_rules,
                                mCoinType.toUpperCase(),
                                drawLimit.getConfirm(),
                                getString(R.string.amount_space_coin_x,
                                        FinanceUtil.subZeroAndDot(drawLimit.getMinWithdrawAmount(),8), mCoinType.toUpperCase())));
                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.copyAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.copyAddress:
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setPrimaryClip(ClipData.newPlainText(null, mAddress.getText()));
                ToastUtil.show(R.string.copy_success);
                break;
            default:
        }
    }
}
