package com.songbai.futurex.model;

public class CoinIntroduce {

    /**
     * blockStation : https://blockchain.info/
     * circulateCount : 1.673000082E7
     * deleted : 0
     * detail : 比特币（Bitcoin，简称BTC）是目前使用最为广泛的一种数字货币，它诞生于2009年1月3日，是一种点对点（P2P）传输的数字加密货币，总量2100万枚。比特币网络每10分钟释放出一定数量币，预计在2140年达到极限。比特币被投资者称为“数字黄金”。比特币依据特定算法，通过大量的计算产生，不依靠特定货币机构发行，其使用整个P2P网络中众多节点构成的分布式数据库来确认并记录所有的交易行为，并使用密码学设计确保货币流通各个环节安全性，可确保无法通过大量制造比特币来人为操控币值。基于密码学的设计可以使比特币只能被真实拥有者转移、支付及兑现。同样确保了货币所有权与流通交易的匿名性。
     * enName : BTC,Bitcoin
     * id : 4
     * locale : zh_CN
     * name : 比特币
     * officalAddr : https://bitcoin.org/en/
     * publishCount : 2.1E7
     * publishDate : 1232640000000
     * publishPrice : 5652121.232
     * status : 1
     * symbol : btc
     * whitepaperAddr : https://bitcoin.org/bitcoin.pdf
     */

    private String blockStation;
    private String circulateCount;
    private int deleted;
    private String detail;
    private String enName;
    private int id;
    private String locale;
    private String name;
    private String officalAddr;
    private String publishCount;
    private long publishDate;
    private String publishPrice;
    private int status;
    private String symbol;
    private String whitepaperAddr;

    public String getBlockStation() {
        return blockStation;
    }

    public void setBlockStation(String blockStation) {
        this.blockStation = blockStation;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfficalAddr() {
        return officalAddr;
    }

    public void setOfficalAddr(String officalAddr) {
        this.officalAddr = officalAddr;
    }

    public String getPublishCount() {
        return publishCount;
    }

    public void setPublishCount(String publishCount) {
        this.publishCount = publishCount;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public String getCirculateCount() {
        return circulateCount;
    }

    public void setCirculateCount(String circulateCount) {
        this.circulateCount = circulateCount;
    }

    public String getPublishPrice() {
        return publishPrice;
    }

    public void setPublishPrice(String publishPrice) {
        this.publishPrice = publishPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getWhitepaperAddr() {
        return whitepaperAddr;
    }

    public void setWhitepaperAddr(String whitepaperAddr) {
        this.whitepaperAddr = whitepaperAddr;
    }
}
