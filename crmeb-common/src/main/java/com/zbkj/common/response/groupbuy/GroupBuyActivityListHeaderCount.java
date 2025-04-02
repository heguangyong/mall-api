package com.zbkj.common.response.groupbuy;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author stivepeim
 * @date 2024/8/14 16:12
 * @description GroupBuyActivityListHeadeNum
 */
@Data
public class GroupBuyActivityListHeaderCount {

    @ApiModelProperty(value = "控制状态 0=初始化 1=已拒绝 2=已撤销 3=待审核 4=已通过")
    private Integer groupStatus;

    @ApiModelProperty(value = "状态对应数量")
    private Integer count;

    public GroupBuyActivityListHeaderCount(Integer groupStatus, Integer count) {
        this.groupStatus = groupStatus;
        this.count = count;
    }
}
