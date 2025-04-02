package com.zbkj.common.vo.wxvedioshop.order;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 生成订单Vo对象
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
public class ShopOrderProductInfoAddVo {

    /** 商家自定义商品ID */
    @TableField(value = "out_product_id")
    @ApiModelProperty(value = "外部商品spuid，可以不在商品库里，但是会响销售统计")
    @NotEmpty
    private String outProductId;

    /** 商家自定义商品skuID，可填空字符串（如果这个product_id下没有sku） */
    @TableField(value = "out_sku_id")
    @ApiModelProperty(value = "外部商品skuid，可以不在商品库里，但是会响销售统计")
    @NotEmpty
    private String outSkuId;

    /** 购买的数量 */
    @TableField(value = "product_cnt")
    @ApiModelProperty(value = "商品个数")
    @NotEmpty
    private Integer productCnt;

    /** 生成订单时商品的售卖价（单位：分），可以跟上传商品接口的价格不一致 */
    @TableField(value = "sale_price")
    @ApiModelProperty(value = "生成订单时商品的售卖价（单位：分），可以跟上传商品接口的价格不一致")
    @NotEmpty
    private Long salePrice;

    @ApiModelProperty(value = "sku总实付价 （单位：分），必须 > 0")
    private Long skuRealPrice;

    /** 生成订单时商品的头图 */
    @TableField(value = "head_img")
    @ApiModelProperty(value = "生成订单时商品的头图")
    @NotEmpty
    private String headImg;

    /** 生成订单时商品的标题 */
    @ApiModelProperty(value = "生成订单时商品的标题")
    @NotEmpty
    private String title;

    /** 绑定的小程序商品路径 */
    @ApiModelProperty(value = "绑定的小程序商品路径")
    @NotEmpty
    private String path;
}
