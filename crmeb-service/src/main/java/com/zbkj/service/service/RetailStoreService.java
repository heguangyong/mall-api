package com.zbkj.service.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.vo.RetailStoreConfigVo;

import java.util.List;

/**
 * 分销业务
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
public interface RetailStoreService {

//    /**
//     * 分销员列表
//     * @param keywords 搜索参数
//     * @param dateLimit 时间参数
//     * @param pageRequest 分页参数
//     * @return CommonPage
//     */
//    CommonPage<SpreadUserResponse> getSpreadPeopleList(String keywords, String dateLimit, PageParamRequest pageRequest);

    /**
     * 获取分销配置
     *
     * @return 分销配置信息
     */
    RetailStoreConfigVo getRetailStoreConfig();

    /**
     * 保存或者更新分销配置信息
     *
     * @param retailShopRequest 待保存数据
     * @return 保存结果
     */
    Boolean saveRetailStoreConfig(RetailStoreConfigVo retailShopRequest);

    /**
     * 获取推广订单总数量
     *
     * @return 推广订单总数量
     */
    Integer getSpreadOrderCount();

    /**
     * 推广订单
     *
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<UserSpreadOrderMonthResponse> getSpreadOrder(PageParamRequest pageParamRequest);

    /**
     * 推广人团队数量
     *
     * @return 推广人团队数量
     */
    UserSpreadPeopleTeamNumResponse getSpreadPeopleTeamNum();

    /**
     * 获取推广人列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页
     * @return List<UserSpreadPeopleItemResponse>
     */
    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(UserSpreadPeopleRequest request, PageParamRequest pageParamRequest);

    /**
     * 推广人排行榜
     *
     * @param type 时间范围(week-周，month-月)
     * @return List<User>
     */
    List<User> getSpreadPeopleTopByDate(String type);

    /**
     * 佣金排行榜
     *
     * @param type String 时间范围
     * @return List<User>
     */
    List<User> getBrokerageTopByDate(String type);

    /**
     * 佣金记录
     *
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<BrokerageRecordDetailResponse> getBrokerageRecord(PageParamRequest pageParamRequest);

    /**
     * 推广海报轮播图
     *
     * @return List
     */
    List<RetailStorePosterBannerResponse> getPosterBanner();

    /**
     * 绑定推广关系（登录状态）
     *
     * @param spreadPid 推广人
     */
    void bindingUser(Integer spreadPid);

    /**
     * 获取结算配置
     */
    UserClosingConfigResponse getUserClosingConfig();

    /**
     * 用户结算申请
     *
     * @param request 申请参数
     * @return Boolean
     */
    Boolean userClosingApply(UserClosingApplyRequest request);

    /**
     * 用户结算记录
     *
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<UserClosingRecordResponse> getUserClosingRecord(PageParamRequest pageParamRequest);

    /**
     * 佣金转入余额
     *
     * @param request 请求参数
     * @return Boolean
     */
    Boolean brokerageToYue(BrokerageToYueRequest request);

    /**
     * 修改用户上级推广人
     */
    Boolean updateUserSpread(UserUpdateSpreadRequest request);

    /**
     * 清除用户上级推广人
     */
    Boolean clearUserSpread(Integer id);

    /**
     * 分销员列表
     */
    PageInfo<SpreadUserResponse> getRetailStorePeoplePage(RetailStorePeopleSearchRequest request);

    /**
     * 根据条件获取下级推广用户列表
     * @param request 查询参数
     * @param pageRequest 分页参数
     * @return 下级推广用户列表
     */
    PageInfo<User> getRetailStoreSubUserList(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest);

    /**
     * 根据条件获取推广订单列表
     * @param request 查询参数
     * @param pageRequest 分页参数
     * @return 推广订单列表
     */
    PageInfo<PromotionOrderResponse> getPromotionOrderList(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest);
}
