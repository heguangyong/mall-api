package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.*;
import com.zbkj.common.enums.GroupBuyRecordEnum;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantBalanceRecord;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.order.RechargeOrder;
import com.zbkj.common.model.record.BrowseRecord;
import com.zbkj.common.model.record.UserVisitRecord;
import com.zbkj.common.model.system.SystemNotification;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.*;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.PaidMemberBenefitsVo;
import com.zbkj.service.service.*;
import com.zbkj.service.service.groupbuy.GroupBuyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 异步调用服务实现类
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
public class AsyncServiceImpl implements AsyncService {

    private final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Lazy
    @Autowired
    private ProductService storeProductService;
    @Autowired
    private RedisUtil redisUtil;
    @Lazy
    @Autowired
    private UserVisitRecordService userVisitRecordService;
    @Lazy
    @Autowired
    private BrowseRecordService browseRecordService;
    @Lazy
    @Autowired
    private OrderService orderService;
    @Lazy
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Lazy
    @Autowired
    private OrderDetailService orderDetailService;
    @Lazy
    @Autowired
    private OrderStatusService orderStatusService;
    @Lazy
    @Autowired
    private UserService userService;
    @Lazy
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CrmebConfig crmebConfig;
    @Lazy
    @Autowired
    private SystemNotificationService systemNotificationService;
    @Lazy
    @Autowired
    private UserTokenService userTokenService;
    @Lazy
    @Autowired
    private TemplateMessageService templateMessageService;
    @Lazy
    @Autowired
    private UserExperienceRecordService userExperienceRecordService;
    @Lazy
    @Autowired
    private SystemUserLevelService systemUserLevelService;
    @Lazy
    @Autowired
    private UserLevelService userLevelService;
    @Lazy
    @Autowired
    private CommunityNotesService communityNotesService;
    @Lazy
    @Autowired
    private CommunityReplyService communityReplyService;
    @Lazy
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Lazy
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;
    @Lazy
    @Autowired
    private MerchantBalanceRecordService merchantBalanceRecordService;
    @Lazy
    @Autowired
    private MerchantService merchantService;
    @Lazy
    @Autowired
    private WechatOrderShippingService wechatOrderShippingService;
    @Lazy
    @Autowired
    private PaidMemberService paidMemberService;
    @Lazy
    @Autowired
    private GroupBuyUserService groupBuyUserService;

