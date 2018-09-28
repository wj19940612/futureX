package com.songbai.futurex.fragment.mine;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.CoinAddressCount;
import com.songbai.futurex.utils.PermissionUtil;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.zxing.activity.CaptureActivity;

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
    public static final int CODE_SCAN = 10123;
    public static final int REQ_CODE_PERMISSION = 10000;

    @BindView(R.id.address)
    EditText mAddress;
    @BindView(R.id.remark)
    EditText mRemark;
    @BindView(R.id.confirm)
    TextView mConfirm;
    @BindView(R.id.scanWithDraw)
    ImageView mScanWithDraw;

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
        mRemark.setText(mCoinAddressCount.getCoinType().toUpperCase() + "-Address Name");
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

    @OnClick({R.id.confirm, R.id.scanWithDraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                mRemakText = mRemark.getText().toString();
                addDrawWalletAddrByCoinType(mCoinAddressCount.getCoinType(), mAddressText, mRemakText);
                break;
            case R.id.scanWithDraw:
                scanWithDraw();
                break;
        }
    }

    private void scanWithDraw() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasSelfPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.VIBRATE})) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent, CODE_SCAN);
            } else {
                requestPermission(getActivity(), REQ_CODE_PERMISSION, new String[]{Manifest.permission.CAMERA}, new PermissionCallback() {

                    @Override
                    public void onPermissionGranted(int requestCode) {
                        Log.e("zzz", "onPermissionGranted");
                        if (requestCode == REQ_CODE_PERMISSION) {
                            Intent intent = new Intent(getActivity(), CaptureActivity.class);
                            startActivityForResult(intent, CODE_SCAN);
                        }
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
                        Log.e("zzz", "onPermissionDenied");
                        if (requestCode == REQ_CODE_PERMISSION) {
                            ToastUtil.show(R.string.please_open_camera_permission);
                        }
                    }
                });
            }
        } else {
            if (PermissionUtil.cameraIsCanUse()) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent, CODE_SCAN);
            } else {
                ToastUtil.show(R.string.please_open_camera_permission);
            }
        }
    }

    private void addDrawWalletAddrByCoinType(String coinType, String toAddr, String remark) {
        Apic.addDrawWalletAddrByCoinType(coinType, toAddr, remark).tag(TAG)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_SCAN && resultCode == Activity.RESULT_OK && data != null) {
            String result = data.getStringExtra(CaptureActivity.RESULT);
            checkWithDrawResult(result);
        }
    }

    private void checkWithDrawResult(final String result) {
        Apic.judgeAddress(mCoinAddressCount.getCoinType(), result).tag(TAG).callback(new Callback<Resp<Boolean>>() {

            @Override
            protected void onRespSuccess(Resp resp) {
                if ((resp.getData() != null) && (Boolean) resp.getData()) {
                    mAddress.setText(result);
                } else {
                    showErrorAddress();
                }
            }

            @Override
            protected void onRespFailure(Resp failedResp) {
//                super.onRespFailure(failedResp);
                showErrorAddress();
            }
        }).fireFreely();
    }

    private void showErrorAddress() {
        ToastUtil.show(R.string.invalid_qr_code);
    }


}
