package com.songbai.futurex.fragment.mine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.songbai.futurex.BuildConfig;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.local.SupportLang;
import com.songbai.futurex.utils.LanguageUtils;
import com.songbai.futurex.view.IconTextRow;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.MsgHintController;
import com.songbai.futurex.websocket.MessageProcessor;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class SettingsFragment extends UniqueActivity.UniFragment {
    private static final int SETTINGS_RESULT = 12354;
    @BindView(R.id.language)
    IconTextRow mLanguage;
    @BindView(R.id.logout)
    TextView mLogout;
    @BindView(R.id.pricingMethod)
    IconTextRow mPricingMethod;
    @BindView(R.id.quickBtn)
    ImageView mQuickBtn;
    @BindView(R.id.pushBtn)
    ImageView mPushBtn;
    @BindView(R.id.hostGroup)
    LinearLayout mHostGroup;
    @BindView(R.id.radioGroup)
    RadioGroup mRadioGroup;
    @BindView(R.id.pro)
    RadioButton mPro;
    @BindView(R.id.test)
    RadioButton mTest;
    private Unbinder mBind;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mLogout.setVisibility(LocalUser.getUser().isLogin() ? View.VISIBLE : View.GONE);
        String currentLanguageStr = Preference.get().getCurrentLangageStr();
        if (!TextUtils.isEmpty(currentLanguageStr)) {
            mLanguage.setSubText(currentLanguageStr);
        } else {
            getSupportLocal();
        }
        String pricingMethod = Preference.get().getPricingMethod();
        switch (pricingMethod) {
            case "cny":
                mPricingMethod.setSubText(R.string.cny);
                break;
            case "usd":
                mPricingMethod.setSubText(R.string.usd);
                break;
            case "twd":
                mPricingMethod.setSubText(R.string.twd);
                break;
            default:
        }
        initView();
        switchHostInAlpha();
    }

    private void switchHostInAlpha() {
        if ("alpha".equals(BuildConfig.FLAVOR)) {
            mTest.setChecked("ex.esongbai.xyz".equals(Preference.get().getAlphaHost()));
            mPro.setChecked(!"ex.esongbai.xyz".equals(Preference.get().getAlphaHost()));
        }
        mHostGroup.setVisibility("alpha".equals(BuildConfig.FLAVOR) ? View.VISIBLE : View.GONE);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.pro:
                        Log.e("wtf", "onCheckedChanged:pro " + checkedId);
                        Preference.get().setAlphaHost("m.bitfutu.re");
                        restart();
                        break;
                    case R.id.test:
                        Log.e("wtf", "onCheckedChanged:test " + checkedId);
                        Preference.get().setAlphaHost("ex.esongbai.xyz");
                        restart();
                        break;
                    default:
                }
            }
        });
    }

    public void restart() {
        Intent intent = getActivity().getPackageManager()
                .getLaunchIntentForPackage(getActivity().getApplication().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent); // 0.1秒钟后重启应用
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void setOriginLanguageText(ArrayList<SupportLang> data) {
        if (data != null) {
            Locale userLocale = LanguageUtils.getUserLocale(getContext());
            for (SupportLang supportLang : data) {
                boolean match;
                String country = supportLang.getCountry();
                if (TextUtils.isEmpty(country)) {
                    match = userLocale.getLanguage().equals(supportLang.getLang());
                } else {
                    match = userLocale.getLanguage().equals(supportLang.getLang()) && userLocale.getCountry().equals(country);
                }
                if (match) {
                    mLanguage.setSubText(supportLang.getName());
                    Preference.get().setCurrentLangageStr(supportLang.getName());
                }
            }
        }
    }

    private void getSupportLocal() {
        Apic.getSupportLang().tag(TAG)
                .callback(new Callback<Resp<ArrayList<SupportLang>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<SupportLang>> resp) {
                        setOriginLanguageText(resp.getData());
                    }
                }).fireFreely();
    }

    private void initView() {
        mQuickBtn.setSelected(Preference.get().isQuickExchange());
        LocalUser localUser = LocalUser.getUser();
        if (localUser.isLogin()) {
            UserInfo userInfo = localUser.getUserInfo();
            mPushBtn.setVisibility(View.VISIBLE);
            mPushBtn.setSelected(userInfo.getEntrustPush() == 1);
        } else {
            mPushBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.language, R.id.pricingMethod, R.id.aboutUs, R.id.feedback, R.id.logout, R.id.quickBtn,
            R.id.pushBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.language:
                UniqueActivity.launcher(getActivity(), SettingLanguageFragment.class).execute();
                break;
            case R.id.pricingMethod:
                UniqueActivity.launcher(getActivity(), SelectPricingFragment.class).execute();
                break;
            case R.id.aboutUs:
                UniqueActivity.launcher(getActivity(), AboutUsFragment.class).execute();
                break;
            case R.id.feedback:
                UniqueActivity.launcher(getActivity(), FeedbackFragment.class).execute();
                break;
            case R.id.logout:
                showLogoutView();
                break;
            case R.id.quickBtn:
                clickQuickBtn();
                break;
            case R.id.pushBtn:
                clickPushBtn();
                break;
            default:
        }
    }

    private void clickPushBtn() {
        mPushBtn.setEnabled(false);
        Apic.turnRemindingPush(mPushBtn.isSelected() ? 0 : 1).callback(new Callback<Resp>() {
            @Override
            protected void onRespSuccess(Resp resp) {
                mPushBtn.setSelected(!mPushBtn.isSelected());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mPushBtn.setEnabled(true);
            }
        }).fire();
    }

    private void clickQuickBtn() {
        mQuickBtn.setEnabled(false);
        Preference.get().setQuickExchange(!mQuickBtn.isSelected());
        mQuickBtn.setSelected(!mQuickBtn.isSelected());
        mQuickBtn.setEnabled(true);
    }

    private void showLogoutView() {
        MsgHintController withDrawPsdViewController = new MsgHintController(getActivity(), new MsgHintController.OnClickListener() {
            @Override
            public void onConfirmClick() {
                logout();
            }
        });
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog.setCustomViewController(withDrawPsdViewController)
                .show();
        withDrawPsdViewController.setMsg(R.string.confirm_logout);
        withDrawPsdViewController.setImageRes(0);
    }

    private void logout() {
        Apic.logout().tag(TAG).indeterminate(this)
                .callback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        MessageProcessor.get().unRegister();
                        LocalUser.getUser().logout();
                        Preference.get().setOptionalListRefresh(true);
                        Preference.get().setPosterListRefresh(true);
                        setResult(SETTINGS_RESULT, new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true));
                        finish();
                    }
                }).fire();
    }
}
