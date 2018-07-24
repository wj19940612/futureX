package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.SmartDialog;

/**
 * Modified by john on 2018/6/1
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class GoogleAuthCodeViewController extends SmartDialog.CustomViewController {

    private TextView mTitle;
    private OnClickListener mOnClickListener;
    private Context mContext;
    private EditText mAuthCodeInput;
    private TextView mConfirm;

    public interface OnClickListener {
        void onConfirmClick(String authCode);
    }

    public GoogleAuthCodeViewController(Context context, OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_google_auth_code, null);
    }

    @Override
    public void onInitView(View view, final SmartDialog dialog) {
        mTitle = view.findViewById(R.id.topTitle);
        mAuthCodeInput = (EditText) view.findViewById(R.id.authCodeInput);
        mConfirm = (TextView) view.findViewById(R.id.confirm);
        ImageView close = (ImageView) view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mAuthCodeInput.addTextChangedListener(new ValidationWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                boolean enable = checkConfirmButtonEnable();
                if (enable != mConfirm.isEnabled()) {
                    mConfirm.setEnabled(enable);
                }
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String authCode = mAuthCodeInput.getText().toString();
                mOnClickListener.onConfirmClick(authCode);
            }
        });
    }

    private boolean checkConfirmButtonEnable() {
        if (TextUtils.isEmpty(mAuthCodeInput.getText().toString())) {
            return false;
        }
        return true;
    }

    public void setTitle(int titleRes) {
        if (isViewInitialized()) {
            mTitle.setText(titleRes);
        }
    }
}
