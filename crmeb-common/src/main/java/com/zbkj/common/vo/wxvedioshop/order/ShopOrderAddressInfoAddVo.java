package com.zbkj.common.vo.wxvedioshop.order;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 生成订单Vo对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class ShopOrderAddressInfoAddVo {

    /** 收件人姓名 */
    @TableField(value = "receiver_name")
    @ApiModelProperty(value = "收件人姓名")
    @NotEmpty
    private String receiverName;

    /** 详细收货地址信息 */
    @TableField(value = "detailed_address")
    @ApiModelProperty(value = "详细收货地址信息")
    @NotEmpty
    private String detailedAddress;

    /** 收件人手机号码 */
    @TableField(value = "tel_number")
    @ApiModelProperty(value = "收件人手机号码")
    @NotEmpty
    private String telNumber;

    /** 国家 */
    @ApiModelProperty(value = "国家")
    private String country;

    /** 省份 */
    @ApiModelProperty(value = "省份")
    @NotEmpty
    private String province;

    /** 城市 */
    @ApiModelProperty(value = "城市")
    @NotEmpty
    private String city;

    /** 乡镇 */
    @ApiModelProperty(value = "乡镇")
    @NotEmpty
    private String town;
}
