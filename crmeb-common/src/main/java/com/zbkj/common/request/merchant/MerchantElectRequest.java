package com.zbkj.common.request.merchant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value="MerchantElect 新增对象", description="商户电子面单配置")
public class MerchantElectRequest {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "打印机类型 0通用1快递100")
    @NotNull(message = "打印机类型 不能为空")
    private Integer op;

    @ApiModelProperty(value = "云打印机编号")
    private String cloudPrintNo;

    @ApiModelProperty(value = "寄件地址")
    @NotEmpty(message = "寄件地址 不能为空")
    private String senderAddr;

    @ApiModelProperty(value = "寄件人")
    @NotEmpty(message = "寄件人 不能为空")
    private String senderUsername;

    @ApiModelProperty(value = "寄件人电话")
    @NotEmpty(message = "寄件人电话 不能为空")
    private String senderPhone;
}
