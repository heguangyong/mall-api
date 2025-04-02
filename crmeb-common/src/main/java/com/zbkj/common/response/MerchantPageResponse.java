package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 平台端商户分页列表响应对象
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
@ApiModel(value="MerchantPageResponse对象", description="平台端商户分页列表响应对象")
public class MerchantPageResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商户ID")
    private Integer id;

    @ApiModelProperty(value = "商户名称")
    private String name;

    @ApiModelProperty(value = "商户分类ID")
    private Integer categoryId;

    @ApiModelProperty(value = "商户类型ID")
    private Integer typeId;

    @ApiModelProperty(value = "商户姓名")
    private String realName;

    @ApiModelProperty(value = "商户手机号")
    private String phone;

    @ApiModelProperty(value = "是否自营：0-非自营，1-自营")
    private Boolean isSelf;

    @ApiModelProperty(value = "是否推荐:0-不推荐，1-推荐")
    private Boolean isRecommend;

    @ApiModelProperty(value = "商户开关:0-关闭，1-开启")
    private Boolean isSwitch;

    @ApiModelProperty(value = "复制商品数量")
    private Integer copyProductNum;

    @ApiModelProperty(value = "商户星级1-5")
    private Integer starLevel;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "商户logo（横）")
    private String rectangleLogo;

    @ApiModelProperty(value = "商户封面图")
    private String coverImage;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "商户创建类型：admin-管理员创建，apply-商户入驻申请")
    private String createType;

    @ApiModelProperty(value = "商户创建管理员名称")
    private String createName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
