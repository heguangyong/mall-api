package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.closing.MerchantClosingRecord;
import com.zbkj.common.request.*;
import com.zbkj.common.request.merchant.MerchantClosingSearchRequest;
import com.zbkj.common.response.MerchantClosingPlatformPageResponse;

import java.util.List;

/**
*  TransferRecordService 接口
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
public interface MerchantClosingRecordService extends IService<MerchantClosingRecord> {

    /**
     * 商户结算分页列表(平台)
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MerchantClosingPlatformPageResponse> getMerchantClosingPageListByPlatform(MerchantClosingSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 商户结算记录详情
     * @param closingNo 结算单号
     */
    MerchantClosingRecord getByClosingNo(String closingNo);

    /**
     * 商户结算申请审核
     * @param request 请求参数
     * @return Boolean
     */
    Boolean userClosingAudit(ClosingAuditRequest request);

    /**
     * 商户结算到账凭证
     */
    Boolean proof(ClosingProofRequest request);

    /**
     * 商户结算备注
     */
    Boolean remark(ClosingRemarkRequest request);

    /**
     * 商户结算分页列表(商户)
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MerchantClosingRecord> getMerchantClosingPageListByMerchant(MerchantClosingSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取某一天的所有数据
     * @param merId 商户id，0为所有商户
     * @param date 日期：年-月-日
     * @return List
     */
    List<MerchantClosingRecord> findByDate(Integer merId, String date);
}
