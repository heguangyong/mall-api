package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.merchant.MerchantEmployee;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.record.BrowseRecord;
import com.zbkj.common.model.sgin.UserSignRecord;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBalanceRecord;
import com.zbkj.common.model.user.UserExperienceRecord;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.LoginResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.front.service.FrontOrderService;
import com.zbkj.front.service.UserCenterService;
import com.zbkj.service.dao.UserDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户中心 服务实现类
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
public class UserCenterServiceImpl extends ServiceImpl<UserDao, User> implements UserCenterService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SystemUserLevelService systemUserLevelService;
    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private UserExperienceRecordService userExperienceRecordService;
    @Autowired
    private FrontOrderService frontOrderService;
    @Autowired
    private BrowseRecordService browseRecordService;
    @Autowired
    private ProductService productService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private UserSignRecordService userSignRecordService;
    @Autowired
    private MerchantEmployeeService merchantEmployeeService;


    /**
     * 获取个人中心详情
     * @return 个人中心数据
     */
    @Override
    public UserCenterResponse getUserCenterInfo() {
        Integer uid = userService.getUserId();
        UserCenterResponse response = new UserCenterResponse();
        response.setCenterBanner(systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_USER_CENTER_BANNER));
        response.setCenterMenu(systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_USER_CENTER_MENU));
        if (uid <= 0) {
            response.setId(0);
            return response;
        }
        User user = getById(uid);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(LoginResultCode.LOGIN_EXPIRE);
        }
        BeanUtils.copyProperties(user, response);
        response.setPhone(CrmebUtil.maskMobile(response.getPhone()));
        // 优惠券数量
        response.setCouponCount(couponUserService.getUseCount(user.getId()));
        // 收藏数量
        response.setCollectCount(productRelationService.getCollectCountByUid(user.getId()));
        // 足迹
        response.setBrowseNum(browseRecordService.getCountByUid(uid));

        response.setIsVip(false);
        if (response.getLevel() > 0) {
            SystemUserLevel systemUserLevel = systemUserLevelService.getByLevelId(user.getLevel());
            if (ObjectUtil.isNotNull(systemUserLevel)) {
                response.setIsVip(true);
                response.setVipIcon(systemUserLevel.getIcon());
                response.setVipName(systemUserLevel.getName());
            }
        }
        // 判断是否展示我的推广，1.分销模式是否开启
        response.setIsPromoter(false);
        String retailStoreSwitch = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_SWITCH);
        if (retailStoreSwitch.equals(Constants.COMMON_SWITCH_OPEN) && user.getIsPromoter()) {
            response.setIsPromoter(true);
        }

        String paidMemberPaidEntranceStr = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_PAID_MEMBER_PAID_ENTRANCE);
        if (paidMemberPaidEntranceStr.equals(Constants.COMMON_SWITCH_OPEN)) {
            response.setPaidMemberPaidEntrance(1);
        }

        // 作用于当前用户在登录状态下获得了移动端管理权限
