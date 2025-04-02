package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.bill.PlatformMonthStatement;
import com.zbkj.common.request.PageParamRequest;

/**
*  PlatformMonthStatementService 接口
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
public interface PlatformMonthStatementService extends IService<PlatformMonthStatement> {

    /**
     * 获取某一月的数据
     * @param month 月份：年-月
     * @return PlatformMonthStatement
     */
    PlatformMonthStatement getByMonth(String month);

    /**
     * 分页列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<PlatformMonthStatement> getPageList(String dateLimit, PageParamRequest pageParamRequest);
}