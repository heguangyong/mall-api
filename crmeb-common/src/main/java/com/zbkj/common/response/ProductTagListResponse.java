package com.zbkj.common.response;

import lombok.Data;

import java.util.List;

/**
 * @Auther: 大粽子
 * @Date: 2023/10/21 16:42
 * @Description: 描述对应的业务场景
 */
@Data
public class ProductTagListResponse {

    // 爆品
    private List<ProductTagTaskResponse> hotGoods;
    // 自营
    private List<ProductTagTaskResponse> isSelfGoods;
    // 热卖
    private List<ProductTagTaskResponse> hotSaleGoods;
    // 优选
    private List<ProductTagTaskResponse> preferredGoods;
    // 包邮
    private List<ProductTagTaskResponse> freeShippingGoods;

}
