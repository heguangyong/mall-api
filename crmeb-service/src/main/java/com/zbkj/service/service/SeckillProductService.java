package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.request.SeckillProductSearchRequest;
import com.zbkj.common.response.SeckillProductPageResponse;

import java.util.List;

/**
*  SeckillProductService 接口
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
public interface SeckillProductService extends IService<SeckillProduct> {

    /**
     * 获取活动秒杀商品数量
     * @param activityId 活动ID
     * @return 活动秒杀商品数量
     */
    Integer getCountByActivityId(Integer activityId);

    /**
     * 获取秒杀活动商品
     * @param activityId 活动ID
     */
    List<SeckillProduct> findByActivityId(Integer activityId);

    /**
     * 获取秒杀商品分页列表
     * @param request 搜索参数
     */
    PageInfo<SeckillProductPageResponse> getSeckillProductPage(SeckillProductSearchRequest request);

    /**
     * 强制下架
     * @param ids 商品id，英文逗号拼接
     */
    Boolean forceDown(String ids);

    /**
     * 删除
     * @param ids 商品id，英文逗号拼接
     */
    Boolean delete(String ids);

    /**
     * 上架
     * @param ids 商品id，英文逗号拼接
     */
    Boolean up(String ids, Integer merId);

    /**
     * 下架
     * @param ids 商品id，英文逗号拼接
     */
    Boolean down(String ids, Integer merId);

    /**
     * 首页秒杀商品列表
     * @param aidList 秒杀活动ID列表
     * @return 首页秒杀商品列表
     */
    List<SeckillProduct> getIndexList(List<Integer> aidList);

    /**
     * 移动端商户秒杀商品列表
     * @param seckillIdList 秒杀ID列表
     * @param merId 商户ID，0-全平台
     * @param pageNum 页码
     * @param limit 每页数量
     * @return 秒杀商品列表
     */
    PageInfo<SeckillProduct> getFrontPage(List<Integer> seckillIdList, Integer merId, Integer pageNum, Integer limit);

    /**
     * 获取移动端秒杀商品详情
     * @param id 秒杀商品ID
     * @return 秒杀商品详情
     */
    SeckillProduct getFrontDetail(Integer id);

    /**
     * 添加/扣减库存
     * @param id 商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationStock(Integer id, Integer num, String type);

    /**
     * 根据主商品删除秒杀商品
     * @param masterId 主商品ID
     */
    Boolean deleteByProductId(Integer masterId);

    /**
     * 通过商户下架所有秒杀商品
     * @param merId 商户ID
     */
    Boolean downByMerId(Integer merId);

    /**
     * 通过基础商品ID下架秒杀商品
     * @param productId 基础商品ID
     */
    Boolean downByProductId(Integer productId);

    /**
     * 通过基础商品ID下架秒杀商品
     * @param productIdList 基础商品ID列表
     */
    Boolean downByProductIdList(List<Integer> productIdList);
}