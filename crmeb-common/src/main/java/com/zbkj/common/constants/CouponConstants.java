package com.zbkj.common.constants;

/**
 * 优惠券常量类
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
public class CouponConstants {

    /**
     * ---------------------------------------
     * --------优惠券常量----------------------
     * ---------------------------------------
     */

    /** 优惠券发行方-平台 */
    public static final Integer COUPON_PUBLISHER_PLATFORM = 1;
    /** 优惠券发行方-商户 */
    public static final Integer COUPON_PUBLISHER_MERCHANT = 2;

    /** 优惠券类别-商家券 */
    public static final Integer COUPON_CATEGORY_MERCHANT = 1;
    /** 优惠券类别-商品券 */
    public static final Integer COUPON_CATEGORY_PRODUCT = 2;
    /** 优惠券类别-通用券 */
    public static final Integer COUPON_CATEGORY_UNIVERSAL = 3;
    /** 优惠券类别-品类券 */
    public static final Integer COUPON_CATEGORY_PRODUCT_CATEGORY = 4;
    /** 优惠券类别-品牌券 */
    public static final Integer COUPON_CATEGORY_BRAND = 5;
    /** 优惠券类别-跨店券 */
    public static final Integer COUPON_CATEGORY_JOINT_MERCHANT = 6;

    /** 优惠券领取类型—手动领取 */
    public static final Integer COUPON_RECEIVE_TYPE_MANUAL = 1;
    /** 优惠券领取类型—商品买赠送券 */
    public static final Integer COUPON_RECEIVE_TYPE_PAY_PRODUCT = 2;
    /** 优惠券领取类型—平台活动发放 */
    public static final Integer COUPON_RECEIVE_TYPE_PLAT_SEND = 3;

    /** 优惠券类型—满减券 */
    public static final Integer COUPON_TYPE_SATISFY = 1;
    /** 优惠券类型—折扣券 */
    public static final Integer COUPON_TYPE_DISCOUNT = 2;


    /**
     * ---------------------------------------
     * --------用户优惠券常量-------------------
     * ---------------------------------------
     */

    /** 用户优惠券领取类型—用户注册 */
    public static final String STORE_COUPON_USER_TYPE_REGISTER = "new";

    /** 用户优惠券领取类型—用户领取 */
    public static final String STORE_COUPON_USER_TYPE_GET = "receive";

    /** 用户优惠券领取类型—后台发放 */
    public static final String STORE_COUPON_USER_TYPE_SEND = "send";

    /** 用户优惠券领取类型—买赠送 */
    public static final String STORE_COUPON_USER_TYPE_BUY = "buy";

    /** 用户优惠券状态—未使用 */
    public static final Integer STORE_COUPON_USER_STATUS_USABLE = 0;

    /** 用户优惠券状态—已使用 */
    public static final Integer STORE_COUPON_USER_STATUS_USED = 1;

    /** 用户优惠券状态—已失效 */
    public static final Integer STORE_COUPON_USER_STATUS_LAPSED = 2;

}
