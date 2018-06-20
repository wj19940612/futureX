package com.songbai.futurex.activity.mine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.PromoterInfo;
import com.songbai.futurex.utils.AnimatorUtil;
import com.songbai.futurex.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yangguangda
 * @date 2018/5/31
 */
public class InviteActivity extends BaseActivity {
    @BindView(R.id.rules)
    TextView mRules;
    @BindView(R.id.inviteCode)
    TextView mInviteCode;
    @BindView(R.id.checkDetail)
    TextView mCheckDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);
        Apic.getCurrentPromoterMsg()
                .callback(new Callback<Resp<PromoterInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<PromoterInfo> resp) {
                        PromoterInfo promoterInfo = resp.getData();
                        mInviteCode.setText(promoterInfo.getCode());
                    }
                })
                .fire();
    }

    @OnClick({R.id.checkDetail, R.id.joinNow, R.id.copy, R.id.inviteBuddies, R.id.createPoster})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.checkDetail:
                boolean b = mRules.getVisibility() == View.GONE;
                if (b) {
                    expand(mRules);
                } else {
                    collapse(mRules);
                }
                break;
            case R.id.joinNow:
                break;
            case R.id.copy:
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setPrimaryClip(ClipData.newPlainText(null, mInviteCode.getText()));
                ToastUtil.show(R.string.copy_success);
                break;
            case R.id.inviteBuddies:
                break;
            case R.id.createPoster:
                break;
            default:
        }
    }

    // 展开
    private void expand(final View view) {
        AnimatorUtil.expandVertical(view, new AnimatorUtil.OnAnimatorFactionListener() {
            @Override
            public void onFaction(float fraction) {
                if (fraction == 1) {
                    mCheckDetail.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_invitation_angle_up, 0);
                }
            }
        });
    }

    // 折叠
    private void collapse(final View view) {
        AnimatorUtil.collapseVertical(view, new AnimatorUtil.OnAnimatorFactionListener() {
            @Override
            public void onFaction(float fraction) {
                if (fraction == 1) {
                    view.setVisibility(View.GONE);
                    mCheckDetail.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_invitation_angle_down, 0);
                }
            }
        });
    }
}
