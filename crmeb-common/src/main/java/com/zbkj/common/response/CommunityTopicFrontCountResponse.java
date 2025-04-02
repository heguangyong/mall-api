package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName CommunityTopicFrontCountResponse
 * @Description 社区话题移动端统计数据响应对象
 * @Author HZW
 * @Date 2023/3/7 18:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CommunityTopicFrontCountResponse", description="社区话题移动端统计数据响应对象")
public class CommunityTopicFrontCountResponse implements Serializable {

    private static final long serialVersionUID = -8121525449704982702L;

    @ApiModelProperty("话题ID")
    private Integer id;

    @ApiModelProperty("话题名称")
    private String name;

    @ApiModelProperty("话题笔记数量")
    private Integer noteNum;

}
