package com.zbkj.common.response;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zbkj.common.model.seckill.SeckillActivityTime;
import com.zbkj.common.model.seckill.SeckillTimeInterval;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 秒杀活动分页响应对象
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
@ApiModel(value = "SeckillActivityPageResponse对象", description = "秒杀活动分页响应对象")
public class SeckillActivityPageResponse implements Serializable {

    private static final long serialVersionUID = 3285713110515203543L;

    @ApiModelProperty(value = "秒杀活动ID")
    private Integer id;

    @ApiModelProperty(value = "秒杀活动名称")
    private String name;

    @ApiModelProperty(value = "秒杀开始日期")
    private String startDate;

    @ApiModelProperty(value = "秒杀结束日期")
    private String endDate;

    @ApiModelProperty(value = "活动期间单笔下单购买数量，0不限制")
    private Integer oneQuota;

    @ApiModelProperty(value = "全部活动期间，用户购买总数限制，0不限制")
    private Integer allQuota;

    @ApiModelProperty(value = "商家星级")
    private Integer merStars;

    @ApiModelProperty(value = "商品类型,英文逗号拼接")
    private String proCategory;

    @ApiModelProperty(value = "开启状态: 0=关闭 1=开启")
    private Integer isOpen;

    @ApiModelProperty(value = "状态:0未开始，1进行中，2已结束")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "商品数量")
    private Integer productNum;

    @ApiModelProperty(value = "秒杀场次信息")
    private List<String> timeList;

    @ApiModelProperty(value = "平台商品分类名称字符")
    private String productCategoryNames;
}
