package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class DrawCoinAddressFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw_coin_address, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DrawCoinAddressAdapter adapter = new DrawCoinAddressAdapter();
        adapter.setOnAddressTypeClickListener(new DrawCoinAddressAdapter.OnAddressTypeClickListener() {
            @Override
            public void onAddressTypeClick() {
                UniqueActivity.launcher(getActivity(), DrawCoinAddressListFragment.class).execute();
            }
        });
        mRecyclerView.setAdapter(adapter);
        getDrawWalletAddrByCoinType();
    }

    private void getDrawWalletAddrByCoinType() {
        Apic.getDrawWalletAddrByCoinType()
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {

                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    static class DrawCoinAddressAdapter extends RecyclerView.Adapter {
        private static OnAddressTypeClickListener mOnAddressTypeClickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_draw_coin_address_type, parent, false);
            return new DrawCoinAddressTypeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof DrawCoinAddressTypeHolder) {
                ((DrawCoinAddressTypeHolder) holder).bindDate();
            }
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        void setOnAddressTypeClickListener(OnAddressTypeClickListener onAddressTypeClickListener) {
            mOnAddressTypeClickListener = onAddressTypeClickListener;
        }

        static class DrawCoinAddressTypeHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.addressType)
            IconTextRow mAddressType;

            DrawCoinAddressTypeHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            private void bindDate() {
                mAddressType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnAddressTypeClickListener != null) {
                            mOnAddressTypeClickListener.onAddressTypeClick();
                        }
                    }
                });
            }
        }

        interface OnAddressTypeClickListener {
            void onAddressTypeClick();
        }
    }
}
