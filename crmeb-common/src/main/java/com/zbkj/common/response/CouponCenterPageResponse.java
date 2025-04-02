package com.zbkj.common.response;

import com.zbkj.common.vo.SimpleProductVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName CouponCenterPageResponse
 * @Description 优惠券领券中心分页响应对象
 * @Author HZW
 * @Date 2023/5/18 14:25
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CouponCenterPageResponse对象", description = "CouponCenterPageResponse")
public class CouponCenterPageResponse implements Serializable {

    private static final long serialVersionUID = -1136504859561930672L;

    @ApiModelProperty(value = "优惠券表ID")
    private Integer id;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "类别 1-商家券, 2-商品券, 3-通用券，4-品类券，5-品牌券，6-跨店券")
    private Integer category;

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

    @ApiModelProperty(value = "领取是否限时, 默认0-不限时，1-限时")
    private Boolean isTimeReceive;

    @ApiModelProperty(value = "可领取开始时间")
    private Date receiveStartTime;

    @ApiModelProperty(value = "可领取结束时间")
    private Date receiveEndTime;

    @ApiModelProperty(value = "是否固定使用时间, 默认0-否，1-使用固定时间")
    private Boolean isFixedTime;

    @ApiModelProperty(value = "可使用时间范围 开始时间")
    private Date useStartTime;

    @ApiModelProperty(value = "可使用时间范围 结束时间")
    private Date useEndTime;

    @ApiModelProperty(value = "天数")
    private Integer day;

    @ApiModelProperty(value = "商品列表")
    private List<SimpleProductVo> productVoList;

    @ApiModelProperty(value = "商品品类名称")
    private String productCategoryName;

    @ApiModelProperty(value = "商品品牌名称")
    private String productBrandName;

    @ApiModelProperty(value = "用户是否领取")
    private Boolean isUserReceive = false;
}
