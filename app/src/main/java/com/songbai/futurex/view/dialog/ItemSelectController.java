package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.SmartDialog;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class ItemSelectController extends SmartDialog.CustomViewController {
    private Context mContext;
    private RecyclerView mRecycleView;
    private TextView mSelectHint;
    private String mHintText;
    private RecyclerView.Adapter mAdapter;

    public ItemSelectController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_bottom_item_select, null);
    }

    @Override
    protected void onInitView(View view, final SmartDialog dialog) {
        mRecycleView = view.findViewById(R.id.recyclerView);
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mSelectHint = view.findViewById(R.id.selectHint);
        TextView cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        setHintText(mHintText);
        setAdapter(mAdapter);
    }

    public void setHintText(String hintText) {
        mHintText = hintText;
        if (mSelectHint != null) {
            mSelectHint.setText(hintText);
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        if (mRecycleView != null) {
            mRecycleView.setAdapter(adapter);
        }
    }
}
