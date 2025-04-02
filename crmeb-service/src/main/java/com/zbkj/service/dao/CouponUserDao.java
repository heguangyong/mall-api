package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.coupon.CouponUser;
import com.zbkj.common.response.CouponUserOrderResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户优惠券表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
public interface CouponUserDao extends BaseMapper<CouponUser> {

    /**
     * 获取预下单可用优惠券
     */
    List<CouponUserOrderResponse> findListByPreOrder(Map<String, Object> map);

    /**
     * 查询适用的优惠券列表(多商品)
     */
    List<CouponUser> findManyByUidAndMerIdAndMoneyAndProList(Map<String, Object> map);

    /**
     * 查询适用的平台优惠券列表(多商品)
     */
    List<CouponUser> findManyPlatByUidAndMerIdAndMoneyAndProList(Map<String, Object> map);

    /**
     * 优惠券领取记录（平台端）
     */
    List<CouponUser> findCouponReceiveRecordListByPlat(Map<String, Object> map);
}
