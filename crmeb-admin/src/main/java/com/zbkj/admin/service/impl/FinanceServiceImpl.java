package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.FinanceService;
import com.zbkj.common.constants.ClosingConstant;
import com.zbkj.common.constants.OrderConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.bill.*;
import com.zbkj.common.model.closing.MerchantClosingRecord;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantInfo;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserClosing;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.request.merchant.MerchantClosingApplyRequest;
import com.zbkj.common.request.merchant.MerchantClosingSearchRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.MerchantClosingConfigVo;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 财务服务实现类
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
@Service
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private BillService billService;
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantBillService merchantBillService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantInfoService merchantInfoService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemAdminService systemAdminService;
    @Autowired
    private PlatformDailyStatementService platformDailyStatementService;
    @Autowired
    private PlatformMonthStatementService platformMonthStatementService;
    @Autowired
    private MerchantDailyStatementService merchantDailyStatementService;
    @Autowired
    private MerchantMonthStatementService merchantMonthStatementService;
    @Autowired
    private UserClosingService userClosingService;
    @Autowired
    private MerchantClosingRecordService merchantClosingService;
    @Autowired
    private SummaryFinancialStatementsService summaryFinancialStatementsService;
    @Autowired
    private MerchantBalanceRecordService merchantBalanceRecordService;

    /**
     * 用户结算分页列表
     *
     * @param request          搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserClosing> getUserClosingPageList(UserClosingSearchRequest request, PageParamRequest pageParamRequest) {
        return userClosingService.getPlatformPage(request, pageParamRequest);
    }

    /**
     * 用户结算申请审核
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean userClosingAudit(ClosingAuditRequest request) {
        if (request.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_FAIL) && StrUtil.isBlank(request.getRefusalReason())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "驳回时请填写驳回原因");
        }
        return userClosingService.userClosingAudit(request);
    }

    /**
     * 用户结算到账凭证
     */
    @Override
    public Boolean userClosingProof(ClosingProofRequest request) {
        return userClosingService.proof(request);
    }

    /**
     * 用户结算备注
     */
    @Override
    public Boolean userClosingRemark(ClosingRemarkRequest request) {
        return userClosingService.remark(request);
    }

    /**
     * 获取商户结算设置
     */
    @Override
    public MerchantClosingConfigVo getMerchantClosingConfig() {
        ArrayList<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.MERCHANT_GUARANTEED_AMOUNT);
        keyList.add(SysConfigConstants.MERCHANT_TRANSFER_MIN_AMOUNT);
        keyList.add(SysConfigConstants.MERCHANT_TRANSFER_MAX_AMOUNT);
        keyList.add(SysConfigConstants.MERCHANT_SHARE_NODE);
        keyList.add(SysConfigConstants.MERCHANT_SHARE_FREEZE_TIME);
        MyRecord myRecord = systemConfigService.getValuesByKeyList(keyList);

        BigDecimal guaranteedAmount = StrUtil.isNotBlank(myRecord.getStr(SysConfigConstants.MERCHANT_GUARANTEED_AMOUNT)) ? new BigDecimal(myRecord.getStr(SysConfigConstants.MERCHANT_GUARANTEED_AMOUNT)) : BigDecimal.ZERO;
        BigDecimal transferMinAmount = StrUtil.isNotBlank(myRecord.getStr(SysConfigConstants.MERCHANT_TRANSFER_MIN_AMOUNT)) ? new BigDecimal(myRecord.getStr(SysConfigConstants.MERCHANT_TRANSFER_MIN_AMOUNT)) : BigDecimal.ZERO;
        BigDecimal transferMaxAmount = StrUtil.isNotBlank(myRecord.getStr(SysConfigConstants.MERCHANT_TRANSFER_MAX_AMOUNT)) ? new BigDecimal(myRecord.getStr(SysConfigConstants.MERCHANT_TRANSFER_MAX_AMOUNT)) : BigDecimal.ZERO;
        String merchantShareNode = myRecord.getStr(SysConfigConstants.MERCHANT_SHARE_NODE);
        String merchantShareFreezeTime = myRecord.getStr(SysConfigConstants.MERCHANT_SHARE_FREEZE_TIME);
        MerchantClosingConfigVo configVo = new MerchantClosingConfigVo();
        configVo.setGuaranteedAmount(guaranteedAmount);
        configVo.setTransferMinAmount(transferMinAmount);
        configVo.setTransferMaxAmount(transferMaxAmount);
        configVo.setMerchantShareNode(StrUtil.isBlank(merchantShareNode) ? "complete" : merchantShareNode);
        configVo.setMerchantShareFreezeTime(StrUtil.isBlank(merchantShareFreezeTime) ? 0 : Integer.parseInt(merchantShareFreezeTime));
        return configVo;
    }

    /**
     * 编辑商户结算设置
     */
    @Override
    public Boolean merchantClosingConfigEdit(MerchantClosingConfigVo request) {
        return transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.MERCHANT_GUARANTEED_AMOUNT, request.getGuaranteedAmount().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.MERCHANT_TRANSFER_MIN_AMOUNT, request.getTransferMinAmount().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.MERCHANT_TRANSFER_MAX_AMOUNT, request.getTransferMaxAmount().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.MERCHANT_SHARE_NODE, request.getMerchantShareNode());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.MERCHANT_SHARE_FREEZE_TIME, request.getMerchantShareFreezeTime().toString());
            return Boolean.TRUE;
        });
    }

    /**
     * 商户结算分页列表
     *
     * @param request          搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantClosingPlatformPageResponse> getMerchantClosingPageList(MerchantClosingSearchRequest request, PageParamRequest pageParamRequest) {
        return merchantClosingService.getMerchantClosingPageListByPlatform(request, pageParamRequest);
    }

    /**
     * 商户结算记录详情
     *
     * @param closingNo 结算单号
     * @return MerchantClosingInfoResponse
     */
    @Override
    public MerchantClosingInfoResponse getMerchantClosingDetailByPlatform(String closingNo) {
        MerchantClosingRecord closingRecord = merchantClosingService.getByClosingNo(closingNo);
        Merchant merchant = merchantService.getByIdException(closingRecord.getMerId());
        MerchantClosingInfoResponse response = new MerchantClosingInfoResponse();
        BeanUtils.copyProperties(closingRecord, response);
        response.setMerName(merchant.getName());
        response.setBalance(merchant.getBalance());
        response.setRealName(response.getClosingName());
        return response;
    }

    /**
     * 商户结算申请审核
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean merchantClosingAudit(ClosingAuditRequest request) {
        if (request.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_FAIL) && StrUtil.isBlank(request.getRefusalReason())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "驳回时请填写驳回原因");
        }
        return merchantClosingService.userClosingAudit(request);
    }

    /**
     * 商户结算到账凭证
     */
    @Override
    public Boolean merchantClosingProof(ClosingProofRequest request) {
        return merchantClosingService.proof(request);
    }

    /**
     * 商户结算备注
     */
    @Override
    public Boolean merchantClosingRemark(ClosingRemarkRequest request) {
        return merchantClosingService.remark(request);
    }

    /**
     * 商户端获取转账申请基础信息
     *
     * @return MerchantClosingBaseInfoResponse
     */
    @Override
    public MerchantClosingBaseInfoResponse getClosingBaseInfo() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        // 商户转账配置信息
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(systemAdmin.getMerId());
        switch (merchantInfo.getSettlementType()) {
            case ClosingConstant.CLOSING_TYPE_BANK:
                if (StrUtil.isBlank(merchantInfo.getBankUserName())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先在配置中进行商户转账信息配置");
                }
                break;
            case ClosingConstant.CLOSING_TYPE_ALIPAY:
                if (StrUtil.isBlank(merchantInfo.getAlipayCode())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先在配置中进行商户转账信息配置");
                }
                break;
            case ClosingConstant.CLOSING_TYPE_WECHAT:
                if (StrUtil.isBlank(merchantInfo.getWechatCode())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先在配置中进行商户转账信息配置");
                }
                break;
        }

        MerchantClosingBaseInfoResponse response = new MerchantClosingBaseInfoResponse();
        BeanUtils.copyProperties(merchantInfo, response);

        Merchant merchant = merchantService.getByIdException(systemAdmin.getMerId());
        String guaranteedAmountStr = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_GUARANTEED_AMOUNT);
        String transferMinAmount = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_TRANSFER_MIN_AMOUNT);
        String transferMaxAmount = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_TRANSFER_MAX_AMOUNT);
        BigDecimal guaranteedAmount = new BigDecimal(guaranteedAmountStr);
        response.setGuaranteedAmount(guaranteedAmount);
        response.setTransferMinAmount(new BigDecimal(transferMinAmount));
        response.setTransferMaxAmount(new BigDecimal(transferMaxAmount));
        response.setBalance(merchant.getBalance());
        if (merchant.getBalance().compareTo(guaranteedAmount) < 1) {
            response.setTransferBalance(BigDecimal.ZERO);
        } else {
            response.setTransferBalance(merchant.getBalance().subtract(guaranteedAmount));
        }

        response.setFreezeAmount(merchantBalanceRecordService.getFreezeAmountByMerId(merchant.getId()));
        return response;
    }


    /**
     * 商户结算申请
     *
     * @param request 申请参数
     * @return Boolean
     */
    @Override
    public Boolean merchantClosingApply(MerchantClosingApplyRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Merchant merchant = merchantService.getByIdException(systemAdmin.getMerId());
        // 查询转账最低、最高金额进行判断
        String guaranteedAmountStr = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_GUARANTEED_AMOUNT);
        String transferMinAmount = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_TRANSFER_MIN_AMOUNT);
        String transferMaxAmount = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_TRANSFER_MAX_AMOUNT);
        if (merchant.getBalance().compareTo(new BigDecimal(guaranteedAmountStr)) < 1) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户余额不足提现保证金");
        }
        if (request.getAmount().compareTo(new BigDecimal(transferMinAmount)) < 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "提现金额小于最小提现金额");
        }
        if (request.getAmount().compareTo(new BigDecimal(transferMaxAmount)) > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "提现金额大于最高提现金额");
        }

        MerchantInfo merchantInfo = merchantInfoService.getByMerId(merchant.getId());
        switch (merchantInfo.getSettlementType()) {
            case ClosingConstant.CLOSING_TYPE_BANK:
                if (StrUtil.isBlank(merchantInfo.getBankUserName())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先在配置中进行商户转账信息配置");
                }
                break;
            case ClosingConstant.CLOSING_TYPE_ALIPAY:
                if (StrUtil.isBlank(merchantInfo.getAlipayCode())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先在配置中进行商户转账信息配置");
                }
                break;
            case ClosingConstant.CLOSING_TYPE_WECHAT:
                if (StrUtil.isBlank(merchantInfo.getWechatCode())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先在配置中进行商户转账信息配置");
                }
                break;
        }
        MerchantClosingRecord record = new MerchantClosingRecord();
        record.setMerId(merchant.getId());
        record.setAmount(request.getAmount());
        record.setClosingType(request.getClosingType());
        switch (request.getClosingType()) {
            case ClosingConstant.CLOSING_TYPE_BANK:
                record.setClosingName(merchantInfo.getBankUserName());
                record.setClosingBank(merchantInfo.getBankName());
                record.setClosingBankCard(merchantInfo.getBankCard());
                break;
            case ClosingConstant.CLOSING_TYPE_ALIPAY:
                record.setAlipayAccount(merchantInfo.getAlipayCode());
                record.setPaymentCode(merchantInfo.getAlipayQrcodeUrl());
                record.setClosingName(merchantInfo.getRealName());
                break;
            case ClosingConstant.CLOSING_TYPE_WECHAT:
                record.setWechatNo(merchantInfo.getWechatCode());
                record.setPaymentCode(merchantInfo.getWechatQrcodeUrl());
                record.setClosingName(merchantInfo.getRealName());
                break;
        }
        if (StrUtil.isNotEmpty(request.getMark())) {
            record.setMark(request.getMark());
        }
        record.setClosingNo(CrmebUtil.getOrderNo(OrderConstants.CLOSING_ORDER_PREFIX_MERCHANT));
        return merchantClosingService.save(record);
    }

    /**
     * 商户端结算记录分页列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantClosingPageResponse> getMerchantClosingRecordPageList(MerchantClosingSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        request.setMerId(systemAdmin.getMerId());
        PageInfo<MerchantClosingRecord> recordPageInfo = merchantClosingService.getMerchantClosingPageListByMerchant(request, pageParamRequest);
        List<MerchantClosingRecord> recordList = recordPageInfo.getList();
        if (CollUtil.isEmpty(recordList)) {
            return CommonPage.copyPageInfo(recordPageInfo, CollUtil.newArrayList());
        }
        List<Integer> auditIdList = recordList.stream().map(MerchantClosingRecord::getAuditId).distinct().collect(Collectors.toList());
        Map<Integer, String> nameMap = systemAdminService.getNameMapByIdList(auditIdList);
        List<MerchantClosingPageResponse> responseList = recordList.stream().map(record -> {
            MerchantClosingPageResponse response = new MerchantClosingPageResponse();
            BeanUtils.copyProperties(record, response);
            response.setAuditName(Optional.ofNullable(nameMap.get(record.getAuditId())).orElse(""));
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(recordPageInfo, responseList);
    }

    /**
     * 商户端结算记录详情
     *
     * @param closingNo 结算单号
     * @return MerchantClosingDetailResponse
     */
    @Override
    public MerchantClosingDetailResponse getMerchantClosingDetailByMerchant(String closingNo) {
        MerchantClosingRecord merchantClosing = merchantClosingService.getByClosingNo(closingNo);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(merchantClosing.getMerId())) {
            throw new CrmebException("不能获取非自己商户的数据");
        }
        MerchantClosingDetailResponse response = new MerchantClosingDetailResponse();
        BeanUtils.copyProperties(merchantClosing, response);
        response.setRealName(response.getClosingName());
        return response;
    }


    /**
     * 资金监控分页列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<FundsFlowResponse> getFundsFlow(FundsFlowRequest request, PageParamRequest pageParamRequest) {
        PageInfo<Bill> billPageInfo = billService.getFundsFlow(request, pageParamRequest);
        List<Bill> billList = billPageInfo.getList();
        if (CollUtil.isEmpty(billList)) {
            return CommonPage.copyPageInfo(billPageInfo, CollUtil.newArrayList());
        }
        List<Integer> uidList = billList.stream().filter(e -> e.getUid() > 0).map(Bill::getUid).collect(Collectors.toList());
        Map<Integer, User> userHashMap = CollUtil.newHashMap();
        if (CollUtil.isNotEmpty(uidList)) {
            userHashMap = userService.getUidMapList(uidList);
        }
        List<Integer> merIdList = billList.stream().filter(e -> e.getMerId() > 0).map(Bill::getMerId).collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = CollUtil.newHashMap();
        if (CollUtil.isNotEmpty(merIdList)) {
            merchantMap = merchantService.getMapByIdList(merIdList);
        }
        List<FundsFlowResponse> responseList = CollUtil.newArrayList();
        for (Bill bill : billList) {
            FundsFlowResponse response = new FundsFlowResponse();
            BeanUtils.copyProperties(bill, response);
            response.setNickName(bill.getUid() > 0 ? userHashMap.get(bill.getUid()).getNickname() : "");
            response.setMerName(bill.getMerId() > 0 ? merchantMap.get(bill.getMerId()).getName() : "");
            responseList.add(response);
        }
        return CommonPage.copyPageInfo(billPageInfo, responseList);
    }

    /**
     * 商户端资金监控分页列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<FundsFlowResponse> getMerchantFundsFlow(FundsFlowRequest request, PageParamRequest pageParamRequest) {
        PageInfo<MerchantBill> merchantBillPageInfo = merchantBillService.getFundsFlow(request, pageParamRequest);
        List<MerchantBill> merchantBillList = merchantBillPageInfo.getList();
        if (CollUtil.isEmpty(merchantBillList)) {
            return CommonPage.copyPageInfo(merchantBillPageInfo, CollUtil.newArrayList());
        }
        List<Integer> uidList = merchantBillList.stream().filter(e -> e.getUid() > 0).map(MerchantBill::getUid).collect(Collectors.toList());
        Map<Integer, User> userHashMap = CollUtil.newHashMap();
        if (CollUtil.isNotEmpty(uidList)) {
            userHashMap = userService.getUidMapList(uidList);
        }

        List<FundsFlowResponse> responseList = CollUtil.newArrayList();
        for (MerchantBill bill : merchantBillList) {
            FundsFlowResponse response = new FundsFlowResponse();
            BeanUtils.copyProperties(bill, response);
            response.setNickName(bill.getUid() > 0 ? userHashMap.get(bill.getUid()).getNickname() : "");
            responseList.add(response);
        }
        return CommonPage.copyPageInfo(merchantBillPageInfo, responseList);
    }

    /**
     * 平台端日帐单分页列表
     *
     * @param dateLimit        时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformStatementResponse> getPlatformDailyStatementList(String dateLimit, PageParamRequest pageParamRequest) {
        PageInfo<PlatformDailyStatement> pageInfo = platformDailyStatementService.getPageList(dateLimit, pageParamRequest);
        List<PlatformDailyStatement> list = pageInfo.getList();
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<PlatformStatementResponse> responseList = list.stream().map(statement -> {
            PlatformStatementResponse response = new PlatformStatementResponse();
            response.setId(statement.getId());
            response.setOrderRealIncome(statement.getOrderPayAmount());
            response.setOrderReceivable(statement.getOrderPayAmount().add(statement.getPlatCouponPrice().add(statement.getIntegralPrice())));
            response.setPlatCouponPrice(statement.getPlatCouponPrice());
            response.setIntegralPrice(statement.getIntegralPrice());

            response.setOrderRealRefund(statement.getOrderRefundPrice());
//            response.setOrderRefundable(statement.getRefundMerchantTransferAmount().add(statement.getRefundHandlingFee()).add(statement.getRefundBrokeragePrice()));
            response.setOrderRefundable(statement.getOrderRefundPrice().add(statement.getRefundPlatCouponPrice()).add(statement.getRefundReplaceIntegralPrice()));
            response.setRefundPlatCouponPrice(statement.getRefundPlatCouponPrice());
            response.setRefundIntegralPrice(statement.getRefundReplaceIntegralPrice());

            response.setRealIncome(response.getOrderRealIncome().subtract(response.getOrderRealRefund()));

            response.setPayTransfer(statement.getMerchantTransferAmount());
            response.setRefundTransfer(statement.getRefundMerchantTransferAmount());
            response.setMerchantTransferAmount(response.getPayTransfer().subtract(response.getRefundTransfer()));

            response.setBrokeragePrice(statement.getBrokeragePrice());
            response.setRefundBrokeragePrice(statement.getRefundBrokeragePrice());
            response.setRefundReplaceBrokerage(statement.getRefundReplaceBrokerage());
            response.setBrokerage(response.getBrokeragePrice().subtract(response.getRefundBrokeragePrice()).add(response.getRefundReplaceBrokerage()));

            response.setFreightFee(statement.getFreightFee());
            response.setRefundFreightFee(statement.getRefundFreightFee());
            response.setFreight(response.getFreightFee().subtract(response.getRefundFreightFee()));

            response.setActualExpenditure(response.getMerchantTransferAmount().add(response.getBrokerage()).add(response.getFreight()));

            response.setCurrentDayBalance(response.getRealIncome().subtract(response.getActualExpenditure()));
            response.setDataDate(statement.getDataDate());
            response.setPayNum(statement.getTotalOrderNum());
            response.setRefundNum(statement.getRefundNum());
            response.setMerchantTransferNum(statement.getMerchantTransferNum());

            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 平台端月帐单分页列表
     *
     * @param dateLimit        时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformStatementResponse> getPlatformMonthStatementList(String dateLimit, PageParamRequest pageParamRequest) {
        PageInfo<PlatformMonthStatement> pageInfo = platformMonthStatementService.getPageList(dateLimit, pageParamRequest);
        List<PlatformMonthStatement> list = pageInfo.getList();
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<PlatformStatementResponse> responseList = list.stream().map(statement -> {
            PlatformStatementResponse response = new PlatformStatementResponse();
            response.setId(statement.getId());
            response.setOrderRealIncome(statement.getOrderPayAmount());
            response.setOrderReceivable(statement.getOrderPayAmount().add(statement.getPlatCouponPrice().add(statement.getIntegralPrice())));
            response.setPlatCouponPrice(statement.getPlatCouponPrice());
            response.setIntegralPrice(statement.getIntegralPrice());

            response.setOrderRealRefund(statement.getOrderRefundPrice());
//            response.setOrderRefundable(statement.getRefundMerchantTransferAmount().add(statement.getRefundHandlingFee()).add(statement.getRefundBrokeragePrice()));
            response.setOrderRefundable(statement.getOrderRefundPrice().add(statement.getRefundPlatCouponPrice()).add(statement.getRefundReplaceIntegralPrice()));
            response.setRefundPlatCouponPrice(statement.getRefundPlatCouponPrice());
            response.setRefundIntegralPrice(statement.getRefundReplaceIntegralPrice());

            response.setRealIncome(response.getOrderRealIncome().subtract(response.getOrderRealRefund()));

            response.setPayTransfer(statement.getMerchantTransferAmount());
            response.setRefundTransfer(statement.getRefundMerchantTransferAmount());
            response.setMerchantTransferAmount(response.getPayTransfer().subtract(response.getRefundTransfer()));

            response.setBrokeragePrice(statement.getBrokeragePrice());
            response.setRefundBrokeragePrice(statement.getRefundBrokeragePrice());
            response.setRefundReplaceBrokerage(statement.getRefundReplaceBrokerage());
            response.setBrokerage(response.getBrokeragePrice().subtract(response.getRefundBrokeragePrice()).add(response.getRefundReplaceBrokerage()));

            response.setFreightFee(statement.getFreightFee());
            response.setRefundFreightFee(statement.getRefundFreightFee());
            response.setFreight(response.getFreightFee().subtract(response.getRefundFreightFee()));

            response.setActualExpenditure(response.getMerchantTransferAmount().add(response.getBrokerage()).add(response.getFreight()));

            response.setCurrentDayBalance(response.getRealIncome().subtract(response.getActualExpenditure()));
            response.setDataDate(statement.getDataDate());
            response.setPayNum(statement.getTotalOrderNum());
            response.setRefundNum(statement.getRefundNum());
            response.setMerchantTransferNum(statement.getMerchantTransferNum());

            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 商户端日帐单分页列表
     *
     * @param dateLimit        时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantStatementResponse> getMerchantDailyStatementList(String dateLimit, PageParamRequest pageParamRequest) {
        PageInfo<MerchantDailyStatement> pageInfo = merchantDailyStatementService.getPageList(dateLimit, pageParamRequest);
        List<MerchantDailyStatement> list = pageInfo.getList();
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<MerchantStatementResponse> responseList = list.stream().map(statement -> {
            MerchantStatementResponse response = new MerchantStatementResponse();
            response.setId(statement.getId());
            response.setOrderReceivable(statement.getOrderPayAmount().add(statement.getPlatCouponPrice().add(statement.getIntegralPrice())));
            response.setOrderRealIncome(statement.getOrderPayAmount());
            response.setPlatCouponPrice(statement.getPlatCouponPrice());
            response.setIntegralPrice(statement.getIntegralPrice());

//            response.setOrderRefundable(statement.getRefundMerchantTransferAmount().add(statement.getRefundHandlingFee()).add(statement.getRefundBrokeragePrice()));
            response.setOrderRefundable(statement.getRefundAmount().add(statement.getRefundPlatCouponPrice()).add(statement.getRefundIntegralPrice()));
            response.setOrderRealRefund(statement.getRefundAmount());
            response.setRefundPlatCouponPrice(statement.getRefundPlatCouponPrice());
            response.setRefundIntegralPrice(statement.getRefundIntegralPrice());

            response.setRealIncome(response.getOrderReceivable().subtract(response.getOrderRefundable()));

            response.setPayHandlingFee(statement.getHandlingFee());
            response.setRefundHandlingFee(statement.getRefundHandlingFee());
            response.setHandlingFee(response.getPayHandlingFee().subtract(response.getRefundHandlingFee()));

            response.setBrokeragePrice(statement.getBrokeragePrice());
            response.setRefundBrokeragePrice(statement.getRefundBrokeragePrice());
            response.setBrokerage(response.getBrokeragePrice().subtract(response.getRefundBrokeragePrice()));

            response.setActualExpenditure(response.getHandlingFee().add(response.getBrokerage()));

            response.setCurrentDayBalance(response.getRealIncome().subtract(response.getActualExpenditure()));
            response.setDataDate(statement.getDataDate());
            response.setPayNum(statement.getOrderNum());
            response.setRefundNum(statement.getRefundNum());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 商户端月帐单分页列表
     *
     * @param dateLimit        时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantStatementResponse> getMerchantMonthStatementList(String dateLimit, PageParamRequest pageParamRequest) {
        PageInfo<MerchantMonthStatement> pageInfo = merchantMonthStatementService.getPageList(dateLimit, pageParamRequest);
        List<MerchantMonthStatement> list = pageInfo.getList();
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<MerchantStatementResponse> responseList = list.stream().map(statement -> {
            MerchantStatementResponse response = new MerchantStatementResponse();
            response.setId(statement.getId());
            response.setOrderReceivable(statement.getOrderPayAmount().add(statement.getPlatCouponPrice().add(statement.getIntegralPrice())));
            response.setOrderRealIncome(statement.getOrderPayAmount());
            response.setPlatCouponPrice(statement.getPlatCouponPrice());
            response.setIntegralPrice(statement.getIntegralPrice());

//            response.setOrderRefundable(statement.getRefundMerchantTransferAmount().add(statement.getRefundHandlingFee()).add(statement.getRefundBrokeragePrice()));
            response.setOrderRefundable(statement.getRefundAmount().add(statement.getRefundPlatCouponPrice()).add(statement.getRefundIntegralPrice()));
//            response.setOrderRealRefund(response.getOrderReceivable().subtract(statement.getRefundPlatCouponPrice()).subtract(statement.getRefundIntegralPrice()));
            response.setOrderRealRefund(statement.getRefundAmount());
            response.setRefundPlatCouponPrice(statement.getRefundPlatCouponPrice());
            response.setRefundIntegralPrice(statement.getRefundIntegralPrice());

            response.setRealIncome(response.getOrderReceivable().subtract(response.getOrderRefundable()));

            response.setPayHandlingFee(statement.getHandlingFee());
            response.setRefundHandlingFee(statement.getRefundHandlingFee());
            response.setHandlingFee(response.getPayHandlingFee().subtract(response.getRefundHandlingFee()));

            response.setBrokeragePrice(statement.getBrokeragePrice());
            response.setRefundBrokeragePrice(statement.getRefundBrokeragePrice());
            response.setBrokerage(response.getBrokeragePrice().subtract(response.getRefundBrokeragePrice()));

            response.setActualExpenditure(response.getHandlingFee().add(response.getBrokerage()));

            response.setCurrentDayBalance(response.getRealIncome().subtract(response.getActualExpenditure()));
            response.setDataDate(statement.getDataDate());
            response.setPayNum(statement.getOrderNum());
            response.setRefundNum(statement.getRefundNum());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 财务流水汇总列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<SummaryFinancialStatements> summaryFinancialStatements(String dateLimit, PageParamRequest pageParamRequest) {
        return summaryFinancialStatementsService.getPageList(dateLimit, pageParamRequest);
    }
}
