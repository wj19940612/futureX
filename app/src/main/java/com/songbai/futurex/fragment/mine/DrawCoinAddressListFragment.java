package com.songbai.futurex.fragment.mine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.CoinAddress;
import com.songbai.futurex.model.mine.CoinAddressCount;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.view.EmptyRecyclerView;
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
public class DrawCoinAddressListFragment extends UniqueActivity.UniFragment implements
        CopyDeleteController.OnItemClickListener {
    public static final int REQUEST_ADD_ADDRESS = 11233;
    public static final int DRAW_COIN_ADDRESS_RESULT = 11234;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;
    private Unbinder mBind;
    private CoinAddressCount mCoinAddressCount;
    private DrawCoinAddressListAdapter mAdapter;
    private boolean mModified;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw_coin_address_list, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mCoinAddressCount = extras.getParcelable(ExtraKeys.COIN_ADDRESS_INFO);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mTitleBar.setTitle(mCoinAddressCount.getCoinType().toUpperCase());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setEmptyView(mEmptyView);
        mAdapter = new DrawCoinAddressListAdapter();
        mAdapter.setOnItemClickListener(new DrawCoinAddressListAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(CoinAddress coinAddress) {
                showDeleteView(coinAddress);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        getDrawWalletAddrByCoinType(mCoinAddressCount.getCoinType());
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
        Apic.getDrawWalletAddrByCoinType(coinType).tag(TAG)
                .callback(new Callback<Resp<ArrayList<CoinAddress>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<CoinAddress>> resp) {
                        ArrayList<CoinAddress> data = resp.getData();
                        mAdapter.setList(data);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.hideAll(false);
                    }
                })
                .fireFreely();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick(R.id.addAddress)
    public void onViewClicked() {
        UniqueActivity.launcher(this, AddAddressFragment.class).putExtra(ExtraKeys.COIN_ADDRESS_INFO, mCoinAddressCount).execute(this, REQUEST_ADD_ADDRESS);
    }

    @Override
    public void onCopyClick(CoinAddress coinAddress) {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setPrimaryClip(ClipData.newPlainText(null, coinAddress.getToAddr()));
        ToastUtil.show(R.string.copy_success);
    }

    @Override
    public void onDeleteClick(CoinAddress coinAddress) {
        removeDrawWalletAddr(coinAddress);
    }

    private void removeDrawWalletAddr(final CoinAddress coinAddress) {
        Apic.removeDrawWalletAddr(coinAddress.getId()).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        mAdapter.removeItem(coinAddress);
                        mAdapter.notifyDataSetChanged();
                        mModified = true;
                        setResult(DRAW_COIN_ADDRESS_RESULT,
                                new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, mModified));
                    }
                })
                .fire();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ADDRESS) {
            if (data != null) {
                boolean shouldRefresh = data.getBooleanExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, false);
                if (shouldRefresh) {
                    getDrawWalletAddrByCoinType(mCoinAddressCount.getCoinType());
                    mModified = shouldRefresh;
                    setResult(DRAW_COIN_ADDRESS_RESULT,
                            new Intent().putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, mModified));
                }
            }
        }
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
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sOnItemClickListener != null) {
                            sOnItemClickListener.onItemLongClick(coinAddress);
                        }
                    }
                });
            }
        }
    }
}
