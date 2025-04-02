package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.admin.service.MarketingActivityService;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.CouponConstants;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.coupon.CouponUser;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.BirthdayPresentRequest;
import com.zbkj.common.request.NewPeoplePresentRequest;
import com.zbkj.common.response.BirthdayPresentResponse;
import com.zbkj.common.response.NewPeoplePresentResponse;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.service.service.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 行为service实现类
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
@Log4j2
@Service
public class MarketingActivityServiceImpl implements MarketingActivityService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private SmsService smsService;

    /**
     * 获取生日有礼配置
     */
    @Override
    public BirthdayPresentResponse getBirthdayPresentConfig() {
        String birthdayPresentSwitch = systemConfigService.getValueByKey(SysConfigConstants.BIRTHDAY_PRESENT_SWITCH);
        String birthdayPresentCoupon = systemConfigService.getValueByKey(SysConfigConstants.BIRTHDAY_PRESENT_COUPON);

        BirthdayPresentResponse response = new BirthdayPresentResponse();
        boolean birthdaySwitch = false;
        if (StrUtil.isNotBlank(birthdayPresentSwitch) && birthdayPresentSwitch.equals(Constants.COMMON_SWITCH_OPEN)) {
            birthdaySwitch = true;
        }
        response.setBirthdaySwitch(birthdaySwitch);
        if (StrUtil.isNotBlank(birthdayPresentCoupon)) {
            List<Integer> couponIdList = CrmebUtil.stringToArray(birthdayPresentCoupon);
            List<Coupon> couponList = couponService.findByIds(couponIdList);
            response.setCouponList(couponList);
        }
        return response;
    }

    /**
     * 编辑生日有礼配置
     *
     * @param request 请求参数
     */
    @Override
    public Boolean editBirthdayPresent(BirthdayPresentRequest request) {
        String birthdayPresentSwitch = request.getBirthdaySwitch() ? Constants.COMMON_SWITCH_OPEN : Constants.COMMON_SWITCH_CLOSE;
        String birthdayPresentCoupon = "";
        if (CollUtil.isNotEmpty(request.getCouponIdList())) {
            birthdayPresentCoupon = request.getCouponIdList().stream().map(String::valueOf).collect(Collectors.joining(","));
        }
        String finalBirthdayPresentCoupon = birthdayPresentCoupon;
        return transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.BIRTHDAY_PRESENT_SWITCH, birthdayPresentSwitch);
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.BIRTHDAY_PRESENT_COUPON, finalBirthdayPresentCoupon);
            return Boolean.TRUE;
        });
    }

    /**
     * 获取新人礼配置
     */
    @Override
    public NewPeoplePresentResponse getNewPeopleConfig() {
        String newPeoplePresentSwitch = systemConfigService.getValueByKey(SysConfigConstants.NEW_PEOPLE_PRESENT_SWITCH);
        String newPeoplePresentCoupon = systemConfigService.getValueByKey(SysConfigConstants.NEW_PEOPLE_PRESENT_COUPON);

        NewPeoplePresentResponse response = new NewPeoplePresentResponse();
        boolean newPeopleSwitch = false;
        if (StrUtil.isNotBlank(newPeoplePresentSwitch) && newPeoplePresentSwitch.equals(Constants.COMMON_SWITCH_OPEN)) {
            newPeopleSwitch = true;
        }
        response.setNewPeopleSwitch(newPeopleSwitch);
        if (StrUtil.isNotBlank(newPeoplePresentCoupon)) {
            List<Integer> couponIdList = CrmebUtil.stringToArray(newPeoplePresentCoupon);
            List<Coupon> couponList = couponService.findByIds(couponIdList);
            response.setCouponList(couponList);
        }
        return response;
    }

    /**
     * 编辑新人礼配置
     *
     * @param request 请求参数
     */
    @Override
    public Boolean editNewPeopleConfig(NewPeoplePresentRequest request) {
        String newPeoplePresentSwitch = request.getNewPeopleSwitch() ? Constants.COMMON_SWITCH_OPEN : Constants.COMMON_SWITCH_CLOSE;
        String newPeoplePresentCoupon = "";
        if (CollUtil.isNotEmpty(request.getCouponIdList())) {
            newPeoplePresentCoupon = request.getCouponIdList().stream().map(String::valueOf).collect(Collectors.joining(","));
        }
        String finalNewPeoplePresentCoupon = newPeoplePresentCoupon;
        return transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.NEW_PEOPLE_PRESENT_SWITCH, newPeoplePresentSwitch);
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.NEW_PEOPLE_PRESENT_COUPON, finalNewPeoplePresentCoupon);
            return Boolean.TRUE;
        });
    }

    /**
     * 发送生日有礼
     */
    @Override
    public void sendBirthdayPresent() {
        String birthdaySwitch = systemConfigService.getValueByKey(SysConfigConstants.BIRTHDAY_PRESENT_SWITCH);
        if (StrUtil.isBlank(birthdaySwitch) || birthdaySwitch.equals(Constants.COMMON_SWITCH_CLOSE)) {
            return;
        }
        String newPeopleSwitchCouponStr = systemConfigService.getValueByKey(SysConfigConstants.BIRTHDAY_PRESENT_COUPON);
        if (StrUtil.isBlank(newPeopleSwitchCouponStr)) {
            return;
        }
        List<Integer> couponIdList = CrmebUtil.stringToArray(newPeopleSwitchCouponStr);
        if (CollUtil.isEmpty(couponIdList)) {
            return;
        }
        DateTime nowDate = DateUtil.date();
        String birthday = nowDate.toString(DateConstants.DATE_FORMAT_MONTH_DATE);
        List<User> userList = userService.findByBirthday(birthday);
        if (CollUtil.isEmpty(userList)) {
            return;
        }

        List<Coupon> couponList = couponService.findByIds(couponIdList);
        List<Coupon> canSendCouponList = couponList.stream().filter(coupon -> {
            if (coupon.getIsDel()) {
                return false;
            }
            if (!coupon.getStatus()) {
                return false;
            }
            if (!coupon.getReceiveType().equals(CouponConstants.COUPON_RECEIVE_TYPE_PLAT_SEND)) {
                return false;
            }
            if (coupon.getIsLimited() && coupon.getLastTotal() <= 0) {
                return false;
            }
            if (coupon.getIsTimeReceive()) {
                if (nowDate.compareTo(coupon.getReceiveStartTime()) < 0) {
                    return false;
                }
                if (nowDate.compareTo(coupon.getReceiveEndTime()) > 0) {
                    return false;
                }
            }
            if (coupon.getIsFixedTime()) {
                if (nowDate.compareTo(coupon.getUseEndTime()) > 0) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        for (User user : userList) {
            // 发放优惠券
            if (CollUtil.isNotEmpty(canSendCouponList)) {
                List<CouponUser> couponUserList = new ArrayList<>();
                List<Coupon> opCouponList = new ArrayList<>();
                for (Coupon coupon : canSendCouponList) {
                    if (coupon.getIsLimited() && coupon.getLastTotal() <= 0) {
                        continue;
                    }
                    if (!couponUserService.userIsCanReceiveCoupon(coupon, user.getId())) {
                        continue;
                    }
                    CouponUser couponUser = new CouponUser();
                    couponUser.setCouponId(coupon.getId());
                    couponUser.setMerId(coupon.getMerId());
                    couponUser.setUid(user.getId());
                    couponUser.setName(coupon.getName());
                    couponUser.setPublisher(coupon.getPublisher());
                    couponUser.setCategory(coupon.getCategory());
                    couponUser.setReceiveType(coupon.getReceiveType());
                    couponUser.setCouponType(coupon.getCouponType());
                    couponUser.setMoney(coupon.getMoney());
                    couponUser.setDiscount(coupon.getDiscount());
                    couponUser.setMinPrice(coupon.getMinPrice());
                    couponUser.setStatus(CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
                    //是否有固定的使用时间
                    if (!coupon.getIsFixedTime()) {
                        String endTime = CrmebDateUtil.addDay(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT), coupon.getDay(), DateConstants.DATE_FORMAT);
                        couponUser.setStartTime(CrmebDateUtil.nowDateTimeReturnDate(DateConstants.DATE_FORMAT));
                        couponUser.setEndTime(CrmebDateUtil.strToDate(endTime, DateConstants.DATE_FORMAT));
                    } else {
                        couponUser.setStartTime(coupon.getUseStartTime());
                        couponUser.setEndTime(coupon.getUseEndTime());
                    }
                    couponUserList.add(couponUser);
                    if (coupon.getIsLimited()) {
                        coupon.setLastTotal(coupon.getLastTotal() - 1);
                    }
                    opCouponList.add(coupon);
                }
                if (CollUtil.isNotEmpty(couponUserList)) {
                    Boolean execute = transactionTemplate.execute(e -> {
                        opCouponList.forEach(c -> {
                            couponService.deduction(c.getId(), 1, c.getIsLimited());
                        });
                        couponUserService.saveBatch(couponUserList);
                        return Boolean.TRUE;
                    });
                    if (!execute) {
                        log.error("生日有礼定时任务：发放失败，用户ID={}", user.getId());
                    }
                }
            }
            // 发送生日祝福短信
            if (StrUtil.isNotBlank(user.getPhone()) && user.getPhone().length() == 11) {
                smsService.sendBirthdayPresent(user.getPhone(), "生日礼品—优惠券");
            }
        }
    }
}
