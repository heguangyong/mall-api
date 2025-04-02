package com.zbkj.service.service.groupbuy;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.groupbuy.GroupBuyActivitySku;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.groupbuy.MerchantGroupPageRequest;
import com.zbkj.common.response.ProductDetailResponse;
import com.zbkj.common.response.groupbuy.GroupBuyActivityFrontOrderViewResponse;
import com.zbkj.common.response.groupbuy.GroupBuyActivityProductListForSale;
import com.zbkj.common.response.groupbuy.GroupBuyActivitySkuForSaleListResponse;

import java.util.List;

/**
* @author dazongzi
* @description GroupBuyActivitySkuService 接口
* @date 2024-08-13
*/
public interface GroupBuyActivitySkuService extends IService<GroupBuyActivitySku> {

    /**
     * 根据拼团活动id获取拼团商品列表
     * @param groupActivityId 拼团活动id
     * @return 拼团商品列表
     */
    List<GroupBuyActivitySku> getListByGroupActivityId(Integer groupActivityId);


    /**
     * 审核拼团商品状态 同步写入
     * @param id sku ID
     * @param op 审核状态
     * @return 执行结果
     */
    Boolean reviewGroupBuySkuStatus(Integer id, Boolean op);

    ////////////////////////////////////////////////////////// 商城方法

    /**
     * 获取拼团商品详情
     * @param productId 商品id
     * @return ProductDetailResponse
     */
    ProductDetailResponse getGroupBuyDetail(Integer productId, Integer groupActivityId, Integer groupRecordId);

    /**
     * 根据商品id获取拼团商品列表
     * @param paramRequest 分页参数
     * @return List<GroupBuyActivitySku>
     */
    List<GroupBuyActivityProductListForSale> getListByGroupProductIdByList(PageParamRequest paramRequest, Integer showgroup);

    /**
     * 根据 attrId 和 活动id 获取拼团商品
     * @param activityId 拼团活动id
     * @param attrId sku id
     * @return 拼团活动数据
     */
    GroupBuyActivitySku getByAttrId(Integer activityId, Integer attrId);

    /**
     * 减少拼团商品库存
     * @param groupBuyActivityId 拼团活动id
     * @param attrValueId skuId
     * @param payNum 购买数量
     */
    void subSKUStock(Integer groupBuyActivityId, Integer attrValueId, Integer payNum);

    /**
     * 回滚库存
     * @param groupBuyActivityId 拼团活动id
     * @param skuId skuId
     * @param payNum 购买数量
     */
    void rollBackSKUStock(Order order, Integer groupBuyActivityId, Integer skuId, Integer payNum);

    /**
     * 拼团订单中查看拼团活动
     * @param orderNo 订单编号
     * @return 查询到的拼团活动信息
     */
    GroupBuyActivityFrontOrderViewResponse getGroupBuyActivityFrontForShare(String orderNo);

    /**
     * 从分享链接过来查看拼团详情
     * @param groupActivityId 活动id
     * @param productId 商品id
     * @return 拼团活动详情信息
     */
    GroupBuyActivityFrontOrderViewResponse getGroupBuyActivityFrontForReadShare(Integer groupActivityId, Integer productId, Integer recordId);

    /**
     * 根据 skuIds 删除对应的拼团商品属性
     * @param finalSkuIdList 拼团商品属性id集合
     */
    void deleteSkusIdsForProductTurnOff(List<Integer> finalSkuIdList);

    /**
     * 根据拼团活动id获取移动端拼团商品sku列表
     * @param groupActivityId 拼团活动id
     * @return 拼团商品列表
     */
    GroupBuyActivitySku getFrontMinActivePriceByGroupActivityId(Integer groupActivityId);

    /**
     * 商户拼团商品分页列表
     */
    PageInfo<GroupBuyActivityProductListForSale> findMerchantProductPageByFront(MerchantGroupPageRequest request);
}
