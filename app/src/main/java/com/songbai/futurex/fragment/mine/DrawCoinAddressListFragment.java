package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.CoinAddress;
import com.songbai.futurex.model.mine.CoinInfo;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.CopyDeleteController;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class DrawCoinAddressListFragment extends UniqueActivity.UniFragment implements CopyDeleteController.OnItemClickListener {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private Unbinder mBind;
    private CoinInfo mCoinInfo;
    private DrawCoinAddressListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw_coin_address_list, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mCoinInfo = extras.getParcelable(ExtraKeys.COIN_INFO);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mTitleBar.setTitle(mCoinInfo.getSymbol().toUpperCase());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new DrawCoinAddressListAdapter();
        mAdapter.setOnItemClickListener(new DrawCoinAddressListAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(CoinAddress coinAddress) {
                showDeleteView(coinAddress);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        getDrawWalletAddrByCoinType(mCoinInfo.getSymbol());
    }

    private void showDeleteView(CoinAddress coinAddress) {
        CopyDeleteController copyDeleteController = new CopyDeleteController(getContext());
        copyDeleteController.setOnItemClickListener(this);
        copyDeleteController.setCoinAddress(coinAddress);
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(copyDeleteController)
                .show();
    }

    private void getDrawWalletAddrByCoinType(String coinType) {
        Apic.getDrawWalletAddrByCoinType(coinType)
                .callback(new Callback<Resp<ArrayList<CoinAddress>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<CoinAddress>> resp) {
                        mAdapter.setList(resp.getData());
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick(R.id.addAddress)
    public void onViewClicked() {
        UniqueActivity.launcher(this, AddAddressFragment.class).putExtra(ExtraKeys.COIN_INFO, mCoinInfo).execute();
    }

    @Override
    public void onCopyClick() {

    }

    @Override
    public void onDeleteClick(CoinAddress coinAddress) {
        mAdapter.removeItem(coinAddress);
        mAdapter.notifyDataSetChanged();
    }

    static class DrawCoinAddressListAdapter extends RecyclerView.Adapter {

        private ArrayList<CoinAddress> mList = new ArrayList<>();
        private static OnItemClickListener sOnItemClickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_draw_coin_address, parent, false);
            return new DrawCoinAddressHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof DrawCoinAddressHolder) {
                ((DrawCoinAddressHolder) holder).bindHolder(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(ArrayList<CoinAddress> list) {
            mList.clear();
            mList.addAll(list);
        }

        public void removeItem(CoinAddress coinAddress) {
            mList.remove(coinAddress);
        }

        interface OnItemClickListener {
            void onItemLongClick(CoinAddress coinAddress);
        }

        void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            sOnItemClickListener = onItemClickListener;
        }

        static class DrawCoinAddressHolder extends RecyclerView.ViewHolder {
            private View mRootView;
            @BindView(R.id.remark)
            TextView mRemark;
            @BindView(R.id.toAddress)
            TextView mToAddress;

            DrawCoinAddressHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                mRootView = view;
            }

            void bindHolder(final CoinAddress coinAddress) {
                mRemark.setText(coinAddress.getRemark());
                mToAddress.setText(coinAddress.getToAddr());
                mRootView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (sOnItemClickListener != null) {
                            sOnItemClickListener.onItemLongClick(coinAddress);
                        }
                        return true;
                    }
                });
            }
        }
    }
}
