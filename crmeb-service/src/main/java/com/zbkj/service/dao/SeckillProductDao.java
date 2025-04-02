package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.response.SeckillProductPageResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 秒杀商品表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-12-15
 */
public interface SeckillProductDao extends BaseMapper<SeckillProduct> {

    /**
     * 秒杀商品分页列表
     * @param map
     */
    List<SeckillProductPageResponse> getSeckillProductPage(Map<String, Object> map);
}
