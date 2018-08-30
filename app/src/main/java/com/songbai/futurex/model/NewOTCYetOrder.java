package com.songbai.futurex.model;

/**
 * @author yangguangda
 * @date 2018/8/29
 */
public class NewOTCYetOrder {

    /**
     * id : 308
     * count : 2
     */

    private OrderBean id;
    private int count;
    private int direct;

    public OrderBean getId() {
        return id;
    }

    public void setId(OrderBean id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }
}
