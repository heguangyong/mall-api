package com.zbkj.common.vo.wxvedioshop.audit;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 接口调用请求说明
 *
 * 获取曾经提交的小程序审核资质
 *
 * 请求类目会返回多次的请求记录，请求品牌只会返回最后一次的提交记录
 *
 * 图片经过转链，请使用高版本 chrome 浏览器打开
 *
 * 如果曾经没有提交，没有储存历史文件，或是获取失败，接口会返回1050006
 *
 * 注：该接口返回的是曾经在小程序方提交过的审核，非组件的入驻审核！
 * @Auther: 大粽子
 * @Date: 2022/9/26 14:51
 * @Description: 描述对应的业务场景
 */
@Data
public class ShopAuditGetMiniAppCertificateRequestVo {

    @ApiModelProperty(value = "REQ_TYPE 1:类目 2:品牌")
    @NotEmpty
    private Integer req_type;
}
