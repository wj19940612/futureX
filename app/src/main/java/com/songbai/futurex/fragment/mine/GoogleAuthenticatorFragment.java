package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.songbai.futurex.model.mine.CreateGoogleKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class GoogleAuthenticatorFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.qcCode)
    ImageView mQcCode;
    @BindView(R.id.secretKey)
    TextView mSecretKey;
    @BindView(R.id.googleAuthCode)
    EditText mGoogleAuthCode;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_authenticator, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        createGoogleKey();
    }

    private void createGoogleKey() {
        Apic.createGoogleKey()
                .callback(new Callback<Resp<CreateGoogleKey>>() {
                    @Override
                    protected void onRespSuccess(Resp<CreateGoogleKey> resp) {
                        setGoogleKey(resp.getData());
                    }
                })
                .fire();
    }

    private void setGoogleKey(CreateGoogleKey data) {
        GlideApp
                .with(this)
                .load(data.getQrCode())
                .into(mQcCode);
        mSecretKey.setText(data.getGoogleKey());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.saveQcCode, R.id.copy, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saveQcCode:
                break;
            case R.id.copy:
                break;
            case R.id.confirm:
                break;
            default:
        }
    }
}
