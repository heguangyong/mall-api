package com.zbkj.common.model.activity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 活动商品关联表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_activity_product")
@ApiModel(value="ActivityProduct对象", description="活动商品关联表")
public class ActivityProduct implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "活动id")
    private Integer aid;

    @ApiModelProperty(value = "商品id")
    private Integer proId;

    @ApiModelProperty(value = "活动商品图片")
    private String proImage;

    @ApiModelProperty(value = "排序")
    private Integer sort;


}
