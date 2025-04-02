package com.zbkj.common.model.merchant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 商户电子面单配置表
 * </p>
 *
 * @author dazongzi
 * @since 2025-02-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant_elect")
@ApiModel(value="MerchantElect对象", description="商户电子面单配置表")
public class MerchantElect implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
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
