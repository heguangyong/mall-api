package com.zbkj.admin.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.*;
import com.zbkj.common.response.SeckillActivityDetailResponse;
import com.zbkj.common.response.SeckillActivityPageResponse;
import com.zbkj.common.response.SeckillProductPageResponse;
import com.zbkj.common.response.SeckillTimeIntervalResponse;

import java.util.List;

/**
 * 秒杀service
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
public interface SeckillService {

    /**
     * 新增秒杀时段
     */
    Boolean createTimeInterval(SeckillTimeIntervalRequest request);

    /**
     * 编辑秒杀时段
     */
    Boolean updateTimeInterval(SeckillTimeIntervalRequest request);

    /**
     * 删除秒杀时段
     * @param id 秒杀时段ID
     */
    Boolean deleteTimeInterval(Integer id);

    /**
     * 秒杀时段列表
     * @param request 搜索参数
     */
    List<SeckillTimeIntervalResponse> getTimeIntervalList(SeckillTimeIntervalSearchRequest request);

    /**
     * 秒杀时段开关
     * @param id 秒杀时段ID
     */
    Boolean switchTimeInterval(Integer id);

    /**
     * 新增秒杀活动
     */
    Boolean createActivity(SeckillActivitySaveRequest request);

    /**
     * 秒杀活动分页列表
     */
    PageInfo<SeckillActivityPageResponse> activityPage(SeckillActivitySearchRequest request);

    /**
     * 秒杀活动详情
     */
    SeckillActivityDetailResponse activityDetail(Integer id);

    /**
     * 编辑秒杀活动
     */
    Boolean updateActivity(SeckillActivitySaveRequest request);

    /**
     * 删除秒杀活动
     * @param id 活动id
     */
    Boolean deleteActivity(Integer id);

    /**
     * 秒杀活动开关
     * @param id 活动id
     */
    Boolean switchActivity(Integer id);

    /**
     * 获取秒杀商品分页列表
     * @param request 搜索参数
     */
    PageInfo<SeckillProductPageResponse> getSeckillProductPage(SeckillProductSearchRequest request);

    /**
     * 秒杀商品设置活动价
     */
    Boolean setProductPrice(SeckillProductPriceRequest request);

    /**
     * 秒杀商品强制下架
     */
    Boolean forceDownProduct(SeckillProductBatchRequest request);

    /**
     * 秒杀商品删除
     */
    Boolean deleteProduct(SeckillProductBatchRequest request);

    /**
     * 秒杀商品上架
     */
    Boolean upProduct(SeckillProductBatchRequest request);

    /**
     * 秒杀商品下架
     */
    Boolean downProduct(SeckillProductBatchRequest request);

    /**
     * 商户端删除秒杀商品
     */
    Boolean merchantDeleteProduct(SeckillProductBatchRequest request);

    /**
     * 秒杀商品撤回审核
     * @param id 秒杀商品ID
     */
    Boolean withdrawProductAudit(Integer id);

    /**
     * 商户秒杀商品添加
     */
    Boolean merchantAddProduct(SeckillProductAddRequest request);

    /**
     * 平台秒杀商品添加
     */
    Boolean platAddProduct(SeckillProductAddRequest request);

    /**
     * 秒杀商品审核
     */
    Boolean auditProduct(ProductAuditRequest request);
}
