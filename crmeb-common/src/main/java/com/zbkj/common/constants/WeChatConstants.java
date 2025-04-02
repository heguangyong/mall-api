package com.zbkj.common.constants;

/** 微信配置
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
public class WeChatConstants {

    /** 微信token类型-公众号 */
    public static final String WECHAT_TOKEN_TYPE_PUBLIC = "public";
    /** 微信token类型-小程序 */
    public static final String WECHAT_TOKEN_TYPE_MINI = "mini";
    /** 微信token类型-APP */
    public static final String WECHAT_TOKEN_TYPE_APP = "app";

    //-------------------------------------------微信系统配置------------------------------------------------------------
    /** 公众号appId key */
    public static final String WECHAT_PUBLIC_APPID = "wechat_appid";
    /** 公众号appSecret key */
    public static final String WECHAT_PUBLIC_APPSECRET = "wechat_appsecret";
    /** 小程序appId key */
    public static final String WECHAT_MINI_APPID = "routine_appid";
    /** 小程序appSecret key */
    public static final String WECHAT_MINI_APPSECRET = "routine_appsecret";
    /** 小程序 名称 */
    public static final String WECHAT_MINI_NAME = "routine_name";
    /** 微信小程序消息服务器配置的token */
    public static final String WECHAT_MINI_TOKEN = "routine_token";
    /** 微信小程序消息服务器配置的EncodingAESKey */
    public static final String WECHAT_MINI_AES_KEY = "routine_aes_key";
    /** 微信小程序消息格式 */
    public static final String WECHAT_MINI_MSG_DATA_FORMAT = "JSON";

    /** 公众号 支付商户号 */
    public static final String WECHAT_PAY_PUBLIC_MCHID = "pay_weixin_mchid";
    /** 公众号 支付key */
    public static final String WECHAT_PAY_PUBLIC_KEY = "pay_weixin_key";
    /** 公众号 支付证书地址 */
    public static final String WECHAT_PAY_PUBLIC_CERTIFICATE_PATH = "pay_weixin_certificate_path";
    /** 小程序 支付商户号 */
    public static final String WECHAT_PAY_MINI_MCHID = "pay_routine_mchid";
    /** 小程序 支付key */
    public static final String WECHAT_PAY_MINI_KEY = "pay_routine_key";
    /** 小程序 支付证书地址 */
    public static final String WECHAT_PAY_MINI_CERTIFICATE_PATH = "pay_routine_certificate_path";

    /** 微信app appId */
    public static final String WECHAT_APP_APPID = "wechat_app_appid";
    /** 微信app 商户号 */
    public static final String WECHAT_PAY_APP_MCHID = "pay_weixin_app_mchid";
    /** 微信app 支付key */
    public static final String WECHAT_PAY_APP_KEY = "pay_weixin_app_key";
    /** 微信app 支付证书地址 */
    public static final String WECHAT_PAY_APP_CERTIFICATE_PATH = "pay_weixin_app_certificate_path";

    //------------------------------------------------微信公众号------------------------------------------------
    //微信接口请求地址
    public static final String API_URL = "https://api.weixin.qq.com/";
    //获取token
    public static final String API_TOKEN_URI = "cgi-bin/token?grant_type=client_credential";
    // 微信token 过期时间，娶了一个中间值 4000 官方的7200不靠谱
    public static final Long API_TOKEN_EXPIRES = 3000L;
    //微信公众号菜单创建
    public static final String PUBLIC_API_MENU_CREATE_URI = "cgi-bin/menu/create";
    //微信公众号菜单获取
    public static final String PUBLIC_API_MENU_GET_URI = "cgi-bin/menu/get";
    //微信公众号菜单删除
    public static final String PUBLIC_API_MENU_DELETE_URI = "cgi-bin/menu/delete";
    //微信公众号，获取自定义菜单配置接口
    public static final String PUBLIC_API_MENU_SELF_SET_URI = "cgi-bin/get_current_selfmenu_info";
    //微信公众号，创建个性化菜单
    public static final String PUBLIC_API_MENU_ADD_CONDITIONAL_URI = "cgi-bin/menu/addconditional";
    //微信公众号，删除个性化菜单
    public static final String PUBLIC_API_MENU_DEL_CONDITIONAL_URI = "cgi-bin/menu/delconditional";
    //微信公众号，测试个性化菜单匹配结果
    public static final String PUBLIC_API_USER_INFO_URI = "cgi-bin/menu/trymatch";
    //获取公众号已创建的标签
    public static final String PUBLIC_API_TAG_LIST_URI = "cgi-bin/tags/get";
    //创建标签
    public static final String PUBLIC_API_TAG_CREATE_URI = "cgi-bin/tags/create";
    //编辑标签
    public static final String PUBLIC_API_TAG_UPDATE_URI = "cgi-bin/tags/update";
    //删除标签
    public static final String PUBLIC_API_TAG_DELETE_URI = "cgi-bin/tags/delete";
    //获取标签下粉丝列表
    public static final String PUBLIC_API_TAG_USER_GET_URI = "cgi-bin/user/tag/get";
    //批量为用户打标签
    public static final String PUBLIC_API_TAG_MEMBER_BATCH_URI = "cgi-bin/tags/members/batchtagging";
    //批量为用户取消标签
    public static final String PUBLIC_API_TAG_MEMBER_BATCH_UN_URI = "cgi-bin/tags/members/batchuntagging";
    //获取用户身上的标签列表
    public static final String PUBLIC_API_TAG_GET_ID_LIST_URI = "cgi-bin/tags/getidlist";
    //获取 JsApiTicket
    public static final String PUBLIC_API_JS_API_TICKET = "cgi-bin/ticket/getticket";
    //发送公众号模板消息
    public static final String PUBLIC_API_PUBLIC_TEMPLATE_MESSAGE_SEND = "cgi-bin/message/template/send";
    //获取设置的行业信息
    public static final String PUBLIC_API_TEMPLATE_MESSAGE_INDUSTRY = "cgi-bin/template/get_industry";
    //发送客服消息
    public static final String PUBLIC_API_KF_MESSAGE_SEND = "cgi-bin/message/custom/send";


    //------------------------------------------------微信小程序------------------------------------------------
    //小程序行业消息
    public static final String PUBLIC_API_PROGRAM_CATEGORY = "wxaapi/newtmpl/getcategory";
    //小程序公共模板库
    public static final String PUBLIC_API_PROGRAM_PUBLIC_TEMP = "wxaapi/newtmpl/getpubtemplatetitles";
    //小程序模板关键词列表
    public static final String PUBLIC_API_PROGRAM_PUBLIC_TEMP_KEYWORDS = "wxaapi/newtmpl/getpubtemplatekeywords";
    //添加小程序订阅消息
    public static final String PUBLIC_API_ADD_PROGRAM_TEMPLATE = "wxaapi/newtmpl/addtemplate";
    //删除小程序订阅消息
    public static final String PUBLIC_API_DELETE_PROGRAM_TEMPLATE = "wxaapi/newtmpl/deltemplate";
    //发送小程序模板消息
    public static final String PUBLIC_API_PROGRAM_TEMPLATE_MESSAGE_SEND = "cgi-bin/message/subscribe/send";

    //授权登录

    //获取临时code跳转地址
    public static final String WE_CHAT_AUTHORIZE_REDIRECT_URI_URL  = "/api/front/wechat/authorize/login";

    //获取openId
    public static final String WE_CHAT_AUTHORIZE_GET_OPEN_ID = "sns/oauth2/access_token";

    //获取小程序openId
    public static final String WE_CHAT_AUTHORIZE_PROGRAM_GET_OPEN_ID = "sns/jscode2session";

    //获取用户信息
    public static final String WE_CHAT_AUTHORIZE_GET_USER_INFO = "sns/userinfo";

    //生成二维码
    public static final String WE_CHAT_CREATE_QRCODE = "wxa/getwxacodeunlimit";

    //微信消息存储队列
    public static final String WE_CHAT_MESSAGE_SEND_KEY = "we_chat_message_send_list";

    //大家注意这里消息类型的定义，以 RESP 开头的表示返回的消息类型，以 REQ 表示微信服务器发来的消息类型
    /**
     * 返回消息类型：文本
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_TEXT = "text";
    /**
     * 返回消息类型：音乐
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_MUSIC = "music";
    /**
     * 返回消息类型：图文
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_NEWS = "news";
    /**
     * 返回消息类型：图片
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_IMAGE = "image";
    /**
     * 返回消息类型：语音
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_VOICE = "voice";
    /**
     * 返回消息类型：视频
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_VIDEO = "video";
    /**
     * 请求消息类型：文本
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_TEXT = "text";
    /**
     * 请求消息类型：图片
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_IMAGE = "image";
    /**
     * 请求消息类型：链接
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_LINK = "link";
    /**
     * 请求消息类型：地理位置
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_LOCATION = "location";
    /**
     * 请求消息类型：音频
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_VOICE = "voice";
    /**
     * 请求消息类型：视频
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_VIDEO = "video";
    /**
     * 请求消息类型：推送
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_EVENT = "event";
    /**
     * 事件类型：subscribe(订阅)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_SUBSCRIBE = "subscribe";
    /**
     * 事件类型：unsubscribe(取消订阅)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    /**
     * 事件类型：CLICK(自定义菜单点击事件)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_CLICK = "click";
    /**
     * 事件类型：VIEW(自定义菜单 URl 视图)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_VIEW = "view";
    /**
     * 事件类型：LOCATION(上报地理位置事件)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_LOCATION = "LOCATION";
    /**
     * 事件类型：LOCATION(上报地理位置事件)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_SCAN = "SCAN";

    //无效关键字key
    public static final String WE_CHAT_MESSAGE_DEFAULT_CONTENT_KEY = "default";
    //Js sdk api 列表
    public static final String PUBLIC_API_JS_API_SDK_LIST = "editAddress,openAddress,updateTimelineShareData,updateAppMessageShareData,onMenuShareTimeline,onMenuShareAppMessage,onMenuShareQQ,onMenuShareWeibo,onMenuShareQZone,startRecord,stopRecord,onVoiceRecordEnd,playVoice,pauseVoice,stopVoice,onVoicePlayEnd,uploadVoice,downloadVoice,chooseImage,previewImage,uploadImage,downloadImage,translateVoice,getNetworkType,openLocation,getLocation,hideOptionMenu,showOptionMenu,hideMenuItems,showMenuItems,hideAllNonBaseMenuItem,showAllNonBaseMenuItem,closeWindow,scanQRCode,chooseWXPay,openProductSpecificView,addCard,chooseCard,openCard";


    //token
    public static final String REDIS_TOKEN_KEY = "wechat_token";
    public static final String REDIS_PROGRAM_TOKEN_KEY = "wechat_program_token";
    //tag
    public static final String REDIS_TAGS_LIST_KEY = "wechat_tags_list";
    //user tag
    public static final String REDIS_TAGS_LIST_USER_KEY = "wechat_tags_user_list";
    //微信菜单
    public static final String REDIS_PUBLIC_MENU_KEY = "wechat_public_menu_key";
    //微信自定义菜单
    public static final String REDIS_PUBLIC_MENU_SELF_KEY = "wechat_public_menu_self_key";



    //授权请求地址
    public static final String WE_CHAT_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={$appId}&redirect_uri={$redirectUri}&response_type=code&scope=snsapi_base&state=#wechat_redirect";


    //-------------------------------------------微信支付------------------------------------------------------------
    //微信支付接口请求地址
    public static final String PAY_API_URL = "https://api.mch.weixin.qq.com/";
    public static final String PAY_API_URI = "pay/unifiedorder";
    public static final String PAY_NOTIFY_API_URI_WECHAT = "/api/admin/payment/callback/wechat";
    // 公共号退款
    public static final String PAY_REFUND_API_URI_WECHAT = "secapi/pay/refund";

    public static final String PAY_TYPE_JS = "JSAPI";
    public static final String PAY_TYPE_H5 = "MWEB";

    // --------------------------------------------------------------------------------------------------------
    // 微信部分
    // --------------------------------------------------------------------------------------------------------

    /** 获取accessToken的url */
    public static final String WECHAT_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={}&secret={}";
    /** 开放平台获取accessToken的url */
    public static final String WECHAT_OAUTH2_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={}&secret={}&code={}&grant_type=authorization_code";
    /** 开放平台获取用户的url */
    public static final String WECHAT_SNS_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token={}&openid={}&lang={}";

    /** 公众号js-sdk获取ticket的url */
    public static final String WECHAT_PUBLIC_JS_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={}&type=jsapi";
    /** 公众号发送模板消息的url */
    public static final String WECHAT_PUBLIC_SEND_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={}";
    /** 公众号获取自定义菜单配置的url */
    public static final String WECHAT_PUBLIC_MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token={}";
    /** 公众号创建自定义菜单的url */
    public static final String WECHAT_PUBLIC_MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token={}";
    /** 公众号删除自定义菜单的url */
    public static final String WECHAT_PUBLIC_MENU_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token={}";
    /** 企业号上传其他类型永久素材的url */
