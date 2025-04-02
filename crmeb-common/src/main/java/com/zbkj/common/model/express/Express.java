package com.zbkj.common.model.express;

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
 * 快递公司表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_express")
@ApiModel(value="Express对象", description="快递公司表")
public class Express implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "快递公司id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "快递公司简称")
    private String code;

    @ApiModelProperty(value = "快递公司全称")
    private String name;

    @ApiModelProperty(value = "是否需要月结账号")
    private Boolean partnerId;

    @ApiModelProperty(value = "是否需要月结密码")
    private Boolean partnerKey;

    @ApiModelProperty(value = "是否需要取件网店")
    private Boolean net;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "网点名称")
    private String netName;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否显示")
    private Boolean isShow;

    @ApiModelProperty(value = "是否可用")
    private Boolean status;


}
