package com.zbkj.common.model.sgin;

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
 * 用户签到记录表
 * </p>
 *
 * @author HZW
 * @since 2022-09-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_sign_record")
@ApiModel(value="UserSignRecord对象", description="用户签到记录表")
public class UserSignRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "签到积分")
    private Integer integral;

    @ApiModelProperty(value = "签到经验")
    private Integer experience;

    @ApiModelProperty(value = "连续签到天数")
    private Integer day;

    @ApiModelProperty(value = "连续签到奖励积分")
    private Integer awardIntegral;

    @ApiModelProperty(value = "连续签奖励到经验")
    private Integer awardExperience;

    @ApiModelProperty(value = "签到日期，yyyy-MM-dd")
    private String date;

    @ApiModelProperty(value = "备注说明")
    private String mark;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;


}
