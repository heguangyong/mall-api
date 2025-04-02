package com.zbkj.common.constants;

/**
 * 通知常量类
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
public class NotifyConstants {

    /** 消息开关状态-不存在 */
    public static Integer SWITCH_NOT_EXIST = 0;
    /** 消息开关状态-开启 */
    public static Integer SWITCH_OPEN = 1;
    /** 消息开关状态-关闭 */
    public static Integer SWITCH_COLSE = 2;

    /** 详情类型——公众号模板消息 */
    public static String DETAIL_TYPE_WECHAT = "wechat";
    /** 详情类型——小程序模板消息 */
    public static String DETAIL_TYPE_ROUTINE = "routine";
    /** 详情类型——短信 */
    public static String DETAIL_TYPE_SMS = "sms";

    /** 支付成功标记*/
    public static final String PAY_SUCCESS_MARK = "paySuccess";
    /** 发货标记 */
    public static final String DELIVER_GOODS_MARK = "deliverGoods";
    /** 收货标记 */
    public static final String RECEIPT_GOODS_MARK = "receiptGoods";
    /** 拼团成功标记 */
    public static final String GROUP_SUCCESS_MARK = "groupSuccess";
    /** 砍价成功标记 */
    public static final String BARGAINING_SUCCESS_MARK = "bargainingSuccess";
    /** 订单配送标记 */
    public static final String FULFILLMENT_ORDER_MARK = "fulfillmentOrder";
    /** 充值成功标记 */
    public static final String RECHARGE_SUCCESS_MARK = "rechargeSuccess";
    /** 入驻审核通过标记 */
    public static final String AUDIT_SUCCESS_MARK = "auditSuccess";
    /** 入驻审核未通过标记 */
    public static final String AUDIT_FAIL_MARK = "auditFail";
    /** 改价通知 */
    public static final String MODIFY_ORDER_PRICE = "modifyOrderPrice";
    /** 生日礼标记 */
    public static final String BIRTHDAY_PRESENT_MARK = "birthdayPresent";
    /** 商户管理员下单提醒 */
    public static final String MERCHANT_PAY_SUCCESS_REMINDER = "merchantPaySuccess";
    /** 商户入驻平台管理提醒 */
    public static final String MERCHANT_SETTLED_APPLY = "merchantSettledApply";

}
