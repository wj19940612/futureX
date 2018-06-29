package com.songbai.futurex.model;

public class Introduce {

    /**
     * code : MPlatformIntroduction
     * content : <p>未来所（BitFuture）为全球领先的数字资产综合交易平台，主要为用户提供比特币（BTC）、莱特币（LTC）、以太币（ETH）等数字资产的币币和衍生品交易服务， 在创办之初即获得美国资本千万美金投资，并凭借其雄厚的资本、高超的区块链技术，上线1月有余，用户即遍布全球50多个主要国家，全球累计800万的数字资产交易用户,日交易金额在1500万左右。</p><p><br/></p>
     * format : 1
     * id : 5b3365e519719b71e48d349f
     * lang : zh_CN
     * operator : admin
     * title :  平台介绍
     */

    private String code;
    private String content;
    private int format;
    private String id;
    private String lang;
    private String operator;
    private String title;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
