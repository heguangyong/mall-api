package com.zbkj.common.request.merchant;

import com.zbkj.common.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 商户入驻申请请求对象
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
@ApiModel(value = "MerchantSettledApplyRequest对象", description = "商户入驻申请请求对象")
public class MerchantSettledApplyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户名称", required = true)
    @NotEmpty(message = "商户名称不能为空")
    @Length(max = 16, message = "商户名称不能超过16个字符")
    private String name;

    @ApiModelProperty(value = "商户分类ID", required = true)
    @NotNull(message = "商户分类不能为空")
    private Integer categoryId;

    @ApiModelProperty(value = "商户类型ID", required = true)
    @NotNull(message = "商户类型不能为空")
    private Integer typeId;

    @ApiModelProperty(value = "商户姓名", required = true)
    @NotEmpty(message = "商户姓名不能为空")
    private String realName;

    @ApiModelProperty(value = "商户手机号", required = true)
    @NotEmpty(message = "商户手机号不能为空")
    @Pattern(regexp = RegularConstants.PHONE_TWO, message = "手机号格式不正确，请重新输入")
    private String phone;

    @ApiModelProperty(value = "手续费(%)", required = true)
//    @NotNull(message = "手续费不能为空")
//    @Range(min = 0, max = 100, message = "手续费率范围为0-100")
    private Integer handlingFee;

    @ApiModelProperty(value = "商户关键字")
    private String keywords;

    @ApiModelProperty(value = "资质图片", required = true)
    @NotEmpty(message = "资质图片不能为空")
    private String qualificationPicture;

    @ApiModelProperty(value = "验证码", required = true)
    @NotEmpty(message = "验证码不能为空")
    @Pattern(regexp = RegularConstants.VALIDATE_CODE_NUM_SIX, message = "验证码格式不正确，必须为6位数字")
    private String captcha;

}
