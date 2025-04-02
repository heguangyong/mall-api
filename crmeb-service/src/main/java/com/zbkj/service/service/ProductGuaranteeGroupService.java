package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.ProductGuaranteeGroup;
import com.zbkj.common.request.ProductGuaranteeGroupAddRequest;
import com.zbkj.common.response.ProductGuaranteeGroupListResponse;

import java.util.List;

/**
*  ProductGuaranteeGroupService 接口
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
public interface ProductGuaranteeGroupService extends IService<ProductGuaranteeGroup> {

    /**
     * 添加保障服务组合
     * @param request 请求参数
     */
    Boolean add(ProductGuaranteeGroupAddRequest request);

    /**
     * 删除保障服务组合
     * @param id 组合id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 编辑保障服务组合
     * @param request 请求参数
     */
    Boolean edit(ProductGuaranteeGroupAddRequest request);

    /**
     * 保障服务列表
     * @return
     */
    List<ProductGuaranteeGroupListResponse> findList();
}
