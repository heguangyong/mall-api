package com.zbkj.front.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.token.FrontTokenComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 移动端管理端 token验证拦截器 使用前注意需要一个@Bean手动注解，否则注入无效
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
public class FrontTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private FrontTokenComponent frontTokenComponent;

    //程序处理之前需要处理的业务
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        String token = frontTokenComponent.getToken(request);
        if (StrUtil.isBlank(token)) {
            response.getWriter().write(JSONObject.toJSONString(CommonResult.failed(CommonResultCode.UNAUTHORIZED)));
            return false;
        }

        Boolean result = frontTokenComponent.checkToken(token);
        if (!result) {
            response.getWriter().write(JSONObject.toJSONString(CommonResult.failed(CommonResultCode.PERMISSION_EXPIRATION)));
            return false;
        }

        // 用户权限与管理权限区分
        String uri = request.getRequestURI();
        if (uri.contains("api/front/employee")) {
            if (uri.contains("api/front/employee/merchant/active") || uri.contains("api/front/employee/merchant/belong/List") ) {
                // 移动端商户管理入口，使用用户token校验
                if (!token.contains(RedisConstants.REDIS_TOKEN_USER_NORMAL_KEY)) {
                    response.getWriter().write(JSONObject.toJSONString(CommonResult.failed(CommonResultCode.UNAUTHORIZED)));
                    return false;
                }
            } else if (!token.contains(RedisConstants.REDIS_TOKEN_USER_SHOP_MANAGER_KEY)) {
                response.getWriter().write(JSONObject.toJSONString(CommonResult.failed(CommonResultCode.UNAUTHORIZED)));
                return false;
            }
        } else {
            if (!token.contains(RedisConstants.REDIS_TOKEN_USER_NORMAL_KEY)) {
                response.getWriter().write(JSONObject.toJSONString(CommonResult.failed(CommonResultCode.UNAUTHORIZED)));
                return false;
            }
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

}
