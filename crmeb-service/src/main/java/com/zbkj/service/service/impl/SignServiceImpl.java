package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.sgin.SignConfig;
import com.zbkj.common.model.sgin.UserSignRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserExperienceRecord;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SignConfigRequest;
import com.zbkj.common.request.SignRecordSearchRequest;
import com.zbkj.common.response.SignConfigResponse;
import com.zbkj.common.response.SignPageInfoResponse;
import com.zbkj.common.response.UserSignRecordResponse;
import com.zbkj.common.vo.PaidMemberBenefitsVo;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 签到服务实现类
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
public class SignServiceImpl implements SignService {

    @Autowired
    private SignConfigService signConfigService;
    @Autowired
    private UserSignRecordService userSignRecordService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private UserExperienceRecordService userExperienceRecordService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private PaidMemberService paidMemberService;

    /**
     * 获取签到配置
     */
    @Override
    public SignConfigResponse getConfig() {
        String signRule = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_SIGN_RULE_DESCRIPTION);
        SignConfigResponse response = new SignConfigResponse();
        response.setSignRuleDescription(signRule);
        List<SignConfig> configList = signConfigService.findList();
        if (CollUtil.isEmpty(configList)) {
            response.setBaseSignConfig(getInitBaseConfig());
            return response;
        }
        for (int i = 0; i < configList.size(); i++) {
            SignConfig signConfig = configList.get(i);
            if (signConfig.getDay().equals(0)) {
                response.setBaseSignConfig(signConfig);
                configList.remove(i);
                break;
            }
        }
        if (ObjectUtil.isNull(response.getBaseSignConfig())) {
            response.setBaseSignConfig(getInitBaseConfig());
        }
        response.setSignConfigList(configList);
        return response;
    }

    /**
     * 新增连续签到配置
     * @param request 配置参数
     * @return Boolean
     */
    @Override
    public Boolean addConfig(SignConfigRequest request) {
        request.setId(null);
        return signConfigService.add(request);
    }

    /**
     * 删除连续签到配置
     * @param id 签到配置id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        return signConfigService.delete(id);
    }

    /**
     * 编辑基础签到配置
     * @param request 配置参数
     * @return Boolean
     */
    @Override
    public Boolean editBaseConfig(SignConfigRequest request) {
        if (StrUtil.isBlank(request.getSignRuleDescription())) {
            throw new CrmebException("请填写签到规则说明");
        }
        return transactionTemplate.execute(e -> {
            signConfigService.editBaseConfig(request);
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_SIGN_RULE_DESCRIPTION, request.getSignRuleDescription());
            return Boolean.TRUE;
        });
    }

    /**
     * 编辑连续签到配置
     * @param request 配置参数
     * @return Boolean
     */
    @Override
    public Boolean editAwardConfig(SignConfigRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException("签到配置id不能为空");
        }
        return signConfigService.editAwardConfig(request);
    }

    /**
     * 获取用户签到记录
     * @param request 分页参数
     */
    @Override
    public PageInfo<UserSignRecordResponse> getSignRecordList(SignRecordSearchRequest request) {
        return userSignRecordService.pageRecordList(request);
    }

    /**
     * 获取签到页信息
     * @param month 月份 yyyy-MM
     * @return 签到页信息
     */
    @Override
    public SignPageInfoResponse getPageInfo(String month) {
        User user = userService.getInfo();
        DateTime date = DateUtil.date();
        String todayStr = date.toString(DateConstants.DATE_FORMAT_DATE);
        // 是否签到提示
        boolean isTip = false;
        // 判断今天是否签到
        UserSignRecord lastSignRecord = userSignRecordService.getLastByUid(user.getId());
        if (ObjectUtil.isNull(lastSignRecord) || !lastSignRecord.getDate().equals(todayStr)) {
            lastSignRecord = sign(todayStr, user, lastSignRecord);
            isTip = true;
        }
        //获取月签到记录
        String nowMonth = date.toString(DateConstants.DATE_FORMAT_MONTH);
        if (StrUtil.isNotBlank(month) && !nowMonth.equals(month)) {
            String lastMonthStr = DateUtil.offsetMonth(DateUtil.beginOfMonth(date), -1).toString(DateConstants.DATE_FORMAT_MONTH);
            if (!lastMonthStr.equals(month)) {
                throw new CrmebException("只能查看当前月及上月的签到日历");
            }
        }
        if (StrUtil.isBlank(month)) {
            month = nowMonth;
        }
        List<UserSignRecord> signRecordList = userSignRecordService.findByMonth(user.getId(), month);
        List<String> signDateStrList = signRecordList.stream().map(UserSignRecord::getDate).collect(Collectors.toList());
        SignPageInfoResponse response = new SignPageInfoResponse();
        response.setSignDateList(signDateStrList);
        response.setSignDayNum(lastSignRecord.getDay());
        response.setIntegral(lastSignRecord.getIntegral() + lastSignRecord.getAwardIntegral());
        response.setExperience(lastSignRecord.getExperience() + lastSignRecord.getAwardExperience());
        String signRule = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_SIGN_RULE_DESCRIPTION);
        response.setSignRule(signRule);
        response.setIsTip(isTip);
        return response;
    }

    /**
     * 获取移动端签到记录列表
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    @Override
    public PageInfo<UserSignRecord> findFrontSignRecordList(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        return userSignRecordService.findPageByUid(uid, pageParamRequest);
    }

    /**
     * 签到
     * @param todayStr 今天日期 yyyy-MM-dd
     * @param user 用户
     * @param lastSignRecord 最后签到记录
     */
    private UserSignRecord sign(String todayStr, User user, UserSignRecord lastSignRecord) {
        // 今日,未签到,进行签到
        SignConfigResponse config = getConfig();
        SignConfig baseSignConfig = config.getBaseSignConfig();
        // 连续签到天数
        if (user.getSignNum() > 0) {
            String yesterdayStr = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
            if (ObjectUtil.isNull(lastSignRecord) || !lastSignRecord.getDate().equals(yesterdayStr)) {
                user.setSignNum(0);
            }
        }
        int integral = 0;
        int experience = 0;
        UserSignRecord userSignRecord = new UserSignRecord();
        userSignRecord.setUid(user.getId());
        userSignRecord.setDate(todayStr);
        userSignRecord.setDay(user.getSignNum() + 1);
        userSignRecord.setIntegral(0);
        userSignRecord.setExperience(0);
        userSignRecord.setAwardIntegral(0);
        userSignRecord.setAwardExperience(0);

        if (baseSignConfig.getIsIntegral()) {
            userSignRecord.setIntegral(baseSignConfig.getIntegral());
            integral += baseSignConfig.getIntegral();
        }
        if (baseSignConfig.getIsExperience()) {
            userSignRecord.setExperience(baseSignConfig.getExperience());
            experience += baseSignConfig.getExperience();
        }
        List<SignConfig> signConfigList = config.getSignConfigList();
        SignConfig signConfig = signConfigList.stream().filter(e -> e.getDay().equals(user.getSignNum() + 1)).findAny().orElse(null);
        if (ObjectUtil.isNotNull(signConfig)) {
            if (signConfig.getIsIntegral()) {
                userSignRecord.setAwardIntegral(signConfig.getIntegral());
                integral += signConfig.getIntegral();
            }
            if (signConfig.getIsExperience()) {
                userSignRecord.setAwardExperience(signConfig.getExperience());
                experience += signConfig.getExperience();
            }
        } else {
            if (user.getSignNum() > 0 && CollUtil.isNotEmpty(signConfigList)) {
                SignConfig signConfigMax = signConfigList.stream().max(Comparator.comparing(SignConfig::getDay)).get();
                if (signConfigMax.getIsIntegral()) {
                    userSignRecord.setAwardIntegral(signConfigMax.getIntegral());
                    integral += signConfigMax.getIntegral();
                }
                if (signConfigMax.getIsExperience()) {
                    userSignRecord.setAwardExperience(signConfigMax.getExperience());
                    experience += signConfigMax.getExperience();
                }
            }
        }

        if (user.getIsPaidMember()) {
            List<PaidMemberBenefitsVo> benefitsList = paidMemberService.getBenefitsList();
            for (PaidMemberBenefitsVo b : benefitsList) {
                if (b.getStatus()) {
                    if (b.getName().equals("integralDoubling") && b.getMultiple() > 1 && b.getChannelStr().contains("1")) {
                        integral = integral * b.getMultiple();
                        userSignRecord.setIntegral(userSignRecord.getIntegral() * b.getMultiple());
                        userSignRecord.setAwardIntegral(userSignRecord.getAwardIntegral() * b.getMultiple());
                    }
                    if (b.getName().equals("experienceDoubling") && b.getMultiple() > 1 && b.getChannelStr().contains("1")) {
                        experience = experience * b.getMultiple();
                        userSignRecord.setExperience(userSignRecord.getExperience() * b.getMultiple());
                        userSignRecord.setAwardExperience(userSignRecord.getAwardExperience() * b.getMultiple());
                    }
                }
            }
        }

        StringBuilder mark = new StringBuilder("签到奖励");
        if (baseSignConfig.getIsIntegral() || (ObjectUtil.isNotNull(signConfig) && signConfig.getIsIntegral())) {
            mark.append(integral).append("积分");
        }
        if (baseSignConfig.getIsExperience() || (ObjectUtil.isNotNull(signConfig) && signConfig.getIsExperience())) {
            mark.append(experience).append("经验");
        }
        userSignRecord.setMark(mark.toString());
        int finalIntegral = integral;
        int finalExperience = experience;
        Boolean execute = transactionTemplate.execute(e -> {
            userService.updateSignNumByUid(userSignRecord.getDay(), user.getId());
            userSignRecordService.save(userSignRecord);
            if (finalIntegral > 0) {
                userService.updateIntegral(user.getId(), finalIntegral, Constants.OPERATION_TYPE_ADD);
                UserIntegralRecord integralRecord = new UserIntegralRecord();
                integralRecord.setUid(user.getId());
                integralRecord.setLinkId("0");
                integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SIGN);
                integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                integralRecord.setTitle(IntegralRecordConstants.INTEGRAL_RECORD_TITLE_SIGN);
                integralRecord.setMark(StrUtil.format("签到奖励{}积分", finalIntegral));
                integralRecord.setIntegral(finalIntegral);
                integralRecord.setBalance(finalIntegral + user.getIntegral());
                integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
                userIntegralRecordService.save(integralRecord);
            }
            if (finalExperience > 0) {
                userService.updateExperience(user.getId(), finalExperience, Constants.OPERATION_TYPE_ADD);
                UserExperienceRecord experienceRecord = new UserExperienceRecord();
                experienceRecord.setUid(user.getId());
                experienceRecord.setLinkId("0");
                experienceRecord.setLinkType(ExperienceRecordConstants.EXPERIENCE_RECORD_LINK_TYPE_SIGN);
                experienceRecord.setType(ExperienceRecordConstants.EXPERIENCE_RECORD_TYPE_ADD);
                experienceRecord.setTitle(ExperienceRecordConstants.EXPERIENCE_RECORD_TITLE_SIGN);
                experienceRecord.setMark(StrUtil.format("签到奖励{}经验", finalExperience));
                experienceRecord.setExperience(finalExperience);
                experienceRecord.setBalance(finalExperience + user.getExperience());
                experienceRecord.setStatus(ExperienceRecordConstants.EXPERIENCE_RECORD_STATUS_CREATE);
                userExperienceRecordService.save(experienceRecord);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("签到失败");
        }
        if (finalExperience > 0) {
            asyncService.userLevelUp(user.getId(), user.getLevel(), user.getExperience() + finalExperience);
        }
        return userSignRecord;
    }

    /**
     * 获取初始化基础签到配置
     */
    private SignConfig getInitBaseConfig() {
        SignConfig signConfig = new SignConfig();
        signConfig.setDay(0);
        signConfig.setIsIntegral(false);
        signConfig.setIsExperience(false);
        signConfig.setIntegral(0);
        signConfig.setExperience(0);
        signConfigService.save(signConfig);
        return signConfig;
    }
}
