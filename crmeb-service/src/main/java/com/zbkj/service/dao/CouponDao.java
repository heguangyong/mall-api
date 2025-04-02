package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.coupon.Coupon;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
public interface CouponDao extends BaseMapper<Coupon> {

    /**
     * 获取移动端优惠券列表
     * @param map 查询参数
     */
    List<Coupon> getH5ListBySearch(Map<String, Object> map);

    /**
     * 获取可用优惠券 为DIY首页
     * @param limit 配合前端样式获取对应条数
     * @return 可用优惠券列表
     */
    List<Coupon> getCouponListForDiyHomePage(Integer limit);

    /**
     * 获取所有适用金额的商户优惠券（多商品）
     * @param map 查询参数
     */
    List<Coupon> findManyByMerIdAndMoney(Map<String, Object> map);

    /**
     * 获取所有适用金额的平台优惠券（多商品）
     * @param map 查询参数
     */
    List<Coupon> findManyPlatByMerIdAndMoney(Map<String, Object> map);
}
