package com.zbkj.common.response.groupbuy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 拼团开团记录详情响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/9/26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "GroupBuyRecordDetailResponse", description = "拼团开团记录详情响应对象")
public class GroupBuyRecordDetailResponse implements Serializable {

    private static final long serialVersionUID = -6375894771668563418L;

    @ApiModelProperty(value = "拼团开团记录ID")
    private Integer groupBuyingId;

    @ApiModelProperty(value = "状态：0=进行中，10=已成功，-1=已失败")
    private Integer recordStatus;

    @ApiModelProperty(value = "成团总人数")
    private Integer buyingCountNum;

    @ApiModelProperty(value = "已参团人数")
    private Integer yetBuyingNum;

    @ApiModelProperty(value = "开始时间")
    private Date createTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "拼团商品图")
    private String productImage;

    @ApiModelProperty(value = "拼团商品名称")
    private String productName;

    @ApiModelProperty(value = "成员数据列表")
    List<GroupBuyRecordDetailMemberResponse> memberDataList;
}
