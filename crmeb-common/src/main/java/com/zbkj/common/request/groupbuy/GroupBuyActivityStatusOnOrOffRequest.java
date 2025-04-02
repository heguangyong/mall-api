package com.zbkj.common.request.groupbuy;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.annotation.Resource;

/**
 * @author stivepeim
 * @date 2024/8/20 10:21
 * @description GroupBuyActivityStatusOnOrOffRequest
 */
@Data
public class GroupBuyActivityStatusOnOrOffRequest {

    @ApiModelProperty(value = "拼团活动id")
    private Integer id;

    @ApiModelProperty(value = "拼团活动 待设置的状态 0=关闭 1=开启")
    @Range(min = 0, max = 1, message = "拼团活动 待设置的状态 0=关闭 1=开启")
    private Integer status;
}
