package com.zbkj.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.request.wxvedio.forregister.ShopRegisterApplySceneRequest;
import com.zbkj.common.request.wxvedio.forregister.ShopRegisterFinishAccessInfoRequest;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.common.utils.WxUtil;
import com.zbkj.common.vo.wxvedioshop.BaseResultResponseVo;
import com.zbkj.common.vo.wxvedioshop.register.RegisterCheckResponseVo;
import com.zbkj.service.service.WechatService;
import com.zbkj.service.service.WechatVideoShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 申请接入
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
@Service
public class WechatVideoShopServiceImpl implements WechatVideoShopService {

    private static final Logger logger = LoggerFactory.getLogger(WechatVideoShopServiceImpl.class);
    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private WechatService wechatService;

    /**
     * 接入申请
     *
     * @return 接入结果
     */
    @Override
    public BaseResultResponseVo shopRegisterApply() {
        // get accessToken
        String miniAccessToken = wechatService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_REGISTER_APPLY, miniAccessToken);
        Map<String, Object> map = new HashMap<>();
        String mapData = restTemplateUtil.postMapData(url, map);
        JSONObject jsonObject = JSONObject.parseObject(mapData);
        WxUtil.checkResult(jsonObject);
        BaseResultResponseVo baseResultResponseVo = JSONObject.parseObject(jsonObject.toJSONString(), BaseResultResponseVo.class);
        return baseResultResponseVo;
    }

    /**
     * 获取接入状态
     *
     * @return 接入状态结果
     */
    @Override
    public RegisterCheckResponseVo shopRegisterCheck() {
        // get accessToken
        String miniAccessToken = wechatService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_REGISTER_CHECK, miniAccessToken);
        Map<String, Object> map = new HashMap<>();
        String resultData = restTemplateUtil.postMapData(url, map);
        JSONObject jsonObject = JSONObject.parseObject(resultData);
        WxUtil.checkResult(jsonObject);
        RegisterCheckResponseVo registerCheckResponseVo = JSON.parseObject(jsonObject.toJSONString(), RegisterCheckResponseVo.class);
        return registerCheckResponseVo;
    }

    /**
     * 完成接入任务
     * https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/ministore/minishopopencomponent2/API/enter/finish_access_info.html
     * <p>
     * access_info_item
     * <p>
     * 6:完成 spu 接口，7:完成订单接口 / 19:完成二级商户号订单，8:完成物流接口，9:完成售后接口 / 20:完成二级商户号售后，10:测试完成，11:发版完成
     *
     * @return 接入任务状态
     */
    @Override
    public BaseResultResponseVo shopRegisterFinishAccessInfo(ShopRegisterFinishAccessInfoRequest shopRegisterFinishAccessInfoRequest) {
        // get accessToken
        String miniAccessToken = wechatService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_REGISTER_FINISH_ACCESS, miniAccessToken);
        Map<String, Object> map = new HashMap<>();
        map.put("access_info_item", shopRegisterFinishAccessInfoRequest.getAccess_info_item());
        String mapData = restTemplateUtil.postMapData(url, map);
        JSONObject jsonObject = JSONObject.parseObject(mapData);
        WxUtil.checkResult(jsonObject);
        BaseResultResponseVo baseResultResponseVo = JSONObject.parseObject(JSON.parse(mapData).toString(), BaseResultResponseVo.class);
        return baseResultResponseVo;
    }

    /**
     * 场景接入申请
     * https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/ministore/minishopopencomponent2/API/enter/scene_apply.html
     * scene_group_id
     *
     * @param shopRegisterApplyScene 参数对象 --》 1:视频号
     * @return 记录申请记录
     */
    @Override
    public BaseResultResponseVo shopRegisterApplyScene(ShopRegisterApplySceneRequest shopRegisterApplyScene) {
        // get accessToken
        String miniAccessToken = wechatService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_REGISTER_APPLY_SCENE, miniAccessToken);
        Map<String, Object> map = new HashMap<>();
        map.put("access_info_item", shopRegisterApplyScene.getScene_group_id());
        String mapData = restTemplateUtil.postMapData(url, map);
        JSONObject jsonObject = JSONObject.parseObject(mapData);
        WxUtil.checkResult(jsonObject);
        BaseResultResponseVo baseResultResponseVo = JSONObject.parseObject(jsonObject.toJSONString(), BaseResultResponseVo.class);
        return baseResultResponseVo;
    }
}
