package com.songbai.futurex.model;

import com.songbai.futurex.http.Apic;

/**
 * Modified by john on 2018/6/2
 * <p>
 * Description: 区号
 * <p>
 * APIs: {@link Apic#getAreaCodes()}
 */
public class AreaCode {

    /**
     * country : CN
     * englishName : Chinese
     * lang : zh
     * locale : zh-CN
     * name : 中国
     * symbol : zh-CN
     * teleCode : 86
     */

    private String country;
    private String englishName;
    private String lang;
    private String locale;
    private String name;
    private String symbol;
    private String teleCode;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTeleCode() {
        return teleCode;
    }

    public void setTeleCode(String teleCode) {
        this.teleCode = teleCode;
    }

    @Override
    public String toString() {
        return "AreaCode{" +
                "country='" + country + '\'' +
                ", englishName='" + englishName + '\'' +
                ", lang='" + lang + '\'' +
                ", locale='" + locale + '\'' +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", teleCode='" + teleCode + '\'' +
                '}';
    }
}
