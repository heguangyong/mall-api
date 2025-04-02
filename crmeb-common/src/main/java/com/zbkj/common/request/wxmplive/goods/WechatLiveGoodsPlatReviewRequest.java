package com.zbkj.common.request.wxmplive.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/29 09:52
 * @Description: 描述对应的业务场景
 */
@Data
public class WechatLiveGoodsPlatReviewRequest {

    @ApiModelProperty(value = "审核直播商品id")
    @NotNull(message = "审核直播商品id 不能为空")
    private Integer id;

    @ApiModelProperty(value = "2=平台审核通过，3=平台审核失败")
    @Range(min = 2, max = 3)
    @NotNull(message = "平台审核状态 不能为空")
    private Integer reviewStatus;

    @ApiModelProperty(value = "审核商品不通过原因")
    private String reviewReason;
}
