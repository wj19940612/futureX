package com.songbai.futurex.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.utils.OnRVItemClickListener;
import com.songbai.futurex.utils.SearchRecordsHelper;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Modified by john on 2018/6/13
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class SearchCurrencyFragment extends UniqueActivity.UniFragment {
    private static final int REQ_CODE_MARKET_DETAIL = 95;

    @BindView(R.id.searchBox)
    EditText mSearchBox;
    @BindView(R.id.cancel)
    TextView mCancel;
    @BindView(R.id.currencyPairList)
    EmptyRecyclerView mCurrencyPairList;
    @BindView(R.id.emptyView)
    LinearLayout mEmptyView;

    Unbinder unbinder;

    private CurrencyPairAdapter mCurrencyPairAdapter;
    private List<CurrencyPair> mOptionalList;

    private SearchRecordsHelper mSearchRecordsHelper;

    @OnClick(R.id.cancel)
    public void onViewClicked() {
        finish();
    }

    static class OptionalWrap {

        public CurrencyPair pair;
        public boolean added;

        public OptionalWrap(CurrencyPair pair, boolean added) {
            this.pair = pair;
            this.added = added;
        }

        public static List<OptionalWrap> getWrapList(List<CurrencyPair> displayList, List<CurrencyPair> optionalList) {
            List<OptionalWrap> wrapList = new ArrayList<>();
            for (int i = 0; i < displayList.size(); i++) {
                CurrencyPair pair = displayList.get(i);
                boolean added = indexOf(pair, optionalList);
                wrapList.add(new OptionalWrap(pair, added));
            }
            return wrapList;
        }

        private static boolean indexOf(CurrencyPair pair, List<CurrencyPair> optionalList) {
            for (CurrencyPair currencyPair : optionalList) {
                if (pair.getPairs().equalsIgnoreCase(currencyPair.getPairs())) {
                    return true;
                }
            }
            return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_currency, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mOptionalList = extras.getParcelableArrayList(ExtraKeys.OPTIONAL_LIST);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mSearchRecordsHelper = new SearchRecordsHelper();

        mSearchBox.addTextChangedListener(mSearchWatcher);
        initCurrencyPairList();

        updateCurrencyList(mSearchRecordsHelper.getRecordList());
    }

    private void initCurrencyPairList() {
        mCurrencyPairList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCurrencyPairAdapter = new CurrencyPairAdapter(new CurrencyPairAdapter.Callback() {
            @Override
            public void onClearRecordsClick() {
                mSearchRecordsHelper.clearRecord();
                updateCurrencyList(mSearchRecordsHelper.getRecordList());
            }

            @Override
            public void onAddOptionalClick(final View view, final OptionalWrap wrap) {
                if (wrap.added) { // cancel
                    Apic.cancelOptional(wrap.pair.getPairs()).tag(TAG)
                            .callback(new Callback<Resp>() {
                                @Override
                                protected void onRespSuccess(Resp resp) {
                                    wrap.added = false;
                                    view.setSelected(wrap.added);
                                    updateOptionalList(wrap);
                                    ToastUtil.show(R.string.optional_cancel);
                                }
                            }).fire();
                } else { // add
                    Apic.addOptional(wrap.pair.getPairs()).tag(TAG)
                            .callback(new Callback<Resp>() {
                                @Override
                                protected void onRespSuccess(Resp resp) {
                                    wrap.added = true;
                                    view.setSelected(wrap.added);
                                    updateOptionalList(wrap);
                                    ToastUtil.show(R.string.optional_added);
                                }
                            }).fire();
                }
            }

            @Override
            public void onItemClick(View view, int position, Object obj) {
                if (obj instanceof OptionalWrap) {
                    CurrencyPair currencyPair = ((OptionalWrap) obj).pair;

                    mSearchRecordsHelper.addRecord(currencyPair);
                    if (mCurrencyPairAdapter.getType() == CurrencyPairAdapter.Type.SHOW_HISTORY_RECORDS) {
                        updateCurrencyList(mSearchRecordsHelper.getRecordList());
                    }

                    openMarketDetailPage(currencyPair);
                }
            }
        });
        mCurrencyPairList.setAdapter(mCurrencyPairAdapter);
        mCurrencyPairList.setEmptyView(mEmptyView);
    }

    private void updateOptionalList(OptionalWrap wrap) {
        CurrencyPair operatedCurrencyPair = null;
        for (CurrencyPair currencyPair : mOptionalList) {
            if (currencyPair.getPairs().equals(wrap.pair.getPairs())) {
                operatedCurrencyPair = currencyPair;
                break;
            }
        }
        if (operatedCurrencyPair != null && !wrap.added) {
            mOptionalList.remove(operatedCurrencyPair);
            setResult(Activity.RESULT_OK); // update optional page
        } else if (wrap.added) {
            mOptionalList.add(wrap.pair);
            setResult(Activity.RESULT_OK);
        }
    }

    private void openMarketDetailPage(CurrencyPair currencyPair) {
        UniqueActivity.launcher(SearchCurrencyFragment.this, MarketDetailFragment.class)
                .putExtra(ExtraKeys.CURRENCY_PAIR, currencyPair)
                .execute(SearchCurrencyFragment.this, REQ_CODE_MARKET_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_MARKET_DETAIL && resultCode == Activity.RESULT_OK) {

        }
    }

    private ValidationWatcher mSearchWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String keyword = s.toString();
            if (TextUtils.isEmpty(keyword)) {
                mCurrencyPairAdapter.setType(CurrencyPairAdapter.Type.SHOW_HISTORY_RECORDS);
                updateCurrencyList(mSearchRecordsHelper.getRecordList());
            } else {
                mCurrencyPairAdapter.setType(CurrencyPairAdapter.Type.SHOW_SEARCH_RESULTS);
                requestSearchResults(keyword);
            }
        }
    };

    private void requestSearchResults(String keyword) {
        Apic.searchCurrencyPairs(keyword).tag(TAG)
                .id(keyword)
                .callback(new Callback4Resp<Resp<List<CurrencyPair>>, List<CurrencyPair>>() {
                    @Override
                    protected void onRespData(List<CurrencyPair> data) {
                        String keyword = mSearchBox.getText().toString();
                        if (getId().equals(keyword)) {
                            updateCurrencyList(data);
                        }
                    }
                }).fireFreely();
    }

    private void updateCurrencyList(List<CurrencyPair> displayList) {
        List<OptionalWrap> wrapList = OptionalWrap.getWrapList(displayList, mOptionalList);
        mCurrencyPairAdapter.setOptionalWrapList(wrapList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSearchBox.removeTextChangedListener(mSearchWatcher);

        unbinder.unbind();
    }

    static class CurrencyPairAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEAD = 0;
        private static final int ITEM = 1;

        private Callback mCallback;
        private List<OptionalWrap> mOptionalWrapList;
        private Type mType;

        public enum Type {
            SHOW_HISTORY_RECORDS,
            SHOW_SEARCH_RESULTS
        }

        public interface Callback extends OnRVItemClickListener {
            void onClearRecordsClick();

            void onAddOptionalClick(View view, OptionalWrap wrap);
        }

        public CurrencyPairAdapter(Callback callback) {
            mCallback = callback;
            mOptionalWrapList = new ArrayList<>();
            mType = Type.SHOW_HISTORY_RECORDS;
        }

        public void setOptionalWrapList(List<OptionalWrap> optionalWrapList) {
            mOptionalWrapList = optionalWrapList;
            notifyDataSetChanged();
        }

        public void setType(@NonNull Type type) {
            mType = type;
            notifyDataSetChanged();
        }

        public Type getType() {
            return mType;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == HEAD) {
                View head = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_history_record, parent, false);
                return new HeadViewHolder(head);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_record, parent, false);
                return new ViewHolder(view);
            }
        }

        static class HeadViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.clearRecord)
            TextView mClearRecord;

            public HeadViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.pairName)
            TextView mPairName;
            @BindView(R.id.optionalStatus)
            ImageView mOptionalStatus;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(final OptionalWrap wrap, final Callback callback, final int position) {
                CurrencyPair pair = wrap.pair;
                mPairName.setText(pair.getUpperCasePairName());
                mOptionalStatus.setSelected(wrap.added);
                mOptionalStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onAddOptionalClick(mOptionalStatus, wrap);
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onItemClick(v, position, wrap);
                    }
                });
            }
        }

        private OptionalWrap getItem(int position) {
            if (mType == Type.SHOW_HISTORY_RECORDS) {
                return mOptionalWrapList.get(position - 1);
            } else {
                return mOptionalWrapList.get(position);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof HeadViewHolder) {
                ((HeadViewHolder) holder).mClearRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.onClearRecordsClick();
                    }
                });
            } else {
                ((ViewHolder) holder).bind(getItem(position), mCallback, position);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mType == Type.SHOW_HISTORY_RECORDS) {
                return position == 0 ? HEAD : ITEM;
            } else {
                return ITEM;
            }
        }

        @Override
        public int getItemCount() {
            return mType == Type.SHOW_HISTORY_RECORDS ? mOptionalWrapList.size() + 1 : mOptionalWrapList.size();
        }
    }
}
