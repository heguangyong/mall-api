package com.zbkj.common.vo.wxvedioshop.audit;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 上传类目资质Item
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
public class ShopAuditCategoryRequestItemVo {

    @ApiModelProperty(value = "营业执照或组织机构代码证，图片url")
    @NotEmpty
    private String license;

    @ApiModelProperty(value = "类目层级对象")
    @NotEmpty
    private ShopAuditCategoryRequestItemDataVo category_info;

    @ApiModelProperty(value = "类目使用场景,1:视频号 ，3:订单中心（非视频号订单中心，未明确开通此场景的商家请勿传值）。 组件开通流程中以及未接入场景时，请保持为空\"scene_group_list\":[]")
    @NotEmpty
    private List<Integer> scene_group_list;
}
