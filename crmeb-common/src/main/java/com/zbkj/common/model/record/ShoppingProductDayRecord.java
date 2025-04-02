package com.zbkj.common.model.record;

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
 * 商城商品日记录表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_shopping_product_day_record")
@ApiModel(value="ShoppingProductDayRecord对象", description="商城商品日记录表")
public class ShoppingProductDayRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "日期,yyyy-MM-dd")
    private String date;

    @ApiModelProperty(value = "新增商品数量")
    private Integer addProductNum;

    @ApiModelProperty(value = "浏览量")
    private Integer pageView;

    @ApiModelProperty(value = "收藏量")
    private Integer collectNum;

    @ApiModelProperty(value = "加购件数")
    private Integer addCartNum;

    @ApiModelProperty(value = "下单商品数")
    private Integer orderProductNum;

    @ApiModelProperty(value = "交易成功商品数")
    private Integer orderSuccessProductNum;


}
