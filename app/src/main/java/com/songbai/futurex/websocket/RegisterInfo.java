package com.songbai.futurex.websocket;

import com.songbai.futurex.BuildConfig;
import com.songbai.futurex.Preference;

/**
 * Modified by john on 2018/6/12
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class RegisterInfo {

    private String host;
    private String channel;
    private String token1;
    private String token2;
    private String device;

    public RegisterInfo(String tokens) {
        host = BuildConfig.HOST;
        // 测试包切换环境
        if ("alpha".equals(BuildConfig.FLAVOR)) {
            if (!BuildConfig.IS_PROD) {
                host = Preference.get().getAlphaHost();
            }
            host = Preference.get().getAlphaHost();
        }
        String[] strings = processTokens(tokens);
        if (strings.length == 2) {
            token1 = strings[0];
            token2 = strings[1];
        }
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    private String[] processTokens(String tokens) {
        if (tokens == null) return new String[0];

        String[] strings = tokens.split(";");
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            strings[i] = str.substring(str.indexOf("=") + 1);
        }
        return strings;
    }


    @Override
    public String toString() {
        return "RegisterInfo{" +
                "host='" + host + '\'' +
                ", channel='" + channel + '\'' +
                ", token1='" + token1 + '\'' +
                ", token2='" + token2 + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
