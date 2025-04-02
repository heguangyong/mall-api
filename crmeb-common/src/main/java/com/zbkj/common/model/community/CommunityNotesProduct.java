package com.zbkj.common.model.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 社区笔记商品表
 * </p>
 *
 * @author HZW
 * @since 2023-03-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("eb_community_notes_product")
@ApiModel(value = "CommunityNotesProduct对象", description = "社区笔记商品表")
public class CommunityNotesProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("笔记ID")
    @TableField("note_id")
    private Integer noteId;

    @ApiModelProperty("商品ID")
    @TableField("product_id")
    private Integer productId;

    @ApiModelProperty("是否购买：0-未购买，1-已购买")
    @TableField("is_pay")
    private Integer isPay;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("商品名称")
    @TableField(exist = false)
    private String productName;

    @ApiModelProperty("商品图")
    @TableField(exist = false)
    private String productImage;

    @ApiModelProperty("商品售价")
    @TableField(exist = false)
    private BigDecimal price;

    @ApiModelProperty(value = "商品市场价")
    @TableField(exist = false)
    private BigDecimal otPrice;
}
