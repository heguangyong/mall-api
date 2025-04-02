package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

/**
 * 用户推广人请求对象
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
@ApiModel(value = "UserSpreadPeopleRequest对象", description = "用户推广人请求对象")
public class UserSpreadPeopleRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "推荐人类型|0=一级|1=二级", allowableValues = "range[0,1]", required = true)
    @Range(min = 0, max = 1, message = "推荐人类型必须在 0（一级），1（二级） 中选择")
    private Integer grade = 0;

    @ApiModelProperty(value = "搜索关键字")
    private String keyword;

    @ApiModelProperty(value = "排序, 排序|childCount=团队排序,amountCount=金额排序,orderCount=订单排序", allowableValues = "range[childCount,amountCount,orderCount]")
    @StringContains(limitValues = {"childCount", "amountCount", "orderCount"}, message = "未知的排序规则")
    private String sortKey;

    @ApiModelProperty(value = "排序值 DESC ASC")
    @StringContains(limitValues = {"DESC", "ASC"}, message = "未知的排序值")
    private String isAsc = "DESC";
}
