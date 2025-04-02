package com.zbkj.common.vo.wxvedioshop.audit;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 上传类目资质 itemData
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
public class ShopAuditCategoryRequestItemDataVo {

        @ApiModelProperty(value = "一级类目，字符类型，最长不超过10")
        @NotEmpty
        @Length(max = 10, message = "一级类目，字符类型，最长不超过10")
        private Integer level1;

        @ApiModelProperty(value = "二级类目，字符类型，最长不超过10")
        @Length(max = 10, message = "二级类目，字符类型，最长不超过10")
        private Integer level2;

        @ApiModelProperty(value = "三级类目，字符类型，最长不超过10")
        @Length(max = 10, message = "三级类目，字符类型，最长不超过10")
        private Integer level3;

        @ApiModelProperty(value = "资质材料，图片url，图片类型，最多不超过10张")
        @Size(max = 10, message = "资质材料，图片url，图片类型，最多不超过10张")
        @NotEmpty
        private List<String> certificate;
}
