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
import com.zbkj.common.constants.PayConstants;
import com.zbkj.common.model.bill.SummaryFinancialStatements;
import com.zbkj.common.model.closing.MerchantClosingRecord;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.RechargeOrder;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.model.user.UserClosing;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.SummaryFinancialStatementsDao;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * SummaryFinancialStatementsService 接口实现
 * </p>
 *
 * @author HZW
 * @since 2023-06-09
 */
@Service
public class SummaryFinancialStatementsServiceImpl extends ServiceImpl<SummaryFinancialStatementsDao, SummaryFinancialStatements> implements SummaryFinancialStatementsService {

    @Resource
    private SummaryFinancialStatementsDao dao;

    @Autowired
    private OrderService orderService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private RechargeOrderService rechargeOrderService;
    @Autowired
    private MerchantClosingRecordService merchantClosingRecordService;
    @Autowired
    private UserClosingService userClosingService;

    /**
     * 分页列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<SummaryFinancialStatements> getPageList(String dateLimit, PageParamRequest pageParamRequest) {
        Page<SummaryFinancialStatements> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SummaryFinancialStatements> lqw = Wrappers.lambdaQuery();
        if (StrUtil.isNotEmpty(dateLimit)){
            DateLimitUtilVo dateLimitVo = CrmebDateUtil.getDateLimit(dateLimit);
            String startDate = DateUtil.parse(dateLimitVo.getStartTime()).toDateStr();
            String endDate = DateUtil.parse(dateLimitVo.getEndTime()).toDateStr();
            lqw.between(SummaryFinancialStatements::getDataDate, startDate, endDate);
        }
        lqw.orderByDesc(SummaryFinancialStatements::getId);
        List<SummaryFinancialStatements> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        // 判断是否包含今天
        String today = DateUtil.date().toDateStr();
        for(SummaryFinancialStatements statements : list) {
            if (!statements.getDataDate().equals(today)) {
                continue;
            }
            writeDailyStatement(statements);
        }
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 通过日期获取记录
     * @param date 日期：年-月-日
     */
    @Override
    public SummaryFinancialStatements getByDate(String date) {
        LambdaQueryWrapper<SummaryFinancialStatements> lqw = Wrappers.lambdaQuery();
        lqw.eq(SummaryFinancialStatements::getDataDate, date);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    private void writeDailyStatement(SummaryFinancialStatements statements) {
        // 为今天的帐单计算数据
        List<Order> orderList = orderService.findPayByDate(0, statements.getDataDate());
        List<RefundOrder> refundOrderList = refundOrderService.findByDate(0, statements.getDataDate());
        List<RechargeOrder> rechargeOrderList = rechargeOrderService.findByDate(statements.getDataDate());
        List<MerchantClosingRecord> merchantClosingRecordList = merchantClosingRecordService.findByDate(0, statements.getDataDate());
        List<UserClosing> userClosingList = userClosingService.findByDate(statements.getDataDate());

        statements.setRechargeAmount(rechargeOrderList.stream().map(RechargeOrder::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        statements.setRechargeNum(rechargeOrderList.size());
        statements.setWechatPayAmount(orderList.stream().filter(e -> e.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)).map(Order::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        statements.setWechatPayNum((int) orderList.stream().filter(e -> e.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)).count());
        statements.setAliPayAmount(orderList.stream().filter(e -> e.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)).map(Order::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        statements.setAliPayNum((int) orderList.stream().filter(e -> e.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)).count());
        statements.setIncomeAmount(statements.getRechargeAmount().add(statements.getWechatPayAmount()).add(statements.getAliPayAmount()));
        statements.setMerchantSplitSettlement(merchantClosingRecordList.stream().map(MerchantClosingRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        statements.setMerchantSplitSettlementNum(merchantClosingRecordList.size());
        statements.setBrokerageSettlement(userClosingList.stream().map(UserClosing::getClosingPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        statements.setBrokerageSettlementNum(userClosingList.size());
        statements.setOrderRefundAmount(refundOrderList.stream().map(RefundOrder::getRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        statements.setOrderRefundNum(refundOrderList.size());
        statements.setPayoutAmount(statements.getMerchantSplitSettlement().add(statements.getBrokerageSettlement()).add(statements.getOrderRefundAmount()));
        statements.setIncomeExpenditure(statements.getIncomeAmount().subtract(statements.getPayoutAmount()));
    }
}

