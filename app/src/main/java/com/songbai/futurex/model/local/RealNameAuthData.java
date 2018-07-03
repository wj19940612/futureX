package com.songbai.futurex.model.local;

/**
 * @author yangguangda
 * @date 2018/6/11
 */
public class RealNameAuthData {
    String idcardNum;
    String name;
    String idcardFrontImg;
    String idcardBackImg;
    String handIdcardImg;
    int idType;

    public static final class Builder {
        String idcardNum;
        String name;
        String idcardFrontImg;
        String idcardBackImg;
        String handIdcardImg;
        int idType;

        public static Builder create() {
            return new Builder();
        }

        public Builder idcardNum(String idcardNum) {
            this.idcardNum = idcardNum;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder idcardFrontImg(String idcardFrontImg) {
            this.idcardFrontImg = idcardFrontImg;
            return this;
        }

        public Builder idcardBackImg(String idcardBackImg) {
            this.idcardBackImg = idcardBackImg;
            return this;
        }

        public Builder handIdcardImg(String handIdcardImg) {
            this.handIdcardImg = handIdcardImg;
            return this;
        }

        public Builder idType(int type) {
            this.idType = type;
            return this;
        }

        public RealNameAuthData build() {
            RealNameAuthData realNameAuthData = new RealNameAuthData();
            realNameAuthData.idcardNum = this.idcardNum;
            realNameAuthData.name = this.name;
            realNameAuthData.idcardFrontImg = this.idcardFrontImg;
            realNameAuthData.idcardBackImg = this.idcardBackImg;
            realNameAuthData.handIdcardImg = this.handIdcardImg;
            realNameAuthData.idType = this.idType;
            return realNameAuthData;
        }
    }
}
