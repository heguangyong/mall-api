package com.zbkj.common.request.merchant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Auther: 大粽子
 * @Date: 2023/9/21 10:00
 * @Description: 描述对应的业务场景
 */
@Data
public class MerchantPrintUpdateStatusRequest {
    @ApiModelProperty(value = "小票打印机配置id")
    @NotNull(message = "小票打印机配置id 不能为空")
    private Integer id;

    @ApiModelProperty(value = "小票打印机配置状态 0=关闭，1=开启")
    @Range(min = 0, max = 1, message = "状态必须是0或者1")
    @NotNull(message = "小票打印机配置状态 不能为空")
    private Integer status;
}
