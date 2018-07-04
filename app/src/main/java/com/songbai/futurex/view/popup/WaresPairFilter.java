package com.songbai.futurex.view.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.CountryCurrency;
import com.songbai.futurex.model.LegalCoin;

import java.util.ArrayList;

/**
 * @author yangguangda
 * @date 2018/6/26
 */
public class WaresPairFilter {
    private OnSelectCallBack mOnSelectCallBack;
    private ArrayList<LegalCoin> mLegalCoins;
    private ArrayList<CountryCurrency> mCountryCurrencies;
    private String mTempLegalSymbol;
    private String mTempCurrencySymbol;
    private final View mView;
    private PopupWindow mPopupWindow;
    private final PricingCurrencyAdapter mPricingCurrencyAdapter;
    private final CurrencyAdapter mCurrencyAdapter;

    public WaresPairFilter(Context context, ArrayList<LegalCoin> legalCoins, ArrayList<CountryCurrency> countryCurrencies) {
        mLegalCoins = legalCoins;
        mCountryCurrencies = countryCurrencies;
        mView = LayoutInflater.from(context)
                .inflate(R.layout.view_coin_currency_pair_filter, null, false);
        RecyclerView pricingCurrency = mView.findViewById(R.id.pricingCurrency);
        pricingCurrency.setLayoutManager(new GridLayoutManager(context, 3));
        mPricingCurrencyAdapter = new PricingCurrencyAdapter();
        mPricingCurrencyAdapter.setList(mLegalCoins);
        mPricingCurrencyAdapter.setOnItemClickListener(mOnItemClickListener);
        pricingCurrency.setAdapter(mPricingCurrencyAdapter);
        RecyclerView legalCurrency = mView.findViewById(R.id.legalCurrency);
        legalCurrency.setLayoutManager(new GridLayoutManager(context, 3));
        mCurrencyAdapter = new CurrencyAdapter();
        mCurrencyAdapter.setList(mCountryCurrencies);
        mCurrencyAdapter.setOnItemClickListener(mOnItemClickListener);
        legalCurrency.setAdapter(mCurrencyAdapter);
        TextView cancel = mView.findViewById(R.id.cancel);
        TextView ok = mView.findViewById(R.id.ok);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSelectCallBack != null) {
                    if (!TextUtils.isEmpty(mTempLegalSymbol) && !TextUtils.isEmpty(mTempCurrencySymbol)) {
                        mOnSelectCallBack.onSelected(mTempLegalSymbol, mTempCurrencySymbol);
                    }
                }
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });

    }

    public void showOrDismiss(View view) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            mPopupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.showAsDropDown(view);
        }
    }

    public boolean isShowing() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    public void setSelectedSymbol(String selectedLegalSymbol, String selectedCurrencySymbol) {
        mTempLegalSymbol = selectedLegalSymbol;
        mTempCurrencySymbol = selectedCurrencySymbol;
    }

    public interface OnSelectCallBack {
        void onSelected(String tempLegalSymbol, String tempCurrencySymbol);
    }

    public void setOnSelectCallBack(OnSelectCallBack onSelectCallBack) {
        mOnSelectCallBack = onSelectCallBack;
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onPricingItemClick(LegalCoin legalCoin) {
            mTempLegalSymbol = legalCoin.getSymbol().toLowerCase();
            mPricingCurrencyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCurrencyItemClick(CountryCurrency countryCurrency) {
            mTempCurrencySymbol = countryCurrency.getEnglishName().toLowerCase();
            mCurrencyAdapter.notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener {
        void onPricingItemClick(LegalCoin legalCoin);

        void onCurrencyItemClick(CountryCurrency countryCurrency);
    }

    private class PricingCurrencyAdapter extends RecyclerView.Adapter {
        private ArrayList<LegalCoin> mList = new ArrayList<>();
        private OnItemClickListener mOnItemClickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pricing_currency, parent, false);
            return new PricingCurrencyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PricingCurrencyHolder) {
                ((PricingCurrencyHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(ArrayList<LegalCoin> list) {
            mList.clear();
            mList.addAll(list);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        private class PricingCurrencyHolder extends RecyclerView.ViewHolder {

            private TextView mTextView;

            public PricingCurrencyHolder(View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(R.id.textView);
            }

            public void bindData(final LegalCoin legalCoin) {
                String text = legalCoin.getSymbol().toUpperCase();
                mTextView.setSelected(text.equalsIgnoreCase(mTempLegalSymbol));
                mTextView.setText(text);
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onPricingItemClick(legalCoin);
                        }
                    }
                });
            }
        }
    }

    private class CurrencyAdapter extends RecyclerView.Adapter {
        private ArrayList<CountryCurrency> mList = new ArrayList<>();
        private OnItemClickListener mOnItemClickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pricing_currency, parent, false);
            return new CurrencyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CurrencyHolder) {
                ((CurrencyHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(ArrayList<CountryCurrency> list) {
            mList.clear();
            mList.addAll(list);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        private class CurrencyHolder extends RecyclerView.ViewHolder {

            private TextView mTextView;

            public CurrencyHolder(View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(R.id.textView);
            }

            public void bindData(final CountryCurrency countryCurrency) {
                String text = countryCurrency.getEnglishName();
                mTextView.setSelected(text.equalsIgnoreCase(mTempCurrencySymbol));
                mTextView.setText(text.toUpperCase());
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onCurrencyItemClick(countryCurrency);
                        }
                    }
                });
            }
        }
    }
}
