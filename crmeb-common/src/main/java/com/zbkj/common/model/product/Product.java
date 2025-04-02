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
import java.util.Date;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_product")
@ApiModel(value = "Product对象", description = "商品表")
public class Product implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户Id")
    private Integer merId;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "展示图")
    private String flatPattern;

    @ApiModelProperty(value = "轮播图")
    private String sliderImage;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品简介")
    private String intro;

    @ApiModelProperty(value = "关键字,英文逗号拼接")
    private String keyword;

    @ApiModelProperty(value = "商户分类id(逗号拼接)")
    private String cateId;

    @ApiModelProperty(value = "品牌id")
    private Integer brandId;

    @ApiModelProperty(value = "平台分类id")
    private Integer categoryId;

    @ApiModelProperty(value = "保障服务ids(英文逗号拼接)")
    private String guaranteeIds;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "会员价格")
    private BigDecimal vipPrice;

    @ApiModelProperty(value = "市场价/原价/划线价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "单位名")
    private String unitName;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

    @ApiModelProperty(value = "虚拟销量")
    private Integer ficti;

    @ApiModelProperty(value = "浏览量")
    private Integer browse;

    @ApiModelProperty(value = "商品二维码地址(用户小程序海报)")
    private String codePath;

    @ApiModelProperty(value = "淘宝京东1688类型")
    private String soureLink;

    @ApiModelProperty(value = "主图视频链接")
    private String videoLink;

    @ApiModelProperty(value = "运费模板ID")
    private Integer tempId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "总后台排序")
    private Integer rank;

    @ApiModelProperty(value = "规格 0单 1多")
    private Boolean specType;

    @ApiModelProperty(value = "是否回收站")
    private Boolean isRecycle;

    @ApiModelProperty(value = "是否单独分佣")
    private Boolean isSub;

    @ApiModelProperty(value = "状态（0：未上架，1：上架）")
    private Boolean isShow;

    @ApiModelProperty(value = "审核状态：0-无需审核 1-待审核，2-审核成功，3-审核拒绝")
    private Integer auditStatus;

    @ApiModelProperty(value = "是否加入审核，0-正常，1-审核流程中")
    private Boolean isAudit;

    @ApiModelProperty(value = "拒绝原因")
    private String reason;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品")
    private Integer type;

    @ApiModelProperty(value = "是否付费会员商品")
    private Boolean isPaidMember;

    @ApiModelProperty(value = "是否自动上架")
    private Boolean isAutoUp;

    @ApiModelProperty(value = "配送方式：1-商家配送，2-到店核销")
    private String deliveryMethod;

    @ApiModelProperty(value = "营销类型：0=基础商品,1=秒杀,2=拼团")
    private Integer marketingType;

    @ApiModelProperty(value = "是否支持退款")
    private Boolean refundSwitch;

    @ApiModelProperty(value = "系统表单ID")
    private Integer systemFormId;

    @ApiModelProperty(value = "兑换积分")
    private Integer redeemIntegral;

    @ApiModelProperty(value = "兑换数量限制")
    private Integer exchangeNum;

    @ApiModelProperty(value = "是否热门推荐")
    private Integer isHot;

    @ApiModelProperty(value = "商品详情")
    @TableField(exist = false)
    private String content;

    @ApiModelProperty(value = "活动边框 列表中是边框 详情中是背景图")
    @TableField(exist = false)
    private String activityStyle;

    @TableField(exist = false)
    @ApiModelProperty(value = "拼团商品价格")
    private BigDecimal groupPrice;
}
