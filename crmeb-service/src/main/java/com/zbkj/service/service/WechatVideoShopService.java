package com.zbkj.service.service;


import com.zbkj.common.request.wxvedio.forregister.ShopRegisterApplySceneRequest;
import com.zbkj.common.request.wxvedio.forregister.ShopRegisterFinishAccessInfoRequest;
import com.zbkj.common.vo.wxvedioshop.BaseResultResponseVo;
import com.zbkj.common.vo.wxvedioshop.register.RegisterCheckResponseVo;

/**
 * 视频号交易组件服务——商家入驻部分
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
public interface WechatVideoShopService {

    /**
     * https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/ministore/minishopopencomponent2/API/enter/enter_apply.html
     * {}
     * 接入申请
     * @return 接入结果
     */
    BaseResultResponseVo shopRegisterApply();

    /**
     * https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/ministore/minishopopencomponent2/API/enter/enter_check.html
     * {}
     * 获取接入状态
     * @return 接入状态结果
     */
    RegisterCheckResponseVo shopRegisterCheck();

    /**
     * 完成接入任务
     * https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/ministore/minishopopencomponent2/API/enter/finish_access_info.html
     *
     * scene_group_id
     *
     * 6:完成 spu 接口，7:完成订单接口 / 19:完成二级商户号订单，8:完成物流接口，9:完成售后接口 / 20:完成二级商户号售后，10:测试完成，11:发版完成
     *
     * @return 接入任务状态
     */
    BaseResultResponseVo shopRegisterFinishAccessInfo(ShopRegisterFinishAccessInfoRequest shopRegisterFinishAccessInfoRequest);

    /** 场景接入申请
     * https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/ministore/minishopopencomponent2/API/enter/scene_apply.html
     * scene_group_id
     * @param scene_group_id 1:视频号
     * @return 记录申请记录
     */
    BaseResultResponseVo shopRegisterApplyScene(ShopRegisterApplySceneRequest registerApplyScene);
}
