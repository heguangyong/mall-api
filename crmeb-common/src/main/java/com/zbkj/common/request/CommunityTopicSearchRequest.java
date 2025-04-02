package com.zbkj.common.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @ClassName CommunityTopicSearchRequest
 * @Description 社区话题查询请求对象
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommunityTopicSearchRequest对象", description = "社区话题查询请求对象")
public class CommunityTopicSearchRequest extends PageParamRequest {

    @ApiModelProperty("话题名称")
    private String name;

    @ApiModelProperty("是否推荐：1-推荐，0-不推荐")
    private Integer isHot;
}
