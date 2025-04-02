package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.BrokerageRecordConstants;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.RetailStoreSubUserSearchRequest;
import com.zbkj.common.request.UserRecordSearchRequest;
import com.zbkj.common.utils.ArrayUtil;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.UserBrokerageRecordDao;
import com.zbkj.service.service.UserBrokerageRecordService;
import com.zbkj.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户佣金记录服务接口实现类
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
public class UserBrokerageRecordServiceImpl extends ServiceImpl<UserBrokerageRecordDao, UserBrokerageRecord> implements UserBrokerageRecordService {

    private static final Logger logger = LoggerFactory.getLogger(UserBrokerageRecordServiceImpl.class);

    @Resource
    private UserBrokerageRecordDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 通过订单号获取佣金记录
     *
     * @param orderNo 订单号
     * @return List
     */
    @Override
    public List<UserBrokerageRecord> getByOrderNo(String orderNo) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserBrokerageRecord::getLinkNo, orderNo);
        return dao.selectList(lqw);
    }

    /**
     * 根据订单编号获取记录列表
     *
     * @param linkNo   关联单号
     * @param linkType 关联类型
     * @return 记录列表
     */
    @Override
    public List<UserBrokerageRecord> findListByLinkNoAndLinkType(String linkNo, String linkType) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getLinkNo, linkNo);
        lqw.eq(UserBrokerageRecord::getLinkType, linkType);
        return dao.selectList(lqw);
    }

    /**
     * 佣金解冻
     */
    @Override
    public void brokerageThaw() {
        // 查询需要解冻的佣金
        List<UserBrokerageRecord> thawList = findThawList();
        if (CollUtil.isEmpty(thawList)) {
            return;
        }
        for (UserBrokerageRecord record : thawList) {
            // 查询对应的用户
            User user = userService.getById(record.getUid());
            if (ObjectUtil.isNull(user)) {
                continue;
            }
            record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
            // 计算佣金余额
            BigDecimal balance = user.getBrokeragePrice().add(record.getPrice());
            record.setBalance(balance);
            record.setUpdateTime(DateUtil.date());
            // 分佣
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(record);
                userService.updateBrokerage(record.getUid(), record.getPrice(), Constants.OPERATION_TYPE_ADD);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("佣金解冻处理—数据库出错，记录id = {}", record.getId()));
            }
        }

    }

    /**
     * 获取佣金明细列表根据uid
     *
     * @param uid              uid
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserBrokerageRecord> findDetailListByUid(Integer uid, PageParamRequest pageParamRequest) {
        Page<UserBrokerageRecord> recordPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        QueryWrapper<UserBrokerageRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("status", BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        queryWrapper.orderByDesc("id");
        List<UserBrokerageRecord> recordList = dao.selectList(queryWrapper);
        return CommonPage.copyPageInfo(recordPage, recordList);
    }

    /**
     * 获取累计推广条数
     *
     * @param uid 用户uid
     * @return Integer
     */
    @Override
    public Integer getSpreadCountByUid(Integer uid) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getId);
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        return dao.selectCount(lqw);
    }

    /**
     * 获取推广记录列表
     *
     * @param uid              用户uid
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public List<UserBrokerageRecord> findSpreadListByUid(Integer uid, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        lqw.orderByDesc(UserBrokerageRecord::getUpdateTime);
        return dao.selectList(lqw);
    }

    /**
     * 获取推广记录列表
     * @param request 用户uid
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserBrokerageRecord> findAdminSpreadListByUid(RetailStoreSubUserSearchRequest request, PageParamRequest pageParamRequest) {
        Page<Object> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getUid, request.getUid());
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        if (request.getType().equals(1)) {
            lqw.eq(UserBrokerageRecord::getBrokerageLevel, 1);
        }
        if (request.getType().equals(2)) {
            lqw.eq(UserBrokerageRecord::getBrokerageLevel, 2);
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.like(UserBrokerageRecord::getLinkNo, keywords);
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            lqw.between(UserBrokerageRecord::getUpdateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        lqw.orderByDesc(UserBrokerageRecord::getUpdateTime);
        return CommonPage.copyPageInfo(page, dao.selectList(lqw));
    }

    /**
     * 获取月份对应的推广订单数
     *
     * @param uid       用户uid
     * @param monthList 月份列表
     * @return Map
     */
    @Override
    public Map<String, Integer> getSpreadCountByUidAndMonth(Integer uid, List<String> monthList) {
        QueryWrapper<UserBrokerageRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("count(id) as uid, update_time");
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("link_type", BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        queryWrapper.eq("status", BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        queryWrapper.apply(StrUtil.format("left(update_time, 7) in ({})", ArrayUtil.strListToSqlJoin(monthList)));
        queryWrapper.groupBy("left(update_time, 7)");
        List<UserBrokerageRecord> list = dao.selectList(queryWrapper);
        Map<String, Integer> map = CollUtil.newHashMap();
        if (CollUtil.isEmpty(list)) {
            return map;
        }
        list.forEach(record -> {
            map.put(CrmebDateUtil.dateToStr(record.getUpdateTime(), DateConstants.DATE_FORMAT_MONTH), record.getUid());
        });
        return map;
    }

    /**
     * 获取佣金排行榜（周、月）(取前50)
     *
     * @param type week、month
     * @return List
     */
    @Override
    public List<UserBrokerageRecord> getBrokerageTopByDate(String type) {
        QueryWrapper<UserBrokerageRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("uid", "sum(price) AS price");
        queryWrapper.eq("link_type", BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        queryWrapper.eq("status", BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(type);
        if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
            queryWrapper.between("update_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        queryWrapper.groupBy("uid");
        queryWrapper.orderByDesc("price");
        queryWrapper.last(" limit 50");
        return dao.selectList(queryWrapper);
    }

    /**
     * 根据Uid获取分佣记录列表
     * @param uid 用户uid
     * @return List
     */
    @Override
    public List<UserBrokerageRecord> getSpreadListByUid(Integer uid) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getType, BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        return dao.selectList(lqw);
    }

    /**
     * 获取冻结期佣金
     *
     * @param uid uid
     * @return BigDecimal
     */
    @Override
    public BigDecimal getFreezePrice(Integer uid) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getPrice);
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.in(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE);
        List<UserBrokerageRecord> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取已结算佣金
     *
     * @param uid uid
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSettledCommission(Integer uid) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getPrice);
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_WITHDRAW);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        List<UserBrokerageRecord> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 根据关联单号获取唯一记录
     *
     * @param linkNo 关联单号
     * @return UserBrokerageRecord
     */
    @Override
    public UserBrokerageRecord getOneByLinkNo(String linkNo) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getLinkNo, linkNo);
        return dao.selectOne(lqw);
    }

    /**
     * 获取用户佣金记录（管理端）
     */
    @Override
    public PageInfo<UserBrokerageRecord> getAdminUserBrokerageRecord(UserRecordSearchRequest request) {
        Page<UserBrokerageRecord> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<UserBrokerageRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserBrokerageRecord::getUid, request.getUserId());
        lqw.orderByDesc(UserBrokerageRecord::getId);
        List<UserBrokerageRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }

    /**
     * 获取需要解冻的记录列表
     *
     * @return 记录列表
     */
    private List<UserBrokerageRecord> findThawList() {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.le(UserBrokerageRecord::getThawTime, System.currentTimeMillis());
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getType, BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN);
        return dao.selectList(lqw);
    }
}

