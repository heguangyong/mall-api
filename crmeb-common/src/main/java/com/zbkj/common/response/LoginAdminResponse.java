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
 * 系统管理员Response对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="LoginAdminResponse对象", description="系统管理员Response对象")
public class LoginAdminResponse implements Serializable {

    private Integer id;

    private String account;

    private String realName;

    private String roles;

    private String roleNames;

    private String lastIp;

    private Date lastTime;

    private Integer addTime;

    private Integer loginCount;

    private Integer level;

    private Boolean status;

    private String Token;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "是否接收短信")
    private Boolean isSms;

    @ApiModelProperty(value = "权限标识数组")
    private List<String> permissionsList;

    @ApiModelProperty(value = "商户星级")
    private Integer merStarLevel = 0;

    @ApiModelProperty(value = "商户小票打印开关：0关闭，1=手动打印，2=自动打印，3=自动和手动")
    private Integer merReceiptPrintingSwitch;

    @ApiModelProperty(value = "电子面单开关：0关闭，1=开启")
    private Integer electrPrintingSwitch;

    @ApiModelProperty(value = "商户id，0-平台")
    private Integer merId;
}
