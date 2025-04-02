package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 我的经验响应对象
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
@ApiModel(value = "UserMyExpResponse对象", description = "我的经验响应对象")
public class UserMyExpResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "当前经验")
    private Integer experience;

    @ApiModelProperty(value = "用户等级ID")
    private Integer level;

    @ApiModelProperty(value = "用户等级名称")
    private String userLevelName;

    @ApiModelProperty(value = "用户等级级别")
    private Integer grade;

    @ApiModelProperty(value = "用户等级图标")
    private String icon;

    @ApiModelProperty(value = "用户等级背景图")
    private String backImage;

    @ApiModelProperty(value = "用户等级文字背景色")
    private String backColor;

    @ApiModelProperty(value = "升级经验")
    private Integer upExperience = 0;

    @ApiModelProperty(value = "今天是否签到")
    private Boolean todaySign = false;

    @ApiModelProperty(value = "是否开启社区")
    private Boolean isOpenCommunity = false;

    @ApiModelProperty(value = "今天发布笔记数量")
    private Integer noteNum = 0;

    @ApiModelProperty(value = "获取经验笔记最大数量")
    private Integer noteMaxNum;

    @ApiModelProperty(value = "发布笔记经验成长值")
    private Integer noteExp;

    @ApiModelProperty(value = "用户等级开关")
    private Boolean userLevelSwitch = true;

    @ApiModelProperty(value = "今日获得经验值")
    private Integer todayExp;

    @ApiModelProperty(value = "下一等级名称")
    private String nextLevelName;

}
