package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName CommunityNoteSimpleResponse
 * @Description 社区笔记简易响应对象
 * @Author HZW
 * @Date 2023/3/7 18:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CommunityNoteSimpleResponse对象", description="社区笔记简易响应对象")
public class CommunityNoteSimpleResponse implements Serializable {

    private static final long serialVersionUID = -8121525449704982702L;

    @ApiModelProperty("笔记ID")
    private Integer id;

    @ApiModelProperty("封面")
    private String cover;

    @ApiModelProperty("笔记类型：1-图文，2-视频")
    private Integer type;
}
