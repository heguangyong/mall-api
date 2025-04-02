package com.zbkj.common.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 用户余额记录表
 * </p>
 *
 * @author HZW
 * @since 2022-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_balance_record")
@ApiModel(value="UserBalanceRecord对象", description="用户余额记录表")
public class UserBalanceRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "关联id(订单号、充值单号)(system默认为0）")
    private String linkId;

    @ApiModelProperty(value = "关联类型:order-订单,recharge-充值,system-系统,brokerage-佣金转余额")
    private String linkType;

    @ApiModelProperty(value = "类型：1-增加，2-扣减")
    private Integer type;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "剩余")
    private BigDecimal balance;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;


}
