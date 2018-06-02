package com.songbai.futurex.fragment.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.AreaCode;
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
    @BindView(R.id.getAuthCode)
    TextView mGetAuthCode;
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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.closePage, R.id.registerTypeSwitch, R.id.areaCode, R.id.getAuthCode, R.id.next, R.id.switchToLogin})
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
        }
    }

    private void showAreaCodeSelector() {
        final SelectAreaCodesViewController controller
                = new SelectAreaCodesViewController(getActivity());
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

        public SelectAreaCodesViewController(Context context) {
            mContext = context;
        }

        @Override
        public View getCustomView() {
            return LayoutInflater.from(mContext).inflate(R.layout.view_select_area_codes, null);
        }

        @Override
        public void setupView(View view) {
            mRecyclerView = view.findViewById(R.id.recyclerView);
            mCancel = view.findViewById(R.id.cancel);
            mProgressBar = view.findViewById(R.id.progressBar);
        }

        public void setAreaCodeList(List<AreaCode> areaCodeList) {
            mAreaCodeList = areaCodeList;

            mRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
            mRecyclerView.addItemDecoration(dividerItemDecoration);
            AreaCodeAdapter adapter = new AreaCodeAdapter(mAreaCodeList);
            mRecyclerView.setAdapter(adapter);
        }
    }

    public static class AreaCodeAdapter extends RecyclerView.Adapter<AreaCodeAdapter.ViewHolder> {

        private List<AreaCode> mAreaCodeList;

        public AreaCodeAdapter(List<AreaCode> areaCodeList) {
            mAreaCodeList = areaCodeList;
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
            holder.mCountryName.setText(areaCode.getName());
            holder.mAreaCode.setText(areaCode.getTeleCode());
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
        mPhoneAuthCode.setVisibility(View.VISIBLE);

        mEmail.setVisibility(View.GONE);
        mEmailAuthCode.setVisibility(View.GONE);
    }

    private void switchToEmailRegister() {
        mPageTitle.setText(R.string.email_register);
        mRegisterTypeSwitch.setText(R.string.phone_register);

        mPhoneLine.setVisibility(View.GONE);
        mPhoneAuthCode.setVisibility(View.GONE);

        mEmail.setVisibility(View.VISIBLE);
        mEmailAuthCode.setVisibility(View.VISIBLE);
    }

    private boolean isPhoneRegister() {
        return mPhoneAuthCode.getVisibility() == View.VISIBLE;
    }
}
