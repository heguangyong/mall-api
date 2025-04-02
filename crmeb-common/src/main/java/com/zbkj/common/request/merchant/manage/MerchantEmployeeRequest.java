package com.zbkj.common.request.merchant.manage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: 大粽子
 * @Date: 2024/5/24 10:32
 * @Description: 描述对应的业务场景
 */
@Data
public class MerchantEmployeeRequest {

    @ApiModelProperty(value = "主键 - 更新时必填")
    private Integer id;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "关联用户id")
    private Integer uid;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "权限-逗号风格: 1订单管理，2商品管理，3售后管理，4代客下单，5订单核销，5统计")
    private String role;

    @ApiModelProperty(value = "状态：0关，1开")
    private Integer status;
}
