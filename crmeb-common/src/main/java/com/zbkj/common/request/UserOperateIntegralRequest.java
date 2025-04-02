package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 操作用户积分请求对象
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
@ApiModel(value = "UserOperateIntegralRequest", description = "操作用户积分请求对象")
public class UserOperateIntegralRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "uid", required = true)
    @NotNull(message = "请选择用户")
    @Min(value = 1, message = "请输入正确的uid")
    private Integer uid;

    @ApiModelProperty(value = "操作类型， add=增加，sub=减少", required = true)
    @NotBlank(message = "请选择操作类型")
    @StringContains(limitValues = {"add", "sub"}, message = "未知的操作类型")
    private String operateType;

    @ApiModelProperty(value = "积分", required = true)
    @NotNull(message = "积分不能为空")
    @Range(min = 1, max = 999999, message = "积分范围为1~999999")
    private Integer integral;

}
