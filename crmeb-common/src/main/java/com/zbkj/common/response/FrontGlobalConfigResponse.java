package com.zbkj.common.response;

import com.zbkj.common.annotation.StringContains;
import com.zbkj.common.model.system.SystemConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 移动端全局配置信息响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FrontGlobalConfigResponse", description = "移动端全局配置信息响应对象")
public class FrontGlobalConfigResponse implements Serializable {

    private static final long serialVersionUID = -6309840320447728140L;

    @ApiModelProperty(value = "付费会员-会员价格展示 all-全部，paid-仅付费会员")
    private String paidMemberPriceDisplay;

    @ApiModelProperty(value = "用户是否是付费会员")
    private Boolean userIsPaidMember = false;

    @ApiModelProperty(value = "颜色配置")
    private String changeColorConfig;

    @ApiModelProperty(value = "全局本地图片域名")
    private String imageDomain;

    @ApiModelProperty(value = "版权图片")
    private String copyrightCompanyImage;

    @ApiModelProperty(value = "移动端域名")
    private String frontDomain;

    @ApiModelProperty(value = "网站名称")
    private String siteName;

    @ApiModelProperty(value = "商户入驻开关：1-开，0-关")
    private String merchantApplySwitch;

    @ApiModelProperty(value = "授权备案-联系电话")
    private String authorizePhone;

    @ApiModelProperty(value = "授权备案-地址")
    private String authorizeAddress;

    @ApiModelProperty(value = "授权备案-授权信息")
    private String authorizeInfo;

    @ApiModelProperty(value = "授权备案-备案号")
    private String authorizeFilingNum;
}
