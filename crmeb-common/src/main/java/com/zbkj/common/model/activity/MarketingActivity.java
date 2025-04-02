package com.zbkj.common.model.activity;

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
 * 营销活动表
 * </p>
 *
 * @author HZW
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_marketing_activity")
@ApiModel(value="MarketingActivity对象", description="营销活动表")
public class MarketingActivity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "活动ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "活动名称")
    private String name;

    @ApiModelProperty(value = "是否开启：0-未开启，1-已开启")
    private Boolean isOpen;

    @ApiModelProperty(value = "活动展示类型：1-轮播列表，2-大小格，3-大图模式")
    private Boolean type;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "0未删除1已删除")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "活动banner")
    private String banner;

    @ApiModelProperty(value = "活动简介")
    private String instruction;


}
