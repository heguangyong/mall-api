package com.zbkj.common.vo.wxvedioshop.cat_brand;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Auther: 大粽子
 * @Date: 2022/9/26 18:44
 * @Description: 更新商家信息 request
 */
@Data
public class ShopAccountUpdateAddressItemVo {

    @NotEmpty
    @ApiModelProperty(value = "收件人姓名")
    private String receiver_name;

    @NotEmpty
    @ApiModelProperty(value = "详细收货地址信息")
    private String detailed_address;

    @NotEmpty
    @ApiModelProperty(value = "收件人手机号码")
    private String tel_number;

    @ApiModelProperty(value = "国家")
    private String country;

    @NotEmpty
    @ApiModelProperty(value = "省")
    private String province;

    @NotEmpty
    @ApiModelProperty(value = "市")
    private String city;

    @NotEmpty
    @ApiModelProperty(value = "区")
    private String town;
}
