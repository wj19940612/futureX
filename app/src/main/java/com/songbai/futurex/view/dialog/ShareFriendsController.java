package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yangguangda
 * @date 2018/8/1
 */
public class ShareFriendsController extends SmartDialog.CustomViewController {
    @BindView(R.id.selectHint)
    TextView mSelectHint;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.cancel)
    TextView mCancel;
    private Context mContext;

    public ShareFriendsController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        View view = View.inflate(mContext, R.layout.view_share_friends, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onInitView(View view, final SmartDialog dialog) {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,4));
        mRecyclerView.setAdapter(new ShareItemAdapter());
    }

    private class ShareItemAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
