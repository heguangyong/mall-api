package com.zbkj.common.request.merchant;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改商户复制商品数量请求对象
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
@ApiModel(value="MerchantUpdateProductNumRequest对象", description="修改商户复制商品数量请求对象")
public class MerchantUpdateProductNumRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商户ID")
    @NotNull(message = "商户ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "修改类型：add，sub", required = true)
    @NotBlank(message = "修改类型不能为空")
    @StringContains(limitValues = {"add","sub"}, message = "未知的修改类型")
    private String type;

    @ApiModelProperty(value = "数量", required = true)
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "修改数量最小值为1")
    @Max(value = 9999, message = "修改数量最大值为9999")
    private Integer num;
}
