package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 移动端商品详情请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/7/16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductFrontDetailRequest", description = "移动端商品详情请求对象")
public class ProductFrontDetailRequest implements Serializable {

    private static final long serialVersionUID = 4691485469798817225L;

    @ApiModelProperty(value = "商品ID")
    @NotNull(message = "商品ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "营销类型：0=基础商品,1=秒杀,2=拼团")
    private Integer marketingType = 0;

    @ApiModelProperty(value = "基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品")
    private Integer type = 0;

    @ApiModelProperty(value = "拼团活动id 当 marketingType = 2 时 必填")
    private Integer groupActivityId;

    @ApiModelProperty(value = "拼团记录id，营销类型2=拼团 时必填 0=开团 1=拼团")
    private Integer groupBuyRecordId = 0;
}
