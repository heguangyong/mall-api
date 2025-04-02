package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.GroupConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.IntegralIntervalAddRequest;
import com.zbkj.common.request.IntegralIntervalPageSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.GroupConfigDao;
import com.zbkj.service.service.GroupConfigService;
import com.zbkj.service.service.SystemAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * SystemConfigServiceImpl 接口实现
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
public class GroupConfigServiceImpl extends ServiceImpl<GroupConfigDao, GroupConfig> implements GroupConfigService {

    @Resource
    private GroupConfigDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 通过tag获取数据列表
     *
     * @param tag 标签
     * @param sortRule 排序规则
     * @param status 展示状态：1-展示
     */
    @Override
    public List<GroupConfig> findByTag(Integer tag, String sortRule, Boolean status) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getIsDel, 0);
        if (ObjectUtil.isNotNull(status)) {
            lqw.eq(GroupConfig::getStatus, status ? 1 : 0);
        }
        if (StrUtil.isBlank(sortRule) || sortRule.equals(Constants.SORT_ASC)) {
            lqw.orderByAsc(GroupConfig::getSort);
        } else {
            lqw.orderByDesc(GroupConfig::getSort);
        }
        lqw.orderByDesc(GroupConfig::getId);
        return dao.selectList(lqw);
    }

    /**
     * 通过tag获取数据列表
     *
     * @param tag 标签
     * @param merId 商户ID
     * @param sortRule 排序规则
     * @param status 展示状态：1-展示
     */
    @Override
    public List<GroupConfig> findByTag(Integer tag, Integer merId, String sortRule, Boolean status) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getMerId, merId);
        lqw.eq(GroupConfig::getIsDel, 0);
        if (ObjectUtil.isNotNull(status)) {
            lqw.eq(GroupConfig::getStatus, status ? 1 : 0);
        }
        if (StrUtil.isBlank(sortRule) || sortRule.equals(Constants.SORT_ASC)) {
            lqw.orderByAsc(GroupConfig::getSort);
        } else {
            lqw.orderByDesc(GroupConfig::getSort);
        }
        lqw.orderByDesc(GroupConfig::getId);
        return dao.selectList(lqw);
    }

    /**
     * 添加数据
     * @param configList 组合配置
     */
    @Override
    public Boolean saveList(List<GroupConfig> configList) {
        Integer tag = configList.get(0).getTag();
        configList.forEach(groupConfig -> {
            if (StrUtil.isNotBlank(groupConfig.getImageUrl())) {
                groupConfig.setImageUrl(systemAttachmentService.clearPrefix(groupConfig.getImageUrl()));
            }
        });
        Integer merId = Optional.ofNullable(configList.get(0).getMerId()).orElse(0);
        return transactionTemplate.execute(e -> {
            deleteByTag(tag, merId);
            saveBatch(configList);
            return Boolean.TRUE;
        });
    }

    /**
     * 按tag删除
     */
    @Override
    public Boolean deleteByTag(Integer tag, Integer merId) {
        LambdaUpdateWrapper<GroupConfig> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(GroupConfig::getIsDel, 1);
        wrapper.eq(GroupConfig::getTag, tag);
        wrapper.eq(GroupConfig::getIsDel, 0);
        if (ObjectUtil.isNotNull(merId) && merId > 0) {
            wrapper.eq(GroupConfig::getMerId, merId);
        }
        return update(wrapper);
    }

    /**
     * 获取积分区间分页列表
     */
    @Override
    public PageInfo<GroupConfig> getIntegralIntervalPage(IntegralIntervalPageSearchRequest request) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, GroupConfigConstants.TAG_INTEGRAL_INTERVAL);
        lqw.eq(GroupConfig::getIsDel, 0);
        if (StrUtil.isNotBlank(request.getName())) {
            String name = URLUtil.decode(request.getName());
            lqw.like(GroupConfig::getName, name);
        }
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(GroupConfig::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            if (StrUtil.isNotBlank(dateLimit.getStartTime()) && StrUtil.isNotBlank(dateLimit.getEndTime())) {
                //判断时间
                int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
                if (compareDateResult == -1) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
                }
                lqw.between(GroupConfig::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
            }
        }
        lqw.orderByDesc(GroupConfig::getSort, GroupConfig::getId);

        Page<GroupConfig> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<GroupConfig> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 新增积分区间
     */
    @Override
    public Boolean saveIntegralInterval(IntegralIntervalAddRequest request) {
        if (isExistName(request.getName(), GroupConfigConstants.TAG_INTEGRAL_INTERVAL)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "区间名称已存在");
        }
        String value = request.getValue();
        String[] split = value.split("-");
        int startValue = Integer.parseInt(split[0]);
        int endValue = Integer.parseInt(split[1]);
        if (endValue < startValue) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "积分区间值存在错误");
        }
        GroupConfig groupConfig = new GroupConfig();
        groupConfig.setName(request.getName());
        groupConfig.setValue(request.getValue());
        groupConfig.setStatus(request.getStatus());
        groupConfig.setSort(request.getSort());
        groupConfig.setTag(GroupConfigConstants.TAG_INTEGRAL_INTERVAL);
        groupConfig.setMerId(0);
        return save(groupConfig);
    }

    /**
     * 修改积分区间
     */
    @Override
    public Boolean updateIntegralInterval(IntegralIntervalAddRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "区间ID不能为空");
        }
        GroupConfig groupConfig = getByIdException(request.getId());
        if (!groupConfig.getTag().equals(GroupConfigConstants.TAG_INTEGRAL_INTERVAL)) {
            throw new CrmebException("数据不存在");
        }
        if (!request.getName().equals(groupConfig.getName())) {
            if (isExistName(request.getName(), GroupConfigConstants.TAG_INTEGRAL_INTERVAL)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "区间名称已存在");
            }
        }
        if (!request.getValue().equals(groupConfig.getValue())) {
            String value = request.getValue();
            String[] split = value.split("-");
            int startValue = Integer.parseInt(split[0]);
            int endValue = Integer.parseInt(split[1]);
            if (endValue < startValue) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "积分区间值存在错误");
            }
        }
        groupConfig.setName(request.getName());
        groupConfig.setValue(request.getValue());
        groupConfig.setStatus(request.getStatus());
        groupConfig.setSort(request.getSort());
        return updateById(groupConfig);
    }

    /**
     * 删除积分区间
     */
    @Override
    public Boolean deleteIntegralInterval(Integer id) {
        GroupConfig groupConfig = getByIdException(id);
        if (!groupConfig.getTag().equals(GroupConfigConstants.TAG_INTEGRAL_INTERVAL)) {
            throw new CrmebException("数据不存在");
        }
        groupConfig.setIsDel(true);
        return updateById(groupConfig);
    }

    /**
     * 修改积分区间状态
     */
    @Override
    public Boolean updateIntegralIntervalStatus(Integer id) {
        GroupConfig groupConfig = getByIdException(id);
        if (!groupConfig.getTag().equals(GroupConfigConstants.TAG_INTEGRAL_INTERVAL)) {
            throw new CrmebException("数据不存在");
        }
        groupConfig.setStatus(!groupConfig.getStatus());
        return updateById(groupConfig);
    }

    /**
     * 查询tag下数据条数
     */
    @Override
    public Integer getCountByTag(Integer tag) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getIsDel, 0);
        return dao.selectCount(lqw);
    }

    @Override
    public GroupConfig getByIdException(Integer id) {
        GroupConfig groupConfig = getById(id);
        if (ObjectUtil.isNull(groupConfig) || groupConfig.getIsDel()) {
            throw new CrmebException("数据不存在");
        }
        return groupConfig;
    }

    /**
     * 通过tag和商户ID获取唯一数据
     *
     * @param tag 标签
     * @param merId 商户ID
     */
    @Override
    public GroupConfig getOneByTagAndMerId(Integer tag, Integer merId) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getMerId, merId);
        lqw.eq(GroupConfig::getIsDel, 0);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 通过tag和商户ID删除数据
     *
     * @param tag 标签
     * @param merId 商户ID
     */
    @Override
    public Boolean deleteByTagAndMerId(Integer tag, Integer merId) {
        LambdaUpdateWrapper<GroupConfig> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(GroupConfig::getIsDel, 1);
        wrapper.eq(GroupConfig::getTag, tag);
        wrapper.eq(GroupConfig::getMerId, merId);
        wrapper.eq(GroupConfig::getIsDel, 0);
        return update(wrapper);
    }

    /**
     * 保存单一数据
     * @param groupConfig 组合配置
     */
    @Override
    public Boolean saveOne(GroupConfig groupConfig) {
        Integer tag = groupConfig.getTag();
        if (StrUtil.isNotBlank(groupConfig.getImageUrl())) {
            groupConfig.setImageUrl(systemAttachmentService.clearPrefix(groupConfig.getImageUrl()));
        }
        return transactionTemplate.execute(e -> {
            deleteByTagAndMerId(tag, groupConfig.getMerId());
            save(groupConfig);
            return Boolean.TRUE;
        });
    }

    /**
     * 是否存在推荐板块名称
     * @param name 名称
     * @param tag 标签
     */
    @Override
    public Boolean isExistName(String name, Integer tag) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getName, name);
        lqw.eq(GroupConfig::getIsDel, 0);
        lqw.last(" limit 1");
        GroupConfig groupConfig = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(groupConfig);
    }
}

