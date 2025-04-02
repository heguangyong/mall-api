package com.zbkj.common.vo.wxvedioshop.cat_brand;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/** 更新商家信息
 * @Auther: 大粽子
 * @Date: 2022/9/26 18:40
 * @Description: 更新商家信息  Request
 */
@Data
public class ShopAccountUpdateVo {

    @ApiModelProperty(value = "小程序path")
    private String service_agent_path;

    @ApiModelProperty(value = "客服联系方式")
    private String service_agent_phone;

    @ApiModelProperty(value = "客服类型，支持多个，0: 小程序客服（会校验是否开启），1: 自定义客服path 2: 联系电话")
    @NotEmpty
    private List<Integer> service_agent_type;

    @ApiModelProperty(value = "默认退货地址，退货售后超时时，会让用户将货物寄往此地址。")
    private ShopAccountUpdateAddressItemVo default_receiving_address;
}
