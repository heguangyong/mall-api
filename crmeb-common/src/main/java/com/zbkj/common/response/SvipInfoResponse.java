package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * svip信息响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SvipInfoResponse", description = "svip信息响应对象")
public class SvipInfoResponse implements Serializable {

    private static final long serialVersionUID = 924336973149367712L;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "是否付费会员")
    private Boolean isPaidMember;

    @ApiModelProperty(value = "是否永久付费会员")
    private Boolean isPermanentPaidMember;

    @ApiModelProperty(value = "付费会员到期时间")
    private Date paidMemberExpirationTime;

    @ApiModelProperty(value = "权益列表")
    private List<SvipBenefitsResponse> benefitsList;

    @ApiModelProperty(value = "会员卡列表")
    private List<SvipCardResponse> cardList;
}
