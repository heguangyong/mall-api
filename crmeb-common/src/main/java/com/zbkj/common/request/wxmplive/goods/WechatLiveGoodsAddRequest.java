package com.zbkj.common.request.wxmplive.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/27 18:54
 * @Description: 描述对应的业务场景
 */
@Data
public class WechatLiveGoodsAddRequest {

//    @TableId(value = "id", type = IdType.AUTO)
//    private Integer id;

    @ApiModelProperty(value = "填入mediaID（mediaID获取后，三天内有效）；图片mediaID的获取，请参考以下文档New_temporary_materials；图片规则：图片尺寸最大300像素*300像素；", required = true)
    @NotBlank(message = "封面素材不能为空 填入mediaID（mediaID获取后，三天内有效）；图片mediaID的获取，请参考以下文档New_temporary_materials；图片规则：图片尺寸最大300像素*300像素；")
    private String coverImgUrl;

    @ApiModelProperty(value = "对应cover_img_url的本地图片地址")
    @NotBlank(message = "对应cover_img_url的本地图片地址 不能为空")
    private String coverImgUrlLocal;

    @ApiModelProperty(value = "商品名称，最长14个汉字，1个汉字相当于2个字符", required = true)
    @NotBlank(message = "商品名称不能为空 商品名称，最长14个汉字，1个汉字相当于2个字符")
    private String name;

    @ApiModelProperty(value = "价格类型，1：一口价（只需要传入price，price2不传） 2：价格区间（price字段为左边界，price2字段为右边界，price和price2必传） 3：显示折扣价（price字段为原价，price2字段为现价， price和price2必传）", required = true)
    @NotNull(message = "价格类型")
    private Integer priceType;

    @ApiModelProperty(value = "数字，最多保留两位小数，单位元",required = true)
    @NotNull(message = "价格1 不能为空")
    private BigDecimal price;

    @ApiModelProperty(value = "数字，最多保留两位小数，单位元")
    private BigDecimal price2;

    @ApiModelProperty(value = "直播商品id对应的本地商品id", required = true)
    @NotNull(message = "请选择直播商品")
    private Integer productId;

    @ApiModelProperty(value = "商品详情页的小程序路径，路径参数存在 url 的，该参数的值需要进行 encode 处理再填入", required = true)
    @NotBlank(message = "商品详情页小程序路径")
    private String url;

//    @ApiModelProperty(value = "直播商品id")
//    private Integer goodsId;

//    @ApiModelProperty(value = "审核单ID")
//    private String auditId;

//    @ApiModelProperty(value = "0：未审核，1：审核中，2:审核通过，3审核失败")
//    private Integer auditStatus;

//    @ApiModelProperty(value = "当商品为第三方小程序的商品则填写为对应第三方小程序的appid，自身小程序商品则为''")
//    private String thirdPartyAppid;

//    @ApiModelProperty(value = "1、2：表示是为 API 添加商品，否则是直播控制台添加的商品")
//    private Integer thirdPartyTag;
//
//    @ApiModelProperty(value = "0=商户创建平台待审核，1=平台审核通过，2=平台审核失败,3=微信审核成功，4=微信审核失败")
//    private Integer reviewStatus;
//
//    @ApiModelProperty(value = "审核失败原因")
//    private String reviewReason;
}
