package com.zbkj.common.request.producttag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Auther: 大粽子
 * @Date: 2023/10/11 11:47
 * @Description: 商品标签维护对象
 */
@Data
public class ProductTagRequest {
    @ApiModelProperty(value = "主键 编辑时传递")
    private Integer id;

    @ApiModelProperty(value = "标签名称")
    @NotEmpty(message = "标签名称 不能为空")
    @Length(min = 1, max = 4, message = "标签名称长度1-4个字符")
    private String tagName;

    @ApiModelProperty(value = "标签说明")
    @Length(min = 0, max = 50, message = "标签说明不能大于50个字")
    private String tagNote;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序 不能为空")
    private Integer sort;

    @ApiModelProperty(value = "状态#0关闭|1开启")
    @NotNull(message = "状态 不能为空")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "开始时间 不能为空")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间 不能为空")
    private Date endTime;

    @ApiModelProperty(value = "标签在商城中的位置#0=标题下|1=标题前")
    @NotNull(message = "标签位置 不能为空")
    private Integer position;

    @ApiModelProperty(value = "商品参与类型 product=指定商品，brand=指定品牌，merchant=指定商户，category=指定商品分类")
    @NotEmpty(message = "商品参与类型 不能为空")
    private String playType;

    @ApiModelProperty(value = "参与的规则id集合，配合play_type使用,在内置标签情况下这里是配置的规则值")
    private String playProducts;

    @ApiModelProperty(value = "归属方#0=系统内置(不能删除)|1=平台自建")
    private Integer owner;

}
