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
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 商户余额记录表
 * </p>
 *
 * @author HZW
 * @since 2023-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant_balance_record")
@ApiModel(value = "MerchantBalanceRecord对象", description = "商户余额记录表")
public class MerchantBalanceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "关联单号（订单号、结算单号、退款单号）")
    private String linkNo;

    @ApiModelProperty(value = "关联类型（order-订单,closing-结算,refund-退款）")
    private String linkType;

    @ApiModelProperty(value = "类型：1-增加，2-扣减")
    private Integer type;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "剩余")
    private BigDecimal balance;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "状态：1-订单创建，2-冻结期，3-完成，4-失效（订单退款/申请被拒），5-提现申请")
    private Integer status;

    @ApiModelProperty(value = "冻结期时间（天）")
    private Integer frozenTime;

    @ApiModelProperty(value = "解冻时间")
    private Long thawTime;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
