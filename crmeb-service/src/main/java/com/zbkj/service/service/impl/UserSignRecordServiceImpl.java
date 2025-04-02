package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.sgin.UserSignRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SignRecordSearchRequest;
import com.zbkj.common.request.UserRecordSearchRequest;
import com.zbkj.common.response.UserSignRecordResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.UserSignRecordDao;
import com.zbkj.service.service.UserSignRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
*  UserSignRecordServiceImpl 接口实现
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
@Service
public class UserSignRecordServiceImpl extends ServiceImpl<UserSignRecordDao, UserSignRecord> implements UserSignRecordService {

    @Resource
    private UserSignRecordDao dao;
    @Autowired
    private CrmebConfig crmebConfig;

    /**
     * 获取用户签到记录
     *
     * @param request 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserSignRecordResponse> pageRecordList(SignRecordSearchRequest request) {
        Page<UserSignRecord> page = PageHelper.startPage(request.getPage(), request.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                map.put("startTime", dateLimit.getStartTime());
                map.put("endTime", dateLimit.getEndTime());
            }
        }
        List<UserSignRecordResponse> responseList = dao.pageRecordList(map);
        if (CollUtil.isNotEmpty(responseList) && crmebConfig.getPhoneMaskSwitch()) {
            responseList.forEach(e -> e.setPhone(CrmebUtil.maskMobile(e.getPhone())));
        }
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 获取用户最后一条签到记录
     * @param uid 用户id
     * @return UserSignRecord
     */
    @Override
    public UserSignRecord getLastByUid(Integer uid) {
        LambdaQueryWrapper<UserSignRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserSignRecord::getUid, uid);
        lqw.orderByDesc(UserSignRecord::getCreateTime);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取某个月的签到记录
     * @param uid 用户id
     * @param month 月份 yyyy-MM
     * @return 签到记录
     */
    @Override
    public List<UserSignRecord> findByMonth(Integer uid, String month) {
        QueryWrapper<UserSignRecord> query = Wrappers.query();
        query.eq("uid", uid);
        query.apply("date_format(create_time, '%Y-%m') = {0}", month);
        return dao.selectList(query);
    }

    /**
     * 获取用户签到记录列表
     * @param uid 用户ID
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    @Override
    public PageInfo<UserSignRecord> findPageByUid(Integer uid, PageParamRequest pageParamRequest) {
        Page<UserSignRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserSignRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserSignRecord::getUid, uid);
        lqw.orderByDesc(UserSignRecord::getId);
        List<UserSignRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }

    /**
     * 获取用户签到记录(管理端)
     */
    @Override
    public PageInfo<UserSignRecord> getAdminUserSignRecord(UserRecordSearchRequest request) {
        Page<UserSignRecord> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<UserSignRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserSignRecord::getUid, request.getUserId());
        lqw.orderByDesc(UserSignRecord::getId);
        List<UserSignRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }
}

