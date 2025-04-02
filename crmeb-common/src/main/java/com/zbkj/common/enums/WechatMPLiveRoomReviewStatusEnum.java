package com.zbkj.common.enums;

/** 直播间的
 * 审核:0=商户创建成功平台待审核，1=平台审核失败，2=平台审核成功并提交给微信审核失败，3=平台审核成功并提交给微信审核成功
 * @Auther: 大粽子
 * @Date: 2023/3/29 12:21
 * @Description: 微信直播间审核状态枚举
 */
public enum WechatMPLiveRoomReviewStatusEnum {

    BEFORE_REVIEW(0, "商户创建成功平台待审核"),
    PLAT_REVIEW_ERROR(1, "平台审核失败"),
    WECHAT_REVIEW_ERROR(2, "平台审核成功并提交给微信审核失败"),
    WECHAT_REVIEW_SUCCESS(3, "平台审核成功并提交给微信审核成功");

    private Integer code;
    private String message;

    WechatMPLiveRoomReviewStatusEnum(Integer code, String message) {
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
