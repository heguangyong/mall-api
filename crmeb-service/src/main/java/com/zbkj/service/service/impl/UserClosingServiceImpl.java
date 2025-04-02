package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.BrokerageRecordConstants;
import com.zbkj.common.constants.ClosingConstant;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.common.model.user.UserClosing;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.result.UserResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.UserClosingDao;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.UserBrokerageRecordService;
import com.zbkj.service.service.UserClosingService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserExtractServiceImpl 接口实现
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
public class UserClosingServiceImpl extends ServiceImpl<UserClosingDao, UserClosing> implements UserClosingService {

    @Resource
    private UserClosingDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    /**
     * 根据uid获取分页结算记录
     *
     * @param uid              用户id
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserClosing> getPageByUid(Integer uid, PageParamRequest pageParamRequest) {
        Page<UserClosing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserClosing> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserClosing::getUid, uid);
        lqw.orderByDesc(UserClosing::getId);
        List<UserClosing> closingList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, closingList);
    }

    /**
     * 用户结算分页列表
     *
     * @param request          搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserClosing> getPlatformPage(UserClosingSearchRequest request, PageParamRequest pageParamRequest) {
        Page<UserClosing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //带 UserExtract 类的多条件查询
        LambdaQueryWrapper<UserClosing> lqw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.and(i -> i.
                    or().like(UserClosing::getWechatNo, keywords).   //微信号
                    or().like(UserClosing::getCardholder, keywords). //名称
                    or().like(UserClosing::getBankCardNo, keywords). //银行卡号
                    or().like(UserClosing::getAlipayAccount, keywords) //支付宝
            );
        }
        //提现状态
        if (ObjectUtil.isNotNull(request.getAuditStatus())) {
            lqw.eq(UserClosing::getAuditStatus, request.getAuditStatus());
        }
        //提现方式
        if (StrUtil.isNotBlank(request.getClosingType())) {
            lqw.eq(UserClosing::getClosingType, request.getClosingType());
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            lqw.between(UserClosing::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        if (ObjectUtil.isNotNull(request.getAccountStatus())) {
            lqw.eq(UserClosing::getAccountStatus, request.getAccountStatus());
        }
        //按创建时间降序排列
        lqw.orderByDesc(UserClosing::getId);
        List<UserClosing> closingList = dao.selectList(lqw);
        if (CollUtil.isNotEmpty(closingList)) {
            List<Integer> uidList = closingList.stream().map(UserClosing::getUid).distinct().collect(Collectors.toList());
            Map<Integer, User> userMap = userService.getUidMapList(uidList);
            for (UserClosing userClosing : closingList) {
                userClosing.setNickName(Optional.ofNullable(userMap.get(userClosing.getUid()).getNickname()).orElse(""));
                userClosing.setIsLogoff(Optional.ofNullable(userMap.get(userClosing.getUid()).getIsLogoff()).orElse(false));
            }
        }
        return CommonPage.copyPageInfo(page, closingList);
    }

    /**
     * 用户结算申请审核
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean userClosingAudit(ClosingAuditRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        UserClosing userClosing = getByClosingNo(request.getClosingNo());
        if (!userClosing.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_AUDIT)) {
            throw new CrmebException(UserResultCode.USER_CLOSING_PROCESSING);
        }
        User user = userService.getById(userClosing.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }
        userClosing.setAuditId(admin.getId());
        userClosing.setAuditTime(DateUtil.date());

        UserBrokerageRecord brokerageRecord = userBrokerageRecordService.getOneByLinkNo(userClosing.getClosingNo());
        // 拒绝
        if (request.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_FAIL)) {//未通过时恢复用户总金额
            userClosing.setRefusalReason(request.getRefusalReason());
            userClosing.setAuditStatus(ClosingConstant.CLOSING_AUDIT_STATUS_FAIL);
            // 添加提现申请拒绝佣金记录
            brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
            return transactionTemplate.execute(e -> {
                updateById(userClosing);
                userBrokerageRecordService.updateById(brokerageRecord);
                return Boolean.TRUE;
            });
        }
        // 同意
        userClosing.setAuditStatus(ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS);
        if (user.getBrokeragePrice().compareTo(userClosing.getClosingPrice()) < 0) {
            throw new CrmebException(UserResultCode.USER_BROKERAGE_INSUFFICIENT);
        }
        brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        return transactionTemplate.execute(e -> {
            updateById(userClosing);
            // 修改用户佣金
            Boolean result = userService.updateBrokerage(user.getId(), userClosing.getClosingPrice(), Constants.OPERATION_TYPE_SUBTRACT);
            if (!result) {
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            userBrokerageRecordService.updateById(brokerageRecord);
            return Boolean.TRUE;
        });
    }

    /**
     * 根据结算单号查询
     *
     * @param closingNo 结算单号
     * @return UserClosing
     */
    private UserClosing getByClosingNo(String closingNo) {
        LambdaQueryWrapper<UserClosing> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserClosing::getClosingNo, closingNo);
        lqw.last(" limit 1");
        UserClosing userClosing = dao.selectOne(lqw);
        if (ObjectUtil.isNull(userClosing)) {
            throw new CrmebException(UserResultCode.USER_CLOSING_NULL);
        }
        return userClosing;
    }

    /**
     * 用户结算到账凭证
     */
    @Override
    public Boolean proof(ClosingProofRequest request) {
        UserClosing userClosing = getByClosingNo(request.getClosingNo());
        if (!userClosing.getAuditStatus().equals(ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS)) {
            throw new CrmebException(UserResultCode.USER_CLOSING_AUDIT_STATUS_EXCEPTION);
        }
        userClosing.setClosingProof(systemAttachmentService.clearPrefix(request.getClosingProof()));
        userClosing.setAccountStatus(ClosingConstant.CLOSING_ACCOUNT_STATUS_SUCCESS);
        userClosing.setClosingTime(DateUtil.date());
        return updateById(userClosing);
    }

    /**
     * 用户结算备注
     */
    @Override
    public Boolean remark(ClosingRemarkRequest request) {
        UserClosing userClosing = getByClosingNo(request.getClosingNo());
        userClosing.setMark(request.getRemark());
        return updateById(userClosing);
    }

    /**
     * 获取用户对应的提现数据
     *
     * @param userId 用户id
     * @return 提现数据
     */
    @Override
    public UserClosing getUserExtractByUserId(Integer userId) {
        QueryWrapper<UserClosing> qw = new QueryWrapper<>();
        qw.select("SUM(closing_price) as closing_price,count(id) as id, uid");
        qw.eq("audit_status", ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS);
        qw.eq("account_status", ClosingConstant.CLOSING_ACCOUNT_STATUS_SUCCESS);
        qw.eq("uid", userId);
        qw.groupBy("uid");
        UserClosing userClosing = dao.selectOne(qw);
        if (ObjectUtil.isNull(userClosing)) {
            userClosing = new UserClosing();
            userClosing.setId(0);
            userClosing.setClosingPrice(BigDecimal.ZERO);
        }
        return userClosing;
    }

    /**
     * 获取某一天的所有数据
     * @param date 日期：年-月-日
     * @return List
     */
    public List<UserClosing> findByDate(String date) {
        LambdaQueryWrapper<UserClosing> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserClosing::getAuditStatus, ClosingConstant.CLOSING_AUDIT_STATUS_SUCCESS);
        lqw.eq(UserClosing::getAccountStatus, ClosingConstant.CLOSING_ACCOUNT_STATUS_SUCCESS);
        lqw.apply("date_format(update_time, '%Y-%m-%d') = {0}", date);
        return dao.selectList(lqw);
    }

}

