package com.zbkj.common.constants;

/**
 * 退款订单状态常量类
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
public class RefundOrderConstants {

    /** 退款单审核-通过 */
    public static final String REFUND_ORDER_AUDIT_SUCCESS = "success";
    /** 退款单审核-拒绝 */
    public static final String REFUND_ORDER_AUDIT_REFUSE = "refuse";

    /** 退款单售后类型-仅退款 */
    public static final Integer REFUND_ORDER_AFTER_SALES_TYPE_PRICE = 1;
    /** 退款单售后类型-退货退款 */
    public static final Integer REFUND_ORDER_AFTER_SALES_TYPE_PRICE_PRODUCT = 2;



    /** 退款单日志-申请退款 */
    public static final String REFUND_ORDER_LOG_APPLY = "apply";
    /** 退款单日志-商家审核 */
    public static final String REFUND_ORDER_LOG_AUDIT = "audit";
    /** 退款单日志-商品退回 */
    public static final String REFUND_ORDER_LOG_RETURNING_GOODS = "returning";
    /** 退款单日志-商家确认收货 */
    public static final String REFUND_ORDER_LOG_RECEIVING = "receiving";
    /** 退款单日志-退款成功 */
    public static final String REFUND_ORDER_LOG_REFUND = "refund";
    /** 退款单日志-平台强制退款 */
    public static final String REFUND_ORDER_LOG_COMPULSORY = "compulsory";
    /** 退款单日志-撤销 */
    public static final String REFUND_ORDER_LOG_REVOKE = "revoke";
    /** 退款单日志-拒绝收货 */
    public static final String REFUND_ORDER_LOG_REJECTION_GOODS = "rejectionGoods";

}
