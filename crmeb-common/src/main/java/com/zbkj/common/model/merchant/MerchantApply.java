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
 * 商户申请表
 * </p>
 *
 * @author HZW
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant_apply")
@ApiModel(value="MerchantApply对象", description="商户申请表")
public class MerchantApply implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "申请ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "申请用户ID")
    private Integer uid;

    @ApiModelProperty(value = "商户名称")
    private String name;

    @ApiModelProperty(value = "商户分类ID")
    private Integer categoryId;

    @ApiModelProperty(value = "商户类型ID")
    private Integer typeId;

    @ApiModelProperty(value = "商户账号")
    private String account;

    @ApiModelProperty(value = "登录密码")
    private String password;

    @ApiModelProperty(value = "商户姓名")
    private String realName;

    @ApiModelProperty(value = "商户邮箱")
    private String email;

    @ApiModelProperty(value = "商户手机号")
    private String phone;

    @ApiModelProperty(value = "手续费(%)")
    private Integer handlingFee;

    @ApiModelProperty(value = "商户关键字")
    private String keywords;

    @ApiModelProperty(value = "商户地址")
    private String address;

    @ApiModelProperty(value = "是否自营：0-非自营，1-自营")
    private Boolean isSelf;

    @ApiModelProperty(value = "是否推荐:0-不推荐，1-推荐")
    private Boolean isRecommend;

    @ApiModelProperty(value = "审核状态：1-待审核，2-审核通过，3-审核拒绝")
    private Integer auditStatus;

    @ApiModelProperty(value = "拒绝原因")
    private String denialReason;

    @ApiModelProperty(value = "审核员ID")
    private Integer auditorId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "资质图片")
    private String qualificationPicture;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
