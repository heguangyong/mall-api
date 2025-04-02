package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品券关联商品修改对象
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
@ApiModel(value = "CouponProductJoinRequest对象", description = "商品券关联商品修改对象")
public class CouponProductJoinRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "优惠券id")
    @NotNull(message = "优惠券ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "商品id，英文逗号分隔，商品券专用字段")
    @NotEmpty(message = "关联商品不能为空")
    private String productIds;

}
