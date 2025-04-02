package com.zbkj.common.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户结算表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_closing")
@ApiModel(value="UserClosing对象", description="用户结算表")
public class UserClosing implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "结算单号")
    private String closingNo;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "结算类型：bank = 银行卡 alipay = 支付宝 wechat=微信")
    private String closingType;

    @ApiModelProperty(value = "银行卡持卡人姓名")
    private String cardholder;

    @ApiModelProperty(value = "银行卡卡号")
    private String bankCardNo;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "开户地址")
    private String bankAddress;

    @ApiModelProperty(value = "支付宝账号")
    private String alipayAccount;

    @ApiModelProperty(value = "微信号")
    private String wechatNo;

    @ApiModelProperty(value = "微信收款二维码")
    private String paymentCode;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "结算金额")
    private BigDecimal closingPrice;

    @ApiModelProperty(value = "用户余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "审核状态：0-待审核，1-通过审核，2-审核失败")
    private Integer auditStatus;

    @ApiModelProperty(value = "拒绝原因")
    private String refusalReason;

    @ApiModelProperty(value = "审核员id")
    private Integer auditId;

    @ApiModelProperty(value = "审核时间")
    private Date auditTime;

    @ApiModelProperty(value = "到账状态：0-未到账，1-已到账")
    private Integer accountStatus;

    @ApiModelProperty(value = "结算凭证")
    private String closingProof;

    @ApiModelProperty(value = "结算时间")
    private Date closingTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "用户昵称")
    @TableField(exist = false)
    private String nickName;

    @ApiModelProperty(value = "用户是否注销")
    @TableField(exist = false)
    private Boolean isLogoff;
}
