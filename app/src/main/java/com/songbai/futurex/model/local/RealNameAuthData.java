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

    public String getIdcardNum() {
        return idcardNum;
    }

    public void setIdcardNum(String idcardNum) {
        this.idcardNum = idcardNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcardFrontImg() {
        return idcardFrontImg;
    }

    public void setIdcardFrontImg(String idcardFrontImg) {
        this.idcardFrontImg = idcardFrontImg;
    }

    public String getIdcardBackImg() {
        return idcardBackImg;
    }

    public void setIdcardBackImg(String idcardBackImg) {
        this.idcardBackImg = idcardBackImg;
    }

    public String getHandIdcardImg() {
        return handIdcardImg;
    }

    public void setHandIdcardImg(String handIdcardImg) {
        this.handIdcardImg = handIdcardImg;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

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