//    public static final String WECHAT_PUBLIC_QYAPI_ADD_MATERIAL_URL = "https://qyapi.weixin.qq.com/cgi-bin/material/add_material?type={}&access_token={}";
    public static final String WECHAT_PUBLIC_QYAPI_ADD_MATERIAL_URL = "https://api.weixin.qq.com/cgi-bin/material/add_material?type={}&access_token={}";
    /** 公众号获取模板列表（自己的） */
    public static final String WECHAT_PUBLIC_GET_ALL_PRIVATE_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token={}";
    /** 公众号删除模板（自己的） */
    public static final String WECHAT_PUBLIC_DEL_PRIVATE_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/template/del_private_template?access_token={}";
    /** 公众号添加模板（自己的） */
    public static final String WECHAT_PUBLIC_API_ADD_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token={}";


    /** 小程序登录凭证校验的url */
    public static final String WECHAT_MINI_SNS_AUTH_CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid={}&secret={}&js_code={}&grant_type=authorization_code";
    /** 小程序生成小程序码的url */
    public static final String WECHAT_MINI_QRCODE_UNLIMITED_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token={}";
    /** 小程序发送订阅消息的url */
    public static final String WECHAT_MINI_SEND_SUBSCRIBE_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={}";
    /** 小程序获取订阅列表（自己的） */
    public static final String WECHAT_MINI_GET_ALL_PRIVATE_TEMPLATE_URL = "https://api.weixin.qq.com/wxaapi/newtmpl/gettemplate?access_token={}";
    /** 小程序删除模板（自己的） */
    public static final String WECHAT_MINI_DEL_PRIVATE_TEMPLATE_URL = "https://api.weixin.qq.com/wxaapi/newtmpl/deltemplate?access_token={}";
    /** 小程序获取订阅模板（小程序的） */
    public static final String WECHAT_MINI_GET_TEMPLATE_URL = "https://api.weixin.qq.com/wxaapi/newtmpl/getpubtemplatekeywords?access_token={}&tid={}";
    /** 公众号添加模板（自己的） */
    public static final String WECHAT_MINI_API_ADD_TEMPLATE_URL = "https://api.weixin.qq.com/wxaapi/newtmpl/addtemplate?access_token={}";

    /** 微信小程序发货信息录入接口URL */
    public static final String WECHAT_MINI_UPLOAD_SHIPPING_URL = "https://api.weixin.qq.com/wxa/sec/order/upload_shipping_info?access_token={}";
    /** 微信小程序获取运力id列表接口URL */
    public static final String WECHAT_MINI_GET_DELIVERY_LIST_URL = "https://api.weixin.qq.com/cgi-bin/express/delivery/open_msg/get_delivery_list?access_token={}";

    /** 小程序accessToken redis key */
    public static final String REDIS_WECAHT_MINI_ACCESS_TOKEN_KEY = "wechat_mini_accessToken";
    /** 公众号accessToken redis key */
    public static final String REDIS_WECAHT_PUBLIC_ACCESS_TOKEN_KEY = "wechat_public_accessToken";
    /** 公众号JsApiTicket redis key */
    public static final String REDIS_PUBLIC_JS_API_TICKET = "wechat_js_api_ticket";
    public static final Long REDIS_PUBLIC_JS_API_TICKET_EXPRESS = 7100L;

    /** 小程序生成加密URL Link */
    public static final String WECHAT_MINI_GENERATE_URL_LINK = "https://api.weixin.qq.com/wxa/generate_urllink?access_token={}";


    /**
     * --------------------------------------------------------------------------------------------------------
     * 以下为视频号相关部分
     * --------------------------------------------------------------------------------------------------------
     */

    /*------------------------------------------ 申请接入接口 START ---------------------------------------*/
    public static final String WECHAT_API_BASE_DOME = "https://api.weixin.qq.com";

    /* 申请接入申请 */
    public static final String WECHAT_SHOP_REGISTER_APPLY = WECHAT_API_BASE_DOME.concat("/shop/register/apply?access_token={}");
    /* 获取接入状态 */
    public static final String WECHAT_SHOP_REGISTER_CHECK = WECHAT_API_BASE_DOME.concat("/shop/register/check?access_token={}");
    /* 完成接入任务 */
    public static final String WECHAT_SHOP_REGISTER_FINISH_ACCESS = WECHAT_API_BASE_DOME.concat("/shop/register/finish_access_info?access_token={}");
    /* 场景接入申请 在小程序上线之后 并且所有的api调用都通过了 才可以使用 调用申请成功之后就可以在视频号直播时选择线上商品了 */
    public static final String WECHAT_SHOP_REGISTER_APPLY_SCENE = WECHAT_API_BASE_DOME.concat("/shop/register/apply_scene?access_token={}");

    /*------------------------------------------ 申请接入接口 END ---------------------------------------*/


    /*------------------------------------------ 接入商品前必须接口 START ---------------------------------------*/
    /** 获取商品类目 */
    public static final String WECHAT_SHOP_CAT_GET_URL = WECHAT_API_BASE_DOME.concat("/shop/cat/get?access_token={}");
    /** 上传图片 **/
    public static final String WECHAT_SHOP_IMG_UPLOAD = WECHAT_API_BASE_DOME.concat("/shop/img/upload?&access_token={}");
    /** 上传品牌信息 */
    public static final String WECHAT_SHOP_AUDIT_AUDIT_BRAND = WECHAT_API_BASE_DOME.concat("/shop/audit/audit_brand?&access_token={}");
    /** 上传类目资质 */
    public static final String WECHAT_SHOP_AUDIT_AUDIT_CATEGORY = WECHAT_API_BASE_DOME.concat("/shop/audit/audit_category?&access_token={}");
    /** 查询类目审核结果 */
    public static final String WECHAT_SHOP_AUDIT_RESULT= WECHAT_API_BASE_DOME.concat("/shop/audit/result?access_token={}");
    /** 获取小程序提交过的入驻资质信息 */
    public static final String WECHAT_SHOP_AUDIT_GET_MINIAPP_CERTIFICATE= WECHAT_API_BASE_DOME.concat("/shop/audit/get_miniapp_certificate?access_token={}");
    /*------------------------------------------ 接入商品前必须接口 END ---------------------------------------*/


    /*------------------------------------------ 商家入驻接口 START ---------------------------------------*/
    /** 获取类目列表 */
    public static final String WECHAT_SHOP_ACCOUNT_GET_CATEGORY_LIST = WECHAT_API_BASE_DOME.concat("/shop/account/get_category_list?access_token={}");
    /** 获取品牌列表 */
    public static final String WECHAT_SHOP_ACCOUNT_GET_BRAND_LIST = WECHAT_API_BASE_DOME.concat("/shop/account/get_brand_list?access_token={}");
    /** 更新商家信息 */
    public static final String WECHAT_SHOP_ACCOUNT_UPDATE_INFO = WECHAT_API_BASE_DOME.concat("/shop/account/update_info?access_token={}");
    /** 获取商家信息 */
    public static final String WECHAT_SHOP_ACCOUNT_GET_INFO = WECHAT_API_BASE_DOME.concat("/shop/account/get_info?access_token={}");
    /*------------------------------------------ 商家入驻接口 END ---------------------------------------*/

    /*------------------------------------------ SPU 接口 START ---------------------------------------*/
    /** 添加商品 */
    public static final String WECHAT_SHOP_SPU_ADD_URL = WECHAT_API_BASE_DOME.concat("/shop/spu/add?access_token={}");
    /** 删除商品 */
    public static final String WECHAT_SHOP_SPU_DEL_URL = WECHAT_API_BASE_DOME.concat("/shop/spu/del?access_token={}");
    /** 撤回商品审核 */
    public static final String WECHAT_SHOP_SPU_DEL_AUDIT_URL = WECHAT_API_BASE_DOME.concat("/shop/spu/del_audit?access_token={}");
    /** 获取商品 */
    public static final String WECHAT_SHOP_SPU_GET_URL = WECHAT_API_BASE_DOME.concat("/shop/spu/get?access_token={}");
    /** 获取商品列表 */
    public static final String WECHAT_SHOP_SPU_GET_LIST_URL = WECHAT_API_BASE_DOME.concat("/shop/spu/get_list?access_token={}");
    /** 更新商品 */
    public static final String WECHAT_SHOP_SPU_UPDATE_URL = WECHAT_API_BASE_DOME.concat("/shop/spu/update?access_token={}");
    /** 上架商品 */
    public static final String WECHAT_SHOP_SPU_LISTING_URL = WECHAT_API_BASE_DOME.concat("/shop/spu/listing?access_token={}");
    /** 下架商品 */
    public static final String WECHAT_SHOP_SPU_DELISTING_URL = WECHAT_API_BASE_DOME.concat("/shop/spu/delisting?access_token={}");

    /*------------------------------------------ SPU 接口 END ---------------------------------------*/

    /*------------------------------------------ 订单 接口 START ---------------------------------------*/
    /** 检查场景值是否在支付校验范围内 */
    public static final String WECHAT_SHOP_SCENE_CHECK_URL = WECHAT_API_BASE_DOME.concat("/shop/scene/check?access_token={}");
    /** 生成订单并获取ticket */
    public static final String WECHAT_SHOP_ORDER_ADD_URL = WECHAT_API_BASE_DOME.concat("/shop/order/add?access_token={}");
    /** 视频号忽略此接口 同步订单支付结果  */
    public static final String WECHAT_SHOP_ORDER_PAY_URL = WECHAT_API_BASE_DOME.concat("/shop/order/pay?access_token={}");
    /** 生成支付参数   */
    public static final String WECHAT_SHOP_ORDER_PAYMENT_PARAMS_URL = WECHAT_API_BASE_DOME.concat("/shop/order/getpaymentparams?access_token={}");
    /** 获取订单 */
    public static final String WECHAT_SHOP_ORDER_GET_URL = WECHAT_API_BASE_DOME.concat("/shop/order/get?access_token={}");
    /** 按推广员获取订单 */
    public static final String WECHAT_SHOP_ORDER_GETBYFINDER_URL = WECHAT_API_BASE_DOME.concat("/shop/order/get_list_by_finder?access_token={}");
    /** 按分享员获取订单 */
    public static final String WECHAT_SHOP_ORDER_GETBYSHARER_URL = WECHAT_API_BASE_DOME.concat("/shop/order/get_list_by_sharer?access_token={}");
    /** 获取订单列表 */
    public static final String WECHAT_SHOP_ORDER_GET_LIST_URL = WECHAT_API_BASE_DOME.concat("/shop/order/get_list?access_token={}");
    /*------------------------------------------ 订单 接口 END ---------------------------------------*/

    /*------------------------------------------ 物流 接口 START ---------------------------------------*/
    /** 获取快递公司列表 */
    public static final String WECHAT_SHOP_DELIVERY_GET_COMPANY_LIST_URL = WECHAT_API_BASE_DOME.concat("/shop/delivery/get_company_list?access_token={}");
    /** 订单发货 */
    public static final String WECHAT_SHOP_DELIVERY_SEND_URL = WECHAT_API_BASE_DOME.concat("/shop/delivery/send?access_token={}");
    /** 订单确认收货 */
    public static final String WECHAT_SHOP_DELIVERY_RECIEVE_URL = WECHAT_API_BASE_DOME.concat("/shop/delivery/recieve?access_token={}");
    /*------------------------------------------ 物流 接口 END ---------------------------------------*/

    /*------------------------------------------ 售后 接口 START ---------------------------------------*/
    /** 创建售后 */
    public static final String WECHAT_SHOP_AFTERSALE_ADD_URL = WECHAT_API_BASE_DOME.concat("/shop/aftersale/add?access_token={}");
    /** 获取售后 */
    public static final String WECHAT_SHOP_AFTERSALE_GET_URL = WECHAT_API_BASE_DOME.concat("/shop/aftersale/get?access_token={}");
    /** 更新售后 */
    public static final String WECHAT_SHOP_AFTERSALE_UPDATE_URL = WECHAT_API_BASE_DOME.concat("/shop/aftersale/update?access_token={}");
    /*------------------------------------------ 售后 接口 END ---------------------------------------*/

    /*------------------------------------------ 优惠券 接口 START 二期 ---------------------------------------*/
    /** 商家确认回调领券事件 */
    public static final String WECHAT_SHOP_COUPON_CONFIRM_URL = WECHAT_API_BASE_DOME.concat("/shop/coupon/confirm?access_token={}");
    /** 添加优惠券 */
    public static final String WECHAT_SHOP_COUPON_ADD_URL = WECHAT_API_BASE_DOME.concat("/shop/coupon/add?access_token={}");
    /** 获取优惠券信息 */
    public static final String WECHAT_SHOP_COUPON_GET_URL = WECHAT_API_BASE_DOME.concat("/shop/coupon/get?access_token={}");
    /** 获取优惠券列表 */
    public static final String WECHAT_SHOP_COUPON_GET_LIST_URL = WECHAT_API_BASE_DOME.concat("/shop/coupon/get_list?access_token={}");
    /** 更新优惠券信息 */
    public static final String WECHAT_SHOP_COUPON_UPDATE_URL = WECHAT_API_BASE_DOME.concat("/shop/coupon/update?access_token={}");
    /** 更新优惠券状态 */
    public static final String WECHAT_SHOP_COUPON_UPDATE_STATUS_URL = WECHAT_API_BASE_DOME.concat("/shop/coupon/update_status?access_token={}");
    /** 更新优惠券库存 */
    public static final String WECHAT_SHOP_COUPON_UPDATE_STOCK_URL = WECHAT_API_BASE_DOME.concat("/shop/coupon/update_coupon_stock?access_token={}");
    /** 添加用户优惠券 */
    public static final String WECHAT_SHOP_COUPON_ADD_USER_COUPON_URL = WECHAT_API_BASE_DOME.concat("/shop/coupon/add_user_coupon?access_token={}");
    /** 获取用户优惠券列表 */
    public static final String WECHAT_SHOP_COUPON_GET_USERCOUPON_LIST_URL = WECHAT_API_BASE_DOME.concat("/shop/coupon/get_usercoupon_list?access_token={}");
    /** 更新用户优惠券 */
    public static final String WECHAT_SHOP_COUPON_UPDATE_USER_COUPON_URL = WECHAT_API_BASE_DOME.concat("/shop/coupon/update_user_coupon?access_token={}");
    /** 更新用户优惠券状态 */
    public static final String WECHAT_SHOP_COUPON_UPDATE_USERCOUPON_STATUS_URL = WECHAT_API_BASE_DOME.concat("/shop/coupon/update_usercoupon_status?access_token={}");

    /*------------------------------------------ 优惠券 接口 END 二期 ---------------------------------------*/

    /*------------------------------------------ 推广员 接口 START 二期 ---------------------------------------*/
    /** 获取推广员列表 */
    public static final String WECHAT_SHOP_COUPON_PROMOTER_LIST_URL = WECHAT_API_BASE_DOME.concat("/shop/promoter/list?access_token={}");
    /*------------------------------------------ 推广员 接口 END 二期 ---------------------------------------*/

    /*------------------------------------------ 分销员 接口 START 二期 ---------------------------------------*/
    /** 绑定分享员 */
    public static final String WECHAT_SHOP_SHARER_BIND_URL = WECHAT_API_BASE_DOME.concat("/shop/sharer/bind?access_token={}");
    /** 解绑分享员 */
    public static final String WECHAT_SHOP_SHARER_UNBIND_URL = WECHAT_API_BASE_DOME.concat("/shop/sharer/unbind?access_token={}");
    /** 获取已经绑定的分享员列表*/
    public static final String WECHAT_SHOP_SHARER_GET_SHARER_LIST_URL = WECHAT_API_BASE_DOME.concat("/shop/sharer/get_sharer_list?access_token={}");
    /** 获取分分享员 */
    public static final String WECHAT_SHOP_SHARER_SEARCH_SHARER_URL = WECHAT_API_BASE_DOME.concat("/shop/sharer/search_sharer?access_token={}");
    /** 获取分享员的中带货数据 */
    public static final String WECHAT_SHOP_SHARER_GET_SHARER_DATA_SUMMARY_URL = WECHAT_API_BASE_DOME.concat("/shop/sharer/get_sharer_data_summary?access_token={}");
    /** 获取分享员的直播间订单汇总 */
    public static final String WECHAT_SHOP_SHARER_GET_SHARER_LIVE_ORDER_LIST_URL = WECHAT_API_BASE_DOME.concat("/shop/sharer/get_sharer_live_order_list?access_token={}");
    /** 获取分享员的直播间带货数据汇总 */
    public static final String WECHAT_SHOP_SHARER_GET_SHARER_LIVE_SUMMARY_LIST_URL = WECHAT_API_BASE_DOME.concat("/shop/sharer/get_sharer_live_summary_list?access_token={}");

    /*------------------------------------------ 分销员 接口 END 二期 ---------------------------------------*/

    /** 自定义组件，商品类型 redis key */
    public static final String REDIS_WECHAT_SHOP_CAT_KEY = "wechat_shop_cat";

    /** 微信小程序回调，商品审核回调事件 */
    public static final String WECAHT_CALLBACK_EVENT_SPU_AUDIT = "open_product_spu_audit";
    /** 微信小程序回调，品牌审核回调事件 */
    public static final String WECAHT_CALLBACK_EVENT_BRAND_AUDIT = "open_product_brand_audit";


    /** 账户接入回调 */
    public static final String WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_ACCOUNT_REGISTER = "open_product_account_register";

    /** 场景审核回调 */
    public static final String WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_SCENE_GROUP_AUDIT = "![CDATA[open_product_scene_group_audit]]";

    /** 订单支付成功回调 */
    public static final String WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_ORDER_PAY = "open_product_order_pay";

    /** 商品系统下架回调 */
    public static final String WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_SPU_STATUS_UPDATE = "open_product_spu_status_update";
    /** 类目审核回调 */
    public static final String WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_CATEGORY_AUDIT = "open_product_category_audit";
    /** 分享员绑定解绑通知 */
    public static final String WECHAT_CALLBACK_EVENT_MINIPROGRAM_SHARER_BIND_STATUS_CHANGE = "miniprogram_sharer_bind_status_change";
    /** 用户领券回调 */
    public static final String WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_RECEIVE_COUPON = "open_product_receive_coupon";

    /** 素材上传图片最大值 */
    public static final Long OPEN_MEDIA_UPLOAD_IMAGE_MAX_SIZE = 1024 * 1024 * 10L;
    /** 素材上传图片后缀类型 */
    public static final String OPEN_MEDIA_UPLOAD_IMAGE_SUFFIX_NAME = "bmp,png,jpeg,jpg,gif";
    /** 素材上传语音最大值 */
    public static final Long OPEN_MEDIA_UPLOAD_VOICE_MAX_SIZE = 1024 * 1024 * 2L;
    /** 素材上传语音后缀类型 */
    public static final String OPEN_MEDIA_UPLOAD_VOICE_SUFFIX_NAME = "mp3,wma,wav,amr";


    /*-------------------------------------------------------- 微信素材 -----------------------------------------------*/
    /** 新增微信素材 返回URL */
    public static final String WECHAT_MEDIA_UPLOADIMG = WECHAT_API_BASE_DOME.concat("/cgi-bin/media/uploadimg?access_token={}");
    /** 新增微信永久素材 返回MediaId */
    public static final String WECHAT_MEDIA_UPLOADIMG_HASMEDIAID = WECHAT_API_BASE_DOME.concat("/cgi-bin/material/add_material?access_token={}");
    /** 永久素材获取 */
    public static final String WECHAT_MEDIA_GET = WECHAT_API_BASE_DOME.concat("/cgi-bin/material/get_material?access_token={}");

    /** 微信小程序发货管理开关 */
    public static final String CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH = "wechat_routine_shipping_switch";

}
