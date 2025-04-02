package com.zbkj.common.constants;

/**
 *  系统设置常量类
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
public class SysConfigConstants {

    /** 平台端-左上角菜单logo(登陆后) */
    public static final String CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP = "site_logo_lefttop";
    /** 平台端-左上角缩回菜单logo(登陆后) */
    public static final String CONFIG_KEY_ADMIN_SITE_LOGO_SQUARE = "site_logo_square";
    /** 平台端-登录页LOGO */
    public static final String CONFIG_KEY_ADMIN_LOGIN_LOGO_LOGIN = "site_logo_login";
    /** 平台端-登录页背景图 */
    public static final String CONFIG_KEY_ADMIN_LOGIN_BACKGROUND_IMAGE = "admin_login_bg_pic";
    /** 平台端-登录页左侧logo */
    public static final String CONFIG_KEY_ADMIN_LOGIN_LEFT_LOGO = "admin_login_left_logo";

    /** 商户端端-登录页背景图 */
    public static final String CONFIG_KEY_MERCHANT_LOGIN_BACKGROUND_IMAGE = "merchant_login_bg_pic";
    /** 商户端-登录页LOGO */
    public static final String CONFIG_KEY_MERCHANT_LOGIN_LOGO_LOGIN = "merchant_site_logo_login";
    /** 商户端-登录页左侧LOGO */
    public static final String CONFIG_KEY_MERCHANT_LOGIN_LEFT_LOGO = "mer_admin_login_left_logo";
    /** 商户端-站点地址 */
    public static final String CONFIG_KEY_MERCHANT_SITE_URL = "mer_site_url";
    /** 商户端-左上角菜单logo(登陆后) */
    public static final String CONFIG_KEY_MERCHANT_LOGIN_LOGO_LEFT_TOP = "mer_site_logo_lefttop";
    /** 商户端-左上角缩回菜单logo(登陆后) */
    public static final String CONFIG_KEY_MERCHANT_SITE_LOGO_SQUARE = "mer_site_logo_square";

    /** 微信分享图片（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_IMAGE = "wechat_share_img";
    /** 微信分享标题（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_TITLE = "wechat_share_title";
    /** 微信分享简介（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_SYNOPSIS = "wechat_share_synopsis";


    /** 是否启用分销:1-启用，0-禁止 */
    public static final String RETAIL_STORE_SWITCH = "retail_store_switch";
    /** 分销额度：-1-指定分销，0--用户购买金额大于等于设置金额时，用户自动成为分销员 */
    public static final String RETAIL_STORE_LINE = "retail_store_line";
    /** 分销关系绑定:0-所有用户，1-新用户 */
    public static final String RETAIL_STORE_BINDING_TYPE = "retail_store_binding_type";
    /** 是否开启分销气泡 ：0-展示，1-展示*/
    public static final String RETAIL_STORE_BUBBLE_SWITCH = "retail_store_bubble_switch";
    /** 分销一级返佣比例 */
    public static final String RETAIL_STORE_BROKERAGE_FIRST_RATIO = "retail_store_brokerage_first_ratio";
    /** 分销二级返佣比例 */
    public static final String RETAIL_STORE_BROKERAGE_SECOND_RATIO = "retail_store_brokerage_second_ratio";
    /** 分销佣金冻结时间(天) */
    public static final String RETAIL_STORE_BROKERAGE_FREEZING_TIME = "retail_store_brokerage_freezing_time";
    /** 分销佣金分账节点:pay:订单支付后，receipt:订单收货后，complete:订单完成后 */
    public static final String RETAIL_STORE_BROKERAGE_SHARE_NODE = "retail_store_brokerage_share_node";
    /** 分销提现最低金额 */
    public static final String RETAIL_STORE_EXTRACT_MIN_PRICE = "retail_store_extract_min_price";
    /** 分销提现银行 */
    public static final String RETAIL_STORE_EXTRACT_BANK = "retail_store_extract_bank";


    /** 是否开启充值功能 */
    public static final String CONFIG_KEY_RECHARGE_SWITCH = "recharge_switch";
    /** 腾讯地图key todo 需要前端判断是否删除 */
    public static final String CONFIG_SITE_TENG_XUN_MAP_KEY = "tengxun_map_key";

    /** 微信支付开关 */
    public static final String CONFIG_PAY_WECHAT_OPEN = "pay_weixin_open";
    /** 余额支付状态 */
    public static final String CONFIG_YUE_PAY_STATUS  = "yue_pay_status";
    /** 支付宝支付状态 */
    public static final String CONFIG_ALI_PAY_STATUS = "ali_pay_status";

    /** 网站名称 */
    public static final String CONFIG_KEY_SITE_NAME = "site_name";

    /** 用户默认头像 */
    public static final String USER_DEFAULT_AVATAR_CONFIG_KEY = "h5_avatar";
    /** 系统颜色配置 */
    public static final String CONFIG_CHANGE_COLOR_CONFIG = "change_color_config";

    /** 积分抵扣开关 */
    public static final String CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH = "integral_deduction_switch";
    /** 积分抵扣开启金额(单位元),订单满多少钱可以使用积分抵扣 */
    public static final String CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY = "integral_deduction_start_money";
    /** 积分抵扣金额(1积分抵多少金额，0.01) */
    public static final String CONFIG_KEY_INTEGRAL_DEDUCTION_MONEY = "integral_deduction_money";
    /** 积分抵扣比例(订单中积分可抵扣商品金额比例（0~100）%) */
    public static final String CONFIG_KEY_INTEGRAL_DEDUCTION_RATIO = "integral_deduction_ratio";
    /** 下单支付金额按比例赠送积分（实际支付多少元赠送1积分) */
    public static final String CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE = "order_give_integral";
    /** 积分冻结时间 */
    public static final String CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME = "freeze_integral_day";
    /** 积分冻结时间 */
    public static final String CONFIG_KEY_STORE_INTEGRAL_FREEZE_NODE = "integral_freeze_node";

    /** 移动端登录 logo */
    public static final String CONFIG_KEY_MOBILE_LOGIN_LOGO = "mobile_login_logo";
    /** 是否 开启微信公众号授权等你 */
    public static final String CONFIG_WECHAT_BROWSER_VISIT = "wechat_browser_visit";

    /** 图片上传类型 1本地 2七牛云 3OSS 4COS 5京东, 默认本地 */
    public static final String CONFIG_UPLOAD_TYPE = "uploadType";
    /** 文件上传是否保存本地 */
    public static final String CONFIG_FILE_IS_SAVE = "file_is_save";
    /** 全局本地图片域名 */
    public static final String CONFIG_LOCAL_UPLOAD_URL = "localUploadUrl";
    /** 图片上传,拓展名 */
    public static final String UPLOAD_IMAGE_EXT_STR_CONFIG_KEY = "image_ext_str";
    /** 图片上传,最大尺寸 */
    public static final String UPLOAD_IMAGE_MAX_SIZE_CONFIG_KEY = "image_max_size";
    /** 文件上传,拓展名 */
    public static final String UPLOAD_FILE_EXT_STR_CONFIG_KEY = "file_ext_str";
    /** 文件上传,最大尺寸 */
    public static final String UPLOAD_FILE_MAX_SIZE_CONFIG_KEY = "file_max_size";

    /** 七牛云上传URL */
    public static final String CONFIG_QN_UPLOAD_URL = "qnUploadUrl";
    /** 七牛云Access Key */
    public static final String CONFIG_QN_ACCESS_KEY = "qnAccessKey";
    /** 七牛云Secret Key */
    public static final String CONFIG_QN_SECRET_KEY = "qnSecretKey";
    /** 七牛云存储名称 */
    public static final String CONFIG_QN_STORAGE_NAME = "qnStorageName";
    /** 七牛云存储区域 */
    public static final String CONFIG_QN_STORAGE_REGION = "qnStorageRegion";

    /** 阿里云上传URL */
    public static final String CONFIG_AL_UPLOAD_URL = "alUploadUrl";
    /** 阿里云Access Key */
    public static final String CONFIG_AL_ACCESS_KEY = "alAccessKey";
    /** 阿里云Secret Key */
    public static final String CONFIG_AL_SECRET_KEY = "alSecretKey";
    /** 阿里云存储名称 */
    public static final String CONFIG_AL_STORAGE_NAME = "alStorageName";
    /** 阿里云存储区域 */
    public static final String CONFIG_AL_STORAGE_REGION = "alStorageRegion";

    /** 腾讯云上传URL */
    public static final String CONFIG_TX_UPLOAD_URL = "txUploadUrl";
    /** 腾讯云Access Key */
    public static final String CONFIG_TX_ACCESS_KEY = "txAccessKey";
    /** 腾讯云Secret Key */
    public static final String CONFIG_TX_SECRET_KEY = "txSecretKey";
    /** 腾讯云存储名称 */
    public static final String CONFIG_TX_STORAGE_NAME = "txStorageName";
    /** 腾讯云存储区域 */
    public static final String CONFIG_TX_STORAGE_REGION = "txStorageRegion";

    /** 京东云上传URL */
    public static final String CONFIG_JD_UPLOAD_URL = "jdUploadUrl";
    /** 京东云Access Key */
    public static final String CONFIG_JD_ACCESS_KEY = "jdAccessKey";
    /** 京东云Secret Key */
    public static final String CONFIG_JD_SECRET_KEY = "jdSecretKey";
    /** 京东云存储桶名称 */
    public static final String CONFIG_JD_BUCKET_NAME = "jdBucketName";
    /** 京东云存储区域 */
    public static final String CONFIG_JD_CLOUD_SIGNING_REGION = "jdSigningRegion";
    /** 京东云存储端点 */
    public static final String CONFIG_JD_CLOUD_ENDPOINT = "jdEndpoint";


    /** 客服H5链接 */
    public static final String CONFIG_CONSUMER_H5_URL = "consumer_h5_url";
    /** 客服电话 */
    public static final String CONFIG_CONSUMER_HOTLINE = "consumer_hotline";
    /** 店铺街开关 */
    public static final String CONFIG_KEY_SHOP_STREET_SWITCH = "shop_street_switch";
    /** 客服类型 */
    public static final String CONFIG_CONSUMER_TYPE = "consumer_type";
    /** 客户类型-H5 */
    public static final String CONSUMER_TYPE_H5 = "h5";
    /** 客户类型-热线 */
    public static final String CONSUMER_TYPE_HOTLINE = "hotline";


    /** 商品导入平台地址-淘宝 */
    public static final String CONFIG_IMPORT_PRODUCT_TB = "importProductTB";
    /** 商品导入平台地址-京东 */
    public static final String CONFIG_IMPORT_PRODUCT_JD = "importProductJD";
    /** 商品导入平台地址-苏宁 */
    public static final String CONFIG_IMPORT_PRODUCT_SN = "importProductSN";
    /** 商品导入平台地址-拼多多 */
    public static final String CONFIG_IMPORT_PRODUCT_PDD = "importProductPDD";
    /** 商品导入平台地址-天猫 */
    public static final String CONFIG_IMPORT_PRODUCT_TM = "importProductTM";
    /** 商品导入99Api Key */
    public static final String CONFIG_COPY_PRODUCT_APIKEY = "copy_product_apikey";

    /** 物流查询类型:1-一号通，2-阿里云查询 */
    public static final String LOGISTICS_QUERY_TYPE = "logistics_type";
    /** 阿里云查询快递密钥 */
    public static final String LOGISTICS_QUERY_ALIYUN_CODE = "system_express_app_code";
    /** 阿里云查询快递URL  https://market.aliyun.com/products/56928004/cmapi021863.html#sku=yuncode15863000015 */
    public static final String LOGISTICS_QUERY_ALIYUN_URL = "https://wuliu.market.alicloudapi.com/kdi?no={}";

    /** 移动端文章顶部的banner图最大数量 配置数据最小3最大10 */
    public static final String ARTICLE_BANNER_LIMIT = "news_slides_limit";

    /** 订单订单自动收货天数 */
    public static final String CONFIG_ORDER_AUTO_TAKE_DELIVERY_DAY = "auto_take_delivery_day";
    /** 订单收货后自动完成天数 */
    public static final String CONFIG_ORDER_AUTO_COMPLETE_DAY = "auto_complete_day";
    /** 退款理由 */
    public static final String CONFIG_KEY_STOR_REASON = "stor_reason";


    /** 商户保证金额 */
    public static final String MERCHANT_GUARANTEED_AMOUNT = "guaranteed_amount";
    /** 商户每笔最小转账额度 */
    public static final String MERCHANT_TRANSFER_MIN_AMOUNT = "transfer_min_amount";
    /** 商户每笔最高转账额度 */
    public static final String MERCHANT_TRANSFER_MAX_AMOUNT = "transfer_max_amount";
    /** 商户分账节点:pay:订单支付后，receipt:订单收货后，complete:订单完成后 */
    public static final String MERCHANT_SHARE_NODE = "merchant_share_node";
    /** 商户分账冻结时间（天） */
    public static final String MERCHANT_SHARE_FREEZE_TIME = "merchant_share_freeze_time";



    /** 系统配置列表 */
    public static final String CONFIG_LIST = "config_list";
    /** 移动端域名 */
    public static final String CONFIG_KEY_SITE_URL = "site_url";
    /** 后台api地址(回调地址) */
    public static final String CONFIG_KEY_API_URL = "api_url";
    /** 移动商城api接口地址 */
    public static final String CONFIG_KEY_FRONT_API_URL = "front_api_url";

    /** 充值注意事项 */
    public static final String CONFIG_RECHARGE_ATTENTION = "recharge_attention";

    /********************************************************************************************* 协议START ****************/
    /** 商户入驻协议 */
    public static final String MERCHANT_SETTLEMENT_AGREEMENT = "merSettlementAgreement";
    /** 用户隐私政策 */
    public static final String USER_PRIVACY_AGREEMENT = "userPrivacyAgreement";
    /** 用户注册协议 */
    public static final String USER_REGISTER_AGREEMENT = "merLoginAgreement";
    /** 用户注销声明 */
    public static final String USER_CANCEL_AGREEMENT = "userCancelAccountAgreement";
    /** 用户注销重要提示 */
    public static final String USER_CANCEL_NOTICE_AGREEMENT = "userCancelAccountNoticeAgreement";
    /** 关于我们 */
    public static final String ABOUTUS_AGREEMENT = "userAboutAgreement";
    /** 资质证明 */
    public static final String PLATFROM_INTELLIGENT_AGREEMENT = "platfromIntelligentAgreement";
    /** 平台规则 */
    public static final String PLATFROM_RULE_AGREEMENT = "platfromRuleAgreement";
    /** 优惠券规则 */
    public static final String COUPON_AGREEMENT = "couponAgreement";
    /** 付费会员协议 */
    public static final String PAID_MEMBER_AGREEMENT = "paidMemberAgreement";

    /********************************************************************************************* 协议END ****************/


    /** 一号通账号 */
    public static final String ONE_PASS_SMS_ACCOUNT = "sms_account";
    /** 一号通账号密码 */
    public static final String ONE_PASS_SMS_TOKEN = "sms_token";
    /** 电子面单快递公司id */
    public static final String CONFIG_EXPORT_ID = "config_export_id";
    /** 电子面单快递公司模板id */
    public static final String CONFIG_EXPORT_TEMP_ID = "config_export_temp_id";
    /** 电子面单快递公司编码 */
    public static final String CONFIG_EXPORT_COM = "config_export_com";
    /** 电子面单发货人姓名 */
    public static final String CONFIG_EXPORT_TO_NAME = "config_export_to_name";
    /** 电子面单发货人电话 */
    public static final String CONFIG_EXPORT_TO_TEL = "config_export_to_tel";
    /** 电子面单发货人详细地址 */
    public static final String CONFIG_EXPORT_TO_ADDRESS = "config_export_to_address";
    /** 电子面单打印机编号 */
    public static final String CONFIG_EXPORT_SIID = "config_export_siid";
    /** 电子面单开关 */
    public static final String CONFIG_EXPORT_OPEN = "config_export_open";

    /** 签到规则说明 */
    public static final String CONFIG_SIGN_RULE_DESCRIPTION = "sign_rule_description ";
    /** 用户最小充值金额 */
    public static final String USER_RECHARGE_MIN_AMOUNT = "store_user_min_recharge";


    /** 版权-授权标签 */
    public static final String CONFIG_COPYRIGHT_LABEL = "copyright_label";
    /** 版权-公司信息 */
    public static final String CONFIG_COPYRIGHT_COMPANY_INFO = "copyright_company_name";
    /** 版权-公司图片 */
    public static final String CONFIG_COPYRIGHT_COMPANY_IMAGE = "copyright_company_image";

    /** 版权-授权标签 */
    public static final String CONFIG_INSTALL_STATISTICS = "is_install";

    /** 底部导航—是否自定义 */
    public static final String CONFIG_BOTTOM_NAVIGATION_IS_CUSTOM = "bottom_navigation_is_custom";

    /** 新人礼—开关 */
    public static final String NEW_PEOPLE_PRESENT_SWITCH = "new_people_present_switch";
    /** 新人礼—优惠券 */
    public static final String NEW_PEOPLE_PRESENT_COUPON = "new_people_present_coupon";
    /** 生日有礼—开关 */
    public static final String BIRTHDAY_PRESENT_SWITCH = "birthday_present_switch";
    /** 生日有礼—优惠券 */
    public static final String BIRTHDAY_PRESENT_COUPON = "birthday_present_coupon";

    /** 公众号-浏览访问方式 */
    public static final String WECHAT_PUBLIC_LOGIN_TYPE = "wechat_browser_visit";
    /** 小程序—手机号授权验证类型 */
    public static final String WECHAT_ROUTINE_PHONE_VERIFICATION = "routine_phone_verification";

    /** PC商城—左上角logo */
    public static final String PC_SHOPPING_LEFT_TOP_LOGO = "pc_shopping_left_top_logo";
    /** PC商城—品牌好店广告图 */
    public static final String PC_SHOPPING_GOOD_STORE_IMAGE = "pc_shopping_good_store_image";
    /** PC商城—手机体验购买二维码类型 1:小程序 2:公众号/H5 */
    public static final String PC_SHOPPING_GO_PHONE_QR_CODE_TYPE = "pc_shopping_go_phone_qr_code_type";
    /** 商城授权信息—联系电话 */
    public static final String SHOPPING_AUTHORIZE_PHONE = "shopping_authorize_phone";
    /** 商城授权信息—地址 */
    public static final String SHOPPING_AUTHORIZE_ADDRESS = "shopping_authorize_address";
    /** 商城授权信息—授权信息 */
    public static final String SHOPPING_AUTHORIZE_INFO = "shopping_authorize_info";
    /** 商城授权信息—备案号 */
    public static final String SHOPPING_AUTHORIZE_FILING_NUM = "shopping_authorize_filing_num";
    /** PC商城—商户入驻开关（移动端也生效） */
    public static final String MERCHANT_APPLY_SWITCH = "merchant_apply_switch";

    /** 系统商品复制类型 */
    public static final String CONFIG_PRODUCT_COPY_TYPE = "system_product_copy_type";

    /** 付费会员-购卡入口 1-开启，0-关闭 */
    public static final String CONFIG_PAID_MEMBER_PAID_ENTRANCE = "paid_member_paid_entrance";
    /** 付费会员-会员价格展示 all-全部，paid-仅付费会员 */
    public static final String CONFIG_PAID_MEMBER_PRICE_DISPLAY = "paid_member_price_display";
    /** 付费会员-会员专享商品列表开关 1-开启，0-关闭 */
    public static final String CONFIG_PAID_MEMBER_PRODUCT_SWITCH = "paid_member_product_switch";

    /** 微信H5/PC支付来源 public - 公共号 mini - 小程序 */
    public static final String WECHAT_PAY_SOURCE_H5_PC = "wechat_pay_source";

    /** 开屏广告-开关 */
    public static final String SPLASH_AD_SWITCH = "splash_ad_switch";
    /** 开屏广告-广告时间 */
    public static final String SPLASH_AD_SHOW_TIME = "splash_ad_show_time";
    /** 开屏广告-展示间隔 */
    public static final String SPLASH_AD_SHOW_INTERVAL = "splash_ad_show_interval";
    /** 秒杀样式设置 */
    public static final String SECKILL_STYLE_CONFIG = "seckill_style_config";
}
