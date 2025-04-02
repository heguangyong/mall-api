package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.coupon.CouponUser;
import com.zbkj.common.request.*;
import com.zbkj.common.response.CouponUserOrderResponse;
import com.zbkj.common.response.CouponUserResponse;
import com.zbkj.common.response.UserCouponResponse;
import com.zbkj.common.vo.MyRecord;

import java.math.BigDecimal;
import java.util.List;

/**
 * CouponUserService 接口
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
public interface CouponUserService extends IService<CouponUser> {

    /**
     * 批量使用优惠券
     * @param couponIdList 优惠券Id列表
     * @return Boolean
     */
    Boolean useBatch(List<Integer> couponIdList);

    /**
     * 优惠券发放记录
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<CouponUserResponse> getPageList(CouponUserSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据预下单号获取可用优惠券
     * @param request 预下单参数
     * @return 可用优惠券集合
     */
    List<CouponUserOrderResponse> getListByPreOrderNo(OrderUseCouponRequest request);

    /**
     * 优惠券过期定时任务
     */
    void overdueTask();

    /**
     * 用户领取优惠券
     * @param cid 优惠券id
     */
    Boolean receiveCoupon(Integer cid);

    /**
     * 支付成功赠送处理
     * @param couponId 优惠券编号
     * @param uid  用户uid
     * @return MyRecord
     */
    MyRecord paySuccessGiveAway(Integer couponId, Integer uid);

    /**
     * 获取可用优惠券数量
     * @param uid 用户uid
     */
    Integer getUseCount(Integer uid);

    /**
     * 我的优惠券列表
     * @return PageInfo<StoreCouponUserResponse>
     */
    PageInfo<UserCouponResponse> getMyCouponList(MyCouponRequest request);

    /**
     * 回退优惠券（到未使用状态）
     * @param couponIdList 优惠券id
     * @return 回退结果
     */
    Boolean rollbackByIds(List<Integer> couponIdList);

    /**
     * 优惠券失效处理（通过优惠券ID）
     */
    Boolean loseEfficacyByCouponId(Integer couponId);

    /**
     * 获取优惠券已使用数量
     * @param couponId 优惠券ID
     * @return 优惠券已使用数量
     */
    Integer getUsedNumByCouponId(Integer couponId);

    /**
     * 获取优惠券列表，通过优惠券ID和用户ID列表
     * @param couponId 优惠券ID
     * @param uidList 用户ID列表
     */
    List<CouponUser> findByCouponIdAndUidList(Integer couponId, List<Integer> uidList);

    /**
     * 平台端优惠券领取列表
     */
    PageInfo<CouponUserResponse> getPlatformList(CouponReceiveRecordSearchRequest request);

    /**
     * 获取用户领取的优惠券
     * @param couponId 优惠券ID
     * @param userId 用户ID
     */
    CouponUser getLastByCouponIdAndUid(Integer couponId, Integer userId);

    /**
     * 自动领取优惠券
     */
    Integer autoReceiveCoupon(Coupon coupon, Integer uid);

    /**
     * 查询适用的优惠券列表(多商品)
     * @param userId 用户ID
     * @param merId 商户ID
     * @param proIdList 商品ID列表
     * @param money 金额
     */
    List<CouponUser> findManyByUidAndMerIdAndMoneyAndProList(Integer userId, Integer merId, List<Integer> proIdList, BigDecimal money);

    /**
     * 查询适用的平台优惠券列表(多商品)
     * @param userId 用户ID
     * @param proIdList 商品ID列表
     * @param proCategoryIdList 商品分类ID列表
     * @param merIdList 商户ID列表
     * @param brandIdList 品牌ID列表
     * @param money 金额
     */
    List<CouponUser> findManyPlatByUidAndMerIdAndMoneyAndProList(Integer userId, List<Integer> proIdList, List<Integer> proCategoryIdList, List<Integer> merIdList, List<Integer> brandIdList, BigDecimal money);

    Boolean userIsCanReceiveCoupon(Coupon coupon, Integer userId);
}
