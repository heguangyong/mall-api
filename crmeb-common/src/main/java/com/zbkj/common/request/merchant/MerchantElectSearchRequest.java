package com.zbkj.common.request.merchant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="MerchantElectSearchRequest 查询对象", description="商户电子面列表查询对象")
public class MerchantElectSearchRequest {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "打印机类型0通用1快递100")
    private Integer op;

    @ApiModelProperty(value = "云打印机编号")
    private String cloudPrintNo;

    @ApiModelProperty(value = "寄件地址")
    private String senderAddr;

    @ApiModelProperty(value = "寄件人")
    private String senderUsername;

    @ApiModelProperty(value = "寄件人电话")
    private String senderPhone;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
