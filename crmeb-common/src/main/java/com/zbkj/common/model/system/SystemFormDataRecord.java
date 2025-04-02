package com.zbkj.common.model.system;

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
 * 系统表单记录表
 * </p>
 *
 * @author HZW
 * @since 2024-08-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_form_data_record")
@ApiModel(value="SystemFormDataRecord对象", description="系统表单记录表")
public class SystemFormDataRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "系统表单ID")
    private Integer formId;

    @ApiModelProperty(value = "表单所有的key")
    private String allKeys;

    @ApiModelProperty(value = "表单所有的value")
    private String allValue;

    @ApiModelProperty(value = "表单内容")
    private String formValue;

    @ApiModelProperty(value = "关联ID")
    private Integer linkId;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDelete;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
