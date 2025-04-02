package com.zbkj.common.request.wxmplive.room;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/30 10:22
 * @Description: 直播间审核
 */
@Data
public class WechatMpLiveRoomReviewRequest {

    @ApiModelProperty(value = "待审核直播间本地id", required = true)
    private Integer id;

    @ApiModelProperty(value = "审核状态 0=商户创建/撤回,1=平台待审核/商户重新提交审核，2=平台审核通过，3=平台审核失败,4=微信审核成功，5=微信审核失败", required = true)
    @Range(min = 2, max = 3, message = "平台审核只能操作 拒绝或者通过")
    private Integer reviewStatus;

    @ApiModelProperty(value = "审核状态拒绝原因 拒绝时必填")
    private String reviewReason;
}
