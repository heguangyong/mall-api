package com.zbkj.common.response.groupbuy;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 移动端商城拼团首页 response
 * @author stivepeim
 * @date 2024/8/26 10:38
 * @description GroupBuyActivityFrontResponse
 */
@Data
public class GroupBuyActivityFrontResponse {
    @ApiModelProperty(value = "全部成团订单数")
    private Integer totalAllOrderDone;

    @ApiModelProperty(value = "拼团成功人员列表")
    private List<String> orderDoneUserImages;

    @ApiModelProperty(value = "拼团商品")
    private List<GroupBuyActivityFrontItemResponse> items;
}
