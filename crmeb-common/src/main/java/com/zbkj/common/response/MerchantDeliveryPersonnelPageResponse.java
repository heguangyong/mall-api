package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 商户配送人员分页列表响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/11/4
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantDeliveryPersonnelPageResponse", description = "商户配送人员分页列表响应对象")
public class MerchantDeliveryPersonnelPageResponse implements Serializable {

    private static final long serialVersionUID = -6300544818680629462L;

    @ApiModelProperty(value = "配送人员ID")
    private Integer id;

    @ApiModelProperty(value = "配送人员名称")
    private String personnelName;

    @ApiModelProperty(value = "配送人员手机号")
    private String personnelPhone;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
