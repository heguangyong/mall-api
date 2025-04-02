package com.zbkj.service.service.impl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.BalanceRecordConstants;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.PayConstants;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.member.PaidMemberOrder;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBalanceRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PaidMemberOrderSearchRequest;
import com.zbkj.common.response.PaidMemberOrderInfoResponse;
import com.zbkj.common.response.PaidMemberOrderResponse;
import com.zbkj.common.result.MemberResultCode;
import com.zbkj.common.result.UserResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.PaidMemberOrderDao;
import com.zbkj.service.service.PaidMemberCardService;
import com.zbkj.service.service.PaidMemberOrderService;
import com.zbkj.service.service.UserBalanceRecordService;
import com.zbkj.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hzw
 * @description PaidMemberOrderServiceImpl 接口实现
 * @date 2024-05-10
 */
@Service
public class PaidMemberOrderServiceImpl extends ServiceImpl<PaidMemberOrderDao, PaidMemberOrder> implements PaidMemberOrderService {

    private final Logger logger = LoggerFactory.getLogger(PaidMemberOrderServiceImpl.class);

    @Resource
    private PaidMemberOrderDao dao;
    @Autowired
    private UserService userService;
    @Autowired
    private PaidMemberCardService paidMemberCardService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;

    /**
     * 付费会员订单分页列表
     */
    @Override
    public PageInfo<PaidMemberOrderResponse> findPlatformPage(PaidMemberOrderSearchRequest request) {
        Map<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", URLUtil.decode(request.getOrderNo()));
        }
        if (StrUtil.isNotBlank(request.getCardName())) {
            map.put("cardName", URLUtil.decode(request.getCardName()));
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimitVo = CrmebDateUtil.getDateLimit(request.getDateLimit());
            map.put("startTime", dateLimitVo.getStartTime());
            map.put("endTime", dateLimitVo.getEndTime());
        }
        if (StrUtil.isNotBlank(request.getPayType())) {
            map.put("payType", request.getPayType());
        }
        if (ObjectUtil.isNotNull(request.getPayStatus())) {
            if (request.getPayStatus() == 1 || request.getPayStatus() == 0) {
                map.put("payStatus", request.getPayStatus());
            }
        }
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        Page<PaidMemberOrderResponse> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<PaidMemberOrderResponse> list = dao.findPlatformPage(map);
        list.forEach(e -> e.setUserPhone(CrmebUtil.maskMobile(e.getUserPhone())));
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 付费会员订单详情
     */
    @Override
    public PaidMemberOrderInfoResponse platformInfo(String orderNo) {
        PaidMemberOrder order = getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException(MemberResultCode.PAID_MEMBER_ORDER_NOT_EXIST);
        }
        if (!order.getPaid()) {
            throw new CrmebException(MemberResultCode.PAID_MEMBER_ORDER_NOT_EXIST);
        }
        User user = userService.getById(order.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }
        PaidMemberOrderInfoResponse response = new PaidMemberOrderInfoResponse();
        BeanUtils.copyProperties(order, response);
        response.setUserNickname(user.getNickname());
        response.setUserPhone(CrmebUtil.maskMobile(user.getPhone()));
        return response;
    }

    /**
     * 用户是否购买过试用类型会员卡
     */
    @Override
    public Boolean userIsPayTrial(Integer userId) {
        LambdaQueryWrapper<PaidMemberOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(PaidMemberOrder::getUid, userId);
        lqw.eq(PaidMemberOrder::getPaid, 1);
        lqw.eq(PaidMemberOrder::getType, 0);
        Integer count = dao.selectCount(lqw);
        return count > 0;
    }

    /**
     * 用户是否第一次购买该套餐
     */
    @Override
    public Boolean userIsFirstPay(Integer userId, Integer cardId) {
        LambdaQueryWrapper<PaidMemberOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(PaidMemberOrder::getUid, userId);
        lqw.eq(PaidMemberOrder::getCardId, cardId);
        lqw.eq(PaidMemberOrder::getPaid, 1);
        Integer count = dao.selectCount(lqw);
        return count > 0;
    }

    /**
     * 获取订单
     *
     * @param outTradeNo 商户系统内部的订单号
     */
    @Override
    public PaidMemberOrder getByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<PaidMemberOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(PaidMemberOrder::getOutTradeNo, outTradeNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 支付成功后置处理
     * 延长用户vip时限
     * 赠送用户余额
     *
     * @param svipOrder 支付订单
     */
    @Override
    public Boolean paySuccessAfter(PaidMemberOrder svipOrder) {
        User user = userService.getById(svipOrder.getUid());
        paidMemberCardService.getByIdException(svipOrder.getCardId());

        Integer deadlineDay = svipOrder.getDeadlineDay();
        BigDecimal giftBalance = svipOrder.getGiftBalance();
        boolean isPermanentPaidMember;
        if (user.getIsPermanentPaidMember()) {
            isPermanentPaidMember = true;
        } else {
            isPermanentPaidMember = svipOrder.getType().equals(2);
        }

        Boolean execute = transactionTemplate.execute(e -> {
            boolean update = false;
            DateTime offsetDay;
            if (user.getIsPaidMember()) {
                offsetDay = DateUtil.offsetDay(DateTime.of(user.getPaidMemberExpirationTime()), deadlineDay);
                svipOrder.setCardExpirationTime(offsetDay);
                update = userService.updateUserPaidMember(user.getId(), true, isPermanentPaidMember, offsetDay, user.getPaidMemberExpirationTime());
            } else {
                offsetDay = DateUtil.offsetDay(DateUtil.date(), deadlineDay);
                svipOrder.setCardExpirationTime(offsetDay);
                update = userService.updateUserPaidMember(user.getId(), true, isPermanentPaidMember, offsetDay, null);
            }
            if (!update) {
                logger.error("svip订单更新用户信息失败，orderNo = {}", svipOrder.getOrderNo());
                e.setRollbackOnly();
                return false;
            }
            Boolean updatePaid = updatePaid(svipOrder.getId(), svipOrder.getOrderNo(), offsetDay);
            if (!updatePaid) {
                logger.error("充值订单更新支付状态失败，orderNo = {}", svipOrder.getOrderNo());
                e.setRollbackOnly();
                return false;
            }
            if (giftBalance.compareTo(BigDecimal.ZERO) > 0) {
                // 余额变动
                userService.updateNowMoney(user.getId(), giftBalance, Constants.OPERATION_TYPE_ADD);
                // 创建记录
                userBalanceRecordService.save(createBalanceRecord(svipOrder, user.getNowMoney()));
            }

            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 获取用户购买svip订单记录
     *
     * @param userId 用户ID
     */
    @Override
    public List<PaidMemberOrder> findUserSvipOrderRecord(Integer userId) {
        LambdaQueryWrapper<PaidMemberOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(PaidMemberOrder::getUid, userId);
        lqw.eq(PaidMemberOrder::getPaid, 1);
        lqw.orderByDesc(PaidMemberOrder::getPayTime, PaidMemberOrder::getId);
        return dao.selectList(lqw);
    }

    private UserBalanceRecord createBalanceRecord(PaidMemberOrder svipOrder, BigDecimal nowMoney) {
        UserBalanceRecord record = new UserBalanceRecord();
        record.setUid(svipOrder.getUid());
        record.setLinkId(svipOrder.getOrderNo());
        record.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_SVIP);
        record.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
        record.setAmount(svipOrder.getGiftBalance());
        record.setBalance(nowMoney.add(svipOrder.getGiftBalance()));
        record.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_PAY_SVIP, svipOrder.getGiftBalance()));
        return record;
    }

    private Boolean updatePaid(Integer id, String orderNo, Date cardExpirationTime) {
        LambdaUpdateWrapper<PaidMemberOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(PaidMemberOrder::getPaid, 1);
        wrapper.set(PaidMemberOrder::getPayTime, CrmebDateUtil.nowDateTime());
        wrapper.set(PaidMemberOrder::getCardExpirationTime, cardExpirationTime);
        wrapper.eq(PaidMemberOrder::getId, id);
        wrapper.eq(PaidMemberOrder::getOrderNo, orderNo);
        wrapper.eq(PaidMemberOrder::getPaid, 0);
        return update(wrapper);
    }

    @Override
    public PaidMemberOrder getByOrderNo(String orderNo) {
        LambdaQueryWrapper<PaidMemberOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(PaidMemberOrder::getOrderNo, orderNo);
        return dao.selectOne(lqw);
    }

    /**
     * 获取待上传微信发货管理订单
     */
    @Override
    public List<PaidMemberOrder> findAwaitUploadWechatList() {
        DateTime date = DateUtil.date();
        DateTime offsetMinute = DateUtil.offsetMinute(date, -10);
        LambdaQueryWrapper<PaidMemberOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(PaidMemberOrder::getPaid, 1);
        lqw.eq(PaidMemberOrder::getIsWechatShipping, 0);
        lqw.eq(PaidMemberOrder::getPayType, PayConstants.PAY_TYPE_WE_CHAT);
        lqw.eq(PaidMemberOrder::getPayChannel, PayConstants.PAY_CHANNEL_WECHAT_MINI);
        lqw.le(PaidMemberOrder::getPayTime, offsetMinute);
        return dao.selectList(lqw);
    }
}

