package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.ClosingConstant;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.closing.MerchantClosingRecord;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.request.merchant.MerchantClosingSearchRequest;
import com.zbkj.common.response.MerchantClosingPlatformPageResponse;
import com.zbkj.common.result.MerchantResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.MerchantClosingRecordDao;
import com.zbkj.service.service.MerchantClosingRecordService;
import com.zbkj.service.service.MerchantService;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
*  MerchantClosingRecordRecordServiceImpl 接口实现
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
@Service
public class MerchantClosingRecordServiceImpl extends ServiceImpl<MerchantClosingRecordDao, MerchantClosingRecord> implements MerchantClosingRecordService {

    @Resource
    private MerchantClosingRecordDao dao;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 商户结算分页列表(平台)
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantClosingPlatformPageResponse> getMerchantClosingPageListByPlatform(MerchantClosingSearchRequest request, PageParamRequest pageParamRequest) {
        Map<String, Object> map = CollUtil.newHashMap();
        if (ObjectUtil.isNotNull(request.getMerId())) {
            map.put("merId", request.getMerId());
        }
        if (StrUtil.isNotBlank(request.getClosingNo())) {
            map.put("closingNo", URLUtil.decode(request.getClosingNo()));
        }
        if (StrUtil.isNotBlank(request.getClosingType())) {
            map.put("closingType", request.getClosingType());
        }
        if (ObjectUtil.isNotNull(request.getAuditStatus())) {
            map.put("auditStatus", request.getAuditStatus());
        }
        if (ObjectUtil.isNotNull(request.getAccountStatus())) {
            map.put("accountStatus", request.getAccountStatus());
        }
        if (StrUtil.isNotEmpty(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            map.put("startTime", dateLimit.getStartTime());
            map.put("endTime", dateLimit.getEndTime());
        }
        Page<MerchantClosingRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<MerchantClosingPlatformPageResponse> list = dao.getMerchantClosingPageListByPlatform(map);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 商户结算记录详情
     * @param closingNo 结算单号
     */
    @Override
    public MerchantClosingRecord getByClosingNo(String closingNo) {
        LambdaQueryWrapper<MerchantClosingRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantClosingRecord::getClosingNo, closingNo);
        lqw.last("limit 1");
        MerchantClosingRecord merchantClosingRecord = dao.selectOne(lqw);
        if (ObjectUtil.isNull(merchantClosingRecord)) {
            throw new CrmebException(MerchantResultCode.MERCHANT_CLOSING_RECORD_NOT_EXIST);
        }
        return merchantClosingRecord;
    }

    /**
     * 商户结算申请审核
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean userClosingAudit(ClosingAuditRequest request) {
        MerchantClosingRecord merchantClosing = getByClosingNo(request.getClosingNo());
        if (!merchantClosing.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_AUDIT)) {
            throw new CrmebException(MerchantResultCode.MERCHANT_CLOSING_RECORD_AUDIT_ED);
        }
        if (request.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS)) {
            Merchant merchant = merchantService.getByIdException(merchantClosing.getMerId());
            String guaranteedAmount = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_GUARANTEED_AMOUNT);
            if (new BigDecimal(guaranteedAmount).compareTo(merchant.getBalance().subtract(merchantClosing.getAmount())) > 0) {
                throw new CrmebException(MerchantResultCode.MERCHANT_BALANCE_NOT_ENOUGH);
            }
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        merchantClosing.setAuditStatus(request.getAuditStatus());
        merchantClosing.setAuditId(admin.getId());
        merchantClosing.setAuditTime(DateUtil.date());
        if (StrUtil.isNotEmpty(request.getRefusalReason())) {
            merchantClosing.setRefusalReason(request.getRefusalReason());
        }
        return transactionTemplate.execute(e -> {
            updateById(merchantClosing);
            if (request.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS)) {
                Boolean result = merchantService.operationBalance(merchantClosing.getMerId(), merchantClosing.getAmount(), Constants.OPERATION_TYPE_SUBTRACT);
                if (!result) {
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 商户结算到账凭证
     */
    @Override
    public Boolean proof(ClosingProofRequest request) {
        MerchantClosingRecord merchantClosing = getByClosingNo(request.getClosingNo());
        if (!merchantClosing.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS)) {
            throw new CrmebException(MerchantResultCode.MERCHANT_CLOSING_RECORD_AUDIT_STATUS_ABNORMAL);
        }
        merchantClosing.setClosingProof(systemAttachmentService.clearPrefix(request.getClosingProof()));
        merchantClosing.setAccountStatus(ClosingConstant.CLOSING_ACCOUNT_STATUS_SUCCESS);
        merchantClosing.setClosingTime(DateUtil.date());
        return updateById(merchantClosing);
    }

    /**
     * 商户结算备注
     */
    @Override
    public Boolean remark(ClosingRemarkRequest request) {
        MerchantClosingRecord merchantClosing = getByClosingNo(request.getClosingNo());
        merchantClosing.setPlatformMark(request.getRemark());
        return updateById(merchantClosing);
    }

    /**
     * 商户结算分页列表(商户)
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantClosingRecord> getMerchantClosingPageListByMerchant(MerchantClosingSearchRequest request, PageParamRequest pageParamRequest) {
        Page<MerchantClosingRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<MerchantClosingRecord> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(request.getMerId())) {
            lqw.eq(MerchantClosingRecord::getMerId, request.getMerId());
        }
        if (StrUtil.isNotBlank(request.getClosingNo())) {
            lqw.eq(MerchantClosingRecord::getClosingNo, URLUtil.decode(request.getClosingNo()));
        }
        if (StrUtil.isNotBlank(request.getClosingType())) {
            lqw.eq(MerchantClosingRecord::getClosingType, request.getClosingType());
        }
        if (ObjectUtil.isNotNull(request.getAuditStatus())) {
            lqw.eq(MerchantClosingRecord::getAuditStatus, request.getAuditStatus());
        }
        if (ObjectUtil.isNotNull(request.getAccountStatus())) {
            lqw.eq(MerchantClosingRecord::getAccountStatus, request.getAccountStatus());
        }
        if (StrUtil.isNotEmpty(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            lqw.between(MerchantClosingRecord::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lqw.orderByDesc(MerchantClosingRecord::getId);
        return CommonPage.copyPageInfo(page, dao.selectList(lqw));
    }

    /**
     * 获取某一天的所有数据
     * @param merId 商户id，0为所有商户
     * @param date 日期：年-月-日
     * @return List
     */
    @Override
    public List<MerchantClosingRecord> findByDate(Integer merId, String date) {
        LambdaQueryWrapper<MerchantClosingRecord> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(MerchantClosingRecord::getMerId, merId);
        }
        lqw.eq(MerchantClosingRecord::getAuditStatus, ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS);
        lqw.eq(MerchantClosingRecord::getAccountStatus, ClosingConstant.CLOSING_ACCOUNT_STATUS_SUCCESS);
        lqw.apply("date_format(update_time, '%Y-%m-%d') = {0}", date);
        return dao.selectList(lqw);
    }
}

