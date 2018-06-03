package com.songbai.futurex.fragment.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.AreaCode;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.SmartDialog;

import java.util.List;

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
public class RegisterFragment extends UniqueActivity.UniFragment {

    @BindView(R.id.closePage)
    ImageView mClosePage;
    @BindView(R.id.pageTitle)
    TextView mPageTitle;
    @BindView(R.id.registerTypeSwitch)
    TextView mRegisterTypeSwitch;
    @BindView(R.id.areaCode)
    TextView mAreaCode;
    @BindView(R.id.phoneNumber)
    EditText mPhoneNumber;
    @BindView(R.id.phoneAuthCode)
    EditText mPhoneAuthCode;
    @BindView(R.id.emailAuthCode)
    EditText mEmailAuthCode;
    @BindView(R.id.next)
    TextView mNext;
    @BindView(R.id.switchToLogin)
    TextView mSwitchToLogin;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;
    Unbinder unbinder;
    @BindView(R.id.phoneLine)
    LinearLayout mPhoneLine;
    @BindView(R.id.email)
    EditText mEmail;
    @BindView(R.id.emailLine)
    LinearLayout mEmailLine;
    @BindView(R.id.getPhoneAuthCode)
    TextView mGetPhoneAuthCode;
    @BindView(R.id.phoneAuthCodeLine)
    LinearLayout mPhoneAuthCodeLine;
    @BindView(R.id.getEmailAuthCode)
    TextView mGetEmailAuthCode;
    @BindView(R.id.emailAuthCodeLine)
    LinearLayout mEmailAuthCodeLine;

    private boolean mFreezeGetPhoneAuthCode;
    private boolean mFreezeGetEmailAuthCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mPhoneNumber.addTextChangedListener(mValidationWatcher);
        mPhoneAuthCode.addTextChangedListener(mValidationWatcher);
        mEmail.addTextChangedListener(mValidationWatcher);
        mEmailAuthCode.addTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkNextButtonEnable();
            if (enable != mNext.isEnabled()) {
                mNext.setEnabled(enable);
            }

