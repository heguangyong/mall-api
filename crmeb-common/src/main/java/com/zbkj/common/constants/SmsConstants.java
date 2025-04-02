package com.zbkj.common.constants;

/**
 * 短信常量类
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
public class SmsConstants {

    /** 接口异常错误码 */
    public static final Integer SMS_ERROR_CODE = 400;

    /** 手机验证码redis key */
    public static final String SMS_VALIDATE_PHONE = "sms:validate:code:";
    public static final String SMS_VALIDATE_PHONE_NUM = "sms:validate:phone:";
    /** 短信验证码redis key */
    public static final String SMS_VERIFICATION_CODE_PHONE = "sms:validate:{}:code:{}";
    /** 手机号发送短信验证码数量redis key */
    public static final String SMS_VERIFICATION_PHONE_NUM = "sms:validate:{}:phone:{}";
    public static final String SMS_PHONE_HOUR_NUM = "sms:phone:hour:num:{}:{}";
    public static final String SMS_PHONE_DAY_NUM = "sms:phone:day:num:{}:{}";
    public static final String SMS_IP_HOUR_NUM = "sms:ip:hour:num:{}:{}";

    /** 验证码模板ID */
    public static final Integer SMS_CONFIG_VERIFICATION_CODE_TEMP_ID = 435250;
//    public static final Integer SMS_CONFIG_VERIFICATION_CODE_TEMP_ID = 538393;

    /** 发送短信参数模板 */
    public static final String SMS_COMMON_PARAM_FORMAT = "param[{}]";

    /** 验证码过期时间 */
    public static final String CONFIG_KEY_SMS_CODE_EXPIRE = "sms_code_expire";

    /** 验证码场景-移动端登录 */
    public static final String VERIFICATION_CODE_SCENARIO_LOGIN = "login";
    /** 验证码场景-商户入驻 */
    public static final String VERIFICATION_CODE_SCENARIO_SETTLED = "settled";
    /** 验证码场景-用户换绑手机号获取原手机号验证码 */
    public static final String VERIFICATION_CODE_SCENARIO_USER_UPDATE_BINDING_CURRENT = "update:binding:current";
    /** 验证码场景-用户换绑手机号获取新手机号验证码 */
    public static final String VERIFICATION_CODE_SCENARIO_USER_UPDATE_BINDING_NEW = "update:binding:new";
    /** 验证码场景-用户修改密码 */
    public static final String VERIFICATION_CODE_SCENARIO_USER_UPDATE_PASSWORD = "update:password";

}
