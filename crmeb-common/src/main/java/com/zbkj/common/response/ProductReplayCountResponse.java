package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商品评价数量和好评度响应对象
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
@ApiModel(value = "StoreProductReplayCountResponse对象", description = "产品评价数量和好评度响应对象")
public class ProductReplayCountResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    public ProductReplayCountResponse() {
    }

    public ProductReplayCountResponse(Integer sumCount, Integer goodCount, Integer inCount, Integer poorCount, String replyChance, Integer replyStar) {
        this.sumCount = sumCount;
        this.goodCount = goodCount;
        this.inCount = inCount;
        this.poorCount = poorCount;
        this.replyChance = replyChance;
        this.replyStar = replyStar;
    }

    @ApiModelProperty(value = "评论总数")
    private Integer sumCount;

    @ApiModelProperty(value = "好评总数")
    private Integer goodCount;

    @ApiModelProperty(value = "中评总数")
    private Integer inCount;

    @ApiModelProperty(value = "差评总数")
    private Integer poorCount;

    @ApiModelProperty(value = "好评率")
    private String replyChance;

    @ApiModelProperty(value = "评分星数")
    private Integer replyStar;


}
