package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName CommunityRecommendAuthorResponse
 * @Description 社区推荐作者响应对象
 * @Author HZW
 * @Date 2023/3/7 18:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CommunityRecommendAuthorResponse对象", description="社区推荐作者响应对象")
public class CommunityRecommendAuthorResponse implements Serializable {

    private static final long serialVersionUID = -8121525449704982702L;

    @ApiModelProperty("作者ID")
    private Integer authorId;

    @ApiModelProperty("作者头像")
    private String authorAvatar;

    @ApiModelProperty("作者昵称")
    private String authorName;

    @ApiModelProperty("作者签名")
    private String authorSignature;

    @ApiModelProperty(value = "作者等级图标")
    private String authorLevelIcon;

    @ApiModelProperty(value = "粉丝数量")
    private Integer fansNum;

    @ApiModelProperty("是否关注")
    private Boolean isConcerned;

    @ApiModelProperty("笔记列表")
    private List<CommunityNoteSimpleResponse> noteList;
}
