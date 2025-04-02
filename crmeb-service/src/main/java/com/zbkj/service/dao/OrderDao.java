package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.response.IntegralOrderPageResponse;
import com.zbkj.common.response.MerchantOrderPageResponse;
import com.zbkj.common.response.PlatformOrderPageResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
public interface OrderDao extends BaseMapper<Order> {

    /**
     * 获取用户购买的商品数量
     * @param uid 用户ID
     * @param proId 商品ID
     * @param marketingType 营销类型
     */
    Integer getProductNumCount(@Param(value = "uid") Integer uid, @Param(value = "proId") Integer proId, @Param(value = "marketingType") Integer marketingType);

    /**
     * 获取移动端订单列表
     * @param searchMap 搜索参数
     */
    List<Order> findFrontList(Map<String, Object> searchMap);

    List<PlatformOrderPageResponse> getPlatformAdminPage(Map<String, Object> map);

    List<MerchantOrderPageResponse> getMerchantAdminPage(Map<String, Object> map);

    Integer getIntegralOrderCount(Map<String, Object> map);

    List<IntegralOrderPageResponse> findIntegralOrderPageByPlat(Map<String, Object> map);

    Integer getMerchantAdminPageCount(Map<String, Object> map);

    Integer getPlatformAdminPageCount(Map<String, Object> map);
}
