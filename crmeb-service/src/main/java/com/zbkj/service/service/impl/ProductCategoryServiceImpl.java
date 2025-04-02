package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.product.ProductCategory;
import com.zbkj.common.request.ProductCategoryRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.ProCategoryCacheTree;
import com.zbkj.common.vo.ProCategoryCacheVo;
import com.zbkj.service.dao.ProductCategoryDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ProductCategoryServiceImpl 接口实现
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
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryDao, ProductCategory> implements ProductCategoryService {

    @Resource
    private ProductCategoryDao dao;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductBrandCategoryService productBrandCategoryService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CouponService couponService;

    /**
     * 获取分类列表
     */
    @Override
    public List<ProductCategory> getAdminList() {
        LambdaQueryWrapper<ProductCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductCategory::getIsDel, false);
        lqw.orderByDesc(ProductCategory::getSort);
        lqw.orderByAsc(ProductCategory::getId);
        return dao.selectList(lqw);
    }

    /**
     *
     *
     * @param request 添加参数
     * @return Boolean
     */
    @Override
    public Boolean add(ProductCategoryRequest request) {
        if (checkName(request.getName(), request.getPid())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类名称已存在");
        }
        ProductCategory productCategory = new ProductCategory();
        BeanUtils.copyProperties(request, productCategory);
        productCategory.setId(null);
        productCategory.setPid(0);
        if (!request.getLevel().equals(1)) {
            if (request.getPid().equals(0)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "子级菜单，父级ID不能为0");
            }
            ProductCategory category = getByIdException(request.getPid());
            if (category.getLevel() >= request.getLevel()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "新增的商品分类层级异常");
            }
            productCategory.setPid(request.getPid());
        }
        if (StrUtil.isNotBlank(productCategory.getIcon())) {
            productCategory.setIcon(systemAttachmentService.clearPrefix(productCategory.getIcon()));
        }
        boolean save = save(productCategory);
        if (save) {
            redisUtil.delete(RedisConstants.PRODUCT_CATEGORY_CACHE_LIST_KEY);
            redisUtil.delete(RedisConstants.PRODUCT_CATEGORY_CACHE_MERCHANT_LIST_KEY);
        }
        return save;
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return Boolean
     * 删除时，需要判断分类是否被使用
     * 1.是否被品牌关联
     * 2.是否被商品使用
     * 后续改成如果1，2级分类，有子分类时无法删除
     */
    @Override
    public Boolean delete(Integer id) {
        ProductCategory category = getByIdException(id);
        if (category.getLevel() < 3) {
            List<ProductCategory> categoryList = findAllChildListByPid(category.getId(), category.getLevel());
            if (CollUtil.isNotEmpty(categoryList)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先删除子级分类");
            }
        }
        // 判断是否有商品使用该分类
        if (productService.isUsePlatformCategory(category.getId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_PLAT_CATEGORY_USED.setMessage("有商品使用该分类，无法删除"));
        }
        // 判断是否品牌关联该分类
        if (productBrandCategoryService.isExistCategory(category.getId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_PLAT_CATEGORY_USED.setMessage("有品牌关联该分类，无法删除"));
        }
        category.setIsDel(true);
        Boolean update = transactionTemplate.execute(e -> {
            updateById(category);
            // 删除商品分类相关优惠券
            couponService.deleteByProCategoryId(category.getId());
            return Boolean.TRUE;
        });
        if (update) {
            redisUtil.delete(RedisConstants.PRODUCT_CATEGORY_CACHE_LIST_KEY);
            redisUtil.delete(RedisConstants.PRODUCT_CATEGORY_CACHE_MERCHANT_LIST_KEY);
        }
        return update;
    }

    /**
     * 修改分类
     *
     * @param request 修改参数
     * @return Boolean
     */
    @Override
    public Boolean edit(ProductCategoryRequest request) {
        if (ObjectUtil.isNull(request.getId()) || ObjectUtil.isNull(request.getPid())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品分类ID或Pid不能为空");
        }
        ProductCategory oldCategory = getByIdException(request.getId());
        if (!oldCategory.getName().equals(request.getName()) || !oldCategory.getPid().equals(request.getPid())) {
            if (checkName(request.getName(), request.getPid())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类名称已存在");
            }
        }
        // 如果分类级别更改，三级分类修改时，需要判断是否有商品/品牌 使用该分类
        if (oldCategory.getLevel().equals(3) && !request.getLevel().equals(3)) {
            // 判断是否有商品使用该分类
            if (productService.isUsePlatformCategory(oldCategory.getId())) {
                throw new CrmebException(ProductResultCode.PRODUCT_PLAT_CATEGORY_USED.setMessage("有商品使用该分类，无法修改"));
            }
            // 判断是否品牌关联该分类
            if (productBrandCategoryService.isExistCategory(oldCategory.getId())) {
                throw new CrmebException(ProductResultCode.PRODUCT_PLAT_CATEGORY_USED.setMessage("有品牌关联该分类，无法修改"));
            }
        }

        ProductCategory category = new ProductCategory();
        BeanUtils.copyProperties(request, category);
        category.setPid(oldCategory.getPid());
        if (StrUtil.isNotBlank(category.getIcon())) {
            category.setIcon(systemAttachmentService.clearPrefix(category.getIcon()));
        }
        boolean update = updateById(category);
        if (update) {
            redisUtil.delete(RedisConstants.PRODUCT_CATEGORY_CACHE_LIST_KEY);
            redisUtil.delete(RedisConstants.PRODUCT_CATEGORY_CACHE_MERCHANT_LIST_KEY);
        }
        return update;
    }

    /**
     * 修改分类显示状态
     *
     * @param id 分类ID
     * @return Boolean
     */
    @Override
    public Boolean updateShowStatus(Integer id) {
        ProductCategory category = getByIdException(id);
        List<ProductCategory> categoryList = new ArrayList<>();
        boolean showStatus = !category.getIsShow();
        if (!category.getLevel().equals(1) && showStatus) {
            ProductCategory parentCategory = getByIdException(category.getPid());
            if (parentCategory.getIsShow().equals(false)) {
                parentCategory.setIsShow(showStatus);
                categoryList.add(parentCategory);
            }
            if (parentCategory.getLevel().equals(2)) {
                ProductCategory firstCategory = getByIdException(parentCategory.getPid());
                if (firstCategory.getIsShow().equals(false)) {
                    firstCategory.setIsShow(showStatus);
                    categoryList.add(firstCategory);
                }
            }
        }

        category.setIsShow(showStatus);
        categoryList.add(category);
        Boolean update = transactionTemplate.execute(e -> {
            updateBatchById(categoryList);
            if (category.getLevel().equals(2)) {
                updateChildShowStatus(category.getId(), showStatus);
            }
            if (category.getLevel().equals(1)) {
                List<ProductCategory> productCategoryList = findAllChildListByPid(category.getId(), category.getLevel());
                productCategoryList.forEach(c -> c.setIsShow(showStatus));
                updateBatchById(productCategoryList, 100);
            }
            return Boolean.TRUE;
        });

        if (update) {
            // 平台端缓存列表不受显示状态影响
            redisUtil.delete(RedisConstants.PRODUCT_CATEGORY_CACHE_LIST_KEY);
            redisUtil.delete(RedisConstants.PRODUCT_CATEGORY_CACHE_MERCHANT_LIST_KEY);
        }
        return update;
    }

    /**
     * 更新子级显示状态
     *
     * @param pid 父级ID
     */
    private void updateChildShowStatus(Integer pid, Boolean showStatus) {
        LambdaUpdateWrapper<ProductCategory> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ProductCategory::getIsShow, showStatus);
        wrapper.eq(ProductCategory::getPid, pid);
        update(wrapper);
    }

    /**
     * 获取分类缓存树
     *
     * @return List<ProCategoryCacheVo>
     */
    @Override
    public List<ProCategoryCacheVo> getCacheTree() {
        List<ProductCategory> categoryList = CollUtil.newArrayList();
        if (redisUtil.exists(RedisConstants.PRODUCT_CATEGORY_CACHE_LIST_KEY)) {
            categoryList = redisUtil.get(RedisConstants.PRODUCT_CATEGORY_CACHE_LIST_KEY);
        } else {
            LambdaQueryWrapper<ProductCategory> lqw = Wrappers.lambdaQuery();
            lqw.eq(ProductCategory::getIsDel, false);
            categoryList = dao.selectList(lqw);
            if (CollUtil.isEmpty(categoryList)) {
                return CollUtil.newArrayList();
            }
            redisUtil.set(RedisConstants.PRODUCT_CATEGORY_CACHE_LIST_KEY, categoryList);
        }
        List<ProCategoryCacheVo> voList = categoryList.stream().map(e -> {
            ProCategoryCacheVo cacheVo = new ProCategoryCacheVo();
            BeanUtils.copyProperties(e, cacheVo);
            return cacheVo;
        }).collect(Collectors.toList());
        ProCategoryCacheTree categoryTree = new ProCategoryCacheTree(voList);
        return categoryTree.buildTree();
    }

    /**
     * 商户端分类缓存树
     *
     * @return List<ProCategoryCacheVo>
     */
    @Override
    public List<ProCategoryCacheVo> getMerchantCacheTree() {
        List<ProductCategory> categoryList = CollUtil.newArrayList();
        if (redisUtil.exists(RedisConstants.PRODUCT_CATEGORY_CACHE_MERCHANT_LIST_KEY)) {
            categoryList = redisUtil.get(RedisConstants.PRODUCT_CATEGORY_CACHE_MERCHANT_LIST_KEY);
        } else {
            LambdaQueryWrapper<ProductCategory> lqw = Wrappers.lambdaQuery();
            lqw.eq(ProductCategory::getIsShow, true);
            lqw.eq(ProductCategory::getIsDel, false);
            categoryList = dao.selectList(lqw);
            if (CollUtil.isEmpty(categoryList)) {
                return CollUtil.newArrayList();
            }
            redisUtil.set(RedisConstants.PRODUCT_CATEGORY_CACHE_MERCHANT_LIST_KEY, categoryList);
        }
        List<ProCategoryCacheVo> voList = categoryList.stream().map(e -> {
            ProCategoryCacheVo cacheVo = new ProCategoryCacheVo();
            BeanUtils.copyProperties(e, cacheVo);
            return cacheVo;
        }).collect(Collectors.toList());
        ProCategoryCacheTree categoryTree = new ProCategoryCacheTree(voList);
        return categoryTree.buildTree();
    }

    /**
     * 根据菜单id获取所有下级对象
     *
     * @param pid   菜单id
     * @param level 分类级别
     * @return List<ProductCategory>
     */
    @Override
    public List<ProductCategory> findAllChildListByPid(Integer pid, Integer level) {
        LambdaQueryWrapper<ProductCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductCategory::getPid, pid);
        lqw.eq(ProductCategory::getIsDel, false);
        if (level.equals(2)) {
            return dao.selectList(lqw);
        }
        // level == 1
        List<ProductCategory> categoryList = dao.selectList(lqw);
        if (CollUtil.isEmpty(categoryList)) {
            return categoryList;
        }
        List<Integer> pidList = categoryList.stream().map(ProductCategory::getId).collect(Collectors.toList());
        lqw.clear();
        lqw.in(ProductCategory::getPid, pidList);
        lqw.eq(ProductCategory::getIsDel, false);
        List<ProductCategory> childCategoryList = dao.selectList(lqw);
        categoryList.addAll(childCategoryList);
        return categoryList;
    }

    /**
     * 获取一级分类数据
     */
    @Override
    public List<ProCategoryCacheVo> getFrontFirstCategory() {
        List<ProCategoryCacheVo> cacheVoList = getMerchantCacheTree();
        return cacheVoList.stream().filter(e -> e.getLevel().equals(1)).collect(Collectors.toList());
    }

    /**
     * 获取首页第三级分类数据
     *
     * @param firstId 第一级分类id
     */
    @Override
    public List<ProductCategory> getHomeThirdCategory(Integer firstId) {
        return getThirdCategoryByFirstId(firstId, 9);
    }

    /**
     * 获取第三级分类数据
     *
     * @param firstId 第一级分类id
     * @param limit   查询数量，0全部
     */
    @Override
    public List<ProductCategory> getThirdCategoryByFirstId(Integer firstId, Integer limit) {
        return dao.getThirdCategoryByFirstId(firstId, limit);
    }

    /**
     * 通过分类id列表获取分类map
     *
     * @param cateIdList 分类id列表
     * @return Map
     */
    @Override
    public Map<Integer, ProductCategory> getMapByIdList(List<Integer> cateIdList) {
        LambdaQueryWrapper<ProductCategory> lqw = Wrappers.lambdaQuery();
        lqw.select(ProductCategory::getId, ProductCategory::getName);
        lqw.in(ProductCategory::getId, cateIdList);
        List<ProductCategory> categoryList = dao.selectList(lqw);
        Map<Integer, ProductCategory> categoryMap = CollUtil.newHashMap();
        categoryList.forEach(c -> {
            categoryMap.put(c.getId(), c);
        });
        return categoryMap;
    }

    /**
     * 获取分类名称通过Ids
     *
     * @param proCategoryIds 分类ID字符，逗号分隔
     * @return 分类名称字符，逗号分隔
     */
    @Override
    public String getNameStrByIds(String proCategoryIds) {
        LambdaQueryWrapper<ProductCategory> lqw = Wrappers.lambdaQuery();
        lqw.select(ProductCategory::getName);
        lqw.in(ProductCategory::getId, CrmebUtil.stringToArray(proCategoryIds));
        List<ProductCategory> categoryList = dao.selectList(lqw);
        return categoryList.stream().map(ProductCategory::getName).collect(Collectors.joining(","));
    }

    /**
     * 通过子ID获取所有父ID
     * @param childIdList 子分类ID
     */
    @Override
    public List<Integer> findParentIdByChildIds(List<Integer> childIdList) {
        LambdaQueryWrapper<ProductCategory> lqw = Wrappers.lambdaQuery();
        lqw.in(ProductCategory::getId, childIdList);
        List<ProductCategory> list = dao.selectList(lqw);
        return list.stream().map(ProductCategory::getPid).collect(Collectors.toList());
    }

    @Override
    public List<ProductCategory> findByIdList(List<Integer> idList) {
        LambdaQueryWrapper<ProductCategory> lqw = Wrappers.lambdaQuery();
        lqw.in(ProductCategory::getId, idList);
        return dao.selectList(lqw);
    }

    private ProductCategory getByIdException(Integer id) {
        ProductCategory category = getById(id);
        if (ObjectUtil.isNull(category) || category.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_PLAT_CATEGORY_NOT_EXIST);
        }
        return category;
    }

    /**
     * 校验分类名称
     *
     * @param name 分类名称
     * @return Boolean
     */
    private Boolean checkName(String name, Integer pid) {
        LambdaQueryWrapper<ProductCategory> lqw = Wrappers.lambdaQuery();
        lqw.select(ProductCategory::getId);
        lqw.eq(ProductCategory::getName, name);
        lqw.eq(ProductCategory::getIsDel, false);
        lqw.eq(ProductCategory::getPid, pid);
        lqw.last(" limit 1");
        ProductCategory productCategory = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(productCategory) ? Boolean.TRUE : Boolean.FALSE;
    }
}

