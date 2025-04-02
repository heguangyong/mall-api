package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.response.PlatformProductListResponse;
import com.zbkj.common.response.ProductActivityResponse;
import com.zbkj.common.response.ProductFrontResponse;
import com.zbkj.common.response.ProductMarketingResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
public interface ProductDao extends BaseMapper<Product> {
    /**
     * 平台端商品分页列表
     * @param map 查询参数
     */
    List<PlatformProductListResponse> getPlatformPageList(Map<String, Object> map);

    /**
     * 移动端商品列表
     * @param map 查询参数
     */
    List<ProductFrontResponse> findH5List(Map<String, Object> map);

    /**
     * 商品搜索分页列表（活动）
     * @param map 查询参数
     */
    List<ProductActivityResponse> getActivitySearchPage(Map<String, Object> map);

    /**
     * 商品搜索分页列表（营销）
     * @param map 查询参数
     */
    List<ProductMarketingResponse> getMarketingSearchPage(Map<String, Object> map);

    /**
     * 商品搜索分页列表（活动）商户
     * @param map 查询参数
     */
    List<ProductActivityResponse> getActivitySearchPageByMerchant(Map<String, Object> map);

    /**
     * 商品搜索分页列表（营销）商户
     * @param map 查询参数
     */
    List<ProductMarketingResponse> getMarketingSearchPageByMerchant(Map<String, Object> map);

    /**
     * 根据关键字获取商品所有的品牌ID
     * @param keyword 关键字
     */
    List<Integer> findProductBrandIdByKeyword(@Param(value = "keyword") String keyword);

    /**
     * 根据关键字获取商品所有的分类ID
     * @param keyword 关键字
     */
    List<Integer> findProductCategoryIdByKeyword(@Param(value = "keyword") String keyword);

    /**
     * 平台端商品列表表头数量
     * @param map 查询参数
     */
    Integer getPlatformPageCount(Map<String, Object> map);
}
