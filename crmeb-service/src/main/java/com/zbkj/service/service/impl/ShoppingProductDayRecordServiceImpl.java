package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.record.ShoppingProductDayRecord;
import com.zbkj.service.dao.ShoppingProductDayRecordDao;
import com.zbkj.service.service.ShoppingProductDayRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ShoppingProductDayRecordService 接口实现
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
public class ShoppingProductDayRecordServiceImpl extends ServiceImpl<ShoppingProductDayRecordDao, ShoppingProductDayRecord> implements ShoppingProductDayRecordService {

    @Resource
    private ShoppingProductDayRecordDao dao;

}

