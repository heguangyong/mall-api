package com.zbkj.common.model.product;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 商户商品保障服务组合关联表
 * </p>
 *
 * @author HZW
 * @since 2022-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant_product_guarantee_group")
@ApiModel(value="MerchantProductGuaranteeGroup对象", description="商户商品保障服务组合关联表")
public class MerchantProductGuaranteeGroup implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "组合id")
    private Integer groupId;

    @ApiModelProperty(value = "保障服务id")
    private Integer gid;

    @ApiModelProperty(value = "显示状态")
    private Boolean isShow;


}
