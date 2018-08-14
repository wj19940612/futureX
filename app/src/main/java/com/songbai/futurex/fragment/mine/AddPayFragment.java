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
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.dialog.UploadUserImageDialogFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.BankBindData;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.AuthenticationName;
import com.songbai.futurex.model.mine.BankCardBean;
import com.songbai.futurex.model.mine.BindBankList;
import com.songbai.futurex.utils.Display;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.utils.image.ImageUtils;
import com.songbai.futurex.view.PasswordEditText;
import com.songbai.futurex.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/6/4
 */
public class AddPayFragment extends UniqueActivity.UniFragment {
    public static final int ADD_PAY_RESULT = 12343;

    @BindView(R.id.accountName)
    TextView mAccountName;
    @BindView(R.id.accountNum)
    EditText mAccountNum;
    @BindView(R.id.realName)
    EditText mRealName;
    @BindView(R.id.confirm_add)
    TextView mConfirmAdd;
    @BindView(R.id.withDrawPass)
    PasswordEditText mWithDrawPass;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    Unbinder unbinder;
    @BindView(R.id.nameText)
    TextView mNameText;
    @BindView(R.id.withDrawPassText)
    TextView mWithDrawPassText;
    @BindView(R.id.paymentCode)
    ImageView mPaymentCode;
    @BindView(R.id.paymentCodeText)
    TextView mPaymentCodeText;
    @BindView(R.id.deleteImage)
    TextView mDeleteImage;
    @BindView(R.id.uploadPaymentCode)
    TextView mUploadPaymentCode;
    private boolean mIsAlipay;
    private String mName;
    private BindBankList mBindBankList;
    private boolean mHasBind;
    private String mImage = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_pay, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mIsAlipay = extras.getBoolean(ExtraKeys.IS_ALIPAY);
        mBindBankList = extras.getParcelable(ExtraKeys.BIND_BANK_LIST);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        authenticationName();
        LocalUser user = LocalUser.getUser();
        if (user.isLogin()) {
            String realName = user.getUserInfo().getRealName();
            if (!TextUtils.isEmpty(realName)) {
                mRealName.setText(realName);
                setUnEditable(mRealName);
            }
        }
        if (mIsAlipay) {
            mTitleBar.setTitle(R.string.add_ali_pay);
            mAccountName.setText(R.string.ali_pay_account);
            mAccountNum.setHint(R.string.please_input_ali_pay_account);
            if (mBindBankList != null) {
                String cardNumber = mBindBankList.getAliPay().getCardNumber();
                if (!TextUtils.isEmpty(cardNumber)) {
                    mTitleBar.setTitle(R.string.edit);
                    mHasBind = true;
                }
                mAccountNum.setText(mBindBankList.getAliPay().getCardNumber());
                otcBankAccount(mBindBankList.getAliPay().getId());
            }
            mPaymentCodeText.setText(R.string.alipay_payment_code);
        } else {
            mTitleBar.setTitle(R.string.add_wechat_pay);
            mAccountName.setText(R.string.wechat_account);
            mAccountNum.setHint(R.string.please_input_wechat_account);
            if (mBindBankList != null) {
                String cardNumber = mBindBankList.getWechat().getCardNumber();
                if (!TextUtils.isEmpty(cardNumber)) {
                    mTitleBar.setTitle(R.string.edit);
                    mHasBind = true;
                }
                mAccountNum.setText(cardNumber);
                otcBankAccount(mBindBankList.getWechat().getId());
            }
            mPaymentCodeText.setText(R.string.wechat_payment_code);
        }
        setPaymentCodeBtn();
        mAccountNum.addTextChangedListener(mWatcher);
        mWithDrawPass.getEditText().addTextChangedListener(mWatcher);
    }

    private ValidationWatcher mWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String accountNum = mAccountNum.getText().toString().trim();
            String mWithDrawPassPassword = mWithDrawPass.getPassword();
            if (TextUtils.isEmpty(accountNum) || TextUtils.isEmpty(mWithDrawPassPassword)) {
                mConfirmAdd.setEnabled(false);
            } else {
                mConfirmAdd.setEnabled(true);
            }
        }
    };

    private void setUnEditable(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
    }

    private void authenticationName() {
        Apic.authenticationName().tag(TAG)
                .callback(new Callback<Resp<AuthenticationName>>() {
                    @Override
                    protected void onRespSuccess(Resp<AuthenticationName> resp) {
                        mName = resp.getData().getName();
                        mRealName.setText(mName);
                    }
                })
                .fire();
    }

    private void otcBankAccount(int id) {
        Apic.otcBankAccount(id).tag(TAG)
                .callback(new Callback<Resp<BankCardBean>>() {
                    @Override
                    protected void onRespSuccess(Resp<BankCardBean> resp) {
                        BankCardBean bankCardBean = resp.getData();
                        if (bankCardBean != null) {
                            mName = bankCardBean.getRealName();
                            mRealName.setText(mName);
                            mAccountNum.setText(bankCardBean.getCardNumber());
                            mImage = bankCardBean.getPayPic();
                            setImage(mImage);
                            setPaymentCodeBtn();
                        }
                    }
                })
                .fire();
    }

    private void setPaymentCodeBtn() {
        ViewGroup.LayoutParams layoutParams = mPaymentCode.getLayoutParams();
        layoutParams.width = (int) Display.dp2Px(TextUtils.isEmpty(mImage) ? 74 : 100, getResources());
        layoutParams.height = (int) Display.dp2Px(TextUtils.isEmpty(mImage) ? 74 : 100, getResources());
        mPaymentCode.setLayoutParams(layoutParams);
        mDeleteImage.setVisibility(TextUtils.isEmpty(mImage) ? View.INVISIBLE : View.VISIBLE);
        mUploadPaymentCode.setVisibility(TextUtils.isEmpty(mImage) ? View.VISIBLE : View.INVISIBLE);
    }

    private void bankBand(BankBindData bankBindData) {
        Apic.bankBind(bankBindData).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        setResult(ADD_PAY_RESULT, new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
                        finish();
                    }
                })
                .fireFreely();
    }

    private void updateBankAccount(String type, String account, String name, String payPic, String withDrawPass) {
        Apic.updateBankAccount(type, account, name, payPic, withDrawPass).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        setResult(ADD_PAY_RESULT, new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
                        finish();
                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.confirm_add, R.id.paymentCode, R.id.uploadPaymentCode, R.id.deleteImage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm_add:
                String account = mAccountNum.getText().toString();
                String realName = mRealName.getText().toString();
                if (mHasBind) {
                    updateBankAccount(mIsAlipay ? BankBindData.PAY_TYPE_ALIPAY : BankBindData.PAY_TYPE_WECHATPAY,
                            account,
                            mName,
                            mImage,
                            md5Encrypt(mWithDrawPass.getPassword()));
                } else {
                    BankBindData bankBindData = BankBindData.Builder
                            .aBankBindData()
                            .payType(mIsAlipay ? BankBindData.PAY_TYPE_ALIPAY : BankBindData.PAY_TYPE_WECHATPAY)
                            .cardNumber(account)
                            .realName(mName)
                            .image(mImage)
                            .withDrawPass(md5Encrypt(mWithDrawPass.getPassword()))
                            .build();
                    bankBand(bankBindData);
                }
                break;
            case R.id.paymentCode:
                selectImage();
                break;
            case R.id.uploadPaymentCode:
                selectImage();
                break;
            case R.id.deleteImage:
                mImage = "";
                setPaymentCodeBtn();
                GlideApp
                        .with(this)
                        .load(R.drawable.ic_payment_qc_code)
                        .into(mPaymentCode);
                break;
            default:
        }
    }

    private void selectImage() {
        UploadUserImageDialogFragment.newInstance(UploadUserImageDialogFragment.REQ_CODE_TAKE_PICTURE_FROM_GALLERY,
                "", -1, getString(R.string.please_select_image))
                .setOnImagePathListener(new UploadUserImageDialogFragment.OnImagePathListener() {
                    @Override
                    public void onImagePath(int index, String imagePath) {
                        sendImage(imagePath);
                    }
                }).show(getChildFragmentManager());
    }

    private void sendImage(final String path) {
        String image = ImageUtils.compressImageToBase64(path, getActivity());
        Apic.uploadImage(image).tag(TAG).indeterminate(this)
                .callback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        if (resp != null && resp.getData() != null) {
                            mImage = resp.getData();
                            setImage(mImage);
                            setPaymentCodeBtn();
                        }
                    }
                })
                .fire();
    }

    private void setImage(String data) {
        GlideApp
                .with(this)
                .load(TextUtils.isEmpty(data) ? R.drawable.ic_payment_qc_code : data)
                .into(mPaymentCode);
    }
}
