package com.zbkj.service.service;


import com.zbkj.common.vo.LogisticsResultVo;

/**
 * ExpressService 接口
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
public interface LogisticService {

    /**
     * 快递
     *
     * @param expressNo String 物流单号
     * @param type      String 快递公司字母简写：不知道可不填 95%能自动识别，填写查询速度会更快 https://market.aliyun.com/products/56928004/cmapi021863.html#sku=yuncode15863000015
     * @param com       快递公司编号
     * @param phone     手机号
     * @return LogisticsResultVo
     */
    LogisticsResultVo info(String expressNo, String type, String com, String phone);
}
