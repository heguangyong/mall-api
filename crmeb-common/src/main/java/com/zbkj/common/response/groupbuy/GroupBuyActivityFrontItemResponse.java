package com.zbkj.common.response.groupbuy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author stivepeim
 * @date 2024/8/26 11:22
 * @description GroupBuyActivityFrontItemResponse
 */
@Data
public class GroupBuyActivityFrontItemResponse {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "成团总人数 - 真团")
    private Integer buyCount;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "拼团商品")
    private GroupBuyActivityProductResponse groupBuyActivityProductResponse;

}
