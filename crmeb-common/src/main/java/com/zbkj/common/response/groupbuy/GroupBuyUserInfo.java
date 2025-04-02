package com.zbkj.common.response.groupbuy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author stivepeim
 * @date 2024/9/23 21:24
 * @description GroupBuyUserInfo
 */
@Data
public class GroupBuyUserInfo {

    @ApiModelProperty(value = "id主键")
    private Integer id;

    @ApiModelProperty(value = "昵称")
    private String groupNickname;

    @ApiModelProperty(value = "头像")
    private String groupAvatar;
}
