package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.product.MerchantProductGuaranteeGroup;
import com.zbkj.common.model.product.ProductGuaranteeGroup;
import com.zbkj.common.request.ProductGuaranteeGroupAddRequest;
import com.zbkj.common.response.ProductGuaranteeGroupListResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.ProductGuaranteeGroupDao;
import com.zbkj.service.service.MerchantProductGuaranteeGroupService;
import com.zbkj.service.service.ProductGuaranteeGroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
*  ProductGuaranteeGroupServiceImpl 接口实现
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
public class ProductGuaranteeGroupServiceImpl extends ServiceImpl<ProductGuaranteeGroupDao, ProductGuaranteeGroup> implements ProductGuaranteeGroupService {

    @Resource
    private ProductGuaranteeGroupDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MerchantProductGuaranteeGroupService merchantProductGuaranteeGroupService;

    /**
     * 添加保障服务组合
     * @param request 请求参数
     */
    @Override
    public Boolean add(ProductGuaranteeGroupAddRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        String gids = request.getGids();
        List<Integer> gidList = CrmebUtil.stringToArray(gids);
        ProductGuaranteeGroup productGuaranteeGroup = new ProductGuaranteeGroup();
        productGuaranteeGroup.setMerId(systemAdmin.getMerId());
        productGuaranteeGroup.setName(request.getName());
        productGuaranteeGroup.setIsDel(false);

        List<MerchantProductGuaranteeGroup> mpGroupList = gidList.stream().map(gid -> {
            MerchantProductGuaranteeGroup group = new MerchantProductGuaranteeGroup();
            group.setGid(gid);
            group.setIsShow(true);
            return group;
        }).collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            save(productGuaranteeGroup);
            mpGroupList.forEach(mp -> mp.setGroupId(productGuaranteeGroup.getId()));
            merchantProductGuaranteeGroupService.saveBatch(mpGroupList);
            return Boolean.TRUE;
        });
    }

    /**
     * 删除保障服务组合
     * @param id 组合id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        ProductGuaranteeGroup group = getByIdException(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(group.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_GUARANTEE_GROUP_NOT_EXIST);
        }
        group.setIsDel(true);
        return transactionTemplate.execute(e -> {
            updateById(group);
            merchantProductGuaranteeGroupService.deleteByGroupId(group.getId());
            return Boolean.TRUE;
        });
    }

    /**
     * 编辑保障服务组合
     * @param request 请求参数
     */
    @Override
    public Boolean edit(ProductGuaranteeGroupAddRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "组合ID不能为空");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        ProductGuaranteeGroup guaranteeGroup = getByIdException(request.getId());
        if (!admin.getMerId().equals(guaranteeGroup.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_GUARANTEE_GROUP_NOT_EXIST);
        }
        List<Integer> gidList = CrmebUtil.stringToArray(request.getGids());
        List<MerchantProductGuaranteeGroup> mpGroupList = gidList.stream().map(gid -> {
            MerchantProductGuaranteeGroup group = new MerchantProductGuaranteeGroup();
            group.setGroupId(guaranteeGroup.getId());
            group.setGid(gid);
            group.setIsShow(true);
            return group;
        }).collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            if (!guaranteeGroup.getName().equals(request.getName())) {
                guaranteeGroup.setName(request.getName());
                updateById(guaranteeGroup);
            }
            merchantProductGuaranteeGroupService.deleteByGroupId(guaranteeGroup.getId());
            merchantProductGuaranteeGroupService.saveBatch(mpGroupList);
            return Boolean.TRUE;
        });
    }

    /**
     * 保障服务列表
     */
    @Override
    public List<ProductGuaranteeGroupListResponse> findList() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<ProductGuaranteeGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductGuaranteeGroup::getMerId, systemAdmin.getMerId());
        lqw.eq(ProductGuaranteeGroup::getIsDel, false);
        lqw.orderByDesc(ProductGuaranteeGroup::getId);
        List<ProductGuaranteeGroup> groupList = dao.selectList(lqw);
        List<ProductGuaranteeGroupListResponse> responseList = CollUtil.newArrayList();
        if (CollUtil.isEmpty(groupList)) {
            return responseList;
        }
        groupList.forEach(group -> {
            ProductGuaranteeGroupListResponse response = new ProductGuaranteeGroupListResponse();
            BeanUtils.copyProperties(group, response);
            List<MerchantProductGuaranteeGroup> mpList = merchantProductGuaranteeGroupService.findByGroupId(group.getId());
            response.setGuaranteeList(CollUtil.isNotEmpty(mpList) ? mpList : CollUtil.newArrayList());
            responseList.add(response);
        });
        return responseList;
    }

    private ProductGuaranteeGroup getByIdException(Integer id) {
        ProductGuaranteeGroup group = getById(id);
        if (ObjectUtil.isNull(group) || group.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_GUARANTEE_GROUP_NOT_EXIST);
        }
        return group;
    }
}

