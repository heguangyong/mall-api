package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.sgin.SignConfig;
import com.zbkj.common.request.SignConfigRequest;

import java.util.List;

/**
*  SignConfigService 接口
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
public interface SignConfigService extends IService<SignConfig> {

    /**
     * 获取全部配置列表
     * @return List
     */
    List<SignConfig> findList();

    /**
     * 新增连续签到配置
     * @param request 配置参数
     * @return Boolean
     */
    Boolean add(SignConfigRequest request);

    /**
     * 删除连续签到配置
     * @param id 签到配置id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 编辑基础签到配置
     * @param request 配置参数
     * @return Boolean
     */
    Boolean editBaseConfig(SignConfigRequest request);

    /**
     * 编辑连续签到配置
     * @param request 配置参数
     * @return Boolean
     */
    Boolean editAwardConfig(SignConfigRequest request);
}
