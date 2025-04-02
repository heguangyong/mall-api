package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.cdkey.CardSecret;
import com.zbkj.common.model.cdkey.CdkeyLibrary;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.BatchOperationCommonRequest;
import com.zbkj.common.request.CardSecretAddRequest;
import com.zbkj.common.request.CardSecretSaveRequest;
import com.zbkj.common.request.CardSecretSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.CardSecretSimpleVo;
import com.zbkj.service.dao.CardSecretDao;
import com.zbkj.service.service.CardSecretService;
import com.zbkj.service.service.CdkeyLibraryService;
import com.zbkj.service.service.ProductAttrValueService;
import com.zbkj.service.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CardSecretServiceImpl 接口实现
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
public class CardSecretServiceImpl extends ServiceImpl<CardSecretDao, CardSecret> implements CardSecretService {

    @Resource
    private CardSecretDao dao;

    @Autowired
    private CdkeyLibraryService cdkeyLibraryService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductAttrValueService productAttrValueService;

    private final static Logger logger = LoggerFactory.getLogger(CardSecretServiceImpl.class);

    /**
     * 卡密分页列表
     */
    @Override
    public PageInfo<CardSecret> findPageList(CardSecretSearchRequest request) {
        CdkeyLibrary cdkeyLibrary = cdkeyLibraryService.getByIdException(request.getLibraryId());
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(cdkeyLibrary.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_NOT_EXIST);
        }
        Page<CardSecret> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<CardSecret> lqw = Wrappers.lambdaQuery();
        lqw.eq(CardSecret::getLibraryId, cdkeyLibrary.getId());
        lqw.eq(CardSecret::getIsDel, 0);
        lqw.orderByAsc(CardSecret::getIsUse);
        lqw.orderByDesc(CardSecret::getId);
        List<CardSecret> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 根据卡密库ID删除
     * @param libraryId 卡密库ID
     */
    @Override
    public Boolean deleteByLibraryId(Integer libraryId) {
        LambdaUpdateWrapper<CardSecret> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CardSecret::getIsDel, 1);
        wrapper.eq(CardSecret::getLibraryId, libraryId);
        wrapper.eq(CardSecret::getIsDel, 0);
        return update(wrapper);
    }

    /**
     * 新增卡密
     * @param request 新增参数
     * @return 新增结果
     */
    @Override
    public Boolean add(CardSecretAddRequest request) {
        CdkeyLibrary cdkeyLibrary = cdkeyLibraryService.getByIdException(request.getLibraryId());
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(cdkeyLibrary.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_NOT_EXIST);
        }
        List<CardSecretSimpleVo> csList = request.getCsList();
        List<String> cardList = csList.stream().map(CardSecretSimpleVo::getCardNumber).collect(Collectors.toList());
        validateCardOnly(cardList, cdkeyLibrary.getId());

        List<CardSecret> cardSecretList = csList.stream().map(cs -> {
            CardSecret cardSecret = new CardSecret();
            cardSecret.setCardNumber(cs.getCardNumber());
            cardSecret.setSecretNum(cs.getSecretNum());
            cardSecret.setMerId(cdkeyLibrary.getMerId());
            cardSecret.setLibraryId(cdkeyLibrary.getId());
            cardSecret.setIsUse(false);
            return cardSecret;
        }).collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            boolean update = saveBatch(cardSecretList);
            if (!update) {
                logger.error("新增卡密失败，卡密库ID = {}", request.getLibraryId());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            update = cdkeyLibraryService.operationTotalNum(cdkeyLibrary.getId(), cardSecretList.size(), Constants.OPERATION_TYPE_ADD);
            if (!update) {
                logger.error("新增卡密更新卡密库数量失败，卡密库ID = {}", request.getLibraryId());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            if (cdkeyLibrary.getProductId() > 0) {
                ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(cdkeyLibrary.getProductAttrValueId(), cdkeyLibrary.getProductId(), ProductConstants.PRODUCT_TYPE_CDKEY);
                update = productAttrValueService.operationStock(cdkeyLibrary.getProductAttrValueId(), cardSecretList.size(), Constants.OPERATION_TYPE_QUICK_ADD, ProductConstants.PRODUCT_TYPE_CDKEY, attrValue.getVersion());
                if (!update) {
                    logger.error("新增卡密更新卡密库关联商品规格属性库存数量失败，卡密库ID = {}", request.getLibraryId());
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
                update = productService.operationStock(cdkeyLibrary.getProductId(), cardSecretList.size(), Constants.OPERATION_TYPE_QUICK_ADD);
                if (!update) {
                    logger.error("新增卡密更新卡密库关联商品数量失败，卡密库ID = {}", request.getLibraryId());
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 删除卡密
     * @param id 卡密ID
     * @return 删除结果
     */
    @Override
    public Boolean delete(Integer id) {
        CardSecret cardSecret = getByIdException(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(cardSecret.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_CARD_SECRET_NOT_EXIST);
        }
        if (cardSecret.getIsUse()) {
            throw new CrmebException(ProductResultCode.PRODUCT_CARD_SECRET_USED);
        }
        CdkeyLibrary cdkeyLibrary = cdkeyLibraryService.getByIdException(cardSecret.getLibraryId());
        LambdaUpdateWrapper<CardSecret> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CardSecret::getIsDel, 1);
        wrapper.eq(CardSecret::getId, id);
        wrapper.eq(CardSecret::getIsDel, 0);
        wrapper.eq(CardSecret::getIsUse, 0);

        return transactionTemplate.execute(e -> {
            boolean update = update(wrapper);
            if (!update) {
                logger.error("删除卡密失败，卡密ID={}", id);
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            update = cdkeyLibraryService.operationTotalNum(cardSecret.getLibraryId(), 1, Constants.OPERATION_TYPE_SUBTRACT);
            if (!update) {
                logger.error("删除卡密,更新卡密库库存失败，卡密ID={}", id);
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            if (cdkeyLibrary.getProductId() > 0) {
                ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(cdkeyLibrary.getProductAttrValueId(), cdkeyLibrary.getProductId(), ProductConstants.PRODUCT_TYPE_CDKEY);
                update = productAttrValueService.operationStock(cdkeyLibrary.getProductAttrValueId(), 1, Constants.OPERATION_TYPE_DELETE, ProductConstants.PRODUCT_TYPE_CDKEY, attrValue.getVersion());
                if (!update) {
                    logger.error("新增卡密更新卡密库关联商品规格属性库存数量失败，卡密ID = {}", id);
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
                update = productService.operationStock(cdkeyLibrary.getProductId(), 1, Constants.OPERATION_TYPE_DELETE);
                if (!update) {
                    logger.error("新增卡密更新卡密库关联商品数量失败，卡密ID = {}", id);
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 编辑卡密
     * @param request 编辑参数
     * @return 结果
     */
    @Override
    public Boolean edit(CardSecretSaveRequest request) {
        CardSecret cardSecret = getByIdException(request.getId());
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(cardSecret.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_CARD_SECRET_NOT_EXIST);
        }
        if (cardSecret.getIsUse()) {
            throw new CrmebException(ProductResultCode.PRODUCT_CARD_SECRET_USED);
        }
        if (cardSecret.getCardNumber().equals(request.getCardNumber()) && cardSecret.getSecretNum().equals(request.getSecretNum())) {
            return Boolean.TRUE;
        }
        if (!cardSecret.getCardNumber().equals(request.getCardNumber())) {
            validateCardOnly(request.getCardNumber(), cardSecret.getLibraryId());
            cardSecret.setCardNumber(request.getCardNumber());
        }
        cardSecret.setSecretNum(request.getSecretNum());
        return updateById(cardSecret);
    }

    /**
     * 导入卡密
     * @param file 导入文件
     * @return 导入结果
     */
    @Override
    public Boolean addImportExcel(MultipartFile file, Integer libraryId) {
        CdkeyLibrary cdkeyLibrary = cdkeyLibraryService.getByIdException(libraryId);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(cdkeyLibrary.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_NOT_EXIST);
        }
        String type = FileUtil.extName(file.getOriginalFilename());
        if (!"xlsx".equals(type) && !"xls".equals(type)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请提交正确的excel文件");
        }
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            logger.error("导入卡密获取文件流失败", e);
            throw new CrmebException("导入卡密获取文件流失败");
        }

        ExcelReader excelReader = ExcelUtil.getReader(inputStream);
        excelReader.addHeaderAlias("卡号", "cardNumber");
        excelReader.addHeaderAlias("密码", "secretNum");
        List<CardSecret> cardSecretList = excelReader.readAll(CardSecret.class);
        if (CollUtil.isEmpty(cardSecretList)) {
            return Boolean.TRUE;
        }
        for (CardSecret cardSecret : cardSecretList) {
            if (StrUtil.isBlank(cardSecret.getCardNumber()) || StrUtil.isBlank(cardSecret.getSecretNum())) {
                throw new CrmebException("导入卡密文件数据缺失，请检查数据后重新导入");
            }
        }
        List<String> collect = cardSecretList.stream().map(CardSecret::getCardNumber).distinct().collect(Collectors.toList());
        if (cardSecretList.size() != collect.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "导入数据中包含重复卡号");
        }
        for (CardSecret cardSecret : cardSecretList) {
            validateCardOnly(cardSecret.getCardNumber(), libraryId);
            cardSecret.setMerId(cdkeyLibrary.getMerId());
            cardSecret.setLibraryId(cdkeyLibrary.getId());
            cardSecret.setIsUse(false);
        }
        return transactionTemplate.execute(e -> {
            boolean update = saveBatch(cardSecretList);
            if (!update) {
                logger.error("导入卡密失败，卡密库ID = {}", libraryId);
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            update = cdkeyLibraryService.operationTotalNum(cdkeyLibrary.getId(), cardSecretList.size(), Constants.OPERATION_TYPE_ADD);
            if (!update) {
                logger.error("导入卡密更新卡密库数量失败，卡密库ID = {}", libraryId);
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            if (cdkeyLibrary.getProductId() > 0) {
                ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(cdkeyLibrary.getProductAttrValueId(), cdkeyLibrary.getProductId(), ProductConstants.PRODUCT_TYPE_CDKEY);
                update = productAttrValueService.operationStock(cdkeyLibrary.getProductAttrValueId(), cardSecretList.size(), Constants.OPERATION_TYPE_QUICK_ADD, ProductConstants.PRODUCT_TYPE_CDKEY, attrValue.getVersion());
                if (!update) {
                    logger.error("导入卡密更新卡密库关联商品规格属性库存数量失败，卡密库ID = {}", libraryId);
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
                update = productService.operationStock(cdkeyLibrary.getProductId(), cardSecretList.size(), Constants.OPERATION_TYPE_QUICK_ADD);
                if (!update) {
                    logger.error("导入卡密更新卡密库关联商品数量失败，卡密库ID = {}", libraryId);
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 批量删除卡密
     */
    @Override
    public Boolean batchDelete(BatchOperationCommonRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        List<Integer> integers = CrmebUtil.stringToArray(request.getIds());
        if (CollUtil.isEmpty(integers)) {
            return true;
        }
        List<CardSecret> cardSecretList = findByIdS(integers);
        if (CollUtil.isEmpty(cardSecretList)) {
            throw new CrmebException(ProductResultCode.PRODUCT_CARD_SECRET_NOT_EXIST);
        }
        if (integers.size() != cardSecretList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "数据条目无法对应，请刷请后操作");
        }
        cardSecretList.forEach(cs -> {
            if (!admin.getMerId().equals(cs.getMerId())) {
                throw new CrmebException(ProductResultCode.PRODUCT_CARD_SECRET_NOT_EXIST);
            }
            if (cs.getIsUse()) {
                throw new CrmebException(ProductResultCode.PRODUCT_CARD_SECRET_USED);
            }
            if (cs.getIsDel()) {
                throw new CrmebException(ProductResultCode.PRODUCT_CARD_SECRET_DELETE);
            }
            cs.setIsDel(true);
        });

        CdkeyLibrary cdkeyLibrary = cdkeyLibraryService.getByIdException(cardSecretList.get(0).getLibraryId());
        return transactionTemplate.execute(e -> {
            boolean update = updateBatchById(cardSecretList);
            if (!update) {
                logger.error("批量删除卡密失败，卡密IDS={}", request.getIds());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            update = cdkeyLibraryService.operationTotalNum(cdkeyLibrary.getId(), cardSecretList.size(), Constants.OPERATION_TYPE_SUBTRACT);
            if (!update) {
                logger.error("批量删除卡密,更新卡密库库存失败，卡密IDS={}", request.getIds());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            if (cdkeyLibrary.getProductId() > 0) {
                ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(cdkeyLibrary.getProductAttrValueId(), cdkeyLibrary.getProductId(), ProductConstants.PRODUCT_TYPE_CDKEY);
                update = productAttrValueService.operationStock(cdkeyLibrary.getProductAttrValueId(), cardSecretList.size(), Constants.OPERATION_TYPE_DELETE, ProductConstants.PRODUCT_TYPE_CDKEY, attrValue.getVersion());
                if (!update) {
                    logger.error("新增卡密更新卡密库关联商品规格属性库存数量失败，卡密IDS={}", request.getIds());
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
                update = productService.operationStock(cdkeyLibrary.getProductId(), cardSecretList.size(), Constants.OPERATION_TYPE_DELETE);
                if (!update) {
                    logger.error("新增卡密更新卡密库关联商品数量失败，卡密IDS={}", request.getIds());
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 消费一条卡密，并返回卡密对象
     * @param libraryId 卡密库ID
     */
    @Override
    public CardSecret consume(Integer libraryId) {
        CardSecret cardSecret = getByNotUsed(libraryId);
        if (ObjectUtil.isNull(cardSecret)) {
            return null;
        }
        LambdaUpdateWrapper<CardSecret> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CardSecret::getIsUse, 1);
        wrapper.eq(CardSecret::getIsUse, 0);
        wrapper.eq(CardSecret::getIsDel, 0);
        wrapper.eq(CardSecret::getId, cardSecret.getId());
        boolean update = update(wrapper);
        if (!update) {
            return null;
        }
        return cardSecret;
    }

    /**
     * 取消消费
     * @param cardSecretIdList 卡密ID列表
     */
    @Override
    public Boolean cancelConsume(List<Integer> cardSecretIdList) {
        Integer cardSecretId = cardSecretIdList.get(0);
        CardSecret cardSecret = getById(cardSecretId);
        return transactionTemplate.execute(e -> {
            LambdaUpdateWrapper<CardSecret> wrapper = Wrappers.lambdaUpdate();
            wrapper.set(CardSecret::getIsUse, 0);
            wrapper.eq(CardSecret::getIsUse, 1);
            wrapper.in(CardSecret::getId, cardSecretIdList);
            update(wrapper);
            cdkeyLibraryService.operationUseNum(cardSecret.getLibraryId(), cardSecretIdList.size(), Constants.OPERATION_TYPE_SUBTRACT);
            return Boolean.TRUE;
        });
    }

    /**
     * 获取卡密列表根据ID列表
     * @param cardSecretIdList 卡密ID列表
     */
    @Override
    public List<CardSecret> findByIds(List<Integer> cardSecretIdList) {
        LambdaQueryWrapper<CardSecret> lqw = Wrappers.lambdaQuery();
        lqw.in(CardSecret::getId, cardSecretIdList);
        return dao.selectList(lqw);
    }

    /**
     * 获取一条未消费的卡密
     *
     * @param libraryId 卡密库ID
     */
    private CardSecret getByNotUsed(Integer libraryId) {
        LambdaQueryWrapper<CardSecret> lqw = Wrappers.lambdaQuery();
        lqw.eq(CardSecret::getLibraryId, libraryId);
        lqw.eq(CardSecret::getIsUse, 0);
        lqw.eq(CardSecret::getIsDel, 0);
        lqw.orderByAsc(CardSecret::getId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    private List<CardSecret> findByIdS(List<Integer> idList) {
        LambdaQueryWrapper<CardSecret> lqw = Wrappers.lambdaQuery();
        lqw.in(CardSecret::getId, idList);
        return dao.selectList(lqw);
    }

    private CardSecret getByIdException(Integer id) {
        CardSecret cardSecret = getById(id);
        if (ObjectUtil.isNull(cardSecret) || cardSecret.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_CARD_SECRET_NOT_EXIST);
        }
        return cardSecret;
    }

    /**
     * 校验卡号唯一
     * @param cardNumber 卡号
     * @param libraryId 卡密库ID
     */
    private void validateCardOnly(String cardNumber, Integer libraryId) {
        LambdaQueryWrapper<CardSecret> lqw = Wrappers.lambdaQuery();
        lqw.eq(CardSecret::getCardNumber, cardNumber);
        lqw.eq(CardSecret::getLibraryId, libraryId);
        lqw.eq(CardSecret::getIsDel, 0);
        Integer count = dao.selectCount(lqw);
        if (count > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "卡号重复");
        }
    }

    /**
     * 校验卡号唯一
     * @param cardNumberList 卡号
     * @param libraryId 卡密库ID
     */
    private void validateCardOnly(List<String> cardNumberList, Integer libraryId) {
        List<String> collect = cardNumberList.stream().distinct().collect(Collectors.toList());
        if (cardNumberList.size() != collect.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "添加数据中有重复卡号");
        }
        LambdaQueryWrapper<CardSecret> lqw = Wrappers.lambdaQuery();
        lqw.in(CardSecret::getCardNumber, cardNumberList);
        lqw.eq(CardSecret::getLibraryId, libraryId);
        lqw.eq(CardSecret::getIsDel, 0);
        Integer count = dao.selectCount(lqw);
        if (count > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "卡号重复");
        }
    }
}

