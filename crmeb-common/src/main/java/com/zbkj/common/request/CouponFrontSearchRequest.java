package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 优惠卷搜索请求对象
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
@ApiModel(value = "CouponSearchRequest对象", description = "优惠卷搜索请求对象")
public class CouponFrontSearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类别 0-所有 1-商家券, 2-商品券, 3-通用券，4-品类券，5-品牌券，6-跨店券", required = true)
    @NotNull(message = "优惠券类别不能为空")
    @Range(min = 0, max = 6, message = "未知的优惠券类别")
    private Integer category;

    @ApiModelProperty(value = "产品id,与merId二选一")
    private Integer productId;

    @ApiModelProperty(value = "商户id,与productId二选一")
    private Integer merId;
}
