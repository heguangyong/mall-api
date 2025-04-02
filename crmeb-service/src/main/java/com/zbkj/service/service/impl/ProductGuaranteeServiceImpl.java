package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductGuarantee;
import com.zbkj.common.request.ProductGuaranteeRequest;
import com.zbkj.common.response.ProductGuaranteeResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.service.dao.ProductGuaranteeDao;
import com.zbkj.service.service.MerchantProductGuaranteeGroupService;
import com.zbkj.service.service.ProductGuaranteeService;
import com.zbkj.service.service.ProductService;
import com.zbkj.service.service.SystemAttachmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
*  ProductGuaranteeServiceImpl 接口实现
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
public class ProductGuaranteeServiceImpl extends ServiceImpl<ProductGuaranteeDao, ProductGuarantee> implements ProductGuaranteeService {

    @Resource
    private ProductGuaranteeDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MerchantProductGuaranteeGroupService merchantProductGuaranteeGroupService;

    /**
     * 保障服务列表
     * @return List
     */
    @Override
    public List<ProductGuaranteeResponse> getAdminList() {
        LambdaQueryWrapper<ProductGuarantee> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductGuarantee::getIsDel, false);
        lqw.orderByDesc(ProductGuarantee::getSort);
        List<ProductGuarantee> guaranteeList = dao.selectList(lqw);
        if (CollUtil.isEmpty(guaranteeList)) {
            return CollUtil.newArrayList();
        }
        // 查询保障服务使用的商户数跟商品数（通过商品查询）
        return guaranteeList.stream().map(guarantee -> {
            ProductGuaranteeResponse response = new ProductGuaranteeResponse();
            BeanUtils.copyProperties(guarantee, response);
            response.setMerNum(0);
            response.setProNum(0);
            List<Product> productList = productService.findUseGuarantee(guarantee.getId());
            if (CollUtil.isNotEmpty(productList)) {
                List<Integer> merList = productList.stream().map(Product::getMerId).distinct().collect(Collectors.toList());
                response.setMerNum(merList.size());
                response.setProNum(productList.size());
            }
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 新增保障服务
     * @param request 新增参数
     * @return Boolean
     */
    @Override
    public Boolean add(ProductGuaranteeRequest request) {
        validateName(request.getName());
        ProductGuarantee guarantee = new ProductGuarantee();
        BeanUtils.copyProperties(request, guarantee);
        guarantee.setId(null);
        guarantee.setIcon(systemAttachmentService.clearPrefix(guarantee.getIcon()));
        return dao.insert(guarantee) > 0;
    }

    /**
     * 删除保障服务
     * @param id 保障服务ID
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        ProductGuarantee guarantee = getByIdException(id);
        // 删除前需要判断是否有商品使用，如果有则不能删除
        if (productService.isUseGuarantee(guarantee.getId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_GUARANTEE_USED);
        }
        guarantee.setIsDel(true);
        return transactionTemplate.execute(e -> {
            updateById(guarantee);
            merchantProductGuaranteeGroupService.deleteByGid(guarantee.getId());
            return Boolean.TRUE;
        });
    }

    /**
     * 修改保障服务
     * @param request 修改参数
     * @return Boolean
     */
    @Override
    public Boolean edit(ProductGuaranteeRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "保障服务id不能为空");
        }
        ProductGuarantee oldGuarantee = getByIdException(request.getId());
        if (!oldGuarantee.getName().equals(request.getName())) {
            validateName(request.getName());
        }
        ProductGuarantee guarantee = new ProductGuarantee();
        BeanUtils.copyProperties(request, guarantee);
        guarantee.setIcon(systemAttachmentService.clearPrefix(request.getIcon()));
        return updateById(guarantee);
    }

    /**
     * 修改保障服务显示状态
     * @param id 保障服务ID
     * @return Boolean
     */
    @Override
    public Boolean updateShowStatus(Integer id) {
        ProductGuarantee guarantee = getByIdException(id);
        guarantee.setIsShow(!guarantee.getIsShow());
        return transactionTemplate.execute(e -> {
            updateById(guarantee);
            merchantProductGuaranteeGroupService.updateShowByGid(guarantee.getId(), guarantee.getIsShow());
            return Boolean.TRUE;
        });
    }

    /**
     * 保障服务列表
     * @param type 类型，0-全部，1-有效
     * @return List
     */
    @Override
    public List<ProductGuarantee> getList(Integer type) {
        LambdaQueryWrapper<ProductGuarantee> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductGuarantee::getIsDel, false);
        if (type.equals(1)) {
            lqw.eq(ProductGuarantee::getIsShow, true);
        }
        lqw.orderByDesc(ProductGuarantee::getSort);
        return dao.selectList(lqw);
    }

    /**
     * 保障服务列表
     * @return List
     */
    @Override
    public List<ProductGuarantee> findByIdList(List<Integer> idList) {
        LambdaQueryWrapper<ProductGuarantee> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductGuarantee::getIsDel, false);
        lqw.eq(ProductGuarantee::getIsShow, true);
        lqw.in(ProductGuarantee::getId, idList);
        return dao.selectList(lqw);
    }

    private ProductGuarantee getByIdException(Integer id) {
        ProductGuarantee guarantee = getById(id);
        if (ObjectUtil.isNull(guarantee) || guarantee.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_GUARANTEE_NOT_EXIST);
        }
        return guarantee;
    }

    /**
     * 检测名称是否重复
     * @param name 名称
     */
    private void validateName(String name) {
        LambdaQueryWrapper<ProductGuarantee> lqw = Wrappers.lambdaQuery();
        lqw.select(ProductGuarantee::getId);
        lqw.eq(ProductGuarantee::getName, name);
        lqw.eq(ProductGuarantee::getIsDel, false);
        lqw.last("limit 1");
        ProductGuarantee productGuarantee = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(productGuarantee)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "保障服务名称已存在");
        }
    }
}

