package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.vo.CouponSimpleVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * CouponService 接口
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
public interface CouponService extends IService<Coupon> {

    /**
     * 创建优惠券
     */
    Boolean create(CouponRequest request);

    /**
     * 优惠券详情带异常
     */
    Coupon getInfoException(Integer id);

    /**
     * 优惠券详情
     */
    CouponInfoResponse info(Integer id);

    /**
     * 扣减数量
     *
     * @param id        优惠券id
     * @param num       数量
     * @param isLimited 是否限量
     */
    Boolean deduction(Integer id, Integer num, Boolean isLimited);

    /**
     * 删除优惠券
     *
     * @param id 优惠券id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 移动端优惠券列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return List<CouponFrontResponse>
     */
    PageInfo<CouponFrontResponse> getH5List(CouponFrontSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 修改优惠券状态
     *
     * @param id 优惠券id
     */
    Boolean updateStatus(Integer id);

    /**
     * 商户端优惠券分页列表
     *
     * @param request 查询参数
     * @return PageInfo
     */
    PageInfo<Coupon> getMerchantPageList(CouponSearchRequest request);

    /**
     * 商品可用优惠券列表（商品创建时选择使用）
     *
     * @return List
     */
    List<ProductCouponUseResponse> getProductUsableList();

    /**
     * 商品券关联商品编辑
     *
     * @param request 编辑对象
     * @return Boolean
     */
    Boolean couponProductJoinEdit(CouponProductJoinRequest request);

    /**
     * 获取优惠券简单对象列表
     *
     * @param idList id列表
     * @return List
     */
    List<CouponSimpleVo> findSimpleListByIdList(List<Integer> idList);

    /**
     * 平台端添加优惠券
     *
     * @param request 请求参数
     * @return 添加结果
     */
    Boolean platformAdd(CouponAddRequest request);

    /**
     * 删除平台优惠券
     *
     * @param request 删除参数
     * @return 删除结果
     */
    Boolean platformDelete(CouponDeleteRequest request);

    /**
     * 操作优惠券开关
     *
     * @param id 优惠券ID
     */
    Boolean switchById(Integer id);

    /**
     * 获取平台优惠券详情
     *
     * @param id 优惠券ID
     */
    CouponAdminDetailResponse getPlatformDetail(Integer id);

    /**
     * 平台端优惠券分页列表
     *
     * @param request 请求参数
     */
    PageInfo<CouponPageResponse> getPlatformPageList(CouponSearchRequest request);

    /**
     * 平台批量发送优惠券
     *
     * @param request 发送参数
     * @return 发送结果
     */
    Boolean platformBatchSend(CouponBatchSendRequest request);

    /**
     * 平台端可发送优惠券列表
     */
    PageInfo<Coupon> getPlatformCanSendList(CommonSearchRequest request);

    /**
     * 移动端优惠券领券中心
     *
     * @param request 请求参数
     * @return
     */
    PageInfo<CouponCenterPageResponse> getCouponCenter(CouponCenterSearchRequest request);

    /**
     * 通过品牌删除优惠券
     *
     * @param brandId 品牌ID
     */
    Boolean deleteByBrandId(Integer brandId);

    /**
     * 通过商品分类删除优惠券
     *
     * @param proCategoryId 商品分类ID
     */
    Boolean deleteByProCategoryId(Integer proCategoryId);

    /**
     * 优惠券编辑
     * @param request 请求参数
     */
    Boolean edit(CouponUpdateRequest request);

    /**
     * 获取商品详情优惠券数据
     * @param proId 商品ID
     * @param limit 查询条数
     */
    List<Coupon> findProductDetailLimit(Integer proId, Integer limit);

    /**
     * 通过ID获取优惠券
     * @param couponIdList 优惠券ID列表
     */
    List<Coupon> findByIds(List<Integer> couponIdList);

    /**
     * 获取所有适用金额的商户优惠券（多商品）
     * @param merId 商户ID
     * @param proIdList 商品ID列表
     * @param minPrice 金额
     */
    List<Coupon> findManyByMerIdAndMoney(Integer merId, List<Integer> proIdList, BigDecimal minPrice);

    /**
     * 根据diy首页优惠券组件 获取对应优惠券你列表
     *
     * @param limit 多少条
     * @return 优惠券列表
     */
    List<CouponFrontResponse> getCouponListForDiyPageHome(Integer limit);

    /**
     * 获取所有适用金额的平台优惠券(多商品)
     * @param proIdList 商品ID列表
     * @param proCategoryIdList 商品分类ID列表
     * @param merIdList 商户ID列表
     * @param brandIdList 品牌ID列表
     * @param price 金额
     */
    List<Coupon> findManyPlatByMerIdAndMoney(List<Integer> proIdList, List<Integer> proCategoryIdList, List<Integer> merIdList, List<Integer> brandIdList, BigDecimal price);


    /**
     * 发放新人礼
     * @param userId 用户ID
     */
    List<Coupon> sendNewPeopleGift(Integer userId);
}
