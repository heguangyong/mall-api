package com.zbkj.common.enums;

/**
 * @Auther: 大粽子
 * @Date: 2022/10/10 11:44
 * @Description: 微信自定义交易组件 草稿商品 结合系统审核枚举
 * 平台状态:0初始值 5商家上架 6平台上架 11商家下架 12平台下架
 */
public enum PayComponentPlatformStatusEnum {
    INIT(0, "初始值"),
    MERCHANT_PUTON(5, "商家上架"),
    PLATFORM_PUTON(6, "平台上架"),
    MERCHANT_PUTDOWN(11,"商家下架"),
    PLATFORM_PUTDOWN(12, "平台下架")
    ;


    private String name;
    private Integer code;

    PayComponentPlatformStatusEnum(Integer code, String name) {
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
