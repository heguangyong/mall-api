package com.zbkj.service.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.IntegralIntervalProductRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.IntegralIntervalResponse;
import com.zbkj.common.response.IntegralProductFrontResponse;
import com.zbkj.common.vo.MyRecord;

import java.util.List;

/**
 * 积分商城服务类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/20
 */
public interface IntegralShoppingService {

    /**
     * 获取用户积分数据
     */
    MyRecord getUserIntegralInfo();

    /**
     * 积分商品热门推荐分页列表
     */
    PageInfo<IntegralProductFrontResponse> findProductHotPage(PageParamRequest request);

    /**
     * 获取积分区间列表
     */
    List<IntegralIntervalResponse> getIntegralIntervalList();

    /**
     * 积分商品分页列表(积分区间)
     */
    PageInfo<IntegralProductFrontResponse> findIntegralIntervalProductPage(IntegralIntervalProductRequest request);
}
