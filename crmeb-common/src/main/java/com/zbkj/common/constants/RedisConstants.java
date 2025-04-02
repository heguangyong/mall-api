package com.zbkj.common.constants;

/**
 * Redis常量类
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
public class RedisConstants {

    /** 用户注册信息缓存Key */
    public static final String USER_REGISTER_KEY = "USER:REGISTER:";

    /** 用户登token redis存储前缀 */
    public static final String REDIS_TOKEN_USER_NORMAL_KEY = "TOKEN:USER:NORMAL:";
    public static final String REDIS_TOKEN_USER_SHOP_MANAGER_KEY = "TOKEN:USER:SHOPMANAGER:";
    /** 用户登token WEB存储前缀 */
    public static final String WEB_TOKEN_USER_NORMAL_KEY = "user:normal:";
    public static final String WEB_TOKEN_USER_SHOP_MANAGER_KEY = "user:shopmanager:";

    public static final String FRONT_USER_TOKEN_SET_KEY = "FRONT:USER:TOKEN:{}";
    public static final String FRONT_MERCHANT_USER_TOKEN_SET_KEY = "FRONT:MERCHANT:USER:TOKEN:{}";

    /** 商品浏览量（每日） */
    public static final String PRO_PAGE_VIEW_KEY = "statistics:product:page_view:";
    public static final String PRO_PRO_PAGE_VIEW_KEY = "statistics:product:pro_page_view:{}:{}";

    /** 商品加购量（每日） */
    public static final String PRO_ADD_CART_KEY = "statistics:product:add_cart:";
    public static final String PRO_PRO_ADD_CART_KEY = "statistics:product:pro_add_cart:{}:{}";

    /** 商户访客量 */
    public static final String MERCHANT_VISITORS_KEY = "statistics:merchant:visitors:{}:{}";

    /** 移动端Token过期时间 3小时 */
    public static final long TOKEN_EXPRESS_MINUTES = (60 * 24);

    /** 验证码redis key前缀 */
    public static final String VALIDATE_REDIS_KEY_PREFIX = "validate_code_";

    /** 预下单Key */
    public static final String USER_READY_ORDER_KEY = "user_order:";

    /** 系统菜单缓存Key */
    public static final String MENU_CACHE_LIST_KEY = "menuList";
    /** 平台端系统菜单缓存Key */
    public static final String PLATFORM_MENU_CACHE_LIST_KEY = "platformMenuList";
    /** 商户端系统菜单缓存Key */
    public static final String MERCHANT_MENU_CACHE_LIST_KEY = "merchantMenuList";

    /** 商品分类缓存Key */
    public static final String PRODUCT_CATEGORY_CACHE_LIST_KEY = "productCategoryList";
    /** 商户端商品分类缓存Key */
    public static final String PRODUCT_CATEGORY_CACHE_MERCHANT_LIST_KEY = "productCategoryMerchantList";

    /** 商品全部品牌缓存列表Key */
    public static final String PRODUCT_ALL_BRAND_LIST_KEY = "productBrandAllList";

    /** 商户商品分类缓存Key */
    public static final String STORE_PRODUCT_CATEGORY_CACHE_LIST_KEY = "store:product:category:list:{}";

    /** 物流公司缓存Key */
    public static final String EXPRESS_CACHE_LIST_KEY = "expressList";

    /** 城市数据 redis key */
    public static final String CITY_LIST = "city_list";
    /** 城市tree redis key */
    public static final String CITY_LIST_TREE = "city_list_tree";
    /** 城市区域tree redis key */
    public static final String CITY_REGION_LIST_TREE = "city_region_list_tree";

    /** 快递信息缓存 */
    public static final String LOGISTICS_KEY = "logistics_";

    /** 微信公众号模板消息列表 Key */
    public static final String WE_CHAT_MESSAGE_KEY_PUBLIC = "we_chat_public_message_list";

    /** 微信小程序订阅通知列表 Key */
    public static final String WE_CHAT_MESSAGE_KEY_PROGRAM = "we_chat_program_message_list";

    /** 秒杀商品库存缓存key **/
    public static final String SECKILL_PRODUCT_INFO_KEY = "seckill_product_info:{}";

    /** 秒杀商品库存缓存key **/
    public static final String SECKILL_PRODUCT_QUOTA_KEY = "seckill_product_quota:{}";

    /** 秒杀商品规格SKU库存缓存key **/
    public static final String SECKILL_PRODUCT_SKU_QUOTA_KEY = "seckill_product_sku_quota:{}";

    /** 微信小程序运力缓存Key **/
    public static final String WECHAT_MINI_DELIVERY_KEY = "wechat_delivery";

    /** 管理后台账号登录错误数量keu **/
    public static final String ADMIN_ACCOUNT_LOGIN_ERROR_NUM_KEY = "admin:account:login:error:{}";

    /** 拼团业务用到的创建订单前存储，支付后判断记录的 key **/
    public static final String PINTUAN_ORDER_RECORD_KEY = "pintuan_record:{}";

    /** 订单过期时间Key */
    public static final String ORDER_EXPIRE_TIME = "ORDER-EXPIRATION-TIME:{}";
}
