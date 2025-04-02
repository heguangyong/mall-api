package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 首页经营数据响应对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="HomeOperatingDataResponse对象", description="首页经营数据响应对象")
public class HomeOperatingDataResponse implements Serializable {

    private static final long serialVersionUID = -1486435421582495511L;

    @ApiModelProperty(value = "待发货订单数量")
    private Integer notShippingOrderNum;

    @ApiModelProperty(value = "待退款订单数量")
    private Integer refundingOrderNum;

    @ApiModelProperty(value = "待核销订单数量")
    private Integer awaitVerificationOrderNum;

    @ApiModelProperty(value = "待审核商品数量")
    private Integer awaitAuditProductNum;

    @ApiModelProperty(value = "在售商品数量")
    private Integer onSaleProductNum;

//    @ApiModelProperty(value = "库存预警商品数量")
//    private Integer vigilanceInventoryNum;
//
//    @ApiModelProperty(value = "上架商品数量")
//    private Integer onSaleProductNum;
//
//    @ApiModelProperty(value = "仓库中商品数量")
//    private Integer notSaleProductNum;

}
