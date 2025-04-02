package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员响应对象
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
@ApiModel(value="SystemAdminResponse对象", description="管理员响应对象")
public class SystemAdminResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "后台管理员表ID")
    private Integer id;

    @ApiModelProperty(value = "后台管理员账号")
    private String account;

    @ApiModelProperty(value = "后台管理员姓名")
    private String realName;

    @ApiModelProperty(value = "后台管理员权限(menus_id)")
    private String roles;

    @ApiModelProperty(value = "后台管理员权限")
    private String roleNames;

    @ApiModelProperty(value = "后台管理员最后一次登录ip")
    private String lastIp;

    @ApiModelProperty(value = "后台管理员最后一次登录时间")
    private Date lastTime;

    @ApiModelProperty(value = "后台管理员状态 1有效0无效")
    private Boolean status;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "管理员类型：1= 平台超管, 2=商户超管, 3=系统管理员，4=商户管理员")
    private Integer type;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "是否接收短信")
    private Boolean isSms;
}
