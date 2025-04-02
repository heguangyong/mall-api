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
import com.zbkj.common.model.bill.PlatformDailyStatement;
import com.zbkj.common.model.order.OrderProfitSharing;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.PlatformDailyStatementDao;
import com.zbkj.service.service.OrderProfitSharingService;
import com.zbkj.service.service.PlatformDailyStatementService;
import com.zbkj.service.service.RechargeOrderService;
import com.zbkj.service.service.RefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
*  PlatformDailyStatementServiceImpl 接口实现
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
public class PlatformDailyStatementServiceImpl extends ServiceImpl<PlatformDailyStatementDao, PlatformDailyStatement> implements PlatformDailyStatementService {

    @Resource
    private PlatformDailyStatementDao dao;

    @Autowired
    private OrderProfitSharingService orderProfitSharingService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private RechargeOrderService rechargeOrderService;

    /**
     * 通过日期获取记录
     * @param date 日期：年-月-日
     */
    @Override
    public PlatformDailyStatement getByDate(String date) {
        LambdaQueryWrapper<PlatformDailyStatement> lqw = Wrappers.lambdaQuery();
        lqw.eq(PlatformDailyStatement::getDataDate, date);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 通过月份获取所有记录
     * @param month 月份：年-月
     * @return List
     */
    @Override
    public List<PlatformDailyStatement> findByMonth(String month) {
        LambdaQueryWrapper<PlatformDailyStatement> lqw = Wrappers.lambdaQuery();
        lqw.apply("date_format(data_date, '%Y-%m') = {0}", month);
        return dao.selectList(lqw);
    }

    /**
     * 分页列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformDailyStatement> getPageList(String dateLimit, PageParamRequest pageParamRequest) {
        Page<PlatformDailyStatement> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PlatformDailyStatement> lqw = Wrappers.lambdaQuery();
        if (StrUtil.isNotEmpty(dateLimit)){
            DateLimitUtilVo dateLimitVo = CrmebDateUtil.getDateLimit(dateLimit);
            String startDate = DateUtil.parse(dateLimitVo.getStartTime()).toDateStr();
            String endDate = DateUtil.parse(dateLimitVo.getEndTime()).toDateStr();
            lqw.between(PlatformDailyStatement::getDataDate, startDate, endDate);
        }
        lqw.orderByDesc(PlatformDailyStatement::getId);
        List<PlatformDailyStatement> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        // 判断是否包含今天
        String today = DateUtil.date().toDateStr();
        for(PlatformDailyStatement dailyStatement : list) {
            if (!dailyStatement.getDataDate().equals(today)) {
                continue;
            }
            writeDailyStatement(dailyStatement);
        }
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 为帐单写入数据
     * @param dailyStatement 帐单
     */
    private void writeDailyStatement(PlatformDailyStatement dailyStatement) {
        // 为今天的帐单计算数据
        List<OrderProfitSharing> sharingList = orderProfitSharingService.findByDate(0, dailyStatement.getDataDate());
        List<RefundOrder> refundOrderList = refundOrderService.findByDate(0, dailyStatement.getDataDate());
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

//        // 收入 = 订单支付总金额 + 退订单支付积分抵扣金额
//        BigDecimal income = orderPayAmount.add(refundReplaceIntegralPrice);
//        // 支出 = 商户分账金额 + 一级佣金 + 二级佣金 + 订单支付积分抵扣金额 + 平台退款金额 + 平台退款代扣佣金
//        BigDecimal payoutAmount = merchantTransferAmount.add(firstBrokerage).add(secondBrokerage).add(integralPrice).add(refundAmount).add(refundReplaceBrokerage);
//        // 支出笔数 = 分账笔数 + 退款笔数
//        int payoutNum = merchantTransferNum + refundOrderNum;
//
//        // 平台日收支 = 平台手续费 + 退订单支付积分抵扣金额 - 订单支付积分抵扣金额 - 平台退款金额 - 平台退款代扣佣金
//        BigDecimal incomeExpenditure = handlingFee.add(refundReplaceIntegralPrice).subtract(integralPrice).subtract(refundAmount).subtract(refundReplaceBrokerage);

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

        dailyStatement.setOrderPayAmount(orderPayAmount);
        dailyStatement.setTotalOrderNum(orderPayNum);
        dailyStatement.setHandlingFee(handlingFee);
        dailyStatement.setMerchantTransferAmount(merchantTransferAmount);
        dailyStatement.setMerchantTransferNum(merchantTransferNum);
        dailyStatement.setFirstBrokerage(firstBrokerage);
        dailyStatement.setSecondBrokerage(secondBrokerage);
        dailyStatement.setIntegralPrice(integralPrice);
//        dailyStatement.setPayoutAmount(payoutAmount);
//        dailyStatement.setPayoutNum(payoutNum);
        dailyStatement.setRefundReplaceBrokerage(refundReplaceBrokerage);
        dailyStatement.setRefundReplaceIntegralPrice(refundReplaceIntegralPrice);
        dailyStatement.setRefundAmount(refundAmount);
        dailyStatement.setRefundNum(refundOrderNum);
//        dailyStatement.setRechargeAmount(rechargeAmount);
//        dailyStatement.setRechargeNum(rechargeNum);
        dailyStatement.setIncomeExpenditure(incomeExpenditure.abs());

        dailyStatement.setPlatCouponPrice(platCouponPrice);
        dailyStatement.setBrokeragePrice(brokeragePrice);
        dailyStatement.setOrderRefundPrice(refundAmount);
        dailyStatement.setRefundPlatCouponPrice(refundPlatCouponPrice);
        dailyStatement.setRefundHandlingFee(refundHandlingFee);
        dailyStatement.setRefundBrokeragePrice(refundBrokeragePrice);
        dailyStatement.setRefundMerchantTransferAmount(refundMerchantTransferAmount);

        dailyStatement.setFreightFee(freightFee);
        dailyStatement.setRefundFreightFee(refundFreightFee);
    }

}

