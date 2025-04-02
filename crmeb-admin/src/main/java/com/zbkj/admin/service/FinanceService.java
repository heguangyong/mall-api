package com.zbkj.admin.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.bill.SummaryFinancialStatements;
import com.zbkj.common.model.user.UserClosing;
import com.zbkj.common.request.*;
import com.zbkj.common.request.merchant.MerchantClosingApplyRequest;
import com.zbkj.common.request.merchant.MerchantClosingSearchRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.vo.MerchantClosingConfigVo;

/**
 *  财务服务
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
public interface FinanceService {

    /**
     * 用户结算分页列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserClosing> getUserClosingPageList(UserClosingSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 用户结算申请审核
     * @param request 请求参数
     * @return Boolean
     */
    Boolean userClosingAudit(ClosingAuditRequest request);

    /**
     * 用户结算到账凭证
     */
    Boolean userClosingProof(ClosingProofRequest request);

    /**
     * 用户结算备注
     */
    Boolean userClosingRemark(ClosingRemarkRequest request);

    /**
     * 获取商户结算设置
     */
    MerchantClosingConfigVo getMerchantClosingConfig();

    /**
     * 编辑商户结算设置
     */
    Boolean merchantClosingConfigEdit(MerchantClosingConfigVo request);

    /**
     * 商户结算分页列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MerchantClosingPlatformPageResponse> getMerchantClosingPageList(MerchantClosingSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 商户结算记录详情
     * @param closingNo 结算单号
     * @return MerchantClosingInfoResponse
     */
    MerchantClosingInfoResponse getMerchantClosingDetailByPlatform(String closingNo);

    /**
     * 商户结算申请审核
     * @param request 请求参数
     * @return Boolean
     */
    Boolean merchantClosingAudit(ClosingAuditRequest request);

    /**
     * 商户结算到账凭证
     */
    Boolean merchantClosingProof(ClosingProofRequest request);

    /**
     * 商户结算备注
     */
    Boolean merchantClosingRemark(ClosingRemarkRequest request);

    /**
     * 获取结算申请基础信息
     * @return MerchantClosingBaseInfoResponse
     */
    MerchantClosingBaseInfoResponse getClosingBaseInfo();

    /**
     * 商户结算申请
     * @param request 申请参数
     * @return Boolean
     */
    Boolean merchantClosingApply(MerchantClosingApplyRequest request);

    /**
     * 商户端结算记录分页列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MerchantClosingPageResponse> getMerchantClosingRecordPageList(MerchantClosingSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 商户端结算记录详情
     * @param closingNo 结算单号
     * @return MerchantClosingDetailResponse
     */
    MerchantClosingDetailResponse getMerchantClosingDetailByMerchant(String closingNo);


    /**
     * 资金监控分页列表
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<FundsFlowResponse> getFundsFlow(FundsFlowRequest request, PageParamRequest pageParamRequest);

    /**
     * 商户端资金监控分页列表
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<FundsFlowResponse> getMerchantFundsFlow(FundsFlowRequest request, PageParamRequest pageParamRequest);

    /**
     * 平台端日帐单分页列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<PlatformStatementResponse> getPlatformDailyStatementList(String dateLimit, PageParamRequest pageParamRequest);

    /**
     * 平台端月帐单分页列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<PlatformStatementResponse> getPlatformMonthStatementList(String dateLimit, PageParamRequest pageParamRequest);

    /**
     * 商户端日帐单分页列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MerchantStatementResponse> getMerchantDailyStatementList(String dateLimit, PageParamRequest pageParamRequest);

    /**
     * 商户端月帐单分页列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MerchantStatementResponse> getMerchantMonthStatementList(String dateLimit, PageParamRequest pageParamRequest);

    /**
     * 财务流水汇总列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<SummaryFinancialStatements> summaryFinancialStatements(String dateLimit, PageParamRequest pageParamRequest);
}
