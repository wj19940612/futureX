package com.songbai.futurex.wrapper.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.wrapper.Apic;
import com.songbai.futurex.wrapper.FeedbackActivity;
import com.songbai.futurex.wrapper.LocalWrapUser;
import com.songbai.futurex.wrapper.ProfileActivity;
import com.songbai.futurex.wrapper.SettingActivity;
import com.songbai.futurex.wrapper.WrapLoginActivity;
import com.songbai.futurex.wrapper.WrapUserInfo;
import com.songbai.wrapres.IconTextRow;
import com.songbai.wrapres.SmartDialog;
import com.songbai.wrapres.model.Operation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * Modified by john on 2018/7/11
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class WrapMineFragment extends BaseFragment {

    @BindView(R.id.headPortrait)
    ImageView mHeadPortrait;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.headLayout)
    RelativeLayout mHeadLayout;
    @BindView(R.id.contribute)
    IconTextRow mContribute;
    @BindView(R.id.feedBack)
    IconTextRow mFeedBack;
    @BindView(R.id.setting)
    IconTextRow mSetting;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wrap_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserLoginStatus();
    }

    private void updateUserLoginStatus() {
        updateUserHeadPortrait();
        WrapUserInfo userInfo = LocalWrapUser.getUser().getUserInfo();
        if (LocalWrapUser.getUser().isLogin()) {
            mUserName.setText(userInfo.getUserName());
        } else {
            mUserName.setText(R.string.click_login);
        }
    }

    private void updateUserHeadPortrait() {
        if (LocalWrapUser.getUser().isLogin()) {
            GlideApp.with(getActivity())
                    .load(LocalWrapUser.getUser().getUserInfo().getUserPortrait())
                    .placeholder(R.drawable.ic_default_head_portrait)
                    .circleCrop()
                    .into(mHeadPortrait);
        } else {
            GlideApp.with(getActivity())
                    .load(R.drawable.ic_default_head_portrait)
                    .circleCrop()
                    .into(mHeadPortrait);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.headPortrait, R.id.userName, R.id.contribute, R.id.feedBack, R.id.setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.headPortrait:
            case R.id.userName:
                if (LocalWrapUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), ProfileActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), WrapLoginActivity.class).execute();
                }
                break;
            case R.id.contribute:
                requestOperationWeChatAccount();
                break;
            case R.id.feedBack:
                Launcher.with(getActivity(), FeedbackActivity.class).execute();
                break;
            case R.id.setting:
                Launcher.with(getActivity(), SettingActivity.class).execute();
                break;
        }
    }

    private void requestOperationWeChatAccount() {
        Apic.requestOperationSetting(Operation.OPERATION_TYPE_WE_CHAT)
                .tag(TAG)
                .callback(new Callback4Resp<Resp<Operation>, Operation>() {
                    @Override
                    protected void onRespData(Operation data) {
                        showAddWeChatAccountDialog(data.getSYS_OPERATE_WX());
                    }
                })
                .fire();
    }

    private void showAddWeChatAccountDialog(final String data) {
        SmartDialog.with(getActivity(), getString(R.string.please_add_us_wechat_account, data))
                .setPositive(R.string.copy_us_wechat_account, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        copyWeChatAccount(data);
                        ToastUtil.show(R.string.copy_success);
                    }
                })
                .show();
    }

    private void copyWeChatAccount(String data) {
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, data);
        clipboardManager.setPrimaryClip(clipData);
    }
}
