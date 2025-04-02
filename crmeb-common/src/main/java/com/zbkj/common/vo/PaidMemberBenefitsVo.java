package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员权益Vo对象
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
@ApiModel(value = "PaidMemberBenefitsVo", description = "会员权益Vo对象")
public class PaidMemberBenefitsVo implements Serializable {

    private static final long serialVersionUID = -3097800453211866415L;

    @ApiModelProperty(value = "ID", required = true)
    @NotNull(message = "ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "权益名称")
    private String name;

    @ApiModelProperty(value = "权益图标", required = true)
    @NotBlank(message = "权益图标不能为空")
    private String imageUrl;

    @ApiModelProperty(value = "展示名称", required = true)
    @NotBlank(message = "展示名称不能为空")
    @Length(max = 6, message = "展示名称不能超过6个字符")
    private String value;

    @ApiModelProperty(value = "权益简介", required = true)
    @NotBlank(message = "权益简介不能为空")
    @Length(max = 12, message = "展示名称不能超过12个字符")
    private String message;

    @ApiModelProperty(value = "状态:是否显示", required = true)
    @NotNull(message = "状态不能为空")
    private Boolean status;

    @ApiModelProperty(value = "排序", required = true)
    @NotNull(message = "排序不能为空")
    @Range(min = 0, max = 10, message = "排序范围0~10")
    private Integer sort;

    @ApiModelProperty(value = "权益说明")
    private String expand;

    @ApiModelProperty(value = "倍数")
    private Integer multiple;

    @ApiModelProperty(value = "渠道字符串:积分（1-签到，2-购买商品），经验值（1-签到，2-发布种草，3-购买商品，4-商品评价，5-邀请新用户）")
    private String channelStr;
}
