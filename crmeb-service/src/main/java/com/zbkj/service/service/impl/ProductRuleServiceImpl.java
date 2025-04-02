package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.product.ProductRule;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductRuleRequest;
import com.zbkj.common.request.ProductRuleSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.ProductRuleDao;
import com.zbkj.service.service.ProductRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ProductRuleServiceImpl 接口实现
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
@Service
public class ProductRuleServiceImpl extends ServiceImpl<ProductRuleDao, ProductRule> implements ProductRuleService {

    @Resource
    private ProductRuleDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return PageInfo<ProductRule>
    */
    @Override
    public PageInfo<ProductRule> getList(ProductRuleSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<ProductRule> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<ProductRule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductRule::getMerId, systemAdmin.getMerId());
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lambdaQueryWrapper.like(ProductRule::getRuleName, keywords);
        }
        lambdaQueryWrapper.orderByDesc(ProductRule::getId);
        List<ProductRule> list = dao.selectList(lambdaQueryWrapper);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 新增商品规格
     * @param productRuleRequest 规格参数
     * @return 新增结果
     */
    @Override
    public boolean save(ProductRuleRequest productRuleRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (existRuleName(productRuleRequest.getRuleName(), systemAdmin.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_RULE_EXIST);
        }
        ProductRule productRule = new ProductRule();
        BeanUtils.copyProperties(productRuleRequest, productRule);
        productRule.setMerId(systemAdmin.getMerId());
        return save(productRule);
    }

    /**
     * 规格名是否存在
     * @param ruleName 规格名
     */
    private Boolean existRuleName(String ruleName, Integer merId) {
        LambdaQueryWrapper<ProductRule> lqw = Wrappers.lambdaQuery();
        lqw.select(ProductRule::getId);
        lqw.eq(ProductRule::getRuleName, ruleName);
        lqw.eq(ProductRule::getMerId, merId);
        lqw.last(" limit 1");
        ProductRule productRule = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(productRule);
    }

    /**
     * 修改规格
     * @param productRuleRequest 规格参数
     * @return Boolean
     */
    @Override
    public Boolean updateRule(ProductRuleRequest productRuleRequest) {
        if (ObjectUtil.isNull(productRuleRequest.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先选择规格");
        }
        getRuleInfo(productRuleRequest.getId());
        ProductRule newProductRule = new ProductRule();
        BeanUtils.copyProperties(productRuleRequest, newProductRule);
        return updateById(newProductRule);
    }

    /**
     * 删除商品规格
     * @param id 规格ID
     */
    @Override
    public Boolean deleteById(Integer id) {
        getRuleInfo(id);
        return removeById(id);
    }

    /**
     * 商品规格详情
     * @param id 规格ID
     */
    @Override
    public ProductRule getRuleInfo(Integer id) {
        ProductRule productRule = getById(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (ObjectUtil.isNull(productRule) || !admin.getMerId().equals(productRule.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_RULE_NOT_EXIST);
        }
        return productRule;
    }

}

