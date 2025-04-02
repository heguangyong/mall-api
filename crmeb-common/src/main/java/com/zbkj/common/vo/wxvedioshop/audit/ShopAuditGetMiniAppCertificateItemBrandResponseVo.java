package com.zbkj.common.vo.wxvedioshop.audit;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 小程序过往审核资质数据 品牌返回数据
 * @Auther: 大粽子
 * @Date: 2022/9/26 14:57
 * @Description: 描述对应的业务场景
 */
@Data
public class ShopAuditGetMiniAppCertificateItemBrandResponseVo {

    @ApiModelProperty(value = "品牌名")
    private String brand_wording;

    @ApiModelProperty(value = "商标授权书")
    private List<String> sale_authorization;

    @ApiModelProperty(value = "商标注册证")
    private List<String> trademark_registration_certificate;

    @ApiModelProperty(value = "因为category_info_list为数组，因此即使为空，也可能出现在回包中")
    private List<String> category_info_list;

}
