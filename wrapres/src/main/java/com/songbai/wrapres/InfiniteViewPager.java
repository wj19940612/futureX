package com.songbai.wrapres;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

public class InfiniteViewPager extends ViewPager {

    private InnerAdapter mInnerAdapter;
    private boolean mHasDetach;

    public InfiniteViewPager(Context context) {
        super(context);
    }

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        mInnerAdapter = new InnerAdapter(adapter);
        super.setAdapter(mInnerAdapter);
        setCurrentItem(1);
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        super.addOnPageChangeListener(new InnerPageChangeListener(listener));
    }

    //RecyclerView滚动上去，直至ViewPager看不见，再滚动下来，ViewPager下一次切换没有动画
    //ViewPager里有一个私有变量mFirstLayout，它是表示是不是第一次显示布局，如果是true，则使用无动画的方式显示当前item，如果是false，则使用动画方式显示当前item。
    //当ViewPager滚动上去后，因为RecyclerView的回收机制，ViewPager会走onDetachFromWindow，当再次滚动下来时，ViewPager会走onAttachedToWindow，而问题就出在onAttachToWindow。
    //在onAttachedToWindow中，mFirstLayout被重置为true，所以下一次滚动就没有动画。
    //这会导致OnPageChangeListener不调用滑动事件
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHasDetach = false;

        try {
            Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
            mFirstLayout.setAccessible(true);
            mFirstLayout.set(this, false);
//            getAdapter().notifyDataSetChanged();
            //之前的滑动滞留动作，会触发多余的onPageScrollStateChanged
            if (getCurrentItem() == mInnerAdapter.getCount() - 1) {
                setCurrentItem(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHasDetach = true;

    }

    public void setHasDestroy(boolean hasDestroy) {
        mHasDetach = hasDestroy;
    }

    public boolean isDetachFromWindow() {
        return mHasDetach;
    }

    private class InnerPageChangeListener implements OnPageChangeListener {

        private OnPageChangeListener mExternalListener;
        private int mInnerPosition;

        public InnerPageChangeListener(OnPageChangeListener externalListener) {
            mExternalListener = externalListener;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int pos = calExternalPosition(position, mInnerAdapter);
            if (mExternalListener != null) {
                mExternalListener.onPageScrolled(pos, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            mInnerPosition = position;
            int pos = calExternalPosition(position, mInnerAdapter);
            if (mExternalListener != null) {
                mExternalListener.onPageSelected(pos); // FIXME: 15/11/2017 this will be called twice, since setCurrentItem to real item.
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mExternalListener != null) {
                mExternalListener.onPageScrollStateChanged(state);
            }

            if (state == ViewPager.SCROLL_STATE_IDLE) { // When viewpager is settled
                if (mInnerPosition == 0) { // fake obj: head, move to last second
                    setCurrentItem(mInnerAdapter.getCount() - 2, false);
                } else if (mInnerPosition == mInnerAdapter.getCount() - 1) { // fake obj: tail, move to the second
                    setCurrentItem(1, false);
                }
            }
        }
    }

    private class InnerAdapter extends PagerAdapter {

        private PagerAdapter mExternalAdapter;

        public InnerAdapter(PagerAdapter adapter) {
            mExternalAdapter = adapter;
            mExternalAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    notifyDataSetChanged();
                }
            });
        }

        public PagerAdapter getExternalAdapter() {
            return mExternalAdapter;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            if (mExternalAdapter.getCount() > 1) {
                return mExternalAdapter.getCount() + 2; // fake object at head and tail
            }
            return mExternalAdapter.getCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return mExternalAdapter.isViewFromObject(view, object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mExternalAdapter.destroyItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = calExternalPosition(position, this);
            return mExternalAdapter.instantiateItem(container, position);
        }
    }

    private int calExternalPosition(int innerPos, InnerAdapter innerAdapter) {
        if (innerPos == 0) { // first -> external last
            return innerAdapter.getExternalAdapter().getCount() - 1;
        } else if (innerPos == innerAdapter.getCount() - 1) { // last -> external 0
            return 0;
        } else {
            return innerPos - 1;
        }
    }
}
