package com.zbkj.common.vo.wxvedioshop.audit;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 小程序过往审核资质数据 类目返回数据
 * @Auther: 大粽子
 * @Date: 2022/9/26 14:57
 * @Description: 描述对应的业务场景
 */
@Data
public class ShopAuditGetMiniAppCertificateItemCategoryResponseVo {

    @ApiModelProperty(value = "小程序的一级类目")
    private Integer first_category_id;

    @ApiModelProperty(value = "商标授权书")
    private String first_category_name;

    @ApiModelProperty(value = "小程序的二级类目")
    private Integer second_category_id;

    @ApiModelProperty(value = "小程序的二级类目名")
    private String second_category_name;

    @ApiModelProperty(value = "资质相关图片")
    private List<String> certificate_url;

}
