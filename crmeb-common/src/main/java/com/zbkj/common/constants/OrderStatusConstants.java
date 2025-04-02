package com.zbkj.common.constants;

/**
 * 订单状态日志常量类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public class OrderStatusConstants {

    /** 操作类型-创建 */
    public static final String ORDER_STATUS_CREATE = "create";
    /** 操作类型-支付成功 */
    public static final String ORDER_STATUS_PAY_SUCCESS = "pay_success";
    /** 操作类型-虚拟发货 */
    public static final String ORDER_STATUS_DELIVERY_VI = "delivery_fictitious";
    /** 操作类型-快递 */
    public static final String ORDER_STATUS_EXPRESS = "express";
    /** 操作类型-支付拆单 */
    public static final String ORDER_STATUS_PAY_SPLIT = "pay_split";
    /** 操作类型-拆单发货历史订单 */
    public static final String ORDER_STATUS_EXPRESS_SPLIT_OLD = "express_split_old";
    /** 操作类型-拆单发货历史订单 */
    public static final String ORDER_STATUS_EXPRESS_SPLIT_NEW = "express_split_new";
    /** 操作类型-用户删除订单 */
    public static final String ORDER_STATUS_USER_DELETE = "user_delete";
    /** 操作类型-用户收货 */
    public static final String ORDER_STATUS_USER_TAKE_DELIVERY = "user_take_delivery";
    /** 操作类型-订单完成 */
    public static final String ORDER_STATUS_COMPLETE = "complete";



    /** 订单操作日志类型说明-生成订单 */
    public static final String ORDER_LOG_MESSAGE_CREATE = "订单生成";
    /** 订单操作日志类型说明-用户付款成功 */
    public static final String ORDER_LOG_MESSAGE_PAY_SUCCESS = "用户付款成功";
    /** 订单操作日志类型说明-虚拟发货 */
    public static final String ORDER_LOG_MESSAGE_DELIVERY_VI = "虚拟发货";
    /** 订单操作日志类型说明-快递发货 */
    public static final String ORDER_LOG_MESSAGE_EXPRESS = "已发货 快递公司：{deliveryName}, 快递单号：{deliveryCode}";
    /** 订单操作日志类型说明-快递发货 */
    public static final String ORDER_LOG_MESSAGE_SEND_MERCHANT = "已发货 配送人员：{}, 手机号：{}";
    /** 订单操作日志类型说明-无需发货 */
    public static final String ORDER_LOG_MESSAGE_SEND_NO_NEED = "已发货 发货备注：{}";
    /** 订单操作日志类型说明-支付拆单 */
    public static final String ORDER_LOG_MESSAGE_PAY_SPLIT = "支付成功按商户拆单，历史单号为：{}";
    /** 订单操作日志类型说明-支付拆单 */
    public static final String ORDER_LOG_MESSAGE_EXPRESS_SPLIT = "拆单发货，历史单号为：{}";
    /** 订单操作日志类型说明-用户删除订单 */
    public static final String ORDER_LOG_USER_DELETE = "用户删除订单";
    /** 订单操作日志类型说明-用户收货 */
    public static final String ORDER_LOG_USER_RECEIPT = "用户已收货";
    /** 订单操作日志类型说明-系统自动收货 */
    public static final String ORDER_LOG_SYSTEM_AUTO_RECEIPT = "系统自动收货";
}
