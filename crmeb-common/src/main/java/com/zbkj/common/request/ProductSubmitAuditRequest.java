package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品提审请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductSubmitAuditRequest", description = "商品提审请求对象")
public class ProductSubmitAuditRequest implements Serializable {

    private static final long serialVersionUID = -6892860533641718898L;

    @ApiModelProperty(value = "商品ID", required = true)
    @NotNull(message = "商品ID 不能为空")
    private Integer id;

    @ApiModelProperty(value = "是否自动上架")
    private Boolean isAutoUp = false;

}
