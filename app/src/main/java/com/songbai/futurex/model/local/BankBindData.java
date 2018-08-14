package com.songbai.futurex.model.local;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class BankBindData {
    public static final String MAINLAND_BANK = "1";
    public static final String TW_BANK = "2";

    public static final String PAY_TYPE_ALIPAY = "1";
    public static final String PAY_TYPE_WECHATPAY = "2";
    public static final String PAY_TYPE_BANK_CARD = "3";

    private String cardNumber;//卡号 必填
    private String payType;//绑定类型 必填 1、支付宝；2、微信；3、银行卡
    private String bankArea;//地区
    private String bankName;//银行名称
    private String bankBranch;//支行名称
    private String bankCode;//银行代码
    private String realName;//账户名
    private String payPic;//账户名
    private String withDrawPass;//资金密码 必填

    public static final class Builder {
        private String cardNumber;
        private String payType;
        private String bankArea;
        private String bankName;
        private String bankBranch;
        private String bankCode;
        private String realName;
        private String payPic;
        private String withDrawPass;

        public static Builder aBankBindData() {
            return new Builder();
        }

        public Builder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder payType(String payType) {
            this.payType = payType;
            return this;
        }

        public Builder bankArea(String bankArea) {
            this.bankArea = bankArea;
            return this;
        }

        public Builder bankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        public Builder bankBranch(String bankBranch) {
            this.bankBranch = bankBranch;
            return this;
        }

        public Builder bankCode(String bankCode) {
            this.bankCode = bankCode;
            return this;
        }

        public Builder realName(String realName) {
            this.realName = realName;
            return this;
        }

        public Builder image(String payPic) {
            this.payPic = payPic;
            return this;
        }

        public Builder withDrawPass(String withDrawPass) {
            this.withDrawPass = withDrawPass;
            return this;
        }

        public BankBindData build() {
            BankBindData bankBindData = new BankBindData();
            bankBindData.cardNumber = this.cardNumber;
            bankBindData.payType = this.payType;
            bankBindData.bankArea = this.bankArea;
            bankBindData.bankName = this.bankName;
            bankBindData.bankBranch = this.bankBranch;
            bankBindData.bankCode = this.bankCode;
            bankBindData.realName = this.realName;
            bankBindData.payPic = this.payPic;
            bankBindData.withDrawPass = this.withDrawPass;
            return bankBindData;
        }
    }
}
