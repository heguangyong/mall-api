package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.bill.SummaryFinancialStatements;
import com.zbkj.common.request.PageParamRequest;

/**
 * <p>
 * SummaryFinancialStatementsService 接口
 * </p>
 *
 * @author HZW
 * @since 2023-06-09
 */
public interface SummaryFinancialStatementsService extends IService<SummaryFinancialStatements> {

    /**
     * 分页列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<SummaryFinancialStatements> getPageList(String dateLimit, PageParamRequest pageParamRequest);

    /**
     * 通过日期获取记录
     * @param date 日期：年-月-日
     */
    SummaryFinancialStatements getByDate(String date);
}