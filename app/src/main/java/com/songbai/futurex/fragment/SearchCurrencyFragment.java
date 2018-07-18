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
import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.Launcher;
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
 * Description: 搜索货币对页面
 * <p>
 * APIs:
 */
public class SearchCurrencyFragment extends UniqueActivity.UniFragment {

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

    private SearchRecordsHelper mSearchRecordsHelper;

    private boolean mSearchForTrade;

    @OnClick(R.id.cancel)
    public void onViewClicked() {
        finish();
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
        mSearchForTrade = extras.getBoolean(ExtraKeys.SERACH_FOR_TRADE);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mSearchRecordsHelper = new SearchRecordsHelper();

        mSearchBox.addTextChangedListener(mSearchWatcher);
        initCurrencyPairList();

        updateCurrencyList(mSearchRecordsHelper.getRecordList());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCurrencyListBaseOnSearch(mSearchBox.getText().toString());
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
            public void onAddOptionalClick(final View view, final CurrencyPair pair) {
                if (!LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), LoginActivity.class)
                            .execute();
                    return;
                }

                if (pair.isAddOptional()) {
                    Apic.cancelOptional(pair.getPairs()).tag(TAG)
                            .callback(new Callback<Resp>() {
                                @Override
                                protected void onRespSuccess(Resp resp) {
                                    pair.setAddOptional(false);
                                    view.setSelected(pair.isAddOptional());

                                    mSearchRecordsHelper.updateRecord(pair);

                                    ToastUtil.show(R.string.optional_cancel);
                                }
                            }).fire();
                } else {
                    Apic.addOptional(pair.getPairs()).tag(TAG)
                            .callback(new Callback<Resp>() {
                                @Override
                                protected void onRespSuccess(Resp resp) {
                                    pair.setAddOptional(true);
                                    view.setSelected(pair.isAddOptional());

                                    mSearchRecordsHelper.updateRecord(pair);

                                    ToastUtil.show(R.string.optional_added);
                                }
                            }).fire();
                }

                Preference.get().setOptionalListRefresh(true);
            }

            @Override
            public void onItemClick(View view, int position, Object obj) {
                if (obj instanceof CurrencyPair) {
                    CurrencyPair currencyPair = (CurrencyPair) obj;

                    mSearchRecordsHelper.addRecord(currencyPair);

                    if (mCurrencyPairAdapter.getType() == CurrencyPairAdapter.Type.SHOW_HISTORY_RECORDS) {
                        updateCurrencyList(mSearchRecordsHelper.getRecordList());
                    }

                    if (mSearchForTrade) {
                        Intent intent = new Intent().putExtra(ExtraKeys.CURRENCY_PAIR, currencyPair);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        openMarketDetailPage(currencyPair);
                    }
                }
            }
        });
        mCurrencyPairList.setAdapter(mCurrencyPairAdapter);
        mCurrencyPairList.setEmptyView(mEmptyView);
    }

    private void openMarketDetailPage(CurrencyPair currencyPair) {
        UniqueActivity.launcher(SearchCurrencyFragment.this, MarketDetailFragment.class)
                .putExtra(ExtraKeys.CURRENCY_PAIR, currencyPair)
                .execute();
    }

    private ValidationWatcher mSearchWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            updateCurrencyListBaseOnSearch(s.toString());
        }
    };

    private void updateCurrencyListBaseOnSearch(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            mCurrencyPairAdapter.setType(CurrencyPairAdapter.Type.SHOW_HISTORY_RECORDS);
            updateCurrencyList(mSearchRecordsHelper.getRecordList());
        } else {
            mCurrencyPairAdapter.setType(CurrencyPairAdapter.Type.SHOW_SEARCH_RESULTS);
            requestSearchResults(keyword);
        }
    }

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
        mCurrencyPairAdapter.setCurrencyPairList(displayList);
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
        private List<CurrencyPair> mCurrencyPairList;
        private Type mType;

        public enum Type {
            SHOW_HISTORY_RECORDS,
            SHOW_SEARCH_RESULTS
        }

        public interface Callback extends OnRVItemClickListener {
            void onClearRecordsClick();

            void onAddOptionalClick(View view, CurrencyPair pair);
        }

        public CurrencyPairAdapter(Callback callback) {
            mCallback = callback;
            mCurrencyPairList = new ArrayList<>();
            mType = Type.SHOW_HISTORY_RECORDS;
        }

        public void setCurrencyPairList(List<CurrencyPair> currencyPairList) {
            mCurrencyPairList = currencyPairList;
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

            public void bind(final CurrencyPair pair, final Callback callback, final int position) {
                mPairName.setText(pair.getUpperCasePairName());
                mOptionalStatus.setSelected(pair.isAddOptional());
                mOptionalStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onAddOptionalClick(mOptionalStatus, pair);
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onItemClick(v, position, pair);
                    }
                });
            }
        }

        private CurrencyPair getItem(int position) {
            if (mType == Type.SHOW_HISTORY_RECORDS) {
                return mCurrencyPairList.get(position - 1);
            } else {
                return mCurrencyPairList.get(position);
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
            return mType == Type.SHOW_HISTORY_RECORDS ? mCurrencyPairList.size() + 1 : mCurrencyPairList.size();
        }
    }
}
