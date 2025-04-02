package com.zbkj.common.response;

import com.zbkj.common.vo.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * PC配置信息响应对象
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
@ApiModel(value = "PcShoppingConfigResponse", description = "PC配置信息响应对象")
public class PcShoppingConfigResponse implements Serializable {

    private static final long serialVersionUID = -4585094537501770138L;

    @ApiModelProperty(value = "PC商城—左上角logo")
    private String leftTopLogo;

    @ApiModelProperty(value = "PC商城—品牌好店广告图")
    private String goodStoreImage;

    @ApiModelProperty(value = "PC商城—手机体验购买二维码类型 1:小程序 2:公众号/H5")
    private String goPhoneQrCodeType;

//    @ApiModelProperty(value = "联系电话")
//    private String phone;
//
//    @ApiModelProperty(value = "地址")
//    private String address;
//
//    @ApiModelProperty(value = "授权信息")
//    private String authInfo;
//
//    @ApiModelProperty(value = "备案号")
//    private String filingNum;

    @ApiModelProperty(value = "首页广告")
    private PcHomeAdvertisementVo advertisement;

    @ApiModelProperty(value = "经营理念列表")
    private List<PcPhilosophyVo> philosophyList;

    @ApiModelProperty(value = "友情链接列表")
    private List<PcFriendlyLinkVo> friendlyLinkList;

    @ApiModelProperty(value = "快捷入口列表")
    private List<PcQuickEntryVo> quickEntryList;

    @ApiModelProperty(value = "二维码列表")
    private List<PcBottomQrCodeVo> qrCodeList;

    @ApiModelProperty(value = "首页导航列表")
    private List<PcHomeNavigationVo> homeNavigationList;
}
