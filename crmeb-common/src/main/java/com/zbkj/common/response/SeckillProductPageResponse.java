package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zbkj.common.model.product.ProductAttrValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 秒杀商品分页列表响应对象
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
@ApiModel(value = "SeckillProductPageResponse对象", description = "秒杀商品分页列表响应对象")
public class SeckillProductPageResponse {

    @ApiModelProperty(value = "秒杀商品ID")
    private Integer id;

    @ApiModelProperty(value = "活动id")
    private Integer activityId;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品原价")
    private BigDecimal price;

    @ApiModelProperty(value = "商品秒杀价")
    private BigDecimal seckillPrice;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "规格 0单 1多")
    private Boolean specType;

    @ApiModelProperty(value = "状态（0：未上架，1：上架）")
    private Boolean isShow;

    @ApiModelProperty(value = "审核状态：1-待审核，2-审核成功，3-审核拒绝")
    private Integer auditStatus;

    @ApiModelProperty(value = "拒绝原因")
    private String reason;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "商户分类ID")
    private Integer merCategoryId;

    @ApiModelProperty(value = "商户星级")
    private Integer merStarLevel;

    @ApiModelProperty(value = "商品分类名称")
    private String categoryName;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "活动状态:0未开始，1进行中，2已结束")
    private Integer activityStatus;

    @ApiModelProperty(value = "商品规格SKU")
    private List<ProductAttrValue> attrValue;

    @ApiModelProperty(value = "关联的普通商品ID")
    private Integer productId;

    @ApiModelProperty(value = "秒杀场次信息")
    private List<String> timeList;

    @ApiModelProperty(value = "基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品")
    private Integer type;
}
