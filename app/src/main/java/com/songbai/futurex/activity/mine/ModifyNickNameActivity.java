package com.songbai.futurex.activity.mine;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.utils.inputfilter.EmojiFilter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyNickNameActivity extends BaseActivity {

    private static final int maxLength = 16;

    @BindView(R.id.userNameInput)
    EditText mUserNameInput;
    @BindView(R.id.input)
    LinearLayout mInput;
    @BindView(R.id.submitNickName)
    TextView mSubmitNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modiify_nick_name);
        ButterKnife.bind(this);

        mUserNameInput.addTextChangedListener(mValidationWatcher);
        mUserNameInput.setFilters(new InputFilter[]{filter, new EmojiFilter()});

        String nickName = getIntent().getStringExtra(ExtraKeys.NICK_NAME);
        mUserNameInput.setText(nickName);

    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean submitBenEnable = checkSubmitBenEnable();
            if (mSubmitNickName.isEnabled() != submitBenEnable) {
                mUserNameInput.setSelection(s.length());
                mSubmitNickName.setEnabled(submitBenEnable);
            }
        }
    };

    private boolean checkSubmitBenEnable() {
        String s = mUserNameInput.getText().toString();
        return !TextUtils.isEmpty(s);
    }

    /**
     * 中文按照2个字符算  英文按1个字符
     */
    private static InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
            int dindex = 0;
            int count = 0;

            while (count <= maxLength && dindex < dest.length()) {
                char c = dest.charAt(dindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }

            if (count > maxLength) {
                return dest.subSequence(0, dindex - 1);
            }

            int sindex = 0;
            while (count <= maxLength && sindex < src.length()) {
                char c = src.charAt(sindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }

            if (count > maxLength) {
                sindex--;
            }

            return src.subSequence(0, sindex);
        }
    };


    @OnClick({R.id.submitNickName})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submitNickName:
                submitNickName();
                break;
            default:
        }
    }

    private void submitNickName() {
        final String nickName = mUserNameInput.getText().toString();
        Apic.updateNickName(nickName)
                .tag(TAG)
                .indeterminate(this)
                .callback(new Callback<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        ToastUtil.show(R.string.update_success);
                        UserInfo userInfo = LocalUser.getUser().getUserInfo();
                        userInfo.setUserName(nickName);
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .fire();
    }
}
