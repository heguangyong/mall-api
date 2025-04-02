package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 平台端商户分页列表表头数量响应对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MerchantHeaderNumResponse对象", description="平台端商户分页列表表头数量响应对象")
public class MerchantHeaderNumResponse implements Serializable {

    private static final long serialVersionUID=1L;

    public MerchantHeaderNumResponse() {}

    public MerchantHeaderNumResponse(Integer openNum, Integer closeNum) {
        this.openNum = openNum;
        this.closeNum = closeNum;
    }

    @ApiModelProperty(value = "开启的商户数量")
    private Integer openNum;

    @ApiModelProperty(value = "关闭的商户数量")
    private Integer closeNum;
}
