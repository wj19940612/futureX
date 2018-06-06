package com.songbai.futurex.model.local;

/**
 * Modified by john on 2018/6/3
 * <p>
 * Description: 验证码获取组装对象
 * <p>
 * APIs: {@link com.songbai.futurex.http.Apic#}
 */
public class AuthCodeGet {

    public static final int TYPE_REGISTER = 1; // 注册
    public static final int TYPE_MODIFY_PHONE = 2; // 修改手机号
    public static final int TYPE_QUICK_LOGIN = 3;
    public static final int TYPE_SAFE_PSD = 4; // 修改安全密码
    public static final int TYPE_FORGET_PSD = 5;
    public static final int BINDING_EMAIL = 6; // 绑定邮箱
    public static final int HISTORY_ACCOUNT_VERIFY = 7; // 历史账号验证

    private String phone;
    private String email;
    private int type;
    private String imgCode;
    private String teleCode;

    public static final class Builder {
        private String phone;
        private String email;
        private int type;
        private String imgCode;
        private String teleCode;

        private Builder() {
        }

        public static Builder anAuthCodeGet() {
            return new Builder();
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder imgCode(String imgCode) {
            this.imgCode = imgCode;
            return this;
        }

        public Builder teleCode(String teleCode) {
            this.teleCode = teleCode;
            return this;
        }

        public AuthCodeGet build() {
            AuthCodeGet authCodeGet = new AuthCodeGet();
            authCodeGet.teleCode = this.teleCode;
            authCodeGet.phone = this.phone;
            authCodeGet.type = this.type;
            authCodeGet.imgCode = this.imgCode;
            authCodeGet.email = this.email;
            return authCodeGet;
        }
    }
}
