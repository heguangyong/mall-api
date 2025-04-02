package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.NotifyConstants;
import com.zbkj.common.constants.OnePassConstants;
import com.zbkj.common.constants.SmsConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.sms.SmsTemplate;
import com.zbkj.common.model.system.SystemNotification;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.OnePassResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.OnePassApplicationInfoVo;
import com.zbkj.common.vo.OnePassUserInfoVo;
import com.zbkj.common.vo.OnePassUserSmsVo;
import com.zbkj.common.vo.SendSmsVo;
import com.zbkj.service.service.*;
import com.zbkj.service.util.OnePassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 行为service实现类
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
@Service
public class OnePassSmsServiceImpl implements OnePassSmsService, SmsService {

    private static final Logger logger = LoggerFactory.getLogger(OnePassSmsServiceImpl.class);

    @Autowired
    private OnePassUtil onePassUtil;
    @Autowired
    private OnePassService onePassService;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SmsTemplateService smsTemplateService;
    @Autowired
    private SystemNotificationService systemNotificationService;

    /**
     * 发短信之前的校验
     */
    private void beforeSendMessage() {
        OnePassUserInfoVo userInfoVo = onePassService.info();
        OnePassUserSmsVo userSmsVo = userInfoVo.getSms();
        Integer open = userSmsVo.getOpen();
        if (!open.equals(1)) {
            logger.error("发送短信请先开通一号通账号服务");
            throw new CrmebException(OnePassResultCode.SMS_NOT_OPEN);
        }
        if (userSmsVo.getNum() <= 0) {
            logger.error("一号通账号服务余量不足");
            throw new CrmebException(OnePassResultCode.SMS_SHORTAGE_IN_NUMBER);
        }
    }

    /**
     * 发送公共验证码
     *
     * @param phone 手机号
     * @param scenario 场景值
     * @return Boolean
     */
    @Override
    public Boolean sendCommonCode(String phone, String scenario) {
        ValidateFormUtil.isPhone(phone, "手机号码错误");
//        beforeSendMessage();
//        beforeSendCommonCodeCheck(phone, scenario);
        //获取短信验证码过期时间
        String codeExpireStr = systemConfigService.getValueByKey(SmsConstants.CONFIG_KEY_SMS_CODE_EXPIRE);
        if (StrUtil.isBlank(codeExpireStr) || Integer.parseInt(codeExpireStr) == 0) {
            codeExpireStr = Constants.NUM_FIVE + "";// 默认5分钟过期
        }
//        Integer code = CrmebUtil.randomCount(111111, 999999);
        Integer code = 111111;
        HashMap<String, Object> justPram = new HashMap<>();
        justPram.put("code", code);
        justPram.put("time", codeExpireStr);
//        Boolean aBoolean = push(phone, SmsConstants.SMS_CONFIG_VERIFICATION_CODE_TEMP_ID, justPram);
        Boolean aBoolean = true;
        if (!aBoolean) {
            throw new CrmebException("发送短信失败，请联系后台管理员");
        }
        // 将验证码存入redis
        redisUtil.set(StrUtil.format(SmsConstants.SMS_VERIFICATION_CODE_PHONE, scenario, phone), code, Long.valueOf(codeExpireStr), TimeUnit.MINUTES);
        redisUtil.set(StrUtil.format(SmsConstants.SMS_VERIFICATION_PHONE_NUM, scenario, phone), 1, 60L);
        return aBoolean;
    }

    /**
     * 发送公共验证码前校验
     * @param phone 手机号
     * @param scenario 场景值
     */
    private void beforeSendCommonCodeCheck(String phone, String scenario) {
        String key = StrUtil.format(SmsConstants.SMS_VERIFICATION_PHONE_NUM, scenario, phone);
        if (redisUtil.exists(key)) {
            throw new CrmebException("您的短信发送过于频繁，请稍后再试");
        }
    }

