package com.zbkj.common.request.groupbuy;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * @author stivepeim
 * @date 2024/8/13 10:08
 * @description GroupBuyActivityRequest
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GroupBuyActivityRequest {

    @ApiModelProperty(value = "主键 - 更新时必填")
    private Integer id;

    @ApiModelProperty(value = "拼团活动名称")
    @NotBlank(message = "拼团团活动名称 必须填写")
    @Length(max = 100, message = "拼团团活动名称 不能超过100个字符")
    private String groupName;

    @ApiModelProperty(value = "开始时间")
    @NotNull(message = "开始时间不能为空")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private Date endTime;

    @ApiModelProperty(value = "成团有效期 小时数")
    @NotNull(message = "成团有效期 不能为空")
    @Min(value = 0, message = "成团有效期小于0")
    @Max(value = 240, message = "成团有效期大于240")
    private Integer validHour;

    @ApiModelProperty(value = "成团总人数 - 真团")
    @NotNull(message = "成团总人数 不能为空")
    @Min(value = 0, message = "成团总人数 不能小于0")
    private Integer buyCount;

    @ApiModelProperty(value = "购买上限")
    @NotNull(message = "购买上限 不能为空")
    @Min(value = 0, message = "购买上限 不能小于0")
    private Integer allQuota;

    @ApiModelProperty(value = "单次购买数量")
    @NotNull(message = "单次购买数量 不能为空")
    @Min(value = 0, message = "单次购买数量 不能小于0")
    private Integer oncQuota;

    @ApiModelProperty(value = "参与商品数量")
    @NotNull(message = "参与商品数量 不能为空")
    private Integer productCount;

    @ApiModelProperty(value = "凑团 0不可见，1可见")
    @NotNull(message = "凑团 不能为空")
    @Range(min = 0, max = 1, message = "凑团 0不可见，1可见")
    private Integer showGroup;

    @ApiModelProperty(value = "虚拟成团 假团 0关闭 1开启")
    @Range(min = 0, max = 1, message = "虚拟成团 假团 0关闭 1开启")
    private Integer fictiStatus;

    @ApiModelProperty(value = "活动商品")
//    @NotEmpty(message = "活动商品 不能为空")
    private List<GroupBuyActivitySkuRequest> groupBuySkuRequest;
}
