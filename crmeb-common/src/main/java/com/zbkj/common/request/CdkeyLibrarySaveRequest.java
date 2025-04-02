package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 卡密库保存对象
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
@ApiModel(value = "CdkeyLibrarySaveRequest", description = "卡密库保存对象")
public class CdkeyLibrarySaveRequest implements Serializable {

    private static final long serialVersionUID = -7053809553211774431L;

    @ApiModelProperty(value = "卡密库ID,编辑时必填")
    private Integer id;

    @ApiModelProperty(value = "卡密库名称", required = true)
    @NotBlank(message = "请填写卡密库名称")
    @Length(max = 32, message = "卡密库名称长度不能超过32个字符")
    private String name;

    @ApiModelProperty(value = "备注")
    @Length(max = 200, message = "备注长度不能超过200个字符")
    private String remark;
}
