package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 平台端商品表头请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/7/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PlatProductTabsHeaderRequest", description = "商品表头请求对象")
public class PlatProductTabsHeaderRequest extends ProductTabsHeaderRequest implements Serializable {

    private static final long serialVersionUID = 1522492504392316445L;

    @ApiModelProperty(value = "商户ID,平台端商品列表使用")
    private Integer merId;

    @ApiModelProperty(value = "商户是否自营：0-非自营，1-自营,平台端商品列表使用")
    private Integer isSelf;
}
