package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 客服响应对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CustomerServiceResponse", description = "客服响应对象")
public class CustomerServiceResponse implements Serializable {

    private static final long serialVersionUID = -6309840320447728140L;

    @ApiModelProperty(value = "客服类型:h5,hotline,message,email")
    private String consumerType;

    @ApiModelProperty(value = "客服电话")
    private String consumerHotline;

    @ApiModelProperty(value = "客服H5链接")
    private String consumerH5Url;
}
