package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.cdkey.CdkeyLibrary;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CdkeyLibrarySaveRequest;
import com.zbkj.common.request.CdkeyLibrarySearchRequest;
import com.zbkj.common.response.CdkeyLibraryPageResponse;
import com.zbkj.common.response.CdkeyLibrarySimpleResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.CdkeyLibraryDao;
import com.zbkj.service.service.CardSecretService;
import com.zbkj.service.service.CdkeyLibraryService;
import com.zbkj.service.service.ProductAttrValueService;
import com.zbkj.service.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CdkeyLibraryServiceImpl 接口实现
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
public class CdkeyLibraryServiceImpl extends ServiceImpl<CdkeyLibraryDao, CdkeyLibrary> implements CdkeyLibraryService {

    @Resource
    private CdkeyLibraryDao dao;

    @Autowired
    private CardSecretService cardSecretService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CdkeyLibraryServiceImpl.class);

    /**
     * 分页列表
     *
     * @param request 搜索参数
     */
    @Override
    public PageInfo<CdkeyLibraryPageResponse> findPageList(CdkeyLibrarySearchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<CdkeyLibrary> lqw = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(request.getName())) {
            String kerwords = URLUtil.decode(request.getName());
            lqw.like(CdkeyLibrary::getName, kerwords);
        }
        if (StrUtil.isNotBlank(request.getProductName())) {
            String productName = URLUtil.decode(request.getProductName());
            List<Product> products = productService.likeProductName(productName, admin.getMerId());
            if (CollUtil.isEmpty(products)) {
                Page<CdkeyLibrary> page = PageHelper.startPage(request.getPage(), request.getLimit());
                return CommonPage.copyPageInfo(page, new ArrayList<>());
            }
            List<Integer> productIdList = products.stream().map(Product::getId).collect(Collectors.toList());
            lqw.in(CdkeyLibrary::getProductId, productIdList);
        }
        lqw.eq(CdkeyLibrary::getMerId, admin.getMerId());
        lqw.eq(CdkeyLibrary::getIsDel, 0);
        lqw.orderByDesc(CdkeyLibrary::getId);
        Page<CdkeyLibrary> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<CdkeyLibrary> libraryList = dao.selectList(lqw);
        if (CollUtil.isEmpty(libraryList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<Integer> proIdList = libraryList.stream().map(CdkeyLibrary::getProductId).filter(productId -> productId > 0).distinct().collect(Collectors.toList());
        Map<Integer, Product> productMap = productService.getMapByIdList(proIdList);
        List<CdkeyLibraryPageResponse> responseList = new ArrayList<>();
        for (CdkeyLibrary cdkeyLibrary : libraryList) {
            CdkeyLibraryPageResponse response = new CdkeyLibraryPageResponse();
            response.setId(cdkeyLibrary.getId());
            response.setName(cdkeyLibrary.getName());
            response.setRemark(cdkeyLibrary.getRemark());
            response.setUsedNum(cdkeyLibrary.getUsedNum());
            response.setTotalNum(cdkeyLibrary.getTotalNum());
            response.setCreateTime(cdkeyLibrary.getCreateTime());
            if (cdkeyLibrary.getProductId() > 0) {
                response.setProductName(productMap.get(cdkeyLibrary.getProductId()).getName());
                ProductAttrValue attrValue = productAttrValueService.getById(cdkeyLibrary.getProductAttrValueId());
                response.setProductAttrValueName(attrValue.getSku());
            } else {
                response.setProductName("");
                response.setProductAttrValueName("");
            }
            responseList.add(response);
        }
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 新增卡密库
     * @param request 新增参数
     * @return 新增结果
     */
    @Override
    public Boolean add(CdkeyLibrarySaveRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (isExistName(request.getName(), 0, admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "卡密库名称已存在");
        }
        CdkeyLibrary cdkeyLibrary = new CdkeyLibrary();
        cdkeyLibrary.setName(request.getName());
        cdkeyLibrary.setRemark(StrUtil.isNotBlank(request.getRemark()) ? request.getRemark() : "");
        cdkeyLibrary.setMerId(admin.getMerId());
        return save(cdkeyLibrary);
    }

    /**
     * 删除卡密库
     * @param id 卡密库ID
     * @return 删除结果
     */
    @Override
    public Boolean delete(Integer id) {
        CdkeyLibrary cdkeyLibrary = getByIdException(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(cdkeyLibrary.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_NOT_EXIST);
        }
        cdkeyLibrary.setIsDel(true);
        return transactionTemplate.execute(e -> {
            boolean update = updateById(cdkeyLibrary);
            if (!update) {
                logger.error("删除卡密库失败，卡密库id={}", id);
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            cardSecretService.deleteByLibraryId(cdkeyLibrary.getId());
            return Boolean.TRUE;
        });
    }

    /**
     * 修改卡密库
     * @param request 修改参数
     * @return 修改结果
     */
    @Override
    public Boolean updateLibrary(CdkeyLibrarySaveRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择要修改的卡密库");
        }
        CdkeyLibrary cdkeyLibrary = getByIdException(request.getId());
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(cdkeyLibrary.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_NOT_EXIST);
        }
        if (!cdkeyLibrary.getName().equals(request.getName())) {
            if (isExistName(request.getName(), cdkeyLibrary.getId(), cdkeyLibrary.getMerId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "卡密库名称已存在");
            }
            cdkeyLibrary.setName(request.getName());
        }
        cdkeyLibrary.setRemark(StrUtil.isNotBlank(request.getRemark()) ? request.getRemark() : "");
        return updateById(cdkeyLibrary);
    }

    /**
     * 未关联卡密库列表
     */
    @Override
    public List<CdkeyLibrarySimpleResponse> findUnrelatedList() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<CdkeyLibrary> lqw = Wrappers.lambdaQuery();
        lqw.select(CdkeyLibrary::getId, CdkeyLibrary::getName, CdkeyLibrary::getTotalNum, CdkeyLibrary::getUsedNum);
        lqw.eq(CdkeyLibrary::getProductId, 0);
        lqw.eq(CdkeyLibrary::getMerId, admin.getMerId());
        lqw.eq(CdkeyLibrary::getIsDel, 0);
        lqw.orderByDesc(CdkeyLibrary::getId);
        List<CdkeyLibrary> libraryList = dao.selectList(lqw);
        if (CollUtil.isEmpty(libraryList)) {
            return new ArrayList<>();
        }
        return libraryList.stream().map(library -> {
            CdkeyLibrarySimpleResponse response = new CdkeyLibrarySimpleResponse();
            response.setId(library.getId());
            response.setName(library.getName());
            response.setStock(library.getTotalNum() - library.getUsedNum());
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public CdkeyLibrary getByIdException(Integer id) {
        CdkeyLibrary cdkeyLibrary = getById(id);
        if (ObjectUtil.isNull(cdkeyLibrary) || cdkeyLibrary.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_NOT_EXIST);
        }
        return cdkeyLibrary;
    }

    @Override
    public Boolean operationTotalNum(Integer id, Integer num, String type) {
        UpdateWrapper<CdkeyLibrary> updateWrapper = new UpdateWrapper<>();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            updateWrapper.setSql(StrUtil.format("total_num = total_num + {}", num));
        }
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            updateWrapper.setSql(StrUtil.format("total_num = total_num - {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format(" and (total_num - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新卡密库库存失败,卡密库id = " + id);
        }
        return update;
    }

    /**
     * 清除商品关联
     * @param proId 商品ID
     */
    @Override
    public Boolean clearAssociationProduct(Integer proId) {
        LambdaUpdateWrapper<CdkeyLibrary> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CdkeyLibrary::getProductId, 0);
        wrapper.set(CdkeyLibrary::getProductAttrValueId, 0);
        wrapper.eq(CdkeyLibrary::getProductId, proId);
        wrapper.eq(CdkeyLibrary::getIsDel, 0);
        return update(wrapper);
    }

    @Override
    public Boolean clearAssociationByIds(List<Integer> idList) {
        LambdaUpdateWrapper<CdkeyLibrary> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CdkeyLibrary::getProductId, 0);
        wrapper.set(CdkeyLibrary::getProductAttrValueId, 0);
        wrapper.in(CdkeyLibrary::getId, idList);
        wrapper.eq(CdkeyLibrary::getIsDel, 0);
        return update(wrapper);
    }

    @Override
    public List<CdkeyLibrary> findByIdList(List<Integer> cdkIdList) {
        LambdaQueryWrapper<CdkeyLibrary> lqw = Wrappers.lambdaQuery();
        lqw.in(CdkeyLibrary::getId, cdkIdList);
        lqw.eq(CdkeyLibrary::getIsDel, 0);
        return dao.selectList(lqw);
    }

    /**
     * 操作使用数量
     * @param id 卡密库ID
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    @Override
    public Boolean operationUseNum(Integer id, Integer num, String type) {
        UpdateWrapper<CdkeyLibrary> updateWrapper = new UpdateWrapper<>();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            updateWrapper.setSql(StrUtil.format("used_num = used_num + {}", num));
        }
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            updateWrapper.setSql(StrUtil.format("used_num = used_num - {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format(" and (used_num - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新卡密库使用数量失败,卡密库id = " + id);
        }
        return update;
    }

    /**
     * 是否存在名称
     * @param name 卡密库名称
     * @param id 卡密库ID
     * @param merId 商户ID
     */
    private Boolean isExistName(String name, Integer id, Integer merId) {
        LambdaQueryWrapper<CdkeyLibrary> lqw = Wrappers.lambdaQuery();
        lqw.eq(CdkeyLibrary::getName, name);
        if (ObjectUtil.isNotNull(id) && id > 0) {
            lqw.ne(CdkeyLibrary::getId, id);
        }
        lqw.eq(CdkeyLibrary::getMerId, merId);
        lqw.eq(CdkeyLibrary::getIsDel, 0);
        Integer count = dao.selectCount(lqw);
        return count >= 1;
    }
}

