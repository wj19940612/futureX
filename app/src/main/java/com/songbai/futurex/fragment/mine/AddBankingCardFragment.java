package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.BankBindData;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.BankListBean;
import com.songbai.futurex.view.PasswordEditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class AddBankingCardFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.bankArea)
    TextView mBankArea;
    @BindView(R.id.mainlandBankName)
    TextView mMainlandBankName;
    @BindView(R.id.mainlandBankBranch)
    EditText mMainlandBankBranch;
    @BindView(R.id.mainlandCardNumber)
    EditText mMainlandCardNumber;
    @BindView(R.id.mainlandGroup)
    LinearLayout mMainlandGroup;
    @BindView(R.id.twBankName)
    EditText mTwBankName;
    @BindView(R.id.bankCode)
    EditText mBankCode;
    @BindView(R.id.twBankBranch)
    EditText mTwBankBranch;
    @BindView(R.id.twCardNumber)
    EditText mTwCardNumber;
    @BindView(R.id.realName)
    EditText mRealName;
    @BindView(R.id.withDrawPass)
    PasswordEditText mWithDrawPass;
    @BindView(R.id.twGroup)
    LinearLayout mTwGroup;
    @BindView(R.id.confirm_add)
    TextView mConfirmAdd;
    private Unbinder mBind;
    private ArrayList<BankListBean> mMainland;
    private ArrayList<BankListBean> mTw;
    private PopupWindow mPopupWindow;
    private ArrayList<String> option = new ArrayList<>();
    boolean isMainland = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_mainland_banking_card, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        option.add(getString(R.string.cn_mainland));
        option.add(getString(R.string.cn_tw));
        setViewByArea(isMainland);
        getBankList();
    }

    private void setViewByArea(boolean isMainland) {
        mBankArea.setText(option.get(isMainland ? 0 : 1));
        mTwGroup.setVisibility(isMainland ? View.GONE : View.VISIBLE);
        mMainlandGroup.setVisibility(isMainland ? View.VISIBLE : View.GONE);
    }

    private void getBankList() {
        Apic.bankList()
                .callback(new Callback<Resp<ArrayList<BankListBean>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<BankListBean>> resp) {
                        ArrayList<BankListBean> data = resp.getData();
                        divideIntoGroups(data);
                    }
                })
                .fire();
    }

    private void divideIntoGroups(ArrayList<BankListBean> data) {
        mMainland = new ArrayList<>();
        mTw = new ArrayList<>();
        for (BankListBean bean : data) {
            switch (bean.getBankArea()) {
                case BankListBean.AREA_MAINLAND:
                    mMainland.add(bean);
                    break;
                case BankListBean.AREA_TW:
                    mTw.add(bean);
                    break;
                default:
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.bankArea, R.id.mainlandBankName, R.id.confirm_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bankArea:
                showAreaSelect();
                break;
            case R.id.mainlandBankName:
                showBankSelect(isMainland ? mMainland : mTw);
                break;
            case R.id.confirm_add:
                BankBindData bankBindData;
                if (isMainland) {
                    bankBindData = BankBindData.Builder
                            .aBankBindData()
                            .payType(BankBindData.PAY_TYPE_BANK_CARD)
                            .bankArea(BankBindData.MAINLAND_BANK)
                            .bankName(mMainlandBankName.getText().toString())
                            .bankBranch(mMainlandBankBranch.getText().toString())
                            .cardNumber(mMainlandCardNumber.getText().toString())
                            .realName(LocalUser.getUser().getUserInfo().getRealName())
                            .withDrawPass(md5Encrypt(mWithDrawPass.getPassword()))
                            .build();
                } else {
                    bankBindData = BankBindData.Builder
                            .aBankBindData()
                            .payType(BankBindData.PAY_TYPE_BANK_CARD)
                            .bankArea(BankBindData.TW_BANK)
                            .bankName(mTwBankName.getText().toString())
                            .bankCode(mBankCode.getText().toString())
                            .bankBranch(mTwBankBranch.getText().toString())
                            .cardNumber(mTwCardNumber.getText().toString())
                            .realName(mRealName.getText().toString())
                            .withDrawPass(md5Encrypt(mWithDrawPass.getPassword()))
                            .build();
                }
                bankBand(bankBindData);
                break;
            default:
        }
    }

    private void showBankSelect(final ArrayList<BankListBean> bankListBeans) {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (isMainland) {
                    mMainlandBankName.setText(bankListBeans.get(options1).getBankName());
                } else {
                    mTwBankName.setText(bankListBeans.get(options1).getBankName());
                }
            }
        }).build();
        pvOptions.setPicker(bankListBeans, null, null);
        pvOptions.show();
    }

    private void showAreaSelect() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                mBankArea.setText(option.get(options1));
                isMainland = options1 == 0;
                setViewByArea(isMainland);
            }
        }).build();
        pvOptions.setPicker(option, null, null);
        pvOptions.show();
    }

    private void bankBand(BankBindData bankBindData) {
        Apic.bankBind(bankBindData)
                .callback(new Callback<Object>() {
                    @Override
                    protected void onRespSuccess(Object resp) {
                        AddBankingCardFragment.this.getActivity().finish();
                    }
                })
                .fire();
    }
}