    /**
     * 商品详情统计
     *
     * @param proId 商品id
     * @param uid   用户uid
     */
    @Async
    @Override
    public void productDetailStatistics(Integer proId, Integer uid) {
        // 商品浏览量+1
        storeProductService.addBrowse(proId);
        // 商品浏览量统计(每日/商城)
        String dateStr = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        redisUtil.incrAndCreate(RedisConstants.PRO_PAGE_VIEW_KEY + dateStr);
        // 商品浏览量统计(每日/个体)
        redisUtil.incrAndCreate(StrUtil.format(RedisConstants.PRO_PRO_PAGE_VIEW_KEY, dateStr, proId));
        if (uid.equals(0)) {
            return;
        }
        // 保存用户访问记录
        if (uid > 0) {
            UserVisitRecord visitRecord = new UserVisitRecord();
            visitRecord.setDate(DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE));
            visitRecord.setUid(uid);
            visitRecord.setVisitType(VisitRecordConstants.VISIT_TYPE_DETAIL);
            userVisitRecordService.save(visitRecord);

            BrowseRecord browseRecord = browseRecordService.getByUidAndProId(uid, proId);
            if (ObjectUtil.isNull(browseRecord)) {
                browseRecord = new BrowseRecord();
                browseRecord.setUid(uid);
                browseRecord.setProductId(proId);
                browseRecord.setDate(DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE));
                browseRecord.setCreateTime(DateUtil.date());
                browseRecordService.save(browseRecord);
            } else {
                browseRecord.setDate(DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE));
                browseRecordService.myUpdate(browseRecord);
            }
        }

    }

    /**
     * 保存用户访问记录
     *
     * @param userId    用户id
     * @param visitType 访问类型
     */
    @Async
    @Override
    public void saveUserVisit(Integer userId, Integer visitType) {
        UserVisitRecord visitRecord = new UserVisitRecord();
        visitRecord.setDate(DateUtil.date().toDateStr());
        visitRecord.setUid(userId);
        visitRecord.setVisitType(visitType);
        userVisitRecordService.save(visitRecord);
    }

    /**
     * 订单支付成功拆单处理
     *
     * @param orderNo 订单号
     */
    @Async
    @Override
    public void orderPaySuccessSplit(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            logger.error("异步——订单支付成功拆单处理 | 订单不存在，orderNo: {}", orderNo);
            return;
        }

        List<MerchantOrder> merchantOrderList = merchantOrderService.getByOrderNo(orderNo);
        if (CollUtil.isEmpty(merchantOrderList)) {
            logger.error("异步——订单支付成功拆单处理 | 商户订单信息不存在,orderNo: {}", orderNo);
            return;
        }
        Boolean execute;
        if (merchantOrderList.size() == 1) {
            // 单商户订单
            execute = oneMerchantOrderProcessing(order, merchantOrderList.get(0));
        } else {
            execute = manyMerchantOrderProcessing(order, merchantOrderList);
        }
        if (!execute) {
            logger.error("异步——订单支付成功拆单处理 | 拆单处理失败，orderNo: {}", orderNo);
            return;
        }
        // 添加支付成功redis队列
        // 异步——订单支付成功拆单处理 | 拆单成功，加入后置处理队列
        redisUtil.lPush(TaskConstants.ORDER_TASK_PAY_SUCCESS_AFTER, order.getOrderNo());
    }

    /**
     * 访问用户个人中心记录
     *
     * @param uid 用户id
     */
    @Async
    @Override
    public void visitUserCenter(Integer uid) {
        UserVisitRecord visitRecord = new UserVisitRecord();
        visitRecord.setDate(DateUtil.date().toString("yyyy-MM-dd"));
        visitRecord.setUid(uid);
        visitRecord.setVisitType(VisitRecordConstants.VISIT_TYPE_CENTER);
        userVisitRecordService.save(visitRecord);
    }

    /**
     * 安装统计
     */
    @Async
    @Override
    public void installStatistics() {
        String isInstall = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_INSTALL_STATISTICS);
        if (StrUtil.isNotBlank(isInstall) && isInstall.equals("1")) {
            return;
        }
        String version = crmebConfig.getVersion();
        if (StrUtil.isBlank(version) || !(StrUtil.startWithIgnoreCase(version, "CRMEB"))) {
            return;
        }
        String apiUrl = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(apiUrl) || !(StrUtil.startWithIgnoreCase(apiUrl, "http"))) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("host", apiUrl);
        map.put("version", version);
        map.put("https", "https");
        String result = HttpUtil.post("https://shop.crmeb.net/index.php/admin/server.upgrade_api/updatewebinfo", JSONObject.toJSONString(map));
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getInteger("status").equals(200)) {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_INSTALL_STATISTICS, "1");
        }
    }

    /**
     * 发送充值成功通知
     *
     * @param rechargeOrder 充值订单
     * @param user          用户
     */
    @Async
    @Override
    public void sendRechargeSuccessNotification(RechargeOrder rechargeOrder, User user) {
        if (!rechargeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            return;
        }
        SystemNotification payNotification = systemNotificationService.getByMark(NotifyConstants.RECHARGE_SUCCESS_MARK);
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        if (rechargeOrder.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_PUBLIC) && user.getIsWechatPublic() && payNotification.getIsWechat().equals(1)) {
            // 公众号模板消息
            userToken = userTokenService.getTokenByUserId(user.getId(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return;
            }
            /**
             * {{first.DATA}}
             * 客户名称：{{keyword1.DATA}}
             * 充值单号：{{keyword2.DATA}}
             * 充值金额：{{keyword3.DATA}}
             * {{remark.DATA}}
             */
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "充值成功通知！");
            temMap.put("keyword1", user.getNickname());
            temMap.put("keyword2", rechargeOrder.getOrderNo());
            temMap.put("keyword3", rechargeOrder.getPrice().toString());
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎下次再来！");
            templateMessageService.pushTemplateMessage(payNotification.getWechatId(), temMap, userToken.getToken());
            return;
        }
        // 小程序通知
        if (rechargeOrder.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI) && user.getIsWechatPublic() && payNotification.getIsRoutine().equals(1)) {
            // 公众号模板消息
            userToken = userTokenService.getTokenByUserId(user.getId(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
            if (ObjectUtil.isNull(userToken)) {
                return;
            }
            /**
             * 交易单号
             * {{character_string1.DATA}}
             * 充值金额
             * {{amount3.DATA}}
             * 充值时间
             * {{date5.DATA}}
             * 赠送金额
             * {{amount6.DATA}}
             * 备注
             * {{thing7.DATA}}
             */
            temMap.put("character_string1", rechargeOrder.getOrderNo());
            temMap.put("amount3", rechargeOrder.getPrice().toString());
            temMap.put("date5", rechargeOrder.getPayTime().toString());
            temMap.put("amount6", rechargeOrder.getGivePrice().toString());
            temMap.put("thing7", "您的充值已成功！");
            templateMessageService.pushTemplateMessage(payNotification.getRoutineId(), temMap, userToken.getToken());
            return;
        }
        return;
    }

    /**
     * 社区笔记用户添加经验
     *
     * @param userId 用户ID
     * @param noteId 文章ID
     */
    @Async
    @Override
    public void noteUpExp(Integer userId, Integer noteId) {

        String levelSwitch = systemConfigService.getValueByKey(UserLevelConstants.SYSTEM_USER_LEVEL_SWITCH);
        if (!Constants.COMMON_SWITCH_OPEN.equals(levelSwitch)) {
            // 开启会员后，才进行经验添加
            return;
        }
        String noteExpStr = systemConfigService.getValueByKeyException(UserLevelConstants.SYSTEM_USER_LEVEL_COMMUNITY_NOTES_EXP);
        if (noteExpStr.equals("0")) {
            return;
        }
        String noteNum = systemConfigService.getValueByKeyException(UserLevelConstants.SYSTEM_USER_LEVEL_COMMUNITY_NOTES_NUM);
        if (noteNum.equals("0")) {
            return;
        }
        if (userExperienceRecordService.isExistNote(userId, noteId)) {
            return;
        }
        Integer noteCountToday = userExperienceRecordService.getCountByNoteToday(userId);
        if (noteCountToday >= Integer.parseInt(noteNum)) {
            return;
        }
        User user = userService.getById(userId);
        int noteExp = Integer.parseInt(noteExpStr);
        if (user.getIsPaidMember()) {
            List<PaidMemberBenefitsVo> benefitsList = paidMemberService.getBenefitsList();
            for (PaidMemberBenefitsVo b : benefitsList) {
                if (b.getStatus()) {
                    if (b.getName().equals("experienceDoubling") && b.getMultiple() > 1 && b.getChannelStr().contains("2")) {
                        noteExp = noteExp * b.getMultiple();
                    }
                }
            }
        }
        UserExperienceRecord record = new UserExperienceRecord();
        record.setUid(userId);
        record.setLinkId(noteId.toString());
        record.setLinkType(ExperienceRecordConstants.EXPERIENCE_RECORD_LINK_TYPE_NOTE);
        record.setType(ExperienceRecordConstants.EXPERIENCE_RECORD_TYPE_ADD);
        record.setTitle(ExperienceRecordConstants.EXPERIENCE_RECORD_TITLE_NOTE);
        record.setExperience(noteExp);
        record.setBalance(user.getExperience() + noteExp);
//        record.setMark(StrUtil.format("社区发布笔记奖励{}经验", noteExp));
        record.setMark(StrUtil.format("社区发布种草奖励{}经验", noteExp));
        record.setCreateTime(DateUtil.date());

        int finalNoteExp = noteExp;
        Boolean execute = transactionTemplate.execute(e -> {
            userService.updateExperience(userId, finalNoteExp, Constants.OPERATION_TYPE_ADD);
            userExperienceRecordService.save(record);
            userLevelUp(userId, user.getLevel(), user.getExperience() + finalNoteExp);
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error("用户社区发布笔记添加经验失败，userId={},noteId={}", userId, noteId);
        }
    }

    /**
     * 社区笔记点赞与取消
     *
     * @param noteId        笔记ID
     * @param operationType 操作类型：add-点赞，sub-取消
     */
    @Async
    @Override
    public void communityNoteLikeOrClean(Integer noteId, String operationType) {
        communityNotesService.operationLike(noteId, operationType);
    }

    /**
     * 社区笔记评论点赞与取消
     *
     * @param replyId       评论ID
     * @param operationType 操作类型：add-点赞，sub-取消
     */
    @Async
    @Override
    public void communityReplyLikeOrClean(Integer replyId, String operationType) {
        communityReplyService.operationLike(replyId, operationType);
    }

    /**
     * 社区笔记添加评论后置处理
     *
     * @param noteId   笔记ID
     * @param parentId 一级评论ID，0-没有
     * @param replyId  评论ID
     */
    @Async
    @Override
    public void noteAddReplyAfter(Integer noteId, Integer parentId, Integer replyId) {
        communityNotesService.operationReplyNum(noteId, 1, Constants.OPERATION_TYPE_ADD);
        if (parentId > 0) {
            communityReplyService.operationReplyNum(parentId, 1, Constants.OPERATION_TYPE_ADD);
        }
    }

    /**
     * 社区评论删除后置处理
     *
     * @param noteId       笔记ID
     * @param firstReplyId 一级笔记评论ID
     * @param replyId      评论ID
     * @param countReply   评论回复数量
     */
    @Async
    @Override
    public void communityReplyDeleteAfter(Integer noteId, Integer firstReplyId, Integer replyId, Integer countReply) {
        if (firstReplyId > 0) {
            communityNotesService.operationReplyNum(noteId, 1, Constants.OPERATION_TYPE_SUBTRACT);
            communityReplyService.operationReplyNum(firstReplyId, 1, Constants.OPERATION_TYPE_SUBTRACT);
        } else {
            communityNotesService.operationReplyNum(noteId, 1 + countReply, Constants.OPERATION_TYPE_SUBTRACT);
        }
    }

    /**
     * 用户升级
     *
     * @param userId      用户ID
     * @param userLevelId 用户等级
     * @param exp         当前经验
     */
    @Async
    @Override
    public void userLevelUp(Integer userId, Integer userLevelId, Integer exp) {
        String levelSwitch = systemConfigService.getValueByKey(UserLevelConstants.SYSTEM_USER_LEVEL_SWITCH);
        if (levelSwitch.equals(Constants.COMMON_SWITCH_CLOSE)) {
            return;
        }
        SystemUserLevel userLevel = systemUserLevelService.getByLevelId(userLevelId);
        SystemUserLevel systemLevel = systemUserLevelService.getByExp(exp);
        if (userLevel.getGrade() >= systemLevel.getGrade()) {
            return;
        }
        if (systemLevel.getExperience() > exp) {
            systemLevel = systemUserLevelService.getPreviousGrade(systemLevel.getGrade());
        }

        UserLevel level = new UserLevel();
        level.setUid(userId);
        level.setLevelId(systemLevel.getId());
        level.setGrade(systemLevel.getGrade());

        userService.updateUserLevel(userId, level.getLevelId());
        userLevelService.deleteByUserId(userId);
        userLevelService.save(level);
    }

    /**
     * 订单支付成功后冻结处理
     * @param orderNoList 订单编号列表
     */
    @Async
    @Override
    public void orderPayAfterFreezingOperation(List<String> orderNoList) {
        String merchantShareNode = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_SHARE_NODE);
        if (StrUtil.isNotBlank(merchantShareNode) && "pay".equals(merchantShareNode)) {
            String merchantShareNodeFreezeDayStr = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_SHARE_FREEZE_TIME);
            if (StrUtil.isNotBlank(merchantShareNodeFreezeDayStr)) {
                orderMerchantShareFreezingOperation(orderNoList, Integer.parseInt(merchantShareNodeFreezeDayStr));
            }
        }
        String retailStoreBrokerageShareNode = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_SHARE_NODE);
        if (StrUtil.isNotBlank(retailStoreBrokerageShareNode) && "pay".equals(retailStoreBrokerageShareNode)) {
            String retailStoreBrokerageShareFreezeDayStr = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_FREEZING_TIME);
            if (StrUtil.isNotBlank(retailStoreBrokerageShareFreezeDayStr)) {
                orderBrokerageShareFreezingOperation(orderNoList, Integer.parseInt(retailStoreBrokerageShareFreezeDayStr));
            }
        }
        String integralFreezeNode = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_FREEZE_NODE);
        if (StrUtil.isNotBlank(integralFreezeNode) && "pay".equals(integralFreezeNode)) {
            String integralDayStr = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME);
            if (StrUtil.isNotBlank(integralDayStr)) {
                orderIntegralFreezingOperation(orderNoList, Integer.parseInt(integralDayStr));
            }
        }
    }

    /**
     * 订单完成后冻结处理
     * @param orderNoList 订单编号列表
     */
    @Async
    @Override
    public void orderCompleteAfterFreezingOperation(List<String> orderNoList) {
        String merchantShareNode = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_SHARE_NODE);
        String merchantShareNodeFreezeDayStr = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_SHARE_FREEZE_TIME);
        int merchantFreezeDay = 7;
        if (StrUtil.isNotBlank(merchantShareNode) && "complete".equals(merchantShareNode) && StrUtil.isNotBlank(merchantShareNodeFreezeDayStr)) {
            merchantFreezeDay = Integer.parseInt(merchantShareNodeFreezeDayStr);
        }
        orderMerchantShareFreezingOperation(orderNoList, merchantFreezeDay);


        String retailStoreBrokerageShareNode = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_SHARE_NODE);
        String retailStoreBrokerageShareFreezeDayStr = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_FREEZING_TIME);
        int brokerageFreezeDay = 7;
        if (StrUtil.isNotBlank(retailStoreBrokerageShareNode) && "complete".equals(retailStoreBrokerageShareNode) && StrUtil.isNotBlank(retailStoreBrokerageShareFreezeDayStr)) {
            brokerageFreezeDay = Integer.parseInt(retailStoreBrokerageShareFreezeDayStr);
        }
        orderBrokerageShareFreezingOperation(orderNoList, brokerageFreezeDay);


        String integralFreezeNode = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_FREEZE_NODE);
        String integralDayStr = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME);
        int integralFreezeDay = 7;
        if (StrUtil.isNotBlank(integralFreezeNode) && "complete".equals(integralFreezeNode) && StrUtil.isNotBlank(integralDayStr)) {
            integralFreezeDay = Integer.parseInt(integralDayStr);
        }
        orderIntegralFreezingOperation(orderNoList, integralFreezeDay);
    }

    /**
     * 微信小程序发货上传发货管理
     * @param orderNo 订单编号
     */
    @Async
    @Override
    public void wechatSendUploadShipping(String orderNo) {
        try {
            wechatOrderShippingService.uploadShippingInfo(orderNo);
        } catch (Exception e) {
            logger.error("微信小程序发货上传发货管理异常，e=", e);
        }
    }

    /**
     * 核销订单微信小程序发货上传发货管理
     * @param orderNo 订单编号
     */
    @Async
    @Override
    public void verifyOrderWechatSendUploadShipping(String orderNo) {
        try {
            wechatOrderShippingService.uploadShippingInfo(orderNo, "verify");
        } catch (Exception e) {
            logger.error("微信小程序发货上传发货管理异常，e=", e);
        }
    }

    /**
     * 订单积分冻结处理
     * @param orderNoList 订单编号列表
     * @param freezeDay 积分冻结天数
     */
    private void orderIntegralFreezingOperation(List<String> orderNoList, Integer freezeDay) {
        Order order = orderService.getByOrderNo(orderNoList.get(0));
        List<UserIntegralRecord> recordList = new ArrayList<>();
        if (freezeDay > 0) {
            for (String orderNo : orderNoList) {
                UserIntegralRecord record = userIntegralRecordService.getByOrderNoAndType(orderNo, IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                if (ObjectUtil.isNull(record)) {
                    continue;
                }
                if (!record.getStatus().equals(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_CREATE)) {
                    continue;
                }
                // 佣金进入冻结期
                record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN);
                // 计算解冻时间
                record.setFrozenTime(freezeDay);
                DateTime dateTime = DateUtil.offsetDay(new Date(), freezeDay);
                long thawTime = dateTime.getTime();
                record.setThawTime(thawTime);
                record.setUpdateTime(DateUtil.date());
                recordList.add(record);
            }
            if (CollUtil.isEmpty(recordList)) {
                return;
            }
            boolean batch = userIntegralRecordService.updateBatchById(recordList);
            if (!batch) {
                logger.error("订单积分冻结处理失败，订单号={}", order.getOrderNo());
            }
            return;
        }
        User user = userService.getById(order.getUid());
        if (ObjectUtil.isNull(user)) {
            logger.error("订单积分冻结处理失败，未找到对应的用户信息，订单编号={}", order.getOrderNo());
            return;
        }
        Integer balance = user.getIntegral();

        for (String orderNo : orderNoList) {
            UserIntegralRecord record = userIntegralRecordService.getByOrderNoAndType(orderNo, IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
            if (ObjectUtil.isNull(record)) {
                continue;
            }
            if (record.getStatus().equals(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE)) {
                continue;
            }
            // 佣金完结期
            record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            // 计算解冻时间
            long thawTime = DateUtil.current(false);
            record.setFrozenTime(freezeDay);
            record.setThawTime(thawTime);
            // 计算积分余额
            balance = balance + record.getIntegral();
            record.setBalance(balance);
            record.setUpdateTime(DateUtil.date());
            recordList.add(record);
        }
        if (CollUtil.isEmpty(recordList)) {
            return;
        }
        int integral = recordList.stream().mapToInt(UserIntegralRecord::getIntegral).sum();
        Boolean execute = transactionTemplate.execute(e -> {
            userIntegralRecordService.updateBatchById(recordList);
            userService.updateIntegral(user.getId(), integral, Constants.OPERATION_TYPE_ADD);
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error("订单积分冻结处理：直接解冻失败，订单编号={}", order.getOrderNo());
        }
    }

    /**
     * 订单佣金分账冻结处理
     * @param orderNoList 订单编号列表
     */
    private void orderBrokerageShareFreezingOperation(List<String> orderNoList, Integer freezeDay) {
        Order order = orderService.getByOrderNo(orderNoList.get(0));
        List<UserBrokerageRecord> recordList = new ArrayList<>();
        if (freezeDay > 0) {
            for (String orderNo : orderNoList) {
                List<UserBrokerageRecord> brokerageRecordList = userBrokerageRecordService.findListByLinkNoAndLinkType(orderNo, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
                if (CollUtil.isEmpty(brokerageRecordList)) {
                    continue;
                }
                for (UserBrokerageRecord record : brokerageRecordList) {
                    if (!record.getStatus().equals(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE)) {
                        continue;
                    }
                    // 佣金进入冻结期
                    record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN);
                    // 计算解冻时间
                    record.setFrozenTime(freezeDay);
                    DateTime dateTime = DateUtil.offsetDay(new Date(), record.getFrozenTime());
                    long thawTime = dateTime.getTime();
                    record.setThawTime(thawTime);
                    record.setUpdateTime(DateUtil.date());
                    recordList.add(record);
                }
            }
            if (CollUtil.isEmpty(recordList)) {
                return;
            }
            userBrokerageRecordService.updateBatchById(recordList);
            return;
        }

        for (String orderNo : orderNoList) {
            List<UserBrokerageRecord> brokerageRecordList = userBrokerageRecordService.findListByLinkNoAndLinkType(orderNo, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
            if (CollUtil.isEmpty(brokerageRecordList)) {
                continue;
            }
            for (UserBrokerageRecord record : brokerageRecordList) {
                if (!record.getStatus().equals(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE)) {
                    continue;
                }
                User user = userService.getById(record.getUid());
                if (ObjectUtil.isNull(user)) {
                    continue;
                }
                record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
                // 计算佣金余额
                BigDecimal balance = user.getBrokeragePrice().add(record.getPrice());
                record.setBalance(balance);
                record.setUpdateTime(DateUtil.date());

                recordList.add(record);
            }
        }
        if (CollUtil.isEmpty(recordList)) {
            return;
        }
        Boolean execute = transactionTemplate.execute(e -> {
            userBrokerageRecordService.updateBatchById(recordList);
            recordList.forEach(record -> {
                userService.updateBrokerage(record.getUid(), record.getPrice(), Constants.OPERATION_TYPE_ADD);
            });
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("佣金解冻处理—数据库出错，订单编号 = {}", order.getOrderNo()));
        }

    }

    /**
     * 订单商户分账冻结处理
     * @param orderNoList 订单编号列表
     */
    private void orderMerchantShareFreezingOperation(List<String> orderNoList, Integer freezeDay) {
        Order order = orderService.getByOrderNo(orderNoList.get(0));
        List<MerchantBalanceRecord> recordList = new ArrayList<>();
        if (freezeDay > 0) {
            for (String orderNo : orderNoList) {
                MerchantBalanceRecord record = merchantBalanceRecordService.getByLinkNo(orderNo);
                if (ObjectUtil.isNull(record)) {
                    continue;
                }
                if (!record.getStatus().equals(1)) {
                    continue;
                }
                record.setStatus(2);
                record.setFrozenTime(freezeDay);
                DateTime dateTime = DateUtil.offsetDay(new Date(), record.getFrozenTime());
                long thawTime = dateTime.getTime();
                record.setThawTime(thawTime);
                record.setUpdateTime(DateUtil.date());
                recordList.add(record);
            }
            if (CollUtil.isEmpty(recordList)) {
                return;
            }
            merchantBalanceRecordService.updateBatchById(recordList);
            return;
        }
        for (String orderNo : orderNoList) {
            MerchantBalanceRecord record = merchantBalanceRecordService.getByLinkNo(orderNo);
            if (ObjectUtil.isNull(record)) {
                continue;
            }
            if (!record.getStatus().equals(1)) {
                continue;
            }
            record.setStatus(3);
            Merchant merchant = merchantService.getById(record.getMerId());
            record.setBalance(merchant.getBalance().add(record.getAmount()));
            recordList.add(record);
        }
        if (CollUtil.isEmpty(recordList)) {
            return;
        }
        Boolean execute = transactionTemplate.execute(e -> {
            merchantBalanceRecordService.updateBatchById(recordList);
            recordList.forEach(record -> {
                merchantService.operationBalance(record.getMerId(), record.getAmount(), Constants.OPERATION_TYPE_ADD);
            });
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("商户余额解冻处理—数据库出错，订单编号 = {}", order.getOrderNo()));
        }
    }

    /**
     * 单商户订单处理
     *
     * @param order         主订单
     * @param merchantOrder 商户订单
     */
    private Boolean oneMerchantOrderProcessing(Order order, MerchantOrder merchantOrder) {
        // 赠送积分积分处理：1.下单赠送积分
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        User user = userService.getById(order.getUid());
        presentIntegral(merchantOrder, orderDetailList, order, user.getIsPaidMember());

        // 生成新的商户订单
        Order newOrder = new Order();
        BeanUtils.copyProperties(order, newOrder);
        MerchantOrder newMerOrder = new MerchantOrder();
        BeanUtils.copyProperties(merchantOrder, newMerOrder);
        newOrder.setId(null);
        newOrder.setOrderNo(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_MERCHANT));
        newOrder.setMerId(merchantOrder.getMerId());
        newOrder.setLevel(OrderConstants.ORDER_LEVEL_MERCHANT);
        newOrder.setPlatOrderNo(order.getOrderNo());
        newOrder.setStatus(OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
        newOrder.setPlatCouponPrice(merchantOrder.getPlatCouponPrice());
        newOrder.setMerCouponPrice(merchantOrder.getMerCouponPrice());
        if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)
            && !order.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)) { // 排除拼团订单
            newOrder.setStatus(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION);
        }
        if(order.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)){ // 拼团订单下单固定状态
            newOrder.setGroupBuyRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode());
        }
        newMerOrder.setId(null);
        newMerOrder.setOrderNo(newOrder.getOrderNo());
        List<OrderDetail> newOrderDetailList = orderDetailList.stream().map(e -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(e, orderDetail);
            orderDetail.setId(null);
            orderDetail.setOrderNo(newOrder.getOrderNo());
            return orderDetail;
        }).collect(Collectors.toList());

        order.setIsDel(true);
        return transactionTemplate.execute(e -> {
            // 订单
            Boolean delete = orderService.paySplitDelete(order.getOrderNo());
            if (!delete) {
                logger.error("支付拆单失败，订单号:{}", order.getOrderNo());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            merchantOrderService.updateById(merchantOrder);
            if (order.getGainIntegral() > 0) {
                orderDetailService.updateBatchById(orderDetailList);
            }
            orderService.save(newOrder);
            merchantOrderService.save(newMerOrder);
            orderDetailService.saveBatch(newOrderDetailList);

            if(order.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)){
                // 根据当前商户订单号对应的平台单号去更新拼团购买记录的订单号为sh
                logger.info("拼团正在处理的订单号：${}:", order.getOrderNo());
                groupBuyUserService.afterPay(order.getOrderNo());
            }

            //订单日志
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_PAY_SPLIT, StrUtil.format(OrderStatusConstants.ORDER_LOG_MESSAGE_PAY_SPLIT, order.getOrderNo()));
            return Boolean.TRUE;
        });
    }

    /**
     * 多商户订单处理
     *
     * @param order             主订单
     * @param merchantOrderList 商户订单列表
     */
    private Boolean manyMerchantOrderProcessing(Order order, List<MerchantOrder> merchantOrderList) {
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        User user = userService.getById(order.getUid());
        // 赠送积分积分处理：1.下单赠送积分
        presentIntegral(merchantOrderList, orderDetailList, order, user.getIsPaidMember());
        // 商户拆单
        List<Order> newOrderList = CollUtil.newArrayList();
        List<MerchantOrder> newMerchantOrderList = CollUtil.newArrayList();
        List<OrderDetail> newOrderDetailList = CollUtil.newArrayList();

        order.setIsDel(true);
        for (MerchantOrder merchantOrder : merchantOrderList) {
            Order newOrder = new Order();
            BeanUtils.copyProperties(order, newOrder);
            newOrder.setId(null);
            newOrder.setOrderNo(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_MERCHANT));
            newOrder.setMerId(merchantOrder.getMerId());
            newOrder.setTotalNum(merchantOrder.getTotalNum());
            newOrder.setProTotalPrice(merchantOrder.getProTotalPrice());
            newOrder.setTotalPostage(merchantOrder.getTotalPostage());
            newOrder.setTotalPrice(merchantOrder.getTotalPrice());
            newOrder.setCouponPrice(merchantOrder.getCouponPrice());
            newOrder.setMerCouponPrice(merchantOrder.getMerCouponPrice());
            newOrder.setPlatCouponPrice(merchantOrder.getPlatCouponPrice());
            newOrder.setUseIntegral(merchantOrder.getUseIntegral());
            newOrder.setIntegralPrice(merchantOrder.getIntegralPrice());
            newOrder.setPayPrice(merchantOrder.getPayPrice());
            newOrder.setPayPostage(merchantOrder.getPayPostage());
            newOrder.setGainIntegral(merchantOrder.getGainIntegral());
            newOrder.setLevel(OrderConstants.ORDER_LEVEL_MERCHANT);
            newOrder.setStatus(OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
            newOrder.setPlatOrderNo(order.getOrderNo());
            newOrder.setIsDel(false);
            newOrder.setStatus(OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
            if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                newOrder.setStatus(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION);
            }
            newOrder.setSvipDiscountPrice(merchantOrder.getSvipDiscountPrice());
            MerchantOrder newMerchantOrder = new MerchantOrder();
            BeanUtils.copyProperties(merchantOrder, newMerchantOrder);
            newMerchantOrder.setId(null);
            newMerchantOrder.setOrderNo(newOrder.getOrderNo());
            List<OrderDetail> tempDetailList = orderDetailList.stream().filter(e -> e.getMerId().equals(merchantOrder.getMerId())).collect(Collectors.toList());
            tempDetailList.forEach(d -> {
                d.setId(null);
                d.setOrderNo(newOrder.getOrderNo());
            });
            newOrderList.add(newOrder);
            newMerchantOrderList.add(newMerchantOrder);
            newOrderDetailList.addAll(tempDetailList);
        }

        return transactionTemplate.execute(e -> {
            // 订单
            Boolean delete = orderService.paySplitDelete(order.getOrderNo());
            if (!delete) {
                logger.error("支付拆单失败，订单号:{}", order.getOrderNo());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            merchantOrderService.updateBatchById(merchantOrderList);
            orderService.saveBatch(newOrderList);
            merchantOrderService.saveBatch(newMerchantOrderList);
            orderDetailService.saveBatch(newOrderDetailList);
            // 订单日志
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_PAY_SPLIT, StrUtil.format(OrderStatusConstants.ORDER_LOG_MESSAGE_PAY_SPLIT, order.getOrderNo()));
            return Boolean.TRUE;
        });
    }

    /**
     * 赠送积分处理
     */
    private void presentIntegral(MerchantOrder merchantOrder, List<OrderDetail> orderDetailList, Order order, Boolean isPaidMember) {
        //比例
        String integralRatioStr = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE);
        // 当下单支付金额按比例赠送积分 <= 0 时，不进行计算
        if (StrUtil.isNotBlank(integralRatioStr) && order.getPayPrice().compareTo(BigDecimal.ZERO) > 0 && new BigDecimal(integralRatioStr).compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal integralBig = new BigDecimal(integralRatioStr);
            int giveIntegral = merchantOrder.getPayPrice().divide(integralBig, 0, BigDecimal.ROUND_DOWN).intValue();
            if (isPaidMember) {
                List<PaidMemberBenefitsVo> benefitsList = paidMemberService.getBenefitsList();
                for (PaidMemberBenefitsVo b : benefitsList) {
                    if (b.getStatus()) {
                        if (b.getName().equals("integralDoubling") && b.getMultiple() > 1 && b.getChannelStr().contains("2")) {
                            giveIntegral = giveIntegral * b.getMultiple();
                        }
                    }
                }
            }
            merchantOrder.setGainIntegral(giveIntegral);
            order.setGainIntegral(giveIntegral);
            if (giveIntegral > 0) {
                // 订单详情
                for (int i = 0; i < orderDetailList.size(); i++) {
                    OrderDetail orderDetail = orderDetailList.get(i);
                    if (orderDetailList.size() == (i + 1)) {
                        orderDetail.setGainIntegral(giveIntegral);
                        break;
                    }
                    BigDecimal ratio = orderDetail.getPayPrice().divide(merchantOrder.getPayPrice(), 10, BigDecimal.ROUND_HALF_UP);
                    int integral = new BigDecimal(Integer.toString(merchantOrder.getGainIntegral())).multiply(ratio).setScale(0, BigDecimal.ROUND_DOWN).intValue();
                    orderDetail.setGainIntegral(integral);
                    giveIntegral = giveIntegral - integral;
                }
            }
        }
    }

    /**
     * 赠送积分处理
     */
    private void presentIntegral(List<MerchantOrder> merchantOrderList, List<OrderDetail> orderDetailList, Order order, Boolean isPaidMember) {
        int integral = 0;
        //比例
        String integralRatioStr = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE);
        if (StrUtil.isNotBlank(integralRatioStr) && order.getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
            for (MerchantOrder merOrder : merchantOrderList) {
                BigDecimal integralBig = new BigDecimal(integralRatioStr);
                int giveIntegral;
                if (integralBig.compareTo(BigDecimal.ZERO) <= 0) {
                    giveIntegral = 0;
                } else {
                    giveIntegral = merOrder.getPayPrice().divide(integralBig, 0, BigDecimal.ROUND_DOWN).intValue();
                    if (isPaidMember) {
                        List<PaidMemberBenefitsVo> benefitsList = paidMemberService.getBenefitsList();
                        for (PaidMemberBenefitsVo b : benefitsList) {
                            if (b.getStatus()) {
                                if (b.getName().equals("integralDoubling") && b.getMultiple() > 1 && b.getChannelStr().contains("2")) {
                                    giveIntegral = giveIntegral * b.getMultiple();
                                }
                            }
                        }
                    }
                }
                integral += giveIntegral;
                merOrder.setGainIntegral(giveIntegral);
                if (giveIntegral > 0) {
                    List<OrderDetail> detailList = orderDetailList.stream().filter(e -> e.getMerId().equals(merOrder.getMerId())).collect(Collectors.toList());
                    // 订单详情
                    for (int i = 0; i < detailList.size(); i++) {
                        OrderDetail orderDetail = detailList.get(i);
                        if (detailList.size() == (i + 1)) {
                            orderDetail.setGainIntegral(giveIntegral);
                        }
                        BigDecimal ratio = orderDetail.getPayPrice().divide(merOrder.getPayPrice(), 10, BigDecimal.ROUND_HALF_UP);
                        int detailIntegral = new BigDecimal(Integer.toString(merOrder.getGainIntegral())).multiply(ratio).setScale(0, BigDecimal.ROUND_DOWN).intValue();
                        orderDetail.setGainIntegral(detailIntegral);
                        giveIntegral = giveIntegral - detailIntegral;
                    }
                }
            }
            if (integral > 0) {
                order.setGainIntegral(integral);
            }
        }
    }
}
