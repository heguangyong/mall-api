package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 秒杀活动保存请求对象
 * </p>
 *
 * @author HZW
 * @since 2022-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SeckillActivitySaveRequest对象", description = "秒杀活动保存请求对象")
public class SeckillActivitySaveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "秒杀活动ID，编辑时必填")
    private Integer id;

    @ApiModelProperty(value = "秒杀活动名称", required = true)
    @NotBlank(message = "秒杀活动名称不能为空")
    @Length(max = 30, message = "秒杀活动名称最多30个字符")
    private String name;

    @ApiModelProperty(value = "秒杀开始日期", required = true)
    @NotBlank(message = "秒杀开始日期不能为空")
    private String startDate;

    @ApiModelProperty(value = "秒杀结束日期", required = true)
    @NotBlank(message = "秒杀结束日期不能为空")
    private String endDate;

    @ApiModelProperty(value = "活动期间单笔下单购买数量，0不限制", required = true)
    @NotNull(message = "单笔限购不能为空")
    @Min(value = 0, message = "单笔限购最小值为0")
    private Integer oneQuota;

    @ApiModelProperty(value = "全部活动期间，用户购买总数限制，0不限制", required = true)
    @NotNull(message = "活动限购不能为空")
    @Min(value = 0, message = "活动限购最小值为0")
    private Integer allQuota;

    @ApiModelProperty(value = "商家星级", required = true)
    @NotNull(message = "商家星级不能为空")
    private Integer merStars;

    @ApiModelProperty(value = "商品类型(平台一级分类，0-表示全部),英文逗号拼接", required = true)
    @NotBlank(message = "商品类型不能为空")
    private String proCategory;

    @ApiModelProperty(value = "秒杀时段ID,英文逗号拼接", required = true)
    @NotBlank(message = "秒杀时段不能为空")
    private String timeIntervals;

    @ApiModelProperty(value = "秒杀商品列表")
    private List<SeckillActivityProductRequest> productList;
}
