package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商户配送人员搜索请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/11/4
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MerchantDeliveryPersonnelSearchRequest", description="商户配送人员搜索请求对象")
public class MerchantDeliveryPersonnelSearchRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = 6223759431190053373L;

    @ApiModelProperty(value = "配送人员姓名")
    private String personnelName;

    @ApiModelProperty(value = "配送人员手机号")
    private String personnelPhone;

    @ApiModelProperty(value = "创建时间")
    private String dateLimit;
}
