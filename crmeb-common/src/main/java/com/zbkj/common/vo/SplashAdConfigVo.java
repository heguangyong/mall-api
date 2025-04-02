package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 开屏广告配置VO对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/11/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SplashAdConfigVo", description = "开屏广告配置VO对象")
public class SplashAdConfigVo implements Serializable {

    private static final long serialVersionUID = 4458102955982467380L;

    @ApiModelProperty(value = "开屏广告开关")
    @NotNull(message = "广告开关不能为空")
    private Integer splashAdSwitch;

    @ApiModelProperty(value = "开屏广告展示时间，单位：秒")
    @NotNull(message = "广告展示时间不能为空")
    private Integer splashAdShowTime;

    @ApiModelProperty(value = "开屏广告展示间隔，单位：小时")
    @NotNull(message = "广告展示间隔不能为空")
    private Integer splashAdShowInterval;

    @ApiModelProperty(value = "开屏广告数据列表")
    private List<SplashAdDataVo> adList;
}
