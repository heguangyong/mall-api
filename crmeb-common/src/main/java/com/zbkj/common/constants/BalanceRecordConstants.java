package com.zbkj.common.constants;

/**
 * 余额记录常量类
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
public class BalanceRecordConstants {

    /** 余额记录类型—增加 */
    public static final Integer BALANCE_RECORD_TYPE_ADD = 1;
    /** 余额记录类型—扣减 */
    public static final Integer BALANCE_RECORD_TYPE_SUB = 2;

    /** 余额记录关联类型—订单 */
    public static final String BALANCE_RECORD_LINK_TYPE_ORDER = "order";
    /** 余额记录关联类型—充值 */
    public static final String BALANCE_RECORD_LINK_TYPE_RECHARGE = "recharge";
    /** 余额记录关联类型—系统后台 */
    public static final String BALANCE_RECORD_LINK_TYPE_SYSTEM = "system";
    /** 余额记录关联类型—佣金转余额 */
    public static final String BALANCE_RECORD_LINK_TYPE_BROKERAGE = "brokerage";
    /** 余额记录关联类型—佣金转余额 */
    public static final String BALANCE_RECORD_LINK_TYPE_SVIP = "svip";

    /** 余额记录备注—用户订单付款成功 */
    public static final String BALANCE_RECORD_REMARK_ORDER = "用户订单付款成功,扣余额{}元";
    /** 余额记录备注—用户订单退款成功 */
    public static final String BALANCE_RECORD_REMARK_ORDER_REFUND = "用户订单退款成功,返还余额{}元";
    /** 余额记录备注—后台操作添加 */
    public static final String BALANCE_RECORD_REMARK_SYSTEM_ADD = "后台操作,添加余额{}元";
    /** 余额记录备注—后台操作减少 */
    public static final String BALANCE_RECORD_REMARK_SYSTEM_SUB = "后台操作,减少余额{}元";
    /** 余额记录备注—充值 */
    public static final String BALANCE_RECORD_REMARK_RECHARGE = "充值成功，余额增加{}元";
    /** 余额记录备注—佣金转余额 */
    public static final String BALANCE_RECORD_REMARK_BROKERAGE = "佣金转余额成功，添加余额{}元";
    /** 余额记录备注—充值 */
    public static final String BALANCE_RECORD_REMARK_PAY_SVIP = "购买SVIP成功，余额赠送{}元";
    /** 余额记录备注—购买svip订单 */
    public static final String BALANCE_RECORD_REMARK_SVIP_ORDER = "余额购买SVIP成功，扣除余额{}元";

}
