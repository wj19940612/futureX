package com.songbai.futurex.fragment.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.BankBindData;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.BankListBean;
import com.songbai.futurex.utils.Display;
import com.songbai.futurex.utils.LanguageUtils;
import com.songbai.futurex.view.PasswordEditText;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class AddBankingCardFragment extends UniqueActivity.UniFragment {
    public static final int ADD_PAY_RESULT = 12343;
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
    @BindView(R.id.areaText)
    TextView mAreaText;
    @BindView(R.id.mainlandBankNameText)
    TextView mMainlandBankNameText;
    @BindView(R.id.mainlandBankBranchText)
    TextView mMainlandBankBranchText;
    @BindView(R.id.mainlandCardNumberText)
    TextView mMainlandCardNumberText;
    @BindView(R.id.twBankNameText)
    TextView mTwBankNameText;
    @BindView(R.id.bankCodeText)
    TextView mBankCodeText;
    @BindView(R.id.twBankBranchText)
    TextView mTwBankBranchText;
    @BindView(R.id.twCardNumberText)
    TextView mTwCardNumberText;
    @BindView(R.id.realNameText)
    TextView mRealNameText;
    @BindView(R.id.withDrawPassText)
    TextView mWithDrawPassText;
    private Unbinder mBind;
    private ArrayList<BankListBean> mMainland;
    private ArrayList<BankListBean> mTw;
    private PopupWindow mPopupWindow;
    private ArrayList<String> option = new ArrayList<>();
    boolean isMainland = true;
    private OptionsPickerView mPvOptions;
    private OptionsPickerView mBankPvOptions;

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
        alignText();
        setViewByArea(isMainland);
        getBankList();
    }

    private void alignText() {
        Locale userLocale = LanguageUtils.getUserLocale(getContext());
        boolean chinese = "zh".equals(userLocale.getLanguage());
        mAreaText.setMinWidth(chinese ? 0 : (int) Display.dp2Px(90, getResources()));
        mMainlandBankNameText.setMinWidth(chinese ? 0 : (int) Display.dp2Px(90, getResources()));
        mMainlandBankBranchText.setMinWidth(chinese ? 0 : (int) Display.dp2Px(90, getResources()));
        mMainlandCardNumberText.setMinWidth(chinese ? 0 : (int) Display.dp2Px(90, getResources()));
        mTwBankNameText.setMinWidth(chinese ? 0 : (int) Display.dp2Px(90, getResources()));
        mBankCodeText.setMinWidth(chinese ? 0 : (int) Display.dp2Px(90, getResources()));
        mTwBankBranchText.setMinWidth(chinese ? 0 : (int) Display.dp2Px(90, getResources()));
        mTwCardNumberText.setMinWidth(chinese ? 0 : (int) Display.dp2Px(90, getResources()));
        mRealNameText.setMinWidth(chinese ? 0 : (int) Display.dp2Px(90, getResources()));
        mWithDrawPassText.setMinWidth(chinese ? 0 : (int) Display.dp2Px(90, getResources()));
    }

    private void setViewByArea(boolean isMainland) {
        mBankArea.setText(option.get(isMainland ? 0 : 1));
        mTwGroup.setVisibility(isMainland ? View.GONE : View.VISIBLE);
        mMainlandGroup.setVisibility(isMainland ? View.VISIBLE : View.GONE);
    }

    private void getBankList() {
        Apic.bankList().tag(TAG)
                .callback(new Callback<Resp<ArrayList<BankListBean>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<BankListBean>> resp) {
                        ArrayList<BankListBean> data = resp.getData();
                        divideIntoGroups(data);
                    }
                })
                .fireFreely();
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
        ArrayList<String> banks = new ArrayList<>();
        for (BankListBean bankListBean : bankListBeans) {
            banks.add(bankListBean.getBankName());
        }
        mBankPvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (isMainland) {
                    mMainlandBankName.setText(bankListBeans.get(options1).getBankName());
                } else {
                    mTwBankName.setText(bankListBeans.get(options1).getBankName());
                }
            }
        }).setLayoutRes(R.layout.pickerview_custom_view, new CustomListener() {
            @Override
            public void customLayout(View v) {
                TextView cancel = v.findViewById(R.id.cancel);
                TextView confirm = v.findViewById(R.id.confirm);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBankPvOptions.dismiss();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBankPvOptions.returnData();
                        mBankPvOptions.dismiss();
                    }
                });
            }
        }).build();
        mBankPvOptions.setPicker(banks, null, null);
        mBankPvOptions.show();
    }

    private void showAreaSelect() {
        mPvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                mBankArea.setText(option.get(options1));
                isMainland = options1 == 0;
                setViewByArea(isMainland);
            }
        }).setLayoutRes(R.layout.pickerview_custom_view, new CustomListener() {
            @Override
            public void customLayout(View v) {
                TextView cancel = v.findViewById(R.id.cancel);
                TextView confirm = v.findViewById(R.id.confirm);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvOptions.dismiss();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvOptions.returnData();
                        mPvOptions.dismiss();
                    }
                });
            }
        }).build();
        mPvOptions.setPicker(option, null, null);
        mPvOptions.show();
    }

    private void bankBand(BankBindData bankBindData) {
        Apic.bankBind(bankBindData).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        FragmentActivity activity = AddBankingCardFragment.this.getActivity();
                        activity.setResult(ADD_PAY_RESULT, new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
                        activity.finish();
                    }
                })
                .fire();
    }
}
