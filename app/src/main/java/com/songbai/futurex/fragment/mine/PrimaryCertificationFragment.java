package com.songbai.futurex.fragment.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.local.RealNameAuthData;
import com.songbai.futurex.model.status.AuthenticationStatus;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.ItemSelectController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class PrimaryCertificationFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.type)
    TextView mType;
    @BindView(R.id.confirmSubmit)
    TextView mConfirmSubmit;
    @BindView(R.id.realName)
    EditText mRealName;
    @BindView(R.id.certificationNumber)
    EditText mCertificationNumber;
    @BindView(R.id.passportSample)
    ImageView mPassportSample;
    @BindView(R.id.certificationNumHint)
    TextView mCertificationNumHint;
    private Integer[] type = new Integer[]{R.string.mainland_id_card, R.string.tw_id_card, R.string.passport};
    private Unbinder mBind;
    private SmartDialog mSmartDialog;
    private int mCertificationType;
    private String mName;
    private String mNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_primary_certification, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mRealName.addTextChangedListener(mWatcher);
        mCertificationNumber.addTextChangedListener(mWatcher);
        mType.setText(R.string.mainland_id_card);
    }

    private ValidationWatcher mWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mName = mRealName.getText().toString().trim();
            mNumber = mCertificationNumber.getText().toString().trim();
            boolean enabled = !TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mNumber);
            mConfirmSubmit.setEnabled(enabled);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.type, R.id.confirmSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.type:
                showCertificationTypeSelector();
                break;
            case R.id.confirmSubmit:
                submitCertification(mCertificationType, mName, mNumber);
                break;
            default:
        }
    }

    private void submitCertification(int idType, String name, String idcardNum) {
        RealNameAuthData realNameAuthData = RealNameAuthData.Builder.create()
                .idType(idType)
                .name(name)
                .idcardNum(idcardNum)
                .build();
        Apic.realNameAuth(realNameAuthData).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        ToastUtil.show(R.string.primary_certification_complete);
                        LocalUser user = LocalUser.getUser();
                        if (user.isLogin()) {
                            UserInfo userInfo = user.getUserInfo();
                            if (userInfo.getAuthenticationStatus() == AuthenticationStatus.AUTHENTICATION_NONE) {
                                userInfo.setAuthenticationStatus(AuthenticationStatus.AUTHENTICATION_PRIMARY);
                                LocalUser.getUser().setUserInfo(userInfo);
                            }
                        }
                        finish();
                    }
                })
                .fire();
    }

    private void showCertificationTypeSelector() {
        ItemSelectController itemSelectController = new ItemSelectController(getContext());
        CertificationTypeAdapter adapter = new CertificationTypeAdapter();
        adapter.setData(type);
        adapter.setOnItemClickListener(new CertificationTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Integer id) {
                mType.setText(id);
                switch (id) {
                    case R.string.mainland_id_card:
                        mCertificationType = 0;
                        mPassportSample.setVisibility(View.GONE);
                        mCertificationNumHint.setText(R.string.certification_number);
                        mCertificationNumber.setHint(R.string.please_input_certification_number);
                        break;
                    case R.string.tw_id_card:
                        mCertificationType = 1;
                        mPassportSample.setVisibility(View.GONE);
                        mCertificationNumHint.setText(R.string.certification_number);
                        mCertificationNumber.setHint(R.string.please_input_certification_number);
                        break;
                    case R.string.passport:
                        mCertificationType = 2;
                        mPassportSample.setVisibility(View.VISIBLE);
                        mCertificationNumHint.setText(R.string.passport_code);
                        mCertificationNumber.setHint(R.string.see_passport_sample_image_below);
                        break;
                    default:
                }
                mSmartDialog.dismiss();
            }
        });
        itemSelectController.setAdapter(adapter);
        itemSelectController.setHintText(getString(R.string.pleas_select_credentials_type));
        mSmartDialog = SmartDialog.solo(getActivity());
        mSmartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(itemSelectController)
                .show();
    }

    static class CertificationTypeAdapter extends RecyclerView.Adapter {
        private Integer[] mData;
        private static OnItemClickListener mOnItemClickListener;
        private Context mContext;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_select_text, parent, false);
            return new CertificationTypeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CertificationTypeHolder) {
                ((CertificationTypeHolder) holder).bindData(mContext, mData[position]);
            }
        }

        @Override
        public int getItemCount() {
            return mData.length;
        }

        public void setData(Integer[] data) {
            mData = data;
        }

        interface OnItemClickListener {
            void onItemClick(Integer id);
        }

        void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        static class CertificationTypeHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.textView)
            TextView mTextView;

            CertificationTypeHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            void bindData(Context context, final Integer id) {
                mTextView.setText(id);
                mTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(id);
                        }
                    }
                });
            }
        }
    }
}
