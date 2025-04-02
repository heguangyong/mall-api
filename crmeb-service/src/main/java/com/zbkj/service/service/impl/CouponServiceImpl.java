package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
import com.zbkj.common.constants.CouponConstants;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.coupon.CouponProduct;
import com.zbkj.common.model.coupon.CouponUser;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.product.ProductBrand;
import com.zbkj.common.model.product.ProductCategory;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.CouponResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.CouponSimpleVo;
import com.zbkj.common.vo.SimpleProductVo;
import com.zbkj.service.dao.CouponDao;
import com.zbkj.service.service.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CouponServiceImpl 接口实现
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
@Log4j2
@Service
public class CouponServiceImpl extends ServiceImpl<CouponDao, Coupon> implements CouponService {

    @Resource
    private CouponDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CouponProductService couponProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductBrandService productBrandService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 保存优惠券表
     *
     * @param request CouponRequest 新增参数
     */
    @Override
    public Boolean create(CouponRequest request) {
        if (request.getIsLimited() && (ObjectUtil.isNull(request.getTotal()) || request.getTotal().equals(0))) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入限量数量！");
        }
        if (request.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT) && (StrUtil.isBlank(request.getProductIds()))) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择商品");
        }
        if (request.getIsTimeReceive()) {
            if (ObjectUtil.isNull(request.getReceiveStartTime()) || ObjectUtil.isNull(request.getReceiveEndTime())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择领取时间范围！");
            }
            int compareDate = CrmebDateUtil.compareDate(CrmebDateUtil.dateToStr(request.getReceiveStartTime(), DateConstants.DATE_FORMAT), CrmebDateUtil.dateToStr(request.getReceiveEndTime(), DateConstants.DATE_FORMAT), DateConstants.DATE_FORMAT);
            if (compareDate > -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择正确的领取时间范围！");
            }
        }
        if (!request.getIsFixedTime() && (ObjectUtil.isNull(request.getDay()) || request.getDay().equals(0))) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入天数！");
        }
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(request, coupon);
        if (!coupon.getIsLimited()) {
            coupon.setTotal(0);
        }
        coupon.setLastTotal(coupon.getTotal());
        coupon.setPublisher(CouponConstants.COUPON_PUBLISHER_MERCHANT);
        coupon.setCouponType(CouponConstants.COUPON_TYPE_SATISFY);
        if (StrUtil.isNotBlank(request.getProductIds())) {
            coupon.setLinkedData(request.getProductIds());
        }

        //非固定时间, 领取后多少天
        if (!request.getIsFixedTime()) {
            coupon.setUseStartTime(null);
            coupon.setUseEndTime(null);
        }
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        coupon.setMerId(systemAdmin.getMerId());

        List<CouponProduct> cpList = new ArrayList<>();
        if (request.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            String productIds = request.getProductIds();
            List<Integer> productIdList = CrmebUtil.stringToArray(productIds);
            productIdList.forEach(pid -> {
                CouponProduct couponProduct = new CouponProduct();
                couponProduct.setPid(pid);
                cpList.add(couponProduct);
            });
        }
        return transactionTemplate.execute(e -> {
            save(coupon);
            if (CollUtil.isNotEmpty(cpList)) {
                cpList.forEach(cp -> cp.setCid(coupon.getId()));
                couponProductService.saveBatch(cpList, 100);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 获取详情
     *
     * @param id Integer id
     * @return Coupon
     */
    @Override
    public Coupon getInfoException(Integer id) {
        //获取优惠券信息
        Coupon coupon = getById(id);
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel()) {
            throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST);
        }
        return coupon;
    }

    /**
     * 优惠券详情
     *
     * @param id Integer 获取可用优惠券的商品id
     * @return CouponInfoResponse
     */
    @Override
    public CouponInfoResponse info(Integer id) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Coupon coupon = getByIdAndMerIdException(id, systemAdmin.getMerId());

        List<SimpleProductVo> productList = null;
        if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            List<CouponProduct> cpList = couponProductService.findByCid(coupon.getId());
            List<Integer> primaryIdList = cpList.stream().map(CouponProduct::getPid).collect(Collectors.toList());
            productList = productService.getSimpleListInIds(primaryIdList);
        }

        CouponInfoResponse infoResponse = new CouponInfoResponse();
        BeanUtils.copyProperties(coupon, infoResponse);
        if (CollUtil.isNotEmpty(productList)) {
            infoResponse.setProductList(productList);
        }
        return infoResponse;
    }

    private Coupon getByIdAndMerIdException(Integer id, Integer merId) {
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.eq(Coupon::getId, id);
        lqw.eq(Coupon::getMerId, merId);
        lqw.eq(Coupon::getIsDel, false);
        lqw.last(" limit 1");
        Coupon Coupon = dao.selectOne(lqw);
        if (ObjectUtil.isNull(Coupon)) {
            throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST);
        }
        return Coupon;
    }

    /**
     * 扣减数量
     *
     * @param id        优惠券id
     * @param num       数量
     * @param isLimited 是否限量
     */
    @Override
    public Boolean deduction(Integer id, Integer num, Boolean isLimited) {
        UpdateWrapper<Coupon> updateWrapper = new UpdateWrapper<>();
        if (isLimited) {
            updateWrapper.setSql(StrUtil.format("last_total = last_total - {}", num));
            updateWrapper.last(StrUtil.format(" and (last_total - {} >= 0)", num));
        } else {
            updateWrapper.setSql(StrUtil.format("last_total = last_total + {}", num));
        }
        updateWrapper.eq("id", id);
        return update(updateWrapper);
    }

    /**
     * 删除优惠券
     *
     * @param id 优惠券id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Coupon coupon = getByIdAndMerIdException(id, systemAdmin.getMerId());
        coupon.setIsDel(true);
        return transactionTemplate.execute(e -> {
            updateById(coupon);
            couponProductService.deleteByCid(coupon.getId());
            return Boolean.TRUE;
        });
    }

    /**
     * 移动端优惠券列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return List<CouponFrontResponse>
     */
    @Override
    public PageInfo<CouponFrontResponse> getH5List(CouponFrontSearchRequest request, PageParamRequest pageParamRequest) {
        if (ObjectUtil.isNull(request.getMerId()) && ObjectUtil.isNull(request.getProductId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户ID与商品ID不能都为空");
        }
        PageInfo<Coupon> pageInfo = getH5ListBySearch(request.getCategory(), request.getMerId(), request.getProductId(), pageParamRequest);
        List<Coupon> list = pageInfo.getList();
        if (ObjectUtil.isNull(list)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        //获取用户当前已领取的优惠券
        Integer userId = userService.getUserId();
        List<CouponFrontResponse> CouponFrontResponseArrayList = couponListToFrontResponse(list, userId);
        return CommonPage.copyPageInfo(pageInfo, CouponFrontResponseArrayList);
    }

    private List<CouponFrontResponse> couponListToFrontResponse(List<Coupon> couponList, Integer userId) {
        List<CouponFrontResponse> responseList = new ArrayList<>();
        for (Coupon coupon : couponList) {
            CouponFrontResponse response = new CouponFrontResponse();
            BeanUtils.copyProperties(coupon, response);
            if (userId > 0) {
                CouponUser couponUser = couponUserService.getLastByCouponIdAndUid(coupon.getId(), userId);
                if (ObjectUtil.isNotNull(couponUser)) {
                    response.setIsUse(true);
                    if (coupon.getIsRepeated() && !couponUser.getStatus().equals(CouponConstants.STORE_COUPON_USER_STATUS_USABLE)) {
                        response.setIsUse(false);
                    }
                }
            }
            // 更改使用时间格式，去掉时分秒
            response.setUseStartTimeStr(CrmebDateUtil.dateToStr(coupon.getUseStartTime(), DateConstants.DATE_FORMAT_DATE));
            response.setUseEndTimeStr(CrmebDateUtil.dateToStr(coupon.getUseEndTime(), DateConstants.DATE_FORMAT_DATE));
            responseList.add(response);
        }
        return responseList;
    }

    /**
     * 修改优惠券状态
     *
     * @param id 优惠券id
     */
    @Override
    public Boolean updateStatus(Integer id) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Coupon coupon = getByIdAndMerIdException(id, systemAdmin.getMerId());
        Coupon tempCoupon = new Coupon();
        tempCoupon.setId(id);
        tempCoupon.setStatus(!coupon.getStatus());
        return updateById(tempCoupon);
    }

    /**
     * 商户端优惠券分页列表
     *
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<Coupon> getMerchantPageList(CouponSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<Coupon> page = PageHelper.startPage(request.getPage(), request.getLimit());
        //带 Coupon 类的多条件查询
        LambdaQueryWrapper<Coupon> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Coupon::getMerId, systemAdmin.getMerId());
        lqw.eq(Coupon::getIsDel, false);
        if (ObjectUtil.isNotNull(request.getCategory())) {
            lqw.eq(Coupon::getCategory, request.getCategory());
        }
        if (ObjectUtil.isNotNull(request.getReceiveType())) {
            lqw.eq(Coupon::getReceiveType, request.getReceiveType());
        }
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(Coupon::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getName())) {
            String name = URLUtil.decode(request.getName());
            lqw.like(Coupon::getName, name);
        }
        lqw.orderByDesc(Coupon::getSort).orderByDesc(Coupon::getId);
        List<Coupon> couponList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, couponList);
    }

    /**
     * 商品可用优惠券列表（商品创建时选择使用）
     *
     * @return List
     */
    @Override
    public List<ProductCouponUseResponse> getProductUsableList() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        //带 Coupon 类的多条件查询
        LambdaQueryWrapper<Coupon> lqw = new LambdaQueryWrapper<>();
        lqw.select(Coupon::getId, Coupon::getName, Coupon::getMoney, Coupon::getMinPrice, Coupon::getIsLimited,
                Coupon::getLastTotal, Coupon::getIsFixedTime, Coupon::getUseStartTime, Coupon::getUseEndTime, Coupon::getDay);
        lqw.eq(Coupon::getMerId, systemAdmin.getMerId());
        lqw.eq(Coupon::getIsDel, false);
        lqw.eq(Coupon::getReceiveType, CouponConstants.COUPON_RECEIVE_TYPE_PAY_PRODUCT);
        lqw.eq(Coupon::getStatus, 1);
        lqw.orderByDesc(Coupon::getSort).orderByDesc(Coupon::getId);
        List<Coupon> couponList = dao.selectList(lqw);
        if (CollUtil.isEmpty(couponList)) {
            return CollUtil.newArrayList();
        }
        return couponList.stream().map(c -> {
            ProductCouponUseResponse response = new ProductCouponUseResponse();
            BeanUtils.copyProperties(c, response);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 商品券关联商品编辑
     * @param request 编辑对象
     * @return Boolean
     */
    @Override
    public Boolean couponProductJoinEdit(CouponProductJoinRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Coupon coupon = getByIdAndMerIdException(request.getId(), systemAdmin.getMerId());
        if (!coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "优惠券不是商品券");
        }
        List<Integer> productIdList = CrmebUtil.stringToArray(request.getProductIds());
        List<CouponProduct> cpList = productIdList.stream().map(pid -> {
            CouponProduct couponProduct = new CouponProduct();
            couponProduct.setPid(pid);
            couponProduct.setCid(coupon.getId());
            return couponProduct;
        }).collect(Collectors.toList());
        coupon.setLinkedData(request.getProductIds());
        return transactionTemplate.execute(e -> {
            updateById(coupon);
            couponProductService.deleteByCid(coupon.getId());
            couponProductService.saveBatch(cpList, 100);
            return Boolean.TRUE;
        });
    }

    /**
     * 获取优惠券简单对象列表
     *
     * @param idList id列表
     * @return List
     */
    @Override
    public List<CouponSimpleVo> findSimpleListByIdList(List<Integer> idList) {
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.select(Coupon::getId, Coupon::getName);
        lqw.in(Coupon::getId, idList);
        lqw.eq(Coupon::getIsDel, false);
        List<Coupon> couponList = dao.selectList(lqw);
        return couponList.stream().map(coupon -> {
            CouponSimpleVo simpleVo = new CouponSimpleVo();
            simpleVo.setId(coupon.getId());
            simpleVo.setName(coupon.getName());
            return simpleVo;
        }).collect(Collectors.toList());
    }

    /**
     * 平台端添加优惠券
     *
     * @param request 请求参数
     * @return 添加结果
     */
    @Override
    public Boolean platformAdd(CouponAddRequest request) {
        validateParams(request);
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(request, coupon);
        if (!coupon.getIsLimited()) {
            coupon.setTotal(0);
        }
        coupon.setLastTotal(coupon.getTotal());
        coupon.setPublisher(CouponConstants.COUPON_PUBLISHER_PLATFORM);
        coupon.setCouponType(CouponConstants.COUPON_TYPE_SATISFY);
        coupon.setMerId(0);
        List<CouponProduct> cpList = new ArrayList<>();
        if (request.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            String productIds = request.getLinkedData();
            List<Integer> productIdList = CrmebUtil.stringToArray(productIds);
            productIdList.forEach(pid -> {
                CouponProduct couponProduct = new CouponProduct();
                couponProduct.setPid(pid);
                cpList.add(couponProduct);
            });
        }
        return transactionTemplate.execute(e -> {
            save(coupon);
            if (CollUtil.isNotEmpty(cpList)) {
                cpList.forEach(cp -> cp.setCid(coupon.getId()));
                couponProductService.saveBatch(cpList, 100);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 删除平台优惠券
     *
     * @param request 删除参数
     * @return 删除结果
     */
    @Override
    public Boolean platformDelete(CouponDeleteRequest request) {
        Coupon coupon = getById(request.getId());
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel()) {
            throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST);
        }
        if (coupon.getMerId() > 0) {
            throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST);
        }
        coupon.setIsDel(true);
        return transactionTemplate.execute(e -> {
            updateById(coupon);
            if (request.getLoseEfficacyStatus().equals(1)) {
                couponUserService.loseEfficacyByCouponId(coupon.getId());
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 操作优惠券开关
     *
     * @param id 优惠券ID
     */
    @Override
    public Boolean switchById(Integer id) {
        Coupon coupon = getById(id);
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel()) {
            throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST);
        }
        coupon.setStatus(!coupon.getStatus());
        return updateById(coupon);
    }

    /**
     * 获取平台优惠券详情
     *
     * @param id 优惠券ID
     */
    @Override
    public CouponAdminDetailResponse getPlatformDetail(Integer id) {
        Coupon coupon = getById(id);
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel()) {
            throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST);
        }
        CouponAdminDetailResponse response = new CouponAdminDetailResponse();
        BeanUtils.copyProperties(coupon, response);
        response.setIssuedNum(0);
        if (coupon.getIsLimited()) {
            response.setIssuedNum(coupon.getTotal() - coupon.getLastTotal());
        } else {
            response.setIssuedNum(coupon.getLastTotal());
        }
        // 获取使用数量
        response.setUsedNum(couponUserService.getUsedNumByCouponId(coupon.getId()));
        // 获取关联数据
        switch (coupon.getCategory()) {
            case 1:
                break;
            case 2:
                List<CouponProduct> cpList = couponProductService.findByCid(coupon.getId());
                List<Integer> primaryIdList = cpList.stream().map(CouponProduct::getPid).collect(Collectors.toList());
                response.setProductList(productService.getSimpleListInIds(primaryIdList));
                break;
            case 3:
                break;
            case 4:
                String productCategoryIds = coupon.getLinkedData();
                response.setLinkedDataStr(productCategoryService.getNameStrByIds(productCategoryIds));
                break;
            case 5:
                String brandId = coupon.getLinkedData();
                ProductBrand brand = productBrandService.getById(Integer.valueOf(brandId));
                response.setLinkedDataStr(brand.getName());
                break;
            case 6:
                String merIds = coupon.getLinkedData();
                List<Integer> merIdList = CrmebUtil.stringToArray(merIds);
                Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
                List<Merchant> merchantList = CollUtil.newArrayList();
                merchantList.addAll(merchantMap.values());
                response.setMerchantList(merchantList);
        }
        return response;
    }

    /**
     * 平台端优惠券分页列表
     *
     * @param request 请求参数
     */
    @Override
    public PageInfo<CouponPageResponse> getPlatformPageList(CouponSearchRequest request) {
        Page<Coupon> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<Coupon> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Coupon::getMerId, 0);
        lqw.eq(Coupon::getIsDel, false);
        if (ObjectUtil.isNotNull(request.getCategory())) {
            lqw.eq(Coupon::getCategory, request.getCategory());
        }
        if (ObjectUtil.isNotNull(request.getReceiveType())) {
            lqw.eq(Coupon::getReceiveType, request.getReceiveType());
        }
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(Coupon::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getName())) {
            String name = URLUtil.decode(request.getName());
            lqw.like(Coupon::getName, name);
        }
        lqw.orderByDesc(Coupon::getCreateTime);
        List<Coupon> couponList = dao.selectList(lqw);
        if (CollUtil.isEmpty(couponList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<CouponPageResponse> responseList = couponList.stream().map(c -> {
            CouponPageResponse response = new CouponPageResponse();
            BeanUtils.copyProperties(c, response);
            if (c.getIsLimited()) {
                response.setIssuedNum(c.getTotal() - c.getLastTotal());
            } else {
                response.setIssuedNum(c.getLastTotal());
            }
            response.setUsedNum(couponUserService.getUsedNumByCouponId(c.getId()));
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 平台批量发送优惠券
     *
     * @param request 发送参数
     * @return 发送结果
     */
    @Override
    public Boolean platformBatchSend(CouponBatchSendRequest request) {
        Coupon coupon = getById(request.getCouponId());
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel()) {
            throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST);
        }
        if (coupon.getMerId() > 0 || coupon.getPublisher().equals(CouponConstants.COUPON_PUBLISHER_MERCHANT)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "无法操作商户优惠券");
        }
        if (!coupon.getReceiveType().equals(CouponConstants.COUPON_RECEIVE_TYPE_PLAT_SEND)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "平台活动发放类的优惠券方可发送");
        }
        if (coupon.getIsLimited() && (coupon.getLastTotal() < request.getUidList().size())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "优惠券数量不足！");
        }
        DateTime nowDate = DateUtil.date();
        if (coupon.getIsTimeReceive()) {
            if (nowDate.compareTo(coupon.getReceiveEndTime()) > 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "优惠券可领取时间已过");
            }
        }
        if (coupon.getIsFixedTime()) {
            if (nowDate.compareTo(coupon.getUseEndTime()) > 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "优惠券使用有效期已过！");
            }
        }
        List<Integer> uidList = request.getUidList();
        if (!coupon.getIsRepeated()) {// 是否可重复领取
            List<CouponUser> couponUserList = couponUserService.findByCouponIdAndUidList(coupon.getId(), uidList);
            couponUserList.forEach(cu -> {
                if (uidList.contains(cu.getUid())) {
                    uidList.removeIf(e -> e.equals(cu.getUid()));
                }
            });
            if (CollUtil.isEmpty(uidList)) {
                return Boolean.TRUE;
            }
        }
        if (!coupon.getIsFixedTime()) {
            String endTime = CrmebDateUtil.addDay(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT), coupon.getDay(), DateConstants.DATE_FORMAT);
            coupon.setUseEndTime(CrmebDateUtil.strToDate(endTime, DateConstants.DATE_FORMAT));
            coupon.setUseStartTime(CrmebDateUtil.nowDateTimeReturnDate(DateConstants.DATE_FORMAT));
        }
        List<CouponUser> couponUsers = uidList.stream().map(uid -> {
            CouponUser couponUser = new CouponUser();
            couponUser.setCouponId(coupon.getId());
            couponUser.setUid(uid);
            couponUser.setMerId(0);
            couponUser.setName(coupon.getName());
            couponUser.setPublisher(coupon.getPublisher());
            couponUser.setCategory(coupon.getCategory());
            couponUser.setReceiveType(coupon.getReceiveType());
            couponUser.setCouponType(coupon.getCouponType());
            couponUser.setMoney(coupon.getMoney());
            couponUser.setDiscount(coupon.getDiscount());
            couponUser.setMinPrice(coupon.getMinPrice());
            couponUser.setStatus(CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
            //是否有固定的使用时间
            if (!coupon.getIsFixedTime()) {
                String endTime = CrmebDateUtil.addDay(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT), coupon.getDay(), DateConstants.DATE_FORMAT);
                couponUser.setStartTime(CrmebDateUtil.nowDateTimeReturnDate(DateConstants.DATE_FORMAT));
                couponUser.setEndTime(CrmebDateUtil.strToDate(endTime, DateConstants.DATE_FORMAT));
            } else {
                couponUser.setStartTime(coupon.getUseStartTime());
                couponUser.setEndTime(coupon.getUseEndTime());
            }
            return couponUser;
        }).collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            Boolean deduction = deduction(coupon.getId(), uidList.size(), coupon.getIsLimited());
            if (!deduction) {
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            couponUserService.saveBatch(couponUsers, 100);
            return Boolean.TRUE;
        });
    }

    /**
     * 平台端可发送优惠券列表
     */
    @Override
    public PageInfo<Coupon> getPlatformCanSendList(CommonSearchRequest request) {
        Page<Coupon> page = PageHelper.startPage(request.getPage(), request.getLimit());
        String now = DateUtil.now();
        LambdaQueryWrapper<Coupon> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Coupon::getMerId, 0);
        lqw.eq(Coupon::getIsDel, false);
        lqw.eq(Coupon::getPublisher, CouponConstants.COUPON_PUBLISHER_PLATFORM);
        lqw.eq(Coupon::getStatus, true);
        lqw.eq(Coupon::getReceiveType, CouponConstants.COUPON_RECEIVE_TYPE_PLAT_SEND);
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.like(Coupon::getName, keywords);
        }
        lqw.and(i -> i.eq(Coupon::getIsLimited, 0).or(o -> o.gt(Coupon::getLastTotal, 0)));
        lqw.and(i -> i.eq(Coupon::getIsTimeReceive, 0).or(o -> o.le(Coupon::getReceiveStartTime, now).ge(Coupon::getReceiveEndTime, now)));
        lqw.orderByDesc(Coupon::getCreateTime);
        List<Coupon> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 优惠券参数校验
     */
    private void validateParams(CouponAddRequest request) {
        if (request.getIsLimited() && (ObjectUtil.isNull(request.getTotal()) || request.getTotal().equals(0))) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入限量数量！");
        }
        if (request.getIsTimeReceive()) {
            if (ObjectUtil.isNull(request.getReceiveStartTime()) || ObjectUtil.isNull(request.getReceiveEndTime())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择领取时间范围！");
            }
            int compareDate = CrmebDateUtil.compareDate(CrmebDateUtil.dateToStr(request.getReceiveStartTime(), DateConstants.DATE_FORMAT), CrmebDateUtil.dateToStr(request.getReceiveEndTime(), DateConstants.DATE_FORMAT), DateConstants.DATE_FORMAT);
            if (compareDate > -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择正确的领取时间范围！");
            }
        }
        if (request.getIsFixedTime()) {
            if (ObjectUtil.isNull(request.getUseStartTime()) || ObjectUtil.isNull(request.getUseEndTime())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择使用时间范围！");
            }
            int compareDate = CrmebDateUtil.compareDate(CrmebDateUtil.dateToStr(request.getUseStartTime(), DateConstants.DATE_FORMAT), CrmebDateUtil.dateToStr(request.getUseEndTime(), DateConstants.DATE_FORMAT), DateConstants.DATE_FORMAT);
            if (compareDate > -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择正确的使用时间范围！");
            }
        } else {
            if (ObjectUtil.isNull(request.getDay()) || request.getDay().equals(0)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入天数！");
            }
        }
        if (!request.getCategory().equals(CouponConstants.COUPON_CATEGORY_UNIVERSAL) && (StrUtil.isBlank(request.getLinkedData()))) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择关联数据");
        }
        if (request.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY) || request.getCategory().equals(CouponConstants.COUPON_CATEGORY_BRAND)) {
            String[] split = request.getLinkedData().split(",");
            if (split.length > 1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "只能选择一个品类或品牌");
            }
        }
    }

    /**
     * 根据diy首页优惠券组件 获取对应优惠券你列表
     *
     * @param limit 多少条
     * @return 优惠券列表
     */
    @Override
    public List<CouponFrontResponse> getCouponListForDiyPageHome(Integer limit) {
        List<Coupon> list = dao.getCouponListForDiyHomePage(limit);
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        Integer userId = userService.getUserId();
        return couponListToFrontResponse(list, userId);
    }

    /**
     * 用户可领取的优惠券
     *
     * @return List<Coupon>
     */
    private PageInfo<Coupon> getH5ListBySearch(Integer category, Integer merId, Integer productId, PageParamRequest pageParamRequest) {
        Date date = CrmebDateUtil.nowDateTime();

        Map<String, Object> map = new HashMap<>();
        if (category > 0) {
            map.put("category", category);
        }
        map.put("merId", merId);
        map.put("productId", productId);
        map.put("date", date);
        Page<Coupon> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Coupon> couponList = dao.getH5ListBySearch(map);
        return CommonPage.copyPageInfo(page, couponList);
    }

    /**
     * 移动端优惠券领券中心
     *
     * @param request 请求参数
     */
    @Override
    public PageInfo<CouponCenterPageResponse> getCouponCenter(CouponCenterSearchRequest request) {
        Integer userId = userService.getUserId();
        String now = DateUtil.now();

        Page<Coupon> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.eq(Coupon::getPublisher, CouponConstants.COUPON_PUBLISHER_PLATFORM);
        lqw.eq(Coupon::getMerId, 0);
        lqw.eq(Coupon::getReceiveType, CouponConstants.COUPON_RECEIVE_TYPE_MANUAL);
        if (ObjectUtil.isNull(request.getCategory()) || request.getCategory() > 0) {
            lqw.eq(Coupon::getCategory, request.getCategory());
        }
        lqw.and(i -> i.eq(Coupon::getIsTimeReceive, 0).or(o -> o.le(Coupon::getReceiveStartTime, now).ge(Coupon::getReceiveEndTime, now)));
        lqw.eq(Coupon::getStatus, 1);
        lqw.eq(Coupon::getIsDel, 0);
        lqw.orderByDesc(Coupon::getCreateTime);
        List<Coupon> couponList = dao.selectList(lqw);
        List<CouponCenterPageResponse> responseList = new ArrayList<>();

        couponList.forEach(coupon -> {
            CouponCenterPageResponse response = new CouponCenterPageResponse();
            BeanUtils.copyProperties(coupon, response);
            List<SimpleProductVo> productVoList = new ArrayList<>();
            switch (coupon.getCategory()) {
                case 2:// 商品
                    List<CouponProduct> couponProductList = couponProductService.findByCid(coupon.getId());
                    List<Integer> pidList = couponProductList.stream().map(CouponProduct::getPid).collect(Collectors.toList());
                    productVoList = productService.findCouponListLimit3(coupon.getCategory(), pidList, null, null);
                    break;
                case 3:// 通用
                    productVoList = productService.findCouponListLimit3(coupon.getCategory(), null, null, null);
                    break;
                case 4:// 品类券
                    // 获取品类（3级列表）
                    ProductCategory productCategory = productCategoryService.getById(Integer.valueOf(coupon.getLinkedData()));
                    List<Integer> pcIdList = new ArrayList<>();
                    if (productCategory.getLevel().equals(3)) {
                        pcIdList.add(productCategory.getId());
                    } else {
                        List<ProductCategory> productCategoryList = new ArrayList<>();
                        if (productCategory.getLevel().equals(2)) {
                            productCategoryList = productCategoryService.findAllChildListByPid(productCategory.getId(), productCategory.getLevel());
                        }
                        if (productCategory.getLevel().equals(1)) {
                            productCategoryList = productCategoryService.getThirdCategoryByFirstId(productCategory.getId(), 0);
                        }
                        List<Integer> collect = productCategoryList.stream().map(ProductCategory::getId).collect(Collectors.toList());
                        pcIdList.addAll(collect);
                    }
                    if (CollUtil.isNotEmpty(pcIdList)) {
                        productVoList = productService.findCouponListLimit3(coupon.getCategory(), null, coupon.getLinkedData(), pcIdList);
                    }
                    response.setProductCategoryName(productCategory.getName());
                    break;
                case 5:// 品牌
                    ProductBrand brand = productBrandService.getById(Integer.valueOf(coupon.getLinkedData()));
                    productVoList = productService.findCouponListLimit3(coupon.getCategory(), null, coupon.getLinkedData(), null);
                    response.setProductBrandName(brand.getName());
                    break;
                case 6:// 跨店
                    productVoList = productService.findCouponListLimit3(coupon.getCategory(), null, coupon.getLinkedData(), null);
                    break;
            }
            response.setProductVoList(productVoList);

            if (userId > 0) {
                CouponUser couponUser = couponUserService.getLastByCouponIdAndUid(coupon.getId(), userId);
                if (ObjectUtil.isNotNull(couponUser)) {
                    response.setIsUserReceive(true);
                    if (coupon.getIsRepeated() && !couponUser.getStatus().equals(CouponConstants.STORE_COUPON_USER_STATUS_USABLE)) {
                        response.setIsUserReceive(false);
                    }
                }
            }
            responseList.add(response);
        });
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 通过品牌删除优惠券
     *
     * @param brandId 品牌ID
     */
    @Override
    public Boolean deleteByBrandId(Integer brandId) {
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.select(Coupon::getId);
        lqw.eq(Coupon::getCategory, CouponConstants.COUPON_CATEGORY_BRAND);
        lqw.eq(Coupon::getLinkedData, brandId.toString());
        lqw.eq(Coupon::getIsDel, false);
        List<Coupon> couponList = dao.selectList(lqw);
        if (CollUtil.isEmpty(couponList)) {
            return Boolean.TRUE;
        }
        LambdaUpdateWrapper<Coupon> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Coupon::getIsDel, true);
        wrapper.eq(Coupon::getCategory, CouponConstants.COUPON_CATEGORY_BRAND);
        wrapper.eq(Coupon::getLinkedData, brandId.toString());
        return transactionTemplate.execute(e -> {
            update(wrapper);
            couponList.forEach(c -> {
                couponUserService.loseEfficacyByCouponId(c.getId());
            });
            return Boolean.TRUE;
        });
    }

    /**
     * 通过商品分类删除优惠券
     *
     * @param proCategoryId 商品分类ID
     */
    @Override
    public Boolean deleteByProCategoryId(Integer proCategoryId) {
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.select(Coupon::getId);
        lqw.eq(Coupon::getCategory, CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY);
        lqw.eq(Coupon::getLinkedData, proCategoryId.toString());
        lqw.eq(Coupon::getIsDel, false);
        List<Coupon> couponList = dao.selectList(lqw);
        if (CollUtil.isEmpty(couponList)) {
            return Boolean.TRUE;
        }
        LambdaUpdateWrapper<Coupon> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Coupon::getIsDel, true);
        wrapper.eq(Coupon::getCategory, CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY);
        wrapper.eq(Coupon::getLinkedData, proCategoryId.toString());
        return transactionTemplate.execute(e -> {
            update(wrapper);
            couponList.forEach(c -> {
                couponUserService.loseEfficacyByCouponId(c.getId());
            });
            return Boolean.TRUE;
        });
    }

    /**
     * 优惠券编辑
     * @param request 请求参数
     */
    @Override
    public Boolean edit(CouponUpdateRequest request) {
        if (request.getIsLimited() && ObjectUtil.isNull(request.getNum())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择添加发放数量");
        }
        if (request.getIsFixedTime() && (ObjectUtil.isNull(request.getUseEndTime()) || ObjectUtil.isNull(request.getUseStartTime()))) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择使用时间范围");
        }
        if (!request.getIsFixedTime() && ObjectUtil.isNull(request.getDay())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择使用天数");
        }
        Coupon coupon = getById(request.getId());
        if (ObjectUtil.isNull(coupon)) {
            throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST);
        }
        if (request.getIsLimited() && !coupon.getIsLimited()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "不限量优惠券不能改为限量");
        }
        if (request.getIsTimeReceive() && !coupon.getIsTimeReceive()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "领取不限时优惠券不能改为限时");
        }
        if (request.getIsTimeReceive() && coupon.getIsTimeReceive()) {
            if (request.getReceiveEndTime().compareTo(coupon.getReceiveEndTime()) < 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "领取时间只能往长延期");
            }
        }
        if (!coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_UNIVERSAL) && (StrUtil.isBlank(request.getLinkedData()))) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择关联数据");
        }
        if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY) || coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_BRAND)) {
            String[] split = request.getLinkedData().split(",");
            if (split.length > 1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "只能选择一个品类或品牌");
            }
        }

        UpdateWrapper<Coupon> update = Wrappers.update();
        update.set("name", request.getName());
        update.set("is_limited", request.getIsLimited() ? 1 : 0);
        if (request.getIsLimited() && coupon.getIsLimited()) {
            update.setSql(StrUtil.format("total = total + {}", request.getNum()));
            update.setSql(StrUtil.format("last_total = last_total + {}", request.getNum()));
        }
        if (coupon.getIsLimited() && !request.getIsLimited()) {
            update.setSql("last_total = total - last_total");
        }
        update.set("is_time_receive", request.getIsTimeReceive() ? 1 : 0);
        if (request.getIsTimeReceive()) {
            update.set("receive_start_time", request.getReceiveStartTime());
            update.set("receive_end_time", request.getReceiveEndTime());
        }
        update.set("is_fixed_time", request.getIsFixedTime() ? 1 : 0);
        if (request.getIsFixedTime()) {
            update.set("use_start_time", request.getUseStartTime());
            update.set("use_end_time", request.getUseEndTime());
        } else {
            update.set("day", request.getDay());
        }
        if (!coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_UNIVERSAL)) {
            update.set("linked_data", request.getLinkedData());
        }
        update.set("is_repeated", request.getIsRepeated());
        update.eq("id", coupon.getId());

        List<CouponProduct> cpList = new ArrayList<>();
        if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            String productIds = request.getLinkedData();
            List<Integer> productIdList = CrmebUtil.stringToArray(productIds);
            productIdList.forEach(pid -> {
                CouponProduct couponProduct = new CouponProduct();
                couponProduct.setPid(pid);
                couponProduct.setCid(coupon.getId());
                cpList.add(couponProduct);
            });
        }
        return transactionTemplate.execute(e -> {
            update(update);
            if (CollUtil.isNotEmpty(cpList)) {
                couponProductService.deleteByCid(coupon.getId());
                couponProductService.saveBatch(cpList, 100);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 获取商品详情优惠券数据
     * @param proId 商品ID
     * @param limit 查询条数
     */
    @Override
    public List<Coupon> findProductDetailLimit(Integer proId, Integer limit) {
        String now = DateUtil.now();
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.eq(Coupon::getCategory, CouponConstants.COUPON_CATEGORY_PRODUCT);
        lqw.eq(Coupon::getReceiveType, CouponConstants.COUPON_RECEIVE_TYPE_MANUAL);
        lqw.eq(Coupon::getStatus, 1);
        lqw.and(i -> i.eq(Coupon::getIsTimeReceive, 0).or(o -> o.le(Coupon::getReceiveStartTime, now).ge(Coupon::getReceiveEndTime, now)));
        lqw.and(i -> i.eq(Coupon::getIsFixedTime, 0).or(o -> o.ge(Coupon::getUseEndTime, now)));
        lqw.apply(" find_in_set({0}, linked_data) ", proId);
        lqw.eq(Coupon::getIsDel, false);
        lqw.orderByDesc(Coupon::getMoney, Coupon::getId);
        lqw.last(" limit " + limit);
        return dao.selectList(lqw);
    }

    /**
     * 通过ID获取优惠券
     * @param couponIdList 优惠券ID列表
     */
    @Override
    public List<Coupon> findByIds(List<Integer> couponIdList) {
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.in(Coupon::getId, couponIdList);
        return dao.selectList(lqw);
    }

    /**
     * 获取所有适用金额的商户优惠券（多商品）
     * @param merId 商户ID
     * @param proIdList 商品ID列表
     * @param minPrice 金额
     */
    @Override
    public List<Coupon> findManyByMerIdAndMoney(Integer merId, List<Integer> proIdList, BigDecimal minPrice) {
        String now = DateUtil.now();
        Map<String, Object> map = new HashMap<>();
        map.put("merId", merId);
        map.put("proIdList", proIdList);
        map.put("nowDate", now);
        map.put("minPrice", minPrice);
        return dao.findManyByMerIdAndMoney(map);
    }

    /**
     * 获取所有适用金额的平台优惠券(多商品)
     * @param proIdList 商品ID列表
     * @param proCategoryIdList 商品分类ID列表
     * @param merIdList 商户ID列表
     * @param brandIdList 品牌ID列表
     * @param price 金额
     */
    @Override
    public List<Coupon> findManyPlatByMerIdAndMoney(List<Integer> proIdList, List<Integer> proCategoryIdList, List<Integer> merIdList, List<Integer> brandIdList, BigDecimal price) {
        String now = DateUtil.now();
        Map<String, Object> map = new HashMap<>();
        map.put("proIdList", proIdList);
        map.put("proCategoryIdList", proCategoryIdList);
        map.put("merIdList", merIdList);
        map.put("brandIdList", brandIdList);
        map.put("nowDate", now);
        map.put("minPrice", price);
        return dao.findManyPlatByMerIdAndMoney(map);
    }

    /**
     * 发放新人礼
     * @param userId 用户ID
     */
    @Override
    public List<Coupon> sendNewPeopleGift(Integer userId) {
        // 查询可以发放的新人礼优惠券
        String newPeopleSwitch = systemConfigService.getValueByKey(SysConfigConstants.NEW_PEOPLE_PRESENT_SWITCH);
        if (StrUtil.isBlank(newPeopleSwitch) || newPeopleSwitch.equals(Constants.COMMON_SWITCH_CLOSE)) {
            return null;
        }
        String newPeopleSwitchCouponStr = systemConfigService.getValueByKey(SysConfigConstants.NEW_PEOPLE_PRESENT_COUPON);
        if (StrUtil.isBlank(newPeopleSwitchCouponStr)) {
            return null;
        }
        List<Integer> couponIdList = CrmebUtil.stringToArray(newPeopleSwitchCouponStr);
        if (CollUtil.isEmpty(couponIdList)) {
            return null;
        }
        List<Coupon> couponList = findByIds(couponIdList);
        DateTime nowDate = DateUtil.date();
        List<Coupon> canSendCouponList = couponList.stream().filter(coupon -> {
            if (coupon.getIsDel()) {
                return false;
            }
            if (!coupon.getStatus()) {
                return false;
            }
            if (!coupon.getReceiveType().equals(CouponConstants.COUPON_RECEIVE_TYPE_PLAT_SEND)) {
                return false;
            }
            if (coupon.getIsLimited() && coupon.getLastTotal() <= 0) {
                return false;
            }
            if (coupon.getIsTimeReceive()) {
                if (nowDate.compareTo(coupon.getReceiveStartTime()) < 0) {
                    return false;
                }
                if (nowDate.compareTo(coupon.getReceiveEndTime()) > 0) {
                    return false;
                }
            }
            if (coupon.getIsFixedTime()) {
                if (nowDate.compareTo(coupon.getUseEndTime()) > 0) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        if (CollUtil.isEmpty(canSendCouponList)) {
            return null;
        }
        List<CouponUser> couponUserList = canSendCouponList.stream().map(coupon -> {
            CouponUser couponUser = new CouponUser();
            couponUser.setCouponId(coupon.getId());
            couponUser.setUid(userId);
            couponUser.setMerId(coupon.getMerId());
            couponUser.setName(coupon.getName());
            couponUser.setPublisher(coupon.getPublisher());
            couponUser.setCategory(coupon.getCategory());
            couponUser.setReceiveType(coupon.getReceiveType());
            couponUser.setCouponType(coupon.getCouponType());
            couponUser.setMoney(coupon.getMoney());
            couponUser.setDiscount(coupon.getDiscount());
            couponUser.setMinPrice(coupon.getMinPrice());
            couponUser.setStatus(CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
            //是否有固定的使用时间
            if (!coupon.getIsFixedTime()) {
                String endTime = CrmebDateUtil.addDay(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT), coupon.getDay(), DateConstants.DATE_FORMAT);
                couponUser.setStartTime(CrmebDateUtil.nowDateTimeReturnDate(DateConstants.DATE_FORMAT));
                couponUser.setEndTime(CrmebDateUtil.strToDate(endTime, DateConstants.DATE_FORMAT));
            } else {
                couponUser.setStartTime(coupon.getUseStartTime());
                couponUser.setEndTime(coupon.getUseEndTime());
            }
            return couponUser;
        }).collect(Collectors.toList());
        Boolean execute = transactionTemplate.execute(e -> {
            Boolean deduction = Boolean.TRUE;
            for (Coupon coupon : canSendCouponList) {
                deduction = deduction(coupon.getId(), 1, coupon.getIsLimited());
                if (!deduction) {
                    break;
                }
            }
            if (!deduction) {
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            deduction = couponUserService.saveBatch(couponUserList, 100);
            if (!deduction) {
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            log.error("发送新人礼,操作数据库失败，用户ID = {}, 新人礼优惠券配置 = {}", userId, newPeopleSwitchCouponStr);
            return null;
        }
        return canSendCouponList;
    }

}

