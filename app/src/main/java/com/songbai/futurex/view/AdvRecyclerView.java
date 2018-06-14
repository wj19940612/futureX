package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * Modified by john on 2018/6/13
 * <p>
 * Description: 高级 recycleview, 可以添加 head 和 foot
 * <p>
 * APIs:
 */
public class AdvRecyclerView extends RecyclerView {

    private View mEmptyView;
    private boolean mHideAll;

    private SparseArrayCompat<View> mHeadList;
    private SparseArrayCompat<View> mFootList;

    protected InnerAdapterDataObserver mObserver;

    public AdvRecyclerView(Context context) {
        super(context);
        init();
    }

    public AdvRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mHeadList = new SparseArrayCompat<>();
        mFootList = new SparseArrayCompat<>();
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    public void addHeadView(View view) {
        int viewType = mHeadList.size() + 1;
        mHeadList.put(viewType, view);
    }

    public void addFootView(View view) {
        int viewType = -1 * (mFootList.size() + 1);
        mFootList.put(viewType, view);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null && mObserver != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }

        InnerAdapter innerAdapter = new InnerAdapter(adapter, mHeadList, mFootList);
        super.setAdapter(innerAdapter);

        if (innerAdapter != null) {
            mObserver = new InnerAdapterDataObserver();
            innerAdapter.registerAdapterDataObserver(mObserver);
        }
    }

    static class InnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private RecyclerView.Adapter mOuterAdapter;
        private SparseArrayCompat<View> mHeadList;
        private SparseArrayCompat<View> mFootList;

        public InnerAdapter(Adapter outerAdapter, SparseArrayCompat<View> headList, SparseArrayCompat<View> footList) {
            mOuterAdapter = outerAdapter;
            mHeadList = headList;
            mFootList = footList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (mHeadList.get(viewType) != null) {
                HeadFootViewHolder headViewHolder = new HeadFootViewHolder(mHeadList.get(viewType));
                return headViewHolder;
            } else if (mFootList.get(viewType) != null) {
                HeadFootViewHolder footViewHolder = new HeadFootViewHolder(mFootList.get(viewType));
                return footViewHolder;
            } else {
                return mOuterAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (isHeadPosition(position) || isFootPosition(position)) return;

            mOuterAdapter.onBindViewHolder(holder, position - mHeadList.size());
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeadPosition(position)) {
                return mHeadList.keyAt(position);
            } else if (isFootPosition(position)) {
                return mFootList.keyAt(position - mHeadList.size() - mOuterAdapter.getItemCount());
            } else {
                return mOuterAdapter.getItemViewType(position - mHeadList.size());
            }
        }

        private boolean isHeadPosition(int position) {
            return position < mHeadList.size();
        }

        private boolean isFootPosition(int position) {
            return position >= mHeadList.size() + mOuterAdapter.getItemCount();
        }

        @Override
        public int getItemCount() {
            return mHeadList.size() + mOuterAdapter.getItemCount() + mFootList.size();
        }

        static class HeadFootViewHolder extends ViewHolder {

            public HeadFootViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    private void checkIfEmpty() {
        if (mEmptyView != null && getAdapter() != null && !mHideAll) {
            boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    public void hideAll(boolean hideAll) {
        mHideAll = hideAll;
        if (hideAll) {
            mEmptyView.setVisibility(GONE);
            setVisibility(GONE);
        } else {
            checkIfEmpty();
        }
    }

    protected class InnerAdapterDataObserver extends AdapterDataObserver {

        protected AdapterDataObserver internalDataObserver;

        public InnerAdapterDataObserver() {
            // 通过反射获取父类观察者对象
            try {
                Field field = getDeclaredField(RecyclerView.class, "mObserver");
                if (field != null) {
                    internalDataObserver = (AdapterDataObserver) field.get(AdvRecyclerView.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Field getDeclaredField(Class clz, String fieldName) {
            Field field;
            for (; clz != Object.class; clz = clz.getSuperclass()) {
                try {
                    field = clz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field;
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        public void onChanged() {
            if (internalDataObserver != null) {
                internalDataObserver.onChanged();
            }

            checkIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeChanged(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeInserted(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeRemoved(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }
        }
    }

}
