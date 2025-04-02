package com.zbkj.common.response.groupbuy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.request.ProductAttrValueRequest;
import com.zbkj.common.response.AttrValueResponse;
import com.zbkj.common.response.ProductAttrValueResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 拼团商品表
 * </p>
 *
 * @author dazongzi
 * @since 2024-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="GroupBuyActivitySku对象", description="拼团商品表")
public class GroupBuyActivitySkuResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "拼团活动id")
    private Integer groupActivityId;

    @ApiModelProperty(value = "拼团基础商品id")
    private Integer productId;

    @ApiModelProperty(value = "拼团活动价")
    private BigDecimal activePrice;

    @ApiModelProperty(value = "拼团限购数量 - 显示用")
    private Integer quotaShow;

    @ApiModelProperty(value = "拼团剩余数量 - 随减")
    private Integer quota;

    // 自定义 add
    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品 sku id")
    private Integer skuId;

    @ApiModelProperty(value = "商品 SKU")
    private List<AttrValueResponse> attrValue;

//    // 拼团中的划线价 = 基础商品的售价
//    @ApiModelProperty(value = "商品价格 = 拼团中的划线价")
//    private BigDecimal price;

    // 当前拼团商品的已经拼团多少份
    @ApiModelProperty(value = "当前商品已经拼了多少份")
    private Integer latestBuyCount;
}
