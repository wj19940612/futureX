package com.songbai.futurex.model;

import com.google.gson.annotations.SerializedName;

/**
 * Modified by $nishuideyu$ on 2018/3/27
 * <p>
 * Description:
 * </p>
 */

public class Host {

    public static final int LOCATION_FOREIGN = 2;

    /**
     * default : 1
     * host : https://c1.ska0p7.com
     * location : 1
     */

    @SerializedName("default")
    private String defaultX;
    private String host;
    private String location;

    public Host(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Host{" +
                "defaultX='" + defaultX + '\'' +
                ", host='" + host + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public boolean isForeign() {
        return location.equals(String.valueOf(LOCATION_FOREIGN));
    }

    public boolean isDefault() {
        return defaultX.equals(String.valueOf("1"));
    }
}
