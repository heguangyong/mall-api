package com.zbkj.common.utils;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.RegularConstants;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.request.UserCommonSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 表单验证类
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
public class ValidateFormUtil {

    /**
     * 手机
     * @param value String 值
     * @param info String 字段名
     */
    public static void isPhone(String value, String info) {
        regularException(value, info, RegularConstants.PHONE_TWO, "手机");
    }

    /**
     * 验证必填
     * @param value String 值
     * @param info String 字段名

     * @since 2020-05-11
     */
    public static void isRequire(String value, String info){
        if(StringUtils.isBlank(value) ){
            throw new CrmebException("请填写/选择" + info);
        }
    }

    /**
     * 正则表达式验证
     * @param value String 值
     * @param info String 字段名
     * @param regular String 正则表达式

     * @since 2020-05-11
     */
    public static void regularException(String value, String info, String regular, String title){
        if(!regular(value, info, regular)){
            //正则验证
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, info + " 格式必须为 " + title);
        }
    }

    /**
     * 正则表达式验证
     * @param value String 值
     * @param info String 字段名
     * @param regular String 正则表达式

     * @since 2020-05-11
     */
    public static boolean regular(String value, String info, String regular){
        isRequire(value, info);
        Pattern pattern = Pattern.compile(regular);
        return pattern.matcher(value).matches();
    }

    /**
     * 校验是否是手机号，不是则抛出异常
     * @param phone 手机号
     */
    public static void isPhoneException(String phone) {
        boolean match = ReUtil.isMatch(RegularConstants.PHONE_TWO, phone);
        if (!match) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入正确的手机号");
        }
    }

    /**
     * 校验用户搜索公共搜索
     * @param searchType 搜索类型，all-全部，uid-UID,nickname-用户昵称,phone-手机号
     * @param content 搜索内容
     */
    public static void validatorUserCommonSearch(String searchType, String content) {
        if (StrUtil.isBlank(content)) return;
        if (UserConstants.USER_SEARCH_TYPE_UID.equals(searchType)) {
            if (!NumberUtil.isInteger(content)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "UID必须是正整数");
            }
        }
        if (UserConstants.USER_SEARCH_TYPE_PHONE.equals(searchType)) {
            isPhoneException(content);
        }
    }

    /**
     * 校验用户搜索公共搜索
     * @param request 用户搜索公共搜索对象
     */
    public static void validatorUserCommonSearch(UserCommonSearchRequest request) {
        if (StrUtil.isBlank(request.getContent())) return;
        if (UserConstants.USER_SEARCH_TYPE_UID.equals(request.getSearchType())) {
            if (!NumberUtil.isLong(request.getContent()) && Long.parseLong(request.getContent()) >= 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "UID必须是正整数");
            }
        }
        if (UserConstants.USER_SEARCH_TYPE_PHONE.equals(request.getSearchType())) {
            if (!NumberUtil.isLong(request.getContent()) && Long.parseLong(request.getContent()) >= 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号必须是正整数");
            }
        }
    }
}
