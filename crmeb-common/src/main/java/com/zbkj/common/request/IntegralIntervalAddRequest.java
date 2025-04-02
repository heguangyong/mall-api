package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 积分区间添加请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "IntegralIntervalAddRequest", description = "积分区间添加请求对象")
public class IntegralIntervalAddRequest implements Serializable {

    private static final long serialVersionUID = -1985755515799281392L;

    @ApiModelProperty(value = "区间id|添加时不填，修改时必填")
    private Integer id;

    @ApiModelProperty(value = "区间名称", required = true)
    @NotBlank(message = "区间名称不能为空")
    @Length(max = 128, message = "商品名称长度不能超过16个字符")
    private String name;

    @ApiModelProperty(value = "区间值", required = true)
    @NotBlank(message = "区间值不能为空")
    private String value;

    @ApiModelProperty(value = "排序", required = true)
    @NotNull(message = "排序值不能为空")
    @Min(value = 0, message = "排序值不能小于0")
    @Max(value = 99, message = "排序值不能大于99")
    private Integer sort;

    @ApiModelProperty(value = "区间状态", required = true)
    @NotNull(message = "区间状态不能为空")
    private Boolean status;
}
