package com.zbkj.front.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.zbkj.common.constants.SmsConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.IosBindingPhoneRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CommonUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.front.service.IosService;
import com.zbkj.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * IOS服务实现类
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
public class IosServiceImpl implements IosService {

    private static final Logger logger = LoggerFactory.getLogger(IosServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * ios绑定手机号（登录后）
     *
     * @param request 请求对象
     * @return 是否绑定
     */
    @Override
    public Boolean bindingPhone(IosBindingPhoneRequest request) {
        checkValidateCode(request.getPhone(), request.getCaptcha());

        User tempUser = userService.getByPhone(request.getPhone());
        if (ObjectUtil.isNotNull(tempUser)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号已注册");
        }

        // 可以绑定
        Integer userId = userService.getUserIdException();
        User user = userService.getById(userId);
        user.setPhone(request.getPhone());
        user.setPwd(CommonUtil.createPwd(request.getPhone()));
        user.setAccount(request.getPhone());
        return userService.updateById(user);
    }

    /**
     * 检测手机验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    private void checkValidateCode(String phone, String code) {
        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + phone);
        if (validateCode == null) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "验证码已过期");
        }
        if (!validateCode.toString().equals(code)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "验证码错误");
        }
        //删除验证码
        redisUtil.delete(SmsConstants.SMS_VALIDATE_PHONE + phone);
    }

}
