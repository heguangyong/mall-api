package com.zbkj.common.model.record;

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
 * 浏览记录表
 * </p>
 *
 * @author HZW
 * @since 2022-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_browse_record")
@ApiModel(value = "BrowseRecord对象", description = "浏览记录表")
public class BrowseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")

    private Integer uid;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "日期：年-月-日")
    private String date;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
