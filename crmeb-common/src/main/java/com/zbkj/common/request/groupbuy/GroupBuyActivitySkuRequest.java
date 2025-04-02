package com.zbkj.common.request.groupbuy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author stivepeim
 * @date 2024/8/13 10:09
 * @description GroupBuyActivitySkuRequest
 */
@Data
public class GroupBuyActivitySkuRequest {

    @ApiModelProperty(value = "主键 - 更新时必填")
    private Integer id;

    @ApiModelProperty(value = "拼团活动id")
    private Integer groupActivityId;

    @ApiModelProperty(value = "拼团基础商品id")
    @NotNull(message = "拼团基础商品id 不能为空")
    private Integer productId;

    @ApiModelProperty(value = "商品 sku id")
    @NotNull(message = "拼团基础商品 对应的sku id 不能为空")
    private Integer skuId;

    @ApiModelProperty(value = "拼团活动价")
    private BigDecimal activePrice;

    @ApiModelProperty(value = "拼团限购数量 - 显示用")
    @NotNull(message = "拼团限购数量 不能为空")
    private Integer quotaShow;

}
