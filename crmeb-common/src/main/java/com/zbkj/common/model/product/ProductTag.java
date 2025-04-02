package com.zbkj.common.model.product;

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
 *
 * </p>
 *
 * @author dazongzi
 * @since 2023-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_product_tag")
@ApiModel(value="ProductTag对象", description="")
public class ProductTag implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "标签说明")
    private String tagNote;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态#0关闭|1开启")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "标签在商城中的位置#0=标题下|1=标题前")
    private Integer position;

    @ApiModelProperty(value = "商品参与类型 product=指定商品，brand=指定品牌，merchant=指定商户，category=指定商品分类")
    private String playType;

    @ApiModelProperty(value = "参与的规则id集合，配合play_type使用,在内置标签情况下这里是配置的规则值")
    private String playProducts;

    @ApiModelProperty(value = "归属方#0=系统内置(不能删除)|1=平台自建")
    private Integer owner;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
