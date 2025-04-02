package com.zbkj.common.model.seckill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 秒杀活动表
 * </p>
 *
 * @author HZW
 * @since 2022-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_seckill_activity")
@ApiModel(value="SeckillActivity对象", description="秒杀活动表")
public class SeckillActivity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "秒杀活动ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "秒杀活动名称")
    private String name;

    @ApiModelProperty(value = "秒杀开始日期")
    private String startDate;

    @ApiModelProperty(value = "秒杀结束日期")
    private String endDate;

    @ApiModelProperty(value = "活动期间单笔下单购买数量，0不限制")
    private Integer oneQuota;

    @ApiModelProperty(value = "全部活动期间，用户购买总数限制，0不限制")
    private Integer allQuota;

    @ApiModelProperty(value = "商家星级")
    private Integer merStars;

    @ApiModelProperty(value = "商品类型,英文逗号拼接")
    private String proCategory;

    @ApiModelProperty(value = "开启状态: 0=关闭 1=开启")
    private Integer isOpen;

    @ApiModelProperty(value = "状态:0未开始，1进行中，2已结束")
    private Integer status;

    @ApiModelProperty(value = "删除标记 0=未删除 1=删除")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
