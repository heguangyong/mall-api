package com.zbkj.common.response.groupbuy;

import com.zbkj.common.model.groupbuy.GroupBuyRecord;
import com.zbkj.common.model.groupbuy.GroupBuyUser;
import com.zbkj.common.response.ProductDetailResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 拼团订单显示响应对象
 * @author stivepeim
 * @date 2024/9/19 19:44
 * @description GroupBuyActivityFrontOrderViewResponse
 */
@Data
public class GroupBuyActivityFrontOrderViewResponse {

    @ApiModelProperty(value = "拼团订单详情中分享所需")
    private GroupBuyActivityRecordForFrontShareUse recordForShare;

    @ApiModelProperty(value = "拼团订单详情中正在拼团的商品")
    private ProductDetailResponse productDetailResponse;

    @ApiModelProperty(value = "当前正在参与当前拼团的用户")
    private List<GroupBuyUser> groupBuyUserList;

    @ApiModelProperty(value = "当前活动历史拼团成功的")
    private List<GroupBuyUserInfo> groupBuyUserActivityDoneList;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;
}
