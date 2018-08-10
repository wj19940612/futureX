package com.songbai.wrapres.http;

import java.util.List;

/**
 * Created by ${wangJie} on 2018/2/12.
 * {"code":200,"msg":"succ","data":{"content":[],"first":false,"last":true,"number":1,"numberOfElements":0,"size":20,"totalElements":0,"totalPages":0}}
 * 返回的list 数据结构为上面结构  暂时先封装一层
 */

public class ListResp<T> {

    /**
     * code : 200
     * msg : succ
     * data : {"content":[],"first":false,"last":true,"number":1,"numberOfElements":0,"size":20,"totalElements":0,"totalPages":0}
     */

    private int code;
    private String msg;
    private DataBean<T> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean<T> getData() {
        if (data != null) {
            return (DataBean<T>) data.getContent();
        }
        return null;
    }

    public void setData(DataBean<T> data) {
        this.data = data;
    }

    public List<T> getListData() {
        if (data != null) {
            return (List<T>) data.getContent();
        }
        return null;
    }

    public static class DataBean<T> {
        /**
         * content : []
         * first : false
         * last : true
         * number : 1
         * numberOfElements : 0
         * size : 20
         * totalElements : 0
         * totalPages : 0
         */

        private boolean first;
        private boolean last;
        private int number;
        private int numberOfElements;
        private int size;
        private int totalElements;
        private int totalPages;
        private List<T> content;

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getNumberOfElements() {
            return numberOfElements;
        }

        public void setNumberOfElements(int numberOfElements) {
            this.numberOfElements = numberOfElements;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public List<?> getContent() {
            return content;
        }

        public void setContent(List<T> content) {
            this.content = content;
        }
    }
}
