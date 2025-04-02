package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 卡密保存对象
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
@ApiModel(value = "CardSecretSaveRequest", description = "卡密保存对象")
public class CardSecretSaveRequest implements Serializable {

    private static final long serialVersionUID = -7053809553211774431L;

    @ApiModelProperty(value = "卡密ID", required = true)
    @NotNull(message = "卡密ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "卡号", required = true)
    @NotBlank(message = "卡号不能为空")
    @Length(max = 50, message = "卡号不能超过50个字符")
    private String cardNumber;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Length(max = 50, message = "密码不能超过50个字符")
    private String secretNum;
}