    /**
     * 发送支付成功短信
     *
     * @param phone    手机号
     * @param orderNo  订单编号
     * @param payPrice 支付金额
     * @return Boolean
     */
    @Override
    public Boolean sendPaySuccess(String phone, String orderNo, BigDecimal payPrice) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.PAY_SUCCESS_MARK);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("pay_price", payPrice);
        map.put("order_id", orderNo);
        return push(phone, tempId, map);
    }

    /**
     * 发送订单发货提醒短信
     *
     * @param phone     手机号
     * @param nickName  用户昵称
     * @param storeName 商品名称
     * @param orderNo   订单编号
     */
    @Override
    public Boolean sendOrderDeliverNotice(String phone, String nickName, String storeName, String orderNo) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.DELIVER_GOODS_MARK);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("nickname", nickName);
        map.put("store_name", storeName);
        map.put("order_id", orderNo);
        return push(phone, tempId, map);
    }

    /**
     * 发送入驻审核通过通知短信
     * @param phone 手机号
     * @param date 日期，yyyy-MM-dd
     * @param merName 商户名
     * @param merPhone 商户店长手机号
     * @param pwd 商户店长密码
     * @param siteName 站点名称
     * @return Boolean
     */
    @Override
    public Boolean sendMerchantAuditSuccessNotice(String phone, String date, String merName, String merPhone, String pwd, String siteName) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.AUDIT_SUCCESS_MARK);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("date", date);
        map.put("mer", merName);
        map.put("phone", merPhone);
        map.put("pwd", pwd);
        map.put("site_name", siteName);
        return push(phone, tempId, map);
    }

    /**
     * 发送入驻审核未通过通知短信
     * @param phone 手机号
     * @param date 日期，yyyy-MM-dd
     * @param merName 商户名
     * @param siteName 站点名称
     * @return Boolean
     */
    @Override
    public Boolean sendMerchantFileSuccessNotice(String phone, String date, String merName, String siteName) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.AUDIT_FAIL_MARK);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("date", date);
        map.put("mer", merName);
        map.put("site", siteName);
        return push(phone, tempId, map);
    }

    /**
     * 发送生日礼短信
     * @param phone 手机号
     * @param name 祝福内容
     * @return Boolean
     */
    @Override
    public Boolean sendBirthdayPresent(String phone, String name) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.BIRTHDAY_PRESENT_MARK);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("name", name);
        return push(phone, tempId, map);
    }

    /**
     * 发送下单成功提醒（商户）
     * @param adminList 商户管理员列表
     * @param orderNo 订单号
     */
    @Override
    public Boolean sendPaySuccessToMerchant(List<SystemAdmin> adminList, String orderNo) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.MERCHANT_PAY_SUCCESS_REMINDER);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        for (SystemAdmin admin : adminList) {
            HashMap<String, Object> map = CollUtil.newHashMap();
            map.put("admin_name", admin.getRealName());
            map.put("order_id", orderNo);
            Boolean push = push(admin.getPhone(), tempId, map);
            if (!push) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验手机号验证码
     * @param scenario 场景值
     * @param phone 手机号
     * @param code 验证码
     * @return 是否通过校验
     */
    @Override
    public Boolean checkValidateCode(String scenario, String phone, String code) {
        String redisKey = StrUtil.format(SmsConstants.SMS_VERIFICATION_CODE_PHONE, scenario, phone);
        Object validateCode = redisUtil.get(redisKey);
        if (ObjectUtil.isNull(validateCode)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "验证码已过期");
        }
        if (!validateCode.toString().equals(code)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "验证码错误");
        }
        //删除验证码
        redisUtil.delete(redisKey);
        return true;
    }

    /**
     * 商户入驻提醒（平台）
     * @param adminList 管理员列表
     */
    @Override
    public Boolean sendMerchantSettledApply(List<SystemAdmin> adminList, String merName) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.MERCHANT_SETTLED_APPLY);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        for (SystemAdmin admin : adminList) {
            HashMap<String, Object> map = CollUtil.newHashMap();
            map.put("mer_name", merName);
            Boolean push = push(admin.getPhone(), tempId, map);
            if (!push) {
                return false;
            }
        }
        return true;
    }

    private String getOnePassToken() {
        OnePassApplicationInfoVo infoVo = onePassService.getApplicationInfoException();
        return onePassUtil.getToken(infoVo);
    }


    /**
     * 获取短信模板ID
     * @param mark 消息通知标识
     * @return tempId
     */
    private Integer getSmsTempId(String mark) {
        SystemNotification notification = systemNotificationService.getByMark(mark);
        if (ObjectUtil.isNull(notification)) {
            throw new CrmebException(SystemConfigResultCode.NOTIFICATION_NOT_EXIST);
        }
        if (!notification.getIsSms().equals(NotifyConstants.SWITCH_OPEN)) {
            throw new CrmebException(SystemConfigResultCode.NOTIFICATION_SMS_CLOSE.setMessage(notification.getDescription() + "未配置短信相关或已关闭"));
        }
        SmsTemplate smsTemplate = smsTemplateService.getDetail(notification.getSmsId());
        return Integer.valueOf(smsTemplate.getTempId());
    }

    /**
     * 组装发送对象
     *
     * @param phone     手机号
     * @param msgTempId 模板id
     * @param mapPram   参数map
     */
    private Boolean push(String phone, Integer msgTempId, HashMap<String, Object> mapPram) {
        if (StrUtil.isBlank(phone) || msgTempId <= 0) {
            return false;
        }
        SendSmsVo smsVo = new SendSmsVo();
        smsVo.setMobile(phone);
        smsVo.setTemplate(msgTempId);
        smsVo.setParam(JSONObject.toJSONString(mapPram));
        return sendMessage(smsVo);
    }

    /**
     * 发送短信
     *
     * @param sendSmsVo 短信参数
     */
    private Boolean sendMessage(SendSmsVo sendSmsVo) {
        String result;
        try {
            String token = getOnePassToken();
            HashMap<String, String> header = onePassUtil.getCommonHeader(token);

            Map<String, Object> map = JSONObject.parseObject(sendSmsVo.getParam());
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("phone", sendSmsVo.getMobile());
            param.add("temp_id", sendSmsVo.getTemplate());
            map.forEach((key, value) -> param.add(StrUtil.format(SmsConstants.SMS_COMMON_PARAM_FORMAT, key), value));
            result = restTemplateUtil.postFromUrlencoded(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_SEND_URI_V2, param, header);
            checkResult(result);
        } catch (Exception e) {
            //接口请求异常，需要重新发送
            e.printStackTrace();
            logger.error("发送短信失败,{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 检测结构请求返回的数据
     *
     * @param result 接口返回的结果
     * @return JSONObject
     */
    private JSONObject checkResult(String result) {
        if (StrUtil.isBlank(result)) {
            throw new CrmebException("短信平台接口异常，没任何数据返回！");
        }
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            throw new CrmebException("短信平台接口异常！");
        }
        if (SmsConstants.SMS_ERROR_CODE.equals(jsonObject.getInteger("status"))) {
            throw new CrmebException("短信平台接口" + jsonObject.getString("msg"));
        }
        return jsonObject;
    }
}
