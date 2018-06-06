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

import sbai.com.glide.GlideApp;

/**
 * Modified by john on 2018/6/1
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class AuthCodeViewController extends SmartDialog.CustomViewController {

    private OnClickListener mOnClickListener;
    private Context mContext;
    private EditText mAuthCodeInput;
    private ImageView mAuthCodeImage;
    private TextView mConfirm;

    private String mImageUrl;

    public interface OnClickListener {
        void onConfirmClick(String authCode);
    }

    public AuthCodeViewController(Context context, OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_auth_code, null);
    }

    @Override
    public void onInitView(View view, SmartDialog dialog) {
        mAuthCodeInput = (EditText) view.findViewById(R.id.authCodeInput);
        mAuthCodeImage = (ImageView) view.findViewById(R.id.authCodeImage);
        mConfirm = (TextView) view.findViewById(R.id.confirm);

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
                String authCode = mAuthCodeInput.getText().toString();
                mOnClickListener.onConfirmClick(authCode);
            }
        });

        if (!TextUtils.isEmpty(mImageUrl)) {
            updateView();
        }
    }

    private boolean checkConfirmButtonEnable() {
        if (TextUtils.isEmpty(mAuthCodeInput.getText().toString())) {
            return false;
        }
        return true;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
        if (isInitialized()) {
            updateView();
        }
    }

    private void updateView() {
        GlideApp.with(mContext).load(mImageUrl).into(mAuthCodeImage);
    }
}
