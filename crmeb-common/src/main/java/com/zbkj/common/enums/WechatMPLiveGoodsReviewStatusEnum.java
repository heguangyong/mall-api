package com.zbkj.common.enums;

/** 直播间商品状态
 * @Auther: 大粽子
 * @Date: 2023/3/27 19:05
 * @Description: 微信小程序 直播 商品 审核状态
 */
public enum WechatMPLiveGoodsReviewStatusEnum {

    // 0=商户创建/撤回,1=平台待审核/商户重新提交审核，2=平台审核通过，3=平台审核失败,4=微信审核成功，5=微信审核失败
    CREATED(0, "商户创建/撤回"),
    BEFORE_REVIEW(1,"平台待审核/商户重新提交审核"),
    PLAT_REVIEW_SUCCESS(2,"平台审核通过/微信审核中"),
    PLAT_REVIEW_ERROR(3,"平台审核失败"),
    WECHAT_REVIEW_SUCCESS(4,"微信审核成功"),
    WECHAT_REVIEW_ERROR(5,"微信审核失败");

    private Integer code;
    private String message;

    WechatMPLiveGoodsReviewStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
