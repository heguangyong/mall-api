package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商品平台搜索对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/7/13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PlatProductSearchRequest", description = "商品平台搜索对象")
public class PlatProductSearchRequest extends ProductSearchRequest {

    private static final long serialVersionUID = -4572464885363357010L;

    @ApiModelProperty(value = "商户ID,平台端商品列表使用")
    private Integer merId;

    @ApiModelProperty(value = "商户是否自营：0-非自营，1-自营,平台端商品列表使用")
    private Integer isSelf;
}
