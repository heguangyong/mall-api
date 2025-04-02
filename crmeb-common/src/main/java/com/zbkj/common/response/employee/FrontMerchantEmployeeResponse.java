package com.zbkj.common.response.employee;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zbkj.common.model.merchant.Merchant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Auther: 移动端商户管理对应的返回数据
 * @Date: 2024/6/3 10:12
 * @Description: 描述对应的业务场景
 */
@Data
public class FrontMerchantEmployeeResponse implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "关联用户id")
    private Integer uid;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "权限: 1订单管理，2商品管理，3售后管理，4代客下单，5订单核销，5统计")
    private String role;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "当前商户信息")
    EmployeeMerchantResponse currentMerchant;
}
