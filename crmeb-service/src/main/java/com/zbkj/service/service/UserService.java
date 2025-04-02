package com.zbkj.service.service;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.*;
import com.zbkj.common.request.merchant.MerchantUserSearchRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.vo.LoginFrontUserVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户表 服务类
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
public interface UserService extends IService<User> {

    /**
     * 手机号注册用户
     *
     * @param phone     手机号
     * @param spreadUid 推广人编号
     * @return User
     */
    User registerPhone(String phone, Integer spreadUid);

    /**
     * 根据手机号查询用户
     *
     * @param phone 用户手机号
     * @return 用户信息
     */
    User getByPhone(String phone);

    /**
     * 检测能否绑定关系
     *
     * @param user      当前用户
     * @param spreadUid 推广员Uid
     * @param type      用户类型:new-新用户，old—老用户
     * @return Boolean
     */
    Boolean checkBingSpread(User user, Integer spreadUid, String type);

    /**
     * 更新推广员推广数
     *
     * @param uid  uid
     * @param type add or sub
     * @return Boolean
     */
    Boolean updateSpreadCountByUid(Integer uid, String type);

    /**
     * 修改密码
     *
     * @param request 修改密码参数
     * @return Boolean
     */
    Boolean password(PasswordRequest request);

    /**
     * 获取当前用户ID，带异常
     */
    Integer getUserIdException();

    /**
     * 获取当前用户ID
     */
    Integer getUserId();

    /**
     * 获取当前用户信息
     */
    User getInfo();

    /**
     * 移动端当前用户信息
     *
     * @return UserCenterResponse
     */
    UserInfoResponse getUserInfo();


    /**
     * 获取当前用户所对应的 移动端管理权限
     *
     * @return 所对应的 移动端管理权限
     */
    LoginFrontUserVo getEmployeeInfo();

    /**
     * 修改个人信息
     *
     * @param request 修改信息
     */
    Boolean editUser(UserEditInfoRequest request);

    /**
     * 获取当前用户手机号验证码
     */
    Boolean getCurrentPhoneCode();

    /**
     * 换绑手机号获取验证码
     *
     * @param request 请求参数
     */
    Boolean updatePhoneCode(UserBindingPhoneUpdateRequest request);

    /**
     * 换绑手机号
     *
     * @param request 请求参数
     */
    Boolean updatePhone(UserBindingPhoneUpdateRequest request);

    /**
     * 平台端用户分页列表
     *
     * @param request          查询参数
     * @return PageInfo
     */
    PageInfo<UserResponse> getPlatformPage(UserSearchRequest request);

    /**
     * 商户端用户分页列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserResponse> getMerchantPage(MerchantUserSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 更新用户
     *
     * @param userRequest 用户参数
     */
    Boolean updateUser(UserUpdateRequest userRequest);

    /**
     * 是否用户使用标签
     *
     * @param tagId 标签id
     * @return Boolean
     */
    Boolean isUsedTag(Integer tagId);

    /**
     * 用户分配标签
     *
     * @param request 标签参数
     */
    Boolean tag(UserAssignTagRequest request);

    /**
     * 清除对应的用户等级
     * @param levelId 等级id
     */
    Boolean removeLevelByLevelId(Integer levelId);

    /**
     * 获取uidMap
     * @param uidList uid列表
     * @return Map
     */
    Map<Integer, User> getUidMapList(List<Integer> uidList);

    /**
     * 更新余额
     * @param uid 用户ID
     * @param price 金额
     * @param type 增加add、扣减sub
     * @return Boolean
     */
    Boolean updateNowMoney(Integer uid, BigDecimal price, String type);

    /**
     * 更新用户积分
     * @param uid 用户ID
     * @param integral 积分
     * @param type 增加add、扣减sub
     * @return Boolean
     */
    Boolean updateIntegral(Integer uid, Integer integral, String type);

    /**
     * 更新用户佣金
     * @param uid 用户ID
     * @param price 金额
     * @param type 增加add、扣减sub
     * @return Boolean
     */
    Boolean updateBrokerage(Integer uid, BigDecimal price, String type);

    /**
     * 更新用户经验
     * @param uid 用户ID
     * @param experience 经验
     * @param type 增加add、扣减sub
     * @return Boolean
     */
    Boolean updateExperience(Integer uid, Integer experience, String type);

