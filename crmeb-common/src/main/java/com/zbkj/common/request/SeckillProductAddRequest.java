package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 秒杀商品添加请求对象
 * </p>
 *
 * @author HZW
 * @since 2022-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SeckillProductAddRequest对象", description = "秒杀商品添加请求对象")
public class SeckillProductAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "秒杀活动ID", required = true)
    @NotNull(message = "请选择秒杀活动")
    private Integer id;

    @ApiModelProperty(value = "秒杀商品列表")
    @NotEmpty(message = "请选择商品")
    private List<SeckillActivityProductRequest> productList;
}
