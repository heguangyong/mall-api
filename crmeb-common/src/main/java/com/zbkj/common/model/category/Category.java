package com.zbkj.common.model.category;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 分类表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_category")
@ApiModel(value="Category对象", description="分类表")
public class Category implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "父级ID")
    private Integer pid;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "类型，2 附件分类, 4 设置分类， 5 菜单分类，6 配置分类， 7 秒杀配置")
    private Integer type;

    @ApiModelProperty(value = "地址")
    private String url;

    @ApiModelProperty(value = "扩展字段 Jsos格式")
    private String extra;

    @ApiModelProperty(value = "状态, 1正常，0失效")
    private Boolean status;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "分类所属：-1 - 平台，其他-商户")
    private Integer owner;
}
