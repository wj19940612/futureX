package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/8/2
 */
public class PromotionInfos {

    /**
     * promotionRule : 这里是规则
     * promotionGroup : 12345678
     * promotionPic : http://www.qqma.com/imgpic2/cpimagenew/2018/4/5/6e1de60ce43d4bf4b9671d7661024e7a.jpg
     */

    private String promotionRule;
    private String promotionGroup;
    private String promotionPic;

    public String getPromotionRule() {
        return promotionRule;
    }

    public void setPromotionRule(String promotionRule) {
        this.promotionRule = promotionRule;
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
}
