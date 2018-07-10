package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.OtcWarePoster;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.view.SmartDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class BuyLimitController extends SmartDialog.CustomViewController {
    private Context mContext;
    private String[] payType = new String[]{OtcWarePoster.CONDITION_AUTH, OtcWarePoster.CONDITION_TRADE};
    private int[] payTypeStrRes = new int[]{R.string.authentication, R.string.deal_success_count};
    private ArrayList<String> conditionType = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private String mConditionType;

    public BuyLimitController(Context context) {
        mContext = context;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_pay_type_selector, null);
    }

    @Override
    protected void onInitView(final View view, final SmartDialog dialog) {
        if (!TextUtils.isEmpty(mConditionType)) {
            String[] split = mConditionType.split(",");
            for (String s : split) {
                if (!conditionType.contains(s)) {
                    conditionType.add(s);
                }
            }
        }
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextView confirm = view.findViewById(R.id.confirm);
        final TextView cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        final BuyLimitAdapter adapter = new BuyLimitAdapter();
        adapter.setOnRVItemClickListener(new OnRVItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object obj) {
                String s = payType[position];
                if (conditionType.contains(s)) {
                    conditionType.remove(s);
                } else {
                    conditionType.add(s);
                }
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    if (conditionType.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (String s : conditionType) {
                            sb.append(s);
                            sb.append(",");
                        }
                        mOnItemClickListener.onConfirmClick(sb.toString().substring(0, sb.length() - 1));
                    } else {
                        mOnItemClickListener.onConfirmClick("");
                    }
                    dialog.dismiss();
                }
            }
        });
    }

    public void setConditionType(String conditionType) {
        mConditionType = conditionType;
    }

    public interface OnItemClickListener {
        void onConfirmClick(String conditionType);
    }

    public BuyLimitController setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }

    class BuyLimitAdapter extends RecyclerView.Adapter {
        private OnRVItemClickListener mOnRVItemClickListener;

        public void setOnRVItemClickListener(OnRVItemClickListener onRVItemClickListener) {
            mOnRVItemClickListener = onRVItemClickListener;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_buy_limit_type, parent, false);
            return new BuyLimitTypeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof BuyLimitTypeHolder) {
                ((BuyLimitTypeHolder) holder).bindData(position, payTypeStrRes[position]);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnRVItemClickListener != null) {
                        mOnRVItemClickListener.onItemClick(holder.itemView, position, payTypeStrRes[position]);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return payTypeStrRes.length;
        }

        class BuyLimitTypeHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.buyerLimit)
            TextView mBuyerLimit;
            @BindView(R.id.check)
            ImageView mCheck;

            public BuyLimitTypeHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindData(int position, int payTypeStrRe) {
                mBuyerLimit.setText(payTypeStrRe);
                mCheck.setVisibility(conditionType.contains(payType[position]) ? View.VISIBLE : View.GONE);
            }
        }
    }
}
