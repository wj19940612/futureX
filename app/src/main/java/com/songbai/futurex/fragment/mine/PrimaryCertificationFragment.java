package com.songbai.futurex.fragment.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.dialog.ItemSelectController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class PrimaryCertificationFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.type)
    TextView mType;
    @BindView(R.id.realName)
    EditText mRealName;
    @BindView(R.id.certificationNumber)
    EditText mCertificationNumber;
    private Integer[] type = new Integer[]{R.string.mainland_id_card, R.string.tw_id_card, R.string.passport};
    private Unbinder mBind;
    private SmartDialog mSmartDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_primary_certification, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick(R.id.type)
    public void onViewClicked() {
        ItemSelectController itemSelectController = new ItemSelectController(getContext());
        CertificationTypeAdapter adapter = new CertificationTypeAdapter();
        adapter.setData(type);
        adapter.setOnItemClickListener(new CertificationTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Integer id) {
                mType.setText(id);
                switch (id) {
                    case R.string.mainland_id_card:
                        break;
                    case R.string.tw_id_card:
                        break;
                    case R.string.passport:
                        break;
                    default:
                }
                mSmartDialog.dismiss();
            }
        });
        itemSelectController.setAdapter(adapter);
        itemSelectController.setHintText(getString(R.string.pleas_select_credentials_type));
        mSmartDialog = SmartDialog.solo(getActivity());
        mSmartDialog
                .setWidthScale(1)
                .setWindowGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .setCustomViewController(itemSelectController)
                .show();
    }

    static class CertificationTypeAdapter extends RecyclerView.Adapter {
        private Integer[] mData;
        private static OnItemClickListener mOnItemClickListener;
        private Context mContext;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_buttom_select_text, parent, false);
            return new CertificationTypeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CertificationTypeHolder) {
                ((CertificationTypeHolder) holder).bindData(mContext, mData[position]);
            }
        }

        @Override
        public int getItemCount() {
            return mData.length;
        }

        public void setData(Integer[] data) {
            mData = data;
        }

        interface OnItemClickListener {
            void onItemClick(Integer id);
        }

        void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        static class CertificationTypeHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.textView)
            TextView mTextView;

            CertificationTypeHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            void bindData(Context context, final Integer id) {
                mTextView.setText(id);
                mTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(id);
                        }
                    }
                });
            }
        }
    }
}
