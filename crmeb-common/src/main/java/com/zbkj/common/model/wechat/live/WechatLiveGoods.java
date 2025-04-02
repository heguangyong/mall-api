package com.zbkj.common.model.wechat.live;

import com.baomidou.mybatisplus.annotation.IdType;
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
 *
 * </p>
 *
 * @author dazongzi
 * @since 2023-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wechat_live_goods")
@ApiModel(value="WechatLiveGoods对象", description="")
public class WechatLiveGoods implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "填入mediaID（mediaID获取后，三天内有效）；图片mediaID的获取，请参考以下文档New_temporary_materials；图片规则：图片尺寸最大300像素*300像素；")
    private String coverImgUrl;

    @ApiModelProperty(value = "对应cover_img_url的本地图片地址")
    private String coverImgUrlLocal;

    @ApiModelProperty(value = "商品名称，最长14个汉字，1个汉字相当于2个字符")
    private String name;

    @ApiModelProperty(value = "价格类型，1：一口价（只需要传入price，price2不传） 2：价格区间（price字段为左边界，price2字段为右边界，price和price2必传） 3：显示折扣价（price字段为原价，price2字段为现价， price和price2必传）")
    private Integer priceType;

    @ApiModelProperty(value = "数字，最多保留两位小数，单位元")
    private BigDecimal price;

    @ApiModelProperty(value = "数字，最多保留两位小数，单位元")
    private BigDecimal price2;

    @ApiModelProperty(value = "商品详情页的小程序路径，路径参数存在 url 的，")
    private String url;

    @ApiModelProperty(value = "直播商品id 微信返回的")
    private Integer goodsId;

    @ApiModelProperty(value = "直播商品id对应的本地商品id")
    private Integer productId;

    @ApiModelProperty(value = "审核单ID")
    private Long auditId;

    @ApiModelProperty(value = "微信侧状态结果 0：未审核，1：审核中，2:审核通过，3审核失败")
    private Integer auditStatus;

    @ApiModelProperty(value = "当商品为第三方小程序的商品则填写为对应第三方小程序的appid，自身小程序商品则为")
    private String thirdPartyAppid;

    @ApiModelProperty(value = "1、2：表示是为 API 添加商品，否则是直播控制台添加的商品")
    private Integer thirdPartyTag;

    @ApiModelProperty(value = "0=商户创建/撤回,1=平台待审核/商户重新提交审核，2=平台审核通过/微信审核中，3=平台审核失败,4=微信审核成功，5=微信审核失败")
    private Integer reviewStatus;

    @ApiModelProperty(value = "审核失败原因")
    private String reviewReason;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "商户分类")
    private Integer merType;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "排序")
    private Integer sort;


}
