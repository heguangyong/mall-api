package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.model.product.ProductRelation;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CancelCollectRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserCollectRequest;
import com.zbkj.common.response.UserProductRelationResponse;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.service.dao.ProductRelationDao;
import com.zbkj.service.service.ProductRelationService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StoreProductRelationServiceImpl 接口实现
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
public class ProductRelationServiceImpl extends ServiceImpl<ProductRelationDao, ProductRelation>
        implements ProductRelationService {

    @Resource
    private ProductRelationDao dao;

    @Autowired
    private UserService userService;
    @Autowired
    private TransactionTemplate transactionTemplate;


    /**
     * 取消收藏产品
     */
    @Override
    public Boolean delete(CancelCollectRequest request) {
        Integer userId = userService.getUserIdException();
        LambdaQueryWrapper<ProductRelation> lqw = Wrappers.lambdaQuery();
        lqw.in(ProductRelation::getProductId, CrmebUtil.stringToArray(request.getIds()));
        lqw.eq(ProductRelation::getUid, userId);
        return dao.delete(lqw) > 0;
    }

    /**
     * 获取用户收藏列表
     * @param pageParamRequest 分页参数
     * @return List<UserRelationResponse>
     */
    @Override
    public PageInfo<UserProductRelationResponse> getUserList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        Page<Object> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserProductRelationResponse> list = dao.getUserList(userId);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 获取用户的收藏数量
     * @param uid 用户uid
     * @return 收藏数量
     */
    @Override
    public Integer getCollectCountByUid(Integer uid) {
        LambdaQueryWrapper<ProductRelation> lqr = Wrappers.lambdaQuery();
        lqr.eq(ProductRelation::getUid, uid);
        lqr.eq(ProductRelation::getType, ProductConstants.PRODUCT_RELATION_TYPE_COLLECT);
        return dao.selectCount(lqr);
    }

    /**
     * 根据商品Id取消收藏
     * @param proId 商品Id
     * @return Boolean
     */
    @Override
    public Boolean deleteByProId(Integer proId) {
        LambdaQueryWrapper<ProductRelation> lqw = Wrappers.lambdaQuery();
        lqw.in(ProductRelation::getProductId, proId);
        int delete = dao.delete(lqw);
        return delete > 0;
    }

    /**
     * 根据日期获取收藏量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getCountByDate(String date) {
        LambdaQueryWrapper<ProductRelation> lqw = Wrappers.lambdaQuery();
        lqw.select(ProductRelation::getId);
        lqw.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 根据日期获取收藏量
     * @param date 日期，yyyy-MM-dd格式
     * @param proId 商品id
     * @return Integer
     */
    @Override
    public Integer getCountByDateAndProId(String date, Integer proId) {
        LambdaQueryWrapper<ProductRelation> lqw = Wrappers.lambdaQuery();
        lqw.select(ProductRelation::getId);
        lqw.eq(ProductRelation::getProductId, proId);
        lqw.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 添加收藏
     * @param request 收藏参数
     */
    @Override
    public Boolean add(UserCollectRequest request) {
        ProductRelation productRelation = new ProductRelation();
        productRelation.setProductId(request.getProductId());
        productRelation.setUid(userService.getUserIdException());
        productRelation.setType(ProductConstants.PRODUCT_RELATION_TYPE_COLLECT);
        productRelation.setCategory(request.getCategory());
        return save(productRelation);
    }

    /**
     * 获取商品的收藏数量
     * @param productId 商品id
     * @return Integer
     */
    @Override
    public Integer getCollectCountByProductId(Integer productId) {
        LambdaQueryWrapper<ProductRelation> lqw = Wrappers.lambdaQuery();
        lqw.select(ProductRelation::getId);
        lqw.eq(ProductRelation::getProductId, productId);
        return dao.selectCount(lqw);
    }

    /**
     * 用户是否收藏
     * @param uid 用户uid
     * @param proId 商品id
     * @return Boolean
     */
    @Override
    public Boolean existCollectByUser(Integer uid, Integer proId) {
        if (uid.equals(0) || proId.equals(0)) {
            return false;
        }
        LambdaQueryWrapper<ProductRelation> lqw = new LambdaQueryWrapper<>();
        lqw.select(ProductRelation::getId);
        lqw.eq(ProductRelation::getProductId, proId);
        lqw.eq(ProductRelation::getUid, uid);
        lqw.eq(ProductRelation::getType, ProductConstants.PRODUCT_RELATION_TYPE_COLLECT);
        lqw.last(" limit 1");
        ProductRelation relation = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(relation);
    }

    /**
     * 删除收藏
     * @param uid 用户id
     * @param proIdList 商品id
     * @return Boolean
     */
    @Override
    public Boolean deleteByUidAndProIdList(Integer uid, List<Integer> proIdList) {
        LambdaUpdateWrapper<ProductRelation> wrapper = Wrappers.lambdaUpdate();
        wrapper.in(ProductRelation::getProductId, proIdList);
        wrapper.eq(ProductRelation::getCategory, ProductConstants.PRODUCT_RELATION_CATEGORY_NORMAL);
        wrapper.eq(ProductRelation::getUid, uid);
        wrapper.eq(ProductRelation::getType, ProductConstants.PRODUCT_RELATION_TYPE_COLLECT);
        return dao.delete(wrapper) > 0;
    }

    /**
     * 批量添加收藏
     * @param uid 用户id
     * @param proIdList 商品id
     * @return Boolean
     */
    @Override
    public Boolean batchAdd(Integer uid, List<Integer> proIdList) {
        if (CollUtil.isEmpty(proIdList)) {
            return true;
        }
        List<ProductRelation> relationList = proIdList.stream().map(pid -> {
            ProductRelation productRelation = new ProductRelation();
            productRelation.setUid(uid);
            productRelation.setType(ProductConstants.PRODUCT_RELATION_TYPE_COLLECT);
            productRelation.setCategory(ProductConstants.PRODUCT_RELATION_CATEGORY_NORMAL);
            productRelation.setProductId(pid);
            return productRelation;
        }).collect(Collectors.toList());
        return saveBatch(relationList);
    }

    /**
     * 通过用户id删除
     * @param uid 用户ID
     */
    @Override
    public Boolean deleteByUid(Integer uid) {
        LambdaUpdateWrapper<ProductRelation> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(ProductRelation::getUid, uid);
        return remove(wrapper);
    }
}

