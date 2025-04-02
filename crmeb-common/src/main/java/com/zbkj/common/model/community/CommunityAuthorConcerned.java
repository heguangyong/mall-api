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
 * 社区作者关注表
 * </p>
 *
 * @author HZW
 * @since 2023-03-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("eb_community_author_concerned")
@ApiModel(value = "CommunityAuthorConcerned对象", description = "社区作者关注表")
public class CommunityAuthorConcerned implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("作者ID")
    @TableField("author_id")
    private Integer authorId;

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;


}
