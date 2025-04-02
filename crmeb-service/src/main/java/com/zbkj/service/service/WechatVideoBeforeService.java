package com.zbkj.service.service;

import com.zbkj.common.request.wxvedio.image.ShopUploadImgRequest;
import com.zbkj.common.vo.wxvedioshop.WechatVideoUploadImageResponseVo;
import com.zbkj.common.vo.wxvedioshop.audit.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 接入商品前必须接口
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
public interface WechatVideoBeforeService {
    // 获取类目详情 在其他商品中实现了

    /**
     * 上传图片
     * @return 图片上传结果
     */
    WechatVideoUploadImageResponseVo shopImgUpload(ShopUploadImgRequest request);

    /**
     * 上传品牌信息
     * @param requestVo 待上传品牌参数
     * @return 审核单Id
     */
    ShopAuditBrandResponseVo shopAuditBrand(ShopAuditBrandRequestVo requestVo);

    /**
     * 上传类目资质
     * @param requestVo 类目资质参数
     * @return 类目资质结果
     */
    ShopAuditCategoryResponseVo shopAuditCategory(ShopAuditCategoryRequestVo requestVo);

    /**
     * 获取类目审核结果
     * @param request 待审核类目id
     * @return 审核结果
     */
    ShopAuditResultResponseVo shopAuditResult(ShopAuditResultCategoryAndBrandRequestVo request);


    /**
     * 获取小程序资质
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
     * @param requestVo
     * @return
     */
    ShopAuditGetMiniAppCertificateRequestVo shopAuditGetMinCertificate(ShopAuditGetMiniAppCertificateRequestVo requestVo);


    WechatVideoUploadImageResponseVo shopImgUploadTest(MultipartFile file, Integer respType, Integer uploadType, String imgUrl);
}
