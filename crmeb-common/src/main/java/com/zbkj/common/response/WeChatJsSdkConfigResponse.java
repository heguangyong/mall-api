package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 微信公众号js-sdk响应对象
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
@ApiModel(value="WeChatJsSdkConfigResponse对象", description="微信公众号js-sdk响应对象对象")
public class WeChatJsSdkConfigResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "url")
    private String url;

    @ApiModelProperty(value = "生成签名的随机串")
    private String nonceStr;

    @ApiModelProperty(value = "生成签名的时间戳")
    private Long timestamp;

    @ApiModelProperty(value = "签名")
    private String signature;

    @ApiModelProperty(value = "需要使用的 JS 接口列表")
    private List<String> jsApiList;

    @ApiModelProperty(value = "开启调试模式,调用的所有 api 的返回值会在客户端 alert 出来，若要查看传入的参数，可以在 pc 端打开，参数信息会通过 log 打出，仅在 pc 端时才会打印。")
    private Boolean debug;

    @ApiModelProperty(value = "公众号的唯一标识")
    private String appId;
}
