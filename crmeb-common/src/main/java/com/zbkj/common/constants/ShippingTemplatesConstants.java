package com.zbkj.common.constants;

/**
 * 运费模板常量类
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
public class ShippingTemplatesConstants {

    /** 计费方式：0-未知 */
    public static final int CHARGE_MODE_TYPE_UNKNOWN = 0;
    /** 计费方式：1-按件数 */
    public static final int CHARGE_MODE_TYPE_NUMBER = 1;
    /** 计费方式：2-按重量 */
    public static final int CHARGE_MODE_TYPE_WEIGHT = 2;
    /** 计费方式：3-按体积 */
    public static final int CHARGE_MODE_TYPE_VOLIME = 3;

    /** 包邮类型：0-全国包邮 */
    public static final Integer APPOINT_TYPE_ALL = 0;
    /** 包邮类型：1-部分包邮 */
    public static final Integer APPOINT_TYPE_PART = 1;
    /** 包邮类型：2-自定义 */
    public static final Integer APPOINT_TYPE_DEFINED = 2;

    /** 默认模板名称—全国包邮 */
    public static final String DEFAULT_NAME = "全国包邮";

}
