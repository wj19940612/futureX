package com.songbai.futurex.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author yangguangda
 * @date 2018/8/2
 */
public class PromotionInfos {

    /**
     * promotionPicList : https://goss3.vcg.com/creative/vcg/400/version23/VCG21gic16805638.jpg,https://goss2.vcg.com/creative/vcg/400/version23/VCG219ee1a110d.jpg
     * promotionRule : <p style="white-space: normal"><strong><span style="font-family: 宋体">活动规则</span></strong></p><p style="white-space: normal"><span style="font-family: 宋体;"><b>1.&nbsp; &nbsp;</b>活动时间：</span>2018.8.15<span style="font-family: 宋体;">起。</span></p><p style="white-space: normal"><span style="font-variant-numeric: normal; font-variant-east-asian: normal; font-stretch: normal; line-height: normal;"><font face="宋体">2.&nbsp;</font></span><span style="font-variant-numeric: normal; font-variant-east-asian: normal; font-stretch: normal; font-size: 9px; line-height: normal; font-family: &quot;Times New Roman&quot;;">&nbsp; &nbsp;</span><span style="font-family: 宋体;">每个用户自己注册并通过高级认证都可获得</span>100<span style="font-family: 宋体;">个</span>BFB<span style="font-family: 宋体;">奖励。</span></p><p>3.&nbsp; &nbsp;<span style="font-family: 宋体">邀请好友，可额外获得好友交易手续费</span>10%<span style="font-family: 宋体">的</span>BFB<span style="font-family: 宋体">奖励：被邀请人注册并完成高级认证那日起</span>90<span style="font-family: 宋体">天内产生的挖矿收入，邀请人可获得其对应</span>10%<span style="font-family: 宋体">的奖励。</span></p><p><span style="font-family: 宋体">4.&nbsp;</span>&nbsp;&nbsp;<span style="font-family: 宋体">被邀请人必须使用您的邀请码或邀请链接注册才可，邀请成功的定义：被邀请人注册并高级认证成功。</span></p><p style="white-space: normal">5.&nbsp;&nbsp;<span style="font-family: 宋体;">一级邀请奖励：</span>20<span style="font-family: 宋体;">个</span>BFB/<span style="font-family: 宋体;">人</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style="font-family: 宋体;">二级邀请奖励：</span>10<span style="font-family: 宋体;">个</span>BFB/<span style="font-family: 宋体;">人</span><span style="font-family: 宋体;">（邀请一名好友注册并通过高级认证，可获得</span>20<span style="font-family: 宋体;">个</span>BFB<span style="font-family: 宋体;">，被邀请的好友再邀请</span>1<span style="font-family: 宋体;">名好友注册并通过高级认证，即可获得</span>10<span style="font-family: 宋体;">个</span>BFB<span style="font-family: 宋体;">。）</span><br/></p><p style="white-space: normal">6. &nbsp;<span style="font-family: 宋体;">奖励按日发放，于次日发放至您的币币账户，流水类型为「邀请奖励」。</span></p><p style="white-space: normal">7.&nbsp;&nbsp;<span style="font-family: 宋体;">福利不封顶</span>&nbsp;<span style="font-family: 宋体;">邀请越多赚的越多。</span></p><p style="white-space: normal">8.&nbsp;&nbsp;<span style="font-family: 宋体;">用户获得的</span>BFB<span style="font-family: 宋体;">可在平台上进行交易，因活动人数过多，认证审核时间可能会延长，请耐心等待。</span></p><p style="white-space: normal">9.&nbsp;&nbsp;<span style="font-family: 宋体;">活动期间，如有作弊刷量行为，将取消活动资格</span>; BITFUTURE<span style="font-family: 宋体;">保留对活动的最终解释权。</span></p><p style="white-space: normal">&nbsp;</p><p style="white-space: normal"><strong><span style="font-family: 宋体">奖励金计算举例：</span></strong></p><p style="white-space: normal">A<span style="font-family: 宋体;">用户自己注册并通过高级认证，用自己的邀请码邀请</span>B,C,D<span style="font-family: 宋体;">三个用户</span></p><p style="white-space: normal">B,C,D<span style="font-family: 宋体">又各自再用自己的邀请码邀请了</span>E,F,G<span style="font-family: 宋体">三个用户（</span>B<span style="font-family: 宋体">邀请</span>E,C<span style="font-family: 宋体">邀请</span>F,D<span style="font-family: 宋体">邀请</span>G<span style="font-family: 宋体">）</span>&nbsp;<span style="font-family: 宋体;">且</span>B,C,D,E,F,G<span style="font-family: 宋体;">都高级认证成功</span></p><p style="white-space: normal"><span style="font-family: 宋体">对于</span>A<span style="font-family: 宋体">用户：</span>B,C,D<span style="font-family: 宋体">是一级用户，</span>E,F,G<span style="font-family: 宋体">是二级用户，总共获取奖励：</span><span style="text-align: center;">100+20*3+10*3+B,C,D,E,F,G</span><span style="text-align: center; font-family: 宋体;">用户</span><span style="text-align: center;">90</span><span style="text-align: center; font-family: 宋体;">天内的交易手续费</span><span style="text-align: center;">10%</span></p><p style="white-space: normal"><span style="font-family: 宋体">对于</span>B<span style="font-family: 宋体">用户：</span>E<span style="font-family: 宋体">是一级用户，暂时没有二级用户（</span>E<span style="font-family: 宋体">没有邀请用户），总共获取奖励：</span><span style="text-align: center;">100+20+E</span><span style="text-align: center; font-family: 宋体;">用户</span><span style="text-align: center;">90</span><span style="text-align: center; font-family: 宋体;">天内的交易说续费</span><span style="text-align: center;">10%</span></p><p style="white-space: normal"><span style="font-family: 宋体">对于</span>E<span style="font-family: 宋体">用户：由于没有再邀请人，所以只能拿到注册认证奖励</span></p><p style="white-space: normal"><span style="text-align: right; text-indent: 238px; font-family: 宋体;">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 如还有疑问请联系官方客服</span><span style="text-align: right; text-indent: 238px;">QQ</span><span style="text-align: right; text-indent: 238px; font-family: 宋体;">号</span><span style="text-align: right; text-indent: 238px;">3014324699</span></p><p style="white-space: normal;text-align: right">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style="font-family: 宋体">微信号：</span>bitfutu_re</p><p style="white-space: normal">&nbsp;</p><p><br/></p>
     * shareContent : {"host":"https://bitfutu.re/pro/7w57u","pic":"www.baidu.com","title":"这是标题","body":"这是描述"}
     * promotionShare : 全球首家创世联盟BF交易所带你零风险玩赚区块链，注册领取1000000平台币，持有平台币可享受分红，投票上币等权利，成为联盟股东（还有丰厚推广奖励等你拿）
     * promotionGroup : 12345678
     * promotionPic : https://bitexcn.oss-cn-shanghai.aliyuncs.com/upload/20180817154904286/useri1534492144286.png
     */

