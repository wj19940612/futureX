package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.songbai.futurex.R;
import com.songbai.futurex.view.SmartDialog;

/**
 * Modified by john on 2018/6/1
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class AuthCodeViewController implements SmartDialog.CustomViewController {

    private Context mContext;

    public AuthCodeViewController(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_auth_code, null);
    }

    @Override
    public void setupView(View view, SmartDialog dialog) {

    }
}
