package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.graphics.Bitmap;
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
public class AuthCodeViewController extends SmartDialog.CustomViewController {

    private TextView mTitle;
    private OnClickListener mOnClickListener;
    private Context mContext;
    private EditText mAuthCodeInput;
    private ImageView mAuthCodeImage;
    private TextView mConfirm;
    private TextView mLoadImageFailure;

    private Bitmap mAuthCodeBitmap;
    private boolean mControlByOutSide;

    public interface OnClickListener {
        void onConfirmClick(String authCode);

        void onImageCodeClick(ImageView imageView);
    }

    public AuthCodeViewController(Context context, OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_auth_code, null);
    }

    public ImageView getAuthCodeImage() {
        return mAuthCodeImage;
    }

    @Override
    public void onInitView(View view, final SmartDialog dialog) {
        mTitle = view.findViewById(R.id.topTitle);
        mAuthCodeInput = (EditText) view.findViewById(R.id.authCodeInput);
        mAuthCodeImage = (ImageView) view.findViewById(R.id.authCodeImage);
        mConfirm = (TextView) view.findViewById(R.id.confirm);
        mLoadImageFailure = (TextView) view.findViewById(R.id.loadImageFailure);

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
                if (!mControlByOutSide) {
                    dialog.dismiss();
                }
                String authCode = mAuthCodeInput.getText().toString();
                mOnClickListener.onConfirmClick(authCode);

            }
        });

        mAuthCodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onImageCodeClick(mAuthCodeImage);
            }
        });

        mLoadImageFailure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuthCodeImage.performClick();
            }
        });
    }

    public void setControlByOutSide(boolean controlByOutSide) {
        mControlByOutSide = controlByOutSide;
    }

    public void clearAuthCode(){
        mAuthCodeInput.setText("");
    }

    private boolean checkConfirmButtonEnable() {
        String authCode = mAuthCodeInput.getText().toString().trim();
        if (TextUtils.isEmpty(authCode)) {
            return false;
        }
        if (authCode.length() < 4) {
            return false;
        }
        return true;
    }

    public void setTitle(int titleRes) {
        if (isViewInitialized()) {
            mTitle.setText(titleRes);
        }
    }

    public void setAuthCodeBitmap(Bitmap authCodeBitmap) {
        mAuthCodeBitmap = authCodeBitmap;
        if (isViewInitialized() && mAuthCodeBitmap != null) {
            mLoadImageFailure.setVisibility(View.GONE);
            mAuthCodeImage.setImageBitmap(mAuthCodeBitmap);
        }
    }

    public void loadImageFailure() {
        mLoadImageFailure.setVisibility(View.VISIBLE);
    }
}
