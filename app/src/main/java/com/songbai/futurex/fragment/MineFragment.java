package com.songbai.futurex.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.mine.PersonalDataActivity;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/29
 */
public class MineFragment extends BaseFragment {

    @BindView(R.id.headPortrait)
    ImageView mHeadPortrait;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.safeGrade)
    TextView mSafeGrade;
    @BindView(R.id.userPhone)
    TextView mUserPhone;
    @BindView(R.id.userInfoGroup)
    ConstraintLayout mUserInfoGroup;
    @BindView(R.id.login)
    TextView mLogin;
    @BindView(R.id.msgCenter)
    IconTextRow mMsgCenter;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        mBind = ButterKnife.bind(view);
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
//            Apic.getMsgCount().callback(new Callback<String>() {
//                @Override
//                protected void onRespSuccess(String resp) {
//                    mMsgCenter.setSubText(resp);
//                }
//            }).fire();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.property, R.id.tradeOrderLog, R.id.fiatTradeOrder, R.id.invite, R.id.msgCenter, R.id.safetyCenter, R.id.customService, R.id.settings})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.property:
                Launcher.with(this, PersonalDataActivity.class).execute();
                break;
            case R.id.tradeOrderLog:
                break;
            case R.id.fiatTradeOrder:
                break;
            case R.id.invite:
                break;
            case R.id.msgCenter:
                break;
            case R.id.safetyCenter:
                break;
            case R.id.customService:
                break;
            case R.id.settings:
                break;
            default:
        }
    }
}
