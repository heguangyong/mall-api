package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.CouponConstants;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.OrderConstants;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.coupon.CouponUser;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.CouponUserOrderResponse;
import com.zbkj.common.response.CouponUserResponse;
import com.zbkj.common.response.UserCouponResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.CouponResultCode;
import com.zbkj.common.result.OrderResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.PreMerchantOrderVo;
import com.zbkj.common.vo.PreOrderInfoDetailVo;
import com.zbkj.common.vo.PreOrderInfoVo;
import com.zbkj.service.dao.CouponUserDao;
import com.zbkj.service.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CouponUserService 实现类
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
public class CouponUserServiceImpl extends ServiceImpl<CouponUserDao, CouponUser> implements CouponUserService {

    @Resource
    private CouponUserDao dao;

    private final Logger logger = LoggerFactory.getLogger(CouponUserServiceImpl.class);

    @Autowired
    private CouponService couponService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;

    /**
     * 批量使用优惠券
     *
     * @param couponIdList 优惠券Id列表
     * @return Boolean
     */
    @Override
    public Boolean useBatch(List<Integer> couponIdList) {
        LambdaUpdateWrapper<CouponUser> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CouponUser::getStatus, CouponConstants.STORE_COUPON_USER_STATUS_USED);
        wrapper.set(CouponUser::getUseTime, DateUtil.date());
        wrapper.in(CouponUser::getId, couponIdList);
        wrapper.eq(CouponUser::getStatus, CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
        return update(wrapper);
    }

    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CouponUserResponse> getPageList(CouponUserSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<CouponUser> CouponUserPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //带 CouponUser 类的多条件查询
        LambdaQueryWrapper<CouponUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CouponUser::getMerId, systemAdmin.getMerId());
        if (StrUtil.isNotBlank(request.getName())) {
            String couponName = URLUtil.decode(request.getName());
            lqw.like(CouponUser::getName, couponName);
        }
        if (ObjectUtil.isNotNull(request.getUid()) && request.getUid() > 0) {
            lqw.eq(CouponUser::getUid, request.getUid());
        }
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(CouponUser::getStatus, request.getStatus());
        }
        lqw.orderByDesc(CouponUser::getId);
        List<CouponUser> couponUserList = dao.selectList(lqw);
        if (CollUtil.isEmpty(couponUserList)) {
            return new PageInfo<>();
        }
        List<Integer> uidList = couponUserList.stream().map(CouponUser::getUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);

        ArrayList<CouponUserResponse> CouponUserResponseList = new ArrayList<>();
        for (CouponUser CouponUser : couponUserList) {
            CouponUserResponse CouponUserResponse = new CouponUserResponse();
            BeanUtils.copyProperties(CouponUser, CouponUserResponse);
            if (userMap.containsKey(CouponUser.getUid())) {
                CouponUserResponse.setNickname(userMap.get(CouponUser.getUid()).getNickname());
                CouponUserResponse.setAvatar(userMap.get(CouponUser.getUid()).getAvatar());
            }
            CouponUserResponseList.add(CouponUserResponse);
        }
        return CommonPage.copyPageInfo(CouponUserPage, CouponUserResponseList);
    }

    /**
     * 过滤已经领取过此优惠券的用户id
     *
     * @param couponId Integer 优惠券id
     * @param uidList  List<Integer> 用户id集合
     */
    private void filterReceiveUserInUid(Integer couponId, List<Integer> uidList) {
        LambdaQueryWrapper<CouponUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CouponUser::getCouponId, couponId);
        lqw.in(CouponUser::getUid, uidList);
        List<CouponUser> CouponUserList = dao.selectList(lqw);
        if (CollUtil.isNotEmpty(CouponUserList)) {
            List<Integer> receiveUidList = CouponUserList.stream().map(CouponUser::getUid).distinct().collect(Collectors.toList());
            uidList.removeAll(receiveUidList);
        }
    }

    /**
     * 根据预下单号获取可用优惠券
     *
     * @param request 预下单参数
     * @return 优惠券集合
     */
    @Override
    public List<CouponUserOrderResponse> getListByPreOrderNo(OrderUseCouponRequest request) {
        // 通过缓存获取预下单对象
        String key = OrderConstants.PRE_ORDER_CACHE_PREFIX + request.getPreOrderNo();
        boolean exists = redisUtil.exists(key);
        if (!exists) {
            throw new CrmebException(OrderResultCode.PRE_ORDER_NOT_EXIST);
        }
        String orderVoString = redisUtil.get(key).toString();
        PreOrderInfoVo preOrderInfoVo = JSONObject.parseObject(orderVoString, PreOrderInfoVo.class);
        Integer uid = userService.getUserIdException();
        if (request.getMerId() > 0) {// 查询商户优惠券
            //产品id集合
            List<Integer> pidList = null;
            BigDecimal maxPrice = BigDecimal.ZERO;
            for (PreMerchantOrderVo merchantOrderVo : preOrderInfoVo.getMerchantOrderVoList()) {
                if (merchantOrderVo.getMerId().equals(request.getMerId())) {
                    pidList = merchantOrderVo.getOrderInfoList().stream().map(PreOrderInfoDetailVo::getProductId).distinct().collect(Collectors.toList());
                    maxPrice = merchantOrderVo.getProTotalFee();
                }
            }
            String pidPrimaryKeySql = getPidPrimaryKeySql(pidList);

            Date date = CrmebDateUtil.nowDateTime();
            Map<String, Object> map = new HashMap<>();
            map.put("merId", request.getMerId());
            map.put("maxPrice", maxPrice);
            map.put("date", date);
            map.put("uid", uid);
            map.put("pidPrimaryKeySql", pidPrimaryKeySql);
            return dao.findListByPreOrder(map);
        }
        List<PreOrderInfoDetailVo> orderInfoList = new ArrayList<>();
        List<Integer> merIdList = new ArrayList<>();
        for (PreMerchantOrderVo merchantOrderVo : preOrderInfoVo.getMerchantOrderVoList()) {
            orderInfoList.addAll(merchantOrderVo.getOrderInfoList());
            merIdList.add(merchantOrderVo.getMerId());
        }
        List<Integer> proIdsList = orderInfoList.stream().map(PreOrderInfoDetailVo::getProductId).distinct().collect(Collectors.toList());
        BigDecimal price = preOrderInfoVo.getProTotalFee();
//        BigDecimal price = preOrderInfoVo.getProTotalFee().subtract(preOrderInfoVo.getMerCouponFee());
        List<Product> productList = productService.findByIds(proIdsList);
        List<Integer> proCategoryIdList = productList.stream().map(Product::getCategoryId).collect(Collectors.toList());
        List<Integer> secondParentIdList = productCategoryService.findParentIdByChildIds(proCategoryIdList);
        List<Integer> firstParentIdList = productCategoryService.findParentIdByChildIds(secondParentIdList);
        proCategoryIdList.addAll(secondParentIdList);
        proCategoryIdList.addAll(firstParentIdList);
        List<Integer> brandIdList = productList.stream().map(Product::getBrandId).collect(Collectors.toList());
        // 查询适用的用户平台优惠券
        List<CouponUser> platCouponUserList = findManyPlatByUidAndMerIdAndMoneyAndProList(uid, proIdsList, proCategoryIdList, merIdList, brandIdList, price);
        return platCouponUserList.stream().map(e -> {
            CouponUserOrderResponse response = new CouponUserOrderResponse();
            BeanUtils.copyProperties(e, response);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 获取商品拼接sql
     *
     * @param pidList 商品id列表
     */
    private String getPidPrimaryKeySql(List<Integer> pidList) {
        List<String> sqlList = new ArrayList<>();
        pidList.forEach(pid -> {
            String sql = pid + " in (select pid from eb_coupon_product where cid = cu.coupon_id)";
            sqlList.add(sql);
        });
        return "( " + StringUtils.join(sqlList, " or ") + ")";
    }

    /**
     * 优惠券过期定时任务
     */
    @Override
    public void overdueTask() {
        // 查询所有状态——可用的优惠券
        LambdaQueryWrapper<CouponUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CouponUser::getStatus, CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
        List<CouponUser> couponList = dao.selectList(lqw);
        if (CollUtil.isEmpty(couponList)) {
            logger.info("批量更新优惠券过期无可更新优惠券,当前时间：{}", DateUtil.date());
            return;
        }
        // 判断优惠券是否过期
        List<Integer> idList = CollUtil.newArrayList();
        DateTime nowDate = DateUtil.date();
        for (CouponUser couponUser : couponList) {
            if (ObjectUtil.isNotNull(couponUser.getEndTime())) {
                if (DateUtil.between(nowDate, couponUser.getEndTime(), DateUnit.SECOND, false) <= 0) {
                    idList.add(couponUser.getId());
                }
            }
        }
        if (CollUtil.isEmpty(idList)) {
            logger.info("批量更新优惠券过期无可用 用户优惠券id,当前时间：{}", DateUtil.date());
            return;
        }
        LambdaUpdateWrapper<CouponUser> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CouponUser::getStatus, CouponConstants.STORE_COUPON_USER_STATUS_LAPSED);
        wrapper.in(CouponUser::getId, idList);
        wrapper.eq(CouponUser::getStatus, CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
        boolean update = update(wrapper);
        if (!update) {
            logger.error("批量更新优惠券过期动作失败,当前时间：{}", DateUtil.date());
        }
    }

    /**
     * 用户领取优惠券
     *
     * @param cid 优惠券id
     */
    @Override
    public Boolean receiveCoupon(Integer cid) {
        Integer userId = userService.getUserIdException();
        // 获取优惠券信息
        Coupon coupon = couponService.getInfoException(cid);
        if (!coupon.getStatus()) {
            throw new CrmebException(CouponResultCode.COUPON_STATUS_ABNORMAL);
        }
        //看是否有剩余数量,是否够给当前用户
        if (coupon.getIsLimited() && coupon.getLastTotal() < 1) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "优惠券余量不足！");
        }
        //看是否过期
        if (coupon.getIsTimeReceive()) {
            //非永久可领取
            String date = CrmebDateUtil.nowDateTimeStr();
            int result = CrmebDateUtil.compareDate(date, CrmebDateUtil.dateToStr(coupon.getReceiveEndTime(), DateConstants.DATE_FORMAT), DateConstants.DATE_FORMAT);
            if (result > 0) {
                //过期了
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "优惠券领取截止日期已过！");
            }
        }
        if (!userIsCanReceiveCoupon(coupon, userId)) {
            //已经领取过了
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前用户已经领取过此优惠券了！");
        }
        //是否有固定的使用时间
        if (!coupon.getIsFixedTime()) {
            String endTime = CrmebDateUtil.addDay(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT), coupon.getDay(), DateConstants.DATE_FORMAT);
            coupon.setUseEndTime(CrmebDateUtil.strToDate(endTime, DateConstants.DATE_FORMAT));
            coupon.setUseStartTime(CrmebDateUtil.nowDateTimeReturnDate(DateConstants.DATE_FORMAT));
        }
        CouponUser couponUser = new CouponUser();
        couponUser.setCouponId(coupon.getId());
        couponUser.setMerId(coupon.getMerId());
        couponUser.setUid(userId);
        couponUser.setName(coupon.getName());
        couponUser.setPublisher(coupon.getPublisher());
        couponUser.setCategory(coupon.getCategory());
        couponUser.setReceiveType(coupon.getReceiveType());
        couponUser.setCouponType(coupon.getCouponType());
        couponUser.setMoney(coupon.getMoney());
        couponUser.setDiscount(coupon.getDiscount());
        couponUser.setMinPrice(coupon.getMinPrice());
        couponUser.setStartTime(coupon.getUseStartTime());
        couponUser.setEndTime(coupon.getUseEndTime());
        couponUser.setStatus(CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
        return transactionTemplate.execute(e -> {
            save(couponUser);
            couponService.deduction(coupon.getId(), 1, coupon.getIsLimited());
            return Boolean.TRUE;
        });
    }

    /**
     * 用户是否能够领取此优惠券
     * @param coupon 优惠券
     * @param userId 用户ID
     */
    @Override
    public Boolean userIsCanReceiveCoupon(Coupon coupon, Integer userId) {
        LambdaQueryWrapper<CouponUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CouponUser::getCouponId, coupon.getId());
        lqw.eq(CouponUser::getUid, userId);
        lqw.orderByDesc(CouponUser::getId);
        lqw.last(" limit 1");
        CouponUser couponUser = dao.selectOne(lqw);
        if (ObjectUtil.isNull(couponUser)) {
            return true;
        }
        if (!coupon.getIsRepeated()) {
            return false;
        }
        if (!couponUser.getStatus().equals(CouponConstants.STORE_COUPON_USER_STATUS_USABLE)) {
            return true;
        }
        return false;
    }

    /**
     * 支付成功赠送处理
     *
     * @param couponId 优惠券编号
     * @param uid      用户uid
     * @return MyRecord
     */
    @Override
    public MyRecord paySuccessGiveAway(Integer couponId, Integer uid) {
        MyRecord record = new MyRecord();
        record.set("status", "fail");
        // 获取优惠券信息
        Coupon coupon = couponService.getById(couponId);
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel() || !coupon.getStatus()) {
            record.set("errMsg", "优惠券信息不存在或者已失效！");
            return record;
        }

        // 判断是否达到可领取时间
        if (ObjectUtil.isNotNull(coupon.getReceiveStartTime())) {
            //非永久可领取
            String date = CrmebDateUtil.nowDateTimeStr();
            int result = CrmebDateUtil.compareDate(date, CrmebDateUtil.dateToStr(coupon.getReceiveStartTime(), DateConstants.DATE_FORMAT), DateConstants.DATE_FORMAT);
            if (result == -1) {
                // 未开始
                record.set("errMsg", "还未达到优惠券领取时间！");
                return record;
            }
        }

        //看是否过期
        if (coupon.getReceiveEndTime() != null) {
            //非永久可领取
            String date = CrmebDateUtil.nowDateTimeStr();
            int result = CrmebDateUtil.compareDate(date, CrmebDateUtil.dateToStr(coupon.getReceiveEndTime(), DateConstants.DATE_FORMAT), DateConstants.DATE_FORMAT);
            if (result == 1) {
                //过期了
                record.set("errMsg", "已超过优惠券领取最后期限！");
                return record;
            }
        }

        //看是否有剩余数量
        if (coupon.getIsLimited() && coupon.getLastTotal() < 1) {
            record.set("errMsg", "此优惠券已经被领完了！");
            return record;
        }

        //过滤掉已经领取过的用户
        List<Integer> uidList = CollUtil.newArrayList();
        uidList.add(uid);
        filterReceiveUserInUid(coupon.getId(), uidList);
        if (uidList.size() < 1) {
            //都已经领取过了
            record.set("errMsg", "当前用户已经领取过此优惠券了！");
            return record;
        }

        //是否有固定的使用时间
        if (!coupon.getIsFixedTime()) {
            String endTime = CrmebDateUtil.addDay(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT), coupon.getDay(), DateConstants.DATE_FORMAT);
            coupon.setUseEndTime(CrmebDateUtil.strToDate(endTime, DateConstants.DATE_FORMAT));
            coupon.setUseStartTime(CrmebDateUtil.nowDateTimeReturnDate(DateConstants.DATE_FORMAT));
        }

        CouponUser couponUser = new CouponUser();
        couponUser.setCouponId(coupon.getId());
        couponUser.setMerId(coupon.getMerId());
        couponUser.setUid(uid);
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
        record.set("status", "ok");
        record.set("couponUser", couponUser);
        record.set("isLimited", coupon.getIsLimited());
        return record;
    }

    /**
     * 获取可用优惠券数量
     *
     * @param uid 用户uid
     * @return Integer
     */
    @Override
    public Integer getUseCount(Integer uid) {
        LambdaQueryWrapper<CouponUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CouponUser::getUid, uid);
        lqw.eq(CouponUser::getStatus, 0);
        List<CouponUser> couponUserList = dao.selectList(lqw);
        if (CollUtil.isEmpty(couponUserList)) {
            return 0;
        }
        Date date = CrmebDateUtil.nowDateTime();
        for (int i = 0; i < couponUserList.size(); ) {
            CouponUser couponUser = couponUserList.get(i);
            //判断是否在使用时间内
            if (ObjectUtil.isNotNull(couponUser.getStartTime()) && ObjectUtil.isNotNull(couponUser.getEndTime())) {
                if (date.compareTo(couponUser.getEndTime()) >= 0) {
                    couponUserList.remove(i);
                    continue;
                }
            }
            i++;
        }
        return CollUtil.isEmpty(couponUserList) ? 0 : couponUserList.size();
    }

    /**
     * 我的优惠券列表
     *
     * @return PageInfo<CouponUserResponse>
     */
    @Override
    public PageInfo<UserCouponResponse> getMyCouponList(MyCouponRequest request) {
        Integer userId = userService.getUserIdException();
        LambdaQueryWrapper<CouponUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(CouponUser::getUid, userId);
        switch (request.getType()) {
            case "usable":
                lqw.eq(CouponUser::getStatus, CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
                lqw.orderByDesc(CouponUser::getId);
                break;
            case "unusable":
                lqw.gt(CouponUser::getStatus, CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
                lqw.last(StrUtil.format(" order by update_time desc, id desc, case `status` when {} then {} when {} then {} when {} then {} end, update_time desc, id desc", 0, 1, 1, 2, 2, 3));
                break;
            default:
                lqw.orderByDesc(CouponUser::getId);
                break;
        }
        Page<CouponUser> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<CouponUser> couponUserList = dao.selectList(lqw);
        if (CollUtil.isEmpty(couponUserList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        Date date = CrmebDateUtil.nowDateTime();
        List<UserCouponResponse> responseList = CollUtil.newArrayList();
        for (CouponUser couponUser : couponUserList) {
            UserCouponResponse userCouponResponse = new UserCouponResponse();
            BeanUtils.copyProperties(couponUser, userCouponResponse);
            String validStr = "usable";// 可用
            if (couponUser.getStatus().equals(CouponConstants.STORE_COUPON_USER_STATUS_USED)) {
                validStr = "unusable";// 已用
            }
            if (couponUser.getStatus().equals(CouponConstants.STORE_COUPON_USER_STATUS_LAPSED)) {
                validStr = "overdue";// 过期
            }

            //判断是否在使用时间内
            if (ObjectUtil.isNotNull(userCouponResponse.getStartTime()) && ObjectUtil.isNotNull(userCouponResponse.getEndTime())) {
                if (userCouponResponse.getStartTime().compareTo(date) > 0) {
                    validStr = "notStart";// 未开始
                }
                if (date.compareTo(userCouponResponse.getEndTime()) >= 0) {
                    validStr = "overdue";// 过期
                }
            }
            userCouponResponse.setValidStr(validStr);

            responseList.add(userCouponResponse);
        }
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 回退优惠券（到未使用状态）
     *
     * @param couponIdList 优惠券id
     * @return 回退结果
     */
    @Override
    public Boolean rollbackByIds(List<Integer> couponIdList) {
        DateTime date = DateUtil.date();
        List<CouponUser> couponUserList = findByIds(couponIdList);
        couponUserList.forEach(cu -> {
            if (date.compareTo(cu.getEndTime()) >= 0) {
                cu.setStatus(CouponConstants.STORE_COUPON_USER_STATUS_LAPSED);
            } else {
                cu.setStatus(CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
            }
        });
        return updateBatchById(couponUserList);
    }

    private List<CouponUser> findByIds(List<Integer> couponIdList) {
        LambdaQueryWrapper<CouponUser> lqw = Wrappers.lambdaQuery();
        lqw.in(CouponUser::getId, couponIdList);
        return dao.selectList(lqw);
    }

    /**
     * 优惠券失效处理（通过优惠券ID）
     */
    @Override
    public Boolean loseEfficacyByCouponId(Integer couponId) {
        LambdaUpdateWrapper<CouponUser> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CouponUser::getStatus, CouponConstants.STORE_COUPON_USER_STATUS_LAPSED);
        wrapper.eq(CouponUser::getCouponId, couponId);
        return update(wrapper);
    }

    /**
     * 获取优惠券已使用数量
     * @param couponId 优惠券ID
     * @return 优惠券已使用数量
     */
    @Override
    public Integer getUsedNumByCouponId(Integer couponId) {
        LambdaQueryWrapper<CouponUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CouponUser::getCouponId, couponId);
        lqw.eq(CouponUser::getStatus, CouponConstants.STORE_COUPON_USER_STATUS_USED);
        return dao.selectCount(lqw);
    }

    /**
     * 获取优惠券列表，通过优惠券ID和用户ID列表
     * @param couponId 优惠券ID
     * @param uidList 用户ID列表
     */
    @Override
    public List<CouponUser> findByCouponIdAndUidList(Integer couponId, List<Integer> uidList) {
        LambdaQueryWrapper<CouponUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CouponUser::getCouponId, couponId);
        lqw.in(CouponUser::getUid, uidList);
        return dao.selectList(lqw);
    }

    /**
     * 平台端优惠券领取列表
     */
    @Override
    public PageInfo<CouponUserResponse> getPlatformList(CouponReceiveRecordSearchRequest request) {
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        if (StrUtil.isNotBlank(request.getName())) {
            String couponName = URLUtil.decode(request.getName());
            map.put("name", couponName);
        }
        if (ObjectUtil.isNotNull(request.getStatus())) {
            map.put("status", request.getStatus());
        }
        Page<CouponUser> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<CouponUser> couponUserList = dao.findCouponReceiveRecordListByPlat(map);
        if (CollUtil.isEmpty(couponUserList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<Integer> uidList = couponUserList.stream().map(CouponUser::getUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);

        ArrayList<CouponUserResponse> CouponUserResponseList = new ArrayList<>();
        for (CouponUser CouponUser : couponUserList) {
            CouponUserResponse CouponUserResponse = new CouponUserResponse();
            BeanUtils.copyProperties(CouponUser, CouponUserResponse);
            if (userMap.containsKey(CouponUser.getUid())) {
                CouponUserResponse.setNickname(userMap.get(CouponUser.getUid()).getNickname());
                CouponUserResponse.setAvatar(userMap.get(CouponUser.getUid()).getAvatar());
            }
            CouponUserResponseList.add(CouponUserResponse);
        }
        return CommonPage.copyPageInfo(page, CouponUserResponseList);
    }

    /**
     * 获取用户领取的优惠券
     * @param couponId 优惠券ID
     * @param userId 用户ID
     */
    @Override
    public CouponUser getLastByCouponIdAndUid(Integer couponId, Integer userId) {
        LambdaQueryWrapper<CouponUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CouponUser::getCouponId, couponId);
        lqw.eq(CouponUser::getUid, userId);
        lqw.orderByDesc(CouponUser::getId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 自动领取优惠券
     */
    @Override
    public Integer autoReceiveCoupon(Coupon coupon, Integer uid) {
        if (!coupon.getIsFixedTime()) {
            String endTime = CrmebDateUtil.addDay(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT), coupon.getDay(), DateConstants.DATE_FORMAT);
            coupon.setUseEndTime(CrmebDateUtil.strToDate(endTime, DateConstants.DATE_FORMAT));
            coupon.setUseStartTime(CrmebDateUtil.nowDateTimeReturnDate(DateConstants.DATE_FORMAT));
        }
        CouponUser couponUser = new CouponUser();
        couponUser.setCouponId(coupon.getId());
        couponUser.setMerId(coupon.getMerId());
        couponUser.setUid(uid);
        couponUser.setName(coupon.getName());
        couponUser.setPublisher(coupon.getPublisher());
        couponUser.setCategory(coupon.getCategory());
        couponUser.setReceiveType(coupon.getReceiveType());
        couponUser.setCouponType(coupon.getCouponType());
        couponUser.setMoney(coupon.getMoney());
        couponUser.setDiscount(coupon.getDiscount());
        couponUser.setMinPrice(coupon.getMinPrice());
        couponUser.setStartTime(coupon.getUseStartTime());
        couponUser.setEndTime(coupon.getUseEndTime());
        couponUser.setStatus(CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
        Boolean execute = transactionTemplate.execute(e -> {
            save(couponUser);
            couponService.deduction(coupon.getId(), 1, coupon.getIsLimited());
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("自动领取优惠券失败");
        }
        return couponUser.getId();
    }

    /**
     * 查询适用的优惠券列表
     * @param userId 用户ID
     * @param merId 商户ID
     * @param proIdList 商品ID列表
     * @param money 金额
     */
    @Override
    public List<CouponUser> findManyByUidAndMerIdAndMoneyAndProList(Integer userId, Integer merId, List<Integer> proIdList, BigDecimal money) {
        String now = DateUtil.now();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("merId", merId);
        map.put("proIdList", proIdList);
        map.put("money", money);
        map.put("nowDate", now);
        return dao.findManyByUidAndMerIdAndMoneyAndProList(map);
    }

    /**
     * 查询适用的平台优惠券列表(多商品)
     * @param userId 用户ID
     * @param proIdList 商品ID列表
     * @param proCategoryIdList 商品分类ID列表
     * @param merIdList 商户ID列表
     * @param brandIdList 品牌ID列表
     * @param money 金额
     */
    @Override
    public List<CouponUser> findManyPlatByUidAndMerIdAndMoneyAndProList(Integer userId, List<Integer> proIdList, List<Integer> proCategoryIdList, List<Integer> merIdList, List<Integer> brandIdList, BigDecimal money) {
        String now = DateUtil.now();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("proIdList", proIdList);
        map.put("proCategoryIdList", proCategoryIdList);
        map.put("merIdList", merIdList);
        map.put("brandIdList", brandIdList);
        map.put("money", money);
        map.put("nowDate", now);
        return dao.findManyPlatByUidAndMerIdAndMoneyAndProList(map);
    }

}