            if (isPhoneRegister()) {
                enable = checkGetPhoneAuthCodeEnable();
                if (enable != mGetPhoneAuthCode.isEnabled()) {
                    mGetPhoneAuthCode.setEnabled(enable);
                }
            } else {
                enable = checkGetEmailAuthCodeButtonEnable();
                if (enable != mGetEmailAuthCode.isEnabled()) {
                    mGetEmailAuthCode.setEnabled(enable);
                }
            }
        }
    };

    private boolean checkGetPhoneAuthCodeEnable() {
        String phone = mPhoneNumber.getText().toString().trim();
        return !TextUtils.isEmpty(phone) && !mFreezeGetPhoneAuthCode;
    }

    private boolean checkGetEmailAuthCodeButtonEnable() {
        String email = mEmail.getText().toString().trim();
        return !TextUtils.isEmpty(email) && !mFreezeGetEmailAuthCode;
    }

    private boolean checkNextButtonEnable() {
        if (isPhoneRegister()) {
            String phone = mPhoneNumber.getText().toString().trim();
            String phoneAuthCode = mPhoneAuthCode.getText().toString().trim();
            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(phoneAuthCode)) {
                return false;
            }
        } else {
            String email = mEmail.getText().toString().trim();
            String emailAuthCode = mEmailAuthCode.getText().toString().trim();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(emailAuthCode)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.closePage, R.id.registerTypeSwitch, R.id.areaCode, R.id.next, R.id.switchToLogin,
            R.id.getPhoneAuthCode, R.id.getEmailAuthCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closePage:
                getActivity().finish();
                break;
            case R.id.registerTypeSwitch:
                switchRegisterType();
                break;
            case R.id.areaCode:
                showAreaCodeSelector();
                break;
            case R.id.getAuthCode:
                break;
            case R.id.next:
                break;
            case R.id.switchToLogin:
                break;
            case R.id.getPhoneAuthCode:
                requestPhoneAuthCode();
                break;
            case R.id.getEmailAuthCode:
                break;
        }
    }

    private void requestPhoneAuthCode() {
        String phone = mPhoneNumber.getText().toString().trim();
        String areaCode = mAreaCode.getText().toString();
        AuthCodeGet authCodeGet = AuthCodeGet.Builder.anAuthCodeGet()
                .phone(phone)
                .teleCode(areaCode)
                .type(AuthCodeGet.TYPE_REGISTER)
                .build();

        Apic.getAuthCode(authCodeGet).tag(TAG)
                .callback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {

                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_REQUIRED
                                ||failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_TIMEOUT
                                || failedResp.getCode() == Resp.Code.IMAGE_AUTH_CODE_FAILED) {

                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
    }

    private void showAreaCodeSelector() {
        final SelectAreaCodesViewController controller = new SelectAreaCodesViewController(getActivity());
        controller.setSelectListener(new SelectAreaCodesViewController.OnSelectListener() {
            @Override
            public void onSelect(AreaCode areaCode) {
                mAreaCode.setText(areaCode.getTeleCode());
            }
        });
        showAreaCodeDialog(controller);

        Apic.getAreaCodes().tag(TAG)
                .callback(new Callback4Resp<Resp<List<AreaCode>>, List<AreaCode>>() {
                    @Override
                    protected void onRespData(List<AreaCode> data) {
                        controller.setAreaCodeList(data);
                    }
                }).fireFreely();
    }

    private void showAreaCodeDialog(SelectAreaCodesViewController controller) {
        SmartDialog.with(getActivity()).setCustomViewController(controller)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setWidthScale(1)
                .show();
    }

    private static class SelectAreaCodesViewController implements SmartDialog.CustomViewController {

        private Context mContext;
        private RecyclerView mRecyclerView;
        private TextView mCancel;
        private ProgressBar mProgressBar;
        private List<AreaCode> mAreaCodeList;
        private OnSelectListener mSelectListener;


        interface OnSelectListener {
            void onSelect(AreaCode areaCode);
        }

        public void setSelectListener(OnSelectListener selectListener) {
            mSelectListener = selectListener;
        }

        public SelectAreaCodesViewController(Context context) {
            mContext = context;
        }

        @Override
        public View onCreateView() {
            return LayoutInflater.from(mContext).inflate(R.layout.view_select_area_codes, null);
        }

        @Override
        public void setupView(View view, final SmartDialog dialog) {
            mRecyclerView = view.findViewById(R.id.recyclerView);
            mCancel = view.findViewById(R.id.cancel);
            mProgressBar = view.findViewById(R.id.progressBar);
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        public void setAreaCodeList(List<AreaCode> areaCodeList) {
            mAreaCodeList = areaCodeList;

            mRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
            mRecyclerView.addItemDecoration(dividerItemDecoration);
            AreaCodeAdapter adapter = new AreaCodeAdapter(mAreaCodeList, new OnRVItemClickListener() {
                @Override
                public void onItemClick(View view, int position, Object obj) {
                    if (obj != null && mSelectListener != null) {
                        AreaCode areaCode = (AreaCode) obj;
                        mSelectListener.onSelect(areaCode);
                    }
                }
            });
            mRecyclerView.setAdapter(adapter);
        }
    }

    public static class AreaCodeAdapter extends RecyclerView.Adapter<AreaCodeAdapter.ViewHolder> {

        private List<AreaCode> mAreaCodeList;
        private OnRVItemClickListener mOnRVItemClickListener;

        public AreaCodeAdapter(List<AreaCode> areaCodeList, OnRVItemClickListener onRVItemClickListener) {
            mAreaCodeList = areaCodeList;
            mOnRVItemClickListener = onRVItemClickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_area_code, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AreaCode areaCode = mAreaCodeList.get(position);
            holder.bind(areaCode, position, mOnRVItemClickListener);
        }

        @Override
        public int getItemCount() {
            return mAreaCodeList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.countryName)
            TextView mCountryName;
            @BindView(R.id.areaCode)
            TextView mAreaCode;

            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(final AreaCode areaCode, final int position, final OnRVItemClickListener onRVItemClickListener) {
                mCountryName.setText(areaCode.getName());
                mAreaCode.setText(areaCode.getTeleCode());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRVItemClickListener.onItemClick(v, position, areaCode);
                    }
                });
            }
        }
    }

    private void switchRegisterType() {
        if (isPhoneRegister()) {
            switchToEmailRegister();
        } else {
            switchToPhoneRegister();
        }
    }

    private void switchToPhoneRegister() {
        mPageTitle.setText(R.string.phone_register);
        mRegisterTypeSwitch.setText(R.string.email_register);

        mPhoneLine.setVisibility(View.VISIBLE);
        mPhoneAuthCodeLine.setVisibility(View.VISIBLE);

        mEmailLine.setVisibility(View.GONE);
        mEmailAuthCodeLine.setVisibility(View.GONE);
    }

    private void switchToEmailRegister() {
        mPageTitle.setText(R.string.email_register);
        mRegisterTypeSwitch.setText(R.string.phone_register);

        mPhoneLine.setVisibility(View.GONE);
        mPhoneAuthCodeLine.setVisibility(View.GONE);

        mEmailLine.setVisibility(View.VISIBLE);
        mEmailAuthCodeLine.setVisibility(View.VISIBLE);
    }

    private boolean isPhoneRegister() {
        return mPhoneLine.getVisibility() == View.VISIBLE;
    }
}
