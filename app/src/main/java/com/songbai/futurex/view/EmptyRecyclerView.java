package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Modified by john on 2018/6/9
 * <p>
 * Description: 自定义 RecycleView 支持 emptyView
 */
public class EmptyRecyclerView extends RecyclerView {

    private View mEmptyView;
    private boolean mHideAll;

    protected InnerAdapterDataObserver mObserver;

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }
    protected class InnerAdapterDataObserver extends AdapterDataObserver {

        protected AdapterDataObserver internalDataObserver;

        public InnerAdapterDataObserver() {
            // 通过反射获取父类观察者对象
            try {
                Field field = getDeclaredField(RecyclerView.class, "mObserver");
                if (field != null) {
                    internalDataObserver = (AdapterDataObserver) field.get(EmptyRecyclerView.this);
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

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null && mObserver != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }

        super.setAdapter(adapter);

        if (adapter != null) {
            mObserver = new InnerAdapterDataObserver();
            adapter.registerAdapterDataObserver(mObserver);
        }

        checkIfEmpty();
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
}
