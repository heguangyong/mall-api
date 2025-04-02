package com.zbkj.common.enums;

/**
 * 类目申请 状态
 * @Auther: 大粽子
 * @Date: 2022/10/19 17:11
 * @Description:  status  0初始化 1微信侧审核中 2微信侧审核失败 3微信侧审核成功
 */
public enum PayComponentCatStatusEnum {

    INIT(0, "初始化"),
    WECHAT_REVIEW_ING(1, "1微信侧审核中"),
    WECHAT_REVIEW_FAILED(2, "2微信侧审核失败"),
    WECHAT_REVIEW_SUCCESS(3, "3微信侧审核成功"),
    ;

    private String name;
    private Integer code;

    PayComponentCatStatusEnum(Integer code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