    /**
     * 佣金转余额
     * @param uid 用户ID
     * @param price 转入金额
     * @return Boolean
     */
    Boolean brokerageToYue(Integer uid, BigDecimal price);

    /**
     * 操作用户积分
     * @param request 请求参数
     * @return Boolean
     */
    Boolean operateUserInteger(UserOperateIntegralRequest request);

    /**
     * 操作用户余额
     * @param request 请求参数
     * @return Boolean
     */
    Boolean operateUserBalance(UserOperateBalanceRequest request);

    /**
     * 支付成功，用户信息变更
     * @param id 用户id
     * @param isPromoter 是否成为推广员
     */
    Boolean paySuccessChange(Integer id, Boolean isPromoter);

    /**
     * 根据用户id获取自己本身的推广用户
     *
     * @param userIdList List<Integer> 用户id集合
     * @return List<User>
     */
    List<Integer> getSpreadPeopleIdList(List<Integer> userIdList);

    /**
     * 根据用户id获取自己本身的推广用户
     */
    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(Integer userId, List<Integer> spreadUserIdList, String keywords, String sortKey, String isAsc, PageParamRequest pageParamRequest);

    /**
     * 推广人排行(取前50)
     * @param type             时间范围(week-周，month-月)
     * @return List<User>
     */
    List<User> getSpreadPeopleTopByDate(String type);

    /**
     * 绑定推广关系（登录状态）
     * @param spreadUid 推广人id
     */
    void bindSpread(Integer spreadUid);

    /**
     * 获取用户总人数
     */
    Integer getTotalNum();

    /**
     * 根据日期获取注册用户数量
     * @param date 日期 yyyy-MM-dd
     * @return 新增用户数
     */
    Integer getRegisterNumByDate(String date);

    /**
     * 获取用户渠道数据
     * @return List
     */
    List<User> getChannelData();

    /**
     * 更新用户推广人
     * @param userId 用户ID
     * @param spreadUid 推广人ID
     */
    Boolean updateSpreadByUid(Integer userId, Integer spreadUid);

    /**
     * PC后台分销员列表
     */
    PageInfo<User> getRetailStorePeoplePage(RetailStorePeopleSearchRequest request);

    /**
     * 根据推广级别和其他参数当前用户下的推广列表
     *
     * @param request 推广列表参数
     * @return 当前用户的推广人列表
     */
    PageInfo<User> getRetailStoreSubUserList(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest);

    /**
     * 更新用户连续签到天数
     * @param day 连续签到天数
     * @param id 用户ID
     * @return Boolean
     */
    Boolean updateSignNumByUid(Integer day, Integer id);

    /**
     * 用户注销数据前置
     */
    UserLogoffBeforeResponse logoffBefore();

    /**
     * 用户注销
     */
    Boolean logoff();

    /**
     * 管理端用户详情
     * @param id 用户ID
     */
    UserAdminDetailResponse getAdminDetail(Integer id);

    /**
     * 更新用户等级
     * @param userId 用户ID
     * @param level 用户等级
     */
    Boolean updateUserLevel(Integer userId, Integer level);

    /**
     * 通过生日获取用户列表
     * @param birthday 生日日期
     */
    List<User> findByBirthday(String birthday);

    /**
     * 手机号修改密码获取验证码
     */
    Boolean getUpdatePasswordPhoneCode();
    /**
     * 获取当前操作管理用户 或者移动端管理用户
     * @param isEmployee
     * @return
     */
    SystemAdmin getSystemAdminOrMerchantEmployee(Boolean isEmployee);

    /**
     * 获取当前的移动端商户管理者信息
     * @return 移动端管理者身份
     */
    SystemAdmin getSystemAdminByMerchantEmployee();

    /**
     * 赠送用户付费会员
     */
    Boolean giftPaidMember(GiftPaidMemberRequest request);

    Boolean updateUserPaidMember(Integer uid, boolean isPaidMember, boolean isPermanentPaidMember, DateTime expirationTime, Date oldTime);

    /**
     * 查询过期的会员
     */
    List<User> findMemberExpirationList();

    /**
     * 会员过期处理
     */
    Boolean memberExpirationProcessing();
}
