package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 订单商品评价请求对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderProductReplyRequest对象", description = "订单商品评价请求对象")
public class OrderProductReplyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单编号", required = true)
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "订单详情id", required = true)
    @NotNull(message = "订单详情id不能为空")
    private Integer orderDetailId;

    @ApiModelProperty(value = "评价星级", example = "5", required = true)
    @NotNull(message = "评价星级不能为空")
    @Range(min = 1, max = 5, message = "评价星级为1~5")
    private Integer star;

    @ApiModelProperty(value = "评论内容", required = true)
//    @NotBlank(message = "评论内容不能为空") 公司统一去掉售后提交时文字的必填限制
    @Length(max = 512, message = "评论内容不能超过512个字符")
    private String comment;

    @ApiModelProperty(value = "评论图片")
    private List<String> pics;
}
