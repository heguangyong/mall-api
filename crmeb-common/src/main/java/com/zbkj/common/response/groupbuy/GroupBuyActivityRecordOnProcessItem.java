package com.zbkj.common.response.groupbuy;

import com.zbkj.common.model.groupbuy.GroupBuyUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 正在进行中的拼团
 * @author stivepeim
 * @date 2024/9/4 17:42
 * @description GroupBuyAcituvityRecordOnProgessList
 */
@Data
public class GroupBuyActivityRecordOnProcessItem {
    @ApiModelProperty(value = "拼团成功人员列表")
    private List<GroupBuyUser> orderDoneUserImages;

    @ApiModelProperty(value = "还差几人成团")
    private Integer needSomeTogetherCount;

    @ApiModelProperty(value = "结束时间")
    private Date recordEndTime;

    @ApiModelProperty(value = "拼团活动商品id = 当前的")
    private Integer productGroupId;
}
