package com.zbkj.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserExperienceRecord;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.*;

import java.util.List;

/**
 * 用户中心 服务类
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
public interface UserCenterService extends IService<User> {

    /**
     * 获取个人中心详情
     * @return 个人中心数据
     */
    UserCenterResponse getUserCenterInfo();

    /**
     * 我的推广(冻结的佣金 累计提现金额 当前佣金)
     * @return UserMyPromotionResponse
     */
    UserMyPromotionResponse getMyPromotion();

    /**
     * 会员等级列表
     */
    List<SystemUserLevel> getUserLevelList();

    /**
     * 我的账户
     * @return UserMyAccountResponse
     */
    UserMyAccountResponse getMyAccount();

    /**
     * 用户余额记录
     * @param recordType 记录类型：all-全部，expenditure-支出，income-收入，recharge-充值
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserBalanceRecordMonthResponse> getUserBalanceRecord(String recordType, PageParamRequest pageRequest);

    /**
     * 我的积分(当前积分 累计积分 累计消费 冻结中积分)
     *
     * @return 我的积分
     */
    UserMyIntegralResponse getMyIntegral();

    /**
     * 用户积分记录列表
     * @param pageParamRequest 分页参数
     * @return PageInfo<UserIntegralRecord>
     */
    PageInfo<UserIntegralRecord> getUserIntegralRecordList(PageParamRequest pageParamRequest);

    /**
     * 获取用户经验记录
     * @param pageParamRequest 分页参数
     * @return List<UserExperienceRecord>
     */
    @Deprecated
    List<UserExperienceRecord> getUserExperienceList(PageParamRequest pageParamRequest);

    /**
     * 个人中心-订单头部数量
     */
    OrderCenterNumResponse getUserCenterOrderNum();

    /**
     * 获取用户浏览足迹
     * @return 用户浏览足迹
     */
    List<UserBrowseRecordDateResponse> getUserBrowseRecord();

    /**
     * 我的经验
     */
    UserMyExpResponse getMyExp();

    /**
     * 我的经验记录列表
     * @param pageRequest 分页参数
     */
    PageInfo<UserExperienceRecordMonthResponse> findMyExpRecordList(PageParamRequest pageRequest);

    /**
     * 用户签到经验记录
     * @param pageParamRequest 分页参数
     * @return PageInfo<UserExperienceRecord>
     */
    PageInfo<UserExperienceRecord> getUserSignExperienceList(PageParamRequest pageParamRequest);

}
