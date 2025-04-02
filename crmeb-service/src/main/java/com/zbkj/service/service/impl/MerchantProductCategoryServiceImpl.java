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
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantProductCategory;
import com.zbkj.common.request.merchant.MerchantProductCategoryRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.ProCategoryCacheTree;
import com.zbkj.common.vo.ProCategoryCacheVo;
import com.zbkj.service.dao.MerchantProductCategoryDao;
import com.zbkj.service.service.MerchantProductCategoryService;
import com.zbkj.service.service.ProductService;
import com.zbkj.service.service.SystemAttachmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
*  MerchantProductCategoryServiceImpl 接口实现
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
public class MerchantProductCategoryServiceImpl extends ServiceImpl<MerchantProductCategoryDao, MerchantProductCategory> implements MerchantProductCategoryService {

    @Resource
    private MerchantProductCategoryDao dao;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private TransactionTemplate transactionTemplate;


    /**
     * 获取分类列表
     * @return List
     */
    @Override
    public List<MerchantProductCategory> getAdminList() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<MerchantProductCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantProductCategory::getIsDel, false);
        lqw.eq(MerchantProductCategory::getMerId, admin.getMerId());
        lqw.orderByDesc(MerchantProductCategory::getSort);
        lqw.orderByAsc(MerchantProductCategory::getId);
        return dao.selectList(lqw);
    }

    /**
     * 添加分类
     * @param request 分类参数
     * @return Boolean
     */
    @Override
    public Boolean add(MerchantProductCategoryRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (checkName(request.getName(), admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类名称已存在");
        }
        if (!request.getPid().equals(0)) {
            MerchantProductCategory parentCategory = getByIdException(request.getPid());
            if (!parentCategory.getPid().equals(0)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户商品分类只支持二级分类");
            }
        }

        MerchantProductCategory productCategory = new MerchantProductCategory();
        BeanUtils.copyProperties(request, productCategory);
        productCategory.setId(null);
        productCategory.setMerId(admin.getMerId());
        if (StrUtil.isNotBlank(productCategory.getIcon())) {
            productCategory.setIcon(systemAttachmentService.clearPrefix(productCategory.getIcon()));
        }
        boolean save = save(productCategory);
        if (save) {
            redisUtil.delete(StrUtil.format(RedisConstants.STORE_PRODUCT_CATEGORY_CACHE_LIST_KEY, admin.getMerId()));
        }
        return save;
    }

    /**
     * 删除分类
     * @param id 分类id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        MerchantProductCategory category = getByIdException(id);
        if (!admin.getMerId().equals(category.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_MER_CATEGORY_NOT_EXIST);
        }
        if (category.getPid().equals(0)) {
            List<MerchantProductCategory> categoryList = findAllChildListByPid(id);
            if (CollUtil.isNotEmpty(categoryList)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该分类下有子分类，请先删除子分类");
            }
        }
        if (productService.isExistStoreCategory(category.getId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_MER_CATEGORY_USED);
        }
        category.setIsDel(true);
        boolean update = updateById(category);
        if (update) {
            redisUtil.delete(StrUtil.format(RedisConstants.STORE_PRODUCT_CATEGORY_CACHE_LIST_KEY, admin.getMerId()));
        }
        return update;
    }

    /**
     * 修改分类
     * @param request 修改参数
     * @return Boolean
     */
    @Override
    public Boolean edit(MerchantProductCategoryRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品分类ID不能为空");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        MerchantProductCategory oldCategory = getByIdException(request.getId());
        if (!admin.getMerId().equals(oldCategory.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_MER_CATEGORY_NOT_EXIST);
        }
        if (!oldCategory.getName().equals(request.getName())) {
            if (checkName(request.getName(), admin.getMerId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类名称已存在");
            }
        }
        if (!oldCategory.getPid().equals(0) && request.getPid().equals(0)) {
            if (productService.isExistStoreCategory(oldCategory.getPid())) {
                throw new CrmebException(ProductResultCode.PRODUCT_MER_CATEGORY_USED);
            }
        }
        MerchantProductCategory category = new MerchantProductCategory();
        BeanUtils.copyProperties(request, category);
        if (StrUtil.isNotBlank(category.getIcon())) {
            category.setIcon(systemAttachmentService.clearPrefix(category.getIcon()));
        }
        boolean update = updateById(category);
        if (update) {
            redisUtil.delete(StrUtil.format(RedisConstants.STORE_PRODUCT_CATEGORY_CACHE_LIST_KEY, admin.getMerId()));
        }
        return update;
    }

    /**
     * 修改分类显示状态
     * @param id 分类ID
     * @return Boolean
     */
    @Override
    public Boolean updateShowStatus(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        MerchantProductCategory category = getByIdException(id);
        if (!admin.getMerId().equals(category.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_MER_CATEGORY_NOT_EXIST);
        }
        List<MerchantProductCategory> categoryList = new ArrayList<>();
        boolean showStatus = !category.getIsShow();
        if (!category.getPid().equals(0) && showStatus) {
            MerchantProductCategory parentCategory = getByIdException(category.getPid());
            if (!parentCategory.getIsShow()) {
                parentCategory.setIsShow(showStatus);
                categoryList.add(parentCategory);
            }
        }
        category.setIsShow(!category.getIsShow());
        categoryList.add(category);
        Boolean execute = transactionTemplate.execute(e -> {
            updateBatchById(categoryList);
            if (category.getPid().equals(0)) {
                updateChildShowStatus(category.getId(), showStatus);
            }
            return Boolean.TRUE;
        });
        if (execute) {
            redisUtil.delete(StrUtil.format(RedisConstants.STORE_PRODUCT_CATEGORY_CACHE_LIST_KEY, admin.getMerId()));
        }
        return execute;
    }

    /**
     * 更新子级显示状态
     *
     * @param pid 父级ID
     */
    private void updateChildShowStatus(Integer pid, boolean showStatus) {
        LambdaUpdateWrapper<MerchantProductCategory> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantProductCategory::getIsShow, showStatus);
        wrapper.eq(MerchantProductCategory::getPid, pid);
        wrapper.eq(MerchantProductCategory::getIsDel, false);
        update(wrapper);
    }

    /**
     * 获取分类缓存树
     * @return List<ProCategoryCacheVo>
     */
    @Override
    public List<ProCategoryCacheVo> getCacheTree() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return getCacheTreeByMerId(admin.getMerId());
    }

    private List<ProCategoryCacheVo> getCacheTreeByMerId(Integer merId) {
        List<MerchantProductCategory> categoryList;
        if (redisUtil.exists(StrUtil.format(RedisConstants.STORE_PRODUCT_CATEGORY_CACHE_LIST_KEY, merId))) {
            categoryList = redisUtil.get(StrUtil.format(RedisConstants.STORE_PRODUCT_CATEGORY_CACHE_LIST_KEY, merId));
        } else {
            LambdaQueryWrapper<MerchantProductCategory> lqw = Wrappers.lambdaQuery();
            lqw.eq(MerchantProductCategory::getIsDel, false);
            lqw.eq(MerchantProductCategory::getIsShow, true);
            lqw.eq(MerchantProductCategory::getMerId, merId);
            categoryList = dao.selectList(lqw);
            if (CollUtil.isEmpty(categoryList)) {
                return CollUtil.newArrayList();
            }
            redisUtil.set(StrUtil.format(RedisConstants.STORE_PRODUCT_CATEGORY_CACHE_LIST_KEY, merId), categoryList);
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
     * 获取商户商品分类列表
     * @param merId 商户id
     * @return List
     */
    @Override
    public List<ProCategoryCacheVo> findListByMerId(Integer merId) {
        return getCacheTreeByMerId(merId);
    }

    /**
     * 根据分类id获取所有下级对象
     * @param pid 分类父id
     * @return List<ProductCategory>
     */
    private List<MerchantProductCategory> findAllChildListByPid(Integer pid) {
        LambdaQueryWrapper<MerchantProductCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantProductCategory::getPid, pid);
        lqw.eq(MerchantProductCategory::getIsDel, false);
        return dao.selectList(lqw);
    }

    private MerchantProductCategory getByIdException(Integer id) {
        MerchantProductCategory category = getById(id);
        if (ObjectUtil.isNull(category) || category.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_MER_CATEGORY_NOT_EXIST);
        }
        return category;
    }

    /**
     * 校验分类名称
     * @param name 分类名称
     * @return Boolean
     */
    private Boolean checkName(String name, Integer merId) {
        LambdaQueryWrapper<MerchantProductCategory> lqw = Wrappers.lambdaQuery();
        lqw.select(MerchantProductCategory::getId);
        lqw.eq(MerchantProductCategory::getName, name);
        lqw.eq(MerchantProductCategory::getMerId, merId);
        lqw.eq(MerchantProductCategory::getIsDel, false);
        lqw.last(" limit 1");
        MerchantProductCategory productCategory = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(productCategory) ? Boolean.TRUE : Boolean.FALSE;
    }
}

