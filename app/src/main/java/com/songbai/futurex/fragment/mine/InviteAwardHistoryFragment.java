package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.view.EmptyRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/7/31
 */
public class InviteAwardHistoryFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    private Unbinder mBind;

    public static InviteAwardHistoryFragment newInstance() {
        Bundle bundle = new Bundle();
        InviteAwardHistoryFragment inviteAwardHistoryFragment = new InviteAwardHistoryFragment();
        inviteAwardHistoryFragment.setArguments(bundle);
        return inviteAwardHistoryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_award_hisotory, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new InviteAwardHistoryAdapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    class InviteAwardHistoryAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_invite_award_history, parent, false);
            ButterKnife.bind(this, view);
            return new InviteAwardHistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class InviteAwardHistoryHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.timestamp)
            TextView mTimestamp;
            @BindView(R.id.coinType)
            TextView mCoinType;
            @BindView(R.id.volume)
            TextView mVolume;

            public InviteAwardHistoryHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindData() {

            }
        }
    }
}
