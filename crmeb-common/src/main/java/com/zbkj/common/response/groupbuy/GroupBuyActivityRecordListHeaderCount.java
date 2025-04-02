package com.zbkj.common.response.groupbuy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author stivepeim
 * @date 2024/8/14 16:12
 * @description GroupBuyActivityListHeadeNum
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "GroupBuyActivityRecordListHeaderCount", description = "拼团开团记录列表表头数量响应对象")
public class GroupBuyActivityRecordListHeaderCount implements Serializable {

    private static final long serialVersionUID = 1662889779727358289L;

    @ApiModelProperty(value = "进行中数量")
    private Integer ingNum;

    @ApiModelProperty(value = "已成功数量")
    private Integer successNum;

    @ApiModelProperty(value = "已失败数量")
    private Integer failNum;
}
