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
 * 页面设计首页响应对象
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
@ApiModel(value = "PageLayoutIndexResponse对象", description = "页面设计首页响应对象")
public class PageLayoutIndexResponse implements Serializable {

    private static final long serialVersionUID = 8350218800271787826L;

    @ApiModelProperty(value = "首页banner")
    private List<HashMap<String, Object>> indexBanner;

    @ApiModelProperty(value = "首页金刚区")
    private List<HashMap<String, Object>> indexMenu;

    @ApiModelProperty(value = "首页新闻")
    private List<Article> indexNews;

//    @ApiModelProperty(value = "首页logo") v 1.3 版本DIY 已经替代
//    private String indexLogo;

    @ApiModelProperty(value = "个人中心页服务")
    private List<HashMap<String, Object>> userMenu;

    @ApiModelProperty(value = "个人中心页banner")
    private List<HashMap<String, Object>> userBanner;

    @ApiModelProperty(value = "用户默认头像")
    private String userDefaultAvatar;
}
