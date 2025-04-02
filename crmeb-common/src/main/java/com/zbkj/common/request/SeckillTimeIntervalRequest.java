package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 秒杀时段请求对象
 * </p>
 *
 * @author HZW
 * @since 2022-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SeckillTimeIntervalRequest对象", description = "秒杀时段请求对象")
public class SeckillTimeIntervalRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "秒杀时段ID，新增时不填，编辑时必填")
    private Integer id;

    @ApiModelProperty(value = "秒杀时段名称", required = true)
    @NotBlank(message = "秒杀时段名称不能为空")
    @Length(max = 30, message = "秒杀时段名称最多30个字符")
    private String name;

    @ApiModelProperty(value = "秒杀时段开始时间", required = true)
    @NotBlank(message = "秒杀时段开始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "秒杀时段结束时间", required = true)
    @NotBlank(message = "秒杀时段结束时间不能为空")
    private String endTime;

    @ApiModelProperty(value = "状态 0=关闭 1=开启", required = true)
    @NotNull(message = "开启状态不能为空")
    @Range(min = 0, max = 1, message = "位置的开启状态")
    private Integer status;

}
