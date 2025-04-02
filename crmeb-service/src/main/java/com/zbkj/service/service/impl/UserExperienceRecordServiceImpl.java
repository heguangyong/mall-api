package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.ExperienceRecordConstants;
import com.zbkj.common.model.user.UserExperienceRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserRecordSearchRequest;
import com.zbkj.service.dao.UserExperienceRecordDao;
import com.zbkj.service.service.UserExperienceRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户经验记录服务接口实现类
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
public class UserExperienceRecordServiceImpl extends ServiceImpl<UserExperienceRecordDao, UserExperienceRecord> implements UserExperienceRecordService {

    @Resource
    private UserExperienceRecordDao dao;

    /**
     * 获取用户经验列表（移动端）
     * @param userId 用户id
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Deprecated
    @Override
    public List<UserExperienceRecord> getH5List(Integer userId, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserExperienceRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(UserExperienceRecord::getId, UserExperienceRecord::getTitle, UserExperienceRecord::getType, UserExperienceRecord::getExperience, UserExperienceRecord::getCreateTime);
        lqw.eq(UserExperienceRecord::getUid, userId);
        lqw.orderByDesc(UserExperienceRecord::getId);
        return dao.selectList(lqw);
    }

    /**
     * 获取用户社区发文次数
     * @param userId 用户ID
     */
    @Override
    public Integer getCountByNoteToday(Integer userId) {
        LambdaQueryWrapper<UserExperienceRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(UserExperienceRecord::getId);
        lqw.eq(UserExperienceRecord::getUid, userId);
        lqw.eq(UserExperienceRecord::getLinkType, ExperienceRecordConstants.EXPERIENCE_RECORD_LINK_TYPE_NOTE);
        lqw.apply(" date_format(create_time, '%Y-%m-%d') = {0}", DateUtil.date().toDateStr());
        return dao.selectCount(lqw);
    }

    /**
     * 是否存在社区笔记经验记录
     * @param userId 用户ID
     * @param noteId 笔记ID
     */
    @Override
    public Boolean isExistNote(Integer userId, Integer noteId) {
        LambdaQueryWrapper<UserExperienceRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(UserExperienceRecord::getId);
        lqw.eq(UserExperienceRecord::getLinkId, noteId.toString());
        lqw.eq(UserExperienceRecord::getUid, userId);
        lqw.eq(UserExperienceRecord::getLinkType, ExperienceRecordConstants.EXPERIENCE_RECORD_LINK_TYPE_NOTE);
        UserExperienceRecord record = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(record) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 获取用户经验列表V1.2（移动端）
     * @param userId 用户id
     * @param linkType 关联类型
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserExperienceRecord> getH5List_V1_2(Integer userId, String linkType, PageParamRequest pageRequest) {
        Page<UserExperienceRecord> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<UserExperienceRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(UserExperienceRecord::getId, UserExperienceRecord::getTitle, UserExperienceRecord::getType, UserExperienceRecord::getExperience, UserExperienceRecord::getCreateTime);
        lqw.eq(UserExperienceRecord::getUid, userId);
        if (StrUtil.isNotBlank(linkType)) {
            lqw.eq(UserExperienceRecord::getLinkType, linkType);
        }
        lqw.orderByDesc(UserExperienceRecord::getId);
        List<UserExperienceRecord> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 获取用户日期经验值
     * @param userId 用户ID
     * @param date 日期
     * @return
     */
    @Override
    public Integer getCountByDate(Integer userId, String date) {
        QueryWrapper<UserExperienceRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(sum(experience),0) as experience");
        queryWrapper.eq("uid", userId);
        queryWrapper.apply(" date_format(create_time, '%Y-%m-%d') = {0}", date);
        UserExperienceRecord record = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(record)) {
            return 0;
        }
        return record.getExperience();
    }

    /**
     * 获取日期用户发布的笔记数量
     * @param userId 用户ID
     * @param date 日期
     * @return 笔记数量
     */
    @Override
    public Integer getNoteNumRecordByDate(Integer userId, String date) {
        LambdaQueryWrapper<UserExperienceRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(UserExperienceRecord::getId);
        lqw.eq(UserExperienceRecord::getUid, userId);
        lqw.eq(UserExperienceRecord::getLinkType, ExperienceRecordConstants.EXPERIENCE_RECORD_LINK_TYPE_NOTE);
        lqw.last(StrUtil.format(" and date_format(create_time, '%Y-%m-%d') = '{}'", date));
        return dao.selectCount(lqw);
    }

    /**
     * 用户经验记录(管理端)
     */
    @Override
    public PageInfo<UserExperienceRecord> getAdminUserBrokerageRecord(UserRecordSearchRequest request) {
        Page<UserExperienceRecord> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<UserExperienceRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserExperienceRecord::getUid, request.getUserId());
        lqw.orderByDesc(UserExperienceRecord::getId);
        List<UserExperienceRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }

}

