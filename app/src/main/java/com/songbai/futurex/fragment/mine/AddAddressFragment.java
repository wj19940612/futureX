package com.songbai.futurex.fragment.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.CoinAddressCount;
import com.songbai.futurex.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class AddAddressFragment extends UniqueActivity.UniFragment {
    public static final int ADD_ADDRESS_RESULT = 12342;
    @BindView(R.id.address)
    EditText mAddress;
    @BindView(R.id.remark)
    EditText mRemark;
    @BindView(R.id.confirm)
    TextView mConfirm;
    private Unbinder mBind;
    private CoinAddressCount mCoinAddressCount;
    private String mAddressText;
    private String mRemakText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_address, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mCoinAddressCount = extras.getParcelable(ExtraKeys.COIN_ADDRESS_INFO);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mAddress.addTextChangedListener(mWatcher);
    }

    private ValidationWatcher mWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mAddressText = mAddress.getText().toString().trim();
            boolean enable = !TextUtils.isEmpty(mAddressText);
            mConfirm.setEnabled(enable);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick(R.id.confirm)
    public void onViewClicked() {
        addDrawWalletAddrByCoinType(mCoinAddressCount.getCoinType(), mAddressText, mRemakText);
    }

    private void addDrawWalletAddrByCoinType(String coinType, String toAddr, String remark) {
        Apic.addDrawWalletAddrByCoinType(coinType, toAddr, remark)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        setResult(ADD_ADDRESS_RESULT,
                                new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
                        finish();
                    }
                })
                .fire();
    }
}
