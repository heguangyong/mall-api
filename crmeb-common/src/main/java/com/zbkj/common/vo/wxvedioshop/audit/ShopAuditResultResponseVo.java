package com.zbkj.common.vo.wxvedioshop.audit;

import com.zbkj.common.vo.wxvedioshop.BaseResultResponseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询审核结果 Response
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class ShopAuditResultResponseVo extends BaseResultResponseVo {

    private ItemData data;

    @Data
    class ItemData{

        @ApiModelProperty(value = "审核状态, 0：审核中，1：审核成功，9：审核拒绝")
        private Integer status;

        @ApiModelProperty(value = "如果是品牌审核，返回brand_id")
        private Integer brand_id;

        @ApiModelProperty(value = "如果审核拒绝，返回拒绝原因")
        private String reject_reason;
    }
}
