package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.SmartDialog;

/**
 * Modified by john on 2018/8/11
 * <p>
 * Description: 开启 app 时候必须权限说明弹框
 * <p>
 * APIs:
 */
public class PermissionViewController extends SmartDialog.CustomViewController {

    private Context mContext;
    private OnClickListener mOnClickListener;

    private TextView mTitle;
    private TextView mClose;
    private TextView mOpen;


    public interface OnClickListener {
        void onOpenClick(SmartDialog dialog);

        void onCloseClick(SmartDialog dialog);
    }

    public PermissionViewController(Context context, OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_permission, null);
    }

    @Override
    protected void onInitView(View view, final SmartDialog dialog) {
        mTitle = view.findViewById(R.id.title);
        mClose = view.findViewById(R.id.close);
        mOpen = view.findViewById(R.id.openPermission);

        String appName = mContext.getString(R.string.app_name);
        mTitle.setText(mContext.getString(R.string.x_app_need_permissions_below, appName));

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onCloseClick(dialog);
            }
        });
        mOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onOpenClick(dialog);
            }
        });
    }
}
