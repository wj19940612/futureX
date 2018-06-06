package com.songbai.futurex.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.mine.InviteActivity;
import com.songbai.futurex.activity.mine.MyPropertyActivity;
import com.songbai.futurex.activity.mine.PersonalDataActivity;
import com.songbai.futurex.fragment.mine.CustomerServiceFragment;
import com.songbai.futurex.fragment.mine.MessageCenterFragment;
import com.songbai.futurex.fragment.mine.SafetyCenterFragment;
import com.songbai.futurex.fragment.mine.SettingsFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.UnreadMessageCount;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/5/29
 */
public class MineFragment extends BaseFragment {

    @BindView(R.id.headLayout)
    ConstraintLayout mHeadLayout;
    @BindView(R.id.headPortrait)
    ImageView mHeadPortrait;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.userPhone)
    TextView mUserPhone;
    @BindView(R.id.userInfoGroup)
    LinearLayout mUserInfoGroup;
    @BindView(R.id.login)
    TextView mLogin;
    @BindView(R.id.msgCenter)
    IconTextRow mMsgCenter;
    @BindView(R.id.property)
    IconTextRow mProperty;
    @BindView(R.id.tradeOrderLog)
    IconTextRow mTradeOrderLog;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        mBind = ButterKnife.bind(this, view);
        ((BaseActivity) getActivity()).addStatusBarHeightPaddingTop(mHeadLayout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUserLoginStatus();
    }

    private void updateUserLoginStatus() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getMessageCount();
            setUserInfo();
        }
    }

    private void setUserInfo() {
        LocalUser localUser = LocalUser.getUser();
        if (localUser.isLogin()) {
            UserInfo userInfo = localUser.getUserInfo();
            GlideApp
                    .with(this)
                    .load(userInfo.getUserPortrait())
                    .into(mHeadPortrait);
            mUserName.setText(userInfo.getUserName());
            mUserPhone.setText(getString(R.string.phone_number_x, userInfo.getUserPhone()));
            int authenticationStatus = userInfo.getAuthenticationStatus();
            if (authenticationStatus == 1) {
                mUserName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_certification_primary, 0);
            } else if (authenticationStatus == 2) {
                mUserName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_certification_senior, 0);
            }
            mUserInfoGroup.setVisibility(View.VISIBLE);
            mLogin.setVisibility(View.GONE);
        } else {
            mLogin.setVisibility(View.VISIBLE);
            mUserInfoGroup.setVisibility(View.GONE);
        }
    }

    private void getMessageCount() {
        Apic.getMsgCount().callback(new Callback<Resp<UnreadMessageCount>>() {
            @Override
            protected void onRespSuccess(Resp<UnreadMessageCount> resp) {
                setUnreadMessageCount(resp.getData().getCount());
            }
        }).fire();
    }

    private void getUserInfo() {
        Apic.findUserInfo()
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                    }
                })
                .fire();
    }

    private void setUnreadMessageCount(Integer count) {
        mMsgCenter.getSubTextView().setVisibility(count == 0 ? View.INVISIBLE : View.VISIBLE);
        mMsgCenter.setSubText(String.valueOf(count));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.userInfoGroup, R.id.property, R.id.tradeOrderLog, R.id.legalCurrencyTradeOrder, R.id.invite,
            R.id.msgCenter, R.id.safetyCenter, R.id.customService, R.id.settings})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userInfoGroup:
                Launcher.with(this, PersonalDataActivity.class).execute();
                break;
            case R.id.property:
                Launcher.with(this, MyPropertyActivity.class).execute();
                break;
            case R.id.tradeOrderLog:
                break;
            case R.id.legalCurrencyTradeOrder:
                break;
            case R.id.invite:
                Launcher.with(this, InviteActivity.class).execute();
                break;
            case R.id.msgCenter:
                UniqueActivity.launcher(getActivity(), MessageCenterFragment.class).execute();
                break;
            case R.id.safetyCenter:
                UniqueActivity.launcher(getActivity(), SafetyCenterFragment.class).execute();
                break;
            case R.id.customService:
                UniqueActivity.launcher(getActivity(), CustomerServiceFragment.class).execute();
                break;
            case R.id.settings:
                UniqueActivity.launcher(getActivity(), SettingsFragment.class).execute();
                break;
            default:
        }
    }
}
