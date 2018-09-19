package com.songbai.futurex.activity.mine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.fragment.mine.MyInviteFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.PromoterInfo;
import com.songbai.futurex.model.mine.PromotionInfos;
import com.songbai.futurex.utils.AnimatorUtil;
import com.songbai.futurex.utils.AppInfo;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.UmengCountEventId;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.ShareFriendsDialogFragment;
import com.umeng.socialize.UMShareAPI;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sbai.com.glide.GlideApp;

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
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.eventPic)
    ImageView mEventPic;
    private String mPromotionGroup;
    private String mCode;
    private String mPromotionShare;
    private PromotionInfos.ShareContentBean mShareContent;
    private String mCoinType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalUser.getUser().isLogin()) {
                    UniqueActivity.launcher(InviteActivity.this, MyInviteFragment.class)
                            .putExtra(ExtraKeys.COIN_TYPE, mCoinType).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
        requestData();
    }

    private void requestData() {
        Apic.getCurrentPromoterMsg().tag(TAG)
                .callback(new Callback<Resp<PromoterInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<PromoterInfo> resp) {
                        PromoterInfo promoterInfo = resp.getData();
                        if (promoterInfo != null) {
                            mCoinType = promoterInfo.getCoinType();
                            mCode = promoterInfo.getCode();
                            mInviteCode.setText(mCode);
                        }
                    }
                })
                .fire();

        Apic.promotionRule().tag(TAG)
                .callback(new Callback<Resp<PromotionInfos>>() {
                    @Override
                    protected void onRespSuccess(Resp<PromotionInfos> resp) {
                        PromotionInfos promotionInfos = resp.getData();
                        if (promotionInfos != null) {
                            GlideApp
                                    .with(InviteActivity.this)
                                    .load(promotionInfos.getPromotionPic())
                                    .into(mEventPic);
                            Spanned html;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                html = Html.fromHtml(promotionInfos.getPromotionRule(), Html.FROM_HTML_MODE_COMPACT);
                            } else {
                                html = Html.fromHtml(promotionInfos.getPromotionRule());
                            }
                            mRules.setText(html);
                            mPromotionShare = promotionInfos.getPromotionShare();
                            mShareContent = promotionInfos.getShareContent();
                            mPromotionGroup = promotionInfos.getPromotionGroup();
                        }
                    }
                }).fireFreely();
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
                if (AppInfo.isAPPInstalled(this, "org.telegram.messenger")) {
                    Intent intent = new Intent();
                    intent.setPackage("org.telegram.messenger");
                    intent.setData(Uri.parse("https://t.me/" + mPromotionGroup));
                    intent.setAction(Intent.ACTION_VIEW);
                    startActivity(intent);
                } else {
                    ToastUtil.show(R.string.app_not_installed);
                }
                break;
            case R.id.copy:
                umengEventCount(UmengCountEventId.PROMOTE0003);
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setPrimaryClip(ClipData.newPlainText(null, mInviteCode.getText()));
                ToastUtil.show(R.string.copy_success);
                break;
            case R.id.inviteBuddies:
                umengEventCount(UmengCountEventId.PROMOTE0004);
                showShareDialog(false);
                break;
            case R.id.createPoster:
                umengEventCount(UmengCountEventId.PROMOTE0005);
                showShareDialog(true);
                break;
            default:
        }
    }

    private void showShareDialog(boolean hasPoster) {
        ShareFriendsDialogFragment shareFriendsDialogFragment = ShareFriendsDialogFragment.newInstance(hasPoster, mCode, mPromotionShare, mShareContent);
        shareFriendsDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void expand(final View view) {
        AnimatorUtil.expandVertical(view, new AnimatorUtil.OnAnimatorFactionListener() {
            @Override
            public void onFaction(float fraction) {
                if (fraction == 1) {
                    mCheckDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_invitation_angle_up, 0);
                }
            }
        });
    }

    private void collapse(final View view) {
        AnimatorUtil.collapseVertical(view, new AnimatorUtil.OnAnimatorFactionListener() {
            @Override
            public void onFaction(float fraction) {
                if (fraction == 1) {
                    view.setVisibility(View.GONE);
                    mCheckDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_invitation_angle_down, 0);
                }
            }
        });
    }
}
