package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.product.ProductCategory;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * <p>
 * 商品分类表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-20
 */
public interface ProductCategoryDao extends BaseMapper<ProductCategory> {

    /**
     * 获取首页第三级分类数据
     * @param firstId 第一级分类id
     */
    List<ProductCategory> getThirdCategoryByFirstId(@Param("firstId") Integer firstId, @Param("limit") Integer limit);
}
