package com.zbkj.admin.vo;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * PC商城基础配置VO对象
 *
 * @author Hzw
 * @version 1.0.0
 * @Date 2023/9/25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PcShoppingBaseConfigVo", description="PC商城基础配置VO对象")
public class PcShoppingBaseConfigVo implements Serializable {

    private static final long serialVersionUID = 435382279098947378L;

    @ApiModelProperty(value = "PC商城—左上角logo")
    @NotBlank(message = "请选择左上角logo图")
    private String leftTopLogo;

    @ApiModelProperty(value = "PC商城—品牌好店广告图")
    @NotBlank(message = "请选择品牌好店广告图")
    private String goodStoreImage;

    @ApiModelProperty(value = "PC商城—手机体验购买二维码类型 1:小程序 2:公众号/H5")
    private String goPhoneQrCodeType;

    @ApiModelProperty(value = "商户入驻开关：1-开，0-关")
    @NotBlank(message = "请选择商户入驻开关是否开启")
    @StringContains(limitValues = {"1", "0"}, message = "未知的开关状态")
    private String merchantApplySwitch;

}
