package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.record.MerchantDayRecord;

/**
*  MerchantDayRecordService 接口
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
public interface MerchantDayRecordService extends IService<MerchantDayRecord> {

    /**
     * 获取某一天商户的访客量
     * @param merId 商户ID
     * @param date 日期：yyyy-MM-dd
     * @return 访客量
     */
    Integer getVisitorsByDate(Integer merId, String date);
}