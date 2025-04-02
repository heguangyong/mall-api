package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.product.MerchantProductGuaranteeGroup;
import com.zbkj.service.dao.MerchantProductGuaranteeGroupDao;
import com.zbkj.service.service.MerchantProductGuaranteeGroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
*  MerchantProductGuaranteeGroupServiceImpl 接口实现
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
public class MerchantProductGuaranteeGroupServiceImpl extends ServiceImpl<MerchantProductGuaranteeGroupDao, MerchantProductGuaranteeGroup> implements MerchantProductGuaranteeGroupService {

    @Resource
    private MerchantProductGuaranteeGroupDao dao;

    /**
     * 通过组合ID删除
     * @param groupId 组合id
     * @return Boolean
     */
    @Override
    public Boolean deleteByGroupId(Integer groupId) {
        LambdaUpdateWrapper<MerchantProductGuaranteeGroup> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(MerchantProductGuaranteeGroup::getGroupId, groupId);
        return dao.delete(wrapper) > 0;
    }

    /**
     * 通过组合ID查询列表
     * @param groupId 组合id
     * @return Boolean
     */
    @Override
    public List<MerchantProductGuaranteeGroup> findByGroupId(Integer groupId) {
        LambdaQueryWrapper<MerchantProductGuaranteeGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantProductGuaranteeGroup::getGroupId, groupId);
//        lqw.eq(MerchantProductGuaranteeGroup::getIsShow, true);
        return dao.selectList(lqw);
    }

    /**
     * 变更有效状态
     * @param gid 保障服务id
     * @param isShow 是否有效
     * @return Boolean
     */
    @Override
    public Boolean updateShowByGid(Integer gid, Boolean isShow) {
        LambdaUpdateWrapper<MerchantProductGuaranteeGroup> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantProductGuaranteeGroup::getIsShow, isShow);
        wrapper.eq(MerchantProductGuaranteeGroup::getGid, gid);
        return update(wrapper);
    }

    /**
     * 删除组合关联，通过保障服务
     * @param gid 保障服务id
     */
    @Override
    public Boolean deleteByGid(Integer gid) {
        LambdaUpdateWrapper<MerchantProductGuaranteeGroup> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(MerchantProductGuaranteeGroup::getGid, gid);
        return dao.delete(wrapper) > 0;
    }
}