    private String promotionPicList;
    private String promotionRule;
    private ShareContentBean shareContent;
    private String promotionShare;
    private String promotionGroup;
    private String promotionPic;

    public String getPromotionPicList() {
        return promotionPicList;
    }

    public void setPromotionPicList(String promotionPicList) {
        this.promotionPicList = promotionPicList;
    }

    public String getPromotionRule() {
        return promotionRule;
    }

    public void setPromotionRule(String promotionRule) {
        this.promotionRule = promotionRule;
    }

    public ShareContentBean getShareContent() {
        return shareContent;
    }

    public void setShareContent(ShareContentBean shareContent) {
        this.shareContent = shareContent;
    }

    public String getPromotionShare() {
        return promotionShare;
    }

    public void setPromotionShare(String promotionShare) {
        this.promotionShare = promotionShare;
    }

    public String getPromotionGroup() {
        return promotionGroup;
    }

    public void setPromotionGroup(String promotionGroup) {
        this.promotionGroup = promotionGroup;
    }

    public String getPromotionPic() {
        return promotionPic;
    }

    public void setPromotionPic(String promotionPic) {
        this.promotionPic = promotionPic;
    }

    public static class ShareContentBean implements Parcelable {
        /**
         * host : https://bitfutu.re/pro/7w57u
         * pic : www.baidu.com
         * title : 这是标题
         * body : 这是描述
         */

        private String host;
        private String pic;
        private String title;
        private String body;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.host);
            dest.writeString(this.pic);
            dest.writeString(this.title);
            dest.writeString(this.body);
        }

        public ShareContentBean() {
        }

        protected ShareContentBean(Parcel in) {
            this.host = in.readString();
            this.pic = in.readString();
            this.title = in.readString();
            this.body = in.readString();
        }

        public static final Parcelable.Creator<ShareContentBean> CREATOR = new Parcelable.Creator<ShareContentBean>() {
            @Override
            public ShareContentBean createFromParcel(Parcel source) {
                return new ShareContentBean(source);
            }

            @Override
            public ShareContentBean[] newArray(int size) {
                return new ShareContentBean[size];
            }
        };
    }
}
