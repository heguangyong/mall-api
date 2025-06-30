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
 * @author
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_product")
@ApiModel(value = "Product对象", description = "商品表")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户Id")
    @TableField("mer_id")
    private Integer merId;

    @ApiModelProperty(value = "商品图片")
    @TableField("image")
    private String image;

    @ApiModelProperty(value = "展示图")
    @TableField("flat_pattern")
    private String flatPattern;

    @ApiModelProperty(value = "轮播图")
    @TableField("slider_image")
    private String sliderImage;

    @ApiModelProperty(value = "商品名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "商品简介")
    @TableField("intro")
    private String intro;

    @ApiModelProperty(value = "关键字,英文逗号拼接")
    @TableField("keyword")
    private String keyword;

    @ApiModelProperty(value = "商户分类id(逗号拼接)")
    @TableField("cate_id")
    private String cateId;

    @ApiModelProperty(value = "品牌id")
    @TableField("brand_id")
    private Integer brandId;

    @ApiModelProperty(value = "平台分类id")
    @TableField("category_id")
    private Integer categoryId;

    @ApiModelProperty(value = "保障服务ids(英文逗号拼接)")
    @TableField("guarantee_ids")
    private String guaranteeIds;

    @ApiModelProperty(value = "商品价格")
    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty(value = "会员价格")
    @TableField("vip_price")
    private BigDecimal vipPrice;

    @ApiModelProperty(value = "市场价/原价/划线价")
    @TableField("ot_price")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "单位名")
    @TableField("unit_name")
    private String unitName;

    @ApiModelProperty(value = "销量")
    @TableField("sales")
    private Integer sales;

    @ApiModelProperty(value = "库存")
    @TableField("stock")
    private Integer stock;

    @ApiModelProperty(value = "成本价")
    @TableField("cost")
    private BigDecimal cost;

    @ApiModelProperty(value = "虚拟销量")
    @TableField("ficti")
    private Integer ficti;

    @ApiModelProperty(value = "浏览量")
    @TableField("browse")
    private Integer browse;

    @ApiModelProperty(value = "商品二维码地址(用户小程序海报)")
    @TableField("code_path")
    private String codePath;

    @ApiModelProperty(value = "淘宝京东1688链接")
    @TableField("source_link")
    private String sourceLink;

    @ApiModelProperty(value = "主图视频链接")
    @TableField("video_link")
    private String videoLink;

    @ApiModelProperty(value = "运费模板ID")
    @TableField("temp_id")
    private Integer tempId;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "总后台排序")
    @TableField(value = "`rank`")
    private Integer rank;

    @ApiModelProperty(value = "规格 0=单规格，1=多规格")
    @TableField("spec_type")
    private Boolean specType;

    @ApiModelProperty(value = "是否回收站")
    @TableField("is_recycle")
    private Boolean isRecycle;

    @ApiModelProperty(value = "是否单独分佣")
    @TableField("is_sub")
    private Boolean isSub;

    @ApiModelProperty(value = "是否上架（0=未上架，1=上架）")
    @TableField("is_show")
    private Boolean isShow;

    @ApiModelProperty(value = "审核状态：0-无需审核，1-待审核，2-审核成功，3-审核拒绝")
    @TableField("audit_status")
    private Integer auditStatus;

    @ApiModelProperty(value = "是否在审核流程中，0-否，1-是")
    @TableField("is_audit")
    private Boolean isAudit;

    @ApiModelProperty(value = "拒绝原因")
    @TableField("reason")
    private String reason;

    @ApiModelProperty(value = "是否删除")
    @TableField("is_del")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private Date updateTime;

    @ApiModelProperty(value = "基础类型：0=普通商品,1=积分商品,2=虚拟商品,4=视频号,5=云盘商品,6=卡密商品")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "是否付费会员商品")
    @TableField("is_paid_member")
    private Boolean isPaidMember;

    @ApiModelProperty(value = "是否自动上架")
    @TableField("is_auto_up")
    private Boolean isAutoUp;

    @ApiModelProperty(value = "配送方式：1-商家配送，2-到店核销")
    @TableField("delivery_method")
    private String deliveryMethod;

    @ApiModelProperty(value = "营销类型：0=基础商品,1=秒杀,2=拼团")
    @TableField("marketing_type")
    private Integer marketingType;

    @ApiModelProperty(value = "是否支持退款")
    @TableField("refund_switch")
    private Boolean refundSwitch;

    @ApiModelProperty(value = "系统表单ID")
    @TableField("system_form_id")
    private Integer systemFormId;

    @ApiModelProperty(value = "兑换积分")
    @TableField("redeem_integral")
    private Integer redeemIntegral;

    @ApiModelProperty(value = "兑换数量限制")
    @TableField("exchange_num")
    private Integer exchangeNum;

    @ApiModelProperty(value = "是否热门推荐")
    @TableField("is_hot")
    private Integer isHot;

    // ========== 非数据库字段 ==========

    @ApiModelProperty(value = "商品详情")
    @TableField(exist = false)
    private String content;

    @ApiModelProperty(value = "活动样式，列表边框或详情背景")
    @TableField(exist = false)
    private String activityStyle;

    @ApiModelProperty(value = "拼团商品价格")
    @TableField(exist = false)
    private BigDecimal groupPrice;
}
