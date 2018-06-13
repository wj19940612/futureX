package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/6/13
 */
public class DrawLimit {

    /**
     * confirm : 12
     * dayWithdrawAmount : 10000.0
     * maxWithdrawAmount : 10000.0
     * minWithdrawAmount : 0.001
     * withdraw : 1
     * withdrawRate : 1.0E-4
     */

    private int confirm;
    private double dayWithdrawAmount;//每天提现限制多少次
    private double maxWithdrawAmount;//最大提现数
    private double minWithdrawAmount;//最小提现数
    private int withdraw;//
    private double withdrawRate;//每天提现次数

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public double getDayWithdrawAmount() {
        return dayWithdrawAmount;
    }

    public void setDayWithdrawAmount(double dayWithdrawAmount) {
        this.dayWithdrawAmount = dayWithdrawAmount;
    }

    public double getMaxWithdrawAmount() {
        return maxWithdrawAmount;
    }

    public void setMaxWithdrawAmount(double maxWithdrawAmount) {
        this.maxWithdrawAmount = maxWithdrawAmount;
    }

    public double getMinWithdrawAmount() {
        return minWithdrawAmount;
    }

    public void setMinWithdrawAmount(double minWithdrawAmount) {
        this.minWithdrawAmount = minWithdrawAmount;
    }

    public int getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(int withdraw) {
        this.withdraw = withdraw;
    }

    public double getWithdrawRate() {
        return withdrawRate;
    }

    public void setWithdrawRate(double withdrawRate) {
        this.withdrawRate = withdrawRate;
    }
}
