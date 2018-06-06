package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/6/6
 */
public class CreateGoogleKey {
    /**
     * googleKey : LNKSGSQIPV3UUBRD
     * qrCode : otpauth://totp/18767116420?secret=LNKSGSQIPV3UUBRD&issuer=ex.esongbai.abc
     */

    private String googleKey;
    private String qrCode;

    public String getGoogleKey() {
        return googleKey;
    }

    public void setGoogleKey(String googleKey) {
        this.googleKey = googleKey;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
