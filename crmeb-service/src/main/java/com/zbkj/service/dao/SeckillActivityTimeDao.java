package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.seckill.SeckillActivityTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 秒杀活动时间表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-12-15
 */
public interface SeckillActivityTimeDao extends BaseMapper<SeckillActivityTime> {

    /**
     * 通过日期获取活动时间
     * @param dateInt 日期：yyyyMMdd
     * @return
     */
    List<SeckillActivityTime> findFrontByDate(@Param(value = "dateInt") Integer dateInt);

    /**
     * 通过日期、开始时间、结束时间获取活动ID
     * @param dateInt 日期：yyyyMMdd
     * @param startTime 开始时间:HHmm
     * @param endTime 结束时间:HHmm
     */
    List<Integer> findActivityByDateAndTime(@Param(value = "dateInt") Integer dateInt, @Param(value = "startTime") Integer startTime, @Param(value = "endTime") Integer endTime);

    /**
     * 是否有活动使用时段
     * @param tid 时段ID
     */
    Boolean isExistTimeInterval(Integer tid);
}
