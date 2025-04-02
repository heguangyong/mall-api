package com.zbkj.common.request.merchant.manage;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: 大粽子
 * @Date: 2024/5/24 10:29
 * @Description: 商户员工管理
 */
@Data
public class MerchantEmployeeSearchRequest extends PageParamRequest {
    @ApiModelProperty(value = "手机号或者名称")
    private String keywords;

    @ApiModelProperty(value = "状态：0关，1开")
    private Integer status;

}
