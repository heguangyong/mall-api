package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.merchant.MerchantType;
import com.zbkj.common.request.merchant.MerchantTypeRequest;
import com.zbkj.common.request.PageParamRequest;

import java.util.List;

/**
*  MerchantTypeService 接口
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
public interface MerchantTypeService extends IService<MerchantType> {

    /**
     * 获取商户类型分页列表
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MerchantType> getAdminPage(PageParamRequest pageParamRequest);

    /**
     * 添加商户类型
     * @param request 请求参数
     * @return Boolean
     */
    Boolean add(MerchantTypeRequest request);

    /**
     * 编辑商户类型
     * @param request 请求参数
     * @return Boolean
     */
    Boolean edit(MerchantTypeRequest request);

    /**
     * 删除商户类型
     * @param id 商户类型ID
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 全部商户类型列表
     * @return List
     */
    List<MerchantType> allList();

}
