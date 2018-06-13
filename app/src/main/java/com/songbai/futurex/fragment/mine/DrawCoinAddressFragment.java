package com.songbai.futurex.fragment.mine;

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
import com.songbai.futurex.model.local.CoinAddressCountWrapper;
import com.songbai.futurex.model.mine.CoinAddress;
import com.songbai.futurex.model.mine.CoinInfo;
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
            public void onAddressTypeClick(CoinInfo coinInfo) {
                UniqueActivity.launcher(getActivity(), DrawCoinAddressListFragment.class).putExtra(ExtraKeys.COIN_INFO, coinInfo).execute();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        coinLoadSimpleList();
    }

    private void coinLoadSimpleList() {
        Apic.coinLoadSimpleList()
                .callback(new Callback<Resp<ArrayList<CoinInfo>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<CoinInfo>> resp) {
                        ArrayList<CoinInfo> coinInfos = resp.getData();
                        ArrayList<CoinAddressCountWrapper> coinAddressCountWrappers = new ArrayList<>();
                        for (CoinInfo coinInfo : coinInfos) {
                            CoinAddressCountWrapper coinAddressCountWrapper = new CoinAddressCountWrapper();
                            coinAddressCountWrapper.setCoinInfo(coinInfo);
                            coinAddressCountWrappers.add(coinAddressCountWrapper);
                        }
                        mAdapter.setList(coinAddressCountWrappers);
                        mAdapter.notifyDataSetChanged();
                        getDrawWalletAddrByCoinType();
                    }
                })
                .fire();
    }

    private void getDrawWalletAddrByCoinType() {
        Apic.getDrawWalletAddrByCoinType("")
                .callback(new Callback<Resp<ArrayList<CoinAddress>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<CoinAddress>> resp) {
                        sortAndSetAddressCount(resp);
                    }
                })
                .fire();
    }

    private void sortAndSetAddressCount(Resp<ArrayList<CoinAddress>> resp) {
        ArrayList<CoinAddress> coinAddresses = resp.getData();
        for (CoinAddressCountWrapper addressCountWrapper : mAdapter.getList()) {
            String symbol = addressCountWrapper.getCoinInfo().getSymbol();
            for (CoinAddress coinAddress : coinAddresses) {
                if (symbol.equalsIgnoreCase(coinAddress.getCoinType())) {
                    int count = addressCountWrapper.getCount();
                    addressCountWrapper.setCount(count + 1);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    static class DrawCoinAddressAdapter extends RecyclerView.Adapter {
        private static OnAddressTypeClickListener mOnAddressTypeClickListener;
        private ArrayList<CoinAddressCountWrapper> mList = new ArrayList<CoinAddressCountWrapper>();

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

        public ArrayList<CoinAddressCountWrapper> getList() {
            return mList;
        }

        public void setList(ArrayList<CoinAddressCountWrapper> list) {
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

            private void bindDate(CoinAddressCountWrapper coinAddressCountWrapper) {
                final CoinInfo coinInfo = coinAddressCountWrapper.getCoinInfo();
                mAddressType.setText(coinInfo.getSymbol().toUpperCase());
                mAddressType.setSubText(String.valueOf(coinAddressCountWrapper.getCount()));
                mAddressType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnAddressTypeClickListener != null) {
                            mOnAddressTypeClickListener.onAddressTypeClick(coinInfo);
                        }
                    }
                });
            }
        }

        interface OnAddressTypeClickListener {
            void onAddressTypeClick(CoinInfo coinInfo);
        }
    }
}
