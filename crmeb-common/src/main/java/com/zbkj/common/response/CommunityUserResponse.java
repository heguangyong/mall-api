package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName CommunityUserResponse
 * @Description 社区用户响应对象
 * @Author HZW
 * @Date 2023/3/7 18:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CommunityUserResponse对象", description="社区用户响应对象")
public class CommunityUserResponse implements Serializable {

    private static final long serialVersionUID = -8121525449704982702L;

    @ApiModelProperty("用户ID")
    private Integer id;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("用户签名")
    private String signature;

    @ApiModelProperty("用户等级图标")
    private String userLevelIcon;

    @ApiModelProperty("是否关注")
    private Boolean isConcerned;

    @ApiModelProperty("粉丝数")
    private Integer fansNum;

    @ApiModelProperty("是否关注粉丝")
    private Boolean isFansConcerned = false;

    @ApiModelProperty(value = "是否注销")
    private Boolean isLogoff;
}
