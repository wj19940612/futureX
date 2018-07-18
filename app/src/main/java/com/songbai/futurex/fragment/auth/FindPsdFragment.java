package com.songbai.futurex.fragment.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.model.local.FindPsdData;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.RegularExpUtils;
import com.songbai.futurex.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/6/6
 * <p>
 * Description: 忘记密码找回密码
 */
public class FindPsdFragment extends UniqueActivity.UniFragment {

    private static final int REQ_CODE_FIND_PSD = 93;

    @BindView(R.id.closePage)
    ImageView mClosePage;
    @BindView(R.id.phoneOrEmail)
    EditText mPhoneOrEmail;
    @BindView(R.id.next)
    TextView mNext;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;
    @BindView(R.id.phoneNumberClear)
    ImageView mPhoneNumberClear;

    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_psd, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mPhoneOrEmail.addTextChangedListener(mValidationWatcher);

        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    KeyBoardUtils.closeKeyboard(mRootView);
                    return true;
                }
                return false;
            }
        });
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkNextButtonEnable();
            if (enable != mNext.isEnabled()) {
                mNext.setEnabled(enable);
            }

            mPhoneNumberClear.setVisibility(checkClearBtnVisible() ? View.VISIBLE : View.INVISIBLE);
        }
    };

    private boolean checkClearBtnVisible() {
        String phone = mPhoneOrEmail.getText().toString();
        return !TextUtils.isEmpty(phone);
    }

    private boolean checkNextButtonEnable() {
        String phoneOrEmail = mPhoneOrEmail.getText().toString().trim();

        if (TextUtils.isEmpty(phoneOrEmail)) {
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.closePage, R.id.next, R.id.phoneNumberClear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closePage:
                getActivity().finish();
                break;
            case R.id.next:
                openAuthCodePage();
                break;
            case R.id.phoneNumberClear:
                mPhoneOrEmail.setText("");
                break;
        }
    }

    private void openAuthCodePage() {
        final String phoneOrEmail = mPhoneOrEmail.getText().toString().trim();
        FindPsdData findPsdData = new FindPsdData();
        if (RegularExpUtils.isValidEmail(phoneOrEmail)) {
            findPsdData.setEmail(phoneOrEmail);
        } else {
            findPsdData.setPhone(phoneOrEmail);
        }
        UniqueActivity.launcher(getActivity(), AuthCodeFragment.class)
                .putExtra(ExtraKeys.FIND_PSD_DATA, findPsdData)
                .execute(this, REQ_CODE_FIND_PSD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_FIND_PSD && resultCode == Activity.RESULT_OK) {
            finish();
        }
    }
}
