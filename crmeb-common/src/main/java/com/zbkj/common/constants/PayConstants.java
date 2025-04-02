package com.zbkj.common.constants;

/**
 *  支付相关常量类
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
public class PayConstants {

    /** 支付方式-微信支付 */
    public static final String PAY_TYPE_WE_CHAT = "weixin";
    /** 支付方式-余额支付 */
    public static final String PAY_TYPE_YUE = "yue";
    /** 支付方式-支付宝支付 */
    public static final String PAY_TYPE_ALI_PAY = "alipay";

    /** 支付渠道-公众号 */
    public static final String PAY_CHANNEL_WECHAT_PUBLIC = "public";
    /** 支付渠道-小程序 */
    public static final String PAY_CHANNEL_WECHAT_MINI = "mini";
    /** 支付渠道-视频号 */
    public static final String PAY_CHANNEL_WECHAT_MINI_VIDEO = "video";
    /** 支付渠道-网页支付 */
    public static final String PAY_CHANNEL_H5 = "h5";
    /** 支付渠道-微信native支付 */
    public static final String PAY_CHANNEL_WECHAT_NATIVE = "native";
    /** 支付渠道-余额支付 */
    public static final String PAY_CHANNEL_YUE = "yue";
    /** 支付渠道-微信Ios */
    public static final String PAY_CHANNEL_WECHAT_APP_IOS = "wechatIos";
    /** 支付渠道-微信Android */
    public static final String PAY_CHANNEL_WECHAT_APP_ANDROID = "wechatAndroid";
    /** 支付渠道-支付宝 */
    public static final String PAY_CHANNEL_ALI_PAY = "alipay";
    /** 支付渠道-支付宝App */
    public static final String PAY_CHANNEL_ALI_APP_PAY = "alipayApp";
    /** 支付渠道-支付宝App */
    public static final String PAY_CHANNEL_ALI_PC_PAY = "alipayPc";

    /** 支付服务类型-订单 */
    public static final String PAY_SERVICE_TYPE_ORDER = "order";
    /** 支付服务类型-充值 */
    public static final String PAY_SERVICE_TYPE_RECHARGE = "recharge";
    /** 支付服务类型-SVIP付费会员 */
    public static final String PAY_SERVICE_TYPE_SVIP = "svip";

    /** 微信交易类型-JSAPI支付（或小程序支付） */
    public static final String WX_PAY_TRADE_TYPE_JS = "JSAPI";
    /** 微信交易类型-H5支付 */
    public static final String WX_PAY_TRADE_TYPE_H5 = "MWEB";
    /** 微信交易类型-APP支付 */
    public static final String WX_PAY_TRADE_TYPE_APP = "APP";
    /** 微信交易类型-Native支付 */
    public static final String WX_PAY_TRADE_TYPE_NATIVE = "NATIVE";

    //微信支付接口请求地址
    public static final String WX_PAY_API_URL = "https://api.mch.weixin.qq.com/";
    // 微信统一预下单
    public static final String WX_PAY_API_URI = "pay/unifiedorder";
    // 微信查询订单
    public static final String WX_PAY_ORDER_QUERY_API_URI = "pay/orderquery";
    // 微信支付回调地址
    public static final String WX_PAY_NOTIFY_API_URI = "/api/publicly/payment/callback/wechat";
    // 微信退款回调地址
    public static final String WX_PAY_REFUND_NOTIFY_API_URI = "/api/publicly/payment/callback/wechat/refund";

    // 支付宝支付回调地址
    public static final String ALI_PAY_NOTIFY_API_URI = "/api/publicly/payment/callback/alipay";

    public static final String WX_PAY_SIGN_TYPE_MD5 = "MD5";
    public static final String WX_PAY_SIGN_TYPE_SHA256 = "HMAC-SHA256";

    public static final String PAY_BODY = "Crmeb支付中心-订单支付";
    public static final String FIELD_SIGN = "sign";

    // 公共号退款
    public static final String WX_PAY_REFUND_API_URI= "secapi/pay/refund";

    /** 微信（H5/PC）支付来源-公众号 */
    public static final String WECHAT_PAY_SOURCE_PUBLIC = "public";
    /** 微信（H5/PC）支付来源-小程序 */
    public static final String WECHAT_PAY_SOURCE_MINI = "mini";
}
