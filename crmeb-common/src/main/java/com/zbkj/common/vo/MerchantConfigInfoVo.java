package com.zbkj.common.vo;

import com.zbkj.common.annotation.StringContains;
import com.zbkj.common.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 商户配置信息Vo对象
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
@ApiModel(value = "MerchantConfigInfoVo对象", description = "商户配置信息Vo对象")
public class MerchantConfigInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户关键字,英文逗号分隔")
    private String keywords;

    @ApiModelProperty(value = "商户详细地址", required = true)
    @NotBlank(message = "商户地址不能为空")
    private String addressDetail;

    @ApiModelProperty(value = "纬度", required = true)
    @NotBlank(message = "纬度不能为空")
    private String latitude;

    @ApiModelProperty(value = "经度", required = true)
    @NotBlank(message = "经度不能为空")
    private String longitude;

    @ApiModelProperty(value = "商户背景图", required = true)
    @NotBlank(message = "商户背景图不能为空")
    private String backImage;

    @ApiModelProperty(value = "商户头像", required = true)
    @NotBlank(message = "商户头像不能为空")
    private String avatar;

    @ApiModelProperty(value = "商户logo（横）")
    @NotBlank(message = "商户logo（横）不能为空")
    private String rectangleLogo;

    @ApiModelProperty(value = "商户封面图")
    @NotBlank(message = "商户封面图不能为空")
    private String coverImage;

    @ApiModelProperty(value = "商户街背景图", required = true)
    @NotBlank(message = "商户街背景图不能为空")
    private String streetBackImage;

    @ApiModelProperty(value = "商户简介", required = true)
    @NotBlank(message = "商户简介不能为空")
    private String intro;

    @ApiModelProperty(value = "警戒库存", required = true)
    @NotNull(message = "警戒库存不能为空")
    @Range(min = 0, max = 9999, message = "警戒库存范围0~9999")
    private Integer alertStock;

    @ApiModelProperty(value = "客服类型：H5-H5链接、phone-电话", required = true)
    @NotEmpty(message = "客服类型不能为空")
    @StringContains(limitValues = {"H5", "phone"}, message = "未知的客服类型")
    private String serviceType;

    @ApiModelProperty(value = "客服H5链接")
    private String serviceLink;

    @ApiModelProperty(value = "客服电话")
    private String servicePhone;

    @ApiModelProperty(value = "pcBanner")
    private String pcBanner;

    @ApiModelProperty(value = "pc背景图")
    private String pcBackImage;

    @ApiModelProperty(value = "自提开关:0-关闭，1-开启")
    @NotNull(message = "请选择是否开启自提")
    private Boolean isTakeTheir;

    @ApiModelProperty(value = "小票打印开关：0关闭，1=自动打印，2=手动打印，3=自动和手动")
    private Integer receiptPrintingSwitch;

    @ApiModelProperty(value = "电子面单开关：0关闭，1=开启")
    private Integer electrPrintingSwitch;

    @ApiModelProperty(value = "腾讯地图KEY")
    private String txMapKey;
}
