package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.model.seckill.SeckillActivityTime;
import com.zbkj.service.dao.SeckillActivityTimeDao;
import com.zbkj.service.service.SeckillActivityTimeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
*  SeckillActivityTimeServiceImpl 接口实现
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
public class SeckillActivityTimeServiceImpl extends ServiceImpl<SeckillActivityTimeDao, SeckillActivityTime> implements SeckillActivityTimeService {

    @Resource
    private SeckillActivityTimeDao dao;

    /**
     * 获取活动时间端信息
     * @param activityId 活动ID
     */
    @Override
    public List<SeckillActivityTime> findByActivityId(Integer activityId) {
        LambdaQueryWrapper<SeckillActivityTime> lqw = Wrappers.lambdaQuery();
        lqw.eq(SeckillActivityTime::getSeckillId, activityId);
        return dao.selectList(lqw);
    }

    /**
     * 删除活动时段
     * @param activityId 活动ID
     */
    @Override
    public Boolean deleteByActivityId(Integer activityId) {
        LambdaUpdateWrapper<SeckillActivityTime> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(SeckillActivityTime::getSeckillId, activityId);
        return remove(wrapper);
    }

    /**
     * 通过日期时间获取秒杀场次
     * @param dateInt 日期：yyyyMMdd
     * @param timeInt 时间:HHmm
     */
    @Override
    public List<SeckillActivityTime> findByActivityStatus(Integer activityStatus, Integer dateInt, Integer timeInt) {
        LambdaQueryWrapper<SeckillActivityTime> lqw = Wrappers.lambdaQuery();
        if (activityStatus.equals(1)) {
            lqw.nested(i -> i.le(SeckillActivityTime::getStartDate, dateInt).ge(SeckillActivityTime::getEndDate, dateInt));
            lqw.nested(i -> i.le(SeckillActivityTime::getStartTime, timeInt).ge(SeckillActivityTime::getEndTime, timeInt));
        }
        if (activityStatus.equals(2)) {
            lqw.lt(SeckillActivityTime::getEndDate, dateInt);
            lqw.or(o -> o.eq(SeckillActivityTime::getEndDate, dateInt).lt(SeckillActivityTime::getEndTime, timeInt));
        }
        return dao.selectList(lqw);
    }

    /**
     * 获取秒杀活动状态
     * @param activityId 活动ID
     * @param dateInt 日期：yyyyMMdd
     * @param timeInt 时间:HHmm
     */
    @Override
    public Integer getActivityStatus(Integer activityId, Integer dateInt, Integer timeInt) {
        LambdaQueryWrapper<SeckillActivityTime> lqw = Wrappers.lambdaQuery();
        lqw.eq(SeckillActivityTime::getSeckillId, activityId);
        List<SeckillActivityTime> timeList = dao.selectList(lqw);
        Integer activityStatus = 0;
        List<SeckillActivityTime> timeList1 = timeList.stream().filter(e ->
                e.getStartDate() <= dateInt && dateInt <= e.getEndDate() && e.getStartTime() <= timeInt && e.getEndTime() >= timeInt
        ).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(timeList1)) {
            activityStatus = 1;
            return activityStatus;
        }
        List<SeckillActivityTime> timeList2 = timeList.stream().filter(e ->
                e.getEndDate() < dateInt || (e.getEndDate().equals(dateInt) && e.getEndTime() < timeInt)
        ).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(timeList2)) {
            activityStatus = 2;
            return activityStatus;
        }
        return activityStatus;
    }

    /**
     * 根据时间获取秒杀活动
     * @return 秒杀活动ID列表
     */
    @Override
    public List<Integer> findActivityByDateTime(Integer dateInt, Integer timeInt) {
        LambdaQueryWrapper<SeckillActivityTime> lqw = Wrappers.lambdaQuery();
        lqw.select(SeckillActivityTime::getId, SeckillActivityTime::getSeckillId);
        lqw.le(SeckillActivityTime::getStartDate, dateInt);
        lqw.ge(SeckillActivityTime::getEndDate, dateInt);
        lqw.le(SeckillActivityTime::getStartTime, timeInt);
        lqw.ge(SeckillActivityTime::getEndTime, timeInt);
        lqw.groupBy(SeckillActivityTime::getSeckillId);
        List<SeckillActivityTime> timeList = dao.selectList(lqw);
        return timeList.stream().map(SeckillActivityTime::getSeckillId).collect(Collectors.toList());
    }

    /**
     * 通过日期获取活动时间
     * @param dateInt 日期：yyyyMMdd
     * @return
     */
    public List<SeckillActivityTime> findFrontByDate(Integer dateInt) {
        return dao.findFrontByDate(dateInt);
    }

    /**
     * 通过日期、开始时间、结束时间获取活动ID
     * @param dateInt 日期：yyyyMMdd
     * @param startTime 开始时间:HHmm
     * @param endTime 结束时间:HHmm
     */
    @Override
    public List<Integer> findActivityByDateAndTime(Integer dateInt, Integer startTime, Integer endTime) {
        return dao.findActivityByDateAndTime(dateInt, startTime, endTime);
    }

    /**
     * 当前时间活动是否进行中
     * @param activityId 活动ID
     */
    @Override
    public Boolean isInProgressNow(Integer activityId) {
        DateTime dateTime = DateUtil.date();
        String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
        String hmStr = dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM);
        return isInProgress(activityId, Integer.valueOf(dateStr), Integer.valueOf(hmStr));
    }

    /**
     * 活动是否进行中
     * @param activityId 活动ID
     * @param dateInt 日期：yyyyMMdd
     * @param timeInt 时间:HHmm
     */
    private Boolean isInProgress(Integer activityId, Integer dateInt, Integer timeInt) {
        LambdaQueryWrapper<SeckillActivityTime> lqw = Wrappers.lambdaQuery();
        lqw.select(SeckillActivityTime::getId);
        lqw.eq(SeckillActivityTime::getSeckillId, activityId);
        lqw.le(SeckillActivityTime::getStartDate, dateInt);
        lqw.ge(SeckillActivityTime::getEndDate, dateInt);
        lqw.le(SeckillActivityTime::getStartTime, timeInt);
        lqw.ge(SeckillActivityTime::getEndTime, timeInt);
        lqw.last(" limit 1");
        SeckillActivityTime seckillActivityTime = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(seckillActivityTime);
    }

    /**
     * 是否有活动使用时段
     * @param tid 时段ID
     */
    @Override
    public Boolean isExistTimeInterval(Integer tid) {
        return dao.isExistTimeInterval(tid);
    }

    /**
     * 更新秒杀活动时段
     * @param tid 时段ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @Override
    public Boolean updateTimeByIntervalId(Integer tid, Integer startTime, Integer endTime) {
        LambdaUpdateWrapper<SeckillActivityTime> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(SeckillActivityTime::getStartTime, startTime);
        wrapper.set(SeckillActivityTime::getEndTime, endTime);
        wrapper.eq(SeckillActivityTime::getTimeIntervalId, tid);
        return update(wrapper);
    }
}