//        response.setLoginResponse(loginService.isEmployeeAuto(user));

        List<MerchantEmployee> employeeList = merchantEmployeeService.getShopMangerByUserId(user.getId());
        if (CollUtil.isNotEmpty(employeeList)) {
            response.setIsEmployee(true);
        }

        // 保存用户访问记录
        asyncService.visitUserCenter(user.getId());
        return response;
    }

    /**
     * 我的推广(冻结的佣金 累计提现金额 当前佣金)
     */
    @Override
    public UserMyPromotionResponse getMyPromotion() {
        User user = userService.getInfo();
        // 冻结的佣金
        BigDecimal freezePrice = userBrokerageRecordService.getFreezePrice(user.getId());
        //累计已提取佣金
        BigDecimal settledCommissionPrice = userBrokerageRecordService.getSettledCommission(user.getId());
        UserMyPromotionResponse response = new UserMyPromotionResponse();
        response.setFreezePrice(freezePrice);
        response.setSettledCommissionPrice(settledCommissionPrice);
        response.setBrokeragePrice(user.getBrokeragePrice());
        return response;
    }

    /**
     * 会员等级列表
     * @return List<UserLevel>
     */
    @Override
    public List<SystemUserLevel> getUserLevelList() {
        return systemUserLevelService.getH5LevelList();
    }

    /**
     * 我的账户
     * @return UserMyAccountResponse
     */
    @Override
    public UserMyAccountResponse getMyAccount() {
        User user = userService.getInfo();
        UserMyAccountResponse response = new UserMyAccountResponse();
        response.setNowMoney(user.getNowMoney());
        response.setMonetary(BigDecimal.ZERO);
        response.setRecharge(BigDecimal.ZERO);
        response.setRechargeSwitch(false);
        List<UserBalanceRecord> monetaryRecordList = userBalanceRecordService.getMonetaryRecordByUid(user.getId());
        if (CollUtil.isNotEmpty(monetaryRecordList)) {
            response.setMonetary(monetaryRecordList.stream().map(UserBalanceRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        List<UserBalanceRecord> rechargeRecordList = userBalanceRecordService.getRechargeRecordByUid(user.getId());
        if (CollUtil.isNotEmpty(rechargeRecordList)) {
            response.setRecharge(rechargeRecordList.stream().map(UserBalanceRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        String rechargeSwitch = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_RECHARGE_SWITCH);
        if (StrUtil.isNotBlank(rechargeSwitch) && rechargeSwitch.equals(Constants.CONFIG_FORM_SWITCH_OPEN)) {
            response.setRechargeSwitch(true);
        }
        return response;
    }

    /**
     * 用户余额记录
     * @param recordType 记录类型：all-全部，expenditure-支出，income-收入，recharge-充值
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserBalanceRecordMonthResponse> getUserBalanceRecord(String recordType, PageParamRequest pageRequest) {
        if (StrUtil.isBlank(recordType)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED,  "记录类型不能为空");
        }
        if (!recordType.equals("all") && !recordType.equals("expenditure") && !recordType.equals("income") && !recordType.equals("recharge")) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "未知的记录类型");
        }
        Integer uid = userService.getUserIdException();
        PageInfo<UserBalanceRecord> pageInfo = userBalanceRecordService.getUserBalanceRecord(uid, recordType, pageRequest);
        List<UserBalanceRecord> recordList = pageInfo.getList();
        if (CollUtil.isEmpty(recordList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        // 获取年-月
        Map<String, List<UserBalanceRecord>> map = CollUtil.newHashMap();
        recordList.forEach(i -> {
            String month = StrUtil.subPre(CrmebDateUtil.dateToStr(i.getCreateTime(), DateConstants.DATE_FORMAT), 7);
            if (map.containsKey(month)) {
                map.get(month).add(i);
            } else {
                List<UserBalanceRecord> list = CollUtil.newArrayList();
                list.add(i);
                map.put(month, list);
            }
        });
        List<UserBalanceRecordMonthResponse> responseList = CollUtil.newArrayList();
        map.forEach((key, value) -> {
            UserBalanceRecordMonthResponse response = new UserBalanceRecordMonthResponse();
            response.setMonth(key);
            response.setList(value);
            responseList.add(response);
        });
        List<UserBalanceRecordMonthResponse> collect = responseList.stream().sorted(Comparator.comparing(s -> DateUtil.parse(s.getMonth(), "yyyy-MM").getTime(), Comparator.reverseOrder())).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, collect);
    }

    /**
     * 我的积分(当前积分 累计积分 累计消费 冻结中积分)
     *
     * @return 我的积分
     */
    @Override
    public UserMyIntegralResponse getMyIntegral() {
        User user = userService.getInfo();
        Integer settledIntegral = userIntegralRecordService.getSettledIntegralByUid(user.getId());
        Integer freezeIntegral = userIntegralRecordService.getFreezeIntegralByUid(user.getId());

        UserMyIntegralResponse response = new UserMyIntegralResponse();
        response.setIntegral(user.getIntegral());
        response.setSettledIntegral(settledIntegral);
        response.setUseIntegral(settledIntegral - user.getIntegral());
        response.setFreezeIntegral(freezeIntegral);
        return response;
    }

    /**
     * 用户积分记录列表
     * @param pageParamRequest 分页参数
     * @return PageInfo<UserIntegralRecord>
     */
    @Override
    public PageInfo<UserIntegralRecord> getUserIntegralRecordList(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        return userIntegralRecordService.findUserIntegralRecordList(uid, pageParamRequest);
    }

    /**
     * 获取用户经验记录
     * @param pageParamRequest 分页参数
     * @return List<UserExperienceRecord>
     */
    @Deprecated
    @Override
    public List<UserExperienceRecord> getUserExperienceList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        return userExperienceRecordService.getH5List(userId, pageParamRequest);
    }

    /**
     * 个人中心-订单头部数量
     */
    @Override
    public OrderCenterNumResponse getUserCenterOrderNum() {
        return frontOrderService.userCenterNum();
    }

    /**
     * 获取用户浏览足迹
     * @return 用户浏览足迹
     */
    @Override
    public List<UserBrowseRecordDateResponse> getUserBrowseRecord() {
        Integer userId = userService.getUserIdException();
        List<BrowseRecord> browseRecordList = browseRecordService.findAllByUid(userId);
        if (CollUtil.isEmpty(browseRecordList)) {
            return new ArrayList<>();
        }
        List<Integer> proIdList = browseRecordList.stream().map(BrowseRecord::getProductId).collect(Collectors.toList());
        Map<Integer, Product> productMap = productService.getMapByIdList(proIdList);
        Map<String, List<BrowseResponse>> map = browseRecordList.stream().map(browseRecord -> {
            BrowseResponse browseResponse = new BrowseResponse();
            BeanUtils.copyProperties(browseRecord, browseResponse);
            browseResponse.setName(productMap.get(browseRecord.getProductId()).getName());
            browseResponse.setImage(productMap.get(browseRecord.getProductId()).getImage());
            browseResponse.setIsShow(productMap.get(browseRecord.getProductId()).getIsShow());
            browseResponse.setIsDel(productMap.get(browseRecord.getProductId()).getIsDel());
            browseResponse.setPrice(productMap.get(browseRecord.getProductId()).getPrice());
            return browseResponse;
        }).collect(Collectors.groupingBy(BrowseResponse::getDate));

        List<UserBrowseRecordDateResponse> responseList = CollUtil.newArrayList();
        map.forEach((key, value) -> {
            UserBrowseRecordDateResponse response = new UserBrowseRecordDateResponse();
            response.setDate(key);
            response.setList(value);
            responseList.add(response);
        });
        return responseList.stream().sorted(Comparator.comparing(s -> DateUtil.parse(s.getDate(), "yyyy-MM-dd").getTime(), Comparator.reverseOrder())).collect(Collectors.toList());
    }

    /**
     * 我的经验
     */
    @Override
    public UserMyExpResponse getMyExp() {
        User user = userService.getInfo();
        UserMyExpResponse response = new UserMyExpResponse();
        String userLevelSwitch = systemConfigService.getValueByKeyException(UserLevelConstants.SYSTEM_USER_LEVEL_SWITCH);
        if (userLevelSwitch.equals(Constants.COMMON_SWITCH_CLOSE)) {
            response.setUserLevelSwitch(false);
            return response;
        }
        // 会员等级列表
        SystemUserLevel userLevel = systemUserLevelService.getByLevelId(user.getLevel());
        if (ObjectUtil.isNull(userLevel)) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("用户等级不存在"));
        }
        response.setAvatar(user.getAvatar());
        response.setNickname(user.getNickname());
        response.setExperience(user.getExperience());
        response.setLevel(user.getLevel());
        response.setUserLevelName(userLevel.getName());
        response.setGrade(userLevel.getGrade());
        response.setIcon(userLevel.getIcon());
        response.setBackImage(userLevel.getBackImage());
        response.setBackColor(userLevel.getBackColor());
        SystemUserLevel nextLevel = systemUserLevelService.getNextLevel(userLevel.getGrade());
        if (ObjectUtil.isNotNull(nextLevel)) {
            response.setUpExperience(nextLevel.getExperience());
            response.setNextLevelName(nextLevel.getName());
        }

        UserSignRecord lastSignRecord = userSignRecordService.getLastByUid(user.getId());
        String todayStr = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        if (ObjectUtil.isNotNull(lastSignRecord) && lastSignRecord.getDate().equals(todayStr)) {
            response.setTodaySign(true);
        }
        String noteMaxNum = systemConfigService.getValueByKeyException(UserLevelConstants.SYSTEM_USER_LEVEL_COMMUNITY_NOTES_NUM);
        String noteExp = systemConfigService.getValueByKeyException(UserLevelConstants.SYSTEM_USER_LEVEL_COMMUNITY_NOTES_EXP);
        response.setNoteExp(Integer.parseInt(noteExp));
        response.setNoteMaxNum(Integer.parseInt(noteMaxNum));
        if (response.getNoteMaxNum() > 0) {
            response.setNoteNum(userExperienceRecordService.getNoteNumRecordByDate(user.getId(), todayStr));
        }
        if (response.getNoteExp() > 0 && response.getNoteMaxNum() > 0) {
            response.setIsOpenCommunity(true);
        }

        response.setTodayExp(userExperienceRecordService.getCountByDate(user.getId(), todayStr));
        return response;
    }

    /**
     * 我的经验记录列表
     * @param pageRequest 分页参数
     */
    @Override
    public PageInfo<UserExperienceRecordMonthResponse> findMyExpRecordList(PageParamRequest pageRequest) {
        Integer userId = userService.getUserIdException();
        PageInfo<UserExperienceRecord> pageInfo = userExperienceRecordService.getH5List_V1_2(userId, "", pageRequest);
        List<UserExperienceRecord> recordList = pageInfo.getList();
        if (CollUtil.isEmpty(recordList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        // 获取年-月
        Map<String, List<UserExperienceRecord>> map = CollUtil.newHashMap();
        recordList.forEach(i -> {
            String month = StrUtil.subPre(CrmebDateUtil.dateToStr(i.getCreateTime(), DateConstants.DATE_FORMAT), 7);
            if (map.containsKey(month)) {
                map.get(month).add(i);
            } else {
                List<UserExperienceRecord> list = CollUtil.newArrayList();
                list.add(i);
                map.put(month, list);
            }
        });
        List<UserExperienceRecordMonthResponse> responseList = CollUtil.newArrayList();
        map.forEach((key, value) -> {
            UserExperienceRecordMonthResponse response = new UserExperienceRecordMonthResponse();
            response.setMonth(key);
            response.setList(value);
            responseList.add(response);
        });
        List<UserExperienceRecordMonthResponse> collect = responseList.stream().sorted(Comparator.comparing(s -> DateUtil.parse(s.getMonth(), "yyyy-MM").getTime(), Comparator.reverseOrder())).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, collect);
    }

    /**
     * 用户签到经验记录
     * @param pageParamRequest 分页参数
     * @return PageInfo<UserExperienceRecord>
     */
    @Override
    public PageInfo<UserExperienceRecord> getUserSignExperienceList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        return userExperienceRecordService.getH5List_V1_2(userId, ExperienceRecordConstants.EXPERIENCE_RECORD_LINK_TYPE_SIGN, pageParamRequest);
    }

}
