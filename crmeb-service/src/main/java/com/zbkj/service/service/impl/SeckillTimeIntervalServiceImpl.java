package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.seckill.SeckillTimeInterval;
import com.zbkj.common.request.SeckillTimeIntervalSearchRequest;
import com.zbkj.service.dao.SeckillTimeIntervalDao;
import com.zbkj.service.service.SeckillTimeIntervalService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
*  SeckillTimeIntervalServiceImpl 接口实现
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
@Service
public class SeckillTimeIntervalServiceImpl extends ServiceImpl<SeckillTimeIntervalDao, SeckillTimeInterval> implements SeckillTimeIntervalService {

    @Resource
    private SeckillTimeIntervalDao dao;

    /**
     * 校验时间唯一
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param id 时间端ID
     * @return 是否唯一
     */
    @Override
    public Boolean checkTimeUnique(Integer startTime, Integer endTime, Integer id) {
        LambdaQueryWrapper<SeckillTimeInterval> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(id) && id > 0 ) {
            lqw.ne(SeckillTimeInterval::getId, id);
        }
        lqw.eq(SeckillTimeInterval::getIsDel, false);
        lqw.nested(i ->
                i.lt(SeckillTimeInterval::getStartTime, endTime)
                .ge(SeckillTimeInterval::getEndTime, endTime)
                .or()
                .gt(SeckillTimeInterval::getEndTime, startTime)
                .le(SeckillTimeInterval::getEndTime, endTime)
                );
        lqw.last(" limit 1");
        SeckillTimeInterval seckillTimeInterval = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(seckillTimeInterval);
        /**
         新增开始时间 >= 开始时间 && 新增结束时间 < 开始时间
         新增开始时间 <= 开始时间 && 新增结束时间 >= 结束时间
         新增开始时间 > 结束时间 && 新增结束时间 <= 结束时间

         b开 >= a开 and （b开 < a关 or b开 <= a开） and （b关 >= a关 or b关 > a开） and b关 <= a关
         SELECT id,name,start_time,end_time,status,is_del,create_time,update_time FROM
         eb_seckill_time_interval WHERE
         (start_time <= ? AND end_time < ?) OR (start_time <= ? AND end_time >= ?) OR (end_time > ? AND end_time <= ? AND is_del = ?)
         limit 1
         */
    }

    /**
     * 删除秒杀时段
     * @param id 秒杀时段ID
     */
    @Override
    public Boolean deleteById(Integer id) {
        LambdaUpdateWrapper<SeckillTimeInterval> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(SeckillTimeInterval::getIsDel, 1);
        wrapper.eq(SeckillTimeInterval::getId, id);
        return update(wrapper);
    }

    /**
     * 秒杀时段列表
     * @param request 搜索参数
     */
    @Override
    public List<SeckillTimeInterval> getAdminList(SeckillTimeIntervalSearchRequest request) {
        LambdaQueryWrapper<SeckillTimeInterval> lqw = Wrappers.lambdaQuery();
        lqw.eq(SeckillTimeInterval::getIsDel, 0);
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(SeckillTimeInterval::getStatus, request.getStatus());
        }
        lqw.orderByAsc(SeckillTimeInterval::getStartTime);
        return dao.selectList(lqw);
    }
}

