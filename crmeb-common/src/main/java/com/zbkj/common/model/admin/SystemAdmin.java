package com.zbkj.common.model.admin;

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
 * 系统管理员表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_admin")
@ApiModel(value="SystemAdmin对象", description="系统管理员表")
public class SystemAdmin implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "管理员ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "管理员账号")
    private String account;

    @ApiModelProperty(value = "管理员密码")
    private String pwd;

    @ApiModelProperty(value = "管理员姓名")
    private String realName;

    @ApiModelProperty(value = "管理员头像")
    private String headerImage;

    @ApiModelProperty(value = "管理员角色(menus_id)")
    private String roles;

    @ApiModelProperty(value = "管理员最后一次登录ip")
    private String lastIp;

    @ApiModelProperty(value = "登录次数")
    private Integer loginCount;

    @ApiModelProperty(value = "管理员级别")
    private Integer level;

    @ApiModelProperty(value = "管理员状态 1有效，0无效")
    private Boolean status;

    @ApiModelProperty(value = "是否删除：1-删除")
    private Boolean isDel;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "是否接收短信")
    private Boolean isSms;

    @ApiModelProperty(value = "管理员类型：1= 平台超管, 2=商户超管, 3=系统管理员，4=商户管理员")
    private Integer type;

    @ApiModelProperty(value = "商户id，0-平台")
    private Integer merId;

    @ApiModelProperty(value = "后台管理员添加时间")
    private Date createTime;

    @ApiModelProperty(value = "后台管理员最后一次登录时间")
    private Date updateTime;


}
