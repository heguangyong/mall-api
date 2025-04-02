package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 付费会员卡保存请求对象
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
@ApiModel(value = "PaidMemberCardSaveRequest", description = "付费会员卡保存请求对象")
public class PaidMemberCardSaveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id,编辑时必传")
    private Integer id;

    @ApiModelProperty(value = "会员卡名称", required = true)
    @NotBlank(message = "会员卡名称不能为空")
    @Length(max = 10, message = "会员卡名称不能超过10个字符")
    private String name;

    @ApiModelProperty(value = "卡片标签")
    @Length(max = 10, message = "卡片标签不能超过6个字符")
    private String label;

    @ApiModelProperty(value = "0-试用，1-期限，2-永久", required = true)
    @NotNull(message = "会员卡类型不能为空")
    @Range(min = 0, max = 2, message = "未知的会员卡类型")
    private Integer type;

    @ApiModelProperty(value = "期限天数")
    private Integer deadlineDay;

    @ApiModelProperty(value = "会员卡原价", required = true)
    @NotNull(message = "会员卡原价不能为空")
    @DecimalMax(value = "99999.99", message = "会员卡原价不能超过99999.99")
    @DecimalMin(value = "0.01", message = "会员卡原价不能小于0.01")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "会员卡售价", required = true)
    @NotNull(message = "会员卡售价不能为空")
    @DecimalMax(value = "99999.99", message = "会员卡售价不能超过99999.99")
    @DecimalMin(value = "0.01", message = "会员卡售价不能小于0.01")
    private BigDecimal price;

    @ApiModelProperty(value = "是否仅首充赠送", required = true)
    @NotNull(message = "是否仅首充赠送不能为空")
    private Boolean isFirstChargeGive;

    @ApiModelProperty(value = "赠送余额", required = true)
    @NotNull(message = "赠送余额不能为空")
    @DecimalMax(value = "999.99", message = "赠送余额不能超过999.99")
    @DecimalMin(value = "0.00", message = "赠送余额不能小于0.00")
    private BigDecimal giftBalance;

    @ApiModelProperty(value = "排序", required = true)
    @NotNull(message = "排序不能为空")
    @Range(min = 0, max = 9999, message = "排序的的范围为0-9999")
    private Integer sort;

    @ApiModelProperty(value = "状态：1-开启，0-关闭")
    @NotNull(message = "会员卡状态不能为空")
    private Boolean status;

}
