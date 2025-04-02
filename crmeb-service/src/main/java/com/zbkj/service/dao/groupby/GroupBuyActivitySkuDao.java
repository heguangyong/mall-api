package com.zbkj.service.dao.groupby;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.groupbuy.GroupBuyActivitySku;
import com.zbkj.common.response.groupbuy.GroupBuyActivityProductListForSale;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>
 * 拼团商品表 Mapper 接口
 * </p>
 *
 * @author dazongzi
 * @since 2024-08-13
 */
public interface GroupBuyActivitySkuDao extends BaseMapper<GroupBuyActivitySku> {

    /** 待废弃
     * 根据商品id获取拼团商品列表
     * @param offset Integer
     * @param limit Integer
     * @return List<GroupBuyActivitySku>
     */
    List<GroupBuyActivityProductListForSale> getListByGroupProductIdByList(
            @Param("offset")Integer offset,
            @Param("limit")Integer limit,
            @Param("showgroup")Integer showgroup);

    /**
     * 根据拼团活动id获取移动端拼团商品拼团价最低的sku
     * @param groupActivityId 拼团活动id
     * @return 拼团商品列表
     */
    GroupBuyActivitySku getFrontMinActivePriceByGroupActivityId(@Param("groupActivityId") Integer groupActivityId);

    List<GroupBuyActivitySku> findGroupBuySkuListByFront(@Param("merId") Integer merId);

    List<GroupBuyActivityProductListForSale> findGroupMerchantProductListByFront(@Param("merId") Integer merId);
}
