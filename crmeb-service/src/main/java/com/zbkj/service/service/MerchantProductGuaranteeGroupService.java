package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.MerchantProductGuaranteeGroup;

import java.util.List;

/**
*  MerchantProductGuaranteeGroupService 接口
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
public interface MerchantProductGuaranteeGroupService extends IService<MerchantProductGuaranteeGroup> {

    /**
     * 通过组合ID删除
     * @param groupId 组合id
     * @return Boolean
     */
    Boolean deleteByGroupId(Integer groupId);

    /**
     * 通过组合ID查询列表
     * @param groupId 组合id
     * @return Boolean
     */
    List<MerchantProductGuaranteeGroup> findByGroupId(Integer groupId);

    /**
     * 变更有效状态
     * @param gid 保障服务id
     * @param isShow 是否有效
     * @return Boolean
     */
    Boolean updateShowByGid(Integer gid, Boolean isShow);

    /**
     * 删除组合关联，通过保障服务
     * @param gid 保障服务id
     */
    Boolean deleteByGid(Integer gid);
}
