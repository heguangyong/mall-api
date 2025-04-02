package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.product.ProductBrand;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品品牌表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-20
 */
public interface ProductBrandDao extends BaseMapper<ProductBrand> {

    /**
     * 查询品牌列表（通过分类）
     * @param map 查询参数
     */
    List<ProductBrand> getPageListByCategory(Map<String, Object> map);

}
