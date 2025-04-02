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

/**
 * <p>
 * 秒杀活动时间表
 * </p>
 *
 * @author HZW
 * @since 2022-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_seckill_activity_time")
@ApiModel(value="SeckillActivityTime对象", description="秒杀活动时间表")
public class SeckillActivityTime implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "秒杀id")
    private Integer seckillId;

    @ApiModelProperty(value = "秒杀时段id")
    private Integer timeIntervalId;

    @ApiModelProperty(value = "秒杀开始日期")
    private Integer startDate;

    @ApiModelProperty(value = "秒杀结束日期")
    private Integer endDate;

    @ApiModelProperty(value = "秒杀开启时间")
    private Integer startTime;

    @ApiModelProperty(value = "秒杀结束时间")
    private Integer endTime;


}
