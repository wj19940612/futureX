package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.AreaCode;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.StrFormatter;
import com.songbai.futurex.view.SmartDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yangguangda
 * @date 2018/7/18
 */
public class SelectAreaCodesViewController extends SmartDialog.CustomViewController {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private TextView mCancel;
    private ProgressBar mProgressBar;
    private List<AreaCode> mAreaCodeList;
    private SelectAreaCodesViewController.OnSelectListener mSelectListener;
    private SmartDialog mSmartDialog;

   public interface OnSelectListener {
        void onSelect(AreaCode areaCode);
    }

    public SelectAreaCodesViewController(Context context, SelectAreaCodesViewController.OnSelectListener onSelectListener) {
        mContext = context;
        mSelectListener = onSelectListener;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_select_area_codes, null);
    }

    @Override
    protected void onInitView(View view, final SmartDialog dialog) {
        mSmartDialog = dialog;
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mCancel = view.findViewById(R.id.cancel);
        mProgressBar = view.findViewById(R.id.progressBar);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (mAreaCodeList != null) {
            updateView();
        }
    }

    public void setAreaCodeList(List<AreaCode> areaCodeList) {
        mAreaCodeList = areaCodeList;
        if (isViewInitialized()) {
            updateView();
        }
    }

    private void updateView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        AreaCodeAdapter adapter = new AreaCodeAdapter(mAreaCodeList, new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                if (obj != null && mSelectListener != null) {
                    AreaCode areaCode = (AreaCode) obj;
                    mSelectListener.onSelect(areaCode);
                    mSmartDialog.dismiss();
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    static class AreaCodeAdapter extends RecyclerView.Adapter<AreaCodeAdapter.ViewHolder> {

        private List<AreaCode> mAreaCodeList;
        private OnRVItemClickListener mOnRVItemClickListener;

        public AreaCodeAdapter(List<AreaCode> areaCodeList, OnRVItemClickListener onRVItemClickListener) {
            mAreaCodeList = areaCodeList;
            mOnRVItemClickListener = onRVItemClickListener;
        }

        @NonNull
        @Override
        public AreaCodeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_area_code, parent, false);
            return new AreaCodeAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AreaCodeAdapter.ViewHolder holder, final int position) {
            final AreaCode areaCode = mAreaCodeList.get(position);
            holder.bind(areaCode);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRVItemClickListener.onItemClick(v, position, areaCode);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAreaCodeList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.countryName)
            TextView mCountryName;
            @BindView(R.id.areaCode)
            TextView mAreaCode;

            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(final AreaCode areaCode) {
                mCountryName.setText(areaCode.getName());
                mAreaCode.setText(StrFormatter.getFormatAreaCode(areaCode.getTeleCode()));
            }
        }
    }
}