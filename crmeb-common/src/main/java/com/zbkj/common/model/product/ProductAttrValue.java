package com.zbkj.common.model.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 商品属性值表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_product_attr_value")
@ApiModel(value = "ProductAttrValue对象", description = "商品属性值表")
public class ProductAttrValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品属性sku")
    private String sku;

    @ApiModelProperty(value = "属性对应的库存")
    private Integer stock;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "图片")
    private String image;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

    @ApiModelProperty(value = "商品条码")
    private String barCode;

    @ApiModelProperty(value = "原价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "一级返佣")
    private Integer brokerage;

    @ApiModelProperty(value = "二级返佣")
    private Integer brokerageTwo;

    @ApiModelProperty(value = "基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品")
    private Integer type;

    @ApiModelProperty(value = "活动限购数量")
    private Integer quota;

    @ApiModelProperty(value = "活动限购数量显示")
    private Integer quotaShow;

    @ApiModelProperty(value = "attr_values 创建更新时的属性对应")
    private String attrValue;

    @ApiModelProperty(value = "是否删除,0-否，1-是")
    private Boolean isDel;

    @ApiModelProperty(value = "并发版本控制")
    private Integer version;

    @ApiModelProperty(value = "主商品attrValueID")
    private Integer masterId;

    @ApiModelProperty(value = "是否回滚库存：0-未回滚，1-已回滚")
    private Boolean isCallback;

    @ApiModelProperty(value = "拓展文本，例云盘链接")
    private String expand;

    @ApiModelProperty(value = "卡密库ID")
    private Integer cdkeyId;

    @ApiModelProperty(value = "会员价格")
    private BigDecimal vipPrice;

    @ApiModelProperty(value = "营销类型：0=基础商品,1=秒杀,2=拼团")
    private Integer marketingType;

    @ApiModelProperty(value = "兑换积分")
    private Integer redeemIntegral;

    @ApiModelProperty(value = "是否显示")
    private Boolean isShow;

    @ApiModelProperty(value = "是否默认")
    private Boolean isDefault;

    @ApiModelProperty(value = "商品编号")
    private String itemNumber;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否付费会员商品")
    private Boolean isPaidMember;

}
