package com.zbkj.common.constants;

import io.swagger.models.auth.In;

/**
 * 结算常量类
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
public class ClosingConstant {

    /** 结算提现类型：银行卡 */
    public static final String CLOSING_TYPE_BANK = "bank";
    /** 结算提现类型：支付宝 */
    public static final String CLOSING_TYPE_ALIPAY = "alipay";
    /** 结算提现类型：微信 */
    public static final String CLOSING_TYPE_WECHAT = "wechat";

    /** 审核状态——待审核 */
    public static final Integer CLOSING_AUDIT_STATUS_AUDIT = 0;
    /** 审核状态——通过审核 */
    public static final Integer CLOSING_AUDIT_STATUS_SUCCESS = 1;
    /** 审核状态——审核失败 */
    public static final Integer CLOSING_AUDIT_STATUS_FAIL = 2;

    /** 到账状态——未到账 */
    public static final Integer CLOSING_ACCOUNT_STATUS_WAIT = 0;
    /** 到账状态——已到账 */
    public static final Integer CLOSING_ACCOUNT_STATUS_SUCCESS = 1;
}
