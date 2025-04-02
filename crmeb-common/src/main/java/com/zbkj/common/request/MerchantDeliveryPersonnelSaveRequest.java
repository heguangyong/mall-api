package com.zbkj.common.request;

import com.zbkj.common.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 商户配送人员保存请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/11/4
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantDeliveryPersonnelSaveRequest", description = "商户配送人员保存请求对象")
public class MerchantDeliveryPersonnelSaveRequest implements Serializable {

    private static final long serialVersionUID = 1531895840406567219L;

    @ApiModelProperty(value = "商户配送人员ID,新增时不填")
    private Integer id;

    @ApiModelProperty(value = "配送人员名称")
    @NotBlank(message = "请输入配送人员名称")
    @Length(max = 16, message = "配送人员名称不能超过16个字符")
    private String personnelName;

    @ApiModelProperty(value = "配送人员手机号")
    @NotBlank(message = "请输入配送人员手机号")
    @Pattern(regexp = RegularConstants.PHONE_TWO, message = "手机号码格式错误")
    private String personnelPhone;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}
