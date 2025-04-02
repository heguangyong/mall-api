package com.zbkj.common.request.merchant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: 大粽子
 * @Date: 2023/9/20 17:19
 * @Description: 商户端小票打印 request
 */
@Data
public class MerchantPrintRequest {

    @ApiModelProperty(value = "主键")
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

    @ApiModelProperty(value = "易联云打印开关 0=关闭，1=开启")
    private Integer printYlyStatus;

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

}
