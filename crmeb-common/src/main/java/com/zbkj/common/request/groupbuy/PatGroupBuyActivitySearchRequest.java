package com.zbkj.common.request.groupbuy;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author stivepeim
 * @date 2024/8/13 09:58
 * @description GroupBuyActivitySearchRequest
 */
@Data
public class PatGroupBuyActivitySearchRequest {

    @ApiModelProperty(value = "拼团活动名称")
    private String groupName;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "活动进程  0=未开始 1=进行中 2=已结束")
    private Integer groupProcess;

    @ApiModelProperty(value = "控制状态 0=初始化 1=已拒绝 2=已撤销 3=待审核 4=已通过")
    @Range(min = 0, max = 4, message = "控制状态 0=初始化 1=已拒绝 2=已撤销 3=待审核 4=已通过")
    private Integer groupStatus = 0;

    @ApiModelProperty(value = "商户分类ID")
    private Integer categoryId;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "活动状态:0关闭，1开启")
    private Integer activityStatus;

}
