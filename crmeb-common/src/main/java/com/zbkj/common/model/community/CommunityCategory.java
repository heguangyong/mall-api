package com.zbkj.common.model.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 社区分类表
 * </p>
 *
 * @author HZW
 * @since 2023-03-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("eb_community_category")
@ApiModel(value = "CommunityCategory对象", description = "社区分类表")
public class CommunityCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("父级ID")
    @TableField("pid")
    private Integer pid;

    @ApiModelProperty("分类名称")
    @TableField("`name`")
    private String name;

    @ApiModelProperty("是否显示：1-显示，0-不显示")
    @TableField("is_show")
    private Integer isShow;

    @ApiModelProperty("排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("是否删除，0-未删除，1-删除")
    @TableField("is_del")
    private Integer isDel;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private Date updateTime;


}
