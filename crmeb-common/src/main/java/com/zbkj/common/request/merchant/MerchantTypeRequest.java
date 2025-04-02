package com.zbkj.common.request.merchant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 商户类型请求对象
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
@ApiModel(value="MerchantTypeRequest对象", description="商户类型请求对象")
public class MerchantTypeRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "分类ID")
    private Integer id;

    @ApiModelProperty(value = "类型名称")
    @NotEmpty(message = "类型名称不能为空")
    @Length(max = 50, message = "类型名称长度不能超过50个字符")
    private String name;

    @ApiModelProperty(value = "类型要求说明")
    @NotEmpty(message = "类型要求说明不能为空")
    @Length(max = 500, message = "类型要求说明长度不能超过500个字符")
    private String info;

}
