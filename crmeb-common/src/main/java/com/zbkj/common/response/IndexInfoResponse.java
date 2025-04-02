package com.zbkj.common.response;

import com.zbkj.common.model.article.Article;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 首页信息响应对象
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
@ApiModel(value = "IndexInfoResponse对象", description = "首页信息响应对象")
public class IndexInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "首页banner滚动图")
    private List<HashMap<String, Object>> banner;

    @ApiModelProperty(value = "首页金刚区")
    private List<HashMap<String, Object>> menus;

    @ApiModelProperty(value = "新闻简报消息滚动")
    private List<Article> headline;

    @ApiModelProperty(value = "移动端顶部logo")
    private String logoUrl;

    @ApiModelProperty(value = "客服电话")
    private String consumerHotline;

    @ApiModelProperty(value = "客服H5链接")
    private String consumerH5Url;

    @ApiModelProperty(value = "客服类型:h5,hotline,message,email")
    private String consumerType;

    @ApiModelProperty(value = "店铺街开关")
    private String shopStreetSwitch;

    @ApiModelProperty(value = "是否开启微信公众号 网页授权 true开启 false不开启")
    private String wechatBrowserVisit;
}
