package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.seckill.SeckillTimeInterval;
import com.zbkj.common.request.SeckillTimeIntervalSearchRequest;

import java.util.List;

/**
*  SeckillTimeIntervalService 接口
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
public interface SeckillTimeIntervalService extends IService<SeckillTimeInterval> {

    /**
     * 校验时间唯一
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param id 时间端ID
     * @return 是否唯一
     */
    Boolean checkTimeUnique(Integer startTime, Integer endTime, Integer id);

    /**
     * 删除秒杀时段
     * @param id 秒杀时段ID
     */
    Boolean deleteById(Integer id);

    /**
     * 秒杀时段列表
     * @param request 搜索参数
     * @return
     */
    List<SeckillTimeInterval> getAdminList(SeckillTimeIntervalSearchRequest request);
}