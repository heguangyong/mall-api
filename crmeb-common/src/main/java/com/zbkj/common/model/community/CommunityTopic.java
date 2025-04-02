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
 * 社区话题表
 * </p>
 *
 * @author HZW
 * @since 2023-03-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("eb_community_topic")
@ApiModel(value = "CommunityTopic对象", description = "社区话题表")
public class CommunityTopic implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("话题名称")
    @TableField("`name`")
    private String name;

    @ApiModelProperty("是否推荐：1-推荐，0-不推荐")
    @TableField("is_hot")
    private Integer isHot;

    @ApiModelProperty("使用次数")
    @TableField("count_use")
    private Integer countUse;

    @ApiModelProperty("浏览量")
    @TableField("count_view")
    private Integer countView;

    @ApiModelProperty("排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("是否删除，0-未删除，1-删除")
    @TableField("is_del")
    private Integer isDel;

    @ApiModelProperty("创建类型：1-系统创建，2-用户创建")
    @TableField("create_type")
    private Integer createType;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private Date updateTime;


}
