package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 一号通用户详情对象
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
@ApiModel(value = "OnePassUserInfoVo对象", description = "一号通用户详情对象")
public class OnePassUserInfoVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "消费")
    private String consume;

    @ApiModelProperty(value = "短信对象")
    private OnePassUserSmsVo sms;

    @ApiModelProperty(value = "商品采集对象")
    private OnePassUserCopyVo copy;

    @ApiModelProperty(value = "物流查询对象")
    private OnePassUserQueryVo query;

    @ApiModelProperty(value = "电子面单对象")
    private OnePassUserDumpVo dump;




}
