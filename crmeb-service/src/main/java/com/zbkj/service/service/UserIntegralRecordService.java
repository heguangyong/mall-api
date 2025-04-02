package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.request.IntegralPageSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserRecordSearchRequest;
import com.zbkj.common.response.IntegralRecordPageResponse;

/**
 * 用户积分记录Service
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
public interface UserIntegralRecordService extends IService<UserIntegralRecord> {

    /**
     * 积分解冻
     */
    void integralThaw();

    /**
     * H5用户积分列表
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @return List
     */
    PageInfo<UserIntegralRecord> findUserIntegralRecordList(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 根据订单号跟类型获取记录（一个订单同类型只会有一条数据）
     * @param orderNo 订单编号
     * @param type 类型：1-增加，2-扣减
     * @return UserIntegralRecord
     */
    UserIntegralRecord getByOrderNoAndType(String orderNo, Integer type);

    /**
     * 用户累计积分
     * @param uid 用户id
     * @return 用户累计积分
     */
    Integer getSettledIntegralByUid(Integer uid);

    /**
     * 用户冻结积分
     * @param uid 用户id
     * @return 用户冻结积分
     */
    Integer getFreezeIntegralByUid(Integer uid);

    /**
     * 管理端查询积分记录分页列表
     * @param request 搜索条件
     * @return 记录列表
     */
    PageInfo<IntegralRecordPageResponse> findRecordPageListByPlat(IntegralPageSearchRequest request);

    /**
     * 用户积分记录（管理端）
     */
    PageInfo<UserIntegralRecord> getAdminUserIntegralRecord(UserRecordSearchRequest request);
}
