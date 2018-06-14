package com.songbai.futurex.fragment.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.CoinAddressCount;
import com.songbai.futurex.view.IconTextRow;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class DrawCoinAddressFragment extends UniqueActivity.UniFragment {
    public static final int REQUEST_ADDRESSLIST = 12312;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private Unbinder mBind;
    private DrawCoinAddressAdapter mAdapter;

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
        mAdapter = new DrawCoinAddressAdapter();
        mAdapter.setOnAddressTypeClickListener(new DrawCoinAddressAdapter.OnAddressTypeClickListener() {
            @Override
            public void onAddressTypeClick(CoinAddressCount coinAddressCount) {
                UniqueActivity.launcher(getActivity(), DrawCoinAddressListFragment.class)
                        .putExtra(ExtraKeys.COIN_ADDRESS_INFO, coinAddressCount)
                        .execute(DrawCoinAddressFragment.this, REQUEST_ADDRESSLIST);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        countDrawWalletAddrByCoinType();
    }

    private void countDrawWalletAddrByCoinType() {
        Apic.countDrawWalletAddrByCoinType("")
                .callback(new Callback<Resp<ArrayList<CoinAddressCount>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<CoinAddressCount>> resp) {
                        mAdapter.setList(resp.getData());
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .fire();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADDRESSLIST) {
            if (data != null) {
                boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, false);
                if (shouldRefresh) {
                    countDrawWalletAddrByCoinType();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    static class DrawCoinAddressAdapter extends RecyclerView.Adapter {
        private static OnAddressTypeClickListener mOnAddressTypeClickListener;
        private ArrayList<CoinAddressCount> mList = new ArrayList<CoinAddressCount>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_draw_coin_address_type, parent, false);
            return new DrawCoinAddressTypeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof DrawCoinAddressTypeHolder) {
                ((DrawCoinAddressTypeHolder) holder).bindDate(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        void setOnAddressTypeClickListener(OnAddressTypeClickListener onAddressTypeClickListener) {
            mOnAddressTypeClickListener = onAddressTypeClickListener;
        }

        public ArrayList<CoinAddressCount> getList() {
            return mList;
        }

        public void setList(ArrayList<CoinAddressCount> list) {
            mList.clear();
            mList.addAll(list);
        }

        static class DrawCoinAddressTypeHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.addressType)
            IconTextRow mAddressType;

            DrawCoinAddressTypeHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            private void bindDate(final CoinAddressCount coinAddressCount) {
                mAddressType.setText(coinAddressCount.getCoinType().toUpperCase());
                mAddressType.setSubText(String.valueOf(coinAddressCount.getCount()));
                mAddressType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnAddressTypeClickListener != null) {
                            mOnAddressTypeClickListener.onAddressTypeClick(coinAddressCount);
                        }
                    }
                });
            }
        }

        interface OnAddressTypeClickListener {
            void onAddressTypeClick(CoinAddressCount coinInfo);
        }
    }
}
