package com.songbai.futurex.utils.adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.OnRVItemClickListener;

/**
 * Modified by john on 2018/6/27
 * <p>
 * Description: 最简单的  RecyclerView Adapter
 * <p>
 * APIs:
 */
public class SimpleRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnRVItemClickListener mOnRVItemClickListener;
    private int[] mTextResources;
    private int mRowRes;

    public SimpleRVAdapter(int[] textResources, int rowRes, OnRVItemClickListener onRVItemClickListener) {
        mOnRVItemClickListener = onRVItemClickListener;
        mTextResources = textResources;
        mRowRes = rowRes;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mRowRes, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SimpleViewHolder) {
            ((SimpleViewHolder) holder).bindData(mTextResources[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRVItemClickListener.onItemClick(holder.itemView, position, mTextResources[position]);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mTextResources.length;
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.textView);
            if (mTextView == null) {
                throw new Resources.NotFoundException("RowResource should contain a view whose id is textView");
            }
        }

        public void bindData(int textResource) {
            if (mTextView != null) {
                mTextView.setText(textResource);
            }
        }
    }
}
