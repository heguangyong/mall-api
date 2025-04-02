package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.product.ProductRule;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductRuleRequest;
import com.zbkj.common.request.ProductRuleSearchRequest;

/**
 * ProductRuleService 接口
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
public interface ProductRuleService extends IService<ProductRule> {

    /**
     * 商品规格列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<ProductRule> getList(ProductRuleSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增商品规格
     * @param productRuleRequest 规格参数
     * @return 新增结果
     */
    boolean save(ProductRuleRequest productRuleRequest);

    /**
     * 修改规格
     * @param productRuleRequest 规格参数
     * @return Boolean
     */
    Boolean updateRule(ProductRuleRequest productRuleRequest);

    /**
     * 删除商品规格
     * @param id 规格ID
     */
    Boolean deleteById(Integer id);

    /**
     * 商品规格详情
     * @param id 规格ID
     */
    ProductRule getRuleInfo(Integer id);
}
