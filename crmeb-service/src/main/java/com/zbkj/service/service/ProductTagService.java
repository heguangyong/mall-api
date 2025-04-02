package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.ProductTag;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.producttag.ProductTagSearchRequest;
import com.zbkj.common.response.ProductTagsFrontResponse;
import com.zbkj.common.response.productTag.ProductTagsForSearchResponse;

import java.util.List;
import java.util.Map;

/**
* @author dazongzi
* @description ProductTagService 接口
* @date 2023-10-11
*/
public interface ProductTagService extends IService<ProductTag> {

    /**
     * 列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @author dazongzi
     * @since 2023-10-11
     * @return List<ProductTag>
     */
    List<ProductTag> getList(ProductTagSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 编辑商品标签
     * @param productTag 标签对象
     * @return 编辑结果
     */
    Boolean editProductTag(ProductTag productTag);

    /**
     * 新增商品标签
     * @param productTag 待新增对象
     * @return 新增结果
     */
    Boolean saveProductTag(ProductTag productTag);

    /**
     * 更新商品标签
     * @param id 标签id
     * @param status 状态#0关闭|1开启
     * @return 结果
     */
    Boolean updateStatus(Integer id, Integer status);

    /**
     * 删除商品标签 仅仅能删除平台自建的
     * @param id 待删除id
     * @return 删除结果
     */
    Boolean deleteProductTag(Integer id);

    /**
     * 根据商品数据设置对应标签
     * @param productId 商品id
     * @param productTags 标签对象
     * @return
     */
    ProductTagsFrontResponse setProductTagByProductTagsRules(Integer productId,Integer brandId,Integer merId,Integer cateId, ProductTagsFrontResponse productTags);

    /**
     * 获取已经开启的商品标签列表
     * @return 商品标签列表
     */
    List<ProductTag> getStatusOnProductTagList();

    /**
     * 根据商品标签id加载商品id集合
     * @param tagId 标签id
     * @return 商品id集合
     */
    ProductTagsForSearchResponse getProductIdListByProductTagId(Integer tagId);
}
