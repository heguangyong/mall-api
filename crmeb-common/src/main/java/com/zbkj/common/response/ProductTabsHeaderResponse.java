package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商品表头响应对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProductTabsHeaderResponse对象", description="商品表头响应对象")
public class ProductTabsHeaderResponse {

    @ApiModelProperty(value = "商品数量")
    private Integer count;

    @ApiModelProperty(value = "类型（1：出售中（已上架），2：仓库中（未上架），3：已售罄，4：警戒库存，5：回收站,6:待审核，7：审核失败，8：待提审）")
    private Integer type;

    public ProductTabsHeaderResponse() {
    }

    public ProductTabsHeaderResponse(Integer count, Integer type) {
        this.count = count;
        this.type = type;
    }
}
