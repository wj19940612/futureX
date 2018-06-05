package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class ReChargeCoinFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.qcCode)
    ImageView mQcCode;
    @BindView(R.id.address)
    TextView mAddress;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recharge_coin, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(ReChargeCoinFragment.this,RechargeHistoryActivity.class).execute();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.saveQcCode, R.id.copyAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saveQcCode:
                break;
            case R.id.copyAddress:
                break;
            default:
        }
    }
}
