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
import com.zbkj.common.model.bill.PlatformMonthStatement;
import com.zbkj.common.model.order.OrderProfitSharing;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.PlatformMonthStatementDao;
import com.zbkj.service.service.OrderProfitSharingService;
import com.zbkj.service.service.PlatformMonthStatementService;
import com.zbkj.service.service.RefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
*  PlatformMonthStatementServiceImpl 接口实现
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
public class PlatformMonthStatementServiceImpl extends ServiceImpl<PlatformMonthStatementDao, PlatformMonthStatement> implements PlatformMonthStatementService {

    @Resource
    private PlatformMonthStatementDao dao;

    @Autowired
    private OrderProfitSharingService orderProfitSharingService;
    @Autowired
    private RefundOrderService refundOrderService;

    /**
     * 获取某一月的数据
     * @param month 月份：年-月
     * @return PlatformMonthStatement
     */
    @Override
    public PlatformMonthStatement getByMonth(String month) {
        LambdaQueryWrapper<PlatformMonthStatement> lqw = Wrappers.lambdaQuery();
        lqw.eq(PlatformMonthStatement::getDataDate, month);
        lqw.last("limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 分页列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformMonthStatement> getPageList(String dateLimit, PageParamRequest pageParamRequest) {
        Page<PlatformMonthStatement> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PlatformMonthStatement> lqw = Wrappers.lambdaQuery();
        if (StrUtil.isNotEmpty(dateLimit)){
            DateLimitUtilVo dateLimitVo = CrmebDateUtil.getMonthLimit(dateLimit);
            String startMonth = dateLimitVo.getStartTime();
            String endMonth = dateLimitVo.getEndTime();
            lqw.between(PlatformMonthStatement::getDataDate, startMonth, endMonth);
        }
        lqw.orderByDesc(PlatformMonthStatement::getId);
        List<PlatformMonthStatement> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        // 判断是否包含本月
        String nowMonth = DateUtil.date().toString(DateConstants.DATE_FORMAT_MONTH);
        for(PlatformMonthStatement monthStatement : list) {
            if (!monthStatement.getDataDate().equals(nowMonth)) {
                continue;
            }
            writeMonthStatement(monthStatement);
        }
        return CommonPage.copyPageInfo(page, list);

    }

    /**
     * 写入本月数据
     * @param monthStatement 本月帐单
     */
    private void writeMonthStatement(PlatformMonthStatement monthStatement) {
        List<OrderProfitSharing> sharingList = orderProfitSharingService.findByMonth(0, monthStatement.getDataDate());
        List<RefundOrder> refundOrderList = refundOrderService.findByMonth(0, monthStatement.getDataDate());
//        List<RechargeOrder> rechargeOrderList = rechargeOrderService.findByMonth(monthStatement.getDataDate());
        // 订单支付总金额
        BigDecimal orderPayAmount = new BigDecimal("0.00");
        // 总订单支付笔数
        int orderPayNum = 0;
        // 日手续费收入
        BigDecimal handlingFee = new BigDecimal("0.00");
        // 商户分账金额
        BigDecimal merchantTransferAmount = new BigDecimal("0.00");
        // 商户分账笔数
        int merchantTransferNum = 0;
        // 平台退款金额
        BigDecimal refundAmount = new BigDecimal("0.00");
        // 平台代扣佣金金额
        BigDecimal refundReplaceBrokerage = new BigDecimal("0.00");
        // 退款平台积分抵扣金额
        BigDecimal refundReplaceIntegralPrice = new BigDecimal("0.00");
        // 退款笔数
        int refundOrderNum = 0;
        // 一级佣金金额
        BigDecimal firstBrokerage = new BigDecimal("0.00");
        // 二级佣金额
        BigDecimal secondBrokerage = new BigDecimal("0.00");
//        // 充值金额
//        BigDecimal rechargeAmount = new BigDecimal("0.00");
//        // 充值笔数
//        int rechargeNum = 0;
        // 订单积分抵扣金额
        BigDecimal integralPrice = new BigDecimal("0.00");
        BigDecimal platCouponPrice = new BigDecimal("0.00");
        BigDecimal brokeragePrice = new BigDecimal("0.00");
        // 退款部分
        BigDecimal refundPlatCouponPrice = new BigDecimal("0.00");
        BigDecimal refundHandlingFee = new BigDecimal("0.00");
        BigDecimal refundBrokeragePrice = new BigDecimal("0.00");
        BigDecimal refundMerchantTransferAmount = new BigDecimal("0.00");

        BigDecimal freightFee = new BigDecimal("0.00");
        BigDecimal refundFreightFee = new BigDecimal("0.00");

        if (CollUtil.isNotEmpty(sharingList)) {
            orderPayAmount =  sharingList.stream().map(OrderProfitSharing::getOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            orderPayNum = sharingList.size();
            handlingFee = sharingList.stream().map(OrderProfitSharing::getProfitSharingPlatPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            merchantTransferAmount = sharingList.stream().map(OrderProfitSharing::getProfitSharingMerPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            firstBrokerage = sharingList.stream().map(OrderProfitSharing::getFirstBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            secondBrokerage = sharingList.stream().map(OrderProfitSharing::getSecondBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            merchantTransferNum = sharingList.size();
            integralPrice = sharingList.stream().map(OrderProfitSharing::getIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            platCouponPrice = sharingList.stream().map(OrderProfitSharing::getPlatCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            brokeragePrice = firstBrokerage.add(secondBrokerage);
            freightFee = sharingList.stream().map(OrderProfitSharing::getFreightFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        if (CollUtil.isNotEmpty(refundOrderList)) {
            refundAmount = refundOrderList.stream().map(RefundOrder::getRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundReplaceBrokerage = refundOrderList.stream().filter(RefundOrder::getIsReplace).map(e -> e.getRefundFirstBrokerageFee().add(e.getRefundSecondBrokerageFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundReplaceIntegralPrice = refundOrderList.stream().map(RefundOrder::getRefundIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundOrderNum = refundOrderList.size();
            refundPlatCouponPrice = refundOrderList.stream().map(RefundOrder::getRefundPlatCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundHandlingFee = refundOrderList.stream().map(RefundOrder::getPlatformRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundMerchantTransferAmount = refundOrderList.stream().map(RefundOrder::getMerchantRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundBrokeragePrice = refundOrderList.stream().map(e -> e.getRefundFirstBrokerageFee().add(e.getRefundSecondBrokerageFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundFreightFee = refundOrderList.stream().map(RefundOrder::getRefundFreightFee).reduce(BigDecimal.ZERO, BigDecimal::add);

        }
//        if (CollUtil.isNotEmpty(rechargeOrderList)) {
//            rechargeNum = rechargeOrderList.size();
//            rechargeAmount = rechargeOrderList.stream().map(RechargeOrder::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
//        }

//        // 支出总金额 = 商户分账金额 + 一级分佣金额 + 二级分佣金额 + 退款金额  + 平台代扣佣金金额
//        BigDecimal payoutAmount = merchantTransferAmount.add(firstBrokerage).add(secondBrokerage).add(refundAmount).add(refundReplaceBrokerage);
//        // 支出笔数 = 分账笔数 + 退款笔数
//        int payoutNum = merchantTransferNum + refundOrderNum;
//
//        // 平台日收支 = 日手续费收入 + 退款平台积分抵扣金额 - 退款金额
//        BigDecimal incomeExpenditure = handlingFee.add(refundReplaceIntegralPrice).subtract(refundAmount);

        // 订单实收 = 订单实际支付金额
        BigDecimal orderIncome = orderPayAmount;

        // 订单应收
        BigDecimal orderReceivable = orderIncome.add(platCouponPrice).add(integralPrice);

        // 订单应退 = 商户退款 + 平台退款 + 佣金退款
        BigDecimal orderRefundable = refundMerchantTransferAmount.add(refundHandlingFee).add(refundBrokeragePrice);

        // 订单实退 = -订单应退 + 退还平台优惠券补贴 + 退还平台积分补贴
        BigDecimal orderRefund = refundPlatCouponPrice.add(refundReplaceIntegralPrice).subtract(orderRefundable);

        // 商家分账 = 支付分账 - 退款分账
        BigDecimal merchantProfitSharing = merchantTransferAmount.subtract(refundMerchantTransferAmount);

        // 佣金 = 支付佣金 - 退还佣金 + 退款佣金代扣
        BigDecimal brokerage = brokeragePrice.subtract(refundBrokeragePrice).add(refundReplaceBrokerage);

        // 实际收入
        BigDecimal income = orderIncome.add(orderRefund);

        // 实际支出 = 商家分账 + 佣金
        BigDecimal payout = merchantProfitSharing.add(brokerage);

        // 当日结余 = 实际收入 - 实际支出
        BigDecimal incomeExpenditure = income.subtract(payout);

        monthStatement.setOrderPayAmount(orderPayAmount);
        monthStatement.setTotalOrderNum(orderPayNum);
        monthStatement.setHandlingFee(handlingFee);
        monthStatement.setMerchantTransferAmount(merchantTransferAmount);
        monthStatement.setMerchantTransferNum(merchantTransferNum);
        monthStatement.setFirstBrokerage(firstBrokerage);
        monthStatement.setSecondBrokerage(secondBrokerage);
        monthStatement.setIntegralPrice(integralPrice);
//        monthStatement.setPayoutAmount(payoutAmount);
//        monthStatement.setPayoutNum(payoutNum);
        monthStatement.setRefundAmount(refundAmount);
        monthStatement.setRefundReplaceBrokerage(refundReplaceBrokerage);
        monthStatement.setRefundReplaceIntegralPrice(refundReplaceIntegralPrice);
        monthStatement.setRefundNum(refundOrderNum);
//        monthStatement.setRechargeAmount(rechargeAmount);
//        monthStatement.setRechargeNum(rechargeNum);
        monthStatement.setIncomeExpenditure(incomeExpenditure.abs());

        monthStatement.setPlatCouponPrice(platCouponPrice);
        monthStatement.setBrokeragePrice(brokeragePrice);
        monthStatement.setOrderRefundPrice(orderRefund.abs());
        monthStatement.setRefundPlatCouponPrice(refundPlatCouponPrice);
        monthStatement.setRefundHandlingFee(refundHandlingFee);
        monthStatement.setRefundBrokeragePrice(refundBrokeragePrice);
        monthStatement.setRefundMerchantTransferAmount(refundMerchantTransferAmount);

        monthStatement.setFreightFee(freightFee);
        monthStatement.setRefundFreightFee(refundFreightFee);
    }
}

