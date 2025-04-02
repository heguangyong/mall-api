package com.zbkj.common.constants;

/**
 * 定时任务常量类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 *
 */
public class TaskConstants {

    /** 订单支付成功后Task */
    public static final String ORDER_TASK_PAY_SUCCESS_AFTER = "orderPaySuccessTask";
    /** 订单收货后Task */
    public static final String ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER = "alterOrderTakeByUser";
    /** 用户删除订单后续操作 Key */
    public static final String ORDER_TASK_REDIS_KEY_AFTER_DELETE_BY_USER = "alterOrderDeleteByUser";
    /** 用户完成订单后续操作 Key */
    public static final String ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER = "alterOrderCompleteByUser";
    /** 用户取消订单后续操作 Key */
    public static final String ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER = "alterOrderCancelByUser";
    /** 用户订单退款后续操作 Key */
    public static final String ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER = "alterOrderRefundByUser";
    /** 订单自动取消 Key */
    public static final String ORDER_TASK_REDIS_KEY_AUTO_CANCEL_KEY = "order_auto_cancel_key";
    /** 秒杀商品回滚库存key **/
    public static final String TASK_SECKILL_PRODUCT_CALLBACK_KEY = "seckill_product_callback_list";
    /** 退款订单支付宝退款查询 key **/
    public static final String TASK_REFUND_ORDER_ALIPAY_QUERY_KEY = "refundOrderAliPayTask";
    /** 拼团记录和订单状态变更 key **/
    public static final String TASK_GROUP_BUY_RECORD_STATUS_KEY = "groupBuyRecordStatusKey";
}
