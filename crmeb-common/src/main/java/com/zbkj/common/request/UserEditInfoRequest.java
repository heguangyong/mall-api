package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户修改个人信息请求对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserEditInfoRequest对象", description = "用户修改个人信息请求对象")
public class UserEditInfoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户昵称")
    @NotBlank(message = "请填写用户昵称")
    @Length(max = 200, message = "用户昵称不能超过255个字符")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    @NotBlank(message = "请上传用户头像")
    @Length(max = 255, message = "用户头像不能超过255个字符")
    private String avatar;

    @ApiModelProperty(value = "生日")
    @NotBlank(message = "请填写生日信息")
    private String birthday;

    @ApiModelProperty(value = "省份")
    @NotBlank(message = "省份不能为空")
    @Length(max = 20, message = "省份不能超过20个字符")
    private String province;

    @ApiModelProperty(value = "城市")
    @NotBlank(message = "城市不能为空")
    @Length(max = 20, message = "城市不能超过20个字符")
    private String city;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    @NotNull(message = "性别不能为空")
    @Range(min = 0, max = 3, message = "未知的性别类型")
    private Integer sex;
}
