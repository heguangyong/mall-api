package com.zbkj.common.constants;

/**
 * 帐单常量类
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
public class BillConstants {

    /** 帐单PM-支出 */
    public static final Integer BILL_PM_SUB = 0;
    /** 帐单PM-收入 */
    public static final Integer BILL_PM_ADD = 1;

    /** 帐单类型-订单支付 */
    public static final String BILL_TYPE_PAY_ORDER = "pay_order";
    /** 帐单类型-订单退款 */
    public static final String BILL_TYPE_REFUND_ORDER = "refund_order";
    /** 帐单类型-用户充值 */
    public static final String BILL_TYPE_RECHARGE_USER = "recharge_user";
    /** 帐单类型-余额支付 */
    public static final String BILL_TYPE_YUE_PAY = "yue_pay";
    /** 帐单类型-商户分账 */
    public static final String BILL_TYPE_MERCHANT_COLLECT = "merchant_collect";
    /** 帐单类型-佣金 */
    public static final String BILL_TYPE_BROKERAGE = "brokerage";
    /** 帐单类型-系统 */
    public static final String BILL_TYPE_SYSTEM = "system";


    /** 商户帐单类型-结算 */
    public static final String MERCHANT_BILL_TYPE_SETTLEMENT = "settlement";

    /** 用户帐单类型-系统增加 */
    public static final String USER_BILL_TYPE_SYSTEM_ADD = "system_add";
    /** 用户帐单类型-系统减少 */
    public static final String USER_BILL_TYPE_SYSTEM_SUB = "system_sub";
}
