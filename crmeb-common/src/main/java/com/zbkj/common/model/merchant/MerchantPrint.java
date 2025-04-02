package com.zbkj.common.model.merchant;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *  商户打印机配置
 * </p>
 *
 * @author dazongzi
 * @since 2023-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant_print")
@ApiModel(value="MerchantPrint对象", description="")
public class MerchantPrint implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "打印机类型: 0=易联云，1=飞蛾云")
    private Integer printType;

    @ApiModelProperty(value = "易联云打印机名称")
    private String printName;

    @ApiModelProperty(value = "易联云appid")
    private String printYlyAppid;

    @ApiModelProperty(value = "易联云用户id")
    private String printYlyUserid;

    @ApiModelProperty(value = "易联云密钥")
    private String printYlySec;

    @ApiModelProperty(value = "易联云终端号")
    private String printYlyMerchineNo;

    @ApiModelProperty(value = "飞蛾云打印机名称")
    private String printFeName;

    @ApiModelProperty(value = "飞蛾云打印机User")
    private String printFeUser;

    @ApiModelProperty(value = "飞蛾云打印机UKey")
    private String printFeUkey;

    @ApiModelProperty(value = "飞蛾云打印机SN")
    private String printFeSn;

    @ApiModelProperty(value = "打印开关 0=关闭，1=开启")
    private Integer status;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "内容Json")
    private String content;
}
