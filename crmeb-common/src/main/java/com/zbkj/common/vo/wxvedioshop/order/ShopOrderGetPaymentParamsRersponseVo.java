package com.zbkj.common.vo.wxvedioshop.order;

import com.alibaba.fastjson.annotation.JSONField;
import com.zbkj.common.vo.wxvedioshop.BaseResultResponseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** 自定义交易组件生成支付参数 response
 * @Auther: 大粽子
 * @Date: 2022/9/28 17:25
 * @Description: 描述对应的业务场景
 */
@Data
public class ShopOrderGetPaymentParamsRersponseVo extends BaseResultResponseVo {

    @ApiModelProperty(value = "时间戳")
    private String timeStamp;

    @ApiModelProperty(value = "获取随机字符串，长度要求在32位以内")
    private String nonceStr;

    @ApiModelProperty(value = "")
    @JSONField(name = "package")
    private String _package;

    @ApiModelProperty(value = "签名")
    private String paySign;

    @ApiModelProperty(value = "签名类型")
    private String signType;
}
