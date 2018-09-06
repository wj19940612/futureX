package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yangguangda
 * @date 2018/9/6
 */
public class SimpleOTCLimitController extends SmartDialog.CustomViewController {
    @BindView(R.id.hintMsg)
    TextView mHintMsg;
    @BindView(R.id.close)
    ImageView mClose;
    @BindView(R.id.setAuth)
    TextView mSetAuth;
    @BindView(R.id.primaryAuthGroup)
    LinearLayout mPrimaryAuthGroup;
    @BindView(R.id.setCashPwd)
    TextView mSetCashPwd;
    @BindView(R.id.cashPwdGroup)
    LinearLayout mCashPwdGroup;
    @BindView(R.id.bindPhone)
    TextView mBindPhone;
    @BindView(R.id.bindPhoneGroup)
    LinearLayout mBindPhoneGroup;
    @BindView(R.id.bindBankCard)
    TextView mBindBankCard;
    @BindView(R.id.bindBankCardGroup)
    LinearLayout mBindBankCardGroup;
    @BindView(R.id.bindPay)
    TextView mBindPay;
    @BindView(R.id.bindPayGroup)
    LinearLayout mBindPayGroup;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;

    public void setAuth(boolean setAuth) {
        if (isViewInitialized()) {
            mSetAuth.setVisibility(setAuth ? View.VISIBLE : View.GONE);
        }
    }

    public void setCashPwd(boolean setCashPwd) {
        if (isViewInitialized()) {
            mSetCashPwd.setVisibility(setCashPwd ? View.VISIBLE : View.GONE);
        }
    }

    public void setPhone(boolean phone) {
        if (isViewInitialized()) {
            mBindPhone.setVisibility(phone ? View.VISIBLE : View.GONE);
        }
    }

    public void bindMainland(boolean bindMainland) {
        if (isViewInitialized()) {
            mBindBankCard.setVisibility(bindMainland ? View.VISIBLE : View.GONE);
        }
    }

    public void bindPay(boolean bindPay) {
        if (isViewInitialized()) {
            mBindPay.setVisibility(bindPay ? View.VISIBLE : View.GONE);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public SimpleOTCLimitController(Context context, OnItemClickListener onItemClickListener) {
        mContext = context;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_simple_otc_alert, null);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    protected void onInitView(View view, final SmartDialog dialog) {
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void setState(boolean otc365, boolean buy) {
        if (isViewInitialized()) {
            if (otc365) {
                mBindPayGroup.setVisibility(View.GONE);
            } else {
                if (!buy) {
                    mBindPhoneGroup.setVisibility(View.GONE);
                    mBindBankCardGroup.setVisibility(View.GONE);
                }
            }
        }
    }

    @OnClick({R.id.setAuth, R.id.setCashPwd, R.id.bindPhone, R.id.bindBankCard, R.id.bindPay})
    public void onViewClicked(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view);
        }
    }
}
