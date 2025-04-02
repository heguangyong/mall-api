package com.zbkj.common.enums;

/**
 * @Auther: 大粽子
 * @Date: 2022/10/10 19:55
 * @Description: 微信 自定义交易组件 商品主状态 线上状态：0-初始值，5-上架，11-自主下架，13-违规下架/风控系统下架
 */
public enum PayComponentStatusEnum {

    STATUS_INIT(0, "初始值"),
    STATUS_PUTON(5, "上架"),
    STATUS_PUTDOWN(11, "下架"),
    STATUS_PUTDOWN_RISK(13, "违规下架/风控系统下架");


    private String name;
    private Integer code;

    PayComponentStatusEnum(Integer code, String name) {
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
