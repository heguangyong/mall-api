package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.user.UserExperienceRecord;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserRecordSearchRequest;

import java.util.List;

/**
 * 用户经验记录服务接口
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
public interface UserExperienceRecordService extends IService<UserExperienceRecord> {

    /**
     * 获取用户经验列表（移动端）
     * @param userId 用户id
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Deprecated
    List<UserExperienceRecord> getH5List(Integer userId, PageParamRequest pageParamRequest);

    /**
     * 获取用户社区发文次数
     * @param userId 用户ID
     */
    Integer getCountByNoteToday(Integer userId);

    /**
     * 是否存在社区笔记经验记录
     * @param userId 用户ID
     * @param noteId 笔记ID
     */
    Boolean isExistNote(Integer userId, Integer noteId);

    /**
     * 获取用户经验列表V1.2（移动端）
     * @param userId 用户id
     * @param linkType 关联类型
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserExperienceRecord> getH5List_V1_2(Integer userId, String linkType, PageParamRequest pageRequest);

    /**
     * 获取用户日期经验值
     * @param userId 用户ID
     * @param date 日期
     * @return
     */
    Integer getCountByDate(Integer userId, String date);

    /**
     * 获取日期用户发布的笔记数量
     * @param userId 用户ID
     * @param date 日期
     * @return 笔记数量
     */
    Integer getNoteNumRecordByDate(Integer userId, String date);

    /**
     * 用户经验记录(管理端)
     */
    PageInfo<UserExperienceRecord> getAdminUserBrokerageRecord(UserRecordSearchRequest request);
}
