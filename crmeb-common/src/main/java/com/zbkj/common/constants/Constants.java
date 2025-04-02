package com.zbkj.common.constants;

/**
 *  配置类
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
public class Constants {

    public static final long TOKEN_EXPRESS_MINUTES = (60 * 24); //3小时

    public static final int NUM_ZERO = 0;
    public static final int NUM_ONE = 1;
    public static final int NUM_TWO = 2;
    public static final int NUM_THREE = 3;
    public static final int NUM_FIVE = 5;
    public static final int NUM_SEVEN = 7;
    public static final int NUM_TEN = 10;
    public static final int NUM_ONE_HUNDRED = 100;

    //头部 token令牌key
    public static final String HEADER_AUTHORIZATION_KEY = "Authori-zation";

    //默认分页
    public static final int DEFAULT_PAGE = 1;
    //默认分页
    public static final int DEFAULT_LIMIT = 20;

    //升序排序
    public static final String SORT_ASC = "asc";
    //降序排序
    public static final String SORT_DESC = "desc";

    // app 版本号
    public static final String CONFIG_APP_VERSION = "app_version";
    // android版本地址
    public static final String CONFIG_APP_ANDROID_ADDRESS = "android_address";
    // ios版本地址
    public static final String CONFIG_APP_IOS_ADDRESS = "ios_address";
    // 开放式升级
    public static final String CONFIG_APP_OPEN_UPGRADE = "open_upgrade";

    //微信消息模板 tempKey
    public static final String WE_CHAT_TEMP_KEY_FIRST = "first";
    public static final String WE_CHAT_TEMP_KEY_END = "remark";

    public static final String FAIL     = "FAIL";
    public static final String SUCCESS  = "SUCCESS";

    /** 公共开关：0关闭 */
    public static final String COMMON_SWITCH_CLOSE = "0";
    /** 公共开关：1开启 */
    public static final String COMMON_SWITCH_OPEN = "1";

    /** 小程序源码包文件名 */
    public static final String WECHAT_SOURCE_CODE_FILE_NAME = "/mp-weixin-target.zip";

    /** 操作类型-添加 */
    public static final String OPERATION_TYPE_ADD = "add";
    /** 操作类型-快捷添加 */
    public static final String OPERATION_TYPE_QUICK_ADD = "quick";
    /** 操作类型-扣减 */
    public static final String OPERATION_TYPE_SUBTRACT = "sub";
    /** 操作类型-删除 */
    public static final String OPERATION_TYPE_DELETE = "delete";
    /** 操作类型-活动创建 */
    public static final String OPERATION_TYPE_ACTIVITY_CREATE = "create";
    /** 操作类型-活动回归 */
    public static final String OPERATION_TYPE_ACTIVITY_ROLL_BACK = "back";

    /** 自定义表单开关：关闭 */
    public static final String CONFIG_FORM_SWITCH_CLOSE = "'0'";
    /** 自定义表单开关：开启 */
    public static final String CONFIG_FORM_SWITCH_OPEN = "'1'";

    /** 公共IS字段值：0（未删除、未开启） */
    public static final Integer COMMON_IS_FILED_ZERO = 0;
    /** 公共IS字段值：1（已删除、已开启） */
    public static final Integer COMMON_IS_FILED_ONE = 1;
}
