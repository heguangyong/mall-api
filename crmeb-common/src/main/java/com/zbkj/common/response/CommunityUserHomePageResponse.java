package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName CommunityUserHomePageResponse
 * @Description 社区用户主页响应对象
 * @Author HZW
 * @Date 2023/3/7 18:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CommunityUserHomePageResponse对象", description="社区用户主页响应对象")
public class CommunityUserHomePageResponse implements Serializable {

    private static final long serialVersionUID = -8121525449704982702L;

    @ApiModelProperty("用户ID")
    private Integer id;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("作者签名")
    private String signature;

    @ApiModelProperty("用户等级图标")
    private String userLevelIcon;

    @ApiModelProperty("是否关注")
    private Boolean isConcerned;

    @ApiModelProperty("关注数")
    private Integer concernedNum;

    @ApiModelProperty("粉丝数")
    private Integer fansNum;

    @ApiModelProperty("获赞数")
    private Integer likeNum;

}
