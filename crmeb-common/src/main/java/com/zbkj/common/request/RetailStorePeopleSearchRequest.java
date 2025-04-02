package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 分销员分页列表查询请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/9/2
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RetailStorePeopleSearchRequest", description = "分销员分页列表查询请求对象")
public class RetailStorePeopleSearchRequest extends UserCommonSearchRequest implements Serializable {

    private static final long serialVersionUID = -5339141272949453449L;

    @ApiModelProperty(value = "today,yesterday,lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/")
    private String dateLimit;
}
