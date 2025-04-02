package com.zbkj.common.response;

import com.zbkj.common.model.product.ProductAttribute;
import com.zbkj.common.model.product.ProductGuarantee;
import com.zbkj.common.vo.CouponSimpleVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品详情响应对象
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
@ApiModel(value="StoreProductInfoResponse对象", description="商品详情响应对象")
public class ProductInfoResponse implements Serializable {

    private static final long serialVersionUID = 9215241889318610262L;

    @ApiModelProperty(value = "商品id")
    private Integer id;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "轮播图")
    private String sliderImage;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品简介")
    private String intro;

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "商户分类id(逗号拼接)")
    private String cateId;

    @ApiModelProperty(value = "品牌id")
    private Integer brandId;

    @ApiModelProperty(value = "平台分类id")
    private Integer categoryId;

    @ApiModelProperty(value = "保障服务ids(英文逗号拼接)")
    private String guaranteeIds;

    @ApiModelProperty(value = "单位名")
    private String unitName;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "虚拟销量")
    private Integer ficti;

    @ApiModelProperty(value = "规格 0单 1多")
    private Boolean specType;

    @ApiModelProperty(value = "商品属性")
    private List<ProductAttribute> attrList;

    @ApiModelProperty(value = "商品属性详情")
    private List<AttrValueResponse> attrValueList;

    @ApiModelProperty(value = "商品描述")
    private String content;

    @ApiModelProperty(value = "优惠券Ids（商户端）")
    private List<Integer> couponIds;

    @ApiModelProperty(value = "优惠券列表（平台端）")
    private List<CouponSimpleVo> couponList;

    @ApiModelProperty(value = "展示图")
    private String flatPattern;

    @ApiModelProperty(value = "运费模板ID")
    private Integer tempId;

    @ApiModelProperty(value = "是否单独分佣")
    private Boolean isSub;

    @ApiModelProperty(value = "商品类型：商品类型 0=普通商品，1=秒杀，2=砍价，3=拼团, 4=视频号, 5-云盘商品,6-卡密商品")
    private Integer type;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "收藏数量")
    private Integer collectNum;

    @ApiModelProperty(value = "状态（0：未上架，1：上架）")
    private Boolean isShow;

    @ApiModelProperty(value = "保障服务列表")
    private List<ProductGuarantee> guaranteeList;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "是否付费会员商品")
    private Boolean isPaidMember;

    @ApiModelProperty(value = "审核状态：0-无需审核 1-待审核，2-审核成功，3-审核拒绝")
    private Integer auditStatus;

    @ApiModelProperty(value = "配送方式：1-商家配送，2-到店核销")
    private String deliveryMethod;

    @ApiModelProperty(value = "系统表单ID")
    private Integer systemFormId;

    @ApiModelProperty(value = "表单内容")
    private String systemFormValue;

    @ApiModelProperty(value = "是否支持退款")
    private Boolean refundSwitch;
}
