package com.zbkj.common.enums;

/**
 * @Auther: 大粽子
 * @Date: 2022/10/10 11:44
 * @Description: 微信自定义交易组件 草稿商品 结合系统审核枚举
 * 平台审核状态:1-未审核，2-平台审核中，3-平台审核失败，4-平台审核成功
 */
public enum PayComponentPlatformEditStatusEnum {
    INIT(1,"待审核"),
    PLATFORM_REVIEW_ING(2, "平台审核中"),
    PLATFORM_REVIEW_FAILED(3, "平台审核失败"),
    PLATFORM_REVIEW_SUCCEEDED(4, "平台审核成功")
    ;


    private String name;
    private Integer code;

    PayComponentPlatformEditStatusEnum(Integer code, String name) {
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
