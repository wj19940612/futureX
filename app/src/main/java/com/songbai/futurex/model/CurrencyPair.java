package com.songbai.futurex.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.adapter.GroupAdapter;

/**
 * Modified by john on 2018/6/8
 * <p>
 * Description: 货币对数据结构
 */
public class CurrencyPair implements GroupAdapter.Groupable, Comparable<CurrencyPair>,Parcelable {

    public static final int CATE_MAIN = 1; // 主区
    public static final int CATE_CREATIVE = 2; // 创新
    public static final int CATE_NEW = 3; // 新币种

    /**
     * category : 1
     * id : 24
     * option : 0
     * pairs : eth_btc
     * prefixSymbol : eth
     * sort : 2
     * suffixSymbol : btc
     */

    private int category;
    private int id;
    private int option;
    private String pairs;
    private String prefixSymbol;
    private int sort;
    private String suffixSymbol;

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public String getPairs() {
        return pairs;
    }

    public void setPairs(String pairs) {
        this.pairs = pairs;
    }

    public String getPrefixSymbol() {
        return prefixSymbol;
    }

    public void setPrefixSymbol(String prefixSymbol) {
        this.prefixSymbol = prefixSymbol;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getSuffixSymbol() {
        return suffixSymbol;
    }

    public void setSuffixSymbol(String suffixSymbol) {
        this.suffixSymbol = suffixSymbol;
    }

    public String getUpperCasePairName() {
        return prefixSymbol.toUpperCase() + "/" + suffixSymbol.toUpperCase();
    }

    @Override
    public int getGroupNameRes() {
        if (category == CATE_MAIN) {
            return R.string.main_area;
        } else if (category == CATE_CREATIVE) {
            return R.string.creative_area;
        } else {
            return R.string.new_currency_area;
        }
    }

    @Override
    public int getGroupId() {
        return category;
    }

    @Override
    public int compareTo(@NonNull CurrencyPair o) {
        if (this.category > o.category) {
            return 1;
        } else if (this.category < o.category) {
            return -1;
        } else {
            return this.sort - o.sort;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.category);
        dest.writeInt(this.id);
        dest.writeInt(this.option);
        dest.writeString(this.pairs);
        dest.writeString(this.prefixSymbol);
        dest.writeInt(this.sort);
        dest.writeString(this.suffixSymbol);
    }

    public CurrencyPair() {
    }

    protected CurrencyPair(Parcel in) {
        this.category = in.readInt();
        this.id = in.readInt();
        this.option = in.readInt();
        this.pairs = in.readString();
        this.prefixSymbol = in.readString();
        this.sort = in.readInt();
        this.suffixSymbol = in.readString();
    }

    public static final Parcelable.Creator<CurrencyPair> CREATOR = new Parcelable.Creator<CurrencyPair>() {
        @Override
        public CurrencyPair createFromParcel(Parcel source) {
            return new CurrencyPair(source);
        }

        @Override
        public CurrencyPair[] newArray(int size) {
            return new CurrencyPair[size];
        }
    };

    @Override
    public String toString() {
        return "CurrencyPair{" +
                "category=" + category +
                ", id=" + id +
                ", option=" + option +
                ", pairs='" + pairs + '\'' +
                ", prefixSymbol='" + prefixSymbol + '\'' +
                ", sort=" + sort +
                ", suffixSymbol='" + suffixSymbol + '\'' +
                '}';
    }
}
