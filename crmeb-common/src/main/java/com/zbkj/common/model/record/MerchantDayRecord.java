package com.zbkj.common.model.record;

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
 * 商户日记录表
 * </p>
 *
 * @author HZW
 * @since 2022-11-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant_day_record")
@ApiModel(value = "MerchantDayRecord对象", description = "商户日记录表")
public class MerchantDayRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "日期，yyyy-MM-dd")
    private String date;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "访客量")
    private Integer visitors;


}
