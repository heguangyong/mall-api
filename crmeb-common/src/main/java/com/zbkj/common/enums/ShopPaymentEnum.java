package com.zbkj.common.enums;

/** 小程序自定义交易组件 枚举
 * @Auther: 大粽子
 * @Date: 2022/9/30 11:10
 * @Description: 描述对应的业务场景
 */
public enum ShopPaymentEnum {

    /** 微信自定义交易组件 常用状态 订单状态：10-待付款，11-收银台支付完成（自动流转，对商家来说和10同等对待即可），20-待发货，30-待收货，100-完成，200-全部商品售后之后，订单取消，250-用户主动取消/待付款超时取消/商家取消 */
    ORDER_WAITE_PAY(10, "待付款"),
    ORDER_PAY_SUCCESS_COLLECT(11, "收银台支付完成（自动流转，对商家来说和10同等对待即可）"),
    ORDER_DELIVERY(20, "待发货"),
    ORDER_RECEIPT(30, "待收获"),
    ORDER_FINISH(100, "完成"),
    ORDER_ALL_ORDER_AFTER_SALES(200, "全部商品售后之后，订单取消"),
    ORDER_USER_CANCEL(250, "用户主动取消/待付款超时取消/商家取消"),
            ;
    private Integer code;
    private String message;

    ShopPaymentEnum(Integer code, String message) {
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
