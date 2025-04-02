package com.zbkj.common.model.merchant;

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
import java.util.Date;

/**
 * <p>
 * 商户表
 * </p>
 *
 * @author HZW
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant")
@ApiModel(value = "Merchant对象", description = "商户表")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户名称")
    private String name;

    @ApiModelProperty(value = "商户分类ID")
    private Integer categoryId;

    @ApiModelProperty(value = "商户类型ID")
    private Integer typeId;

    @ApiModelProperty(value = "商户姓名")
    private String realName;

    @ApiModelProperty(value = "商户邮箱")
    private String email;

    @ApiModelProperty(value = "商户手机号")
    private String phone;

    @ApiModelProperty(value = "手续费(%)")
    private Integer handlingFee;

    @ApiModelProperty(value = "商户关键字")
    private String keywords;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "商户详细地址")
    private String addressDetail;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "是否自营：0-非自营，1-自营")
    private Boolean isSelf;

    @ApiModelProperty(value = "是否推荐:0-不推荐，1-推荐")
    private Boolean isRecommend;

    @ApiModelProperty(value = "商户开关:0-关闭，1-开启")
    private Boolean isSwitch;

    @ApiModelProperty(value = "商品审核开关:0-关闭，1-开启")
    private Boolean productSwitch;

    @ApiModelProperty(value = "自提开关:0-关闭，1-开启")
    private Boolean isTakeTheir;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "资质图片")
    private String qualificationPicture;

    @ApiModelProperty(value = "商户背景图")
    private String backImage;

    @ApiModelProperty(value = "商户头像")
    private String avatar;

    @ApiModelProperty(value = "商户logo（横）")
    private String rectangleLogo;

    @ApiModelProperty(value = "商户封面图")
    private String coverImage;

    @ApiModelProperty(value = "商户街背景图")
    private String streetBackImage;

    @ApiModelProperty(value = "商户简介")
    private String intro;

    @ApiModelProperty(value = "复制商品数量")
    private Integer copyProductNum;

    @ApiModelProperty(value = "商户余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "商户星级1-5")
    private Integer starLevel;

    @ApiModelProperty(value = "pcBanner")
    private String pcBanner;

    @ApiModelProperty(value = "pc背景图")
    private String pcBackImage;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "商户创建类型：admin-管理员创建，apply-商户入驻申请")
    private String createType;

    @ApiModelProperty(value = "创建商户管理员ID")
    private Integer createId;

    @ApiModelProperty(value = "关联管理账号ID")
    private Integer adminId;

    @ApiModelProperty(value = "小票打印开关：0关闭，1=手动打印，2=自动打印，3=自动和手动")
    private Integer receiptPrintingSwitch;

    @ApiModelProperty(value = "电子面单开关：0关闭，1=开启")
    private Integer electrPrintingSwitch;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "商户PCLogo")
    private String pcLogo;

    @ApiModelProperty(value = "商户PC品牌好店封面图片")
    private String pcGoodStoreCoverImage;

    @ApiModelProperty(value = "商户是否强制关闭:0-否，1-强制关闭")
    private Boolean isForceShutdown;
}
