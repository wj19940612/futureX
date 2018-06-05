package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/6/4
 */
public class AddPayFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.accountName)
    TextView mAccountName;
    @BindView(R.id.accountNum)
    EditText mAccountNum;
    @BindView(R.id.realName)
    EditText mRealName;
    @BindView(R.id.confirm_add)
    TextView mConfirmAdd;
    Unbinder unbinder;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_pay, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        if (false) {
            mTitleBar.setTitle(R.string.add_ali_pay);
            mAccountName.setText(R.string.ali_pay_account);
            mAccountNum.setHint(R.string.please_input_ali_pay_account);
        } else {
            mTitleBar.setTitle(R.string.add_wei_chat_pay);
            mAccountName.setText(R.string.wei_chat_account);
            mAccountNum.setHint(R.string.please_input_wei_chat_account);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.confirm_add)
    public void onViewClicked() {
    }
}
