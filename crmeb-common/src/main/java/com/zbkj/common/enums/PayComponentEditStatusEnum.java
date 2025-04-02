package com.zbkj.common.enums;

/**
 * @Auther: 大粽子
 * @Date: 2022/10/10 18:43
 * @Description: 微信侧商品审核枚举 1-未审核，2-审核中，3-审核失败，4-审核成功
 */
public enum PayComponentEditStatusEnum {

    BEFORE_REVIEW(1,"未审核"),
    REVIEW_ING(2,"审核中"),
    REVIEW_FAILED(3,"审核失败"),
    REVIEW_SUCCESS(4,"审核成功");

    private String name;
    private Integer code;

    PayComponentEditStatusEnum(Integer code, String name) {
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
