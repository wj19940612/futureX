package com.songbai.futurex.websocket;

import com.songbai.futurex.model.local.SysTime;
import com.songbai.futurex.utils.DateUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

/**
 * Modified by john on 2018/6/12
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class Utils {

    public static Request getRequest(int code, Object o) {
        Request request = new Request(code, o);
        long timestamp = SysTime.getSysTime().getSystemTimestamp();
        request.setTimestamp(String.valueOf(timestamp));
        request.setUuid(createUUID(timestamp));
        return request;
    }

    private static String createUUID(long timestamp) {
        String uuid = UUID.randomUUID().toString();
        String time = DateUtil.format(timestamp, "yyyyMMdd_HHmmss");
        return uuid + "-" + time;
    }

    public static String uncompress(byte[] bytes, String encoding) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}