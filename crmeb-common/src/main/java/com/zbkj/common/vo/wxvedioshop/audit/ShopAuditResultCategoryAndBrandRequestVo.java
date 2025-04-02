package com.zbkj.common.vo.wxvedioshop.audit;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Auther: 大粽子
 * @Date: 2022/9/26 15:39
 * @Description: 根据审核id，查询品牌和类目的审核结果。
 */

@Data
public class ShopAuditResultCategoryAndBrandRequestVo {

    @ApiModelProperty(value = "提交审核时返回的id")
    @NotEmpty
    private String audit_id;
}
