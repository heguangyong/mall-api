package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.bill.MerchantMonthStatement;
import com.zbkj.common.request.PageParamRequest;

import java.util.List;

/**
*  MerchantMonthStatementService 接口
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
public interface MerchantMonthStatementService extends IService<MerchantMonthStatement> {

    /**
     * 获取某个月的所有帐单
     * @param month 月份：年-月
     * @return List
     */
    List<MerchantMonthStatement> findByMonth(String month);

    /**
     * 分页列表
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MerchantMonthStatement> getPageList(String dateLimit, PageParamRequest pageParamRequest);
}
