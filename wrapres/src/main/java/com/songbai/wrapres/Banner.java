package com.songbai.wrapres;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.songbai.wrapres.model.BannerData;

import java.util.Iterator;
import java.util.List;

import sbai.com.glide.GlideApp;


public class Banner extends FrameLayout {

    InfiniteViewPager mViewPager;
    PageIndicator mPageIndicator;

    private AdvertisementAdapter mAdapter;

    public interface OnBannerClickListener {
        void onBannerClick(BannerData information);
    }

    private OnBannerClickListener mOnBannerClickListener;

    public void setOnBannerClickListener(OnBannerClickListener onBannerClickListener) {
        mOnBannerClickListener = onBannerClickListener;
    }

    public Banner(Context context) {
        super(context);
        init();
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_banner, this, true);
        mViewPager = findViewById(R.id.viewPager);
        mPageIndicator = findViewById(R.id.pageIndicator);
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mPageIndicator != null) {
                mPageIndicator.move(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
            }
        }
    };

    public void nextAdvertisement() {
        if (mAdapter != null && mAdapter.getCount() > 1) {
            //ViewPager还在窗口执行这个动作
            if (!mViewPager.isDetachFromWindow()) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        }
    }

    public void setHomeAdvertisement(List<BannerData> informationList) {
        if (informationList.size() == 0) {
            setVisibility(View.GONE);
            return;
        } else {
            setVisibility(View.VISIBLE);
        }
        filterEmptyInformation(informationList);

        if (!informationList.isEmpty()) {
            int size = informationList.size();
            if (size < 2) {
                mPageIndicator.setVisibility(INVISIBLE);
            } else {
                mPageIndicator.setVisibility(VISIBLE);
            }
            mPageIndicator.setCount(size);

            if (mAdapter == null) {
                mAdapter = new AdvertisementAdapter(getContext(), informationList, mOnBannerClickListener);
                mViewPager.addOnPageChangeListener(mOnPageChangeListener);
                mViewPager.setAdapter(mAdapter);
            } else {
                mAdapter.setNewAdvertisements(informationList);
            }
        }
    }

    private void filterEmptyInformation(List<BannerData> informationList) {
        Iterator<BannerData> iterator = informationList.iterator();
        while (iterator.hasNext()) {
            BannerData banner = iterator.next();
            if (TextUtils.isEmpty(banner.getCover())) {
                iterator.remove();
            }
        }
    }

    private static class AdvertisementAdapter extends PagerAdapter {

        private List<BannerData> mList;
        private Context mContext;
        private OnBannerClickListener mListener;

        public AdvertisementAdapter(Context context, List<BannerData> informationList, OnBannerClickListener listener) {
            mContext = context;
            mList = informationList;
            mListener = listener;
        }

        public void setNewAdvertisements(List<BannerData> informationList) {
            mList = informationList;
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int pos = position;
            ImageView imageView = new ImageView(mContext);
            final BannerData information = mList.get(pos);

            container.addView(imageView);
            if (!TextUtils.isEmpty(information.getCover())) {
                GlideApp.with(mContext).load(R.drawable.img_coin2)
                        .centerCrop().into(imageView);
            }
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onBannerClick(information);
                    }
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
