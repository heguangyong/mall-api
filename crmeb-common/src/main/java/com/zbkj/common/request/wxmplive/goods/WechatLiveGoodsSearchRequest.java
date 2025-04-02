package com.zbkj.common.request.wxmplive.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/27 18:02
 * @Description: 描述对应的业务场景
 */
@Data
public class WechatLiveGoodsSearchRequest {

    @ApiModelProperty(value = "搜索内容：id,商品名称,商户名称,微信直播间id,微信审核单id")
    private String keywords;

    @ApiModelProperty(value = "商户分类")
    private Integer merchant_type;

//    @ApiModelProperty(value = "0：未审核，1：审核中，2:审核通过，3审核失败")
//    private Integer auditStatus;

    @ApiModelProperty(value = "0=商户创建/撤回,1=平台待审核/商户重新提交审核，2=平台审核通过，3=平台审核失败,4=微信审核成功，5=微信审核失败")
    private Integer reviewStatus;

}
