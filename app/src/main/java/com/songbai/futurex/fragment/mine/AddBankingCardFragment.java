package com.songbai.futurex.fragment.mine;

import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.BankBindData;
import com.songbai.futurex.model.mine.BankListBean;
import com.songbai.futurex.utils.Display;

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
    @BindView(R.id.twGroup)
    LinearLayout mTwGroup;
    @BindView(R.id.confirm_add)
    TextView mConfirmAdd;
    private Unbinder mBind;
    private ArrayList<BankListBean> mMainland;
    private ArrayList<BankListBean> mTw;
    private PopupWindow mPopupWindow;
    private Integer[] option = new Integer[]{R.string.cn_mainland, R.string.cn_tw};
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
        setViewByArea(isMainland);
        getBankList();
    }

    private void setViewByArea(boolean isMainland) {
        mBankArea.setText(option[isMainland ? 0 : 1]);
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
                areaSelectPopup();
                break;
            case R.id.mainlandBankName:
                areaSelectBankPopup(mMainlandBankName);
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
                            .build();
                }
                bankBand(bankBindData);
                break;
            default:
        }
    }

    private void bankBand(BankBindData bankBindData) {
        Apic.bankBind(bankBindData)
                .callback(new Callback<Object>() {
                    @Override
                    protected void onRespSuccess(Object resp) {

                    }
                })
                .fire();
    }

    private void areaSelectPopup() {
        View popupView = View.inflate(getContext(), R.layout.layout_popupwindow, null);
        ListView listView = popupView.findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_list_item_1, option) {
            @NonNull
            @Override
            public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                final TextView inflate;
                if (convertView != null && convertView instanceof TextView) {
                    inflate = (TextView) convertView;
                } else {
                    inflate = (TextView) View.inflate(parent.getContext(), R.layout.row_popup_text, null);
                }
                inflate.setText(parent.getContext().getText(option[position]));
                inflate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer integer = option[position];
                        switch (integer) {
                            case R.string.cn_mainland:
                                isMainland = true;
                                setViewByArea(isMainland);
                                break;
                            case R.string.cn_tw:
                                isMainland = false;
                                setViewByArea(isMainland);
                                break;
                            default:
                                break;
                        }
                        mPopupWindow.dismiss();
                    }
                });
                return inflate;
            }
        });
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true) {
            @Override
            public void showAsDropDown(View anchor) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                    Rect rect = new Rect();
                    anchor.getGlobalVisibleRect(rect);
                    int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
                    setHeight(h);
                }
                super.showAsDropDown(anchor);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPopupWindow.setElevation(Display.dp2Px(5, getResources()));
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext().getApplicationContext(), R.color.white)));
        } else {
//            mPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.popup_shader));
        }
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAsDropDown(mBankArea, (int) (mBankArea.getMeasuredWidth() - Display.dp2Px(100, getResources())), 0);
    }

    private void areaSelectBankPopup(final TextView target) {
        View popupView = View.inflate(getContext(), R.layout.layout_popupwindow, null);
        ListView listView = popupView.findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<BankListBean>(getContext(), android.R.layout.simple_list_item_1, mMainland) {
            @NonNull
            @Override
            public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                final TextView inflate;
                if (convertView != null && convertView instanceof TextView) {
                    inflate = (TextView) convertView;
                } else {
                    inflate = (TextView) View.inflate(parent.getContext(), R.layout.row_popup_text, null);
                }
                final BankListBean bankListBean = mMainland.get(position);
                inflate.setText(bankListBean.getBankName());
                inflate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        target.setText(bankListBean.getBankName());
                        mPopupWindow.dismiss();
                    }
                });
                return inflate;
            }
        });
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true) {
            @Override
            public void showAsDropDown(View anchor) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                    Rect rect = new Rect();
                    anchor.getGlobalVisibleRect(rect);
                    int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
                    setHeight(h);
                }
                super.showAsDropDown(anchor);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPopupWindow.setElevation(Display.dp2Px(5, getResources()));
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext().getApplicationContext(), R.color.white)));
        } else {
//            mPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.popup_shader));
        }
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAsDropDown(target, (int) (target.getMeasuredWidth() - Display.dp2Px(100, getResources())), 0);
    }
}
