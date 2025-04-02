package com.zbkj.admin.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.IntegralPageSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.IntegralConfigResponse;
import com.zbkj.common.response.IntegralRecordPageResponse;

/**
 * 积分服务
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
public interface IntegralService {

    /**
     * 获取积分配置
     * @return IntegralConfigResponse
     */
    IntegralConfigResponse getConfig();

    /**
     * 编辑积分配置
     * @param request 积分配置请求对象
     * @return Boolean
     */
    Boolean updateConfig(IntegralConfigResponse request);

    /**
     * 积分记录分页列表
     * @param request 搜索参数
     */
    PageInfo<IntegralRecordPageResponse> findRecordPageList(IntegralPageSearchRequest request);
}
