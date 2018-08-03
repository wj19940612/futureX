package com.songbai.futurex.wrapper;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.KeyBoardUtils;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.wrapres.utils.ValidityCheckUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyUserNameActivity extends BaseActivity {

    @BindView(R.id.userNameInput)
    EditText mUserName;
    @BindView(R.id.submitUserName)
    AppCompatButton mSubmitUserName;
    @BindView(R.id.clear)
    AppCompatImageView mClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_name);
        ButterKnife.bind(this);
        mUserName.addTextChangedListener(mValidationWatcher);
        mUserName.setFilters(new InputFilter[]{new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        if (ValidityCheckUtil.isLegalNickName(source)) {
                            return source;
                        }
                        return "";
                    }
                },
                new InputFilter.LengthFilter(8)});
        mUserName.setText(LocalWrapUser.getUser().getUserInfo().getUserName());

        mUserName.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyBoardUtils.openKeyBoard(mUserName);
            }
        }, 200);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserName.removeTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean buttonEnable = checkConfirmButtonEnable();
            if (buttonEnable) {
                mClear.setVisibility(View.VISIBLE);
            } else {
                mClear.setVisibility(View.INVISIBLE);
            }
            if (mSubmitUserName.isEnabled() != buttonEnable) {
                mSubmitUserName.setEnabled(buttonEnable);
            }
            String string = s.toString();
            if (string.contains(" ")) {
                String newData = string.replaceAll(" ", "");
                mUserName.setText(newData);
            }
            mUserName.setSelection(mUserName.getText().toString().length());
        }
    };

    private boolean checkConfirmButtonEnable() {
        String userName = mUserName.getText().toString().trim().replaceAll(" ", "");
        return !TextUtils.isEmpty(userName);
    }

    private void submitNickName() {
        final String userName = mUserName.getText().toString().trim();
        if (!ValidityCheckUtil.isLegalNickName(userName)) {
            ToastUtil.show(R.string.is_only_a_chinese_name);
            return;
        }

        Apic.submitNickName(userName)
                .tag(TAG)
                .indeterminate(this)
                .callback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            ToastUtil.show(R.string.modify_success);
                            LocalWrapUser.getUser().getUserInfo().setUserName(userName);
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                })
                .fire();
    }


    @OnClick({R.id.submitUserName, R.id.clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submitUserName:
                submitNickName();
                break;
            case R.id.clear:
                mUserName.setText("");
                break;
        }
    }

}
