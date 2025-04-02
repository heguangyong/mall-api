package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.bill.MerchantMonthStatement;
import com.zbkj.common.model.order.OrderProfitSharing;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.MerchantMonthStatementDao;
import com.zbkj.service.service.MerchantMonthStatementService;
import com.zbkj.service.service.OrderProfitSharingService;
import com.zbkj.service.service.OrderService;
import com.zbkj.service.service.RefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * MerchantMonthStatementServiceImpl 接口实现
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
public class MerchantMonthStatementServiceImpl extends ServiceImpl<MerchantMonthStatementDao, MerchantMonthStatement> implements MerchantMonthStatementService {

    @Resource
    private MerchantMonthStatementDao dao;

    @Autowired
    private OrderProfitSharingService orderProfitSharingService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RefundOrderService refundOrderService;

    /**
     * 获取某个月的所有帐单
     *
     * @param month 月份：年-月
     * @return List
     */
    @Override
    public List<MerchantMonthStatement> findByMonth(String month) {
        LambdaQueryWrapper<MerchantMonthStatement> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMonthStatement::getDataDate, month);
        return dao.selectList(lqw);
    }

    /**
     * 分页列表
     *
     * @param dateLimit        时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantMonthStatement> getPageList(String dateLimit, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<MerchantMonthStatement> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<MerchantMonthStatement> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMonthStatement::getMerId, systemAdmin.getMerId());
        if (StrUtil.isNotBlank(dateLimit)) {
            DateLimitUtilVo dateLimitVo = CrmebDateUtil.getMonthLimit(dateLimit);
            String startMonth = dateLimitVo.getStartTime();
            String endMonth = dateLimitVo.getEndTime();
            lqw.between(MerchantMonthStatement::getDataDate, startMonth, endMonth);
        }
        lqw.orderByDesc(MerchantMonthStatement::getId);
        List<MerchantMonthStatement> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        // 判断是否包含本月
        String nowMonth = DateUtil.date().toString(DateConstants.DATE_FORMAT_MONTH);
        for (MerchantMonthStatement monthStatement : list) {
            if (!monthStatement.getDataDate().equals(nowMonth)) {
                continue;
            }
            writeMonthStatement(monthStatement);
        }
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 写入本月数据
     *
     * @param monthStatement 本月帐单
     */
    private void writeMonthStatement(MerchantMonthStatement monthStatement) {
        List<OrderProfitSharing> sharingList = orderProfitSharingService.findByMonth(monthStatement.getMerId(), monthStatement.getDataDate());
        List<RefundOrder> refundOrderList = refundOrderService.findByMonth(monthStatement.getMerId(), monthStatement.getDataDate());

        // 订单支付总金额
        BigDecimal orderPayAmount = new BigDecimal("0.00");
        // 订单支付笔数
        int orderNum = 0;
        // 订单收入金额
        BigDecimal orderIncomeAmount = new BigDecimal("0.00");
        // 平台手续费
        BigDecimal handlingFee = new BigDecimal("0.00");
        // 一二级分佣金额
        BigDecimal firstBrokerage = new BigDecimal("0.00");
        BigDecimal secondBrokerage = new BigDecimal("0.00");
        // 商户退款金额
        BigDecimal refundAmount = new BigDecimal("0.00");
        // 退款笔数
        int refundNum = 0;

        BigDecimal platCouponPrice = new BigDecimal("0.00");
        BigDecimal integralPrice = new BigDecimal("0.00");
        BigDecimal brokeragePrice = new BigDecimal("0.00");

        // 退款
        BigDecimal orderRefundPrice = new BigDecimal("0.00");
        BigDecimal refundPlatCouponPrice = new BigDecimal("0.00");
        BigDecimal refundIntegralPrice = new BigDecimal("0.00");
        BigDecimal refundHandlingFee = new BigDecimal("0.00");
        BigDecimal refundBrokeragePrice = new BigDecimal("0.00");
        BigDecimal refundMerchantTransferAmount = new BigDecimal("0.00");

        BigDecimal freightFee = new BigDecimal("0.00");
        BigDecimal refundFreightFee = new BigDecimal("0.00");

        if (CollUtil.isNotEmpty(sharingList)) {
            orderPayAmount = sharingList.stream().map(OrderProfitSharing::getOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            orderNum = sharingList.size();
            orderIncomeAmount = sharingList.stream().map(OrderProfitSharing::getProfitSharingMerPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            handlingFee = sharingList.stream().map(OrderProfitSharing::getProfitSharingPlatPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            firstBrokerage = sharingList.stream().map(OrderProfitSharing::getFirstBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            secondBrokerage = sharingList.stream().map(OrderProfitSharing::getSecondBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            platCouponPrice = sharingList.stream().map(OrderProfitSharing::getPlatCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            integralPrice = sharingList.stream().map(OrderProfitSharing::getIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            brokeragePrice = firstBrokerage.add(secondBrokerage);
            freightFee = sharingList.stream().map(OrderProfitSharing::getFreightFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        if (CollUtil.isNotEmpty(refundOrderList)) {
            refundAmount = refundOrderList.stream().map(RefundOrder::getRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundNum = refundOrderList.size();
            refundMerchantTransferAmount = refundOrderList.stream().map(RefundOrder::getMerchantRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundHandlingFee = refundOrderList.stream().map(RefundOrder::getPlatformRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundPlatCouponPrice = refundOrderList.stream().map(RefundOrder::getRefundPlatCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundBrokeragePrice = refundOrderList.stream().map(e -> e.getRefundFirstBrokerageFee().add(e.getRefundSecondBrokerageFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundIntegralPrice = refundOrderList.stream().map(RefundOrder::getRefundIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            orderRefundPrice = refundOrderList.stream().map(RefundOrder::getRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundFreightFee = refundOrderList.stream().map(RefundOrder::getRefundFreightFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

//        // 支出总金额 = 商户退款金额
//        BigDecimal payoutAmount = refundAmount;
//        // 支出笔数 = 退款笔数
//        int payoutNum = refundNum;
//        // 商户日收支 = 订单收入金额 - 商户退款金额
//        BigDecimal incomeExpenditure = orderIncomeAmount.subtract(refundAmount);

        // 订单应收 = 订单实付 + 平优 + 平积
        BigDecimal orderReceivable = orderPayAmount.add(platCouponPrice).add(integralPrice);

        // 订单应退 = 商户退款 + 平台退款 + 佣金退款
        BigDecimal orderRefundable = BigDecimal.ZERO.subtract(refundMerchantTransferAmount).subtract(refundHandlingFee).subtract(refundBrokeragePrice);

        // 订单实退 = 订单应退 - 退平台优惠券 - 退平台积分
        BigDecimal orderRefund = orderRefundable.subtract(refundPlatCouponPrice).subtract(refundIntegralPrice);

        // 平台手续费
        BigDecimal platformHandling = handlingFee.subtract(refundHandlingFee);

        // 佣金
        BigDecimal brokerage = brokeragePrice.subtract(refundBrokeragePrice);

        // 实际收入 = 订单应收 - 订单应退
        BigDecimal income = orderReceivable.add(orderRefundable);

        // 实际支出 = 平台手续费 + 佣金
        BigDecimal payout = platformHandling.add(brokerage);

        // 当日结余
        BigDecimal incomeExpenditure = income.subtract(payout);

        monthStatement.setOrderPayAmount(orderPayAmount);
        monthStatement.setOrderNum(orderNum);
        monthStatement.setOrderIncomeAmount(orderIncomeAmount);
        monthStatement.setHandlingFee(handlingFee);
        monthStatement.setFirstBrokerage(firstBrokerage);
        monthStatement.setSecondBrokerage(secondBrokerage);
//        monthStatement.setPayoutAmount(payoutAmount);
//        monthStatement.setPayoutNum(payoutNum);
        monthStatement.setRefundAmount(refundAmount);
        monthStatement.setRefundNum(refundNum);
        monthStatement.setIncomeExpenditure(incomeExpenditure.abs());

        monthStatement.setPlatCouponPrice(platCouponPrice);
        monthStatement.setIntegralPrice(integralPrice);
        monthStatement.setBrokeragePrice(brokeragePrice);
        monthStatement.setOrderRefundPrice(orderRefundPrice);
        monthStatement.setRefundPlatCouponPrice(refundPlatCouponPrice);
        monthStatement.setRefundIntegralPrice(refundIntegralPrice);
        monthStatement.setRefundHandlingFee(refundHandlingFee);
        monthStatement.setRefundBrokeragePrice(refundBrokeragePrice);
        monthStatement.setRefundMerchantTransferAmount(refundMerchantTransferAmount);

        monthStatement.setFreightFee(freightFee);
        monthStatement.setRefundFreightFee(refundFreightFee);
    }
}

