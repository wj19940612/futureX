package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/6/4
 */
public class LegalCurrencyPayFragment extends UniqueActivity.UniFragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.addGathering)
    FrameLayout mAddGathering;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_legal_currency, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new LegalCurrencyPayAdapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.addGathering)
    public void onViewClicked() {
        UniqueActivity.launcher(this, SelectPayTypeFragment.class).execute();
    }

    static class LegalCurrencyPayAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_legal_currency, parent, false);
            return new LegalCurrencyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof LegalCurrencyHolder) {
                ((LegalCurrencyHolder) holder).bindData();
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        static class LegalCurrencyHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.icon)
            ImageView mIcon;
            @BindView(R.id.accountName)
            TextView mAccountName;
            @BindView(R.id.account)
            TextView mAccount;
            @BindView(R.id.delete)
            TextView mDelete;

            LegalCurrencyHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            void bindData() {

            }
        }
    }
}
