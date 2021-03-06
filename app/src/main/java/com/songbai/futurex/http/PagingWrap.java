package com.songbai.futurex.http;

import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/20
 */
public class PagingWrap<T> {

    private List<T> data;

    private int pageSize;
    private int resultCount;//总共多少行
    private int start;
    private int total;//总共多少页
    private boolean last;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    @Override
    public String toString() {
        return "PagingWrap{" +
                "data=" + data +
                ", pageSize=" + pageSize +
                ", resultCount=" + resultCount +
                ", start=" + start +
                ", total=" + total +
                '}';
    }
}
