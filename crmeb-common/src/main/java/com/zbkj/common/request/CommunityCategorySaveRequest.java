package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @ClassName CommunityCategorySaveRequest
 * @Description 社区分类保存请求对象
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommunityCategorySaveRequest对象", description = "社区分类保存请求对象")
public class CommunityCategorySaveRequest implements Serializable {

    private static final long serialVersionUID = -1726003126817361852L;

    @ApiModelProperty("分类ID，编辑时必填")
    private Integer id;

    @ApiModelProperty(value = "分类名称", required = true)
    @NotBlank(message = "分类名称不能为空")
    @Length(max = 8, message = "分类名称最多为8个字符")
    private String name;

    @ApiModelProperty(value = "是否显示：1-显示，0-不显示", required = true)
    @NotNull(message = "显示状态不能为空")
    @Range(min = 0, max = 1, message = "未知的显示状态")
    private Integer isShow;

    @ApiModelProperty(value = "排序", required = true)
    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序最小为0")
    private Integer sort;
}
