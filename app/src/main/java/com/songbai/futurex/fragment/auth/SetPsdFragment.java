package com.songbai.futurex.fragment.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.utils.RegularExpUtils;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.PasswordEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/5/31
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class SetPsdFragment extends UniqueActivity.UniFragment {

    @BindView(R.id.closePage)
    ImageView mClosePage;
    @BindView(R.id.passwordHint)
    TextView mPasswordHint;
    @BindView(R.id.passwordStrength)
    TextView mPasswordStrength;
    @BindView(R.id.passwordStrengthLine)
    LinearLayout mPasswordStrengthLine;
    @BindView(R.id.confirm)
    TextView mConfirm;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;
    Unbinder unbinder;
    @BindView(R.id.loginPsd)
    PasswordEditText mLoginPsd;
    @BindView(R.id.confirmPsd)
    PasswordEditText mConfirmPsd;

    enum PsdStrength {
        NONE,
        WEAK,
        MID,
        STRONG
    }

    private PsdStrength mCurPsdStrength;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_psd, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mLoginPsd.addTextChangedListener(mLoginPsdWatcher);
        mConfirmPsd.addTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mLoginPsdWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mValidationWatcher.afterTextChanged(s);

            PsdStrength psdStrength = getPasswordStrength();
            if (mCurPsdStrength != psdStrength) {
                mCurPsdStrength = psdStrength;
                if (mCurPsdStrength == PsdStrength.NONE) {
                    mPasswordHint.setVisibility(View.VISIBLE);
                    mPasswordStrengthLine.setVisibility(View.GONE);
                } else {
                    mPasswordHint.setVisibility(View.GONE);
                    mPasswordStrengthLine.setVisibility(View.VISIBLE);
                }
                updatePsdStrengthView();
            }
        }
    };

    private void updatePsdStrengthView() {
        switch (mCurPsdStrength) {
            case STRONG:
                mPasswordStrength.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_psd_security_strong, 0, 0, 0);
                mPasswordStrength.setText(R.string.strong);
                break;
            case MID:
                mPasswordStrength.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_psd_security_mid, 0, 0, 0);
                mPasswordStrength.setText(R.string.strong);
                break;
            case WEAK:
                mPasswordStrength.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_psd_security_weak, 0, 0, 0);
                mPasswordStrength.setText(R.string.strong);
                break;
            default:
                break;
        }
    }

    private PsdStrength getPasswordStrength() {
        String password = mLoginPsd.getPassword();
        if (password.length() < 8) {
            return PsdStrength.NONE;
        }

        if (RegularExpUtils.isWeakPassword(password)) {
            return PsdStrength.WEAK;
        }

        if (RegularExpUtils.isMiddlePassword(password)) {
            return PsdStrength.MID;
        }

        if (RegularExpUtils.isStrongPassword(password)) {
            return PsdStrength.STRONG;
        }

        return PsdStrength.NONE;
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkConfirmButtonEnable();
            if (enable != mConfirm.isEnabled()) {
                mConfirm.setEnabled(enable);
            }
        }
    };

    private boolean checkConfirmButtonEnable() {
        String loginPsd = mLoginPsd.getPassword();
        String confirmPsd = mConfirmPsd.getPassword();
        if (TextUtils.isEmpty(loginPsd) || TextUtils.isEmpty(confirmPsd)) {
            return false;
        }

        if (loginPsd.length() < 8 || confirmPsd.length() < 8) {
            return false;
        }

        if (!loginPsd.equals(confirmPsd)) {
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.closePage, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closePage:
                getActivity().finish();
                break;
            case R.id.confirm:
                
                break;
        }
    }
}
