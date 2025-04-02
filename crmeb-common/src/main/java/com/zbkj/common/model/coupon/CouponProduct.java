package com.zbkj.common.model.coupon;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 优惠券商品关联表
 * </p>
 *
 * @author HZW
 * @since 2022-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_coupon_product")
@ApiModel(value="CouponProduct对象", description="优惠券商品关联表")
public class CouponProduct implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "优惠券id")
    private Integer cid;

    @ApiModelProperty(value = "商品id")
    private Integer pid;


}
