package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.RetailStoreSubUserSearchRequest;
import com.zbkj.common.request.UserRecordSearchRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户佣金记录服务接口
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
public interface UserBrokerageRecordService extends IService<UserBrokerageRecord> {

    /**
     * 通过订单号获取佣金记录
     * @param orderNo 订单号
     * @return List
     */
    List<UserBrokerageRecord> getByOrderNo(String orderNo);

    /**
     * 获取记录列表
     * @param linkNo 关联单号
     * @param linkType 关联类型
     * @return 记录列表
     */
    List<UserBrokerageRecord> findListByLinkNoAndLinkType(String linkNo, String linkType);

    /**
     * 佣金解冻
     */
    void brokerageThaw();

    /**
     * 获取佣金明细列表根据uid
     * @param uid uid
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserBrokerageRecord> findDetailListByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取累计推广条数
     * @param uid 用户uid
     * @return Integer
     */
    Integer getSpreadCountByUid(Integer uid);

    /**
     * 获取推广记录列表
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<UserBrokerageRecord> findSpreadListByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取推广记录列表
     * @param request 用户uid
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserBrokerageRecord> findAdminSpreadListByUid(RetailStoreSubUserSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取月份对应的推广订单数
     * @param uid 用户uid
     * @param monthList 月份列表
     * @return Map
     */
    Map<String, Integer> getSpreadCountByUidAndMonth(Integer uid, List<String> monthList);

    /**
     * 获取佣金排行榜（周、月）
     * @param type week、month
     * @return List
     */
    List<UserBrokerageRecord> getBrokerageTopByDate(String type);

    /**
     * 根据Uid获取分佣记录列表
     * @param uid 用户uid
     * @return List
     */
    List<UserBrokerageRecord> getSpreadListByUid(Integer uid);

    /**
     * 获取冻结期佣金
     * @param uid uid
     * @return BigDecimal
     */
    BigDecimal getFreezePrice(Integer uid);

    /**
     * 获取已结算佣金
     * @param uid uid
     * @return BigDecimal
     */
    BigDecimal getSettledCommission(Integer uid);

    /**
     * 根据关联单号获取唯一记录
     * @param linkNo 关联单号
     * @return UserBrokerageRecord
     */
    UserBrokerageRecord getOneByLinkNo(String linkNo);

    /**
     * 获取用户佣金记录（管理端）
     */
    PageInfo<UserBrokerageRecord> getAdminUserBrokerageRecord(UserRecordSearchRequest request);
}
