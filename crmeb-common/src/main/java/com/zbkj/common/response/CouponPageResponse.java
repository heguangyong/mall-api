package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName CouponPageResponse
 * @Description 优惠券分页列表响应对象
 * @Author HZW
 * @Date 2023/5/12 10:48
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CouponPageResponse对象", description="优惠券分页列表响应对象")
public class CouponPageResponse implements Serializable {

    private static final long serialVersionUID = 1416170371569355563L;

    @ApiModelProperty(value = "优惠券表ID")
    private Integer id;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "类别 1-商家券, 2-商品券, 3-通用券，4-品类券，5-品牌券，6-跨店券")
    private Integer category;

    @ApiModelProperty(value = "领取类型 1-手动领取,2-商品赠送券,3-平台活动发放")
    private Integer receiveType;

    @ApiModelProperty(value = "优惠金额")
    private Long money;

    @ApiModelProperty(value = "最低消费，0代表不限制")
    private Long minPrice;

    @ApiModelProperty(value = "是否限量, 默认0 不限量， 1限量")
    private Boolean isLimited;

    @ApiModelProperty(value = "发放总数")
    private Integer total;

    @ApiModelProperty(value = "剩余数量")
    private Integer lastTotal;

    @ApiModelProperty(value = "已发数量")
    private Integer issuedNum;

    @ApiModelProperty(value = "使用数量")
    private Integer usedNum;

    @ApiModelProperty(value = "领取是否限时, 默认0-不限时，1-限时")
    private Boolean isTimeReceive;

    @ApiModelProperty(value = "可领取开始时间")
    private Date receiveStartTime;

    @ApiModelProperty(value = "可领取结束时间")
    private Date receiveEndTime;

    @ApiModelProperty(value = "状态（0：关闭，1：开启）")
    private Boolean status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
