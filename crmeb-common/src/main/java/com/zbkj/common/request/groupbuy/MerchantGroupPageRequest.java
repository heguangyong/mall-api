package com.zbkj.common.request.groupbuy;

import com.zbkj.common.constants.Constants;
import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商户拼团商品分页请求参数（移动端）
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/12/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantGroupPageRequest", description = "商户拼团商品分页请求参数")
public class MerchantGroupPageRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = 5242151556261915833L;

    @ApiModelProperty(value = "商户ID", required = true)
    @NotNull(message = "请选择商户")
    private Integer merId;

}
