package com.zbkj.common.request.wxvedio.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Auther: 大粽子
 * @Date: 2022/10/11 15:13
 * @Description: 视频号 商户草稿商品审核状态操作 request
 */
@Data
public class PayComponentDraftProductMerchantOperationReviewStatus {

    @ApiModelProperty(value = "视频号草稿商品id")
    private Integer draftProductId;

    /**
     * 1-未审核或撤回审核，2-平台审核中，3-平台审核失败，4-平台审核成功，5-微信审核中，6-微信审核失败，7-微信审核成功
     * 如上是全部状态 参数中是仅仅能操作的状态 其他的在回调中完成更新
     */
    @ApiModelProperty(value = "视频号审核状态 1-未审核或撤回审核，2-平台审核中")
    @Range(min = 1, max = 2, message = "视频号审核状态 1-未审核或撤回审核，2-平台审核中")
    @NotNull(message = "平台状态不能为空")
    private Integer platformEditStatus;

//    @ApiModelProperty(value = "视频号审核平台审核状态 拒绝原因 拒绝时必填")
//    private String  platformStatusReason;
}
