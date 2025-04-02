package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 积分商品表头请求对象
 *
 * @author Han
 * @version 1.7.0
 * @Date 2024/8/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "IntegralProductTabsHeaderRequest", description = "积分商品表头请求对象")
public class IntegralProductTabsHeaderRequest implements Serializable {

    private static final long serialVersionUID = 1522492504392316445L;

    @ApiModelProperty(value = "关键字搜索，支持(商品名称, 关键字)")
    private String keywords;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;
}
