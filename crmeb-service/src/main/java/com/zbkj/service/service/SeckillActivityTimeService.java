package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.seckill.SeckillActivityTime;

import java.util.List;

/**
*  SeckillActivityTimeService 接口
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
public interface SeckillActivityTimeService extends IService<SeckillActivityTime> {

    /**
     * 获取活动时间端信息
     * @param activityId 活动ID
     */
    List<SeckillActivityTime> findByActivityId(Integer activityId);

    /**
     * 删除活动时段
     * @param activityId 活动ID
     */
    Boolean deleteByActivityId(Integer activityId);

    /**
     * 通过日期时间获取秒杀场次
     * @param dateInt 日期：yyyyMMdd
     * @param timeInt 时间:HHmm
     */
    List<SeckillActivityTime> findByActivityStatus(Integer activityStatus, Integer dateInt, Integer timeInt);

    /**
     * 获取秒杀活动状态
     * @param activityId 活动ID
     * @param dateInt 日期：yyyyMMdd
     * @param timeInt 时间:HHmm
     */
    Integer getActivityStatus(Integer activityId, Integer dateInt, Integer timeInt);

    /**
     * 根据时间获取秒杀活动
     * @return 秒杀活动ID列表
     */
    List<Integer> findActivityByDateTime(Integer dateInt, Integer timeInt);

    /**
     * 通过日期获取活动时间
     * @param dateInt 日期：yyyyMMdd
     */
    List<SeckillActivityTime> findFrontByDate(Integer dateInt);

    /**
     * 通过日期、开始时间、结束时间获取活动ID
     * @param dateInt 日期：yyyyMMdd
     * @param startTime 开始时间:HHmm
     * @param endTime 结束时间:HHmm
     */
    List<Integer> findActivityByDateAndTime(Integer dateInt, Integer startTime, Integer endTime);

    /**
     * 当前时间活动是否进行中
     * @param activityId 活动ID
     */
    Boolean isInProgressNow(Integer activityId);


    /**
     * 是否有活动使用时段
     * @param tid 时段ID
     */
    Boolean isExistTimeInterval(Integer tid);

    /**
     * 更新秒杀活动时段
     * @param tid 时段ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    Boolean updateTimeByIntervalId(Integer tid, Integer startTime, Integer endTime);
}