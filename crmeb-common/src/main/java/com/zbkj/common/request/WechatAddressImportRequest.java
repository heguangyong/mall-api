package com.zbkj.common.request;

import com.zbkj.common.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 微信地址导入请求对象
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
@ApiModel(value = "WechatAddressImportRequest对象", description = "微信地址导入请求对象")
public class WechatAddressImportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

//    @ApiModelProperty(value = "收货人姓名", required = false)
//    @NotBlank(message = "收货人姓名不能为空")
//    private String userName;

//    @ApiModelProperty(value = "收货人电话", required = false)
//    @NotBlank(message = "收货人电话不能为空")
//    @Pattern(regexp = RegularConstants.PHONE_TWO, message = "请输入正确的手机号")
//    private String telNumber;

    @ApiModelProperty(value = "收货人所在省", required = true)
    @NotBlank(message = "收货人所在省不能为空")
    private String provinceName;

    @ApiModelProperty(value = "收货人所在市", required = true)
    @NotBlank(message = "收货人所在市不能为空")
    private String cityName;

    @ApiModelProperty(value = "收货人所在区/县", required = true)
    @NotBlank(message = "收货人所在区/县不能为空")
    private String countryName;

    @ApiModelProperty(value = "收货人所在街道")
    private String streetName;

    @ApiModelProperty(value = "收货地址国家码", required = true)
    private String nationalCode;

    @ApiModelProperty(value = "收货人详细地址", required = true)
    @NotBlank(message = "收货人详细地址不能为空")
    @Length(max = 500, message = "详细地址不能超过500个字符")
    private String detail;

}
