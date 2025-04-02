package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.bill.Bill;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBalanceRecord;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.common.model.user.UserClosing;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.UserResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.RetailStoreConfigVo;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

/**
 * RetailShopServiceImpl 接口实现 分销业务实现
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
public class RetailStoreServiceImpl implements RetailStoreService {

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;
    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserClosingService userClosingService;
    @Autowired
    private BillService billService;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;
    @Autowired
    private OrderService orderService;

    /**
     * 获取分销配置信息
     *
     * @return 返回配置信息
     */
    @Override
    public RetailStoreConfigVo getRetailStoreConfig() {
        ArrayList<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.RETAIL_STORE_SWITCH);
        keyList.add(SysConfigConstants.RETAIL_STORE_LINE);
        keyList.add(SysConfigConstants.RETAIL_STORE_BINDING_TYPE);
        keyList.add(SysConfigConstants.RETAIL_STORE_BUBBLE_SWITCH);
        keyList.add(SysConfigConstants.RETAIL_STORE_BROKERAGE_FIRST_RATIO);
        keyList.add(SysConfigConstants.RETAIL_STORE_BROKERAGE_SECOND_RATIO);
        keyList.add(SysConfigConstants.RETAIL_STORE_BROKERAGE_FREEZING_TIME);
        keyList.add(SysConfigConstants.RETAIL_STORE_EXTRACT_MIN_PRICE);
        keyList.add(SysConfigConstants.RETAIL_STORE_EXTRACT_BANK);
        keyList.add(SysConfigConstants.RETAIL_STORE_BROKERAGE_SHARE_NODE);
        MyRecord record = systemConfigService.getValuesByKeyList(keyList);

        RetailStoreConfigVo vo = new RetailStoreConfigVo();
        vo.setRetailStoreSwitch(Integer.parseInt(record.getStr(SysConfigConstants.RETAIL_STORE_SWITCH)));
        vo.setRetailStoreLine(Integer.parseInt(record.getStr(SysConfigConstants.RETAIL_STORE_LINE)));
        vo.setRetailStoreBindingType(Integer.parseInt(record.getStr(SysConfigConstants.RETAIL_STORE_BINDING_TYPE)));
        vo.setRetailStoreBubbleSwitch(Integer.parseInt(record.getStr(SysConfigConstants.RETAIL_STORE_BUBBLE_SWITCH)));
        vo.setRetailStoreBrokerageFirstRatio(Integer.parseInt(record.getStr(SysConfigConstants.RETAIL_STORE_BROKERAGE_FIRST_RATIO)));
        vo.setRetailStoreBrokerageSecondRatio(Integer.parseInt(record.getStr(SysConfigConstants.RETAIL_STORE_BROKERAGE_SECOND_RATIO)));
        vo.setRetailStoreBrokerageFreezingTime(Integer.parseInt(record.getStr(SysConfigConstants.RETAIL_STORE_BROKERAGE_FREEZING_TIME)));
        vo.setRetailStoreExtractMinPrice(new BigDecimal(record.getStr(SysConfigConstants.RETAIL_STORE_EXTRACT_MIN_PRICE)));
        vo.setRetailStoreExtractBank(record.getStr(SysConfigConstants.RETAIL_STORE_EXTRACT_BANK).replace("\\n", "\n"));
        vo.setRetailStoreBrokerageShareNode(record.getStr(SysConfigConstants.RETAIL_STORE_BROKERAGE_SHARE_NODE));
        return vo;
    }

    /**
     * 更新分销配置信息
     *
     * @param retailStoreConfigVo 待保存数据
     * @return 更新结果
     */
    @Override
    public Boolean saveRetailStoreConfig(RetailStoreConfigVo retailStoreConfigVo) {
        // 返佣比例之和+起来不能超过100%
        int ration = retailStoreConfigVo.getRetailStoreBrokerageFirstRatio() + retailStoreConfigVo.getRetailStoreBrokerageSecondRatio();
        if (ration > crmebConfig.getRetailStoreBrokerageRatio() || ration < 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("返佣比例之和范围为 0 ~ {}", crmebConfig.getRetailStoreBrokerageRatio()));
        }

        systemConfigService.updateOrSaveValueByName(SysConfigConstants.RETAIL_STORE_SWITCH, retailStoreConfigVo.getRetailStoreSwitch().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.RETAIL_STORE_LINE, retailStoreConfigVo.getRetailStoreLine().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.RETAIL_STORE_BINDING_TYPE, retailStoreConfigVo.getRetailStoreBindingType().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.RETAIL_STORE_BUBBLE_SWITCH, retailStoreConfigVo.getRetailStoreBubbleSwitch().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.RETAIL_STORE_BROKERAGE_FIRST_RATIO, retailStoreConfigVo.getRetailStoreBrokerageFirstRatio().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.RETAIL_STORE_BROKERAGE_SECOND_RATIO, retailStoreConfigVo.getRetailStoreBrokerageSecondRatio().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.RETAIL_STORE_BROKERAGE_FREEZING_TIME, retailStoreConfigVo.getRetailStoreBrokerageFreezingTime().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.RETAIL_STORE_EXTRACT_MIN_PRICE, retailStoreConfigVo.getRetailStoreExtractMinPrice().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.RETAIL_STORE_EXTRACT_BANK, retailStoreConfigVo.getRetailStoreExtractBank());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.RETAIL_STORE_BROKERAGE_SHARE_NODE, retailStoreConfigVo.getRetailStoreBrokerageShareNode());
        return true;
    }

    /**
     * 获取推广订单总数量
     *
     * @return 推广订单总数量
     */
    @Override
    public Integer getSpreadOrderCount() {
        Integer uid = userService.getUserIdException();
        return userBrokerageRecordService.getSpreadCountByUid(uid);
    }

    /**
     * 推广订单
     *
     * @return List;
     */
    @Override
    public List<UserSpreadOrderMonthResponse> getSpreadOrder(PageParamRequest pageParamRequest) {
        User user = userService.getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }

        // 获取推广订单记录，分页
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.findSpreadListByUid(user.getId(), pageParamRequest);
        if (CollUtil.isEmpty(recordList)) {
            return CollUtil.newArrayList();
        }
        // 获取对应的用户信息
        List<Integer> uidList = recordList.stream().map(UserBrokerageRecord::getSubUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);

        List<UserSpreadOrderMonthResponse> orderMonthResponseArrayList = new ArrayList<>();
        List<String> monthList = CollUtil.newArrayList();
        recordList.forEach(record -> {
            UserSpreadOrderResponse userSpreadOrderResponse = new UserSpreadOrderResponse();
            userSpreadOrderResponse.setOrderNo(record.getLinkNo());
            userSpreadOrderResponse.setTime(record.getUpdateTime());
            userSpreadOrderResponse.setAmount(record.getPrice());
            userSpreadOrderResponse.setAvatar(userMap.get(record.getSubUid()).getAvatar());
            userSpreadOrderResponse.setNickname(userMap.get(record.getSubUid()).getNickname());
            userSpreadOrderResponse.setType("返佣");

            String month = CrmebDateUtil.dateToStr(record.getUpdateTime(), DateConstants.DATE_FORMAT_MONTH);
            if (monthList.contains(month)) {
                //如果在已有的数据中找到当前月份数据则追加
                for (UserSpreadOrderMonthResponse orderMonthResponse : orderMonthResponseArrayList) {
                    if (orderMonthResponse.getTime().equals(month)) {
                        orderMonthResponse.getChild().add(userSpreadOrderResponse);
                        break;
                    }
                }
            } else {// 不包含此月份
                //创建一个
                UserSpreadOrderMonthResponse orderMonthResponse = new UserSpreadOrderMonthResponse();
                orderMonthResponse.setTime(month);
                orderMonthResponse.setChild(CollUtil.newArrayList(userSpreadOrderResponse));
                orderMonthResponseArrayList.add(orderMonthResponse);
                monthList.add(month);
            }
        });

        // 获取月份总订单数
        Map<String, Integer> countMap = userBrokerageRecordService.getSpreadCountByUidAndMonth(user.getId(), monthList);
        for (UserSpreadOrderMonthResponse userSpreadOrderItemResponse : orderMonthResponseArrayList) {
            userSpreadOrderItemResponse.setCount(countMap.get(userSpreadOrderItemResponse.getTime()));
        }
        return orderMonthResponseArrayList;
    }

    /**
     * 推广人团队数量
     *
     * @return 推广人团队数量
     */
    @Override
    public UserSpreadPeopleTeamNumResponse getSpreadPeopleTeamNum() {
        //查询当前用户名下的一级推广员
        UserSpreadPeopleTeamNumResponse response = new UserSpreadPeopleTeamNumResponse();
        List<Integer> userIdList = new ArrayList<>();
        Integer userId = userService.getUserIdException();
        userIdList.add(userId);
        userIdList = userService.getSpreadPeopleIdList(userIdList); //我推广的一级用户id集合
        if (CollUtil.isEmpty(userIdList)) {//如果没有一级推广人，直接返回
            response.setCount(0);
            response.setFirstSpreadNum(0);
            response.setSecondSpreadNum(0);
            return response;
        }

        response.setFirstSpreadNum(userIdList.size()); //一级推广人
        //查询二级推广人
        List<Integer> secondSpreadIdList = userService.getSpreadPeopleIdList(userIdList);
        if (CollUtil.isEmpty(secondSpreadIdList)) {
            response.setSecondSpreadNum(0);
            response.setCount(response.getFirstSpreadNum());
            return response;
        }
        response.setSecondSpreadNum(secondSpreadIdList.size());
        response.setCount(response.getFirstSpreadNum() + response.getSecondSpreadNum());
        return response;
    }

    /**
     * 推广用户， 我自己推广了哪些用户
     *
     * @return List<UserSpreadPeopleItemResponse>
     */
    @Override
    public List<UserSpreadPeopleItemResponse> getSpreadPeopleList(UserSpreadPeopleRequest request, PageParamRequest pageParamRequest) {
        //查询当前用户名下的一级推广员
        Integer userId = userService.getUserIdException();
        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(userId);
        userIdList = userService.getSpreadPeopleIdList(userIdList); //我推广的一级用户id集合
        if (CollUtil.isEmpty(userIdList)) {//如果没有一级推广人，直接返回
            return new ArrayList<>();
        }
        if (request.getGrade().equals(1)) {// 二级推广人
            //查询二级推广人
            List<Integer> secondSpreadIdList = userService.getSpreadPeopleIdList(userIdList);
            if (CollUtil.isEmpty(secondSpreadIdList)) {
                return new ArrayList<>();
            }
            //二级推广人
            userIdList.clear();
            userIdList.addAll(secondSpreadIdList);
        }
        return userService.getSpreadPeopleList(userId, userIdList, request.getKeyword(), request.getSortKey(), request.getIsAsc(), pageParamRequest);
    }

    /**
     * 推广人排行榜(取前50)
     *
     * @param type String 时间范围(week-周，month-月)
     * @return List<User>
     */
    @Override
    public List<User> getSpreadPeopleTopByDate(String type) {
        return userService.getSpreadPeopleTopByDate(type);
    }

    /**
     * 佣金排行榜(取前50)
     *
     * @param type String 时间范围
     * @return List<User>
     */
    @Override
    public List<User> getBrokerageTopByDate(String type) {
        // 获取佣金排行榜（周、月）
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.getBrokerageTopByDate(type);
        if (CollUtil.isEmpty(recordList)) {
            return CollUtil.newArrayList();
        }
        // 解决0元排行问题
        for (int i = 0; i < recordList.size(); ) {
            UserBrokerageRecord userBrokerageRecord = recordList.get(i);
            if (userBrokerageRecord.getPrice().compareTo(BigDecimal.ZERO) < 1) {
                recordList.remove(i);
                continue;
            }
            i++;
        }
        if (CollUtil.isEmpty(recordList)) {
            return CollUtil.newArrayList();
        }

        List<Integer> uidList = recordList.stream().map(UserBrokerageRecord::getUid).collect(Collectors.toList());
        //查询用户
        Map<Integer, User> userVoList = userService.getUidMapList(uidList);
        //解决排序问题
        List<User> userList = CollUtil.newArrayList();
        for (UserBrokerageRecord record : recordList) {
            User user = new User();
            User userVo = userVoList.get(record.getUid());
            user.setId(record.getUid());
            user.setAvatar(userVo.getAvatar());
            user.setBrokeragePrice(record.getPrice());
            user.setNickname(userVo.getNickname());
            userList.add(user);
        }
        return userList;
    }

    /**
     * 佣金记录
     *
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<BrokerageRecordDetailResponse> getBrokerageRecord(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        PageInfo<UserBrokerageRecord> pageInfo = userBrokerageRecordService.findDetailListByUid(uid, pageParamRequest);
        List<UserBrokerageRecord> recordList = pageInfo.getList();
        if (CollUtil.isEmpty(recordList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        // 获取年-月
        Map<String, List<UserBrokerageRecord>> map = CollUtil.newHashMap();
        recordList.forEach(i -> {
            String month = StrUtil.subPre(CrmebDateUtil.dateToStr(i.getCreateTime(), DateConstants.DATE_FORMAT), 7);
            if (map.containsKey(month)) {
                map.get(month).add(i);
            } else {
                List<UserBrokerageRecord> list = CollUtil.newArrayList();
                list.add(i);
                map.put(month, list);
            }
        });
        List<BrokerageRecordDetailResponse> responseList = CollUtil.newArrayList();
        map.forEach((key, value) -> {
            BrokerageRecordDetailResponse response = new BrokerageRecordDetailResponse();
            response.setMonth(key);
            response.setList(value);
            responseList.add(response);
        });
        List<BrokerageRecordDetailResponse> collect = responseList.stream().sorted(Comparator.comparing(s -> DateUtil.parse(s.getMonth(), "yyyy-MM").getTime(), Comparator.reverseOrder())).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, collect);
    }

    /**
     * 推广海报轮播图
     *
     * @return List
     */
    @Override
    public List<RetailStorePosterBannerResponse> getPosterBanner() {
        return systemGroupDataService.getListByGid(GroupDataConstants.GROUP_DATA_ID_SPREAD_BANNER_LIST, RetailStorePosterBannerResponse.class);
    }

    /**
     * 绑定推广关系（登录状态）
     *
     * @param spreadPid 推广人
     */
    @Override
    public void bindingUser(Integer spreadPid) {
        userService.bindSpread(spreadPid);
    }

    /**
     * 获取结算配置
     */
    @Override
    public UserClosingConfigResponse getUserClosingConfig() {
        User user = userService.getInfo();
        // 提现最低金额
        String minPrice = systemConfigService.getValueByKeyException(SysConfigConstants.RETAIL_STORE_EXTRACT_MIN_PRICE);
        // 冻结天数
        String freezeDay = systemConfigService.getValueByKeyException(SysConfigConstants.RETAIL_STORE_BROKERAGE_FREEZING_TIME);
        // 可提现佣金
        BigDecimal brokerage = user.getBrokeragePrice();
        // 冻结佣金
        BigDecimal freezeBrokerage = userBrokerageRecordService.getFreezePrice(user.getId());
        // 获取提现银行
        String bank = systemConfigService.getValueByKeyException(SysConfigConstants.RETAIL_STORE_EXTRACT_BANK);

        UserClosingConfigResponse response = new UserClosingConfigResponse();
        response.setMinPrice(minPrice);
        response.setBrokerage(brokerage);
        response.setFreezeBrokerage(freezeBrokerage);
        response.setFreezeDay(freezeDay);
        if (StrUtil.isNotBlank(bank)) {
            response.setBankList(StrUtil.splitTrim(bank, ","));
        }
        return response;
    }

    /**
     * 用户结算申请
     *
     * @param request 申请参数
     * @return Boolean
     */
    @Override
    public Boolean userClosingApply(UserClosingApplyRequest request) {
        userClosingApplyValidate(request);
        User user = userService.getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }
        BigDecimal money = user.getBrokeragePrice();//可提现总金额
        if (money.compareTo(ZERO) < 1) {
            throw new CrmebException(UserResultCode.USER_BROKERAGE_INSUFFICIENT);
        }

        if (money.compareTo(request.getClosingPrice()) < 0) {
            throw new CrmebException(UserResultCode.USER_BROKERAGE_INSUFFICIENT.setMessage("你当前最多可提现 " + money + "元"));
        }

        UserClosing userClosing = new UserClosing();
        userClosing.setClosingType(request.getType());
        BeanUtils.copyProperties(request, userClosing);
        userClosing.setUid(user.getId());
        userClosing.setBalance(money.subtract(request.getClosingPrice()));
        userClosing.setClosingNo(CrmebUtil.getOrderNo(OrderConstants.CLOSING_ORDER_PREFIX_USER));
        //存入银行名称
        if (StrUtil.isNotBlank(userClosing.getPaymentCode())) {
            userClosing.setPaymentCode(systemAttachmentService.clearPrefix(userClosing.getPaymentCode()));
        }

        // 添加佣金记录
        UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
        brokerageRecord.setUid(user.getId());
        brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_WITHDRAW);
        brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB);
        brokerageRecord.setTitle(BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_WITHDRAW_APPLY);
        brokerageRecord.setPrice(userClosing.getClosingPrice());
        brokerageRecord.setBalance(money.subtract(userClosing.getClosingPrice()));
        brokerageRecord.setMark(StrUtil.format("提现申请扣除佣金{}", userClosing.getClosingPrice()));
        brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_WITHDRAW);
        brokerageRecord.setCreateTime(DateUtil.date());
        brokerageRecord.setLinkNo(userClosing.getClosingNo());

        Boolean execute = transactionTemplate.execute(e -> {
            // 保存提现记录
            userClosingService.save(userClosing);
            // 添加佣金记录
            userBrokerageRecordService.save(brokerageRecord);
            return Boolean.TRUE;
        });
        // 此处可添加提现申请通知

        return execute;
    }

    /**
     * 用户结算记录
     *
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public List<UserClosingRecordResponse> getUserClosingRecord(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        PageInfo<UserClosing> pageInfo = userClosingService.getPageByUid(uid, pageParamRequest);
        List<UserClosing> closingList = pageInfo.getList();
        if (CollUtil.isEmpty(closingList)) {
            return CollUtil.newArrayList();
        }
        List<UserClosingRecordResponse> closingRecordResponseList = new ArrayList<>();
        List<String> monthList = CollUtil.newArrayList();
        closingList.forEach(record -> {
            String month = CrmebDateUtil.dateToStr(record.getCreateTime(), DateConstants.DATE_FORMAT_MONTH);
            if (monthList.contains(month)) {
                //如果在已有的数据中找到当前月份数据则追加
                for (UserClosingRecordResponse closingRecordResponse : closingRecordResponseList) {
                    if (closingRecordResponse.getMonth().equals(month)) {
                        closingRecordResponse.getList().add(record);
                        break;
                    }
                }
            } else {// 不包含此月份
                //创建一个
                UserClosingRecordResponse closingRecordResponse = new UserClosingRecordResponse();
                closingRecordResponse.setMonth(month);
                closingRecordResponse.setList(CollUtil.newArrayList(record));
                closingRecordResponseList.add(closingRecordResponse);
                monthList.add(month);
            }
        });
        return closingRecordResponseList;
    }

    /**
     * 佣金转入余额
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean brokerageToYue(BrokerageToYueRequest request) {
        User user = userService.getInfo();
        if (user.getBrokeragePrice().compareTo(ZERO) <= 0) {
            throw new CrmebException(UserResultCode.USER_BROKERAGE_INSUFFICIENT);
        }
        if (user.getBrokeragePrice().compareTo(request.getPrice()) < 0) {
            throw new CrmebException(UserResultCode.USER_BROKERAGE_INSUFFICIENT);
        }
        BigDecimal price = request.getPrice();

        UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
        brokerageRecord.setUid(user.getId());
        brokerageRecord.setLinkNo("0");
        brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_YUE);
        brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB);
        brokerageRecord.setTitle(BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_BROKERAGE_YUE);
        brokerageRecord.setPrice(price);
        brokerageRecord.setBalance(user.getBrokeragePrice().subtract(price));
        brokerageRecord.setMark(StrUtil.format("佣金转余额，减少{}", price));
        brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        brokerageRecord.setCreateTime(DateUtil.date());

        UserBalanceRecord balanceRecord = new UserBalanceRecord();
        balanceRecord.setUid(user.getId());
        balanceRecord.setLinkId("0");
        balanceRecord.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_BROKERAGE);
        balanceRecord.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
        balanceRecord.setAmount(price);
        balanceRecord.setBalance(user.getNowMoney().add(price));
        balanceRecord.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_BROKERAGE, price));

        Bill bill = new Bill();
        bill.setUid(user.getId());
        bill.setPm(BillConstants.BILL_PM_ADD);
        bill.setAmount(price);
        bill.setType(BillConstants.BILL_TYPE_BROKERAGE);
        bill.setMark(StrUtil.format("佣金转余额，用户佣金转余额{}元", price));
        Boolean execute = transactionTemplate.execute(e -> {
            Boolean result = userService.brokerageToYue(user.getId(), brokerageRecord.getPrice());
            if (!result) {
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            billService.save(bill);
            userBalanceRecordService.save(balanceRecord);
            userBrokerageRecordService.save(brokerageRecord);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("佣金转余额失败");
        }
        return execute;
    }

    /**
     * 修改用户上级推广人
     */
    @Override
    public Boolean updateUserSpread(UserUpdateSpreadRequest request) {
        Integer userId = request.getUserId();
        Integer spreadUid = request.getSpreadUid();
        if (userId.equals(spreadUid)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "上级推广人不能为自己");
        }
        User user = userService.getById(userId);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }
        if (user.getIsLogoff()) {
            throw new CrmebException(UserResultCode.USER_LOGOFF);
        }
        if (user.getSpreadUid().equals(spreadUid)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前推广人已经是所选人");
        }
        Integer oldSprUid = user.getSpreadUid();

        User spreadUser = userService.getById(spreadUid);
        if (ObjectUtil.isNull(spreadUser)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "上级用户不存在");
        }
        if (!spreadUser.getIsPromoter()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "上级用户不是推广员");
        }
        if (spreadUser.getSpreadUid().equals(userId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前用户已是推广人的上级");
        }

        return transactionTemplate.execute(e -> {
            userService.updateSpreadByUid(userId, spreadUid);
            userService.updateSpreadCountByUid(spreadUid, Constants.OPERATION_TYPE_ADD);
            if (oldSprUid > 0) {
                userService.updateSpreadCountByUid(oldSprUid, Constants.OPERATION_TYPE_SUBTRACT);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 清除用户上级推广人
     */
    @Override
    public Boolean clearUserSpread(Integer id) {
        User user = userService.getById(id);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }
        if (user.getIsLogoff()) {
            throw new CrmebException(UserResultCode.USER_LOGOFF);
        }
        if (ObjectUtil.isNull(user.getSpreadUid()) || user.getSpreadUid() <= 0) {
            return Boolean.TRUE;
        }
        return transactionTemplate.execute(e -> {
            userService.updateSpreadByUid(user.getId(), 0);
            if (user.getSpreadUid() > 0) {
                userService.updateSpreadCountByUid(user.getSpreadUid(), Constants.OPERATION_TYPE_SUBTRACT);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 分销员列表
     */
    @Override
    public PageInfo<SpreadUserResponse> getRetailStorePeoplePage(RetailStorePeopleSearchRequest request) {
        // id,头像，昵称，姓名，电话，推广用户数，推广订单数，推广订单额，佣金总金额，已提现金额，提现次数，未提现金额，上级推广人
        PageInfo<User> userPageInfo = userService.getRetailStorePeoplePage(request);

        if (CollUtil.isEmpty(userPageInfo.getList())) {
            return CommonPage.copyPageInfo(userPageInfo, CollUtil.newArrayList());
        }
        List<User> userList = userPageInfo.getList();
        List<SpreadUserResponse> responseList = CollUtil.newArrayList();
        userList.forEach(user -> {
            SpreadUserResponse userResponse = new SpreadUserResponse();
            BeanUtils.copyProperties(user, userResponse);
            // 上级推广员名称
            userResponse.setSpreadNickname("无");
            if (ObjectUtil.isNotNull(user.getSpreadUid()) && user.getSpreadUid() > 0) {
                User spreadUser = userService.getById(user.getSpreadUid());
                userResponse.setSpreadNickname(Optional.ofNullable(spreadUser.getNickname()).orElse("--"));
            }

            List<UserBrokerageRecord> recordList = userBrokerageRecordService.getSpreadListByUid(user.getId());
            if (CollUtil.isEmpty(recordList)) {
                // 推广订单数
                userResponse.setSpreadOrderNum(0);
                // 推广订单额
                userResponse.setSpreadOrderTotalPrice(BigDecimal.ZERO);
                // 佣金总金额
                userResponse.setTotalBrokeragePrice(BigDecimal.ZERO);
                // 已提现金额
                userResponse.setExtractCountPrice(BigDecimal.ZERO);
                // 提现次数
                userResponse.setExtractCountNum(0);
                // 冻结中佣金
                userResponse.setFreezeBrokeragePrice(BigDecimal.ZERO);
            } else {
                // 推广订单数
                userResponse.setSpreadOrderNum(recordList.size());
                // 佣金总金额
                userResponse.setTotalBrokeragePrice(recordList.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
                // 推广订单额
                List<String> orderNoList = recordList.stream().map(UserBrokerageRecord::getLinkNo).collect(Collectors.toList());
                BigDecimal spreadOrderTotalPrice = orderService.getSpreadOrderTotalPriceByOrderList(orderNoList);
                userResponse.setSpreadOrderTotalPrice(spreadOrderTotalPrice);

                UserClosing userClosing = userClosingService.getUserExtractByUserId(user.getId());
                // 已提现金额
                userResponse.setExtractCountPrice(userClosing.getClosingPrice());
                // 提现次数
                userResponse.setExtractCountNum(userClosing.getId());
                // 冻结中佣金
                userResponse.setFreezeBrokeragePrice(userBrokerageRecordService.getFreezePrice(user.getId()));
            }
            responseList.add(userResponse);
        });
        return CommonPage.copyPageInfo(userPageInfo, responseList);
    }

    /**
     * 根据条件获取下级推广用户列表
     *
     * @param request     查询参数
     * @param pageRequest 分页参数
     * @return 下级推广用户列表
     */
    @Override
    public PageInfo<User> getRetailStoreSubUserList(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest) {
        return userService.getRetailStoreSubUserList(request, pageRequest);
    }

    /**
     * 根据条件获取推广订单列表
     *
     * @param request     查询参数
     * @param pageRequest 分页参数
     * @return 推广订单列表
     */
    @Override
    public PageInfo<PromotionOrderResponse> getPromotionOrderList(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest) {
        // 获取推广人列表
        if (ObjectUtil.isNull(request.getType())) {
            request.setType(0);
        }
        PageInfo<UserBrokerageRecord> recordPageInfo = userBrokerageRecordService.findAdminSpreadListByUid(request, pageRequest);
        if (CollUtil.isEmpty(recordPageInfo.getList())) {
            return CommonPage.copyPageInfo(recordPageInfo, CollUtil.newArrayList());
        }
        List<Integer> uidList = recordPageInfo.getList().stream().map(UserBrokerageRecord::getSubUid).collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        List<PromotionOrderResponse> responseList = recordPageInfo.getList().stream().map(e -> {
            PromotionOrderResponse response = new PromotionOrderResponse();
            Order order = orderService.getByOrderNo(e.getLinkNo());
            response.setId(order.getId());
            response.setOrderNo(order.getOrderNo());
            response.setNickname(userMap.get(e.getSubUid()).getNickname());
            response.setPrice(e.getPrice());
            response.setUpdateTime(e.getUpdateTime());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(recordPageInfo, responseList);
    }

    /**
     * 用胡结算申请参数校验
     */
    private void userClosingApplyValidate(UserClosingApplyRequest request) {
        if (request.getType().equals(ClosingConstant.CLOSING_TYPE_BANK)) {
            if (StrUtil.isBlank(request.getCardholder())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写持卡人姓名");
            }
            if (StrUtil.isBlank(request.getBankName())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择银行");
            }
            if (StrUtil.isBlank(request.getBankCardNo())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写银行卡卡号");
            }
        }
        if (request.getType().equals(ClosingConstant.CLOSING_TYPE_ALIPAY)) {
            if (StrUtil.isBlank(request.getAlipayAccount())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写支付宝账号");
            }
        }
        if (request.getType().equals(ClosingConstant.CLOSING_TYPE_WECHAT)) {
            if (StrUtil.isBlank(request.getWechatNo())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写微信号");
            }
            if (StrUtil.isBlank(request.getPaymentCode())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写微信收款码");
            }
            if (StrUtil.isBlank(request.getRealName())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写真实姓名");
            }
        }
        String minPrice = systemConfigService.getValueByKeyException(SysConfigConstants.RETAIL_STORE_EXTRACT_MIN_PRICE);
        if (request.getClosingPrice().compareTo(new BigDecimal(minPrice)) < 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("最低提现金额{}元", minPrice));
        }
    }
}
