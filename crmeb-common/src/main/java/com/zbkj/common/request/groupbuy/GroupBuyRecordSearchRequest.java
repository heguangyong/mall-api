package com.zbkj.common.request.groupbuy;

import com.zbkj.common.request.UserCommonSearchRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author stivepeim
 * @date 2024/8/13 09:59
 * @description GroupBuyRecordSearchRequest
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "GroupBuyRecordSearchRequest", description = "拼团开团记录搜索请求对象")
public class GroupBuyRecordSearchRequest extends UserCommonSearchRequest {

    private static final long serialVersionUID = 824147608860688755L;

    @ApiModelProperty(value = "拼团活动名称")
    private String groupActivityName;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "商户名称,平台调用使用")
    private String merName;

    @ApiModelProperty(value = "状态：0=进行中，10=已成功，-1=已失败")
    private Integer recordStatus;
}
