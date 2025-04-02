package com.zbkj.common.constants;

/**
 *  商户常量类
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public class MerchantConstants {

    /** 创建类型-管理员创建 */
    public static final String CREATE_TYPE_ADMIN = "admin";
    /** 创建类型-入驻申请 */
    public static final String CREATE_TYPE_APPLY = "apply";


    /** 商户审核状态-待审核 */
    public static final Integer AUDIT_STATUS_WAIT = 1;
    /** 商户审核状态-审核通过 */
    public static final Integer AUDIT_STATUS_SUCCESS = 2;
    /** 商户审核状态-审核拒绝 */
    public static final Integer AUDIT_STATUS_FAIL = 3;

    /** 商户客服类型-H5 */
    public static final String MERCHANT_SERVICE_TYPE_H5 = "H5";
    /** 商户客服类型-Phone */
    public static final String MERCHANT_SERVICE_TYPE_PHONE = "phone";
    /** 商户客服类型-message */
    public static final String MERCHANT_SERVICE_TYPE_MESSAGE = "message";
    /** 商户客服类型-email */
    public static final String MERCHANT_SERVICE_TYPE_EMAIL = "email";

    /** 商户结算类型-银行卡 */
    public static final String MERCHANT_SETTLEMENT_TYPE_BANK = "bank";
    /** 商户结算类型-微信 */
    public static final String MERCHANT_SETTLEMENT_TYPE_WECHAT = "wechat";
    /** 商户结算类型-支付宝 */
    public static final String MERCHANT_SETTLEMENT_TYPE_ALIPAY = "alipay";
}
